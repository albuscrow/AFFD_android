package ac.affd_android.app.Util;

import ac.affd_android.app.ACApp;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ac on 10/31/16.
 * todo some describe
 */
public class PreferenceUtil {
    static public void save(String key, int value) {
        getPreference()
                .edit()
                .putInt(key, value)
                .apply();

    }

    static public int loadInt(String key) {
        return getPreference().getInt(key, 0);
    }

    private static SharedPreferences getPreference() {
        return PreferenceManager
                .getDefaultSharedPreferences(ACApp.getApplication());
    }



}
