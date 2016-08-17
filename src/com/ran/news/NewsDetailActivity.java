package com.ran.news;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * ��������ҳ��
 * 
 * @author asus
 *
 */
public class NewsDetailActivity extends Activity implements OnClickListener {
	@ViewInject(R.id.ll_controller)
	LinearLayout llController;// ���Ʒ����������С��ll
	@ViewInject(R.id.btn_back)
	// ���ذ�ť
	private ImageButton btnBack;
	@ViewInject(R.id.img_menu)
	// �˵���ť
	private ImageButton btnMenu;
	@ViewInject(R.id.btn_share)
	// ����ť
	private ImageButton btnShare;
	@ViewInject(R.id.btn_textsize)
	// �����С��ť
	private ImageButton btnTextSize;
	@ViewInject(R.id.wv_webview)
	private WebView mWebView;

	@ViewInject(R.id.pb_loading)
	private ProgressBar pbLoading;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_news_detail);
		ViewUtils.inject(this);

		btnBack.setVisibility(View.VISIBLE);
		btnMenu.setVisibility(View.INVISIBLE);
		llController.setVisibility(View.VISIBLE);

		btnBack.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		btnTextSize.setOnClickListener(this);

		url = getIntent().getStringExtra("url");

		mWebView.loadUrl(url);// ������ҳ

		WebSettings settings = mWebView.getSettings();
		settings.setBuiltInZoomControls(true);// ��ʾ�Ŵ���С
		settings.setUseWideViewPort(true);// ��ʾ˫������
		settings.setJavaScriptEnabled(true);// ��JS����

		// ��WebView���ü���
		mWebView.setWebViewClient(new WebViewClient() {
			// ��ҳ���ؿ�ʼ
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				System.out.println("��ҳ���ؿ�ʼ");
				pbLoading.setVisibility(View.VISIBLE);

			}

			// ��ҳ���ؽ���
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				System.out.println("��ҳ���ؽ���");
				// pbLoading.setVisibility(View.INVISIBLE);
			}

			// ��ҳ��ת�����������
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				System.out.println("��ҳ��ת");
				view.loadUrl(url);// ǿ���ڵ�ǰҳ����أ�����ת�����
				return true;
			}
		});

		mWebView.setWebChromeClient(new WebChromeClient() {
			// ���ؽ��ȵĻص�
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress >= 80) {
					pbLoading.setVisibility(View.INVISIBLE);
				}
				System.out.println("���ؽ���" + newProgress);
			}

			// ��ȡ��ҳ�ı���
			@Override
			public void onReceivedTitle(WebView view, String title) {
				// TODO Auto-generated method stub
				super.onReceivedTitle(view, title);
			}

			// ��ȡ��ҳ��ͼ��
			@Override
			public void onReceivedIcon(WebView view, Bitmap icon) {
				// TODO Auto-generated method stub
				super.onReceivedIcon(view, icon);
			}
		});
	}

	// E/Web Console(2403): Uncaught Code.PhotoSwipe.createInstance: No images
	// to passed. at
	// http://10.0.2.2:8080/zhbj/10007/724D6A55496A11726628_files/code.photoswipe-3.0.5.min.js:133

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_share:
			showShare();
			break;
		case R.id.btn_textsize:
			showChooseDialog();
			break;
		default:
			break;
		}
	}

	private int mChooseItem;// �û�ѡ���ѡ��
	private int mSelectItem = 2;// ����ѡ���ѡ��

	private void showChooseDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��������");
		String[] items = new String[] { "��������", "�������", "��������", "С������", "��С������" };
		builder.setSingleChoiceItems(items, mSelectItem,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mChooseItem = which;

					}
				});

		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			WebSettings settings = mWebView.getSettings();

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (mChooseItem) {
				case 0:
					settings.setTextSize(TextSize.LARGEST);
					break;
				case 1:
					settings.setTextSize(TextSize.LARGER);
					break;
				case 2:
					settings.setTextSize(TextSize.NORMAL);
					break;
				case 3:
					settings.setTextSize(TextSize.SMALLER);
					break;
				case 4:
					settings.setTextSize(TextSize.SMALLEST);
					break;

				default:
					break;
				}
				mSelectItem = which;
			}
		});
		builder.setNegativeButton("ȡ��", null);
		builder.show();
	}

	/**
	 * ȷ��SDcard������ڴ���ͼƬ
	 */
	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();

		// �޸�����
		oks.setTheme(OnekeyShareTheme.SKYBLUE);

		// �ر�sso��Ȩ
		oks.disableSSOWhenAuthorize();

		// ����ʱNotification��ͼ������� 2.5.9�Ժ�İ汾�����ô˷���
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
		oks.setTitle(getString(R.string.share));
		// titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
		oks.setTitleUrl("http://sharesdk.cn");
		// text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
		oks.setText("���Ƿ����ı�");
		// imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
		oks.setImagePath("/sdcard/test.jpg");// ȷ��SDcard������ڴ���ͼƬ
		// url����΢�ţ��������Ѻ�����Ȧ����ʹ��
		oks.setUrl("http://sharesdk.cn");
		// comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
		oks.setComment("���ǲ��������ı�");
		// site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
		oks.setSite(getString(R.string.app_name));
		// siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
		oks.setSiteUrl("http://sharesdk.cn");

		// ��������GUI
		oks.show(this);
	}

}
