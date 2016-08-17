package com.ran.news.bitmap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.sharesdk.wechat.utils.m;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class NetCacheUtils {

	private ImageView image;
	private String url;
	LocalCacheUtils mLocalCacheUtils;
	MemoryCacheUtils mMemoryCacheUtils;

	public NetCacheUtils(LocalCacheUtils localCacheUtils,MemoryCacheUtils memoryCacheUtils) {
		mLocalCacheUtils=localCacheUtils;
		mMemoryCacheUtils = memoryCacheUtils;
	}

	/**
	 * 从网络加载图片
	 * 
	 * @param image
	 * @param url
	 */
	public void getBitmapFromNet(ImageView image, String url) {
		BitmapTask task = new BitmapTask();
		task.execute(image, url);

	}

	class BitmapTask extends AsyncTask<Object, Integer, Bitmap> {

		// 在子线程运行，实现异步加载
		@Override
		protected Bitmap doInBackground(Object... params) {
			image = (ImageView) params[0];
			url = (String) params[1];

			image.setTag(url);// 将imageview和url绑定在一起
			return download(url);
		}

		// 主线程执行，更新进度
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		// 主线程运行,更新主界面
		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				String bindUrl = (String) image.getTag();
				if (bindUrl.equals(url)) {
					image.setImageBitmap(result);//将bitmap设置给imageview
					//将bitmap存到本地
					mLocalCacheUtils.setBitmapToLocal(result, url);
					//将bitmap保存到内存
					mMemoryCacheUtils.setBitmapToMemory(result, url);
					
				}

			}

		}

	}

	public Bitmap download(String url) {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.setRequestMethod("GET");

			connection.connect();

			int responseCode = connection.getResponseCode();
			if (responseCode == 200) {
				InputStream in = connection.getInputStream();
				// 将流转换成bitmap
				Bitmap bitmap = BitmapFactory.decodeStream(in);
				return bitmap;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return null;
	}
}
