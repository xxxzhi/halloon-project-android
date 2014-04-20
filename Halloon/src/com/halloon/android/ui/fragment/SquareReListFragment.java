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

public class SquareReListFragment extends BaseTitleBarFragment implements
		OnTitleBarClickListener, OnHeaderRefreshListener,
		OnFooterRefreshListener, OnLocationSeekListener {
	private HalloonApplication application;

	private MainPageFragmentCallback mpCallback;
	private HalloonPullableView pullAndDrop;
	private ListView list;
	private Context context;
	private TextView titleText;
	
	TweetContentAdapter tweetContentAdapter;

	private ArrayList<TweetBean> hotReBeans;
	
	

	private String myName;
	private String nick;

	private int page = 1;
	private String longitude;
	private String latitude;

	private String newResponTime;
	private String oldResponTime;

	private LayoutInflater layoutInflater;
	private View footer;

	private LocationTask locationTask;
	private ViewPager pager = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mpCallback = (MainPageFragmentCallback) activity;
		context = activity;
		application = (HalloonApplication) activity.getApplication();

	}

	public ArrayList<TweetBean> getHotReBeans() {
		return hotReBeans;
	}

	public void setHotReBeans(ArrayList<TweetBean> hotReBeans) {
		this.hotReBeans = hotReBeans;
		
		pos = hotReBeans.size()-1;
	}
	
	
	@Override
	protected void init(HalloonTitleBar titleBar, RelativeLayout content) {
		
		titleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_BACK_BUTTON_ONLY);
		
		layoutInflater = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		
		titleBar.setOnTitleBarClickListener(this);
		titleText = titleBar.getTitleTextView();

		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content.addView(inflater.inflate(R.layout.fragment_square_list, null, false));


		pullAndDrop = (HalloonPullableView) content
				.findViewById(R.id.pull_layout);
		pullAndDrop.setOnHeaderRefreshListener(this);
		pullAndDrop.setOnFooterRefreshListener(this);

		list = (ListView) content.findViewById(R.id.list);
		

		tweetContentAdapter = new TweetContentAdapter(context, hotReBeans);
		footer = inflater.inflate(R.layout.tweet_content_more, null, false);
		list.addFooterView(footer);
		list.setAdapter(tweetContentAdapter);

		titleText.setText(R.string.hot_trans);
	}

