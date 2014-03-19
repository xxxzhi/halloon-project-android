package com.halloon.android.ui.activity;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.json.JSONException;

import android.app.ActivityGroup;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.halloon.android.HalloonApplication;
import com.halloon.android.R;
import com.halloon.android.bean.ProfileBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.DBManager;
import com.halloon.android.data.SettingsManager;
import com.halloon.android.image.FileCache;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.task.BaseCompatiableTask;
import com.halloon.android.util.Constants;
import com.halloon.android.util.ContentTransUtil;
import com.halloon.android.util.NumberUtil;
import com.halloon.android.util.TimeUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

@SuppressWarnings("deprecation")
public class SlideHomeActivity extends ActivityGroup implements
		View.OnClickListener {

	private ImageView cover;

	private MyBroadCastReceiver mBroadcastReceiver = null;

	SlidingMenu menu;
	private ViewGroup container = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_group_main);

		cover = (ImageView) findViewById(R.id.cover);

		// get screen measurement
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		SettingsManager.getInstance(this).setScreenWidth(dm.widthPixels);
		SettingsManager.getInstance(this).setScreenHeight(dm.heightPixels);
		SettingsManager.getInstance(this).setScreenDensity(dm.scaledDensity);
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SettingsManager.getInstance(this).setSystemBarHeight(sbar);

		// get ip address

		new Thread() {
			@Override
			public void run() {
				String ipAddress = getLocalIpAddress();
				SettingsManager.getInstance(SlideHomeActivity.this)
						.setDeviceIp(ipAddress);
			}
		}.start();

		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setBehindWidth(R.dimen.slide_width);
		// menu.setShadowWidthRes(R.dimen.shadow_width);
		// menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slide_width);
		// menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);

		View menuView = getLayoutInflater().inflate(R.layout.slidingmenu, null);
		menu.setMenu(menuView);

		// init slidemenu
		RelativeLayout profile = (RelativeLayout) menuView
				.findViewById(R.id.relative_profile);
		LinearLayout main = (LinearLayout) menuView
				.findViewById(R.id.linear_main);
		LinearLayout contacts = (LinearLayout) menuView
				.findViewById(R.id.linear_contacts);
		LinearLayout message = (LinearLayout) menuView
				.findViewById(R.id.linear_message);
		LinearLayout square = (LinearLayout) menuView
				.findViewById(R.id.linear_square);
		LinearLayout more = (LinearLayout) menuView
				.findViewById(R.id.linear_more);

		square.setOnClickListener(this);
		profile.setOnClickListener(this);
		main.setOnClickListener(this);
		contacts.setOnClickListener(this);
		message.setOnClickListener(this);
		more.setOnClickListener(this);

		myHeadIcon = (ImageView) profile.findViewById(R.id.iv_icon);
		myNick = (TextView) profile.findViewById(R.id.tv_name);
		mySex = (ImageView) profile.findViewById(R.id.my_sex);
		loadProfile();

		container = (ViewGroup) findViewById(R.id.container);

		main.performClick();
	}

	@Override
	public void onStart() {
		super.onStart();
		mBroadcastReceiver = new MyBroadCastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.GLOBAL_TAB_VISIBILITY);
		registerReceiver(mBroadcastReceiver, filter);
	}

	@Override
	public void onResume() {
		super.onResume();
		DBManager.getInstance(this);
	}

	public void onTabChanged(String tabId) {
	}

	// @Override
	// public void onPause(){
	// super.onPause();
	// DBManager.getInstance(this).closeDatabase();
	// }

	@Override
	public void onStop() {
		super.onStop();
		unregisterReceiver(mBroadcastReceiver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (ImageLoader.getInstance(this).checkSize() >= FileCache.MAX_SIZE) {
			ImageLoader.getInstance(this).clearCache();
		}
	}

	private class MyBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();

			if (bundle.getBoolean("isCoverShow")) {
				if (cover != null)
					cover.setVisibility(View.VISIBLE);
			} else {
				AlphaAnimation animation = new AlphaAnimation(1F, 0F);
				animation.setDuration(500);
				animation.setFillAfter(true);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationEnd(Animation animation) {
						cover.setVisibility(View.GONE);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationStart(Animation animation) {
					}

				});
				if (cover != null) {
					if (cover.getVisibility() == View.VISIBLE)
						cover.setAnimation(animation);
				}
			}
		}

	}

	public void toggleSlideMenu() {
		menu.toggle(true);
	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface ntwitf = en.nextElement();
				for (Enumeration<InetAddress> enip = ntwitf.getInetAddresses(); enip
						.hasMoreElements();) {
					InetAddress inetAddress = enip.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return null;
	}

	ProfileBean profileBean;
	private ImageView myHeadIcon = null;
	private TextView myNick = null;
	private ImageView mySex = null;

	private void loadProfile() {
		new BaseCompatiableTask<Void, Void, ProfileBean>() {

			@Override
			protected ProfileBean doInBackground(Void... arg0) {
				ProfileBean tmp_profileBean;

				if (SettingsManager.getInstance(SlideHomeActivity.this)
						.getProfileStatus() == DBManager.PROFILE_STATUS_INIT) {
					tmp_profileBean = ContentManager.getInstance(
							SlideHomeActivity.this).getMyProfile();
				} else {
					tmp_profileBean = DBManager.getInstance(
							SlideHomeActivity.this).getProfile();
					if (tmp_profileBean.getName() == null
							|| tmp_profileBean.getTweetBean() == null) {
						tmp_profileBean = ContentManager.getInstance(
								SlideHomeActivity.this).getMyProfile();
					}
				}

				return tmp_profileBean;
			}

			@Override
			protected void onPostExecute(ProfileBean result) {

				if (result != null) {
					profileBean = result;
					DBManager.getInstance(SlideHomeActivity.this)
							.upgradeProfile(profileBean);
					ImageLoader.getInstance(SlideHomeActivity.this)
							.displayImage(profileBean.getHead() + "/100",
									myHeadIcon, 1, null);
					myNick.setText(profileBean.getNick());
					if (profileBean.getSex() != null) {
						if (profileBean.getSex().equals("男")) {
							mySex.setVisibility(View.VISIBLE);
							mySex.setImageResource(R.drawable.wb_icon_male);
						} else if (profileBean.getSex().equals("女")) {
							mySex.setVisibility(View.VISIBLE);
							mySex.setImageResource(R.drawable.wb_icon_female);
						} else {
							mySex.setVisibility(View.GONE);
						}
					}

					// mySign.setText(profileBean.getIntroduction());
					// tweetButton.setText(context.getString(R.string.wblog)
					// + "\n"
					// + NumberUtil.shortenNumericString(context,
					// profileBean.getTweetNum()));
					// idolButton.setText(context.getString(R.string.idol)
					// + "\n"
					// + NumberUtil.shortenNumericString(context,
					// profileBean.getIdolNum()));
					// fanButton.setText(context.getString(R.string.fan)
					// + "\n"
					// + NumberUtil.shortenNumericString(context,
					// profileBean.getFansNum()));
					// favButton.setText(context.getString(R.string.fav)
					// + "\n"
					// + NumberUtil.shortenNumericString(context,
					// profileBean.getFavNum()));
					//
					// tagView.setContents(profileBean.getTag());

				}
				super.onPostExecute(result);
			}
		}.taskExecute();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.relative_profile:

			intent = new Intent();
			intent.putExtra("accesstoken",
					getIntent().getStringExtra("accessToken"));
			intent.putExtra("oauth", getIntent().getSerializableExtra("oauth"));
			intent.putExtra("flag", getIntent().getIntExtra("flag", 1));
			intent.setClass(this, ProfileActivity.class);

			break;
		case R.id.linear_contacts:
			intent = new Intent();
			intent.putExtra("accesstoken",
					getIntent().getStringExtra("accessToken"));
			intent.putExtra("oauth", getIntent().getSerializableExtra("oauth"));
			intent.putExtra("flag", getIntent().getIntExtra("flag", 1));
			intent.setClass(this, MyContactsActivity.class);

			break;
		case R.id.linear_main:
			intent = new Intent();
			intent.putExtra("accesstoken",
					getIntent().getStringExtra("accessToken"));
			intent.putExtra("oauth", getIntent().getSerializableExtra("oauth"));
			intent.putExtra("flag", getIntent().getIntExtra("flag", 1));
			intent.setClass(this, MainPageActivity.class);
			break;
		case R.id.linear_message:
			intent = new Intent();
			intent.putExtra("accesstoken",
					getIntent().getStringExtra("accessToken"));
			intent.putExtra("oauth", getIntent().getSerializableExtra("oauth"));
			intent.putExtra("flag", getIntent().getIntExtra("flag", 1));
			intent.setClass(this, MyMessageActivity.class);
			break;
		case R.id.linear_square:
			// intent = new Intent();
			// intent.putExtra("accesstoken",
			// getIntent().getStringExtra("accessToken"));
			// intent.putExtra("oauth",
			// getIntent().getSerializableExtra("oauth"));
			// intent.putExtra("flag", getIntent().getIntExtra("flag", 1));
			// intent.setClass(this, MyMessageActivity.class);
			break;
		case R.id.linear_more:
			intent = new Intent();
			intent.putExtra("accesstoken",
					getIntent().getStringExtra("accessToken"));
			intent.putExtra("oauth", getIntent().getSerializableExtra("oauth"));
			intent.putExtra("flag", getIntent().getIntExtra("flag", 1));
			intent.setClass(this, SettingsActivity.class);

			break;
		}
		if (intent == null)
			return;

		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		container.removeAllViews();
		container.addView(getLocalActivityManager().startActivity("Module1",
				intent).getDecorView());
		if (menu.isShown()) {
			menu.toggle(true);
		}
	}
}