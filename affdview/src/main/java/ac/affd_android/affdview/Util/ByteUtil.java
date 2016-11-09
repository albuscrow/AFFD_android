package ac.affd_android.affdview.Util;

import ac.affd_android.affdview.model.ACMatrix;
import ac.affd_android.affdview.model.Vec3f;
import ac.affd_android.affdview.model.Vec3i;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by ac on 2/29/16.
 */
public class ByteUtil {

    public static final int FLOAT_BYTE_SIZE = 4;
    public static final int INT_BYTE_SIZE = 4;

    static public byte[] floatArrayToByteArray(float[] floatArray) {
        ByteBuffer fb = ByteUtil.genBuffer(floatArray.length * FLOAT_BYTE_SIZE);
        for (float f : floatArray) {
            fb.putFloat(f);
        }
        fb.flip();
        return fb.array();
    }

    static public String byteArrayToString(byte[] byteArray) {
        String res = "";
        for (byte b : byteArray) {
            res += b + " ";
        }
        return res.trim();
    }

    static public ByteBuffer genDirectBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    static public ByteBuffer genBuffer(int capacity) {
        return ByteBuffer.allocate(capacity).order(ByteOrder.nativeOrder());
    }

    static public ByteBuffer addToBuffer(ByteBuffer bb, Float data) {
        bb.putFloat(data);
        return bb;
    }
    static public ByteBuffer addToBuffer(ByteBuffer bb, Integer data) {
        bb.putInt(data);
        return bb;
    }


    static public ByteBuffer addToBuffer(ByteBuffer bb, Vec3i data, Integer[] padding) {
        for (int i = 0; i < 3; ++i) {
            bb.putInt(data.getComponent(i));
        }
        for (Integer i : padding) {
            if (i == null) {
                i = 0;
            }
            bb.putInt(i);
        }
        return bb;
    }

    static public ByteBuffer addToBuffer(ByteBuffer bb, Vec3i data, int paddingNumber) {
        return addToBuffer(bb, data, new Integer[paddingNumber]);
    }

    static public ByteBuffer addToBuffer(ByteBuffer bb, Vec3i data) {
        return addToBuffer(bb, data, new Integer[0]);
    }

    static public ByteBuffer addToBuffer(ByteBuffer bb, Vec3f data, Float[] padding) {
        for (int i = 0; i < 3; ++i) {
            bb.putFloat(data.getComponent(i));
        }
        for (Float f : padding) {
            if (f == null) {
                f = 0f;
            }
            bb.putFloat(f);
        }
        return bb;
    }

    static public ByteBuffer addToBuffer(ByteBuffer bb, Vec3f data, int paddingNumber) {
        return addToBuffer(bb, data, new Float[paddingNumber]);
    }

    static public ByteBuffer addToBuffer(ByteBuffer bb, Vec3f data) {
        return addToBuffer(bb, data, new Float[0]);
    }

    static public ByteBuffer addToBuffer(ByteBuffer bb, Vec3f[] data, int paddingNumber) {
        for (Vec3f v : data) {
            addToBuffer(bb, v, paddingNumber);
        }
        return bb;
    }

    static public ByteBuffer addToBuffer(ByteBuffer bb, Vec3i[] data, int paddingNumber) {
        for (Vec3i v : data) {
            addToBuffer(bb, v, paddingNumber);
        }
        return bb;
    }

    static public FloatBuffer ACMatrix2FloatBuffer(ACMatrix matrix) {
        FloatBuffer bb = genDirectBuffer(matrix.size() * FLOAT_BYTE_SIZE).asFloatBuffer();
        bb.put(matrix.data);
        bb.flip();
        return bb;
    }
}
