package ac.affd_android.app.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by ac on 5/4/16.
 * todo some describe
 */

@RunWith(MockitoJUnitRunner.class)
public class BSplineBodyTest {
    BSplineBody bSplineBody;

    @Before
    public void testInitData() {
        bSplineBody = new BSplineBody(new Vec3(0.5f, 0.6f, 0.7f));

    }

    @Test
    public void testGetControlPointForFastCompute() {
        Float[] cp = new Float[]{-0.25f, -0.3f, -0.35f, -0.25f, -0.3f, -0.233333f, -0.25f, -0.3f, 0.0f, -0.25f, -0.3f, 0.233333f, -0.25f, -0.3f, 0.35f, -0.25f, -0.2f, -0.35f, -0.25f, -0.2f, -0.233333f, -0.25f, -0.2f, 0.0f, -0.25f, -0.2f, 0.233333f, -0.25f, -0.2f, 0.35f, -0.25f, 0.0f, -0.35f, -0.25f, 0.0f, -0.233333f, -0.25f, 0.0f, 0.0f, -0.25f, 0.0f, 0.233333f, -0.25f, 0.0f, 0.35f, -0.25f, 0.2f, -0.35f, -0.25f, 0.2f, -0.233333f, -0.25f, 0.2f, 0.0f, -0.25f, 0.2f, 0.233333f, -0.25f, 0.2f, 0.35f, -0.25f, 0.3f, -0.35f, -0.25f, 0.3f, -0.233333f, -0.25f, 0.3f, 0.0f, -0.25f, 0.3f, 0.233333f, -0.25f, 0.3f, 0.35f, -0.166667f, -0.3f, -0.35f, -0.166667f, -0.3f, -0.233333f, -0.166667f, -0.3f, 0.0f, -0.166667f, -0.3f, 0.233333f, -0.166667f, -0.3f, 0.35f, -0.166667f, -0.2f, -0.35f, -0.166667f, -0.2f, -0.233333f, -0.166667f, -0.2f, 0.0f, -0.166667f, -0.2f, 0.233333f, -0.166667f, -0.2f, 0.35f, -0.166667f, 0.0f, -0.35f, -0.166667f, 0.0f, -0.233333f, -0.166667f, 0.0f, 0.0f, -0.166667f, 0.0f, 0.233333f, -0.166667f, 0.0f, 0.35f, -0.166667f, 0.2f, -0.35f, -0.166667f, 0.2f, -0.233333f, -0.166667f, 0.2f, 0.0f, -0.166667f, 0.2f, 0.233333f, -0.166667f, 0.2f, 0.35f, -0.166667f, 0.3f, -0.35f, -0.166667f, 0.3f, -0.233333f, -0.166667f, 0.3f, 0.0f, -0.166667f, 0.3f, 0.233333f, -0.166667f, 0.3f, 0.35f, 0.0f, -0.3f, -0.35f, 0.0f, -0.3f, -0.233333f, 0.0f, -0.3f, 0.0f, 0.0f, -0.3f, 0.233333f, 0.0f, -0.3f, 0.35f, 0.0f, -0.2f, -0.35f, 0.0f, -0.2f, -0.233333f, 0.0f, -0.2f, 0.0f, 0.0f, -0.2f, 0.233333f, 0.0f, -0.2f, 0.35f, 0.0f, 0.0f, -0.35f, 0.0f, 0.0f, -0.233333f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.35f, 0.0f, 0.2f, -0.35f, 0.0f, 0.2f, -0.233333f, 0.0f, 0.2f, 0.0f, 0.0f, 0.2f, 0.233333f, 0.0f, 0.2f, 0.35f, 0.0f, 0.3f, -0.35f, 0.0f, 0.3f, -0.233333f, 0.0f, 0.3f, 0.0f, 0.0f, 0.3f, 0.233333f, 0.0f, 0.3f, 0.35f, 0.166667f, -0.3f, -0.35f, 0.166667f, -0.3f, -0.233333f, 0.166667f, -0.3f, 0.0f, 0.166667f, -0.3f, 0.233333f, 0.166667f, -0.3f, 0.35f, 0.166667f, -0.2f, -0.35f, 0.166667f, -0.2f, -0.233333f, 0.166667f, -0.2f, 0.0f, 0.166667f, -0.2f, 0.233333f, 0.166667f, -0.2f, 0.35f, 0.166667f, 0.0f, -0.35f, 0.166667f, 0.0f, -0.233333f, 0.166667f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.233333f, 0.166667f, 0.0f, 0.35f, 0.166667f, 0.2f, -0.35f, 0.166667f, 0.2f, -0.233333f, 0.166667f, 0.2f, 0.0f, 0.166667f, 0.2f, 0.233333f, 0.166667f, 0.2f, 0.35f, 0.166667f, 0.3f, -0.35f, 0.166667f, 0.3f, -0.233333f, 0.166667f, 0.3f, 0.0f, 0.166667f, 0.3f, 0.233333f, 0.166667f, 0.3f, 0.35f, 0.25f, -0.3f, -0.35f, 0.25f, -0.3f, -0.233333f, 0.25f, -0.3f, 0.0f, 0.25f, -0.3f, 0.233333f, 0.25f, -0.3f, 0.35f, 0.25f, -0.2f, -0.35f, 0.25f, -0.2f, -0.233333f, 0.25f, -0.2f, 0.0f, 0.25f, -0.2f, 0.233333f, 0.25f, -0.2f, 0.35f, 0.25f, 0.0f, -0.35f, 0.25f, 0.0f, -0.233333f, 0.25f, 0.0f, 0.0f, 0.25f, 0.0f, 0.233333f, 0.25f, 0.0f, 0.35f, 0.25f, 0.2f, -0.35f, 0.25f, 0.2f, -0.233333f, 0.25f, 0.2f, 0.0f, 0.25f, 0.2f, 0.233333f, 0.25f, 0.2f, 0.35f, 0.25f, 0.3f, -0.35f, 0.25f, 0.3f, -0.233333f, 0.25f, 0.3f, 0.0f, 0.25f, 0.3f, 0.233333f, 0.25f, 0.3f, 0.35f};
        assert bSplineBody.getControllerPoint().data != null;
        Assert.assertEquals(cp.length, bSplineBody.getControllerPoint().data.length);
        for (int i = 0; i < cp.length; i++) {
            Assert.assertTrue(Math.abs(cp[i] - bSplineBody.getControllerPoint().data[i]) < 0.000001);
        }
//        Assert.assertArrayEquals(bSplineBody.getControllerPoint().data, cp);
        Float[] res = new Float[]{-0.25f, -0.3f, -0.35f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.25f, -0.3f, -0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.25f, -0.3f, 0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.25f, -0.1f, -0.35f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.25f, -0.1f, -0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.25f, -0.1f, 0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.25f, 0.1f, -0.35f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.25f, 0.1f, -0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.25f, 0.1f, 0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.0833333f, -0.3f, -0.35f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.0833333f, -0.3f, -0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.0833333f, -0.3f, 0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.0833333f, -0.1f, -0.35f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.0833333f, -0.1f, -0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.0833333f, -0.1f, 0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.0833333f, 0.1f, -0.35f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.0833333f, 0.1f, -0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.0833333f, 0.1f, 0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0833333f, -0.3f, -0.35f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0833333f, -0.3f, -0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0833333f, -0.3f, 0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0833333f, -0.1f, -0.35f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0833333f, -0.1f, -0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0833333f, -0.1f, 0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0833333f, 0.1f, -0.35f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0833333f, 0.1f, -0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0833333f, 0.1f, 0.116667f, 0.0f, 0.0f, 0.0f, 0.233333f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.166667f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -7.45058e-09f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};

        ACMatrix m = bSplineBody.getControllerPointForSpeedUp();
        Assert.assertArrayEquals(m.shape, new Integer[]{3, 3, 3, 3, 3, 3, 4});
        Assert.assertArrayEquals(m.data, res);
//        System.out.println(Arrays.toString(m.data));

    }

    @Test
    public void testAuxMatrix() {
        Float[] f1 = new Float[]{1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f
        };
        Float[] f2 = new Float[]{1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 1.0f, 0.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, -1.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.0f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f, 0.5f, 0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, -1.5f, 1.0f
        };
        System.out.println(f1.length);
        System.out.println(f2.length);
        Assert.assertEquals(f1.length, f2.length);
        Assert.assertArrayEquals(f1, f2);
    }

}