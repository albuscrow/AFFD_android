package ac.affd_android.affdview.GL.GLOBJ;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static android.opengl.GLES31.*;

/**
 * Created by ac on 3/1/16.
 * todo some describe
 */
public class ACACBO extends ac.affd_android.affdview.GL.GLOBJ.ACGLBuffer {
    private String TAG = "ACACBO";

    ACACBO(int bufferId) {
        super(bufferId);
        this.bufferType = GL_ATOMIC_COUNTER_BUFFER;
    }

    public int getValue(Integer index) {
        glBindBuffer(bufferType, bufferId);
        ByteBuffer byteBuffer = ((ByteBuffer) glMapBufferRange(bufferType, 0, length, GL_MAP_READ_BIT)).order(ByteOrder.nativeOrder());
        IntBuffer buffer = byteBuffer.asIntBuffer();
        int res = buffer.get(index);
        glUnmapBuffer(bufferType);
        return res;
    }

    public String glToString(int dataType) {
        glBindBuffer(bufferType, bufferId);
        ByteBuffer byteBuffer = ((ByteBuffer) glMapBufferRange(bufferType, 0, length, GL_MAP_READ_BIT)).order(ByteOrder.nativeOrder());
        String res = "";
        if (dataType == INT) {
            IntBuffer buffer = byteBuffer.asIntBuffer();
            for (int i = 0; i < buffer.capacity(); ++i) {
                res += buffer.get(i) + " ";
            }
        } else {
            res += "not implement";
        }
        glUnmapBuffer(bufferType);
        return res;
    }
}
