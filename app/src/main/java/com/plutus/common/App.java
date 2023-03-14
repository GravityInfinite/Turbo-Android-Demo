package com.plutus.common;


import androidx.multidex.MultiDexApplication;


public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        GravityEngineHelper.initGravityEngineSDK(this.getApplicationContext(), true);
    }

}
