package jp.fbphoto2addressbook;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Kazuki Nishiura
 */
public class Util {
    public static byte[] getBytesFromBitmap(Bitmap b) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] buf = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            Log.e("FB2ADD", "Util.getBytesFromBitmap, failed to close bitmap stream");
        }
        return buf;
    }
}
