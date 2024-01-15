package com.zzzccc.hooker;
import static androidx.core.content.ContextCompat.getSystemService;


import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import de.robv.android.xposed.XposedBridge;

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
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            // 获取SSID可能需要定位权限，并且可能返回 "<unknown ssid>" 如果权限不足 Android 10之后无权限获取ssid
            String ssid = wifiInfo.getSSID();
            if (ssid != null) {
                // 注意SSID被双引号包围，如果需要可以去掉
                ssid = ssid.substring(1, ssid.length() - 1);
                Log.e("LSPosed","ssid: "+ssid);
            }
            String bssid = wifiInfo.getBSSID();
            if (bssid != null){
                Log.e("LSPosed","bssid: "+bssid);
            }

            try{
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if(telephonyManager.getSimState()== TelephonyManager.SIM_STATE_ABSENT){
                    Log.e("LSPosed","SIM card: not exit");
                }
                else{
                    String iccid = telephonyManager.getSimSerialNumber();
                    if(iccid!=null){
                        Log.e("LSPosed","iccid: "+iccid);
                    }
                }
            }catch (SecurityException e){
                Log.e("LSPosed","Can not ge iccid !");
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
            // REQUEST_CODE 是你定义的整数，用于 onRequestPermissionsResult 回调
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //Android+
            if (telephonyManager != null) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                    try{
                        String serialNumber = Build.getSerial();
                        Log.e("LSPosed","serialNumber: "+serialNumber);
                    }catch (SecurityException e){
                        Log.e("LSPosed","serialNumber: cant get");
                        e.printStackTrace();
                    }
                }
                // API 26+
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT<=Build.VERSION_CODES.Q) {
                    String imei = telephonyManager.getImei();
                    String meid = telephonyManager.getMeid();
                    Log.e("LSPosed","imei: "+ imei);
                    Log.e("LSPosed","meid: "+ meid);
                    // 使用获取到的IMEI
                } else {
                    // API 25及以下
                    String imei = telephonyManager.getDeviceId();
                    Log.e("LSPosed","imei is "+ imei);
                    // 使用获取到的IMEI
                }
            }
            // 使用获取到的ICCID
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PRECISE_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PRECISE_PHONE_STATE}, REQUEST_CODE);
            // REQUEST_CODE 是你定义的整数，用于 onRequestPermissionsResult 回调
        } else {
        }

    }
}