package ac.affd_android.app.GL.control;

import ac.affd_android.app.Constant;
import ac.affd_android.app.GL.GLOBJ.ACGLBuffer;
import ac.affd_android.app.GL.GLProgram.ACProgram;
import ac.affd_android.app.GL.GLProgram.ACShader;
import ac.affd_android.app.GL.GLProgram.ShaderPreCompiler;
import ac.affd_android.app.Util.ByteUtil;
import ac.affd_android.app.Util.GLUtil;
import ac.affd_android.app.model.GlobalInfoProvider;
import ac.affd_android.app.model.Vec3f;
import ac.affd_android.app.model.Vec3i;
import android.content.Context;
import android.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static ac.affd_android.app.Constant.MAX_TESSELLATION_POINT_NUMBER;
import static ac.affd_android.app.Constant.MAX_TESSELLATION_TRIANGLE_NUMBER;
import static android.opengl.GLES30.GL_UNIFORM_BUFFER;
import static android.opengl.GLES31.GL_COMPUTE_SHADER;

/**
 * Created by ac on 5/4/16.
 * used for deformation in every frame
 */
public class DeformationController extends ACController {
    private static final String TAG = "DeformationController";
    private final GlobalInfoProvider globalInfoProvider;
    private ACProgram deformProgram = new ACProgram();
    ACProgram selectProgram = new ACProgram();
    private ACGLBuffer controlPointUniformBuffer;
    private ACGLBuffer tessellationInfoUniformBuffer;
    private boolean controlPointChange = true;
    private int tessellationLevel = 3;
    private Vec3f[] tessellationParameter;
    private Vec3i[] tessellationIndices;

    public DeformationController(GlobalInfoProvider globalInfoProvider) {
        this.globalInfoProvider = globalInfoProvider;
    }

    private Buffer getTessellationInfoData() {
        tessellationParameter = new Vec3f[MAX_TESSELLATION_POINT_NUMBER];
        tessellationIndices = new Vec3i[MAX_TESSELLATION_TRIANGLE_NUMBER];
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

    public void glOnSurfaceCreated(Context c, List<ShaderPreCompiler> preCompilers) {
        //init program
        glInitProgram(c, preCompilers);

        //init ubo for sample
        final Buffer controlPointBuffer = globalInfoProvider.getBsplineBodyFastControlPoint();
        controlPointUniformBuffer = ACGLBuffer.glGenBuffer(GL_UNIFORM_BUFFER)
                .glSetBindingPoint(Constant.BSPLINEBODY_SAMPLE_POINT_BINDING_POINT)
                .postUpdate(controlPointBuffer, controlPointBuffer.limit())
                .glAsyncWithGPU();

        final Buffer tessellationInfoBuffer = getTessellationInfoData();
        tessellationInfoUniformBuffer = ACGLBuffer.glGenBuffer(GL_UNIFORM_BUFFER)
                .glSetBindingPoint(Constant.TESSELLATION_INFO_BINDING_POINT)
                .postUpdate(tessellationInfoBuffer, tessellationInfoBuffer.limit())
                .glAsyncWithGPU();

        //check error
        GLUtil.glCheckError(TAG);
    }

    public void notifyControlPointChange() {
        final Buffer controlPointBuffer = globalInfoProvider.getBsplineBodyFastControlPoint();
        controlPointUniformBuffer.postUpdate(controlPointBuffer, controlPointBuffer.limit());
        controlPointChange = true;
    }

    public void glOnDrawFrame() {
        if (controlPointChange) {
            deformProgram.compute(globalInfoProvider.getSplitTriangleNumber() / group_size + 1);
            controlPointChange = false;
        }
    }

    private void glInitProgram(Context c, List<ShaderPreCompiler> preCompiler) {
        String source;
        try {
            source = IOUtils.toString(c.getAssets().open("deformation.glsl"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        preCompiler = new ArrayList<>(preCompiler);
        preCompiler.add(getLocalSizePreCompiler());
        preCompiler.add(new ShaderPreCompiler(new String[]{"const uint SPLIT_TRIANGLE_NUMBER = 0"}, new String[]{"const uint SPLIT_TRIANGLE_NUMBER = " + globalInfoProvider.getSplitTriangleNumber()}));
        deformProgram.addShader(new ACShader(preCompile(source, preCompiler), GL_COMPUTE_SHADER));
        Log.i(TAG, "begin compile deform program");
        deformProgram.glCompileAndLink();
    }

    public int getTessellationTriangleNumber() {
        return tessellationLevel * tessellationLevel;
    }
}
