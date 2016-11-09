package ac.affd_android.affdview.Util;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by ac on 10/31/16.
 * todo some describe
 */
public class FileUtil {
    static public void save(Context context, String name, ByteBuffer buffer, int length) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(name, Context.MODE_PRIVATE);
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
    static public ByteBuffer load(Context context, String name) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(name);
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

    static public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
