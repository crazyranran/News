package com.ran.news.bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * ���ػ��湤����
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
				return bitmap;//����bitmap����
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;//�쳣����null,�������ȡ

	}

	public void setBitmapToLocal(Bitmap bitmap, String url) {
		// �����ļ���s
		File dirFile = new File(DIR_PATH);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			dirFile.mkdirs();
		}
		// �����ļ�
		try {
			File file = new File(DIR_PATH, MD5Encoder.encode(url));// ʹ��MD5�����㷨
			// ��ͼƬѹ�������أ�ѹ����ʽ��ѹ���������������
			bitmap.compress(CompressFormat.JPEG, 100,
					new FileOutputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
