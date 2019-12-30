package com.npi.warehouse.downloadutil;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.webkit.URLUtil;


/**
 * Create by zougf
 * On 2019/5/14
 * Description:文件下载管理类
 */
class DownloadFileManager {
    public static final String MIME_TYPE = "application/vnd.android.package-archive";
    private String downloadUrl;
    private long downloadId;
    private DownloadManager downloadManager;
    private int fileSize = -1;

    public DownloadFileManager(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    /**
     * 下载文件
     *
     * @param context
     * @return -1为跳转到浏览器了
     */
    public long downloadsFile(Context context) {
        if (downLoadMangerIsEnable(context)) {
            try {
                return toDownloads(context);
            } catch (Exception e) {
                e.printStackTrace();
                toStartBrowsable(context);
                return -1;
            }
        } else {
            toStartBrowsable(context);
            return -1;
        }

    }

    /**
     * 跳转到浏览器
     *
     * @param context
     */
    private void toStartBrowsable(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(downloadUrl));
        context.startActivity(intent);
    }

    /**
     * 下载文件
     *
     * @param context
     */
    private long toDownloads(Context context) {
        String fileName = getFileName();
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        //设置允许使用的网络类型，这里是移动网络和wifi都可以
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        //禁止发出通知，既后台下载，如果要使用这一句必须声明一个权限：android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
        //request.setShowRunningNotification(false);
        //下载器内部不显示下载界面
//        request.setVisibleInDownloadsUi(false);
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        if (fileName != null && fileName.endsWith("apk")) {
            request.setMimeType(MIME_TYPE);
        }
        request.setTitle(fileName);
        downloadId = downloadManager.enqueue(request);
        return downloadId;
    }

    /**
     * 获取文件名称
     *
     * @return
     */
    private String getFileName() {
        return URLUtil.guessFileName(downloadUrl, null, null);
    }

    /**
     * 检查下载状态
     */
    public int checkStatus() {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = null;
        try {
            cursor = downloadManager.query(query);
            if (cursor.moveToFirst()) {
                fileSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                return cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return -1;
    }


    public int getFileSize() {
        try {
            if (fileSize == -1) {
                checkStatus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileSize;
    }

    public Uri getFileUriPath() {
        try {
            return downloadManager.getUriForDownloadedFile(downloadId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }


    public long getDownloadId() {
        return downloadId;
    }


    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    /**
     * downLoadManger是否可用
     *
     * @param context
     * @return
     */
    public static boolean downLoadMangerIsEnable(Context context) {
        int state = context.getApplicationContext().getPackageManager()
                .getApplicationEnabledSetting("com.android.providers.downloads");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED);
        } else {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER);
        }
    }

    public boolean isApkFile() {
        if (downloadManager == null) return false;
        String mimeType = downloadManager.getMimeTypeForDownloadedFile(downloadId);
        return MIME_TYPE.equals(mimeType);
    }
}
