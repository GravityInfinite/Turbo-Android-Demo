package com.plutus.common;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.plutus.common.core.utils.ApkUtils;
import com.plutus.common.core.utils.PLog;
import com.plutus.common.core.utils.SystemUtil;
import com.plutus.common.core.utils.TimeUtils;
import com.plutus.common.turbo.Turbo;
import com.plutus.common.turbo.beans.AdUnionType;
import com.plutus.common.turbo.beans.AdnType;
import com.plutus.common.turbo.beans.HandleEventType;
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
     * 注册turbo平台账号，只调用一次即可
     *
     * @param view
     */
    public void register(View view) {
        Log.d(TAG, " status " + Turbo.get().isRegisterSuccess());
        Turbo.get().register("test_client_id_android_qmkfd", "testUser", "baseChannel", 1, new RegisterCallback() {
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
     * 上报广告事件，取值如下：
     *
     * @param view
     */
    public void ad(View view) {
        Turbo.get().ad(0, AdnType.CSJ, "test_placement_id", "test_adsource_id", 100d, AdUnionType.ADMORE);
    }

    /**
     * 上报买量关键行为事件
     *
     * @param view
     */
    public void handle_event(View view) {
        Turbo.get().handleEvent(HandleEventType.PAY);
        Turbo.get().handleEvent(HandleEventType.REGISTER);
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
     * 记录初次设定的用户属性，比如记录用户首次访问时间
     *
     * @param view
     */
    public void profileSetOnce(View view) {
        Turbo.get().profileSetOnce("$first_visit_time", TimeUtils.formatTime(System.currentTimeMillis(), TimeUtils.YYYY_MM_DD_HH_MM_SS));
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
        Log.d(TAG, "is debug" + ApkUtils.isDebug());
        PLog.INSTANCE.d("track");
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