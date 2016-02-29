package ac.affd_android.app.Util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by ac on 2/29/16.
 */
public class ByteUtil {

    private static final int FLOAT_BYTE_SIZE = 4;

    static public byte[] floatArrayToByteArray(float[] floatArray) {
        ByteBuffer fb = ByteBuffer.allocate(floatArray.length * FLOAT_BYTE_SIZE).order(ByteOrder.nativeOrder());
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
}
