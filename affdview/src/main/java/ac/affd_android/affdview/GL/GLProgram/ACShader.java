package ac.affd_android.affdview.GL.GLProgram;

import static android.opengl.GLES20.*;

/**
 * Created by ac on 5/11/16.
 * todo some describe
 */
public class ACShader {
    private static final String TAG = "ACShader";
    private String source;
    private int type;
    private int id;

    public ACShader(String source, int type) {
        this.source = source;
        this.type = type;
    }

    int getType() {
        return type;
    }

    void glInit() {
        id = glCreateShader(type);
        glShaderSource(id, this.source);
        glCompileShader(id);

        //check error
        int[] result = new int[1];
        glGetShaderiv(id, GL_COMPILE_STATUS, result, 0);
        if (result[0] == GL_FALSE) {
            System.out.println(glGetShaderInfoLog(id));
            throw new RuntimeException();
        }
    }

    void glAttachProgram(int id) {
        glAttachShader(id, this.id);
    }
}
