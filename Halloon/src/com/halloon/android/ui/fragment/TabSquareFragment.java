package com.halloon.android.ui.fragment;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.halloon.android.HalloonApplication;
import com.halloon.android.R;
import com.halloon.android.adapter.SquarePagerAdapter;
import com.halloon.android.adapter.TweetContentAdapter;
import com.halloon.android.bean.FamousBean;
import com.halloon.android.bean.TopicBean;
import com.halloon.android.bean.TweetBean;
import com.halloon.android.bean.UserBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.DBManager;
import com.halloon.android.data.SettingsManager;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.listener.OnLocationSeekListener;
import com.halloon.android.listener.OnTitleBarClickListener;
import com.halloon.android.task.BaseCompatiableTask;
import com.halloon.android.task.LocationTask;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;
import com.halloon.android.ui.activity.SlideHomeActivity;
import com.halloon.android.ui.fragment.TabMainPageFragment.MainPageFragmentCallback;
import com.halloon.android.util.Constants;
import com.halloon.android.util.PopupWindowManager;
import com.halloon.android.util.TimeUtil;
import com.halloon.android.widget.HalloonPullableView;
import com.halloon.android.widget.HalloonPullableView.OnFooterRefreshListener;
import com.halloon.android.widget.HalloonPullableView.OnHeaderRefreshListener;
import com.halloon.android.widget.HalloonTitleBar;

public class TabSquareFragment extends BaseTitleBarFragment implements
		OnTitleBarClickListener,OnClickListener
		, OnLocationSeekListener {
	
	public interface SquareFragmentCallback {
		
		public void setSquareFamousList(ArrayList<FamousBean> famous);
		
		public void setSquareTopicList(ArrayList<TopicBean> topic);
		
		public void setSquareHotReList(ArrayList<TweetBean> hotRe);

	}


	private HalloonApplication application;

	private SquareFragmentCallback mpCallback;
	private Context context;
	private TextView titleText;
	private EditText searchEditText;
	TweetContentAdapter tweetContentAdapter;
	private Button deleteButton;

	private int page = 1;
	private String longitude;
	private String latitude;

	private ArrayList<TweetBean> hotReBeans = new ArrayList<TweetBean>();
	private ArrayList<TopicBean> hotTopicBeans = new ArrayList<TopicBean>();
	private ArrayList<FamousBean> hotFamousBeans = new ArrayList<FamousBean>();
	
	private LayoutInflater layoutInflater;
	private View footer;

	private LocationTask locationTask;
	private ViewPager pager = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mpCallback = (SquareFragmentCallback) activity;
		context = activity;
		application = (HalloonApplication) activity.getApplication();
	}

	class ViewHolder {
		ViewGroup parent ;
		ImageView icon;
		TextView title;
		TextView content;
	}
	
	ViewHolder hotTopic = new ViewHolder(),
			hotRe = new ViewHolder(),
			hotFamous = new ViewHolder();
	
	
	@Override
	protected void init(HalloonTitleBar titleBar, RelativeLayout content) {
		layoutInflater = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		
		titleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_MENU);
		titleBar.setOnTitleBarClickListener(this);
		titleText = titleBar.getTitleTextView();

		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content.addView(inflater.inflate(R.layout.tab_square, null, false));

		searchEditText = (EditText) content.findViewById(R.id.search_editText);
		deleteButton = (Button) content.findViewById(R.id.deleteButton);
		
		pager = (ViewPager) content.findViewById(R.id.viewpager);
		pager.setAdapter(new SquarePagerAdapter(getActivity()));
		initPagerScroll();

		
		ViewGroup group =(ViewGroup)content.findViewById(R.id.rl_hot_topic);
		
		hotTopic.icon = (ImageView) group.findViewById(R.id.imageView1);
		hotTopic.content = (TextView) group.findViewById(R.id.content);
		hotTopic.title = (TextView) group.findViewById(R.id.title);
		
		findView(hotTopic, (ViewGroup)content.findViewById(R.id.rl_hot_topic));
		findView(hotRe, (ViewGroup)content.findViewById(R.id.rl_hot_re));
		findView(hotFamous, (ViewGroup)content.findViewById(R.id.rl_famous));
	}
	
	private void findView(ViewHolder holder ,ViewGroup group){
		group.setOnClickListener(this);
		holder.parent = group;
		holder.icon = (ImageView) group.findViewById(R.id.imageView1);
		holder.content = (TextView) group.findViewById(R.id.content);
		holder.title = (TextView) group.findViewById(R.id.title);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1 && pager.getChildCount() > 0){
				pager.setCurrentItem((pager.getCurrentItem() + 1)
						% pager.getChildCount());
			}
		}

	};

	class ScrollThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.sendEmptyMessage(1);
			}
		}
	}

	ScrollThread thread = null;

	private void initPagerScroll() {
		if (thread == null || !thread.isAlive()) {
			thread = new ScrollThread();
			thread.start();
		}
	}
	private ArrayList<TweetBean> tempHotTweetList = new ArrayList<TweetBean>();
	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			page = 1;
			Log.d(Constants.LOG_TAG, "onTextChanged " + s.toString());
			ArrayList<TweetBean> tmpArrayList = new ArrayList<TweetBean>();

