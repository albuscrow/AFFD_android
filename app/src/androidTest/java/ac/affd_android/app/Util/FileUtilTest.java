package ac.affd_android.app.Util;

import ac.affd_android.affdview.Util.FileUtil;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * Created by ac on 10/31/16.
 * todo some describe
 */

public class FileUtilTest {
    @Test
    public void testSaveLoad() {
        final byte[] array = {1, 2, 3, 4, 5};
        Context context = InstrumentationRegistry.getTargetContext();
        FileUtil.save(context, "test", ByteBuffer.wrap(array), 5);
        ByteBuffer bb = FileUtil.load(context, "test");

        Assert.assertNotNull(bb);
        final byte[] array1 = bb.array();
        Assert.assertArrayEquals(array, array1);
    }
}
