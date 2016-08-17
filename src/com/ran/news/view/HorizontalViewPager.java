package com.ran.news.view;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HorizontalViewPager extends ViewPager {

	private int startX;
	private int startY;

	public HorizontalViewPager(Context context) {
		super(context);
	}

	public HorizontalViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 分情况觉得父控件是否需要拦截 1.上下滑动需要拦截 2.向右滑动且为第一个页面时，需要拦截 3.向左滑动且为最后一个页面，需要拦截
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = (int) ev.getX();
			startY = (int) ev.getY();
			// 按下时先请求父控件不要拦截
			getParent().requestDisallowInterceptTouchEvent(true);
			break;
		case MotionEvent.ACTION_MOVE:
			int endX = (int) ev.getX();
			int endY = (int) ev.getY();

			int dx = endX - startX;
			int dy = endY - startY;
			if (Math.abs(dx) > Math.abs(dy)) {
				// 左右滑动
				if (dx > 0) {
					// 向右滑动
					if (this.getCurrentItem() == 0) {
						// 第一个页面，拦截
						getParent().requestDisallowInterceptTouchEvent(false);

					} else {// 向左滑动
							// 最后一个页面，拦截
						if (getCurrentItem() == this.getAdapter().getCount() - 1) {
							getParent().requestDisallowInterceptTouchEvent(
									false);
						}
					}
				}
			} else {
				// 上下滑动，拦截
				getParent().requestDisallowInterceptTouchEvent(false);

			}
			break;

		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

}
