package ac.affd_android.app;

import ac.affd_android.app.model.ACModelParse;
import ac.affd_android.app.model.InputType;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

/**
 * Created by ac on 2/27/16.
 *
 */
@RunWith(AndroidJUnit4.class)
public class ACOBJTest {
    //private static final String TAG = "ACOBJTest";
    @Test
    public void testObjRead2() {
        Context c = InstrumentationRegistry.getContext();
        Assert.assertNotNull(c);
        InputStream inputStream;
        try {
            inputStream = c.getAssets().open("test2.obj");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Assert.assertNotNull(inputStream);

        ACModelParse obj;
        try {
            obj = new ACModelParse(inputStream, null, InputType.OBJ);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        FloatBuffer fb = FloatBuffer.allocate(44);
        fb.put(new float[]{
                //point 1
                -0.5f, 0.5f, 0.5f, 1f,
                1f, 2f, 3f, 1f,
                0,1,
                //point 2
                0.5f, 0.5f, 0.5f, 1f,
                2f, 3f, 4f, 1f,
                1,1,
                //point 3
                0.5f, -0.5f, -0.5f, 1f,
                3f, 4f, 5f, 0f,
                1,0,
                //point 4
                -0.5f, -0.5f, -0.5f, 0f,
                4f, 5f, 6f, 0f,
                0,0,
                //triangle 1
                0, 1, 2, 5, -1, -1,
                //triangle2
                0, 2, 3, -1, 0, -1});
        fb.flip();

        final FloatBuffer dataForComputeShader = obj.getDataForComputeShader().asFloatBuffer();
        for (int i = 0; i < 44; ++i) {
//            System.out.println("" + fb.get(i) + " " + dataForComputeShader.get(i));
            Assert.assertEquals(fb.get(i), dataForComputeShader.get(i), 0.000001f);
        }
    }
}
