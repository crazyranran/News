package com.ran.news.bitmap;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * ͼƬ�������湤����
 * 
 * @author asus
 *
 */
public class MyBitmapUtils {
	
	NetCacheUtils netCacheUtils;
	LocalCacheUtils localCacheUtils;
	MemoryCacheUtils memoryCacheUtils;

	public MyBitmapUtils() {
		netCacheUtils= new NetCacheUtils(localCacheUtils,memoryCacheUtils);
		localCacheUtils = new LocalCacheUtils();

	}
	//���ڴ���أ�û�оʹӱ��ؼ��أ�û���ٴ��������
	public  void display(ImageView image, String url) {
		// �ڴ����
		Bitmap bitmap = memoryCacheUtils.getBitmapFromMemory(url);
		if(bitmap!=null){
			image.setImageBitmap(bitmap);
			return;
		}
		// ���ؼ���
		bitmap = localCacheUtils.getBitmapFromLocal(url);
		if(bitmap!=null){
			image.setImageBitmap(bitmap);
			memoryCacheUtils.setBitmapToMemory(bitmap, url);
			return;
		}
		// �������
		netCacheUtils.getBitmapFromNet(image,url);
	}
}
