package ac.affd_android.app.GL.GLOBJ;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES31.*;

public class ACSSBO extends ACGLBuffer {
    private final static String TAG = "ACSSBO";

    protected ACSSBO(Integer bufferId) {
        super(bufferId);
        this.bufferType = GL_SHADER_STORAGE_BUFFER;
    }

    public String glToString() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, bufferId);
        ByteBuffer byteBuffer = ((ByteBuffer) glMapBufferRange(GL_SHADER_STORAGE_BUFFER, 0, length, GL_MAP_READ_BIT)).order(ByteOrder.nativeOrder());
        String res = "";
        if (dataType == INT) {
            IntBuffer buffer = byteBuffer.asIntBuffer();
            for (int i = 0; i < buffer.capacity(); ++i) {
                res += buffer.get(i) + " ";
                if (i % 4 == 3) {
                    res += "\n";
                }
            }
        } else if (dataType == FLOAT) {
            FloatBuffer buffer = byteBuffer.asFloatBuffer();
            for (int i = 0; i < buffer.capacity(); ++i) {
                res += buffer.get(i) + " ";
                if (i % 4 == 3) {
                    res += "\n";
                }
            }
        } else {
            res += "not implement";
        }

        glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
        return res;
    }
}
