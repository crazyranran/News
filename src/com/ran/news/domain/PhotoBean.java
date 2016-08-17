package com.ran.news.domain;

import java.util.ArrayList;

/**
 * 组图对象封装
 * 
 * @author asus
 *
 */
public class PhotoBean {
	public int retcode;
	public PhotoData data;

	public class PhotoData {
		public ArrayList<PhotoNewsData> news;
	}
	public class PhotoNewsData{
		public String id;
		public String listimage;
		public String pubdate;
		public String title;
		public String url;
	}
}
