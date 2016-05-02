package ac.affd_android.app.GL.GLProgram;


import ac.affd_android.app.GL.GLOBJ.ACGLBuffer;
import android.content.Context;
import android.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

import static android.opengl.GLES31.*;
import static android.opengl.GLU.gluErrorString;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;

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
    private int triangleNumber;

    {
        setIdentityM(modelMatrix, 0);
    }

    private int vaoId;

    private ACGLBuffer pointBuffer;
    private ACGLBuffer indexBuffer;

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
        glEnableVertexAttribArray(attr1Location);
        glEnableVertexAttribArray(attr2Location);
        glBindBuffer(GL_ARRAY_BUFFER, pointBuffer.bufferId);
//        glBufferData(GL_ARRAY_BUFFER, pointBuffer.length, pointBuffer.data, GL_STATIC_DRAW);
        glVertexAttribPointer(attr1Location, 4, GL_FLOAT, false, 32, 0);
        glVertexAttribPointer(attr2Location, 4, GL_FLOAT, false, 32, 16);
//        glVertexAttribPointer(3, 3, GL_FLOAT, true, 24, 24);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.bufferId);
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.length, indexBuffer.data, GL_STATIC_DRAW);

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
//        glDrawElements(GL_TRIANGLES, indexBuffer.length / 4, GL_UNSIGNED_INT, 0);
        glDrawElements(GL_TRIANGLES, triangleNumber * 3, GL_UNSIGNED_INT, 0);
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

    public void setData(ACGLBuffer outputPointBuffer, ACGLBuffer outputTriangleBuffer) {
        this.pointBuffer = outputPointBuffer;
        this.indexBuffer = outputTriangleBuffer;
    }

    public void setTriangleNumber(int triangleNumber) {
        this.triangleNumber = triangleNumber;
    }
}
