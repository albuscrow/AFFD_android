package ac.affd_android.affdview.GL.GLProgram;

import ac.affd_android.affdview.Util.FileUtil;
import ac.affd_android.affdview.Util.PreferenceUtil;
import android.content.Context;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.opengl.GLES31.*;

/**
 * Created by ac on 2/26/16.
 * todo some describe
 */
public class ACProgram {
    private static final int VERSION = new Random().nextInt();
    private List<ACShader> shaders = new ArrayList<>();
    private int id;
    private String name;

    public ACProgram(String name) {
        this.name = name;
    }

    public void addShader(ACShader shader) {
        shaders.add(shader);
    }

    /**
     * compile link and check error
     */
    public void glCompileAndLink(Context context) {
        this.id = glCreateProgram();
        ByteBuffer bb = FileUtil.load(context, name);
        int oldVersion = PreferenceUtil.loadInt(context, name + "shader_version");

        if (bb == null || oldVersion != VERSION) {
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
            glGetProgramiv(id, GL_PROGRAM_BINARY_LENGTH, result, 0);

            int bufferLength = result[0];
            int length[] = new int[1];
            int format[] = new int[1];
            bb = ByteBuffer.allocate(bufferLength);
            glGetProgramBinary(id, bufferLength, length, 0, format, 0, bb);

            FileUtil.save(context, name, bb, length[0]);

            PreferenceUtil.save(context, name, format[0]);

            if (oldVersion != VERSION) {
                PreferenceUtil.save(context, name + "shader_version", VERSION);
            }
        } else {
            glProgramBinary(id, PreferenceUtil.loadInt(context, name), bb, bb.capacity());

            int[] result = new int[1];
            glGetProgramiv(id, GL_LINK_STATUS, result, 0);
            if (result[0] == GL_FALSE) {
                throw new RuntimeException();
            }

        }
    }

    protected void glUse() {
        glUseProgram(id);
    }

    private void compute(int x, int y, int z) {
        glUse();
        glDispatchCompute(x, y, z);
        glFinish();
    }

    public void compute(int x) {
        compute(x, 1, 1);
    }

    public int getId() {
        return id;
    }

}

