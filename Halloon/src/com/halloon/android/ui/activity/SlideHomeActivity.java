package com.halloon.android.ui.activity;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.halloon.android.R;
import com.halloon.android.bean.ProfileBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.DBManager;
import com.halloon.android.data.SettingsManager;
import com.halloon.android.image.FileCache;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.task.BaseCompatiableTask;
import com.halloon.android.util.Constants;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

@SuppressWarnings("deprecation")
public class SlideHomeActivity extends ActivityGroup implements
		View.OnClickListener {

	private ImageView cover;

	private MyBroadCastReceiver mBroadcastReceiver = null;

	SlidingMenu menu;
	private ViewGroup container = null;
	TextView profileUpdateHint ,mainUpdateHint,contactsUpdateHint,
	squareUpdateHint,moreUpdateHint,messageUpdateHint;
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
		RelativeLayout main = (RelativeLayout) menuView
				.findViewById(R.id.linear_main);
		RelativeLayout contacts = (RelativeLayout) menuView
				.findViewById(R.id.linear_contacts);
		RelativeLayout message = (RelativeLayout) menuView
				.findViewById(R.id.linear_message);
		RelativeLayout square = (RelativeLayout) menuView
				.findViewById(R.id.linear_square);
		RelativeLayout more = (RelativeLayout) menuView
				.findViewById(R.id.linear_more);

		square.setOnClickListener(this);
		profile.setOnClickListener(this);
		main.setOnClickListener(this);
		contacts.setOnClickListener(this);
		message.setOnClickListener(this);
		more.setOnClickListener(this);

		
		profileUpdateHint = (TextView)profile.findViewById(R.id.tv_update_hint);
		mainUpdateHint = (TextView)main.findViewById(R.id.tv_update_hint);
		contactsUpdateHint = (TextView)contacts.findViewById(R.id.tv_update_hint);
		squareUpdateHint = (TextView)square.findViewById(R.id.tv_update_hint);
		moreUpdateHint = (TextView)more.findViewById(R.id.tv_update_hint);
		messageUpdateHint = (TextView)message.findViewById(R.id.tv_update_hint);
		
		myHeadIcon = (ImageView) profile.findViewById(R.id.iv_icon);
		myNick = (TextView) profile.findViewById(R.id.tv_name);
		mySex = (ImageView) profile.findViewById(R.id.my_sex);
		loadProfile();

		container = (ViewGroup) findViewById(R.id.container);

		main.performClick();
		
		initRequestCircle();
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
		stopUpdate = true ;
	}

	
	Thread updateThread = new Thread(){

		@Override
		public void run() {
			super.run();
			while(!stopUpdate){
				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				final HashMap<String, String> map = ContentManager.getInstance(SlideHomeActivity.this).getUnreadCount("0", "");
				
				Message message = Message.obtain(handler,new Runnable() {
					private void update(TextView v,String value){
						if(value != null && value.length() > 0 && !value.equals("0")){
							v.setVisibility(View.VISIBLE);
							v.setText(value);
						}else{
							v.setVisibility(View.GONE);
						}
					}
					@Override
					public void run() {
						//修改拼音
						update(mainUpdateHint,map.get("home"));
						
						
						
						update(messageUpdateHint,map.get("private"));
						update(profileUpdateHint,map.get("fans"));
						int privat =0,mentions=0;
						try{
							privat = Integer.valueOf(map.get("private"));
						}catch (Exception e) {
							privat = 0 ;
						}
						try{
							mentions =  Integer.valueOf(map.get("mentions"));
						}catch (Exception e) {
							mentions = 0 ;
						}
						update(messageUpdateHint,(privat+mentions)+"");
//						update(profileUpdateHint,map.get("create"));
					}
				});
				message.sendToTarget();
			}
		}
		
	};
	
	private Handler handler = new Handler(){
		
	};
	
	boolean stopUpdate = false ;

	private void initRequestCircle(){
		updateThread.start();
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

	public void setSlidingEnabled(boolean enable){
		menu.setSlidingEnabled(enable); 
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
			profileUpdateHint.setVisibility(View.GONE);
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
			mainUpdateHint.setVisibility(View.GONE);
			
			intent = new Intent();
			intent.putExtra("accesstoken",
					getIntent().getStringExtra("accessToken"));
			intent.putExtra("oauth", getIntent().getSerializableExtra("oauth"));
			intent.putExtra("flag", getIntent().getIntExtra("flag", 1));
			intent.setClass(this, MainPageActivity.class);
			break;
		case R.id.linear_message:
			messageUpdateHint.setVisibility(View.GONE);
			
			intent = new Intent();
			intent.putExtra("accesstoken",
					getIntent().getStringExtra("accessToken"));
			intent.putExtra("oauth", getIntent().getSerializableExtra("oauth"));
			intent.putExtra("flag", getIntent().getIntExtra("flag", 1));
			intent.setClass(this, MyMessageActivity.class);
			break;
		case R.id.linear_square:
			 intent = new Intent();
			 intent.putExtra("accesstoken",getIntent().getStringExtra("accessToken"));
			 intent.putExtra("oauth",getIntent().getSerializableExtra("oauth"));
			 intent.putExtra("flag", getIntent().getIntExtra("flag", 1));
			 intent.setClass(this, SquareActivity.class);
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