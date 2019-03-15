package com.devstudiosng.yg.smooop;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Epp extends Application {

    public RealmConfiguration config;
    @Override
    public void onCreate() {
        super.onCreate();

        // The default Realm file is "default.realm" in Context.getFilesDir();
        // we'll change it to "myrealm.realm"
        Realm.init(this);
        config = new RealmConfiguration.Builder()
                .name("epp_debug.realm")
                .schemaVersion(2)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);


    }
}
