package ac.affd_android.app.GL.control;

import ac.affd_android.app.GL.GLProgram.ACProgram;
import ac.affd_android.app.Util.GLUtil;
import ac.affd_android.app.model.ACModelParse;
import android.content.Context;
import android.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

import static ac.affd_android.app.Constant.PRE_SPLIT_POINT_NUMBER;
import static ac.affd_android.app.Constant.PRE_SPLIT_TRIANGLE_NUMBER;
import static android.opengl.GLES31.GL_COMPUTE_SHADER;

/**
 * Created by ac on 5/4/16.
 * used for deformation in every frame
 */
public class DeformationController {
    private static final String TAG = "DeformationController";
    private static final int GROUP_SIZE = 64;
    private final ACModelParse obj;
    private int splittedTriangleNumber;
    private int splittedPointNumber;
    ACProgram deformProgram = new ACProgram();
    ACProgram selectProgram = new ACProgram();

    public DeformationController(ACModelParse obj, int splittedTriangleNumber, int splittedPointNumber) {
        this.splittedTriangleNumber = splittedTriangleNumber;
        this.splittedPointNumber = splittedPointNumber;
        this.obj = obj;
    }

    public void glOnSurfaceCreated(Context c) {
        //init program
        initProgram(c);

        //check error
        GLUtil.checkError(TAG);
    }

    public void glOnDrawFrame() {
        deformProgram.compute(this.splittedTriangleNumber / GROUP_SIZE + 1);
    }

    private void initProgram(Context c) {
        String source;
        try {
            source = IOUtils.toString(c.getAssets().open("deformation.glsl"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        deformProgram.addShader(new ACProgram.ACShader(preCompile(source), GL_COMPUTE_SHADER));
        Log.i(TAG, "begin compile deform program");
        deformProgram.glCompileAndLink();
    }

    private String preCompile(String source) {
        Integer triangleNumber = obj.getTriangleNumber();
        return source
                .replace("SplitPoint BUFFER_INPUT_POINTS[", "SplitPoint BUFFER_INPUT_POINTS[" + triangleNumber * PRE_SPLIT_POINT_NUMBER)
                .replace("SplitTriangle BUFFER_INPUT_TRIANGLES[", "SplitTriangle BUFFER_INPUT_TRIANGLES[" + triangleNumber * PRE_SPLIT_TRIANGLE_NUMBER)
                .replace("const int SPLIT_TRIANGLE_NUMBER = 0", "const int SPLIT_TRIANGLE_NUMBER = " + splittedTriangleNumber)
                .replace("local_size_x = 1", "local_size_x = " + GROUP_SIZE);
    }
}
