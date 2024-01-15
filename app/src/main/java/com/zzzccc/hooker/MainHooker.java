package com.zzzccc.hooker;

import static com.zzzccc.hooker.Utils.IMEIUtil.getDeviceIdByReflect;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHooker implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        // Hook方法是为了找到合适的时机进行操作，这里以app加载为例
        if (!lpparam.packageName.equals("com.kwai.m2u")) {
            return;
        }

        // 找到合适的类和方法进行hook，这里以Activity.onCreate为例
        final Class<?> activityClass = XposedHelpers.findClass("android.app.Activity", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(activityClass, "onCreate", android.os.Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                // 获取Android-ID
                Context context = (Context) param.thisObject;
                String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                XposedBridge.log("Original Android ID: " + androidId);

                // 在这里读取MAC地址文件
                String macAddress = getMachineHardwareAddress();
                XposedBridge.log("MAC Address: " + macAddress);

                //获取已安装应用信息
                PackageManager packageManager = context.getPackageManager();
                List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
                for (ApplicationInfo appInfo : apps) {
                    // 获取应用的名称
                    String appName = packageManager.getApplicationLabel(appInfo).toString();
                    // 获取应用的包名
                    String packageName = appInfo.packageName;
                    // 打印到日志或者处理获取到的应用名称和包名
                    XposedBridge.log("App Name: " + appName + ", Package Name: " + packageName);
                }
                //获取版本号
                XposedBridge.log("The VERSION is : "+Build.VERSION.SDK_INT);
                //获取imei,即序列号 需要插卡
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    try{
                        String serialNumber = Build.getSerial();
                        XposedBridge.log("serialNumber is : "+serialNumber);
                    }catch (SecurityException e){
                        e.printStackTrace();
                    }
                }else{
                    String imei = getDeviceIdByReflect(context);
                    XposedBridge.log("imei is "+imei);
                }


                //获取ssid,bssid,iccid
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                // 获取SSID可能需要定位权限，并且可能返回 "<unknown ssid>" 如果权限不足 Android 10之后无权限获取ssid
                String ssid = wifiInfo.getSSID();
                if (ssid != null) {
                    // 注意SSID被双引号包围，如果需要可以去掉
                    ssid = ssid.substring(1, ssid.length() - 1);
                    XposedBridge.log("ssid is : "+ssid);
                }
                String bssid = wifiInfo.getBSSID();
                if (bssid != null){
                    XposedBridge.log("bssid is : "+bssid);
                }
                try{
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    String iccid = telephonyManager.getSimSerialNumber();
                    if(iccid!=null){
                        XposedBridge.log("iccid is : "+iccid);
                    }
                }catch (SecurityException e){
                    e.printStackTrace();
                }



            }
        });
    }
    public static String getMachineHardwareAddress() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String hardWareAddress = null;
        NetworkInterface iF = null;
        if (interfaces == null) {
            return null;
        }
        while (((Enumeration<?>) interfaces).hasMoreElements()) {
            iF = interfaces.nextElement();
            try {
                hardWareAddress = bytesToString(iF.getHardwareAddress());
                if (hardWareAddress != null)
                    break;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return hardWareAddress;
    }
    /***
     * byte转为String
     *
     * @param bytes
     * @return
     */
    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length()>0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }
    private String readMacAddress(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String mac = reader.readLine();
            reader.close();
            return mac != null ? mac.trim() : "Unavailable";
        } catch (Exception e) {
            XposedBridge.log(e.toString());
            return "Error";
        }
    }
}