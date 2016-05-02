package ac.affd_android.app.Util;

import ac.affd_android.app.Constant;
import android.util.Log;

import static android.content.ContentValues.TAG;
import static android.opengl.GLES20.GL_NO_ERROR;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLU.gluErrorString;

/**
 * Created by ac on 5/2/16.
 * todo some describe
 */
public class GLUtil {
    static public void checkError(String position) {
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
}
