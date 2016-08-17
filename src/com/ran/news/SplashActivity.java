package com.ran.news;

import com.ran.news.utils.PrefUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

public class SplashActivity extends Activity {

	private RelativeLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		layout = (RelativeLayout) findViewById(R.id.splash_layout);
		RotateAnimation rotate = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotate.setDuration(1000);
		rotate.setFillAfter(true);
		// 缩放
		ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scale.setDuration(1500);
		scale.setFillAfter(true);

		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		alpha.setDuration(1000);
		alpha.setFillAfter(true);

		AnimationSet animaSet = new AnimationSet(true);
		animaSet.addAnimation(rotate);
		animaSet.addAnimation(scale);
		animaSet.addAnimation(alpha);
		// 开始动画
		layout.startAnimation(animaSet);
		animaSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 判断跳转到引导界面还是主界面
				boolean goGuide = PrefUtils.getBoolean("goGuide", false,
						getApplicationContext());
				
				if(goGuide){
					startActivity(new Intent(getApplicationContext(),
							MainActivity.class));
				}else{
					
					startActivity(new Intent(getApplicationContext(),
							GuideActivity.class));
				}
				
				finish();
			}
		});
	}

}
