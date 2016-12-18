package ac.affd_android.affdview.GL.GLOBJ;

import java.nio.*;

import static android.opengl.GLES31.*;

public class ACSSBO extends ACGLBuffer {
    private final static String TAG = "ACSSBO";

    ACSSBO(Integer bufferId) {
        super(bufferId);
        this.bufferType = GL_SHADER_STORAGE_BUFFER;
    }

    @Override
    public ByteBuffer getData() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, bufferId);
        ByteBuffer res = ((ByteBuffer) glMapBufferRange(GL_SHADER_STORAGE_BUFFER, 0, length, GL_MAP_READ_BIT)).order(ByteOrder.nativeOrder());
        glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
        return res;
    }

    public interface ModifySSBO{
        void modify(ByteBuffer bb);
    }

    public void modify(ModifySSBO modifySSBO) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, bufferId);
        ByteBuffer res = ((ByteBuffer) glMapBufferRange(GL_SHADER_STORAGE_BUFFER, 0, length, GL_MAP_WRITE_BIT)).order(ByteOrder.nativeOrder());
        modifySSBO.modify(res);
        glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
    }

    public String glToString(int dataType) {
        ByteBuffer byteBuffer = getData();
        StringBuilder res = new StringBuilder();
        if (dataType == INT) {
            IntBuffer buffer = byteBuffer.asIntBuffer();
            for (int i = 0; i < buffer.capacity(); ++i) {
                res.append(buffer.get()).append(" ");
                if (i % 4 == 3) {
                    res.append("\n");
                }
            }
        } else if (dataType == FLOAT) {
            FloatBuffer buffer = byteBuffer.asFloatBuffer();
            for (int i = 0; i < buffer.capacity(); ++i) {
                res.append(buffer.get()).append(" ");
                if (i % 4 == 3) {
                    res.append("\n");
                }
            }
        } else {
            res.append("not implement");
        }

        return res.toString();
    }
}
