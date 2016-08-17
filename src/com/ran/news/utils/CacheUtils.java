package com.ran.news.utils;

import android.content.Context;

public class CacheUtils {
	public static void setCache(String url, String json, Context context) {
		PrefUtils.putString(url, json, context);
	}

	public static String getCache(String url, Context context) {
		return PrefUtils.getString(url, null, context);
	}
}
