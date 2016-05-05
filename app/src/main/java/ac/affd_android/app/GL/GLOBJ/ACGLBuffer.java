package ac.affd_android.app.GL.GLOBJ;

import android.util.Log;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES31.*;

/**
 * Created by ac on 2/26/16.
 * todo some describe
 */
public class ACGLBuffer {
    protected int bindingPoint = -1;
    private final static String TAG = "ACGLBuffer";
    public final int bufferId;
    protected Buffer data;
    protected int length;
    protected boolean dirty = false;
    public int dataType;
    public static final int INT = 0;
    public static final int FLOAT = 1;
    public static final int BYTE = 2;
    public static final int MIX = 3;
    public int bufferType;

    protected ACGLBuffer(int bufferId) {
        this.bufferId = bufferId;
    }

    public ACGLBuffer postUpdate(Buffer buffer, int length, int dataType){
        if (bindingPoint == -1) {
            Log.e(TAG, "specific bindingPoint first");
            throw new RuntimeException();
        }
        this.data = buffer;
        this.length = length;
        this.dataType = dataType;
        dirty = true;
        return this;
    }

    public ACGLBuffer glSetBindingPoint(int bindingPoint) {
        this.bindingPoint = bindingPoint;
        glBindBufferBase(bufferType, bindingPoint, bufferId);
        return this;
    }

    public ACGLBuffer glAsyncWithGPU(){
        if (dirty) {
            glBindBuffer(bufferType, bufferId);
            glBufferData(bufferType, length, data, GL_DYNAMIC_DRAW);
            glBindBuffer(bufferType, 0);
            dirty = false;
        }
        return this;
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
                throw new RuntimeException();
        }
    }

    public String glToString() {
        return "not implement";
    }
}
