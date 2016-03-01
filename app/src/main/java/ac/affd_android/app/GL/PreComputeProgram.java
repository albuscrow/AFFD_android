package ac.affd_android.app.GL;

import android.content.Context;
import static android.opengl.GLES31.*;

import android.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by ac on 2/29/16.
 */
public class PreComputeProgram extends ACProgram {
    private static final String TAG = "ComputeProgram";
    private static final int GROUP_SIZE = 64;
    private final ACOBJ obj;

    public PreComputeProgram(ACOBJ obj) {
        super();
        this.obj = obj;
    }

    public void glOnSurfaceCreated(Context c) {
        String source;
        try {
            source = IOUtils.toString(c.getAssets().open("pre_computer.glsl"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        source = preCompile(source, obj);
        Log.d(TAG, source);
        ACProgram.ACShader shader = new ACProgram.ACShader(source, GL_COMPUTE_SHADER);
        addShader(shader);
        super.glCompileAndLink();
    }

    @Override
    public void glOnDrawFrame() {
        glUse();
        glDispatchCompute(obj.getTriangleNumber() / GROUP_SIZE + 1, 1, 1);
    }

    private String preCompile(String source, ACOBJ obj) {
        int pointNumber = obj.getPointNumber();
        int triangleNumber = obj.getTriangleNumber();

        return source.replace("Point[", "Point[" + pointNumber)
                .replace("Triangle[", "Triangle[" + triangleNumber)
                .replace("local_size_x = 1", "local_size_x = " + GROUP_SIZE);
    }

}
