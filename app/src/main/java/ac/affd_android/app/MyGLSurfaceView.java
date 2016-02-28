package ac.affd_android.app;

import ac.affd_android.app.GL.ACGLBuffer;
import ac.affd_android.app.GL.ACOBJ;
import ac.affd_android.app.GL.ACProgram;
import android.content.Context;
import static android.opengl.GLES31.*;
import static android.opengl.GLU.*;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import org.apache.commons.io.IOUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by ac on 2/24/16.
 */
public class MyGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private static final String TAG = "MyGLSurfaceView";

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private ACGLBuffer testBuffer;
    private ACProgram testProgram;

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
//        String version = glGetString(
//                GL10.GL_VERSION);
//        Log.w(TAG, "GLES Version: " + version);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        //read obj
//        try {
//            new ACOBJ("test_compute_shader.glsl", null, getContext());
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }

        //init computer shader
        String source;
        try {
            source = IOUtils.toString(getContext().getAssets().open("test_compute_shader.glsl"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        testProgram = new ACProgram();
        ACProgram.ACShader s = testProgram.new ACShader(source, GL_COMPUTE_SHADER);

        testProgram.glInit();

        //init buffer
        testBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER);
        testBuffer.glSetBindingPoint(0);

        IntBuffer ib = ByteBuffer.allocateDirect(32 * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        for (int i = 0; i < 32; ++i) {
            ib.put(1);
        }
        ib.flip();
        testBuffer.postUpdate(ib);
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

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        //async buffer with gpu
        testBuffer.glAsyncWithGPU();

        glClear(GL_COLOR_BUFFER_BIT);
        testProgram.glUse();
        glDispatchCompute(1, 1, 1);

        //output
        Log.i(TAG, "testBuffer: " + testBuffer.glToString());
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
