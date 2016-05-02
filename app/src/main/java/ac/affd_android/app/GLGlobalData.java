package ac.affd_android.app;

import android.opengl.Matrix;

/**
 * Created by ac on 5/2/16.
 * todo some describe
 */
public class GLGlobalData {
    public static final float[] mProjectionMatrix = new float[16];
    public static final float[] mViewMatrix = new float[16];

    public static void initLookAt() {
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5, 0f, 0f, 0f, 0.0f, 1.0f, 0.0f);
    }

    public static void initProjectionMatrix(float ratio) {
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }
}
