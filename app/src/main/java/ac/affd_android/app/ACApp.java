package ac.affd_android.app;

import android.app.Application;

/**
 * Created by ac on 10/31/16.
 * todo some describe
 */
public class ACApp extends Application{
    private static ACApp THIS;
    public ACApp() {
        THIS = this;
    }
    public static ACApp getApplication() {
        return THIS;
    }
}
