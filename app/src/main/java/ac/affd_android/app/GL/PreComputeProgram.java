package ac.affd_android.app.GL;

import android.content.Context;
import static android.opengl.GLES31.*;

import android.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by ac on 2/29/16.
 */
public class PreComputeProgram extends ACProgram {
    private static final String TAG = "ComputeProgram";
    private final ACOBJ obj;

    public PreComputeProgram(ACOBJ obj) {
        super();
        this.obj = obj;
    }

    public void glOnSurfaceCreated(Context c) {
        String source;
        try {
            source = IOUtils.toString(c.getAssets().open("pre_computer.glsl"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        source = preCompile(source, obj);
        Log.d(TAG, source);
        ACProgram.ACShader shader = new ACProgram.ACShader(source, GL_COMPUTE_SHADER);
        addShader(shader);
        super.glCompileAndLink();


        //init location
//        mvpMatrixLocation = glGetUniformLocation(id, "wvpMatrix");
//        mvMatrixLocation = glGetUniformLocation(id, "wvMatrix");
//        verticeLocation = glGetAttribLocation(id, "vertice");
//        normalLocation = glGetAttribLocation(id, "normal");

        //gen and bind vao
//        int[] vaoIdArray = new int[1];
//        glGenVertexArrays(1, vaoIdArray, 0);
//        this.vaoId = vaoIdArray[0];
//        glBindVertexArray(vaoId);
//
//        //gen attr and index buffer
//        int[] bufferIdArray = new int[2];
//        glGenBuffers(2, bufferIdArray, 0);
//
//        glEnableVertexAttribArray(attr1Location);
//        glEnableVertexAttribArray(attr2Location);
//        glBindBuffer(GL_ARRAY_BUFFER, bufferIdArray[0]);
////        float[] attrData = new float[] {0,0,0f,1, 0,0,1,0,  1,0f,0f,1, 1,0,0,0, 0f,1,0f,1, 0,1,0,0,};
//////        float[] attrData = new float[] {0,0,0, 0,0,1, 0,1,0, 0,0,1, 1,0,0, 0,0,1};
////        FloatBuffer floatBuffer = ByteBuffer.allocateDirect(attrData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
////        floatBuffer.put(attrData);
////        floatBuffer.flip();
//        InputStream inputStream;
//        try {
//            inputStream = c.getAssets().open("bishop.obj");
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        ACOBJ.Point.init();
//        ACOBJ obj;
//        try {
//            obj = new ACOBJ(inputStream, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//        FloatBuffer fb = obj.getPointsByteArray();
//
//        glBufferData(GL_ARRAY_BUFFER, fb.limit() * 4, fb, GL_STATIC_DRAW);
//        glVertexAttribPointer(attr1Location, 4, GL_FLOAT, false, 32, 0);
//        glVertexAttribPointer(attr2Location, 4, GL_FLOAT, false, 32, 16);
////        glVertexAttribPointer(3, 3, GL_FLOAT, true, 24, 24);
//        glBindBuffer(GL_ARRAY_BUFFER, 0);
//
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferIdArray[1]);
////        short[] indexData = new short[] {0,1,2};
////        ShortBuffer shortBuffer = ByteBuffer.allocateDirect(indexData.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
////        shortBuffer.put(indexData);
////        shortBuffer.flip();
//        IntBuffer index = obj.getIndex();
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, index.limit() * 4, index, GL_STATIC_DRAW);
//        elementSize = index.limit();
//
//        glBindVertexArray(0);
//
//        //check error
//        Log.d(TAG, gluErrorString(glGetError()));
    }

    @Override
    public void glOnDrawFrame() {
        glUse();
        glDispatchCompute(1, 1, 1);
    }

    private String preCompile(String source, ACOBJ obj) {
        int pointNumber = obj.getPointNumber();
        int triangleNumber = obj.getTriangleNumber();

        return source.replace("Point[", "Point[" + pointNumber)
                .replace("Triangle[", "Triangle[" + triangleNumber);
    }

}
