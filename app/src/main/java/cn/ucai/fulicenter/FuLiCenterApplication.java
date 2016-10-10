package cn.ucai.fulicenter;

import android.app.Application;
import android.content.Context;


public class FuLiCenterApplication extends Application {
    public static Context applicationContext;
    private static FuLiCenterApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
    }

    public static FuLiCenterApplication getInstance(){
        return instance;
    }
}
