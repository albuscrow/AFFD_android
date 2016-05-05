package ac.affd_android.app.Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

    static public ByteBuffer genDirctBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    static public ByteBuffer genBuffer(int capacity) {
        return ByteBuffer.allocate(capacity).order(ByteOrder.nativeOrder());
    }
}
