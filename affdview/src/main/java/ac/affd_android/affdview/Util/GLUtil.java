package ac.affd_android.affdview.Util;

import ac.affd_android.affdview.Constant;
import android.util.Log;

import java.nio.IntBuffer;

import static android.content.ContentValues.TAG;
import static android.opengl.GLES20.GL_NO_ERROR;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES30.GL_UNIFORM_OFFSET;
import static android.opengl.GLES30.glGetActiveUniformsiv;
import static android.opengl.GLES30.glGetUniformIndices;
import static android.opengl.GLU.gluErrorString;

/**
 * Created by ac on 5/2/16.
 * todo some describe
 */
public class GLUtil {
    static public void glCheckError(String position) {
        if (!Constant.DEBUG_SWITCH) {
            return;
        }
        int err = glGetError();
        if (err != GL_NO_ERROR) {
            if (position == null) {
                position = "unknown position";
            }
            Log.e(TAG, position + ": " + gluErrorString(err));
        }
    }

    static public IntBuffer glGetUniformOffset(int id, String[] name) {
        IntBuffer indices = ByteUtil.genDirectBuffer(name.length * ByteUtil.INT_BYTE_SIZE).asIntBuffer();
        glGetUniformIndices(id, name, indices);
        IntBuffer offsets = ByteUtil.genDirectBuffer(name.length * ByteUtil.INT_BYTE_SIZE).asIntBuffer();
        glGetActiveUniformsiv(id, 3, indices, GL_UNIFORM_OFFSET, offsets);
        return offsets;
    }
}
