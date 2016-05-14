package ac.affd_android.app.GL;

import ac.affd_android.app.Constant;
import ac.affd_android.app.GL.GLOBJ.ACGLBuffer;
import ac.affd_android.app.GL.GLProgram.ShaderPreCompiler;
import ac.affd_android.app.GL.control.DeformationController;
import ac.affd_android.app.GL.control.DrawProgram;
import ac.affd_android.app.GL.control.PreComputeController;
import ac.affd_android.app.GL.control.SelectController;
import ac.affd_android.app.model.*;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static ac.affd_android.app.Constant.*;
import static ac.affd_android.app.Util.GLUtil.glCheckError;
import static android.opengl.GLES31.*;

/**
 * Created by ac on 2/24/16.
 * todo some describe
 */
public class ACGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, GlobalInfoProvider {
    @SuppressWarnings("unused")
    private static final String TAG = "MyGLSurfaceView";

    //matrix
    static final float[] mProjectionMatrix = new float[16];
    static final float[] mViewMatrix = new float[16];
    private static final int DEFORMATION_MODE = 0;
    private static final int ROTATE_MODE = 1;

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
    private ACGLBuffer selectParameterBuffer;

    private ACGLBuffer debugBuffer;
    private ACModelParse obj = readObj("cube.obj", null);
    private BSplineBody bsplineBody = new BSplineBody(obj.getLength());
    private SelectController selectPointController;
    private int mode = DEFORMATION_MODE;
    private float aspect;


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
        preComputeController = new PreComputeController(this);

        ShaderPreCompiler inputPreCompiler = new ShaderPreCompiler()
                .add("InputPoint BUFFER_INPUT_POINTS[", "InputPoint BUFFER_INPUT_POINTS[" + getOriginalPointNumber())
                .add("InputTriangle BUFFER_INPUT_TRIANGLES[", "InputTriangle BUFFER_INPUT_TRIANGLES[" + getOriginalTriangleNumber());

        ShaderPreCompiler splitedPrecompiler = new ShaderPreCompiler()
                .add("SplitPoint BUFFER_SPLIT_POINTS[", "SplitPoint BUFFER_SPLIT_POINTS[" + getOriginalTriangleNumber() * PRE_SPLIT_POINT_NUMBER)
                .add("SplitTriangle BUFFER_SPLIT_TRIANGLES[", "SplitTriangle BUFFER_SPLIT_TRIANGLES[" + getOriginalTriangleNumber() * PRE_SPLIT_TRIANGLE_NUMBER);

        preComputeController.glOnSurfaceCreated(getContext(), Arrays.asList(inputPreCompiler), Arrays.asList(inputPreCompiler, splitedPrecompiler));

        //init deform program
        deformationController = new DeformationController(this);
        deformationController.glOnSurfaceCreated(getContext(), Arrays.asList(splitedPrecompiler));

        selectPointController = new SelectController(this);
        selectPointController.glOnSurfaceCreated(getContext());

        glInitBufferAfterSplit();

        //init draw program
        drawProgram = new DrawProgram(this);
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
                .postUpdate(null, preComputeController.getSplittedTriangleNumber() * deformationController.getTessellationPointNumber() * TRIANGLE_POINT_SIZE)
                .glAsyncWithGPU();

        rendererTriangleBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(Constant.RENDERER_INDEX_BINDING_POINT)
                .postUpdate(null, preComputeController.getSplittedTriangleNumber() * deformationController.getTessellationTriangleNumber() * TRIANGLE_INDEX_SIZE)
                .glAsyncWithGPU();

        selectParameterBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(Constant.TESSELLATION_ORIGINAL_PARAMETER_BINDING_POINT)
                .postUpdate(null, preComputeController.getSplittedTriangleNumber() * deformationController.getTessellationPointNumber() * TRIANGLE_PARAMETER_SIZE)
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
            obj = new ACModelParse(inputStream, null, InputType.OBJ);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return obj;
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        glViewport(0, 0, width, height);
        aspect = (float) width / height;
        initProjectionMatrix(aspect);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glAsyncBuffer();
        deformationController.glOnDrawFrame();
        glFinish();

