package com.zzzccc.hooker;
import static androidx.core.content.ContextCompat.getSystemService;


import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 检查权限是否已被授予
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 如果没有授予权限，需要请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            // 已经有权限，你可以执行需要这个权限的操作了
            // 例如获取SSID等
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
            // REQUEST_CODE 是你定义的整数，用于 onRequestPermissionsResult 回调
        } else {
            // 已有权限，获取ICCID

            // 使用获取到的ICCID
        }
    }
}