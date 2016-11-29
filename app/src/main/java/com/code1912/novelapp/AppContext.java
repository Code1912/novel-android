package com.code1912.novelapp;
import android.app.Application;
import com.orm.SugarContext;

/**
 * Created by Code1912 on 2016/11/29.
 */

public class AppContext extends Application {
    private static AppContext singleton;

    public static AppContext getInstance(){
        return singleton;
    }

    @Override
    public final void onCreate() {
        super.onCreate();
        singleton = this;
        SugarContext.init(this);
    }

    @Override
    public void onTerminate() {
        SugarContext.terminate();
        super.onTerminate();
    }
}
