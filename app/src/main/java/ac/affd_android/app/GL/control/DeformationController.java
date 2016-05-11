package ac.affd_android.app.GL.control;

import ac.affd_android.app.Constant;
import ac.affd_android.app.GL.GLOBJ.ACGLBuffer;
import ac.affd_android.app.GL.GLProgram.ACProgram;
import ac.affd_android.app.GL.GLProgram.ACShader;
import ac.affd_android.app.GL.GLProgram.ShaderPreCompiler;
import ac.affd_android.app.Util.GLUtil;
import ac.affd_android.app.model.GlobalInfoProvider;
import android.content.Context;
import android.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES30.GL_UNIFORM_BUFFER;
import static android.opengl.GLES31.GL_COMPUTE_SHADER;

/**
 * Created by ac on 5/4/16.
 * used for deformation in every frame
 */
public class DeformationController extends ACController{
    private static final String TAG = "DeformationController";
    private final GlobalInfoProvider globalInfoProvider;
    ACProgram deformProgram = new ACProgram();
    ACProgram selectProgram = new ACProgram();
    ACGLBuffer controlPointUniformBuffer;
    private boolean controlPointChange = true;

    public DeformationController(GlobalInfoProvider globalInfoProvider) {
        this.globalInfoProvider = globalInfoProvider;
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
        preCompiler.add(new ShaderPreCompiler(new String[]{"const int SPLIT_TRIANGLE_NUMBER = 0"}, new String[]{"const int SPLIT_TRIANGLE_NUMBER = " + globalInfoProvider.getSplitTriangleNumber()}));
        deformProgram.addShader(new ACShader(preCompile(source, preCompiler), GL_COMPUTE_SHADER));
        Log.i(TAG, "begin compile deform program");
        deformProgram.glCompileAndLink();
    }

}
