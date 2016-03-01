package ac.affd_android.app.GL;

import android.util.Log;

import static android.opengl.GLES31.*;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 2/26/16.
 */
public class ACGLBuffer {
    protected int bindingPoint = -1;
    private final static String TAG = "ACGLBuffer";
    protected final int bufferId;
    protected Buffer data;
    protected int length;
    protected boolean dirty = false;
    public int dataType;
    public static final int INT = 0;
    public static final int FLOAT = 1;
    public static final int BYTE = 2;
    public int bufferType;

    protected ACGLBuffer(int bufferId) {
        this.bufferId = bufferId;
    }

    public void postUpdate(Buffer buffer, int length, int dataType){
        if (bindingPoint == -1) {
            Log.e(TAG, "specific bindingPoint first");
            return;
        }
        this.data = buffer;
        this.length = length;
        this.dataType = dataType;
        dirty = true;
    }

    public void glSetBindingPoint(int bindingPoint) {
        this.bindingPoint = bindingPoint;
        glBindBufferBase(bufferType, bindingPoint, bufferId);
    }

    public void glAsyncWithGPU(){
        if (dirty) {
            glBindBuffer(bufferType, bufferId);
            glBufferData(bufferType, length, data, GL_DYNAMIC_DRAW);
            glBindBuffer(bufferType, 0);
            dirty = false;
        }
    }

    static List<Integer> preGenBuffer = new ArrayList<>();
    static int preGenBufferNumber = 0;
    static int bufferNumber = 0;

    static public ACGLBuffer glGenBuffer(int target) {
        if (bufferNumber == preGenBufferNumber) {
            int[] temp = new int[8];
            glGenBuffers(8, temp, 0);
            for (int i : temp) {
                preGenBuffer.add(i);
            }
            preGenBufferNumber += 8;
        }
        switch (target){
            case GL_SHADER_STORAGE_BUFFER:
                return new ACSSBO(preGenBuffer.get(bufferNumber++));
            case GL_ATOMIC_COUNTER_BUFFER:
                return new ACACBO(preGenBuffer.get(bufferNumber++));
            default:
                Log.e(TAG, "this target is not implement");
                return null;
        }
//        return new ACGLBuffer(preGenBuffer.get(bufferNumber++));
    }

    public String glToString() {
        return "not implement";
    }

}
