package com.npi.warehouse.downloadutil;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * Create by zougf
 * On 2019/5/14
 * Description:
 */
class ApkInstallUtils {

    public static final int UNKNOWN_CODE = 10086;


    //安装权限弹窗
    public static boolean showInstallPermissionDialog(final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先获取是否有安装未知来源应用的权限
            boolean haveInstallPermission = activity.getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {//没有权限
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("安装提示").setMessage("安装应用需要打开未知来源权限，请去设置中开启权限")
                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startInstallPermissionSettingActivity(activity);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
                return true;
            }
        }
        return false;

    }

    /**
     * 打开安装app权限设置
     *
     * @param activity
     */
    private static void startInstallPermissionSettingActivity(Activity activity) {
        Uri packageURI = Uri.parse("package:" + activity.getPackageName());
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        activity.startActivityForResult(intent, UNKNOWN_CODE);
    }

    /**
     * 安装app
     *
     * @param activity
     * @param filePath
     * @return whether apk exist
     */
    public static boolean install(Activity activity, String filePath) {
        if (showInstallPermissionDialog(activity)) return false;
        return toInstallApk(activity, filePath);
    }

    /**
     * 安装app
     *
     * @param activity
     * @param fileUri
     * @return whether apk exist
     */
    public static boolean install(Activity activity, Uri fileUri) {
        if (showInstallPermissionDialog(activity)) return false;
        return toInstallApk(activity, fileUri);
    }

    public static boolean toInstallApk(Context context, String filePath) {
        File file = new File(filePath);
        if (file.length() > 0 && file.exists() && file.isFile()) {
            Uri apkUri = getFileUri(context, file.getPath());
            toInstallApk(context, apkUri);
            return true;
        }
        return false;
    }

    public static Uri getFileUri(Context context, String filePath) {
        Uri contentUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //如果在Android7.0以上,使用FileProvider获取Uri
            try {
                contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(filePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {    //否则使用Uri.fromFile(file)方法获取Uri
            contentUri = Uri.fromFile(new File(filePath));
        }
        return contentUri;
    }


    public static boolean toInstallApk(Context context, Uri apkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, DownloadFileManager.MIME_TYPE);
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return true;
    }
}
