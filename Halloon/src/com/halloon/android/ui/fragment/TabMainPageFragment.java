package com.halloon.android.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;

import com.halloon.android.HalloonApplication;
import com.halloon.android.R;
import com.halloon.android.adapter.TweetContentAdapter;
import com.halloon.android.bean.ProfileBean;
import com.halloon.android.bean.TweetBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.DBManager;
import com.halloon.android.data.SettingsManager;
import com.halloon.android.task.BaseCompatiableTask;
import com.halloon.android.task.LocationTask;
import com.halloon.android.listener.OnLocationSeekListener;
import com.halloon.android.listener.OnTitleBarClickListener;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;
import com.halloon.android.util.Constants;
import com.halloon.android.util.PopupWindowManager;
import com.halloon.android.util.TimeUtil;
import com.halloon.android.widget.HalloonPullableView;
import com.halloon.android.widget.HalloonPullableView.OnHeaderRefreshListener;
import com.halloon.android.widget.HalloonPullableView.OnFooterRefreshListener;
import com.halloon.android.widget.HalloonTitleBar;

public class TabMainPageFragment extends BaseTitleBarFragment implements OnTitleBarClickListener,
                                                                         OnHeaderRefreshListener,
                                                                         OnFooterRefreshListener,
                                                                         OnLocationSeekListener{
	private HalloonApplication application;

	private MainPageFragmentCallback mpCallback;
	private HalloonPullableView pullAndDrop;
	private ListView list;
	private Button sendButton;
	private Button aroundButton;
	private Button backButton;
	private Button fanButton;
	private Context context;
	private TextView titleText;
	ArrayList<TweetBean> tweetContainer;
	TweetContentAdapter tweetContentAdapter;

	public static final int MAIN_TIMELINE_TWEET = 1;
	public static final int OTHER_TWEET = 2;
	public static final int AROUND_TWEET = 3;

	private int tweetState = MAIN_TIMELINE_TWEET;
	private String otherName;
	private String myName;
	private String nick;
	
	private int page = 0;
	private String longitude;
	private String latitude;
	
	private String newResponTime;
	private String oldResponTime;
	
	private LayoutInflater layoutInflater;
	private View footer;

	private LocationTask locationTask;
	private DelBroadCastReceiver delBroadCast = null;

	public interface MainPageFragmentCallback {

		public void setupDetailFragment(Bundle bundle);

		public void setupPublishFragment();

		public void setupCommentFragment(Bundle bundle);

		public void setupAroundTweetFragment();

		public void setupPictureDialog(String addr, String preSize, Bitmap bitmap);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mpCallback = (MainPageFragmentCallback) activity;
		context = activity;
		application = (HalloonApplication) activity.getApplication();

		if (getArguments().getString("name") != null) otherName = getArguments().getString("name");
		if (getArguments().getString("nick") != null) nick = getArguments().getString("nick");
	}
	
	@Override
	protected void init(HalloonTitleBar titleBar, RelativeLayout content) {
		layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		titleBar.setOnTitleBarClickListener(this);
		
		titleText = titleBar.getTitleTextView();
		
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content.addView(inflater.inflate(R.layout.tab_mainpage, null, false));
		
		pullAndDrop = (HalloonPullableView) content.findViewById(R.id.pull_layout);
		pullAndDrop.setOnHeaderRefreshListener(this);
		pullAndDrop.setOnFooterRefreshListener(this);
		
		list = (ListView) content.findViewById(R.id.list);
		
		tweetContainer = new ArrayList<TweetBean>();
		tweetContentAdapter = new TweetContentAdapter(context, tweetContainer);
		footer = inflater.inflate(R.layout.tweet_content_more, null, false);
		list.addFooterView(footer);
		list.setAdapter(tweetContentAdapter);
		
		if(tweetState == AROUND_TWEET){
			locationTask = new LocationTask(context);
			locationTask.setOnLocationSeekListener(this);
		}
	}

	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		switch (tweetState) {
		case MAIN_TIMELINE_TWEET:
			mTitleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_IMAGE);
			
			sendButton = mTitleBar.getLeftImageButton();
			aroundButton = mTitleBar.getRightImageButton();
			
			String tmp_nick = DBManager.getInstance(context).getProfile().getNick();
			if (SettingsManager.getInstance(context).getProfileStatus() == DBManager.PROFILE_STATUS_READY && tmp_nick != null) {
				titleText.setText(tmp_nick);
			} else {
				new Thread() {
					@Override
					public void run() {
						ProfileBean profileBean = ContentManager.getInstance(context).getMyProfile();
						DBManager.getInstance(context).upgradeProfile(profileBean);
						return;
					}
				}.start();
				
				titleText.setText(DBManager.getInstance(context).getProfile().getNick());
			}
			break;
		case OTHER_TWEET:
			if(otherName != null && !otherName.equals(myName)){
				mTitleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_NORMAL);
				fanButton = mTitleBar.getRightButton(R.string.idol);
			}else{
				mTitleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_BACK_BUTTON_ONLY);
			}
			backButton = mTitleBar.getLeftButton();
			titleText.setText(nick);
			break;
		case AROUND_TWEET:
			mTitleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_BACK_BUTTON_ONLY);
			backButton = mTitleBar.getLeftButton();
			titleText.setText(context.getString(R.string.nearby_tweet));
			break;
		default:
			if (SettingsManager.getInstance(context).getProfileStatus() == DBManager.PROFILE_STATUS_READY) {
				titleText.setText(DBManager.getInstance(context).getProfile().getNick());
			} else {
				titleText.setText(ContentManager.getInstance(context).getMyProfile().getNick());
			}
			break;
		}

		if (DBManager.getInstance(context).getProfile().getName() == null) {
			new Thread() {
				@Override
				public void run() {
					ProfileBean profileBean = new ProfileBean();
					while(profileBean.getName() == null){
						profileBean = ContentManager.getInstance(context).getMyProfile();
					}
					DBManager.getInstance(context).upgradeProfile(profileBean);
					myName = profileBean.getName();
					return;
				}
			}.start();
		} else {
			myName = DBManager.getInstance(context).getProfile().getName();
		}
		
		loadData(25, false);

		// customize fastscroll bar style
		/*
		try {
			Field f = AbsListView.class.getDeclaredField("mFastScroller");
			f.setAccessible(true);
			Object o = f.get(list);
			f = f.getType().getDeclaredField("mThumbDrawable");
			f.setAccessible(true);
			Drawable drawable = (Drawable) f.get(o);
			drawable = context.getResources().getDrawable(R.drawable.ic_launcher);
			f.set(o, drawable);
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/ 

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position < list.getCount() - 1){
					Bundle bundle = new Bundle();
					bundle.putString("id", String.valueOf(id));
					bundle.putParcelable("tweetBean", tweetContentAdapter.getItem(position));
					mpCallback.setupDetailFragment(bundle);
				}else{
					if(footer != null){
						list.removeFooterView(footer);
						footer = layoutInflater.inflate(R.layout.footer_loading, null, false);
						list.addFooterView(footer);
					}
					
					getMoreData();
				}
			}

		});
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				PopupWindowManager popupWindowManager = new PopupWindowManager(context);
				if(position < list.getCount() - 1){
					if (otherName != null && otherName.equals(myName)) {
						popupWindowManager.setupCommentFunctionPopup(tweetContentAdapter.getItem(position).getId(), tweetContentAdapter.getItem(position).getName().equals(myName), tweetContentAdapter.getItem(position).getText(), PopupWindowManager.TWEET_LIST, position);
					} else if (otherName == null && tweetState == OTHER_TWEET) {
						popupWindowManager.setupCommentFunctionPopup(tweetContentAdapter.getItem(position).getId(), tweetContentAdapter.getItem(position).getName().equals(myName), tweetContentAdapter.getItem(position).getText(), PopupWindowManager.TWEET_LIST, position);
					} else {
						popupWindowManager.setupCommentFunctionPopup(tweetContentAdapter.getItem(position).getId(), tweetContentAdapter.getItem(position).getName().equals(myName), tweetContentAdapter.getItem(position).getText(), PopupWindowManager.TWEET_LIST, position);
					}
				}
				return true;
			}

		});

		
	}

	@Override
	public void onStart() {
		super.onStart();
		delBroadCast = new DelBroadCastReceiver();
		IntentFilter delFilter = new IntentFilter();
		delFilter.addAction(Constants.DEL_CONFIRM);

		context.registerReceiver(delBroadCast, delFilter);
	}

	private void loadData(final int tweetNumber, final boolean refresh) {

		if (tweetState != AROUND_TWEET) {
			new BaseCompatiableTask<Void, Void, ArrayList<TweetBean>>() {
				
				@Override
				protected ArrayList<TweetBean> doInBackground(Void... arg0) {
					ArrayList<TweetBean> tmpArrayList = new ArrayList<TweetBean>();
					
					switch (tweetState) {
					case MAIN_TIMELINE_TWEET:
						tmpArrayList = DBManager.getInstance(context).getAllTweetList();
						if (SettingsManager.getInstance(context).getTweetListStatus() == DBManager.TWEET_LIST_STATUS_INIT || refresh || tmpArrayList.size() <= 0) {
							tmpArrayList = ContentManager.getInstance(context).getHomeTimeLineTweet("", "", tweetNumber, "", "");

							HashMap<String, TweetBean> tweetLists = new HashMap<String, TweetBean>();
							for (int i = 0; i < tmpArrayList.size(); i++) {
								tweetLists.put(tmpArrayList.get(i).getTimestamp(), (TweetBean) tmpArrayList.get(i));
							}

							DBManager.getInstance(context).addTweetListContent(tweetLists, true);

							SettingsManager.getInstance(context).setLastUpdateTime(context.getString(R.string.refresh_at) + TimeUtil.getCurrentTime());
						}
						break;
					case OTHER_TWEET:
						tmpArrayList = ContentManager.getInstance(context).getOtherTimeLine("", "", tweetNumber, "", otherName, null, "", "");
						break;
					}

					return tmpArrayList;
				}

				@Override
				protected void onPostExecute(ArrayList<TweetBean> result) {
					super.onPostExecute(result);
					if(result != null && result.size() > 0){
						
						if(!refresh){
							Intent intent = new Intent();
							intent.setAction(Constants.GLOBAL_TAB_VISIBILITY);
							Bundle bundle = new Bundle();
							bundle.putBoolean("isTabShow", true);
							bundle.putBoolean("isCoverShow", false);
							intent.putExtras(bundle);
							context.sendBroadcast(intent);
						}
						tweetContainer.clear();
						tweetContainer.addAll(result);
						tweetContentAdapter.notifyDataSetChanged();
						
						Log.d(Constants.LOG_TAG, tweetContainer.size() + ":");
						
						oldResponTime = result.get(result.size() - 1).getTimestamp();
						newResponTime = result.get(0).getTimestamp();
					} else {
						Toast.makeText(context, context.getString(R.string.refresh_failure), Toast.LENGTH_LONG).show();
					}
					pullAndDrop.onHeaderRefreshComplete(SettingsManager.getInstance(context).getLastUpdateTime());
				}
			}.taskExecute();
		} else if(!refresh){
			locationTask.taskExecute();
		}
	}
	
	private void getMoreData(){
		if(tweetState != AROUND_TWEET){
			new BaseCompatiableTask<Void, Void, ArrayList<TweetBean>>(){
				@Override
				protected ArrayList<TweetBean> doInBackground(Void... params){
					ArrayList<TweetBean> tmpArrayList = new ArrayList<TweetBean>();
					switch(tweetState){
					case MAIN_TIMELINE_TWEET:
						tmpArrayList = ContentManager.getInstance(context).getHomeTimeLineTweet("1", oldResponTime, 25, "", "");
						break;
					case OTHER_TWEET:
						tmpArrayList = ContentManager.getInstance(context).getOtherTimeLine("1", oldResponTime, 25, "", otherName, null, "", "");
						break;
					}
					return tmpArrayList;
				}
				
				@Override
				protected void onPostExecute(ArrayList<TweetBean> result){
					super.onPostExecute(result);
					if(footer != null){
						list.removeFooterView(footer);
						footer = layoutInflater.inflate(R.layout.tweet_content_more, null, false);
						list.addFooterView(footer);
					}
					
					pullAndDrop.onFooterRefreshComplete();
					
					if(result.size() > 0){
						tweetContainer.addAll(result);
						tweetContentAdapter.notifyDataSetChanged();
						oldResponTime = result.get(result.size() - 1).getTimestamp();
					}else{
						Toast.makeText(context, "更新失败", Toast.LENGTH_LONG).show();
					}
					
				}
			}.taskExecute();
		}else{
			loadAroundTweet(longitude, latitude, ++page);
		}
	}

	private void loadAroundTweet(final String longitude, final String latitude, final int page) {
		new BaseCompatiableTask<Void, Void, ArrayList<TweetBean>>() {
			@Override
			protected ArrayList<TweetBean> doInBackground(Void... params) {
				ArrayList<TweetBean> tmp_list = ContentManager.getInstance(context).getAroundTweet(longitude, latitude, String.valueOf(page), "25");

				tweetContainer.clear();
				tweetContainer.addAll(tmp_list);
				return tmp_list;
			}

			@Override
			protected void onPostExecute(ArrayList<TweetBean> result) {
				if(result != null && result.size() > 0){
					//tweetContainer.clear();
					tweetContainer.addAll(result);
					tweetContentAdapter.notifyDataSetChanged();
				}
				//pullAndDrop.onHeaderRefreshComplete();
				pullAndDrop.onFooterRefreshComplete();
			}
		}.taskExecute();
	}
	
	@Override
	public void onTitleContentClick(int contentEnum){
		switch(contentEnum){
		case OnTitleBarClickListener.LEFT_BUTTON:
			((BaseMultiFragmentActivity) context).backStackAction();
			break;
		case OnTitleBarClickListener.LEFT_IMAGE_BUTTON:
			mpCallback.setupPublishFragment();
			break;
		case OnTitleBarClickListener.RIGHT_IMAGE_BUTTON:
			mpCallback.setupAroundTweetFragment();
			break;
		case OnTitleBarClickListener.TITLE_TEXT_VIEW:
			if (tweetState == MAIN_TIMELINE_TWEET) {
				PopupWindowManager pwManager = new PopupWindowManager(context);
				ArrayList<String> arrayList = new ArrayList<String>();
				arrayList.add("your group");
				arrayList.add("my group");
				arrayList.add("his group");
				arrayList.add("her group");
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, arrayList);
				pwManager.setupTitleListPopup(R.id.title_text, adapter);
			}
			break;
		}
	}

	public void setTweetState(int tweetState) {
		this.tweetState = tweetState;
	}

	@Override
	public void onResume(){
		super.onResume();
		if(application.getMainPageState()){
			application.setMainPageState(false);
			tweetContentAdapter.notifyDataSetChanged();
		}
	}
	@Override
	public void onPause(){
		super.onPause();
		
		//bitmapRecycle();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		context.unregisterReceiver(delBroadCast);
	}
	

	private class DelBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			tweetContentAdapter.removeItem(bundle.getInt("position"));
		}
	}
	
	private void bitmapRecycle(){
		for(int i = 0; i < tweetContentAdapter.getCount(); i++){
			View rootView = tweetContentAdapter.getView(i, null, null);
			ImageView imageView = (ImageView) rootView.findViewById(R.id.tweet_image);
			if(imageView != null){
				Bitmap bmp = imageView.getDrawingCache();
				imageView.setImageBitmap(null);
				if(bmp != null && !bmp.isRecycled()){
					bmp.recycle();
				}
			}
			
			ImageView sourceImageView = (ImageView) rootView.findViewById(R.id.forward_image);
			if(sourceImageView != null){
				Bitmap bmp = sourceImageView.getDrawingCache();
				sourceImageView.setImageBitmap(null);
				if(bmp != null && !bmp.isRecycled()){
					bmp.recycle();
				}
			}
		}
		
		System.gc();
		System.runFinalization();
	}

	@Override
	public void onFooterRefresh(HalloonPullableView view) {getMoreData();}

	@Override
	public void onHeaderRefresh(HalloonPullableView view) {loadData(25, true);}
	
	@Override
	public void onLocationSeeking() {}
	
	@Override
	public void onLocationGot(double longitude, double latitude){
		this.longitude = String.valueOf(longitude);
		this.latitude = String.valueOf(latitude);
		loadAroundTweet(this.longitude, this.latitude, 0);
		page++;
	}
	
}
