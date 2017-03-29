package ac.affd_android.affdview.GL;

import ac.affd_android.affdview.Constant;
import ac.affd_android.affdview.GL.GLOBJ.ACGLBuffer;
import ac.affd_android.affdview.GL.GLProgram.GLSLPreprocessor;
import ac.affd_android.affdview.GL.control.DeformationController;
import ac.affd_android.affdview.GL.control.DrawProgram;
import ac.affd_android.affdview.GL.control.PreComputeController;
import ac.affd_android.affdview.GL.control.SelectController;
import ac.affd_android.affdview.R;
import ac.affd_android.affdview.model.*;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Collections;

import static ac.affd_android.affdview.Constant.*;
import static ac.affd_android.affdview.Util.GLUtil.glCheckError;
import static android.graphics.BitmapFactory.decodeStream;
import static android.opengl.GLES31.*;

/**
 * Created by ac on 2/24/16.
 * todo some describe
 */
public class ACGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, GlobalInfoProvider {
    @SuppressWarnings("unused")
    private static final String TAG = "MyGLSurfaceView";

    //matrix
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private static final int DEFORMATION_MODE = 0;
    private static final int ROTATE_MODE = 1;
    private String deformationComputeShaderFileName;

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
    private ACModelParse obj;
    private BSplineBody bsplineBody;
    private SelectController selectPointController;
    private int mode = DEFORMATION_MODE;
    private float aspect;
    private int textureId;
    private InputStream objFileStream = null;
    private int dup = 3;
    private boolean is_ffd = false;

    public void setObjFileStream(InputStream objFileStream) {
        this.objFileStream = objFileStream;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        if (objFileStream == null) {
            throw new RuntimeException("objFileStream can not be null");
        }
        obj = readObj(objFileStream, null);
        bsplineBody = new BSplineBody(obj.getLength());
        //init opengl for renderer
        glClearColor(0.3f, 0.3f, 0.3f, 1f);
        glDisable(GL_CULL_FACE);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        // Set the camera position (View matrix)
        initLookAt();

        //init buffer
        glInitBuffer();
        glInitTexture();
        glInitShaderProgramAndPreCompute(obj);

//        vec4 adjacentPNNormal[SPLITTED_TRIANGLE_NUMBER * 6];
//        uvec3 pointIndex[SPLITTED_TRIANGLE_NUMBER];
//
//        vec3 pnPosition[SPLITTED_POINT_NUMBER];
//        vec3 pnNormal[SPLITTED_POINT_NUMBER];
//        vec3 originalPosition[SPLITTED_POINT_NUMBER];
//        float texU[SPLITTED_POINT_NUMBER];
//        float texV[SPLITTED_POINT_NUMBER];
//        uint cageIndex[SPLITTED_POINT_NUMBER];

//        IntBuffer fb = inputBuffer.getData().asIntBuffer();
//        int limit = fb.limit();
//        for (int i = 0; i < limit; i += 1) {
//            System.out.println(" " + fb.get());
//        }

    }

