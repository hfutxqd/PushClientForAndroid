package xyz.imxqd.pushclient;

import android.app.Application;

import xyz.imxqd.pushclient.dao.DB;

/**
 * Created by imxqd on 2017/3/26.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DB.init(this);
    }
}
