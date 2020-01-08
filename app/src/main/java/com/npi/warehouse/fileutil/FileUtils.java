package com.npi.warehouse.fileutil;import android.content.Context;import android.content.pm.ApplicationInfo;import android.content.pm.PackageInfo;import android.content.pm.PackageManager;import android.net.Uri;import android.os.Build;import android.text.TextUtils;import android.webkit.URLUtil;import androidx.core.content.FileProvider;import java.io.BufferedReader;import java.io.BufferedWriter;import java.io.ByteArrayInputStream;import java.io.ByteArrayOutputStream;import java.io.Closeable;import java.io.File;import java.io.FileInputStream;import java.io.FileNotFoundException;import java.io.FileOutputStream;import java.io.FileWriter;import java.io.IOException;import java.io.InputStream;import java.io.InputStreamReader;import java.io.OutputStream;import java.io.StringReader;import java.io.StringWriter;import java.nio.channels.FileChannel;import java.util.List;import java.util.Locale;/** * 文件相关工具类 */public class FileUtils {	/**	 * 创建目录	 */	public File createDir(String dirName) {		File dir = new File(dirName);		dir.mkdir();		return dir;	}	/**	 * 创建文件	 */	public static File createFile(String folderPath, String fileName) {		String filePath = SDCardUtils.getSDCardPath() + File.separator + folderPath + File.separator + fileName;		File file = new File(filePath);		try {			file.createNewFile();		} catch (IOException e) {			e.printStackTrace();		}		return file;	}	/**	 * 文件是否存在	 */	public static boolean exists(String filePath) {		File file = new File(filePath);		return file.exists();	}	/**	 * 判断文件夹是否存在	 */	public boolean isFileExist(String fileName) {		File file = new File(fileName);		return file.exists();	}	public static Uri getFileUri(Context context, String filePath) {		Uri contentUri;		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //如果在Android7.0以上,使用FileProvider获取Uri			contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(filePath));		} else {    //否则使用Uri.fromFile(file)方法获取Uri			contentUri = Uri.fromFile(new File(filePath));		}		return contentUri;	}	/**	 * 目录文件数	 */	public static int dirFileSize(File dirFile) {		try {			return dirFile.listFiles().length;		} catch (Exception e) {			e.printStackTrace();			return 0;		}	}	/**	 * 获取文件名称	 *	 * @return	 */	public static String getLinkFileName(String url) {		return URLUtil.guessFileName(url, null, null);	}	public static Boolean isApkFile(Context context, String filePath) {		String pkg;		try {			PackageManager packageManager = context.getPackageManager();			PackageInfo info = packageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);			if (info != null) {				ApplicationInfo appInfo = info.applicationInfo;				pkg = appInfo.packageName;//得到安装包名称     			} else {				pkg = "";			}		} catch (Exception e) {			pkg = "";		}		return !TextUtils.isEmpty(pkg);	}	/**	 * 复制文件	 * 	 * @param from	 * @param to	 */	public static void copyFile(File from, File to) {		if (null == from || !from.exists()) {			return;		}		if (null == to) {			return;		}		FileInputStream is = null;		FileOutputStream os = null;		try {			is = new FileInputStream(from);			if (!to.exists()) {				to.createNewFile();			}			os = new FileOutputStream(to);			copyFileFast(is, os);		} catch (Exception e) {			throw new RuntimeException(FileUtils.class.getName(), e);		} finally {			closeIO(is, os);		}	}	/**	 * 快速复制文件（采用nio操作）	 * 	 * @param is	 * @param os	 * @throws IOException	 */	public static void copyFileFast(FileInputStream is, FileOutputStream os) throws IOException {		FileChannel in = is.getChannel();		FileChannel out = os.getChannel();		in.transferTo(0, in.size(), out);	}	/**	 * 关闭文件	 * 	 * @param closeables	 */	public static void closeIO(Closeable... closeables) {		if (null == closeables || closeables.length <= 0) {			return;		}		for (Closeable cb : closeables) {			try {				if (null == cb) {					continue;				}				cb.close();			} catch (IOException e) {				throw new RuntimeException(FileUtils.class.getName(), e);			}		}	}	/**	 * 字符串写入指定路径File	 * 	 * @param res	 * @param filePath	 * @return	 */	public static boolean writeString2File(String res, String filePath, String fileName) {		boolean flag = true;		BufferedReader bufferedReader = null;		BufferedWriter bufferedWriter = null;		try {			File distFile = createFile(filePath, fileName);			if (!distFile.getParentFile().exists())				distFile.getParentFile().mkdirs();			bufferedReader = new BufferedReader(new StringReader(res));			bufferedWriter = new BufferedWriter(new FileWriter(distFile));			char buf[] = new char[1024]; // 字符缓冲区			int len;			while ((len = bufferedReader.read(buf)) != -1) {				bufferedWriter.write(buf, 0, len);			}			bufferedWriter.flush();			bufferedReader.close();			bufferedWriter.close();		} catch (IOException e) {			e.printStackTrace();			flag = false;			return flag;		} finally {			if (bufferedReader != null) {				try {					bufferedReader.close();				} catch (IOException e) {					e.printStackTrace();				}			}		}		return flag;	}	/**	 * InputStream写入指定路径File	 * 	 * @param input	 *            输入的InputStream	 * @param filePath	 *            文件的路径	 * @param fileName	 *            文件的名称	 * @return	 */	public File writeInputStream2File(InputStream input, String filePath, String fileName) {		int FILESIZE = 4 * 1024;		File file = null;		OutputStream output = null;		try {			createDir(filePath);			file = createFile(filePath, fileName);			output = new FileOutputStream(file);			byte[] buffer = new byte[FILESIZE];			int length;			while ((length = (input.read(buffer))) > 0) {				output.write(buffer, 0, length);			}			output.flush();		} catch (Exception e) {			e.printStackTrace();		} finally {			try {				output.close();			} catch (IOException e) {				e.printStackTrace();			}		}		return file;	}	/**	 * byte[]写入指定路径File	 * 	 * @param input	 *            输入的byte[]	 * @param filePath	 *            文件路径	 * @param fileName	 *            文件名称	 */	public static void writeByte2File(byte[] input, String filePath, String fileName) {		File folder = new File(filePath);		folder.mkdirs();		File file = new File(filePath, fileName);		ByteArrayInputStream is = new ByteArrayInputStream(input);		OutputStream os = null;		if (!file.exists()) {			try {				file.createNewFile();				os = new FileOutputStream(file);				byte[] buffer = new byte[1024];				int len = 0;				while (-1 != (len = is.read(buffer))) {					os.write(buffer, 0, len);				}				os.flush();			} catch (Exception e) {				throw new RuntimeException(FileUtils.class.getName(), e);			} finally {				closeIO(is, os);			}		}	}	/**	 * 指定文件路径中读取String	 * 	 * @param filePath	 * @return	 * @throws IOException	 */	public static String readStringFromFile(String filePath, String encoding) {		InputStreamReader reader = null;		StringWriter writer = new StringWriter();		try {			if (encoding == null || "".equals(encoding.trim())) {				reader = new InputStreamReader(new FileInputStream(filePath), encoding);			} else {				reader = new InputStreamReader(new FileInputStream(filePath));			}			// 将输入流写入输出流			char[] buffer = new char[1024];			int n = 0;			while (-1 != (n = reader.read(buffer))) {				writer.write(buffer, 0, n);			}		} catch (Exception e) {			e.printStackTrace();			return null;		} finally {			if (reader != null) {				try {					reader.close();				} catch (IOException e) {					e.printStackTrace();				}			}		}		// 返回转换结果		if (writer != null)			return writer.toString();		else			return null;	}	/**	 * 从指定文件路径中读取byte[]	 * 	 * @param filePath	 * @return	 */	public static byte[] readBytesFromFile(String filePath) {		byte[] buffer = null;		try {			File file = new File(filePath);			FileInputStream fis = new FileInputStream(file);			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);			byte[] b = new byte[1000];			int n;			while ((n = fis.read(b)) != -1) {				bos.write(b, 0, n);			}			fis.close();			bos.close();			buffer = bos.toByteArray();		} catch (FileNotFoundException e) {			e.printStackTrace();		} catch (IOException e) {			e.printStackTrace();		}		return buffer;	}		/**	 * 删除该目录下指定后缀的所有文件,如果路径为文件夹（self为true则删除该文件夹）	 * @param f	 * @param self	 * @param typeLst	 * @return	 */	public static void delFile(File f, boolean self, List<String> typeLst) {		try {			if (!f.exists()) {				return;			}			if (f.isDirectory()) {				File[] fs = f.listFiles();				for (int i = 0; i < fs.length; i++) {					delFile(fs[i],self,typeLst);				}				if(self){					f.delete();				}			} else {				String[] str = f.getAbsolutePath().split("\\.");				if(str.length>0 && typeLst.contains(str[str.length-1].toLowerCase(Locale.getDefault()))) {					f.delete();				}			}		} catch (Exception e) {		}	}		/**	 * 删除该目录下的所有文件,如果路径为文件夹（self为true则删除该文件夹）	 * @param f	 * @param self	 * @return	 */	public static boolean delFile(File f, boolean self) {		boolean flag = false;		try {			if (!f.exists()) {				return true;			}			if (f.isDirectory()) {				File[] fs = f.listFiles();				for (int i = 0; i < fs.length; i++) {					flag = delFile(fs[i],self);					if (!flag) {						break;					}				}				if(self){					f.delete();				}			} else {				flag = f.delete();			}		} catch (Exception e) {			flag = false;		}		return flag;	}	/**	 * 删除该目录下的所有文件,如果路径为文件夹（self为true则删除该文件夹）	 * @param dir	 * @param self	 * @return	 */	public static boolean delFile(String dir, boolean self) {		File f = new File(dir);		return delFile(f,self);	}}