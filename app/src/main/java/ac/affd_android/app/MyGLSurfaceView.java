package ac.affd_android.app;

import android.content.Context;
import static android.opengl.GLES31.*;
import static android.opengl.GLU.*;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;
import org.apache.commons.io.IOUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.nio.Buffer;
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
    private int program;
    private int buffer;

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
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        String version = glGetString(
                GL10.GL_VERSION);
        Log.w(TAG, "GLES Version: " + version);

        //init computer shader
        int shader = glCreateShader(GL_COMPUTE_SHADER);
        try {
            String source = IOUtils.toString(getContext().getAssets().open("test_compute_shader.glsl"));
            glShaderSource(shader, source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        glCompileShader(shader);

        int[] result = new int[]{10};
        glGetShaderiv(shader, GL_COMPILE_STATUS, result, 0);
        if (result[0] == GL_FALSE) {
            String msg = glGetShaderInfoLog(shader);
            Log.e(TAG, "compile error: " + msg);
        }

        program = glCreateProgram();
        glAttachShader(program, shader);
        glLinkProgram(program);
        result[0] = 10;
        glGetProgramiv(program, GL_LINK_STATUS, result, 0);
        if (result[0] == GL_FALSE) {
            String msg = glGetProgramInfoLog(program);
            Log.e(TAG, "link error: " + msg);
        }

        //init buffer
        int[] buffers = new int[1];
        glGenBuffers(1, buffers, 0);
        buffer = buffers[0];
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, buffer);
        IntBuffer ib = ByteBuffer.allocateDirect(32 * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        for (int i = 0; i < 32; ++i) {
            ib.put(1);
        }
        ib.flip();
        glBufferData(GL_SHADER_STORAGE_BUFFER, 32 * 4, ib, GL_DYNAMIC_COPY);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, buffer);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
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

        glClear(GL_COLOR_BUFFER_BIT);
        glUseProgram(program);
        glDispatchCompute(1, 1, 1);

        //output
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, buffer);
        IntBuffer buffer = ((ByteBuffer) glMapBufferRange(GL_SHADER_STORAGE_BUFFER, 0, 32 * 4, GL_MAP_READ_BIT)).order(ByteOrder.nativeOrder()).asIntBuffer();
        for (int i = 0; i < 32; ++i) {
            System.out.println(buffer.get(i));
        }

        glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
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
