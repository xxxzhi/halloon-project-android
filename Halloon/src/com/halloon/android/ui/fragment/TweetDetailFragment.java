package com.halloon.android.ui.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.halloon.android.R;
import com.halloon.android.adapter.TweetDetailAdapter;
import com.halloon.android.bean.TweetBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.listener.OnEmojiSelectedListener;
import com.halloon.android.listener.OnTitleBarClickListener;
import com.halloon.android.task.BaseCompatiableTask;
import com.halloon.android.task.PostActionTask;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;
import com.halloon.android.util.Constants;
import com.halloon.android.widget.HalloonEmojiSelector;
import com.halloon.android.widget.HalloonTitleBar;

public class TweetDetailFragment extends BaseTitleBarFragment implements OnClickListener,
                                                                     OnEmojiSelectedListener,
                                                                     OnTitleBarClickListener{
	private TweetDetailFragmentCallback tdCallback;
	private Context context;
	private ListView listView;
	private Button quickReplyButton;
	private ImageView emojiButton;
	private EditText quickContent;
	private TweetDetailAdapter tweetDetailAdapter;
	private TweetBean tweetDetailBean = new TweetBean();
	private ArrayList<TweetBean> tweetCommentBean = new ArrayList<TweetBean>();
	private String id;
	
	private String oldTime;
	private int status;
	
	private View footerView;
	
	private HalloonEmojiSelector emojiSelector;

	private BaseCompatiableTask<Void, Void, ArrayList<TweetBean>> task;
	private BaseCompatiableTask<Void, Void, ArrayList<TweetBean>> mListTask;

	public interface TweetDetailFragmentCallback {
		public void setupProfileFragment(Bundle bundle);

		public void mListChange(int status);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.tdCallback = (TweetDetailFragmentCallback) activity;
		if (getArguments().getString("id") != null) this.id = getArguments().getString("id");
		if(getArguments().getBundle("tweetBean") != null) this.tweetDetailBean.decodeFromBundle(getArguments().getBundle("tweetBean"));
		context = activity;
	}

	@Override
	public void onStart() {
		super.onStart();
		Intent intent = new Intent();
		intent.setAction(Constants.GLOBAL_TAB_VISIBILITY);
		Bundle bundle = new Bundle();
		bundle.putBoolean("isTabShow", false);
		bundle.putBoolean("isCoverShow", false);
		intent.putExtras(bundle);
		context.sendBroadcast(intent);
	}
	
	@Override
	protected void init(HalloonTitleBar titleBar, RelativeLayout content) {
		titleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_BACK_BUTTON_ONLY);
		titleBar.getTitleTextView().setText(getString(R.string.tweet_detail));
		titleBar.setOnTitleBarClickListener(this);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content.addView(inflater.inflate(R.layout.tweet_detail, null, false));
		
		listView = (ListView) content.findViewById(R.id.pinned_header);
		
		tweetDetailAdapter = new TweetDetailAdapter(context, tweetDetailBean, tweetCommentBean);
		
		footerView = ((Activity) context).getLayoutInflater().inflate(R.layout.footer_loading, null);
		listView.addFooterView(footerView);
		listView.setAdapter(tweetDetailAdapter);
		emojiSelector = (HalloonEmojiSelector) content.findViewById(R.id.emoji_selector);
		emojiSelector.setOnEmojiSelectedListener(this);
		
		quickReplyButton = (Button) content.findViewById(R.id.quick_reply_button);
		emojiButton = (ImageView) content.findViewById(R.id.emoji_button);
		emojiButton.setClickable(true);
		
		quickReplyButton.setOnClickListener(this);
		emojiButton.setOnClickListener(this);
		
		quickContent = (EditText) content.findViewById(R.id.reply);
		quickContent.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event){
				emojiSelector.setVisibility(View.GONE);
				return false;
			}
		});
		
		loadData();
		
	}

	private void loadData() {
		task = new BaseCompatiableTask<Void, Void, ArrayList<TweetBean>>() {

			@Override
			protected ArrayList<TweetBean> doInBackground(Void... params) {
				ArrayList<TweetBean> tmp_list = ContentManager.getInstance(context).getTweetCommentFromId(id, "1", "0", "0", "10");
				
				return tmp_list;
			}

			@Override
			protected void onPostExecute(ArrayList<TweetBean> result) {
				if (result != null && result.size() > 4) {
					oldTime = result.get(result.size() - 1).getTimestamp();
					tweetCommentBean.clear();
					tweetCommentBean.addAll(result);
					tweetDetailAdapter.notifyDataSetChanged();	
				}else{
					
			        //Toast.makeText(context, context.getString(R.string.tweet_not_exist), Toast.LENGTH_LONG).show();
				}	
				
				if(footerView != null) listView.removeFooterView(footerView);
				footerView = ((Activity) context).getLayoutInflater().inflate(R.layout.tweet_content_more, null);
				listView.addFooterView(footerView);
			}
			
		};

		task.taskExecute();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						// TODO Auto-generated method stub
						if (position == 0) {
							Bundle bundle = new Bundle();
							bundle.putString("name", tweetDetailBean.getName());
							bundle.putString("id", tweetDetailBean.getOpenId());
							bundle.putInt("type", TabProfileFragment.OTHER);
							tdCallback.setupProfileFragment(bundle);
						}else if(position == listView.getCount() - 1){
							getMoreData();
						}
					}
				});
	}
	
	private void getMoreData(){
		task = new BaseCompatiableTask<Void, Void, ArrayList<TweetBean>>(){
			@Override
			protected void onPreExecute(){
				if(footerView != null){
					listView.removeFooterView(footerView);
				}
				footerView = ((Activity) context).getLayoutInflater().inflate(R.layout.footer_loading, null);
				listView.addFooterView(footerView);
			}
			@Override
			protected ArrayList<TweetBean> doInBackground(Void... params){
				ArrayList<TweetBean> tmp_list;
				if(status == TweetDetailAdapter.COMMENT_STATUS_COMMENT){
					tmp_list = ContentManager.getInstance(context).getTweetCommentFromId(id, "1", "1", oldTime, "15");
				}else{
					tmp_list = ContentManager.getInstance(context).getTweetCommentFromId(id, "0", "1", oldTime, "15");
				}
				
				return tmp_list;
			}
			
			@Override
			protected void onPostExecute(ArrayList<TweetBean> result){
				if(result != null && result.size() > 4){
					oldTime = result.get(result.size() - 1).getTimestamp();
					tweetCommentBean.addAll(result);
					tweetDetailAdapter.notifyDataSetChanged();
				}
				if(footerView != null){
					listView.removeFooterView(footerView);
				}
				footerView = ((Activity) context).getLayoutInflater().inflate(R.layout.tweet_content_more, null);
				listView.addFooterView(footerView);
				
				super.onPostExecute(result);
			}
		};
		
		task.taskExecute();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if (mListTask != null && mListTask.getStatus() == AsyncTask.Status.RUNNING)
			mListTask.cancel(true);
		if (task != null && task.getStatus() == AsyncTask.Status.RUNNING)
			task.cancel(true);
		Intent intent = new Intent();
		intent.setAction(Constants.GLOBAL_TAB_VISIBILITY);
		Bundle bundle = new Bundle();
		bundle.putBoolean("isTabShow", true);
		bundle.putBoolean("isCoverShow", false);
		intent.putExtras(bundle);
		context.sendBroadcast(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_button:
			((BaseMultiFragmentActivity) context).backStackAction();
			break;
		case R.id.title_text:
			break;
		case R.id.quick_reply_button:
			PostActionTask task = new PostActionTask(context, PostActionTask.COMMENT, id, quickContent.getText().toString());
			task.setIsBack(false);
			
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}else{
				task.execute();
			}
			break;
		case R.id.emoji_button:
			if (emojiSelector.getVisibility() == View.GONE) {
				((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				emojiSelector.setVisibility(View.VISIBLE);
			} else {
				emojiSelector.setVisibility(View.GONE);
			}
			break;
		}
	}

	public void changeMList(final int status) {
		
		this.status = status;
		
		

		mListTask = new BaseCompatiableTask<Void, Void, ArrayList<TweetBean>>() {
			@Override
			protected void onPreExecute(){
				tweetCommentBean.clear();
				tweetDetailAdapter.notifyDataSetChanged();
				if(footerView != null) listView.removeFooterView(footerView);
				footerView = ((Activity) context).getLayoutInflater().inflate(R.layout.footer_loading, null);
				listView.addFooterView(footerView);
			}
			@Override
			protected ArrayList<TweetBean> doInBackground(Void... params) {
				ArrayList<TweetBean> tmp_tweetBeanList;

				if (status == TweetDetailAdapter.COMMENT_STATUS_COMMENT) {
					tmp_tweetBeanList = ContentManager.getInstance(context).getTweetCommentFromId(id, "1", "0", "0", "10");
				} else {
					tmp_tweetBeanList = ContentManager.getInstance(context).getTweetCommentFromId(id, "0", "0", "0", "10");
				}

				return tmp_tweetBeanList;
			}

			@Override
			protected void onPostExecute(ArrayList<TweetBean> result) {
				if (result != null) {
					tweetCommentBean.clear();
					tweetCommentBean.addAll(result);
					tweetDetailAdapter.notifyDataSetChanged();
				}
				if(footerView != null) listView.removeFooterView(footerView);
		        footerView = ((Activity) context).getLayoutInflater().inflate(R.layout.tweet_content_more, null);
		        listView.addFooterView(footerView);
			}
		};

		mListTask.taskExecute();

	}

	@Override
	public EditText getEditText(){
		return quickContent;
	}

	@Override
	public void onTitleContentClick(int contentEnum) {
		switch(contentEnum){
		case OnTitleBarClickListener.LEFT_BUTTON:
			((BaseMultiFragmentActivity) context).backStackAction();
			break;
		}
	}
}