    private void glInitTexture() {
        int[] textureIds = new int[1];
        glGenTextures(1, textureIds, 0);
        glBindTexture(GL_TEXTURE_2D, textureIds[0]);
        textureId = textureIds[0];
        try {
            final Bitmap bitmap = decodeStream(getContext().openFileInput("textureForFFD.jpg"));
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glCheckError("gen texture");
    }

    private void glInitShaderProgramAndPreCompute(ACModelParse obj) {
        //init pre compute program
        preComputeController = new PreComputeController(this);

        GLSLPreprocessor inputPreCompiler = new GLSLPreprocessor()
                .add("POINT_NUMBER", Integer.toString(getOriginalPointNumber()))
                .add("TRIANGLE_NUMBER", Integer.toString(getOriginalTriangleNumber()));

        GLSLPreprocessor splitPreCompiler = new GLSLPreprocessor()
                .add("SPLITTED_POINT_NUMBER", Integer.toString(getOriginalTriangleNumber() * PRE_SPLIT_POINT_NUMBER))
                .add("SPLITTED_TRIANGLE_NUMBER", Integer.toString(getOriginalTriangleNumber() * PRE_SPLIT_TRIANGLE_NUMBER));
        preComputeController.glOnSurfaceCreated(getContext(), Collections.singletonList(inputPreCompiler), Arrays.asList(inputPreCompiler, splitPreCompiler));


        //init deform program
        deformationController = new DeformationController(this);
        deformationController.glOnSurfaceCreated(getContext(), Collections.singletonList(splitPreCompiler), deformationComputeShaderFileName);

        selectPointController = new SelectController(this);
        selectPointController.glOnSurfaceCreated(getContext());
        glInitBufferAfterSplit();

        //init draw program
        drawProgram = new DrawProgram(this, "drawProgram");
        drawProgram.glOnSurfaceCreated(getContext(), rendererPointBuffer, rendererTriangleBuffer);
    }

    private void glInitBuffer() {
        ByteBuffer inputData = obj.getDataForComputeShader();
        inputBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(Constant.PRE_COMPUTE_INPUT_BINDING_POINT)
                .postUpdate(inputData, inputData.limit())
                .glAsyncWithGPU(GL_STATIC_DRAW);
        final ByteBuffer bsplineBodyInfo = bsplineBody.getInfo();
        bsplineBodyInfoBuffer = ACGLBuffer.glGenBuffer(GL_UNIFORM_BUFFER)
                .glSetBindingPoint(Constant.BSPLINEBODY_INFO_BINDING_POINT)
                .postUpdate(bsplineBodyInfo, bsplineBodyInfo.limit())
                .glAsyncWithGPU(GL_STATIC_DRAW);
        final int triangleNumber = getOriginalTriangleNumber() * PRE_SPLIT_TRIANGLE_NUMBER;
        final int pointNumber = getOriginalPointNumber() * PRE_SPLIT_POINT_NUMBER;
        int length = triangleNumber * (6 + 1) * 4 * 4 + pointNumber * (4 + 4 + 4 + 4 + 4) * 4 * 4;
        splitResultBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(Constant.SPLIT_RESULT_BINDING_POINT)
                .postUpdate(null, length)
                .glAsyncWithGPU(GL_STREAM_COPY);
        if (Constant.ACTIVE_DEBUG_BUFFER) {
            debugBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                    .glSetBindingPoint(Constant.DEBUG_BINDING_POINT)
                    .postUpdate(null, 1024 * 1204)
                    .glAsyncWithGPU(GL_DYNAMIC_READ);
        }
    }

    private void glInitBufferAfterSplit() {
        final int splittedTriangleNumber = preComputeController.getSplittedTriangleNumber();
        final int tessellationPointNumber = deformationController.getTessellationPointNumber();
        rendererPointBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(Constant.RENDERER_POINT_BINDING_POINT)
                .postUpdate(null, splittedTriangleNumber * tessellationPointNumber * POINT_SIZE)
                .glAsyncWithGPU(GL_DYNAMIC_COPY);

        rendererTriangleBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(Constant.RENDERER_INDEX_BINDING_POINT)
                .postUpdate(null, splittedTriangleNumber * deformationController.getTessellationTriangleNumber() * TRIANGLE_INDEX_SIZE)
                .glAsyncWithGPU(GL_DYNAMIC_COPY);

        selectParameterBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(Constant.TESSELLATION_ORIGINAL_PARAMETER_BINDING_POINT)
                .postUpdate(null, splittedTriangleNumber * tessellationPointNumber * TRIANGLE_PARAMETER_SIZE)
                .glAsyncWithGPU(GL_DYNAMIC_COPY);
    }


    private ACModelParse readObj(InputStream objIS, @Nullable InputStream mtl) {
        InputStream inputStream = objIS;
        ACModelParse obj;
        try {
            obj = new ACModelParse(inputStream, null, InputType.OBJ);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return obj;
    }

    private final static float TAN_22_5 = 0.40402622583516f;

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        glViewport(0, 0, width, height);
        aspect = (float) width / height;

        initProjectionMatrix(aspect);
    }

    @Override
    public void onDrawFrame(GL10 unused) {

        glAsyncBuffer();
        deformationController.glOnDrawFrame();

        selectPointController.glOnDrawFrame();

        glBindTexture(GL_TEXTURE_2D, textureId);
//        FloatBuffer fb = debugBuffer.getData().asFloatBuffer();
//        for (int i = 0; i < 10; ++i) {
//            for (int j = 0; j < 4; ++j) {
//                System.out.print(" " + fb.get());
//            }
//            System.out.println("");
//        }

        drawProgram.glOnDrawFrame(mViewMatrix, mProjectionMatrix);

        glCheckError("onDrawFrame");
    }

    private void glAsyncBuffer() {
        inputBuffer.glAsyncWithGPU(GL_STATIC_DRAW);
        rendererPointBuffer.glAsyncWithGPU(GL_DYNAMIC_COPY);
        rendererTriangleBuffer.glAsyncWithGPU(GL_DYNAMIC_COPY);
        splitResultBuffer.glAsyncWithGPU(GL_DYNAMIC_COPY);
        bsplineBodyInfoBuffer.glAsyncWithGPU(GL_STATIC_DRAW);
        if (Constant.ACTIVE_DEBUG_BUFFER) {
            debugBuffer.glAsyncWithGPU(GL_DYNAMIC_READ);
        }
    }

    private final Vec3f eyePosition = new Vec3f(0, 0, 0);

    private void initLookAt() {
//        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5, 0f, 0f, 0f, 0.0f, 1.0f, 0.0f);
        Matrix.setLookAtM(mViewMatrix, 0,
                eyePosition.getComponent(0), eyePosition.getComponent(1), eyePosition.getComponent(2),
                0f, 0f, -100f, 0.0f, 1.0f, 0.0f);
    }

    private final static int NEAR = 1;

    private void initProjectionMatrix(float aspect) {
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        float bottom = NEAR * -TAN_22_5;
        float top = NEAR * TAN_22_5;
        float left = aspect * bottom;
        float right = aspect * top;
        float far = 100.0f;
//        Matrix.frustumM(mProjectionMatrix, 0, -aspect, aspect, -1, 1, NEAR, 100);
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, NEAR, far);
    }

