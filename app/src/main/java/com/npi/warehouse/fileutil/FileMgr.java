package com.npi.warehouse.fileutil;

import android.content.Context;

import java.io.File;

public class FileMgr {

	// 获取临时目录路径
	public static String getTempPath(Context context) {
		String tempPath = context.getExternalCacheDir()+ File.separator;
		File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		return tempPath;
	}

	// 获取启动页图片路径
	public static String getSplashPath(Context context) {
		String path = context.getExternalFilesDir("splash")+ File.separator;
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		return path;
	}

	public static String getApkPath(Context context) {
		String path = context.getExternalFilesDir("apk")+ File.separator;
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		return path;
	}

	public static String getFilePath(Context context){
		String path = context.getExternalFilesDir("myPhoto")+ File.separator;
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		return path;
	}

	public static void clear(Context mContext){
		FileUtils.delFile(mContext.getExternalCacheDir(), true);
		FileUtils.delFile(mContext.getExternalFilesDir("splash"), true);
	}

}
