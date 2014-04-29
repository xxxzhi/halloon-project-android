package com.halloon.android.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.halloon.android.HalloonApplication;
import com.halloon.android.R;
import com.halloon.android.adapter.SimpleUserAdapter;
import com.halloon.android.bean.ProfileBean;
import com.halloon.android.bean.TweetBean;
import com.halloon.android.bean.UserBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.DBManager;
import com.halloon.android.data.SettingsManager;
import com.halloon.android.listener.OnTitleBarClickListener;
import com.halloon.android.task.BaseCompatiableTask;
import com.halloon.android.task.LocationTask;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;
import com.halloon.android.ui.activity.SlideHomeActivity;
import com.halloon.android.util.Constants;
import com.halloon.android.util.TimeUtil;
import com.halloon.android.widget.HalloonPullableView;
import com.halloon.android.widget.HalloonPullableView.OnFooterRefreshListener;
import com.halloon.android.widget.HalloonPullableView.OnHeaderRefreshListener;
import com.halloon.android.widget.HalloonTitleBar;

public class UserSimpleListFragment extends BaseTitleBarFragment implements
		OnTitleBarClickListener, OnHeaderRefreshListener,
		OnFooterRefreshListener {
	private HalloonApplication application;

	private HalloonPullableView pullAndDrop;
	private ListView list;
	private Button backButton;
	private Button fanButton;
	private Context context;
	private TextView titleText;
	ArrayList<UserBean> tweetContainer;
	SimpleUserAdapter tweetContentAdapter;

	public static final int FANS = 1;
	public static final int IDOL = 2;


	private String name;
	private String fopenid;
	private int page = 0;

	private String newResponTime;
	private String oldResponTime;

	private LayoutInflater layoutInflater;
	private View footer;


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		application = (HalloonApplication) activity.getApplication();

		if (getArguments() != null) {
			if (getArguments().getString("name") != null)
				name = getArguments().getString("name");
			if (getArguments().getString("fopenid") != null)
				fopenid = getArguments().getString("fopenid");
		}
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
		content.addView(inflater.inflate(R.layout.fragment_user, null, false));

		pullAndDrop = (HalloonPullableView) content
				.findViewById(R.id.pull_layout);
		pullAndDrop.setOnHeaderRefreshListener(this);
		pullAndDrop.setOnFooterRefreshListener(this);

		list = (ListView) content.findViewById(R.id.list);

		tweetContainer = new ArrayList<UserBean>();
		tweetContentAdapter = new SimpleUserAdapter(context, tweetContainer,userType,name);
		footer = inflater.inflate(R.layout.tweet_content_more, null, false);
		list.addFooterView(footer);
		list.setAdapter(tweetContentAdapter);

	    
	    switch(userType ){
	    case FANS:
	    	titleText.setText(name+getResources().getString(R.string.suffix_fans));
	    	break;
	    case IDOL:
	    	titleText.setText(name+getResources().getString(R.string.suffix_idol));
	    	break;
	    }
		
		System.out.println("test exit: init");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		loadData(25, false);

		list.setFocusable(true);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position < list.getCount() - 1) {
					Bundle bundle = new Bundle();
					bundle.putString("id", String.valueOf(id));
//					bundle.putBundle("tweetBean",
//							tweetContentAdapter.getItem(position).toBundle());
//					mpCallback.setupDetailFragment(bundle);
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

	}

	private void loadData(final int userNumber, final boolean refresh) {

			new BaseCompatiableTask<Void, Void, ArrayList<UserBean>>() {

				@Override
				protected ArrayList<UserBean> doInBackground(Void... arg0) {
					ArrayList<UserBean> tmpArrayList = new ArrayList<UserBean>();
					switch(userType){
					case FANS:
						tmpArrayList = ContentManager.getInstance(context).getUserFans("", fopenid, 0);
						break;
					case IDOL:
						tmpArrayList = ContentManager.getInstance(context).getUserIdol("", fopenid, 0);
						break;
					}

					return tmpArrayList;
				}

				@Override
				protected void onPostExecute(ArrayList<UserBean> result) {
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
						tweetContainer.clear();
						tweetContainer.addAll(result);
						tweetContentAdapter.notifyDataSetChanged();


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
	private int lastId = 0;

	private void getMoreData() {
			new BaseCompatiableTask<Void, Void, ArrayList<UserBean>>() {
				@Override
				protected ArrayList<UserBean> doInBackground(Void... params) {
						
						ArrayList<UserBean> tmpArrayList = null;
						switch(userType){
						case FANS:
							tmpArrayList = ContentManager.getInstance(context).getUserFans("", fopenid, tweetContainer.size());
							break;
						case IDOL:
							tmpArrayList = ContentManager.getInstance(context).getUserIdol("", fopenid, tweetContainer.size());
							break;
						}
						
					return tmpArrayList;
				}

				@Override
				protected void onPostExecute(ArrayList<UserBean> result) {
					super.onPostExecute(result);
					if (footer != null) {
						list.removeFooterView(footer);
						footer = layoutInflater.inflate(
								R.layout.tweet_content_more, null, false);
						list.addFooterView(footer);
					}

					pullAndDrop.onFooterRefreshComplete();

					if (result.size() > 0) {
						tweetContainer.addAll(result);
						tweetContentAdapter.notifyDataSetChanged();
					} else {
						Toast.makeText(context, "更新失败", Toast.LENGTH_LONG)
								.show();
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
			break;
		case OnTitleBarClickListener.RIGHT_IMAGE_BUTTON:
//			mpCallback.setupPublishFragment();
//			mpCallback.setupAroundTweetFragment();
			break;
		case OnTitleBarClickListener.TITLE_TEXT_VIEW:
			break;
		}
	}
	
	private int userType;
	public void setUserType(int type) {
		this.userType = type;
	}

	@Override
	public void onFooterRefresh(HalloonPullableView view) {
		getMoreData();
	}

	@Override
	public void onHeaderRefresh(HalloonPullableView view) {
		loadData(25, true);
	}


}
