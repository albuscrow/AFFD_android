package ac.affd_android.affdview.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ac on 10/31/16.
 * todo some describe
 */
public class PreferenceUtil {
    static public void save(Context c, String key, int value) {
        getPreference(c)
                .edit()
                .putInt(key, value)
                .apply();

    }

    static public int loadInt(Context c, String key) {
        return getPreference(c).getInt(key, 0);
    }

    private static SharedPreferences getPreference(Context c) {
        return PreferenceManager
                .getDefaultSharedPreferences(c);
    }



}
