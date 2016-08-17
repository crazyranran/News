package com.ran.news.bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * 本地缓存工具类
 * 
 * @author asus
 *
 */
public class LocalCacheUtils {
	public static final String DIR_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/bimapCache";

	public Bitmap getBitmapFromLocal(String url) {
		try {
			File file = new File(DIR_PATH, MD5Encoder.encode(url));
			if (file.exists()) {
				Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(
						file));
				return bitmap;//返回bitmap对象
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;//异常返回null,从网络读取

	}

	public void setBitmapToLocal(Bitmap bitmap, String url) {
		// 创建文件夹s
		File dirFile = new File(DIR_PATH);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			dirFile.mkdirs();
		}
		// 创建文件
		try {
			File file = new File(DIR_PATH, MD5Encoder.encode(url));// 使用MD5加密算法
			// 将图片压缩到本地（压缩格式，压缩质量，输出流）
			bitmap.compress(CompressFormat.JPEG, 100,
					new FileOutputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
