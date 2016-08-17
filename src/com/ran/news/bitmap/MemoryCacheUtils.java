package com.ran.news.bitmap;

import java.util.HashMap;

import android.graphics.Bitmap;

public class MemoryCacheUtils {
	HashMap<String, Bitmap> memoryCache = new HashMap<String, Bitmap>();

	public Bitmap getBitmapFromMemory(String url) {
		return memoryCache.get(url);
	}

	public void setBitmapToMemory(Bitmap bitmap, String url) {
		memoryCache.put(url, bitmap);
	}

}
