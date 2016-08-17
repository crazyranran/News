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

	private NewsMenuData newsMenuData;// ���ŷ�����Ϣ��������
	public ArrayList<BaseMenuDetailPager> menuDetailsPagers;

	public NewsCenterPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		tvTitle.setText("����");
	
		
		//���Ƿ��л���
		String cache = CacheUtils.getCache(Constants.CATGORIES_URL, mActivity);
		if(!TextUtils.isEmpty(cache)){
			//�л��棬ֱ�ӽ���
			processJson(cache);
		}
			//��ʹ�����л���Ҳ�����������ϻ�ȡ����������֤��������
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
						//�ɹ���ȡ�������ݺ�д�뻺��
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

		// ��ʼ���˵�����ҳ��
		menuDetailsPagers = new ArrayList<BaseMenuDetailPager>();
		menuDetailsPagers.add(new NewsMenuDetailPager(mActivity,newsMenuData.data.get(0).children));
		menuDetailsPagers.add(new TopicMenuDetailPager(mActivity));
		menuDetailsPagers.add(new PhotosMenuDetailPager(mActivity,btnDisplay));
		menuDetailsPagers.add(new InteractMenuDetailPager(mActivity));
		
		// �ֶ��л�����������ҳ��
		setCurrentMenuDetailPager(0);

		// ͨ�����mainActivity���ȥ��ȡ��������󲢸�����������
		MainActivity mainUI = (MainActivity) mActivity;
		LeftMenuFragment leftMenuFragment = mainUI.getLeftMenuFragment();
		// ��������
		leftMenuFragment.setData(newsMenuData.data);
	}

	// ��������ҳ���FrameLayout��䲼��
	protected void setCurrentMenuDetailPager(int position) {
		BaseMenuDetailPager pager = menuDetailsPagers.get(position);
		// �Ƴ�֮ǰ���е�view����, ������Ļ
		flContent.removeAllViews();
		flContent.addView(pager.mRootView);
		//�л�����ʱ���ʼ������
		pager.initData();//��ʼ������
		//���ı���
		tvTitle.setText(newsMenuData.data.get(position).title);
		if (pager instanceof PhotosMenuDetailPager) {
			btnDisplay.setVisibility(View.VISIBLE);
		}else{
			btnDisplay.setVisibility(View.GONE);
		}
	}
}