        selectPointController.glOnDrawFrame();
        glFinish();

//        Log.d(TAG, debugBuffer.glToString(ACGLBuffer.FLOAT));
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

    final static int NEAR = 3;

    private static void initProjectionMatrix(float aspect) {
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -aspect, aspect, -1, 1, NEAR, 7);
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

    @Override
    public int getOriginalTriangleNumber() {
        if (obj == null) {
            throw new RuntimeException();
        } else {
            return obj.getTriangleNumber();
        }
    }

    @Override
    public int getOriginalPointNumber() {
        if (obj == null) {
            throw new RuntimeException();
        } else {
            return obj.getPointNumber();
        }
    }

    @Override
    public int getSplitTriangleNumber() {
        if (preComputeController == null) {
            throw new RuntimeException();
        } else {
            return preComputeController.getSplittedTriangleNumber();
        }
    }

    @Override
    public int getSplitPointNumber() {
        if (preComputeController == null) {
            throw new RuntimeException();
        } else {
            return preComputeController.getSplittedPointNumber();
        }
    }

    @Override
    public Buffer getBsplineBodyInfo() {
        if (bsplineBody == null) {
            throw new RuntimeException();
        } else {
            return bsplineBody.getInfo();
        }
    }

    @Override
    public Buffer getBsplineBodyFastControlPoint() {
        if (bsplineBody == null) {
            throw new RuntimeException();
        } else {
            return bsplineBody.getControllerPointForSpeedUp();
        }
    }

    @Override
    public int getRendererTriangleNumber() {
        return getSplitTriangleNumber() * deformationController.getTessellationTriangleNumber();
    }

    private float lastX;
    private float lastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        event.getAction();
        event.getActionIndex();
        event.getActionMasked();
        float[] modelViewMatrixI;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                mode = DEFORMATION_MODE;
                modelViewMatrixI = getModelViewMatrixI();
                Vec3f startPoint = new Vec3f(0, 0, 0).multiplyMV(modelViewMatrixI, 1);
                Vec3f endPoint = new Vec3f(
                        lastX / getHeight() * 2 - aspect,
                        1 - lastY / getHeight() * 2, -NEAR)
                        .multiplyMV(modelViewMatrixI, 1);
                selectPointController.setStartPointAndDirection(startPoint, endPoint.subtract(startPoint));
                requestRender();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ROTATE_MODE;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DEFORMATION_MODE) {
                    modelViewMatrixI = getModelViewMatrixI();
                    Vec3f direction = new Vec3f(event.getX() - lastX, lastY - event.getY(), 0);
                    if (direction.length() > 20) {
                        direction = direction.multiplyMV(modelViewMatrixI, 0);
                        final Vec3f selectParameter = selectPointController.getSelectParameter();
                        bsplineBody.directFFD(selectParameter, direction.div(500));
                        deformationController.notifyControlPointChange();
                    }

                } else {
                    float deltaX = event.getX() - lastX;
                    float deltaY = event.getY() - lastY;
                    if (deltaX == 0 || deltaY == 0) {
                        break;
                    }
                    //noinspection SuspiciousNameCombination
                    drawProgram.rotate(new Vec2(deltaY, deltaX));
                    lastX = event.getX();
                    lastY = event.getY();
                }
                requestRender();
                break;
            case MotionEvent.ACTION_UP:
                selectPointController.reset();
                bsplineBody.saveControlPoints();
                break;

        }
        return true;
    }

    private float[] getModelViewMatrixI() {
        float[] model_and_view_matrix_I = new float[16];
        Matrix.multiplyMM(model_and_view_matrix_I, 0, mViewMatrix, 0, drawProgram.getModelMatrix(), 0);
        Matrix.invertM(model_and_view_matrix_I, 0, model_and_view_matrix_I, 0);
        return model_and_view_matrix_I;
    }
}
