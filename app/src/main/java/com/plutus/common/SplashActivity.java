package com.plutus.common;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "SplashActivity";
    private boolean isColdStart = true; // 是否为冷启动


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = getIntent();
        if (intent != null) {
            isColdStart = intent.getBooleanExtra("is_cold_start", true);
        }

        Log.d(TAG, "cold start is " + isColdStart);

        // 全屏沉浸式，去掉status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        if (isColdStart) {
            String[] perms;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                perms = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.READ_SMS, android.Manifest.permission.READ_PHONE_NUMBERS};
            } else {
                perms = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,};
            }
            if (EasyPermissions.hasPermissions(this, perms)) {
                allPermissionGranted();
            } else {
                Log.d(TAG, "check permission");
                EasyPermissions.requestPermissions(
                        new PermissionRequest.Builder(SplashActivity.this, 1, perms)
                                .setRationale("需要您授权才能继续，请在接下来的弹窗里选择【允许授权】")
                                .build());
            }
        } else {
            allPermissionGranted();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        allPermissionGranted();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        allPermissionGranted();
    }

    private void allPermissionGranted() {
        startActivity(new Intent(SplashActivity.this, TurboActivity.class));
        SplashActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
    }
}