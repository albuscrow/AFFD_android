package ac.affd_android.app.GL;


import android.content.Context;
import android.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

import static android.opengl.GLES31.*;

/**
 * Created by ac on 2/28/16.
 */
public class ACDrawProgram extends ACProgram{

    private static final String TAG = "ACDrawProgram";
    private ACShader fragmentShader;
    private ACShader vertexShader;
    private int mvpMatrixLocation;
    private int mvMatrixLocation;
    private final float[] mMVPMatrix = new float[16];
    private float[] projectionMatrix;
    private float[] viewMatrix;

    void rotate() {

    }

    void translate() {

    }

    public void glOnSurfaceCreated(Context c) {
        String vertexSource, fragmentSource;
        try {
            vertexSource = IOUtils.toString(c.getAssets().open("vertex.glsl"));
            fragmentSource = IOUtils.toString(c.getAssets().open("fragment.glsl"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        ACProgram.ACShader vertexShader = new ACProgram.ACShader(vertexSource, GL_VERTEX_SHADER);
        ACProgram.ACShader fragmentShader = new ACProgram.ACShader(fragmentSource, GL_FRAGMENT_SHADER);
        addShader(vertexShader);
        addShader(fragmentShader);
        super.glCompileAndLink();
        vertexShader.glRunGLOperate(new GLOperator() {

            @Override
            public void glOperate() {
                mvpMatrixLocation = glGetUniformLocation(id, "wvpMatrix");
                mvMatrixLocation = glGetUniformLocation(id, "wvMatrix");
            }
        });
    }

    @Override
    public void addShader(ACShader shader) {
        if (shader.getType() == GL_VERTEX_SHADER) {
            this.vertexShader = shader;
        } else if (shader.getType() == GL_FRAGMENT_SHADER) {
            this.fragmentShader = shader;
        } else {
            Log.e(TAG, "shader's type is wrong");
        }
        super.addShader(shader);
    }

    public void setProjectionMatrix(float[] projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    public void setViewMatrix(float[] viewMatrix) {
        this.viewMatrix = viewMatrix;
    }

    public void glonDrawFrame() {

    }
}
