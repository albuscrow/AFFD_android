package ac.affd_android.app.GL;


import android.content.Context;
import static android.opengl.GLU.*;

import static android.opengl.Matrix.*;

import android.opengl.Matrix;
import android.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.*;

import static android.opengl.GLES31.*;

/**
 * Created by ac on 2/28/16.
 */
public class ACDrawProgram extends ACProgram{
    private static final String TAG = "ACDrawProgram";
    private ACShader fragmentShader;
    private ACShader vertexShader;

//    private int mvpMatrixLocation;
//    private int mvMatrixLocation;
//    private int verticeLocation;
//    private int normalLocation;

    private float[] projectionMatrix;
    private float[] viewMatrix;
    private float[] modelMatrix = new float[16];
    {
        setIdentityM(modelMatrix, 0);
    }
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

        glEnableVertexAttribArray(2);
//        glEnableVertexAttribArray(normalLocation);
        glBindBuffer(GL_ARRAY_BUFFER, bufferIdArray[0]);
        float[] attrData = new float[] {0,0,0f,1,  10,0f,0f,1, 0f,10,0f,1};
//        float[] attrData = new float[] {0,0,0, 0,0,1, 0,1,0, 0,0,1, 1,0,0, 0,0,1};
        FloatBuffer floatBuffer = ByteBuffer.allocateDirect(attrData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer.put(attrData);
        floatBuffer.flip();
        glBufferData(GL_ARRAY_BUFFER, attrData.length * 4, floatBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 4, GL_FLOAT, false, 0, 0);
//        glVertexAttribPointer(3, 3, GL_FLOAT, true, 24, 24);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferIdArray[1]);
        short[] indexData = new short[] {0,1,2, 0, 2, 1};
        ShortBuffer shortBuffer = ByteBuffer.allocateDirect(indexData.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        shortBuffer.put(indexData);
        shortBuffer.flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData.length * 2, shortBuffer, GL_STATIC_DRAW);
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

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
        glDisable(GL_CULL_FACE);
        glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_SHORT, 0);
        glBindVertexArray(0);
    }

    private void updateData() {
        float[] MVPMatrix = new float[16];
        float[] MVMatrix = new float[16];
        multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(0, 1, false, MVPMatrix, 0);
//        glUniformMatrix4fv(1, 1, false, MVPMatrix, 0);
    }
}
