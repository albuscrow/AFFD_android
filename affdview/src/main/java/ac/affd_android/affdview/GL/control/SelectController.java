package ac.affd_android.affdview.GL.control;

import ac.affd_android.affdview.Constant;
import ac.affd_android.affdview.GL.GLOBJ.ACACBO;
import ac.affd_android.affdview.GL.GLOBJ.ACGLBuffer;
import ac.affd_android.affdview.GL.GLProgram.ACProgram;
import ac.affd_android.affdview.GL.GLProgram.ACShader;
import ac.affd_android.affdview.Util.ByteUtil;
import ac.affd_android.affdview.Util.FileUtil;
import ac.affd_android.affdview.model.GlobalInfoProvider;
import ac.affd_android.affdview.model.Vec3f;
import android.content.Context;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static android.opengl.GLES31.*;

/**
 * Created by ac on 5/13/16.
 * todo some describe
 */
public class SelectController extends ACController {
    private final GlobalInfoProvider globalInfoProvider;
    static final private int SELECTED_RESULT_LENGTH = 512;
    private ACGLBuffer selectedPointBuffer;
    private ACGLBuffer selectedPointNumberAtomic;
    private ACProgram program = new ACProgram("selectProgram");
    private Vec3f startPoint;
    private Vec3f direction;
    private boolean needRun = false;
    private Vec3f closetPointParameter;

    public SelectController(GlobalInfoProvider globalInfoProvider) {
        this.globalInfoProvider = globalInfoProvider;
    }

    public void glOnSurfaceCreated(Context c) {
        this.selectedPointBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(Constant.SELECT_RESULT_BINDING_POINT)
                .postUpdate(null, SELECTED_RESULT_LENGTH)
                .glAsyncWithGPU(GL_STREAM_DRAW);
        this.selectedPointNumberAtomic = ACGLBuffer.glGenBuffer(GL_ATOMIC_COUNTER_BUFFER)
                .glSetBindingPoint(Constant.SELECT_RESULT_ATOMIC_BINDING_POINT);


        String source;
        try {
            source = FileUtil.convertStreamToString(c.getAssets().open("select_point.glsl"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        program.addShader(new ACShader(preCompile(source, null), GL_COMPUTE_SHADER));
        program.glCompileAndLink(c);
    }

    public void resetAtomic() {
        ByteBuffer bb = ByteUtil.genDirectBuffer(ByteUtil.INT_BYTE_SIZE);
        bb.putInt(0);
        bb.flip();
        selectedPointNumberAtomic.postUpdate(bb, bb.limit());
    }

    private void glAsyncBuffer() {
        selectedPointNumberAtomic.glAsyncWithGPU(GL_DYNAMIC_DRAW);
        selectedPointBuffer.glAsyncWithGPU(GL_DYNAMIC_DRAW);
    }

    public void glOnDrawFrame() {
        if (!needRun) {
            return;
        }
        needRun = false;
        resetAtomic();
        glAsyncBuffer();
        updateUniform();
        final int x = globalInfoProvider.getRendererTriangleNumber() / local_size_x + 1;
        program.compute(x);

        int selectedPointNumber = ((ACACBO) selectedPointNumberAtomic).getValue(0);
        FloatBuffer res = selectedPointBuffer.getData().asFloatBuffer();
        closetPointParameter = null;
        float closetLength = Float.MAX_VALUE;
        for (int i = 0; i < selectedPointNumber; ++i) {
            float t = res.get(i * 4 + 3);
            if (t < closetLength && t > 0) {
                closetPointParameter = new Vec3f(res.get(i * 4), res.get(i * 4 + 1), res.get(i * 4 + 2));
                closetLength = t;
            }
        }
    }

    public void reset() {
        closetPointParameter = null;
    }

    private void updateUniform() {
        glProgramUniform1ui(program.getId(), 0, globalInfoProvider.getRendererTriangleNumber());
        glProgramUniform3f(program.getId(), 1, startPoint.getComponent(0), startPoint.getComponent(1), startPoint.getComponent(2));
        glProgramUniform3f(program.getId(), 2, direction.getComponent(0), direction.getComponent(1), direction.getComponent(2));
    }

    public void setStartPointAndDirection(Vec3f startPoint, Vec3f direction) {
        this.startPoint = startPoint;
        this.direction = direction;
        this.needRun = true;
    }

    public Vec3f getSelectParameter() {
        return closetPointParameter;
    }
}
