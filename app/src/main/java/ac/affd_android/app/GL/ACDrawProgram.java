package ac.affd_android.app.GL;


import android.util.Log;

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

    void rotate() {

    }

    void translate() {

    }

    @Override
    public void glInit() {
        super.glInit();
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
}
