package com.npi.warehouse.downloadutil;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;


/**
 * Create by zougf
 * On 2019/5/14
 * Description:app文件下载安装
 */
public class ApkInstallDownloads {
    public static final int UNKNOWN_CODE = 10086;
    private DownloadFileManager downloadFileManager;


    private ApkInstallDownloads() {
    }

    public static ApkInstallDownloads newInstance(String downloadUrl) {
        ApkInstallDownloads apkInstallDownloads = new ApkInstallDownloads();
        apkInstallDownloads.downloadFileManager = new DownloadFileManager(downloadUrl);
        return apkInstallDownloads;
    }

    /**
     * 下载成功广播接收器
     */
    private class CompleteReceiver extends BroadcastReceiver {
        public CompleteReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (completeDownloadId == downloadFileManager.getDownloadId() && downloadFileManager.isApkFile()) {

                Uri uri = downloadFileManager.getFileUriPath();
                if (uri != null) {
                    ApkInstallUtils.toInstallApk(context, uri);
                    context.unregisterReceiver(this);
                } else {
                    Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadFileManager.getDownloadUrl()));
                    context.startActivity(it);
                }
            }
        }
    }


    //安装应用的流程
    public void installProcess(final Activity activity) {
        ApkInstallUtils.showInstallPermissionDialog(activity);

        if (downloadFileManager.downloadsFile(activity) != -1) {
            //注册下载成功的广播
            activity.getApplication().registerReceiver(new CompleteReceiver(), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }


    /**
     * 权限设置的回调
     *
     * @param activity
     * @param requestCode
     * @param resultCode
     */
    public void onActivityResult(Activity activity, int requestCode, int resultCode) {
        if (resultCode == Activity.RESULT_OK && requestCode == UNKNOWN_CODE) {
            if (downloadFileManager.getDownloadId() != -1 && downloadFileManager.checkStatus() == DownloadManager.STATUS_SUCCESSFUL && downloadFileManager.isApkFile()) {
                ApkInstallUtils.install(activity, downloadFileManager.getFileUriPath());
            }
        }
    }

}