//	private TextWatcher textWatcher = new TextWatcher() {
//
//		@Override
//		public void onTextChanged(CharSequence s, int start, int before,
//				int count) {
//			page = 1;
//			Log.d(Constants.LOG_TAG, "onTextChanged " + s.toString());
//			ArrayList<TweetBean> tmpArrayList = new ArrayList<TweetBean>();
//
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
//		}
//
//		public void beforeTextChanged(CharSequence s, int start, int count,
//				int after) {
//
//		}
//
//		public void afterTextChanged(Editable s) {
//
//		}
//
//	};
	
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



		// customize fastscroll bar style
		/*
		 * try { Field f = AbsListView.class.getDeclaredField("mFastScroller");
		 * f.setAccessible(true); Object o = f.get(list); f =
		 * f.getType().getDeclaredField("mThumbDrawable");
		 * f.setAccessible(true); Drawable drawable = (Drawable) f.get(o);
		 * drawable =
		 * context.getResources().getDrawable(R.drawable.ic_launcher); f.set(o,
		 * drawable); } catch (Exception e) { e.printStackTrace(); }
		 */
		list.setFocusable(true);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position < list.getCount() - 1) {
					Bundle bundle = new Bundle();
					bundle.putString("id", String.valueOf(id));
					bundle.putBundle("tweetBean",
							tweetContentAdapter.getItem(position).toBundle());
					mpCallback.setupDetailFragment(bundle);
				} else {
					if (footer != null) {
						list.removeFooterView(footer);
						footer = layoutInflater.inflate(
								R.layout.footer_loading, null, false);
						list.addFooterView(footer);
					}

					getMoreData();
				}
			}

		});
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				PopupWindowManager popupWindowManager = new PopupWindowManager(
						context);
				if (position < list.getCount() - 1) {
					popupWindowManager.setupCommentFunctionPopup(
							tweetContentAdapter.getItem(position).getId(),
							tweetContentAdapter.getItem(position).getName()
									.equals(myName), tweetContentAdapter
									.getItem(position).getText(),
							PopupWindowManager.TWEET_LIST, position);
				}
				return true;
			}

		});
		
		list.setOnScrollListener(mScrollListener);
		
		loadData(25, false);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private void loadData(final int tweetNumber, final boolean refresh) {
		page = 1;
		new BaseCompatiableTask<Void, Void, ArrayList<TweetBean>>() {

			@Override
			protected ArrayList<TweetBean> doInBackground(Void... arg0) {
				ArrayList<TweetBean> tmpArrayList = new ArrayList<TweetBean>();

					tmpArrayList = ContentManager.getInstance(context)
							.getReHotTweetBean(tweetNumber, 0, "0");
					pos = ContentManager.getInstance(context).getPos();

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

				return tmpArrayList;
			}

			@Override
			protected void onPostExecute(ArrayList<TweetBean> result) {
				super.onPostExecute(result);
				if (result != null && result.size() > 0) {

					if (!refresh) {
						Intent intent = new Intent();
						intent.setAction(Constants.GLOBAL_TAB_VISIBILITY);
						Bundle bundle = new Bundle();
						bundle.putBoolean("isTabShow", true);
						bundle.putBoolean("isCoverShow", false);
						intent.putExtras(bundle);
						context.sendBroadcast(intent);
					}
					hotReBeans.clear();
					hotReBeans.addAll(result);
					tweetContentAdapter.notifyDataSetChanged();

					oldResponTime = result.get(result.size() - 1)
							.getTimestamp();
					newResponTime = result.get(0).getTimestamp();

				} else {
					Toast.makeText(context,
							context.getString(R.string.refresh_failure),
							Toast.LENGTH_LONG).show();
				}
				pullAndDrop.onHeaderRefreshComplete(SettingsManager
						.getInstance(context).getLastUpdateTime());
			}
		}.taskExecute();
	}

	final int PAGE_SIZE = 25;
	private int pos;

	private void getMoreData() {
		page ++ ;
		new BaseCompatiableTask<Void, Void, ArrayList<TweetBean>>() {
			@Override
			protected ArrayList<TweetBean> doInBackground(Void... params) {
				ArrayList<TweetBean> tmpArrayList = new ArrayList<TweetBean>();
				tmpArrayList = ContentManager.getInstance(context)
							.getReHotTweetBean(PAGE_SIZE, pos, "0");
				pos = ContentManager.getInstance(context).getPos();

				if (tmpArrayList != null) {
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

						if (tmpArrayList.get(i).getSource() != null
								&& (tmpArrayList.get(i).getSource()
										.getMusicUrl() != null || tmpArrayList
										.get(i).getSource().getVideoImage() != null)) {
							matcher = pattern.matcher(tmpArrayList.get(i)
									.getSource().getText());
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
					} while (++i < tmpArrayList.size());
				}

				return tmpArrayList;
			}

			@Override
			protected void onPostExecute(ArrayList<TweetBean> result) {
				super.onPostExecute(result);
				if (footer != null) {
					list.removeFooterView(footer);
					footer = layoutInflater.inflate(
							R.layout.tweet_content_more, null, false);
					list.addFooterView(footer);
				}

				pullAndDrop.onFooterRefreshComplete();

				if (result.size() > 0) {
					hotReBeans.addAll(result);
					tweetContentAdapter.notifyDataSetChanged();
					oldResponTime = result.get(result.size() - 1)
							.getTimestamp();
				} else {
					Toast.makeText(context, "更新失败", Toast.LENGTH_LONG).show();
				}

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
	public void onFooterRefresh(HalloonPullableView view) {
		getMoreData();
	}

	@Override
	public void onHeaderRefresh(HalloonPullableView view) {
		loadData(25, true);
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

}
