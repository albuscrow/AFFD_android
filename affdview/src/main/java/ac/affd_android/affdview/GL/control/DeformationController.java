package ac.affd_android.affdview.GL.control;

import ac.affd_android.affdview.Constant;
import ac.affd_android.affdview.GL.GLOBJ.ACGLBuffer;
import ac.affd_android.affdview.GL.GLProgram.ACProgram;
import ac.affd_android.affdview.GL.GLProgram.ACShader;
import ac.affd_android.affdview.GL.GLProgram.GLSLPreprocessor;
import ac.affd_android.affdview.Util.ByteUtil;
import ac.affd_android.affdview.Util.FileUtil;
import ac.affd_android.affdview.Util.GLUtil;
import ac.affd_android.affdview.model.GlobalInfoProvider;
import ac.affd_android.affdview.model.Vec3f;
import ac.affd_android.affdview.model.Vec3i;
import android.content.Context;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static ac.affd_android.affdview.Constant.MAX_TESSELLATION_POINT_NUMBER;
import static ac.affd_android.affdview.Constant.MAX_TESSELLATION_TRIANGLE_NUMBER;
import static android.opengl.GLES20.GL_STREAM_DRAW;
import static android.opengl.GLES30.GL_UNIFORM_BUFFER;
import static android.opengl.GLES31.GL_COMPUTE_SHADER;

/**
 * Created by ac on 5/4/16.
 * used for deformation in every frame
 */
public class DeformationController extends ACController {
    private static final String TAG = "DeformationController";
    private final GlobalInfoProvider globalInfoProvider;
    private ACProgram deformProgram = new ACProgram("deformProgram");
    private ACGLBuffer controlPointUniformBuffer;
    private ACGLBuffer tessellationInfoUniformBuffer;
    private boolean controlPointChange = true;
    private int tessellationLevel = 1;

    public DeformationController(GlobalInfoProvider globalInfoProvider) {
        this.globalInfoProvider = globalInfoProvider;
    }

    private Buffer getTessellationInfoData() {
        Vec3f[] tessellationParameter = new Vec3f[MAX_TESSELLATION_POINT_NUMBER];
        Vec3i[] tessellationIndices = new Vec3i[MAX_TESSELLATION_TRIANGLE_NUMBER];
        int i = -1;
        float tessellationLevelFloat = tessellationLevel;
        for (int u = tessellationLevel; u >= 0; --u) {
            for (int v = tessellationLevel - u; v >= 0; --v) {
                int w = tessellationLevel - u - v;
                tessellationParameter[++i] = new Vec3f(u / tessellationLevelFloat,
                        v / tessellationLevelFloat,
                        w / tessellationLevelFloat);
            }
        }
        final Vec3f paddingData = new Vec3f(0, 0, 0);

        for (++i; i < MAX_TESSELLATION_POINT_NUMBER; ++i) {
            tessellationParameter[i] = paddingData;
        }

        i = -1;
        for (int l = 0; l < tessellationLevel; l++) {
            int start = (l + 1) * (l + 2) / 2;
            Vec3i prev = new Vec3i(start, start + 1, start - 1 - l);
            tessellationIndices[++i] = prev;
            for (int j = 0; j < l * 2; j++) {
                Vec3i next = new Vec3i(prev.z, prev.z + 1, prev.y);
                if (j % 2 == 0) {
                    //noinspection SuspiciousNameCombination
                    tessellationIndices[++i] = new Vec3i(next.y, next.x, next.z);
                } else {
                    tessellationIndices[++i] = next;
                }
                prev = next;
            }
        }
        final Vec3i paddingData2 = new Vec3i(0, 0, 0);
        for (++i; i < MAX_TESSELLATION_TRIANGLE_NUMBER; ++i) {
            tessellationIndices[i] = paddingData2;
        }
        ByteBuffer res = ByteUtil.genDirectBuffer((MAX_TESSELLATION_POINT_NUMBER + MAX_TESSELLATION_TRIANGLE_NUMBER) * 16 + 4);
        ByteUtil.addToBuffer(res, tessellationParameter, 1);
        ByteUtil.addToBuffer(res, tessellationIndices, 1);
        ByteUtil.addToBuffer(res, tessellationLevel);
        res.flip();
        return res;
    }

    public void glOnSurfaceCreated(Context c, List<GLSLPreprocessor> preCompilers, String computerShaderFileName) {
        //init program
        glInitProgram(c, preCompilers, computerShaderFileName);

        //init ubo for sample
        final Buffer controlPointBuffer = globalInfoProvider.getBsplineBodyFastControlPoint();
        controlPointUniformBuffer = ACGLBuffer.glGenBuffer(GL_UNIFORM_BUFFER)
                .glSetBindingPoint(Constant.BSPLINEBODY_SAMPLE_POINT_BINDING_POINT)
                .postUpdate(controlPointBuffer, controlPointBuffer.limit() * 4)
                .glAsyncWithGPU(GL_STREAM_DRAW);

        final Buffer tessellationInfoBuffer = getTessellationInfoData();
        tessellationInfoUniformBuffer = ACGLBuffer.glGenBuffer(GL_UNIFORM_BUFFER)
                .glSetBindingPoint(Constant.TESSELLATION_INFO_BINDING_POINT)
                .postUpdate(tessellationInfoBuffer, tessellationInfoBuffer.limit())
                .glAsyncWithGPU(GL_STREAM_DRAW);

        //check error
        GLUtil.glCheckError(TAG);
    }

    public void notifyControlPointChange() {
        final Buffer controlPointBuffer = globalInfoProvider.getBsplineBodyFastControlPoint();
        controlPointUniformBuffer.postUpdate(controlPointBuffer, controlPointBuffer.limit() * 4);
        controlPointChange = true;
    }

    private void glAsyncBuffer() {
        controlPointUniformBuffer.glAsyncWithGPU(GL_STREAM_DRAW);
        tessellationInfoUniformBuffer.glAsyncWithGPU(GL_STREAM_DRAW);
    }

    public void glOnDrawFrame() {
        if (controlPointChange) {
            glAsyncBuffer();
            deformProgram.compute(globalInfoProvider.getSplitTriangleNumber() / local_size_x + 1);
            controlPointChange = false;
        }
    }

    private void glInitProgram(Context c, List<GLSLPreprocessor> preCompiler, String computerShaderFileName) {
        String source;
        try {
            source = FileUtil.convertStreamToString(c.getAssets().open(computerShaderFileName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        preCompiler = new ArrayList<>(preCompiler);
        preCompiler.add(new GLSLPreprocessor()
                .add("SPLIT_TRIANGLE_NUMBER_M", Integer.toString(globalInfoProvider.getSplitTriangleNumber()) + "u"));

        deformProgram.addShader(new ACShader(preCompile(source, preCompiler), GL_COMPUTE_SHADER));
        deformProgram.glCompileAndLink(c);
    }

    public int getTessellationTriangleNumber() {
        return tessellationLevel * tessellationLevel;
    }

    public int getTessellationPointNumber() {
        return (tessellationLevel + 1) * (tessellationLevel + 2) / 2;
    }

}
