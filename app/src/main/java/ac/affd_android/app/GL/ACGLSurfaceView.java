package ac.affd_android.app.GL;

import ac.affd_android.app.GL.GLOBJ.ACGLBuffer;
import ac.affd_android.app.GL.GLProgram.DrawProgram;
import ac.affd_android.app.GL.GLProgram.PreComputeProgram;
import ac.affd_android.app.model.ACModelParse;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static ac.affd_android.app.Constant.*;
import static ac.affd_android.app.Util.GLUtil.checkError;
import static android.opengl.GLES31.*;

/**
 * Created by ac on 2/24/16.
 * todo some describe
 */
public class ACGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    @SuppressWarnings("unused")
    private static final String TAG = "MyGLSurfaceView";

    //matrix
    static final float[] mProjectionMatrix = new float[16];
    static final float[] mViewMatrix = new float[16];

    //shader program
    private DrawProgram drawProgram;
    private PreComputeProgram preComputeProgram;
    //gl buffer
    private ACGLBuffer inputBuffer;
    private ACGLBuffer outputPointBuffer;
    private ACGLBuffer outputTriangleBuffer;
    private ACGLBuffer debugBuffer;


    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        //init opengl for renderer
        glClearColor(0.3f, 0.3f, 0.3f, 1f);
        glDisable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        // Set the camera position (View matrix)
        initLookAt();

        //init model
        ACModelParse obj = readObj("bishop.obj", null);

        //init buffer
        glInitBuffer(obj);
        initShaderProgram(obj);
    }

    private void initShaderProgram(ACModelParse obj) {
        //init pre compute program
        preComputeProgram = new PreComputeProgram(obj);
        preComputeProgram.glOnSurfaceCreated(getContext());

        //init draw program
        drawProgram = new DrawProgram();
        drawProgram.setData(outputPointBuffer, outputTriangleBuffer);
        drawProgram.glOnSurfaceCreated(getContext());
        drawProgram.setTriangleNumber(preComputeProgram.getSplittedTriangleNumber());
    }

    private void glInitBuffer(ACModelParse obj) {
        ByteBuffer inputData = obj.getDataForComputeShader();
        inputBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(0)
                .postUpdate(inputData, inputData.limit(), ACGLBuffer.FLOAT)
                .glAsyncWithGPU();

        outputPointBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(1)
                .postUpdate(null, obj.getTriangleNumber() * PRE_SPLIT_TRIANGLE_NUMBER * TRIANGLE_POINT_SIZE, ACGLBuffer.FLOAT)
                .glAsyncWithGPU();

        outputTriangleBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(2)
                .postUpdate(null, obj.getTriangleNumber() * PRE_SPLIT_TRIANGLE_NUMBER * TRIANGLE_INDEX_SIZE, ACGLBuffer.INT)
                .glAsyncWithGPU();

        debugBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(16)
                .postUpdate(null, 1024, ACGLBuffer.FLOAT)
                .glAsyncWithGPU();

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
        initProjectionMatrix(ratio);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glAsyncBuffer();
        drawProgram.glOnDrawFrame(mViewMatrix, mProjectionMatrix);
        checkError("onDrawFrame");
    }

    private void glAsyncBuffer() {
        inputBuffer.glAsyncWithGPU();
        outputPointBuffer.glAsyncWithGPU();
        outputTriangleBuffer.glAsyncWithGPU();
        debugBuffer.glAsyncWithGPU();
    }

    private static void initLookAt() {
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5, 0f, 0f, 0f, 0.0f, 1.0f, 0.0f);
    }

    private static void initProjectionMatrix(float ratio) {
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    // template code for GLSurfaceView
    public ACGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // template code for GLSurfaceView
    public ACGLSurfaceView(Context context) {
        super(context);
        init();
    }

    // template code for GLSurfaceView
    private void init() {
        // Create an OpenGL ES 3.0 context
        setEGLContextClientVersion(3);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
