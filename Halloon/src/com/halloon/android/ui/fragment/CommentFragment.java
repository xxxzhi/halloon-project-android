package com.halloon.android.ui.fragment;

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
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.halloon.android.R;
import com.halloon.android.listener.OnEmojiSelectedListener;
import com.halloon.android.task.PostActionTask;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;
import com.halloon.android.util.Constants;
import com.halloon.android.widget.HalloonEmojiSelector;

public class CommentFragment extends SherlockFragment implements OnClickListener,
                                                                 OnEmojiSelectedListener{

	private Context context;

	private TextView title;
	private EditText content;
	private ImageView emojiButton;
	private ImageView atButton;
	private ImageView hotButton;
	private Button publicButton;
	private Button backButton;
	
	private HalloonEmojiSelector emojiSelector;

	private String tweetId;

	public static final int COMMENT = 0;
	public static final int RETWEET = 1;

	private int type = COMMENT;

	private boolean reShowTab = true;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.context = activity;
		this.tweetId = getArguments().getString("id");
		this.type = getArguments().getInt("type");
		reShowTab = getArguments().getBoolean("reShowTab");
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
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		View root = layoutInflater.inflate(R.layout.edit_comment_content, null, false);

		title = (TextView) root.findViewById(R.id.title_text);
		content = (EditText) root.findViewById(R.id.edit_comment);
		emojiButton = (ImageView) root.findViewById(R.id.p_emoji);
		atButton = (ImageView) root.findViewById(R.id.p_at);
		hotButton = (ImageView) root.findViewById(R.id.p_sharp);
		publicButton = (Button) root.findViewById(R.id.publish);
		backButton = (Button) root.findViewById(R.id.back_button);
		emojiSelector = (HalloonEmojiSelector) root.findViewById(R.id.emoji_selector);
		emojiSelector.setOnEmojiSelectedListener(this);
		
		content.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				emojiSelector.setVisibility(View.GONE);
				return false;
			}
		});

		emojiButton.setOnClickListener(this);
		atButton.setOnClickListener(this);
		hotButton.setOnClickListener(this);
		publicButton.setOnClickListener(this);
		backButton.setOnClickListener(this);

		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (type == COMMENT) {
			title.setText(context.getString(R.string.comment));
		} else {
			title.setText(context.getString(R.string.retweet));
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		Intent intent = new Intent();
		intent.setAction(Constants.GLOBAL_TAB_VISIBILITY);
		Bundle bundle = new Bundle();
		if (reShowTab) {
			bundle.putBoolean("isTabShow", true);
		} else {
			bundle.putBoolean("isTabShow", false);
		}
		bundle.putBoolean("isCoverShow", false);
		intent.putExtras(bundle);
		context.sendBroadcast(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.p_emoji:
			if (emojiSelector.getVisibility() == View.GONE) {
				((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				emojiSelector.setVisibility(View.VISIBLE);
			} else {
				emojiSelector.setVisibility(View.GONE);
			}
			break;
		case R.id.p_at:
			content.append("@");
			break;
		case R.id.p_sharp:
			content.append("##");
			content.setSelection(content.length() - 1, content.length() - 1);
			break;
		case R.id.publish:
			PostActionTask task;
			if (type == COMMENT) {
				if (content.getText().toString().length() < 1) {
					Toast.makeText(context, context.getString(R.string.nothing_to_say), Toast.LENGTH_LONG).show();
					break;
				}
				task = new PostActionTask(context, PostActionTask.COMMENT, tweetId, content.getText().toString());
			} else {
				task = new PostActionTask(context, PostActionTask.RETWEET, tweetId, content.getText().toString());
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				task.execute();
			}
			break;
		case R.id.back_button:
			((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			((BaseMultiFragmentActivity) context).backStackAction();
			break;
		}
	}

	@Override
	public void onSelected(String emojiName){
		content.append(emojiName);
	}
	
	@Override
	public void onBackSpace(){
		System.out.println("backspace");
		if(content.length() > 0) content.setText(String.valueOf(content.getText()).substring(0, content.getText().length() - 1));
	}
}
