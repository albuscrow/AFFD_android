package ac.affd_android.affdview.Util;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by ac on 2/27/16.
 *
 */
public class ByteUtilTest {
    private static final String TAG = "ByteUtilTest";

    @Test
    public void testFloatToBuffer() {
        float[] floats = new float[]{123.345f};
        byte[] buffer = ByteUtil.floatArrayToByteArray(floats);
        float f = 123.345f;
        int fi = Float.floatToIntBits(f);
        byte[] bs = new byte[4];
        bs[0] = (byte) ((fi) & 0xff);
        bs[1] = (byte) ((fi >> 8) & 0xff);
        bs[2] = (byte) ((fi >> 16) & 0xff);
        bs[3] = (byte) ((fi >> 24) & 0xff);
        Assert.assertEquals(ByteUtil.byteArrayToString(bs), ByteUtil.byteArrayToString(buffer));
    }


}
