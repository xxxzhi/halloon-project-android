package com.halloon.android.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.halloon.android.R;
import com.halloon.android.listener.OnEmojiSelectedListener;
import com.halloon.android.listener.OnLocationSeekListener;
import com.halloon.android.task.LocationTask;
import com.halloon.android.task.PostActionTask;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;
import com.halloon.android.util.Constants;
import com.halloon.android.util.PopupWindowManager;
import com.halloon.android.widget.HalloonEmojiSelector;

public class PublishFragment extends SherlockFragment implements OnClickListener,
                                                                 OnEmojiSelectedListener,
                                                                 OnLocationSeekListener{

	private Context context;

	private Button publishButton;
	private Button backButton;
	private EditText publishText;
	private TextView counter;

	private ImageView imageAdd;
	private ImageView locationAdd;
	private ImageView emojiAdd;
	private ImageView atAdd;
	private ImageView sharpAdd;
	private HalloonEmojiSelector emojiSelector;
	
	private TextView locationText;
	
	private LinearLayout locationInfo;

	private String longitude = "0";
	private String latitude = "0";

	private boolean isImageAdd = false;
	private boolean isLocationAdd = false;

	public interface PublishFragmentCallback {
		public void setupFileBrowserFragment();
		public String getCapturedImagePath();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.context = activity;
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.publish_popup, null);

		publishText = (EditText) root.findViewById(R.id.publish_text);
		counter = (TextView) root.findViewById(R.id.counter);
		publishButton = (Button) root.findViewById(R.id.publish_button);
		backButton = (Button) root.findViewById(R.id.back_button);
		imageAdd = (ImageView) root.findViewById(R.id.p_image);
		locationAdd = (ImageView) root.findViewById(R.id.p_location);
		emojiAdd = (ImageView) root.findViewById(R.id.p_emoji);
		atAdd = (ImageView) root.findViewById(R.id.p_at);
		sharpAdd = (ImageView) root.findViewById(R.id.p_sharp);
		
		locationInfo = (LinearLayout) root.findViewById(R.id.location_info);
		locationText = (TextView) root.findViewById(R.id.location_text);
		locationInfo.setVisibility(View.GONE);

		emojiSelector = (HalloonEmojiSelector) root.findViewById(R.id.emoji_selector);
		emojiSelector.setOnEmojiSelectedListener(this);

		imageAdd.setClickable(true);
		locationAdd.setClickable(true);
		emojiAdd.setClickable(true);
		atAdd.setClickable(true);
		sharpAdd.setClickable(true);
		
		publishText.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				emojiSelector.setVisibility(View.GONE);
				return false;
			}
		});
		
		publishText.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				counter.setText(start + count + "/140");
			}
			
		});

		publishButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
		imageAdd.setOnClickListener(this);
		locationAdd.setOnClickListener(this);
		emojiAdd.setOnClickListener(this);
		atAdd.setOnClickListener(this);
		sharpAdd.setOnClickListener(this);

		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStop() {
		super.onStop();
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
		case R.id.publish_button:
			PostActionTask publishTask;
			if (publishText.getText().length() >= 1) {
				if (isImageAdd && !isLocationAdd) {
					publishTask = new PostActionTask(context, PostActionTask.PUBLISH_IMAGE_TWEET, publishText.getText().toString(), "1", ((PublishFragmentCallback) context).getCapturedImagePath());
				} else if (isLocationAdd && !isImageAdd) {
					publishTask = new PostActionTask(context, PostActionTask.PUBLISH_TWEET, publishText.getText().toString(), longitude, latitude, null, null);
				} else if (isLocationAdd && isImageAdd) {
					publishTask = new PostActionTask(context, PostActionTask.PUBLISH_IMAGE_TWEET, publishText.getText().toString(), longitude, latitude, "1", ((PublishFragmentCallback) context).getCapturedImagePath());
				} else {
					publishTask = new PostActionTask(context, PostActionTask.PUBLISH_TWEET, publishText.getText().toString());
				}

				if (isLocationAdd && (longitude == null || latitude == null)) {
					Toast.makeText(context, context.getString(R.string.waiting_for_location), Toast.LENGTH_LONG).show();
				} else {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						publishTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						publishTask.execute();
					}
				}
			} else {
				Toast.makeText(context, context.getString(R.string.nothing_to_say), Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.back_button:
			((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			((BaseMultiFragmentActivity) context).backStackAction();
			break;
		case R.id.p_image:
			if (isImageAdd) {
				isImageAdd = false;
			} else {
				isImageAdd = true;
			}
			PopupWindowManager pwManager = new PopupWindowManager(context);
			pwManager.setupImageSelectorPopup();
			// ((PublishFragmentCallback) context).setupFileBrowserFragment();
			break;
		case R.id.p_location:
			if (isLocationAdd) {
				isLocationAdd = false;
				locationInfo.setVisibility(View.GONE);
			} else {
				LocationTask task = new LocationTask(context);
				task.setOnLocationSeekListener(this);
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}else{
					task.execute();
				}
			}
			break;
		case R.id.p_emoji:
			if (emojiSelector.getVisibility() == View.GONE) {
				((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				emojiSelector.setVisibility(View.VISIBLE);
			} else {
				emojiSelector.setVisibility(View.GONE);
			}
			break;
		case R.id.p_at:
			publishText.append("@");
			break;
		case R.id.p_sharp:
			publishText.append("##");
			publishText.setSelection(publishText.length() - 1, publishText.length() - 1);
			break;
		}
	}
	
	@Override
	public void onSelected(String emojiName){
		publishText.append(emojiName);
	}
	
	@Override
	public void onBackSpace(){
		System.out.println("backspace");
		if(publishText.length() > 0) publishText.setText(String.valueOf(publishText.getText()).substring(0, publishText.getText().length() - 1));
	}

	@Override
	public void onLocationSeeking() {
		
	}

	@Override
	public void onLocationGot(double longitude, double latitude) {
		// TODO Auto-generated method stub
		isLocationAdd = true;
		this.longitude = String.valueOf(longitude);
		this.latitude = String.valueOf(latitude);
		
		locationInfo.setVisibility(View.VISIBLE);
		locationText.setText(this.longitude);
	}

}
