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
	 * ��������ø��ؼ��Ƿ���Ҫ���� 1.���»�����Ҫ���� 2.���һ�����Ϊ��һ��ҳ��ʱ����Ҫ���� 3.���󻬶���Ϊ���һ��ҳ�棬��Ҫ����
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = (int) ev.getX();
			startY = (int) ev.getY();
			// ����ʱ�����󸸿ؼ���Ҫ����
			getParent().requestDisallowInterceptTouchEvent(true);
			break;
		case MotionEvent.ACTION_MOVE:
			int endX = (int) ev.getX();
			int endY = (int) ev.getY();

			int dx = endX - startX;
			int dy = endY - startY;
			if (Math.abs(dx) > Math.abs(dy)) {
				// ���һ���
				if (dx > 0) {
					// ���һ���
					if (this.getCurrentItem() == 0) {
						// ��һ��ҳ�棬����
						getParent().requestDisallowInterceptTouchEvent(false);

					} else {// ���󻬶�
							// ���һ��ҳ�棬����
						if (getCurrentItem() == this.getAdapter().getCount() - 1) {
							getParent().requestDisallowInterceptTouchEvent(
									false);
						}
					}
				}
			} else {
				// ���»���������
				getParent().requestDisallowInterceptTouchEvent(false);

			}
			break;

		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

}
