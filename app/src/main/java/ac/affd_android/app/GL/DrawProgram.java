package ac.affd_android.app.GL;


import android.content.Context;
import static android.opengl.GLU.*;

import static android.opengl.Matrix.*;

import android.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.*;

import static android.opengl.GLES31.*;

/**
 * Created by ac on 2/28/16.
 */
public class DrawProgram extends ACProgram{
    private static final String TAG = "ACDrawProgram";
    private ACShader fragmentShader;
    private ACShader vertexShader;

    private final int mvMatrixLocation  = 0;
    private final int mvpMatrixLocation = 1;
    private final int attr1Location = 0;
    private final int attr2Location = 1;

    private float[] projectionMatrix;
    private float[] viewMatrix;
    private float[] modelMatrix = new float[16];
    {
        setIdentityM(modelMatrix, 0);
    }
    private int elementSize;
    private int vaoId;

    void rotate() {

    }

    void translate() {

    }

    public void glOnSurfaceCreated(Context c) {
        String vertexSource, fragmentSource;
        try {
            vertexSource = IOUtils.toString(c.getAssets().open("vertex.glsl"));
            fragmentSource = IOUtils.toString(c.getAssets().open("fragment.glsl"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        ACProgram.ACShader vertexShader = new ACProgram.ACShader(vertexSource, GL_VERTEX_SHADER);
        ACProgram.ACShader fragmentShader = new ACProgram.ACShader(fragmentSource, GL_FRAGMENT_SHADER);
        addShader(vertexShader);
        addShader(fragmentShader);
        super.glCompileAndLink();

        //init location
//        mvpMatrixLocation = glGetUniformLocation(id, "wvpMatrix");
//        mvMatrixLocation = glGetUniformLocation(id, "wvMatrix");
//        verticeLocation = glGetAttribLocation(id, "vertice");
//        normalLocation = glGetAttribLocation(id, "normal");

        //gen and bind vao
        int[] vaoIdArray = new int[1];
        glGenVertexArrays(1, vaoIdArray, 0);
        this.vaoId = vaoIdArray[0];
        glBindVertexArray(vaoId);

        //gen attr and index buffer
        int[] bufferIdArray = new int[2];
        glGenBuffers(2, bufferIdArray, 0);

        glEnableVertexAttribArray(attr1Location);
        glEnableVertexAttribArray(attr2Location);
        glBindBuffer(GL_ARRAY_BUFFER, bufferIdArray[0]);
//        float[] attrData = new float[] {0,0,0f,1, 0,0,1,0,  1,0f,0f,1, 1,0,0,0, 0f,1,0f,1, 0,1,0,0,};
////        float[] attrData = new float[] {0,0,0, 0,0,1, 0,1,0, 0,0,1, 1,0,0, 0,0,1};
//        FloatBuffer floatBuffer = ByteBuffer.allocateDirect(attrData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//        floatBuffer.put(attrData);
//        floatBuffer.flip();
        InputStream inputStream;
        try {
            inputStream = c.getAssets().open("bishop.obj");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ACOBJ obj;
        try {
            obj = new ACOBJ(inputStream, null);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        FloatBuffer fb = obj.getPointsByteArray();

        glBufferData(GL_ARRAY_BUFFER, fb.limit() * 4, fb, GL_STATIC_DRAW);
        glVertexAttribPointer(attr1Location, 4, GL_FLOAT, false, 32, 0);
        glVertexAttribPointer(attr2Location, 4, GL_FLOAT, false, 32, 16);
//        glVertexAttribPointer(3, 3, GL_FLOAT, true, 24, 24);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferIdArray[1]);
//        short[] indexData = new short[] {0,1,2};
//        ShortBuffer shortBuffer = ByteBuffer.allocateDirect(indexData.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
//        shortBuffer.put(indexData);
//        shortBuffer.flip();
        IntBuffer index = obj.getIndex();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, index.limit() * 4, index, GL_STATIC_DRAW);
        elementSize = index.limit();

        glBindVertexArray(0);

        //check error
        Log.d(TAG, gluErrorString(glGetError()));
    }

    @Override
    public void addShader(ACShader shader) {
        if (shader.getType() == GL_VERTEX_SHADER) {
            this.vertexShader = shader;
        } else if (shader.getType() == GL_FRAGMENT_SHADER) {
            this.fragmentShader = shader;
        } else {
            Log.e(TAG, "shader's type is wrong");
        }
        super.addShader(shader);
    }

    public void setProjectionMatrix(float[] projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    public void setViewMatrix(float[] viewMatrix) {
        this.viewMatrix = viewMatrix;
    }

    public void glOnDrawFrame() {
        glUse();
        glBindVertexArray(vaoId);
        updateData();
        glDrawElements(GL_TRIANGLES, elementSize, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    private void updateData() {
        //update matrix
        float[] MVPMatrix = new float[16];
        float[] MVMatrix = new float[16];
        multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(mvMatrixLocation, 1, false, MVMatrix, 0);
        glUniformMatrix4fv(mvpMatrixLocation, 1, false, MVPMatrix, 0);
//        glUniformMatrix4fv(1, 1, false, MVPMatrix, 0);
    }
}
