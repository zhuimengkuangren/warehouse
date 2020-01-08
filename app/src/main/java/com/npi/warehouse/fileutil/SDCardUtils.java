package com.npi.warehouse.fileutil;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * SDCard相关工具类
 */
public class SDCardUtils {
	
	/**
	 * 判断SDCard是否可用
	 */
	public static boolean isSDCardEnable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取SD卡路径
	 */
	public static String getSDCardPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	}

	/**
	 * 获取SD卡的剩余容量 单位byte
	 */
	public static long getFreeSizeAll() {
		if (isSDCardEnable()) {
			long blockSize;
//			long totalBlocks;
			long availableBlocks;
			StatFs stat = new StatFs(getSDCardPath());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				blockSize = stat.getBlockSizeLong();
//				totalBlocks = stat.getBlockCountLong();
				availableBlocks = stat.getAvailableBlocksLong();
			} else {
				blockSize = stat.getBlockSize();
//				totalBlocks = stat.getBlockCount();
				availableBlocks = stat.getAvailableBlocks();
			}
			return blockSize * availableBlocks;
		}
		return 0;
	}

	/**
	 * 获取指定路径所在空间的剩余可用容量字节数，单位byte
	 */
	public static long getFreeSizeByPath(String filePath) {
		// 如果是sd卡的下的路径，则获取sd卡可用容量
		if (filePath.startsWith(getSDCardPath())) {
			filePath = getSDCardPath();
		} else {// 如果是内部存储的路径，则获取内存存储的可用容量
			filePath = Environment.getDataDirectory().getAbsolutePath();
		}
		StatFs stat = new StatFs(filePath);
		long blockSize;
//		long totalBlocks;
		long availableBlocks;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			blockSize = stat.getBlockSizeLong();
//			totalBlocks = stat.getBlockCountLong();
			availableBlocks = stat.getAvailableBlocksLong();
		} else {
			blockSize = stat.getBlockSize();
//			totalBlocks = stat.getBlockCount();
			availableBlocks = stat.getAvailableBlocks();
		}
		return blockSize * availableBlocks;
	}

	/**
	 * 调用该方法会返回应用程序的外部文件系统（Environment.getExternalStorageDirectory()）目录的绝对路径，它是用来存放应用的缓存文件，
	 * 它和getCacheDir目录一样，目录下的文件都会在程序被卸载的时候被清除掉。
	 */
	public static File getSDCardCache(Context context) {
		if (isSDCardEnable()) {
			return context.getExternalCacheDir();
		}
		return null;
	}

	/**
	 * 获取系统存储路径
	 */
	public static String getRootDirectoryPath() {
		return Environment.getRootDirectory().getAbsolutePath();
	}

	/**
	 * 返回通过Context.openFileOutput()创建和存储的文件系统的绝对路径，应用程序文件， 这些文件会在程序被卸载的时候全部删掉。
	 */
	public static File getAppFile(Context context) {
		return context.getFilesDir();
	}

	/**
	 * 返回应用程序指定的缓存目录，这些文件在设备内存不足时会优先被删除掉， 所以存放在这里的文件是没有任何保障的，可能会随时丢掉。
	 */
	public static File getAppCache(Context context) {
		return context.getCacheDir();
	}

	/**
	 * 这是一个可以存放你自己应用程序自定义的文件，你可以通过该方法返回的File实例来创建或者访问这个目录，
	 * 注意该目录下的文件只有你自己的程序可以访问。
	 */
	public static File getAppDir(Context context, String path) {
		return context.getDir(path, 0);
	}

}
