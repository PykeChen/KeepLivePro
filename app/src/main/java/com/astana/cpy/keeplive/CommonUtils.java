package com.astana.cpy.keeplive;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * @author cpy
 * @Description:
 * @version:
 * @date: 2018/10/10
 */
public class CommonUtils {


    /**
     * 检查指定权限是否可用。
     *
     * @param context    上下文
     * @param permission 权限名称
     * @return true可用
     */
    public static boolean isPermissionEnable(Context context, String permission) {
        if (context == null || TextUtils.isEmpty(permission)) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 6.0 以上动态权限检查。
            int result = context.checkSelfPermission(permission);
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            // 6.0 以下 manifest 权限检查。
            PackageManager packageManager = context.getPackageManager();
            int result = packageManager.checkPermission(permission, context.getPackageName());
            return result == PackageManager.PERMISSION_GRANTED;
        }
    }


    /**
     * 获取 IMEI
     *
     * @return String
     */
    public static String getIMEI(Context context) {
        boolean enable = isPermissionEnable(context, Manifest.permission.READ_PHONE_STATE);
        String imei = null;
        if (enable) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String tmp = telephonyManager.getDeviceId();
            imei = tmp;
        }

        if (imei == null || imei.equals("")) {
            imei = "000000000000000";
        }
        return imei;
    }

}
