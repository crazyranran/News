package com.ran.news;

import java.util.ArrayList;
import java.util.List;

import com.ran.news.utils.PrefUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class GuideActivity extends Activity implements OnClickListener {
	private ViewPager pager;
	private int imageId[] = new int[] { R.drawable.guide_1, R.drawable.guide_2,
			R.drawable.guide_3 };
	private List<ImageView> imageList = new ArrayList<ImageView>();
	private LinearLayout pointLayout;
	private int mPointWidth;
	private ImageView ivRedPoint;// 小红点
	private Button startBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide);

		pager = (ViewPager) findViewById(R.id.guide_view_pager);
		pointLayout = (LinearLayout) findViewById(R.id.point_container);
		ivRedPoint = (ImageView) findViewById(R.id.red_circle);
		startBtn = (Button) findViewById(R.id.guide_start);
		startBtn.setOnClickListener(this);
		for (int i = 0; i < imageId.length; i++) {
			ImageView view = new ImageView(this);
			view.setBackgroundResource(imageId[i]);
			imageList.add(view);

			// 初始化圆圈

			ImageView pointView = new ImageView(this);
			pointView.setBackgroundResource(R.drawable.default_circle_shape);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			if (i > 0) {
				params.leftMargin = 8;
			}
			pointView.setLayoutParams(params);
			pointLayout.addView(pointView);
		}

		pager.setAdapter(new MyAdapter());

		ivRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						ivRedPoint.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
						mPointWidth = pointLayout.getChildAt(1).getLeft()
								- pointLayout.getChildAt(0).getLeft();
					}
				});
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (position == imageId.length - 1) {
					startBtn.setVisibility(View.VISIBLE);
				} else {
					startBtn.setVisibility(View.GONE);
				}
			}

			// 页面滑动过程中一直调用
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int arg2) {
				int leftMargin = (int) (mPointWidth * positionOffset + position
						* mPointWidth);
				RelativeLayout.LayoutParams params = (LayoutParams) ivRedPoint
						.getLayoutParams();
				params.leftMargin = leftMargin;

				ivRedPoint.setLayoutParams(params);
			}

			@Override
			public void onPageScrollStateChanged(int position) {

			}
		});
	}

	class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageId.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView view = imageList.get(position);
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.guide_start:
			PrefUtils.putBoolean("goGuide", true, this);
			startActivity(new Intent(this, MainActivity.class));
			finish();
			break;

		default:
			break;
		}
	}
}
