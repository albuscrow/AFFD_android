package ac.affd_android.app.GL.GLProgram;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES31.*;

/**
 * Created by ac on 2/26/16.
 * todo some describe
 */
public class ACProgram {
    private static final String TAG = "ACProgram";
    private List<ACShader> shaders = new ArrayList<>();
    private int id;

    public void addShader(ACShader shader) {
        shaders.add(shader);
    }

    /**
     * compile link and check error
     */
    public void glCompileAndLink() {
        this.id = glCreateProgram();
        for (ACShader s : shaders) {
            s.glInit();
            s.glAttachProgram(id);
        }
        glLinkProgram(id);

        int[] result = new int[1];
        glGetProgramiv(id, GL_LINK_STATUS, result, 0);
        if (result[0] == GL_FALSE) {
            throw new RuntimeException();
        }
    }

    protected void glUse() {
        glUseProgram(id);
    }

    void compute(int x, int y, int z) {
        glUse();
        glDispatchCompute(x, y, z);
    }

    public void compute(int x) {
        compute(x, 1, 1);
    }

    public int getId() {
        return id;
    }

}

