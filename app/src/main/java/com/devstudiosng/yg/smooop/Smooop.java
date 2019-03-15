package com.devstudiosng.yg.smooop;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Smooop extends Application {
    public static final String APP_PLAY_STORE_ID = "com.devstudiosng.yg.smooop";
    public static final String SERVER_BASE_URL = "https://smooop.com/api/";
    public static final String NOTIFICATIONS = "messages";
    public static final String ALERT = "sendAlert";
    public static final String SIGN_UP_API = "addUser";

    public RealmConfiguration config;
    @Override
    public void onCreate() {
        super.onCreate();

        // The default Realm file is "default.realm" in Context.getFilesDir();
        // we'll change it to "myrealm.realm"
        Realm.init(this);
        config = new RealmConfiguration.Builder()
                .name("smooop_debug.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);


    }
}
