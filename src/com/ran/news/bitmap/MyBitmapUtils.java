package com.ran.news.bitmap;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 图片三级缓存工具类
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
	//从内存加载，没有就从本地加载，没有再从网络加载
	public  void display(ImageView image, String url) {
		// 内存加载
		Bitmap bitmap = memoryCacheUtils.getBitmapFromMemory(url);
		if(bitmap!=null){
			image.setImageBitmap(bitmap);
			return;
		}
		// 本地加载
		bitmap = localCacheUtils.getBitmapFromLocal(url);
		if(bitmap!=null){
			image.setImageBitmap(bitmap);
			memoryCacheUtils.setBitmapToMemory(bitmap, url);
			return;
		}
		// 网络加载
		netCacheUtils.getBitmapFromNet(image,url);
	}
}
