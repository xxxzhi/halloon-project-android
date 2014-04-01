package com.halloon.android.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.halloon.android.R;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.DBManager;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.listener.OnTitleBarClickListener;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;
import com.halloon.android.ui.activity.OAuthV2Activity;
import com.halloon.android.ui.activity.SlideHomeActivity;
import com.halloon.android.util.NumberUtil;
import com.halloon.android.widget.HalloonTitleBar;

public class TabSettingsFragment extends BaseTitleBarFragment implements OnClickListener{
	
	private CheckBox isMainPageImageMode;
	private SettingAboutCallback aboutCallback;
	
	private Context context;
	
	private Button clearButton,exitButton;

	@Override
	protected void init(HalloonTitleBar titleBar, RelativeLayout content) {
		titleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_NONE);
		titleBar.setOnTitleBarClickListener(this);
		titleBar.getTitleTextView().setText(getString(R.string.tab_more));
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content.addView(inflater.inflate(R.layout.tab_settings, null, false));
		
		isMainPageImageMode = (CheckBox) content.findViewById(R.id.browser_mode_switcher);
		isMainPageImageMode.setChecked(mApplication.getIsMainPageImageMode());
		
		content.findViewById(R.id.setting).setOnClickListener(this);
		content.findViewById(R.id.account).setOnClickListener(this);
		content.findViewById(R.id.browser).setOnClickListener(this);
		content.findViewById(R.id.theme).setOnClickListener(this);
		content.findViewById(R.id.systemplugin).setOnClickListener(this);
		content.findViewById(R.id.hotsoftware).setOnClickListener(this);
		content.findViewById(R.id.privacy).setOnClickListener(this);
		content.findViewById(R.id.flowanaly).setOnClickListener(this);
		content.findViewById(R.id.about).setOnClickListener(this);
		content.findViewById(R.id.feedback).setOnClickListener(this); 
		clearButton = (Button) content.findViewById(R.id.clear);
		exitButton = (Button) content.findViewById(R.id.exit);
		
		exitButton.setOnClickListener(this);
		clearButton.setOnClickListener(this);
		 
	}
	
	public interface SettingAboutCallback{
		public void setSettingAboutFragment();
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		this.aboutCallback = (SettingAboutCallback) activity;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		clearButton.setText(context.getString(R.string.clear_cache) + "(" + NumberUtil.formatBytesSize(context, ImageLoader.getInstance(context).checkSize()) + ")");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		isMainPageImageMode.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
				mApplication.setIsMainPageImageMode(isChecked);
				mApplication.setMainPageState(true);
			}
		});
	}
	@Override
	public void onTitleContentClick(int contentEnum) {
		switch(contentEnum){
		case OnTitleBarClickListener.LEFT_BUTTON:
			((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			((BaseMultiFragmentActivity) context).backStackAction();
			break;
		case OnTitleBarClickListener.LEFT_IMAGE_BUTTON:
			Activity parent = getActivity().getParent() ;
			if(parent instanceof SlideHomeActivity){
				((SlideHomeActivity)parent).toggleSlideMenu();
			}
			break;
		}
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.about:
			aboutCallback.setSettingAboutFragment();
			break;
		case R.id.feedback:
			
			break;
			
		case R.id.flowanaly:
			
			break;
		case R.id.hotsoftware:
			
			break;
		case R.id.systemplugin:
			
			break;
		case R.id.theme:
			
			break;
		case R.id.account:
			
			break;
		case R.id.browser:
			break;
		case R.id.setting:
			break;
			
		case R.id.clear:
			ImageLoader.getInstance(context).clearCache();
			clearButton.setText(context.getString(R.string.clear_cache) + "(" + NumberUtil.formatBytesSize(context, ImageLoader.getInstance(context).checkSize()) + ")");
			break;
		case R.id.exit:
			ImageLoader.getInstance(context).clearCache();
			OAuthV2Activity.clearOAuth(mActivity);
			Intent intent = new Intent();
			intent.setClass(mActivity, OAuthV2Activity.class);
			DBManager.getInstance(mActivity).clear();
			ContentManager.clear();
			mActivity.startActivity(intent);
			mActivity.finish();
			break;
		default:
			break;
		}
		
	}

	

}
