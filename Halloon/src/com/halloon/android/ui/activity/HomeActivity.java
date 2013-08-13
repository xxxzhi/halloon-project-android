package com.halloon.android.ui.activity;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.halloon.android.R;
import com.halloon.android.data.DBManager;
import com.halloon.android.data.SettingsManager;
import com.halloon.android.image.FileCache;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.util.Constants;

@SuppressWarnings("deprecation")
public class HomeActivity extends TabActivity implements OnTabChangeListener {

	private TabHost mTabHost;
	private ImageView cover;

	private MyBroadCastReceiver mBroadcastReceiver = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.fragment_home_tabs);

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
				SettingsManager.getInstance(HomeActivity.this).setDeviceIp(
						ipAddress);
			}
		}.start();

		Intent intent = new Intent();
		mTabHost = getTabHost();
		intent = new Intent();
		intent.putExtra("accesstoken", getIntent().getStringExtra("accessToken"));
		intent.putExtra("oauth", getIntent().getSerializableExtra("oauth"));
		intent.putExtra("flag", getIntent().getIntExtra("flag", 1));
		intent.setClass(this, MainPageActivity.class);
		setupTab(getString(R.string.tab_mainpage), R.drawable.ic_tab_home, intent);

		intent = new Intent();
		intent.putExtra("accesstoken", getIntent().getStringExtra("accessToken"));
		intent.putExtra("oauth", getIntent().getSerializableExtra("oauth"));
		intent.putExtra("flag", getIntent().getIntExtra("flag", 1));
		intent.setClass(this, MyMessageActivity.class);
		setupTab(getString(R.string.tab_message), R.drawable.ic_tab_msg, intent);

		intent = new Intent();
		intent.putExtra("accesstoken", getIntent().getStringExtra("accessToken"));
		intent.putExtra("oauth", getIntent().getSerializableExtra("oauth"));
		intent.putExtra("flag", getIntent().getIntExtra("flag", 1));
		intent.setClass(this, ProfileActivity.class);
		setupTab(getString(R.string.tab_profile), R.drawable.ic_tab_profile, intent);

		intent = new Intent();
		intent.putExtra("accesstoken", getIntent().getStringExtra("accessToken"));
		intent.putExtra("oauth", getIntent().getSerializableExtra("oauth"));
		intent.putExtra("flag", getIntent().getIntExtra("flag", 1));
		intent.setClass(this, MyContactsActivity.class);
		setupTab(getString(R.string.tab_contacts), R.drawable.ic_tab_contacts, intent);

		intent = new Intent();
		intent.putExtra("accesstoken", getIntent().getStringExtra("accessToken"));
		intent.putExtra("oauth", getIntent().getSerializableExtra("oauth"));
		intent.putExtra("flag", getIntent().getIntExtra("flag", 1));
		intent.setClass(this, SettingsActivity.class);
		setupTab(getString(R.string.tab_more), R.drawable.ic_tab_more, intent);

		mTabHost.setOnTabChangedListener(this);

		mTabHost.setCurrentTab(0);

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
	public void onResume(){
		super.onResume();
		DBManager.getInstance(this);
	}

	private void setupTab(final String tag, int drawable, Intent intent) {
		View tabview = createTabView(mTabHost.getContext(), tag, drawable);
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
		mTabHost.addTab(setContent);
	}

	private static View createTabView(final Context context, final String text, int drawable) {
		View view = LayoutInflater.from(context)
				.inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		ImageView iv = (ImageView) view.findViewById(R.id.icon);
		iv.setImageResource(drawable);

		return view;
	}

	public void onTabChanged(String tabId) {
	}
	
//	@Override
//	public void onPause(){
//		super.onPause();
//		DBManager.getInstance(this).closeDatabase();
//	}

	@Override
	public void onStop() {
		super.onStop();
		unregisterReceiver(mBroadcastReceiver);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(ImageLoader.getInstance(this).checkSize() >= FileCache.MAX_SIZE){
			ImageLoader.getInstance(this).clearCache();
		}
	}

	private class MyBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();

			if (bundle.getBoolean("isTabShow")) {
				mTabHost.getTabWidget().setVisibility(View.VISIBLE);
			} else {
				mTabHost.getTabWidget().setVisibility(View.GONE);
			}

			if (bundle.getBoolean("isCoverShow")) {
				
				cover.setVisibility(View.VISIBLE);
			} else {
				AlphaAnimation animation = new AlphaAnimation(1F, 0F);
				animation.setDuration(500);
				animation.setFillAfter(true);
				animation.setAnimationListener(new AnimationListener(){

					@Override
					public void onAnimationEnd(Animation animation) {
						cover.setVisibility(View.GONE);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {}

					@Override
					public void onAnimationStart(Animation animation) {}
					
				});
				if(cover.getVisibility() == View.VISIBLE) cover.setAnimation(animation);
			}
		}

	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface ntwitf = en.nextElement();
				for (Enumeration<InetAddress> enip = ntwitf.getInetAddresses(); enip.hasMoreElements();) {
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
}