    // template code for GLSurfaceView
    public ACGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //get attr
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ACGLSurfaceView, 0, 0);
        deformationComputeShaderFileName = null;
        try {
            deformationComputeShaderFileName = ta.getString(R.styleable.ACGLSurfaceView_deformation_compute_shader_name);
        } finally {
            ta.recycle();
        }


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
    public FloatBuffer getBsplineBodyFastControlPoint() {
        if (bsplineBody == null) {
            throw new RuntimeException();
        } else {
            return bsplineBody.getControlPointForSpeedUp();
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
                Vec3f startPoint = eyePosition.multiplyMV(modelViewMatrixI, 1);
                Vec3f endPoint = new Vec3f(
                        (lastX / getWidth() * 2 - 1) * TAN_22_5 * aspect,
                        (1 - lastY / getHeight() * 2) * TAN_22_5,
                        -NEAR + eyePosition.getComponent(2))
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
                        if (!is_ffd) {
                            bsplineBody.save();
                            is_ffd = true;
                        }
                        direction = direction.multiplyMV(modelViewMatrixI, 0);
                        final Vec3f selectParameter = selectPointController.getSelectParameter();
                        bsplineBody.directFFDMultiPoint(selectParameter, direction.div(500), dup);
                        deformationController.notifyControlPointChange();
                    }
                } else {
                    float deltaX = (event.getX() - lastX);
                    float deltaY = (event.getY() - lastY);
                    if (deltaX == 0 && deltaY == 0) {
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
                is_ffd = false;
                selectPointController.reset();
                bsplineBody.saveControlPoints();
                break;

        }
        return true;
    }

    public void restore() {
        bsplineBody.restore();
        deformationController.notifyControlPointChange();
        requestRender();
    }

    private float[] getModelViewMatrixI() {
        float[] model_and_view_matrix_I = new float[16];
        Matrix.multiplyMM(model_and_view_matrix_I, 0, mViewMatrix, 0, drawProgram.getModelMatrix(), 0);
        Matrix.invertM(model_and_view_matrix_I, 0, model_and_view_matrix_I, 0);
        return model_and_view_matrix_I;
    }

    public void setDup(int dup) {
        this.dup = dup;
    }

    public void setControlPointNumber(int number) {
        bsplineBody = new BSplineBody(obj.getLength(), new Vec3i(number));

        final ByteBuffer bsplineBodyInfo = bsplineBody.getInfo();
        bsplineBodyInfoBuffer.postUpdate(bsplineBodyInfo, bsplineBodyInfo.limit());
    }
}
