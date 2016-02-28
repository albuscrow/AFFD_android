package ac.affd_android.app;

import android.support.test.runner.AndroidJUnit4;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.core.Is.is;

/**
 * Created by ac on 2/27/16.
 */
@RunWith(AndroidJUnit4.class)
public class TestSomething {
    @Test
    public void test() {
        assertThat(true, is(true));
    }
}
