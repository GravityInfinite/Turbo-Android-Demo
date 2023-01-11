package com.plutus.common;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.plutus.common.turbo.Turbo;
import com.plutus.common.turbo.beans.HandleEventType;
import com.plutus.common.turbo.beans.UserGetResponseBody;
import com.plutus.common.turbo.callback.QueryUserInfoCallback;
import com.plutus.common.turbo.callback.RegisterCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class TurboActivity extends AppCompatActivity {

    private static final String TAG = "TurboActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turbo);
    }

    /**
     * 注册turbo平台账号，只调用一次
     *
     * @param view
     */
    public void register(View view) {
        Log.d(TAG, " status " + Turbo.get().isRegisterSuccess());
        Turbo.get().register("test_client_id_android_qmkfd", "testUser", "base", 1, new RegisterCallback() {
            @Override
            public void onFailed(String errorMsg) {
                Log.d(TAG, "register failed " + errorMsg);
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "register success");
                Log.d(TAG, " status " + Turbo.get().isRegisterSuccess());
            }
        });
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
        Turbo.get().trackAdLoadEvent("topon", "placement_id", "ad_source_id", "reward", "csj");
        // 上报广告展示事件
        Turbo.get().trackAdShowEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1);
        // 上报广告点击事件
        Turbo.get().trackAdClickEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1);
        // 上报广告开始播放事件
        Turbo.get().trackAdPlayStartEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1);
        // 上报广告播放完成事件
        Turbo.get().trackAdPlayEndEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1, 50, false);
    }

    /**
     * 上报买量关键行为事件
     *
     * @param view
     */
    public void handle_event(View view) {
        JsonObject properties = new JsonObject();
        properties.addProperty("amount", 300);
        properties.addProperty("real_amount", 300);
        Turbo.get().handleEvent(HandleEventType.PAY, properties, 0, false, null);
        Turbo.get().handleEvent(HandleEventType.REGISTER, null, 0, false, null);
    }

    /**
     * 上报付费事件
     *
     * @param view
     */
    public void trackPayEvent(View view) {
        Turbo.get().trackPayEvent(300, "CNY", "order_id" + System.currentTimeMillis(), "月卡", "支付宝", true);
    }

    /**
     * 查询用户买量信息
     *
     * @param view
     */
    public void queryUserInfo(View view) {
        Turbo.get().queryUserInfoAsync(new QueryUserInfoCallback() {
            @Override
            public void onFailed(String errorMsg) {
                Log.e(TAG, "queryUserInfo onFailed " + errorMsg);
            }

            @Override
            public void onSuccess(UserGetResponseBody.UserGetInfo userGetResponseBody) {
                Log.d(TAG, "queryUserInfo onSuccess " + userGetResponseBody.toString());
            }
        });
    }

    /**
     * 注册全局通用属性
     *
     * @param view
     */
    public void registerSuperProperties(View view) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("role_type", "法师");
            jsonObject.put("role_power", 1000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Turbo.get().registerSuperProperties(jsonObject);
    }

    /**
     * 取消某一个全局属性
     *
     * @param view
     */
    public void unregisterSuperProperties(View view) {
        Turbo.get().unregisterSuperProperty("role_type");
    }

    /**
     * 清除全部全局属性
     *
     * @param view
     */
    public void clearSuperProperties(View view) {
        Turbo.get().clearSuperProperties();
    }

    /**
     * 设置用户属性信息
     *
     * @param view
     * @throws JSONException
     */
    public void profileSet(View view) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("$name", "turboUserName");
        jsonObject.put("$gender", "男");
        Turbo.get().profileSet(jsonObject);
    }

    /**
     * 记录初次设定的用户属性，比如记录用户性别
     *
     * @param view
     */
    public void profileSetOnce(View view) {
        Turbo.get().profileSetOnce("$gender", "male");
    }

    /**
     * 数值类型的属性直接进行累加
     *
     * @param view
     */
    public void profileIncrement(View view) {
        Turbo.get().profileIncrement("$age", 27);
    }

    /**
     * 列表类型的属性，可以直接append
     *
     * @param view
     */
    public void profileAppend(View view) {
        Set<String> movies = new HashSet<>();
        movies.add("Interstellar");
        movies.add("The Negro Motorist Green Book");
        Turbo.get().profileAppend("Movies", movies);
    }

    /**
     * 属性取消
     *
     * @param view
     */
    public void profileUnset(View view) {
        Turbo.get().profileUnset("$name");
    }

    /**
     * 清空用户属性信息
     *
     * @param view
     */
    public void profileDelete(View view) {
        Turbo.get().profileDelete();
    }

    /**
     * 自定义track事件上报
     *
     * @param view
     */
    public void track(View view) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("guild_id", "100");
            jsonObject.put("guild_level", 10);
            jsonObject.put("guild_name", "北方之狼");
            Turbo.get().track("AddGuild", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}