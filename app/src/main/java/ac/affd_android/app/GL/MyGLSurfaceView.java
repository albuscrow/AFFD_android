package ac.affd_android.app.GL;

import android.content.Context;
import static android.opengl.GLES31.*;
import static android.opengl.GLU.*;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ac on 2/24/16.
 */
public class MyGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private static final String TAG = "MyGLSurfaceView";

    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private ACDrawProgram drawProgram;
//    private ACGLBuffer testBuffer;
//    private ACProgram testProgram;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public MyGLSurfaceView(Context context) {
        super(context);
        init();
    }

    private void init() {
        // Create an OpenGL ES 3.0 context
        setEGLContextClientVersion(3);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        glClearColor(0.3f, 0.3f, 0.3f, 1f);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5, 0f, 0f, 0f, 0.0f, 1.0f, 0.0f);

        drawProgram = new ACDrawProgram();
        drawProgram.glOnSurfaceCreated(getContext());
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        drawProgram.setProjectionMatrix(mProjectionMatrix);
        drawProgram.setViewMatrix(mViewMatrix);
        drawProgram.glOnDrawFrame();

    }
    private void checkError() {
        checkError("unspecific position");
    }

    private void checkError(String position) {
        int err = glGetError();
        if (err != GL_NO_ERROR) {
            Log.e(TAG, position + ": " + gluErrorString(err));
        }
    }
}
