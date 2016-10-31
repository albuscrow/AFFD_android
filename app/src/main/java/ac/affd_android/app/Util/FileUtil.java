package ac.affd_android.app.Util;

import ac.affd_android.app.ACApp;
import android.content.Context;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by ac on 10/31/16.
 * todo some describe
 */
public class FileUtil {
    static public void save(String name, ByteBuffer buffer, int length) {
        FileOutputStream fos = null;
        try {
            fos = ACApp.getApplication().openFileOutput(name, Context.MODE_PRIVATE);
            fos.write(buffer.array(), 0, length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nullable
    static public ByteBuffer load(String name) {
        FileInputStream fis = null;
        try {
            fis = ACApp.getApplication().openFileInput(name);
            BufferedInputStream bis = new BufferedInputStream(fis);
            int length = bis.available();
            byte[] output = new byte[length];
            int readLength = bis.read(output, 0, length);
            if (readLength != length) {
                throw new RuntimeException("read shader program from file error");
            }
            return ByteBuffer.wrap(output, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
