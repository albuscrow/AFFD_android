package ac.affd_android.app.GL;

import android.util.Log;

import static android.opengl.GLES31.*;

import java.nio.*;

public class ACSSBO extends ACGLBuffer {
    private final static String TAG = "ACSSBO";
    private int bindingPoint = -1;

    protected ACSSBO(Integer bufferId) {
        super(bufferId);
    }

    public void glSetBindingPoint(int bindingPoint) {
        this.bindingPoint = bindingPoint;
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, bindingPoint, bufferId);
    }

    public void postUpdate(Buffer buffer, int length, int dataType) {
        if (bindingPoint == -1) {
            Log.e(TAG, "specific bindingPoint first");
            return;
        }
        this.buffer = buffer;
        this.length = length;
        this.dataType = dataType;
        dirty = true;
    }


    @Override
    public void glAsyncWithGPU() {
        if (dirty) {
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, bufferId);
            glBufferData(GL_SHADER_STORAGE_BUFFER, length, buffer, GL_DYNAMIC_COPY);
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
            dirty = false;
        }
    }

    public String glToString() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, bufferId);
        ByteBuffer byteBuffer = ((ByteBuffer) glMapBufferRange(GL_SHADER_STORAGE_BUFFER, 0, length, GL_MAP_READ_BIT)).order(ByteOrder.nativeOrder());
        String res = "";
        if (dataType == INT) {
            IntBuffer buffer = byteBuffer.asIntBuffer();
            for (int i = 0; i < buffer.capacity(); ++i) {
                res += buffer.get(i) + " ";
            }
        } else if (dataType == FLOAT) {
            FloatBuffer buffer = byteBuffer.asFloatBuffer();
            for (int i = 0; i < buffer.capacity(); ++i) {
                res += buffer.get(i) + " ";
            }
        } else {
            res += "not implement";
        }

        glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
        return res;
    }
}
