package com.plutus.common;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.gravity.android.GravityEngineSDK;
import cn.gravity.android.InitializeCallback;
import cn.gravity.android.ScreenAutoTracker;
import cn.gravity.android.utils.GELog;

public class MainActivity extends AppCompatActivity implements ScreenAutoTracker {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 注册turbo平台账号，只调用一次即可
     *
     * @param view
     */
    public void initialize(View view) {
        GELog.i(TAG, "initialize called");
        GravityEngineHelper.getInstance().initialize(GravityEngineConstants.ACCESS_TOKEN, GravityEngineConstants.CLIENT_ID, GravityEngineConstants.USER_NAME, "base", new InitializeCallback() {

            @Override
            public void onFailed(String errorMsg, JSONObject initializeBody) {
                Log.d(TAG, "initialize failed " + errorMsg);
            }

            @Override
            public void onSuccess(JSONObject responseJson, JSONObject initializeBody) {
                Log.d(TAG, "initialize success");
            }
        }, false);
    }

    /**
     * 上报广告相关事件
     *
     * @param view
     */
    public void trackAdEvent(View view) {
        /**
         * 上报广告相关事件
         */
        // 上报广告加载事件
        GravityEngineHelper.getInstance().trackAdLoadEvent("topon", "placement_id", "ad_source_id", "reward", "csj");
        // 上报广告展示事件
        GravityEngineHelper.getInstance().trackAdShowEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1);
        // 上报广告点击事件
        GravityEngineHelper.getInstance().trackAdClickEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1);
        // 上报广告开始播放事件
        GravityEngineHelper.getInstance().trackAdPlayStartEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1);
        // 上报广告播放完成事件
        GravityEngineHelper.getInstance().trackAdPlayEndEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1, 50, false);
    }

    /**
     * 上报付费事件
     *
     * @param view
     */
    public void trackPayEvent(View view) {
        GravityEngineHelper.getInstance().trackPayEvent(300, "CNY", "order_id" + System.currentTimeMillis(), "月卡", "支付宝");
    }

    /**
     * 设置用户属性信息
     *
     * @param view
     * @throws JSONException
     */
    public void userSet(View view) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("$name", "turboUserName");
        jsonObject.put("$gender", "男");
        GravityEngineHelper.getInstance().user_set(jsonObject);
    }

    /**
     * 记录初次设定的用户属性，比如记录用户性别
     *
     * @param view
     */
    public void userSetOnce(View view) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("$gender", "male");
        GravityEngineHelper.getInstance().user_setOnce(jsonObject);
    }

    /**
     * 数值类型的属性直接进行累加
     *
     * @param view
     */
    public void userIncrement(View view) {
        GravityEngineHelper.getInstance().user_increment("$age", 27);
    }

    /**
     * 数值类型的属性取最大值
     *
     * @param view
     */
    public void userNumberMax(View view) {
        GravityEngineHelper.getInstance().user_max("ad_ecpm_max", 300);
    }

    /**
     * 数值类型的属性取最小值
     *
     * @param view
     */
    public void userNumberMin(View view) {
        GravityEngineHelper.getInstance().user_min("ad_ecpm_min", 100);
    }

    /**
     * 列表类型的属性，可以直接append
     *
     * @param view
     */
    public void userAppend(View view) throws JSONException {
        JSONArray moviesJsonArray = new JSONArray();
        moviesJsonArray.put("Interstellar");
        moviesJsonArray.put("The Negro Motorist Green Book");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Movies", moviesJsonArray);
        GravityEngineHelper.getInstance().user_append(jsonObject);
    }

    /**
     * 列表类型的属性，可以去重append
     *
     * @param view
     */
    public void userUniqAppend(View view) throws JSONException {
        JSONArray moviesJsonArray = new JSONArray();
        moviesJsonArray.put("Interstellar");
        moviesJsonArray.put("The Negro Motorist Green Book");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Movies", moviesJsonArray);
        GravityEngineHelper.getInstance().user_uniqAppend(jsonObject);
    }

    /**
     * 属性取消
     *
     * @param view
     */
    public void userUnset(View view) {
        GravityEngineHelper.getInstance().user_unset("$age");
    }

    /**
     * 清空用户属性信息
     *
     * @param view
     */
    public void userDelete(View view) {
        GravityEngineHelper.getInstance().user_delete();
    }

    /**
     * 设置公共事件属性
     * @param view
     */
    public void setSuperProperties(View view) {
        try {
            JSONObject superProperties = new JSONObject();
            superProperties.put("age",2);
            superProperties.put("channel","xiaomi");
            GravityEngineHelper.getInstance().setSuperProperties(superProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置自动采集事件自定义属性
     * @param view
     */
    public void setAutoTrackEventProperties(View view) {
        try {
            List<GravityEngineSDK.AutoTrackEventType> typeList = new ArrayList<>();
            typeList.add(GravityEngineSDK.AutoTrackEventType.APP_INSTALL);
            typeList.add(GravityEngineSDK.AutoTrackEventType.APP_END);
            JSONObject properties = new JSONObject();
            properties.put("key1",2);
            GravityEngineHelper.getInstance().setAutoTrackProperties(typeList,properties);
            properties.remove("AUTO_EVENT_PROP1");
            typeList.remove(GravityEngineSDK.AutoTrackEventType.APP_END);
            properties.put("AUTO_EVENT_PROP1","value1");
            properties.put("AUTO_EVENT_PROP2","value2");
            GravityEngineHelper.getInstance().setAutoTrackProperties(typeList, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除公共事件属性 Channel
     * @param view
     */
    public void unsetChannelProperties(View view) {
        GravityEngineHelper.getInstance().unsetSuperProperty("SUPER_PROPERTY_CHANNEL");
    }

    /**
     * 清空所有公共事件属性
     * @param view
     */
    public void unsetAllSuperProperties(View view) {
        GravityEngineHelper.getInstance().clearSuperProperties();
    }

    /**
     * 立即执行上报
     * @param view
     */
    public void flush(View view) {
        GravityEngineHelper.getInstance().flush();
    }

    public void clickTest(View view) {
        Intent intent = new Intent(this, ClickTestActivity.class);
        startActivity(intent);
    }

    @Override
    public String getScreenUrl() {
        return "gravityengine://page/main";
    }

    @Override
    public JSONObject getTrackProperties() {
        return null;
    }
}
