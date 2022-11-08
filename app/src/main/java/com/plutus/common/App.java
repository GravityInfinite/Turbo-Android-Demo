package com.plutus.common;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.plutus.common.core.GravitySDK;
import com.plutus.common.core.utils.SystemUtil;
import com.plutus.common.turbo.Turbo;
import com.plutus.common.turbo.api.TurboConfigOptions;


public class App extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (SystemUtil.isInMainProcess(base)) {
            boolean isDebug = true; // 调试阶段打开可以输出部分日志信息，线上请不要开启，会影响性能
            String buildType = isDebug ? "debug" : "release";
            GravitySDK.onAttachBaseContext(base, isDebug, buildType, "flavorString");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (SystemUtil.isInMainProcess(this)) {
            GravitySDK.onCreate(this);

            // 请尽可能的早调用，暂时只支持使用默认配置
            TurboConfigOptions configOptions = new TurboConfigOptions();
            // 必须每次启动都调用，建议在用户同意隐私政策弹窗之后，传入isAgreePrivacy参数为true
            Turbo.get().init("k7ZjSgc1Z8j551UJUNLlWA==", "x5emsWAxqnlwqpDH1j4bbicR8igmhruT", true, configOptions);
        }
    }

}
