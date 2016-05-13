package ac.affd_android.app.GL.control;

import ac.affd_android.app.Constant;
import ac.affd_android.app.GL.GLOBJ.ACGLBuffer;
import ac.affd_android.app.GL.GLProgram.ACProgram;
import ac.affd_android.app.GL.GLProgram.ACShader;
import ac.affd_android.app.Util.ByteUtil;
import ac.affd_android.app.model.GlobalInfoProvider;
import ac.affd_android.app.model.Vec3f;
import android.content.Context;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

import static android.opengl.GLES31.*;

/**
 * Created by ac on 5/13/16.
 * todo some describe
 */
public class SelectController extends ACController {
    private final GlobalInfoProvider globalInfoProvider;
    static final private int SELECTED_RESULT_LENGTH = 16;
    private ACGLBuffer selectedPointBuffer;
    private ACGLBuffer selectedPointNumberAtomic;
    private ACProgram program = new ACProgram();
    private Vec3f startPoint;
    private Vec3f direction;
    private boolean needRun = false;

    public SelectController(GlobalInfoProvider globalInfoProvider) {
        this.globalInfoProvider = globalInfoProvider;
    }

    public void glOnSurfaceCreated(Context c) {
        this.selectedPointBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(Constant.SELECT_RESULT_BINDING_POINT)
                .postUpdate(null, SELECTED_RESULT_LENGTH)
                .glAsyncWithGPU();
        this.selectedPointNumberAtomic = ACGLBuffer.glGenBuffer(GL_ATOMIC_COUNTER_BUFFER)
                .glSetBindingPoint(Constant.SELECT_RESULT_ATOMIC_BINDING_POINT);


        String source;
        try {
            source = IOUtils.toString(c.getAssets().open("select_point.glsl"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        program.addShader(new ACShader(preCompile(source, null), GL_COMPUTE_SHADER));
        program.glCompileAndLink();
    }

    public void resetAtomic() {
        ByteBuffer bb = ByteUtil.genDirectBuffer(ByteUtil.INT_BYTE_SIZE);
        bb.putInt(0);
        bb.flip();
        selectedPointNumberAtomic.postUpdate(bb, bb.limit());
    }

    private void glAsyncBuffer() {
        selectedPointNumberAtomic.glAsyncWithGPU();
        selectedPointBuffer.glAsyncWithGPU();
    }

    public void glOnDrawFrame() {
        resetAtomic();
        glAsyncBuffer();
        updateUniform();
        program.compute(group_size);
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

}
