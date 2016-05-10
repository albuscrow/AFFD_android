package ac.affd_android.app.GL;

import ac.affd_android.app.Constant;
import ac.affd_android.app.GL.GLOBJ.ACGLBuffer;
import ac.affd_android.app.GL.control.DeformationController;
import ac.affd_android.app.GL.control.DrawProgram;
import ac.affd_android.app.GL.control.PreComputeController;
import ac.affd_android.app.model.ACModelParse;
import ac.affd_android.app.model.BSplineBody;
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
import static ac.affd_android.app.Util.GLUtil.glCheckError;
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
    private PreComputeController preComputeController;
    private DeformationController deformationController;
    //gl buffer
    private ACGLBuffer inputBuffer;
    private ACGLBuffer bsplineBodyInfoBuffer;
    private ACGLBuffer splitResultBuffer;

    // init after pre compute
    private ACGLBuffer rendererPointBuffer;
    private ACGLBuffer rendererTriangleBuffer;

    private ACGLBuffer debugBuffer;
    private ACModelParse obj = readObj("cube.obj", null);
    private BSplineBody bsplineBody = new BSplineBody(obj.getLength());


    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        //init opengl for renderer
        glClearColor(0.3f, 0.3f, 0.3f, 1f);
        glDisable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        // Set the camera position (View matrix)
        initLookAt();

        //init model
        ACModelParse obj = readObj("cube.obj", null);

        //init buffer
        glInitBuffer(obj);
        glInitShaderProgramAndPreCompute(obj);
    }

    private void glInitShaderProgramAndPreCompute(ACModelParse obj) {
        //init pre compute program
        preComputeController = new PreComputeController(obj);
        preComputeController.glOnSurfaceCreated(getContext());

        //init deform program
        deformationController = new DeformationController(obj, preComputeController.getSplittedTriangleNumber(), preComputeController.getSplittedPointNumber());
        deformationController.glOnSurfaceCreated(getContext());

        glInitBufferAfterSplit();

        //init draw program
        drawProgram = new DrawProgram(preComputeController.getSplittedTriangleNumber());
        drawProgram.glOnSurfaceCreated(getContext(), rendererPointBuffer, rendererTriangleBuffer);

    }

    private void glInitBuffer(ACModelParse obj) {
        ByteBuffer inputData = obj.getDataForComputeShader();
        inputBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(Constant.PRE_COMPUTE_INPUT_BINDING_POINT)
                .postUpdate(inputData, inputData.limit())
                .glAsyncWithGPU();
        final ByteBuffer bsplineBodyInfo = bsplineBody.getInfo();
        bsplineBodyInfoBuffer = ACGLBuffer.glGenBuffer(GL_UNIFORM_BUFFER)
                .glSetBindingPoint(Constant.BSPLINEBODY_INFO_BINDING_POINT)
                .postUpdate(bsplineBodyInfo, bsplineBodyInfo.limit())
                .glAsyncWithGPU();
        splitResultBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(Constant.SPLIT_RESULT_BINDING_POINT)
                .postUpdate(null, obj.getTriangleNumber() * PRE_SPLIT_TOTAL_SIZE)
                .glAsyncWithGPU();
        if (Constant.ACTIVE_DEBUG_BUFFER) {
            debugBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                    .glSetBindingPoint(Constant.DEBUG_BINDING_POINT)
                    .postUpdate(null, 112)
                    .glAsyncWithGPU();
        }
    }

    private void glInitBufferAfterSplit() {
        rendererPointBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(Constant.RENDERER_POINT_BINDING_POINT)
                .postUpdate(null, preComputeController.getSplittedTriangleNumber() * TRIANGLE_POINT_SIZE)
                .glAsyncWithGPU();

        rendererTriangleBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(Constant.RENDERER_INDEX_BINDING_POINT)
                .postUpdate(null, preComputeController.getSplittedTriangleNumber() * TRIANGLE_INDEX_SIZE)
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
        initProjectionMatrix(ratio);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glAsyncBuffer();
        deformationController.glOnDrawFrame();
        glFinish();
        Log.d(TAG, debugBuffer.glToString());
        drawProgram.glOnDrawFrame(mViewMatrix, mProjectionMatrix);
        glCheckError("onDrawFrame");
    }

    private void glAsyncBuffer() {
        inputBuffer.glAsyncWithGPU();
        rendererPointBuffer.glAsyncWithGPU();
        rendererTriangleBuffer.glAsyncWithGPU();
        splitResultBuffer.glAsyncWithGPU();
        if (Constant.ACTIVE_DEBUG_BUFFER) {
            debugBuffer.glAsyncWithGPU();
        }
    }

    private static void initLookAt() {
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5, 0f, 0f, 0f, 0.0f, 1.0f, 0.0f);
    }

    private static void initProjectionMatrix(float ratio) {
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
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
