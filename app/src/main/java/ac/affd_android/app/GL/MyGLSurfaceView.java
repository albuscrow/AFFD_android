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
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by ac on 2/24/16.
 */
public class MyGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private static final String TAG = "MyGLSurfaceView";

    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private DrawProgram drawProgram;
    private PreComputeProgram preComputeProgram;
    private ACGLBuffer inputBuffer;
    private ACGLBuffer outputPointBuffer;
    private ACGLBuffer outputTriangleBuffer;
    private ACGLBuffer debugBuffer;
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

        ACOBJ obj = readObj("test2.obj", null);
        if (obj == null) {
            Log.e(TAG, "open obj file fail");
            return;
        }


        //init buffer
        inputBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER);
        if (inputBuffer == null) {
            Log.e(TAG, "gen ssbo failed");
            return;
        }
        inputBuffer.glSetBindingPoint(0);
        ByteBuffer inputData = obj.getDataForComputeShader();
        inputBuffer.postUpdate(inputData, inputData.limit(), ACGLBuffer.FLOAT);

        outputPointBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER);
        if (outputPointBuffer == null) {
            Log.e(TAG, "gen ssbo failed");
            return;
        }
        outputPointBuffer.glSetBindingPoint(1);
        outputPointBuffer.postUpdate(null, obj.getPointNumber() * (3 + 3 + 2) * 4, ACGLBuffer.FLOAT);

        outputTriangleBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER);
        if (outputTriangleBuffer == null) {
            Log.e(TAG, "gen ssbo failed");
            return;
        }
        outputTriangleBuffer.glSetBindingPoint(2);
        outputTriangleBuffer.postUpdate(null, obj.getPointNumber() * (3 + 3 + 2) * 4, ACGLBuffer.INT);

        debugBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER);
        if (debugBuffer == null) {
            Log.e(TAG, "gen ssbo failed");
            return;
        }
        debugBuffer.glSetBindingPoint(16);
        debugBuffer.postUpdate(null, 1024, ACGLBuffer.FLOAT);


        //init pre compute program
        preComputeProgram = new PreComputeProgram(obj);
        preComputeProgram.glOnSurfaceCreated(getContext());

        //init draw program
        drawProgram = new DrawProgram();
        drawProgram.glOnSurfaceCreated(getContext());
    }

    private ACOBJ readObj(String objFileName, String mtlFileName) {
        InputStream inputStream;
        try {
            inputStream = getContext().getAssets().open(objFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ACOBJ obj;
        try {
            obj = new ACOBJ(inputStream, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return obj;
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

        asyncBuffer();
        preComputeProgram.glOnDrawFrame();
        Log.d(TAG, debugBuffer.glToString());

        drawProgram.setProjectionMatrix(mProjectionMatrix);
        drawProgram.setViewMatrix(mViewMatrix);
        drawProgram.glOnDrawFrame();


    }

    private void asyncBuffer() {
        inputBuffer.glAsyncWithGPU();
        outputPointBuffer.glAsyncWithGPU();
        outputTriangleBuffer.glAsyncWithGPU();
        debugBuffer.glAsyncWithGPU();
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
