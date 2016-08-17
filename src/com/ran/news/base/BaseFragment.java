package com.ran.news.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * Fragment的基类
 * @author rlp
 *	1.初始化布局
 *	2.初始化数据
 */
public abstract class BaseFragment extends Fragment {
	public Activity mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = initView();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
	}

	/**
	 * 初始化数据
	 */
	public void initData() {

	}

	/**
	 * 初始化视图，子类必须实现
	 * 
	 * @return
	 */
	public abstract View initView();
}
