package com.plutus.common;

import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.gravity.android.GEConfig;
import cn.gravity.android.GravityEngineSDK;

public class GravityEngineHelper {
    private static GravityEngineSDK mInstance;

    public static GravityEngineSDK getInstance() {
        return mInstance;
    }

    /**
     * 初始化 SDK
     */
    public static void initGravityEngineSDK(Context context, boolean isDebug) {
        GEConfig config = GEConfig.getInstance(context, GravityEngineConstants.ACCESS_TOKEN);
        if (isDebug) {
            config.setMode(GEConfig.ModeEnum.DEBUG);
        }
        config.enableAndroidId(false);
        config.enableOAID(false);
        config.enableIMEI(false);
        config.enableMAC(false);
        mInstance = GravityEngineSDK.setupAndStart(config);
        enableAutoTrack();
    }

    /**
     * 开启事件的自动采集
     */
    public static void enableAutoTrack() {
        List<GravityEngineSDK.AutoTrackEventType> typeList = new ArrayList<>();
        typeList.add(GravityEngineSDK.AutoTrackEventType.APP_INSTALL);
        typeList.add(GravityEngineSDK.AutoTrackEventType.APP_END);
        typeList.add(GravityEngineSDK.AutoTrackEventType.APP_CRASH);
        typeList.add(GravityEngineSDK.AutoTrackEventType.APP_VIEW_SCREEN);
        typeList.add(GravityEngineSDK.AutoTrackEventType.APP_CLICK);
        //测试自动采集事件自定义属性
        JSONObject properties = new JSONObject();
        JSONObject superProperties = new JSONObject();
        try {
            properties.put("key1", "self value1");
            superProperties.put("key1", "super value1");
            mInstance.setSuperProperties(superProperties);
            //
//            mInstance.enableAutoTrack(typeList, new GravityEngineSDK.AutoTrackEventListener() {
//                @Override
//                public JSONObject eventCallback(GravityEngineSDK.AutoTrackEventType eventType, JSONObject properties) {
//                    try {
//                        return new JSONObject("{\"keykey\":\"value1111\"}");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        return null;
//                    }
//                }
//            });
//            mInstance.enableAutoTrack(typeList, new GravityEngineSDK.AutoTrackEventListener() {
//                @Override
//                public JSONObject eventCallback(GravityEngineSDK.AutoTrackEventType eventType, JSONObject properties) {
//                    try {
//                        return new JSONObject("{\"keykey\":\"value2222\"}");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        return null;
//                    }
//                }
//            });
            mInstance.enableAutoTrack(typeList, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
