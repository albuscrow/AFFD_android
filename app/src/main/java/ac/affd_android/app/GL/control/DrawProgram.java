package ac.affd_android.app.GL.control;


import ac.affd_android.app.Constant;
import ac.affd_android.app.GL.GLOBJ.ACGLBuffer;
import ac.affd_android.app.GL.GLProgram.ACProgram;
import ac.affd_android.app.GL.GLProgram.ACShader;
import ac.affd_android.app.Util.GLUtil;
import ac.affd_android.app.model.GlobalInfoProvider;
import ac.affd_android.app.model.Vec2;
import android.content.Context;
import android.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

import static android.opengl.GLES31.*;
import static android.opengl.Matrix.*;

/**
 * Created by ac on 2/28/16.
 * todo some describe
 */
public class DrawProgram extends ACProgram {
    private static final String TAG = "ACDrawProgram";
    private final GlobalInfoProvider globalInfoProvider;
    private float[] modelMatrix = new float[16];

    {
        setIdentityM(modelMatrix, 0);
    }

    private int vaoId;

    public DrawProgram(GlobalInfoProvider globalInfoProvider) {
        this.globalInfoProvider = globalInfoProvider;
    }

    public void rotate(Vec2 v) {
        float[] sTemp = new float[16];
        //noinspection SuspiciousNameCombination
        setRotateM(sTemp, 0, 3, v.x, v.y, 0);
        multiplyMM(modelMatrix, 0, sTemp, 0, modelMatrix, 0);
    }

    void translate() {

    }

    public void glOnSurfaceCreated(Context c, ACGLBuffer pointBuffer, ACGLBuffer indexBuffer) {
        //init program
        initProgram(c);

        //gen and bind vao
        initVAO(pointBuffer, indexBuffer);

        //check error
        GLUtil.glCheckError(TAG);
    }

    private void initProgram(Context c) {
        initShader(c, "vertex.glsl", GL_VERTEX_SHADER);
        initShader(c, "fragment.glsl", GL_FRAGMENT_SHADER);
        super.glCompileAndLink();
    }

    private void initShader(Context c, String fileName, int type) {
        if (type != GL_VERTEX_SHADER && type != GL_FRAGMENT_SHADER) {
            Log.e(TAG, "shader's type is wrong");
            throw new RuntimeException();
        }
        String source;
        try {
            source = IOUtils.toString(c.getAssets().open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        ACShader shader = new ACShader(source, type);
        addShader(shader);
    }

    private void initVAO(ACGLBuffer pointBuffer, ACGLBuffer indexBuffer) {
        int[] vaoIdArray = new int[1];
        glGenVertexArrays(1, vaoIdArray, 0);
        this.vaoId = vaoIdArray[0];
        glBindVertexArray(vaoId);

        //gen attr and index buffer
        glEnableVertexAttribArray(Constant.ATTR1_LOCATION);
        glEnableVertexAttribArray(Constant.ATTR2_LOCATION);
        glBindBuffer(GL_ARRAY_BUFFER, pointBuffer.bufferId);
        glVertexAttribPointer(Constant.ATTR1_LOCATION, 4, GL_FLOAT, false, Constant.POINT_SIZE, Constant.POINT_SIZE_P3T1_OFFSET);
        glVertexAttribPointer(Constant.ATTR2_LOCATION, 4, GL_FLOAT, false, Constant.POINT_SIZE, Constant.POINT_SIZE_N3T1_OFFSET);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.bufferId);

        glBindVertexArray(0);
    }

    public void glOnDrawFrame(float[] mViewMatrix, float[] mProjectionMatrix) {
        glUse();
        glBindVertexArray(vaoId);
        updateData(mViewMatrix, mProjectionMatrix);
        glDrawElements(GL_TRIANGLES, globalInfoProvider.getRendererTriangleNumber() * 3, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    private void updateData(float[] mViewMatrix, float[] mProjectionMatrix) {
        //update matrix
        float[] MVPMatrix = new float[16];
        float[] MVMatrix = new float[16];
        multiplyMM(MVMatrix, 0, mViewMatrix, 0, modelMatrix, 0);
        multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(Constant.MV_MATRIX_LOCATION, 1, false, MVMatrix, 0);
        glUniformMatrix4fv(Constant.MVP_MATRIX_LOCATION, 1, false, MVPMatrix, 0);
    }

    public float[] getModelMatrix() {
        return modelMatrix;
    }
}
