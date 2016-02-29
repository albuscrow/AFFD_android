package ac.affd_android.app;

import ac.affd_android.app.GL.ACOBJ;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.util.Log;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by ac on 2/27/16.
 */
@RunWith(AndroidJUnit4.class)
public class ACOBJTest extends InstrumentationTestCase{
    private static final String TAG = "ACOBJTest";
//    @Before
//    public void openFile() {
//        try {
//            super.setUp();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Instrumentation instrumentation = getInstrumentation();
//        injectInstrumentation(InstrumentationRegistry.getContext());
//        assertNotNull(instrumentation);
//        Context c = InstrumentationRegistry.getContext();
//        assertNotNull(c);
//        try {
//            this.inputStream = c.getAssets().open("test.obj");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertNotNull(inputStream);
//    }

    @Test
    public void testObjRead3() {
        Context c = InstrumentationRegistry.getContext();
        assertNotNull(c);
        InputStream inputStream;
        try {
            inputStream = c.getAssets().open("test3.obj");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        assertNotNull(inputStream);

        ACOBJ obj;
        ACOBJ.init();
        try {
            obj = new ACOBJ(inputStream, null);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
//        Log.d(TAG, floatBufferToString(obj.getVertices()));
        FloatBuffer fb = FloatBuffer.allocate(15);
        fb.put(-1.0f);
        fb.put(-1.0f);
        fb.put(-1.0f);
        fb.put(-1.0f/3.0f);
        fb.put(-1.0f/3.0f);
        fb.put(-1.0f/3.0f);
        fb.put(1.0f/3.0f);
        fb.put(1.0f/3.0f);
        fb.put(1.0f/3.0f);
        fb.put(-1.0f);
        fb.put(-1.0f);
        fb.put(-1.0f);
        fb.put(1.0f);
        fb.put(1.0f);
        fb.put(1.0f);
        fb.flip();
        assertEquals(fb, obj.getVertices());
//        assertEquals("1.0 2.0 3.0 2.0 3.0 4.0 3.0 4.0 5.0 1.0 2.0 3.0 4.0 5.0 6.0 ", floatBufferToString(obj.getVertices()));
//        assertEquals("0.0 1.0 1.0 1.0 1.0 0.0 1.0 1.0 0.0 0.0 ", floatBufferToString(obj.getTexcoord()));
//        assertEquals("1.0 2.0 3.0 2.0 3.0 4.0 3.0 4.0 5.0 3.0 4.0 5.0 4.0 5.0 6.0 ", floatBufferToString(obj.getNormal()));
//        assertEquals("0 1 2 3 2 4 ", intBufferToString(obj.getIndex()));
        assertEquals("5 -1 -1 -1 0 -1 ", intBufferToString(obj.getAdjTable()));
    }

    @Test
    public void testObjRead2() {
        Context c = InstrumentationRegistry.getContext();
        assertNotNull(c);
        InputStream inputStream;
        try {
            inputStream = c.getAssets().open("test2.obj");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        assertNotNull(inputStream);

        ACOBJ obj;
        ACOBJ.Point.init();
        try {
            obj = new ACOBJ(inputStream, null);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Log.d(TAG, floatBufferToString(obj.getVertices()));
        FloatBuffer fb = FloatBuffer.allocate(32);
        //point 1
        fb.put(-1.0f);
        fb.put(-1.0f);
        fb.put(-1.0f);
        fb.put(0f);

        fb.put(1f);
        fb.put(2f);
        fb.put(3f);
        fb.put(1f);


        //point 2
        fb.put(-1.0f/3.0f);
        fb.put(-1.0f/3.0f);
        fb.put(-1.0f/3.0f);
        fb.put(1f);

        fb.put(2f);
        fb.put(3f);
        fb.put(4f);
        fb.put(1f);


        //point 3
        fb.put(1.0f/3.0f);
        fb.put(1.0f/3.0f);
        fb.put(1.0f/3.0f);
        fb.put(1f);

        fb.put(3f);
        fb.put(4f);
        fb.put(5f);
        fb.put(0f);


        //point 4
        fb.put(1.0f);
        fb.put(1.0f);
        fb.put(1.0f);
        fb.put(0f);

        fb.put(4f);
        fb.put(5f);
        fb.put(6f);
        fb.put(0f);

        fb.flip();
        assertEquals(fb, obj.getPointsByteArray());
//        assertEquals("1.0 2.0 3.0 2.0 3.0 4.0 3.0 4.0 5.0 4.0 5.0 6.0 ", floatBufferToString(obj.getVertices()));
//        getPointsByteArray
//        assertEquals("1.0 2.0 3.0 2.0 3.0 4.0 3.0 4.0 5.0 4.0 5.0 6.0 ", floatBufferToString(obj.getNormal()));
//        assertEquals("0.0 1.0 1.0 1.0 1.0 0.0 0.0 0.0 ", floatBufferToString(obj.getTexcoord()));
        assertEquals("0 1 2 0 2 3 ", intBufferToString(obj.getIndex()));
        assertEquals("5 -1 -1 -1 0 -1 ", intBufferToString(obj.getAdjTable()));
    }

    private String intBufferToString(IntBuffer buffer) {
        String res = "";
        for (int i = 0; i < buffer.limit(); ++i) {
            res += buffer.get() + " ";
        }
        return res;
    }

    private String floatBufferToString(FloatBuffer buffer) {
        String res = "";
        for (int i = 0; i < buffer.limit(); ++i) {
            res += buffer.get() + " ";
        }
        return res;
    }

}
