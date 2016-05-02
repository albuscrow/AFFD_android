package ac.affd_android.app.GL;

import ac.affd_android.app.GL.GLOBJ.ACGLBuffer;
import ac.affd_android.app.GL.GLProgram.DrawProgram;
import ac.affd_android.app.GL.GLProgram.PreComputeProgram;
import ac.affd_android.app.model.ACModelParse;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static ac.affd_android.app.Constant.*;
import static android.opengl.GLES31.*;
import static android.opengl.GLU.gluErrorString;

/**
 * Created by ac on 2/24/16.
 * todo some describe
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
        //init opengl for renderer
        glClearColor(0.3f, 0.3f, 0.3f, 1f);
        glDisable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        // Set the camera position (View matrix)
        initLookAt();

        ACModelParse obj = readObj("bishop.obj", null);

        //init buffer
        inputBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER);

        inputBuffer.glSetBindingPoint(0);
        ByteBuffer inputData = obj.getDataForComputeShader();
        inputBuffer.postUpdate(inputData, inputData.limit(), ACGLBuffer.FLOAT);
        inputBuffer.glAsyncWithGPU();


        outputPointBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER);

        outputPointBuffer.glSetBindingPoint(1);
        outputPointBuffer.postUpdate(null, obj.getTriangleNumber() * PRE_SPLIT_TRIANGLE_NUMBER * TRIANGLE_POINT_SIZE, ACGLBuffer.FLOAT);
        outputPointBuffer.glAsyncWithGPU();

        outputTriangleBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER);

        outputTriangleBuffer.glSetBindingPoint(2);
        outputTriangleBuffer.postUpdate(null, obj.getTriangleNumber() * PRE_SPLIT_TRIANGLE_NUMBER * TRIANGLE_INDEX_SIZE, ACGLBuffer.INT);
        outputTriangleBuffer.glAsyncWithGPU();

        preComputeProgram = new PreComputeProgram(obj);
        preComputeProgram.glOnSurfaceCreated(getContext());

        debugBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER);

        debugBuffer.glSetBindingPoint(16);
        debugBuffer.postUpdate(null, 1024, ACGLBuffer.FLOAT);
        debugBuffer.glAsyncWithGPU();

        //init pre compute program

        //init draw program
        drawProgram = new DrawProgram();
        drawProgram.setData(outputPointBuffer, outputTriangleBuffer);
        drawProgram.glOnSurfaceCreated(getContext());
        drawProgram.setTriangleNumber(preComputeProgram.getSplittedTriangleNumber());
    }

    private void initLookAt() {
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5, 0f, 0f, 0f, 0.0f, 1.0f, 0.0f);
    }

    private ACModelParse readObj(String objFileName, String mtlFileName) {
        InputStream inputStream;
        try {
            inputStream = getContext().getAssets().open(objFileName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        ACModelParse obj;
        try {
            obj = new ACModelParse(inputStream, null, ACModelParse.InputType.OBJ);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
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

        drawProgram.setProjectionMatrix(mProjectionMatrix);
        drawProgram.setViewMatrix(mViewMatrix);
        drawProgram.glOnDrawFrame();
        checkError("onDrawFrame");
    }

    private void asyncBuffer() {
        inputBuffer.glAsyncWithGPU();
        outputPointBuffer.glAsyncWithGPU();
        outputTriangleBuffer.glAsyncWithGPU();
        debugBuffer.glAsyncWithGPU();
    }

    private void checkError(String position) {
        int err = glGetError();
        if (err != GL_NO_ERROR) {
            if (position == null) {
                position = "unknown position";
            }
            Log.e(TAG, position + ": " + gluErrorString(err));
        }
    }
}
