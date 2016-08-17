package com.ran.news.base.impl;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.ran.news.MainActivity;
import com.ran.news.base.BaseMenuDetailPager;
import com.ran.news.base.BasePager;
import com.ran.news.base.impl.menuDetail.InteractMenuDetailPager;
import com.ran.news.base.impl.menuDetail.NewsMenuDetailPager;
import com.ran.news.base.impl.menuDetail.PhotosMenuDetailPager;
import com.ran.news.base.impl.menuDetail.TopicMenuDetailPager;
import com.ran.news.domain.NewsMenuData;
import com.ran.news.global.Constants;
import com.ran.news.utils.CacheUtils;

public class NewsCenterPager extends BasePager {

	private NewsMenuData newsMenuData;// 新闻分类信息网络数据
	public ArrayList<BaseMenuDetailPager> menuDetailsPagers;

	public NewsCenterPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		tvTitle.setText("新闻");
	
		
		//看是否有缓存
		String cache = CacheUtils.getCache(Constants.CATGORIES_URL, mActivity);
		if(!TextUtils.isEmpty(cache)){
			//有缓存，直接解析
			processJson(cache);
		}
			//即使发现有缓存也继续从网络上获取并解析，保证最新数据
			getDataFromServer();
		
	}

	public void getDataFromServer() {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, Constants.CATGORIES_URL,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;
						// System.out.println(result);
						processJson(result);
						//成功获取网络数据后写入缓存
						CacheUtils.setCache(Constants.CATGORIES_URL, result, mActivity);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						error.printStackTrace();
						Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT)
								.show();
					}
				});
	}

	private void processJson(String result) {

		Gson gson = new Gson();
		newsMenuData = gson.fromJson(result, NewsMenuData.class);

		// 初始化菜单详情页面
		menuDetailsPagers = new ArrayList<BaseMenuDetailPager>();
		menuDetailsPagers.add(new NewsMenuDetailPager(mActivity,newsMenuData.data.get(0).children));
		menuDetailsPagers.add(new TopicMenuDetailPager(mActivity));
		menuDetailsPagers.add(new PhotosMenuDetailPager(mActivity,btnDisplay));
		menuDetailsPagers.add(new InteractMenuDetailPager(mActivity));
		
		// 手动切换到新闻详情页面
		setCurrentMenuDetailPager(0);

		// 通过获得mainActivity间接去获取侧边栏对象并给他设置数据
		MainActivity mainUI = (MainActivity) mActivity;
		LeftMenuFragment leftMenuFragment = mainUI.getLeftMenuFragment();
		// 设置数据
		leftMenuFragment.setData(newsMenuData.data);
	}

	// 新闻中心页面的FrameLayout填充布局
	protected void setCurrentMenuDetailPager(int position) {
		BaseMenuDetailPager pager = menuDetailsPagers.get(position);
		// 移除之前所有的view对象, 清理屏幕
		flContent.removeAllViews();
		flContent.addView(pager.mRootView);
		//切换到的时候初始化数据
		pager.initData();//初始化数据
		//更改标题
		tvTitle.setText(newsMenuData.data.get(position).title);
		if (pager instanceof PhotosMenuDetailPager) {
			btnDisplay.setVisibility(View.VISIBLE);
		}else{
			btnDisplay.setVisibility(View.GONE);
		}
	}
}