//			tmpArrayList = ContentManager.getInstance(mActivity).searchTweet(
//					s.toString(), PAGE_SIZE, page, 0, "0", 0, "0", "0", "0");
//			
//			
//			tweetContainer.clear();
//			tweetContainer.addAll(tmpArrayList);
//			tweetContentAdapter.notifyDataSetChanged();
//			if (s.length() == 0) {
//				tweetContainer.clear();
//				tweetContainer.addAll(tempHotTweetList);
//				deleteButton.setVisibility(View.INVISIBLE);
//			} else {
//				deleteButton.setVisibility(View.VISIBLE);
//			}
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		public void afterTextChanged(Editable s) {

		}

	};
	
	private AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}
		private int lastfirstVisibleItem = 0 ;
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
//			if(lastfirstVisibleItem > firstVisibleItem ){
//				//scroll to down
//				if(pager.getVisibility() != View.VISIBLE ){
//					pager.setVisibility(View.VISIBLE);
//				}
//			}else{
//				pager.setVisibility(View.GONE);
//			}
//			lastfirstVisibleItem = firstVisibleItem ;
		}
	};
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		titleText.setText(R.string.tab_square);

		searchEditText.addTextChangedListener(textWatcher);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				searchEditText.setText("");
			}
		});
		
		loadData(25, false);
	}

	@Override
	public void onStart() {
		super.onStart();
	}
	
	private void loadData(final int tweetNumber, final boolean refresh) {
		page = 1;
		new BaseCompatiableTask<Void, Object,Void>() {

			@Override
			protected Void doInBackground(Void... arg0) {
				ArrayList<TweetBean> tmpArrayList = new ArrayList<TweetBean>();
				ArrayList<TopicBean> temphotTopicBeans = new ArrayList<TopicBean>();
				ArrayList<FamousBean> temphotFamousBeans = new ArrayList<FamousBean>();
				
				tmpArrayList = ContentManager.getInstance(context)
						.getReHotTweetBean(tweetNumber, 0, "0");

				if (tmpArrayList != null && tmpArrayList.size() > 0) {
					int i = 0;
					do {
						String text = tmpArrayList.get(i).getText();
						String ADDR_PATTERN = "http://url\\.cn/[a-zA-Z0-9]+";
						Pattern pattern = Pattern.compile(ADDR_PATTERN);
						Matcher matcher = pattern.matcher(text);
						if (tmpArrayList.get(i).getMusicUrl() != null
								|| tmpArrayList.get(i).getVideoImage() != null) {

							while (matcher.find()) {
								String group = matcher.group();
								group = group
										.substring(group.lastIndexOf("/") + 1);
								if (application.getShortList().get(group) == null) {
									String longUrl = ContentManager
											.getInstance(context)
											.getExpandedUrl(group);
									application.getShortList().put(group,
											longUrl);
								}
							}
						}

						if (tmpArrayList.get(i).getSource() != null) {
							if (tmpArrayList.get(i).getSource().getMusicUrl() != null
									|| tmpArrayList.get(i).getSource()
											.getVideoImage() != null) {
								matcher = pattern.matcher(tmpArrayList.get(i)
										.getSource().getText());
								while (matcher.find()) {

									String group = matcher.group();
									group = group.substring(group
											.lastIndexOf("/") + 1);
									if (application.getShortList().get(group) == null) {
										String longUrl = ContentManager
												.getInstance(context)
												.getExpandedUrl(group);
										application.getShortList().put(group,
												longUrl);
									}

								}
							}
						}
					} while (++i < tmpArrayList.size());
				}
				
				publishProgress(0,tmpArrayList);
				
				temphotFamousBeans = ContentManager.getInstance(context)
						.getFamousList("101", "");
				publishProgress(1,temphotFamousBeans);
				
				temphotTopicBeans = ContentManager.getInstance(context)
						.getHotTopic(tweetNumber, 0);
				publishProgress(2,temphotTopicBeans);
				
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Object... values) {
				super.onProgressUpdate(values);
				
				int type =(Integer) values[0];
				switch (type) {
				case 0:
					ArrayList<TweetBean> tempRe = (ArrayList<TweetBean>) values[1];
					
					if (tempRe == null || tempRe.size() == 0) {
						Toast.makeText(context,
								context.getString(R.string.refresh_failure),
								Toast.LENGTH_LONG).show();
					}else{
						hotReBeans.clear();
						hotReBeans.addAll(tempRe);
						
						
						ImageLoader.getInstance(context).displayImage(hotReBeans.get(0).getHead()+ "/120",
								hotRe.icon, 0, 
						null,R.drawable.ic_launcher);
						
//						hotRe.title .setText( hotReBeans.get(0).getName() ) ;
						hotRe.content .setText( hotReBeans.get(0).getText() );
						((TextView)hotRe.parent.findViewById(R.id.forward_count))
						.setText(hotReBeans.get(0).getCount());
						
						((TextView)hotRe.parent.findViewById(R.id.comment_count))
						.setText(hotReBeans.get(0).getMCount());
					}
					
					break;
				case 1:
					ArrayList<FamousBean> tempFamout = (ArrayList<FamousBean>) values[1];
					
					if (tempFamout == null || tempFamout.size() == 0) {
						Toast.makeText(context,
								context.getString(R.string.refresh_failure),
								Toast.LENGTH_LONG).show();
					}else{
						hotFamousBeans.clear();
						hotFamousBeans.addAll(tempFamout);
						
						ImageLoader.getInstance(context).displayImage(hotFamousBeans.get(0).getHead()+ "/120",
								hotFamous.icon, 0, 
						null,R.drawable.ic_launcher);
						
//						hotFamous.title .setText( hotFamousBeans.get(0).getNick() ) ;
						hotFamous.content .setText( hotFamousBeans.get(0).getBrief());
						
					}
					break;
				case 2:
					ArrayList<TopicBean> tempToic = (ArrayList<TopicBean>) values[1];
					
					if (tempToic == null || tempToic.size() == 0) {
						Toast.makeText(context,
								context.getString(R.string.refresh_failure),
								Toast.LENGTH_LONG).show();
					}else{
						hotTopicBeans.clear();
						hotTopicBeans.addAll(tempToic);
						
//						ImageLoader.getInstance(context).displayImage(hotTopicBeans.get(0).getName()+ "/120",
//								hotTopic.icon, 0, 
//						null,R.drawable.ic_launcher);
//						
//						hotTopic.title .setText( hotTopicBeans.get(0).getName() ) ;
						hotTopic.content .setText( hotTopicBeans.get(0).getKeywords());
						
						
					}
					break;
				default:
					return ;
				}
				
				
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				
			}
		}.taskExecute();
	}


	@Override
	public void onTitleContentClick(int contentEnum) {
		switch (contentEnum) {
		case OnTitleBarClickListener.LEFT_BUTTON:
			((BaseMultiFragmentActivity) context).backStackAction();
			break;
		case OnTitleBarClickListener.LEFT_IMAGE_BUTTON:
			Activity parent = getActivity().getParent();
			if (parent instanceof SlideHomeActivity) {
				((SlideHomeActivity) parent).toggleSlideMenu();
			}
			break;
		case OnTitleBarClickListener.RIGHT_IMAGE_BUTTON:
			// mpCallback.setupAroundTweetFragment();
			break;
		case OnTitleBarClickListener.TITLE_TEXT_VIEW:

			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (application.getMainPageState()) {
			application.setMainPageState(false);
			tweetContentAdapter.notifyDataSetChanged();
		}
	}

	private void bitmapRecycle() {
		for (int i = 0; i < tweetContentAdapter.getCount(); i++) {
			View rootView = tweetContentAdapter.getView(i, null, null);
			ImageView imageView = (ImageView) rootView
					.findViewById(R.id.tweet_image);
			if (imageView != null) {
				Bitmap bmp = imageView.getDrawingCache();
				imageView.setImageBitmap(null);
				if (bmp != null && !bmp.isRecycled()) {
					bmp.recycle();
				}
			}

			ImageView sourceImageView = (ImageView) rootView
					.findViewById(R.id.forward_image);
			if (sourceImageView != null) {
				Bitmap bmp = sourceImageView.getDrawingCache();
				sourceImageView.setImageBitmap(null);
				if (bmp != null && !bmp.isRecycled()) {
					bmp.recycle();
				}
			}
		}
		
		System.gc();
		System.runFinalization();
	}


	@Override
	public void onLocationSeeking() {
	}

	@Override
	public void onLocationGot(double longitude, double latitude) {
		this.longitude = String.valueOf(longitude);
		this.latitude = String.valueOf(latitude);
		// loadAroundTweet(this.longitude, this.latitude, 0);
		page++;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_hot_re:
			mpCallback.setSquareHotReList(hotReBeans);
			break;
		case R.id.rl_famous:
			mpCallback.setSquareFamousList(hotFamousBeans);
			break;
			
		case R.id.rl_hot_topic:
			
			break;
		default:
			break;
		}
	}
	
}
