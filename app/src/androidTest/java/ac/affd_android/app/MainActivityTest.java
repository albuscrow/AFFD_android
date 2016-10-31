package ac.affd_android.app;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

import android.support.test.runner.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
//    @Rule
//    public ActivityTestRule<MainActivity> ar = new ActivityTestRule<>(MainActivity.class);
    @Test
    public void test() {
        Assert.assertTrue(true);
    }
}