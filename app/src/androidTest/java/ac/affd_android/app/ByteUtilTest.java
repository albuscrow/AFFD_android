package ac.affd_android.app;

import ac.affd_android.app.GL.ACOBJ;
import ac.affd_android.app.Util.ByteUtil;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.util.Log;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.nio.*;

/**
 * Created by ac on 2/27/16.
 */
@RunWith(AndroidJUnit4.class)
public class ByteUtilTest {
    private static final String TAG = "ByteUtilTest";

    @Test
    public void testFloatToBuffer() {
        float[] floats = new float[]{123.345f};
        byte[] buffer = ByteUtil.floatArrayToByteArray(floats);
        Log.i(TAG, ByteUtil.byteArrayToString(buffer));
        Log.i(TAG, ByteOrder.nativeOrder().toString());
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
