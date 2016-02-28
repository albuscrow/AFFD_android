package ac.affd_android.app;

import ac.affd_android.app.GL.ACOBJ;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.AndroidJUnitRunner;
import android.test.InstrumentationTestCase;
import android.util.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.DoubleBuffer;
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
        ACOBJ.Point.init();
        try {
            obj = new ACOBJ(inputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Log.d(TAG, doubleBufferToString(obj.getVertices()));
        assertEquals("1.0 2.0 3.0 2.0 3.0 4.0 3.0 4.0 5.0 1.0 2.0 3.0 4.0 5.0 6.0 ", doubleBufferToString(obj.getVertices()));
        assertEquals("0.0 1.0 1.0 1.0 1.0 0.0 1.0 1.0 0.0 0.0 ", doubleBufferToString(obj.getTexcoord()));
        assertEquals("1.0 2.0 3.0 2.0 3.0 4.0 3.0 4.0 5.0 3.0 4.0 5.0 4.0 5.0 6.0 ", doubleBufferToString(obj.getNormal()));
        assertEquals("0 1 2 3 2 4 ", intBufferToString(obj.getIndex()));
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
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Log.d(TAG, doubleBufferToString(obj.getVertices()));
        assertEquals("1.0 2.0 3.0 2.0 3.0 4.0 3.0 4.0 5.0 4.0 5.0 6.0 ", doubleBufferToString(obj.getVertices()));
        assertEquals("1.0 2.0 3.0 2.0 3.0 4.0 3.0 4.0 5.0 4.0 5.0 6.0 ", doubleBufferToString(obj.getNormal()));
        assertEquals("0.0 1.0 1.0 1.0 1.0 0.0 0.0 0.0 ", doubleBufferToString(obj.getTexcoord()));
        assertEquals("0 1 2 0 2 3 ", intBufferToString(obj.getIndex()));
    }

    private String intBufferToString(IntBuffer buffer) {
        String res = "";
        for (int i = 0; i < buffer.limit(); ++i) {
            res += buffer.get() + " ";
        }
        return res;
    }

    private String doubleBufferToString(DoubleBuffer buffer) {
        String res = "";
        for (int i = 0; i < buffer.limit(); ++i) {
            res += buffer.get() + " ";
        }
        return res;
    }

}
