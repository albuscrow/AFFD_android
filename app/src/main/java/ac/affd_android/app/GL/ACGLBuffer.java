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
    private final static String TAG = "ACGLBuffer";
    protected final int bufferId;
    protected Buffer buffer;
    protected int length;
    protected boolean dirty = false;

    protected ACGLBuffer(int bufferId) {
        this.bufferId = bufferId;
    }

    public void postUpdate(Buffer ib){
        this.buffer = ib;
        this.length = ib.limit();
    }

    public void glSetBindingPoint(int bindingPoint) {}

    public void glAsyncWithGPU(){}

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
            default:
                Log.e(TAG, "this target is not implement");
                return null;
        }
//        return new ACGLBuffer(preGenBuffer.get(bufferNumber++));
    }

    public String glToString() {
        return null;
    }
}
