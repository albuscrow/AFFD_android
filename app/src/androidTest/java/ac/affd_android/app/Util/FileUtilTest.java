package ac.affd_android.app.Util;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.ByteBuffer;

/**
 * Created by ac on 10/31/16.
 * todo some describe
 */

@RunWith(AndroidJUnit4.class)
public class FileUtilTest {
    @Test
    public void testSaveLoad() {
        final byte[] array = {1, 2, 3, 4, 5};
        FileUtil.save("test", ByteBuffer.wrap(array), 5);
        ByteBuffer bb = FileUtil.load("test");

        Assert.assertNotNull(bb);
        final byte[] array1 = bb.array();
        Assert.assertArrayEquals(array, array1);
    }
}
