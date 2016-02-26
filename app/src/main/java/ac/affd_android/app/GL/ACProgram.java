package ac.affd_android.app.GL;

import android.graphics.Shader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES31.*;

/**
 * Created by ac on 2/26/16.
 */
public class ACProgram {
//    private static final int DRAW_SHADER = 0;
//    private static final int COMPUTE_SHADER = 1;
    private static final String TAG = "ACProgram";
//    private int type = DRAW_SHADER;
    private List<ACShader> shaders = new ArrayList<>();
    private int id;

//    public ACProgram() {
//        this.type = type;
//    }

    public void addShader(ACShader shader) {
        shaders.add(shader);
    }

    public void glInit() {
        this.id = glCreateProgram();
        for (ACShader s : shaders) {
            s.glInit();
            s.glAttachProgram(id);
        }
        glLinkProgram(id);

        int[] result = new int[1];
        glGetProgramiv(id, GL_LINK_STATUS, result, 0);
        if (result[0] == GL_FALSE) {
            String msg = glGetProgramInfoLog(id);
            Log.e(TAG, "link error: " + msg);
        }
    }

    public void glUse() {
        glUseProgram(id);
    }

    public class ACShader {
        private static final String TAG = "ACShader";
        private String source;
        private int type;
        private int id;

        public ACShader(String source, int type) {
            this.source = source;
            this.type = type;
            addShader(this);
        }

        public void glInit() {
            id = glCreateShader(type);
//            String source = IOUtils.toString(getContext().getAssets().open("test_compute_shader.glsl"));
            glShaderSource(id, this.source);
            glCompileShader(id);

            //check error
            int[] result = new int[1];
            glGetShaderiv(id, GL_COMPILE_STATUS, result, 0);
            if (result[0] == GL_FALSE) {
                String msg = glGetShaderInfoLog(id);
                Log.e(TAG, "compile error: " + msg);
            }
        }

        public void glAttachProgram(int id) {
            glAttachShader(id, this.id);
        }
    }
}

