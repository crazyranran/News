package com.ran.news.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import com.ran.news.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView;

/**
 * ��������ˢ�µ�ListView
 * 
 * @author asus
 *
 */
public class RefreshListView extends ListView implements
		AbsListView.OnScrollListener,
		android.widget.AdapterView.OnItemClickListener {

	private static final int STATE_PUUL_TO_REFRESH = 1;// ����ˢ��
	private static final int STATE_RELISE_TO_REFRESH = 2;// �ɿ�ˢ��
	private static final int STATE_REFRESHING = 3;// ����ˢ��

	private int measuredHeight;// ͷ���ָ߶�
	private int startY = -1;
	private View mHeaderView;// ͷ����
	private View mFooterView;// �Ų���

	private OnRefreshListener mListener;// �ص��ӿ�
	private int footViewHeight;// �Ų��ָ߶�

	private int mCurrentState = STATE_PUUL_TO_REFRESH;// Ĭ��������ˢ�µ�״̬

	private TextView tvTitle;// ״̬
	private ImageView ivArrow;// ��ͷ
	private TextView tvTime;// ʱ��
	private ProgressBar pbRefresh;// ������

	private RotateAnimation animUp;// ��ͷ���϶���
	private RotateAnimation animDown;// ��ͷ���¶���

	private boolean isLoadingMore;// ����Ƿ����ڼ��ظ���

	public RefreshListView(Context context) {
		super(context);
		initHeaderView();
		initFooterView();
	}

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initHeaderView();
		initFooterView();
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeaderView();
		initFooterView();
	}

	public void initHeaderView() {
		mHeaderView = View.inflate(getContext(), R.layout.list_header_refresh,
				null);
		this.addHeaderView(mHeaderView);// ���ͷ����

		// ����ͷ���֣�paddingTop=-���ָ߶ȣ��ͻ������ߣ���������
		// �ò����߶ȣ���û�л�����
		// ����֮ǰ��Ҫ��ȡ�߶ȣ��ֶ�����

		mHeaderView.measure(0, 0);
		measuredHeight = mHeaderView.getMeasuredHeight();// ������ͷ���ֵĸ߶�
		mHeaderView.setPadding(0, -measuredHeight, 0, 0);

		tvTitle = (TextView) mHeaderView.findViewById(R.id.tv_title_refresh);
		ivArrow = (ImageView) mHeaderView.findViewById(R.id.iv_arrow);
		tvTime = (TextView) mHeaderView.findViewById(R.id.tv_time_refresh);
		pbRefresh = (ProgressBar) mHeaderView.findViewById(R.id.pb_refresh);
		initAnim();// ��ʼ������

		setCurrentTime();// ���õ�ǰʱ��
	}

	/**
	 * ��ʼ���Ų���
	 */
	private void initFooterView() {
		mFooterView = View.inflate(getContext(), R.layout.list_footer_refresh,
				null);
		this.addFooterView(mFooterView);
		mFooterView.measure(0, 0);
		footViewHeight = mFooterView.getMeasuredHeight();
		mFooterView.setPadding(0, -footViewHeight, 0, 0);// ���ؽŲ���
		// ���û�������
		this.setOnScrollListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			// �����סͷ�����Ż�����Listview�ò���down�¼������»�ȡ
			if (startY == -1) {
				startY = (int) ev.getY();
			}
			// ״̬������ˢ�£������κδ�����listview����
			if (mCurrentState == STATE_REFRESHING) {
				break;
			}
			int endY = (int) ev.getY();
			int dy = endY - startY;

			if (dy > 0 && getFirstVisiblePosition() == 0) {
				// ���»���������ʾ���ǵ�һλ�òŸı�padding����ʾ����ˢ��
				int paddingTop = dy - measuredHeight;
				// ����padding��ֵ�л�״̬
				if (paddingTop >= 0 && mCurrentState != STATE_RELISE_TO_REFRESH) {
					// �ɿ�ˢ��
					mCurrentState = STATE_RELISE_TO_REFRESH;
					refreshState();
				} else if (paddingTop < 0
						&& mCurrentState != STATE_PUUL_TO_REFRESH) {
					// ����ˢ��
					mCurrentState = STATE_PUUL_TO_REFRESH;
					refreshState();
				}
				mHeaderView.setPadding(0, paddingTop, 0, 0);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			startY = -1;// ��ʼ�������
			if (mCurrentState == STATE_RELISE_TO_REFRESH) {
				// �����ǰ��״̬���ɿ�ˢ�£��Ǿ��л�������ˢ�£�������ʾͷ����
				mCurrentState = STATE_REFRESHING;
				mHeaderView.setPadding(0, 0, 0, 0);
				refreshState();
				// ˢ�»ص�
				if (mListener != null) {
					mListener.onRefresh();
				}
			} else if (mCurrentState == STATE_PUUL_TO_REFRESH) {
				// �����ǰ��״̬��������ˢ�£�����ͷ����
				mHeaderView.setPadding(0, -measuredHeight, 0, 0);
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * ����״̬ˢ������
	 */
	private void refreshState() {
		switch (mCurrentState) {
		// ����ˢ��״̬
		case STATE_PUUL_TO_REFRESH:
			tvTitle.setText("����ˢ��");
			// ��ͷ�����ƶ�����
			ivArrow.startAnimation(animDown);// ��ͷ�����ƶ�
			pbRefresh.setVisibility(View.INVISIBLE);// ���ؽ�����
			break;
		// �ɿ�ˢ��״̬
		case STATE_RELISE_TO_REFRESH:
			tvTitle.setText("�ɿ�ˢ��");
			ivArrow.startAnimation(animUp);// ��ͷ�����ƶ�
			pbRefresh.setVisibility(View.INVISIBLE);// ���ؽ�����
			break;
		// ����ˢ��״̬
		case STATE_REFRESHING:
			tvTitle.setText("����ˢ��...");
			pbRefresh.setVisibility(View.VISIBLE);// ��ʾ������
			ivArrow.clearAnimation();// ���������������ؿؼ�
			ivArrow.setVisibility(View.INVISIBLE);// ���ؼ�ͷ
			break;
		default:
			break;
		}
	}

	public void initAnim() {
		animUp = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);// ��ͷ���϶���
		animUp.setDuration(500);
		animUp.setFillAfter(true);

		animDown = new RotateAnimation(-180, -0, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animDown.setDuration(500);
		animDown.setFillAfter(true);// ����״̬
	}

	public void setOnRefreshListener(OnRefreshListener listener) {
		mListener = listener;
	}

	public void setCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		String time = format.format(new Date());
		tvTime.setText(time);
	}

	// ˢ����ɺ�ص��������
	public void onRefreshCompleted(boolean success) {
		if (!isLoadingMore) {
			// ����ͷ����
			mHeaderView.setPadding(0, -measuredHeight, 0, 0);
			mCurrentState = STATE_PUUL_TO_REFRESH;
			pbRefresh.setVisibility(View.INVISIBLE);
			ivArrow.setVisibility(View.INVISIBLE);
			tvTitle.setText("����ˢ��");
			// ˢ�³ɹ�������ʱ��
			if (success) {
				setCurrentTime();
			}
		} else {
			// ���ؽŲ���
			mFooterView.setPadding(0, -footViewHeight, 0, 0);
			isLoadingMore = false;
		}
	}

	public interface OnRefreshListener {
		// ����ˢ�»ص�
		public void onRefresh();

		public void loadMore();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {// ���һ��idleλ�úͼ��ϴ�С��Ⱦ������һ��idle
			int lastVisiblePosition = getLastVisiblePosition();// ��ǰ��ʾ�����һ��λ��
			if (lastVisiblePosition >= getCount() - 1 && !isLoadingMore) {

				isLoadingMore = true;
				mFooterView.setPadding(0, 0, 0, 0);// ���ظ��ࡣ��������ʾ�Ų���
				// ���õ�ǰҪչʾ��item��λ�ã�ֱ����ʾ���ظ������item��λ����ʾ
				setSelection(getCount() - 1);
				if (mListener != null) {
					mListener.loadMore();
				}
			}

		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	private OnItemClickListener mItemOnClickListener;

	// ��д����¼���)����
	@Override
	public void setOnItemClickListener(
			android.widget.AdapterView.OnItemClickListener listener) {
		mItemOnClickListener = listener;
		super .setOnItemClickListener(this);// ������¼����ø�RefreshListView
	}

	// ��position��ȥͷ��������
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mItemOnClickListener != null) {
			mItemOnClickListener.onItemClick(parent, view, position
					- getHeaderViewsCount(), id);
		}
	}

}
