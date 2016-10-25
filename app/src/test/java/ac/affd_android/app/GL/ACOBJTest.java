package ac.affd_android.app.GL;

import android.app.Activity;
import android.content.Context;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
/**
 * Created by ac on 2/26/16.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ACOBJTest {
    private static final String FAKE_STRING = "HELLO WORLD";
    @Mock
    Context c = new Activity();

    @Test
    public void testTest() {
        Assert.assertThat(true, is(true));
    }

    @Test
    public void testTest2() {
        Assert.assertThat(true, is(true));
    }

    @Test
    public void testTest3() {
        Assert.assertThat(true, is(true));
    }

    @Test
    public void testTest4() {
        when(c.getString(android.R.string.cancel)).thenReturn(FAKE_STRING);
        Assert.assertThat(c.getString(android.R.string.cancel), is(FAKE_STRING));
    }
}
