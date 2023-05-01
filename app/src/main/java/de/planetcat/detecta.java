package de.planetcat;

import android.app.Application;
import android.content.Context;

public class detecta extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        detecta.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return detecta.context;
    }
}
