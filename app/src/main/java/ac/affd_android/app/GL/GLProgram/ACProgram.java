package ac.affd_android.app.GL.GLProgram;

import ac.affd_android.app.GL.GLOperator;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES31.*;

/**
 * Created by ac on 2/26/16.
 * todo some describe
 */
class ACProgram {
    private static final String TAG = "ACProgram";
    private List<ACShader> shaders = new ArrayList<>();
    private int id;

    public void addShader(ACShader shader) {
        shaders.add(shader);
    }

    /**
     * compile link and check error
     */
    void glCompileAndLink() {
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

    void glUse() {
        glUseProgram(id);
    }

    static class ACShader {
        private static final String TAG = "ACShader";
        private String source;
        private int type;
        private int id;

        ACShader(String source, int type) {
            this.source = source;
            this.type = type;
        }

        int getType() {
            return type;
        }

        public int getId() {
            return id;
        }

        void glInit() {
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

        void glAttachProgram(int id) {
            glAttachShader(id, this.id);
        }

        public void glRunGLOperate(GLOperator operator) {
            operator.glOperate();
        }
    }


    public void glOnSurfaceCreated(Context c) {
    }

    public void glOnDrawFrame() {
    }
}

