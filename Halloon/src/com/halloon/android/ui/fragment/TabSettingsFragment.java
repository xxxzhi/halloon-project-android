package com.halloon.android.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.halloon.android.R;
import com.halloon.android.widget.HalloonTitleBar;

public class TabSettingsFragment extends BaseTitleBarFragment{
	
	private CheckBox isMainPageImageMode;

	@Override
	protected void init(HalloonTitleBar titleBar, RelativeLayout content) {
		titleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_NONE);
		titleBar.getTitleTextView().setText(getString(R.string.tab_more));
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content.addView(inflater.inflate(R.layout.tab_settings, null, false));
		
		isMainPageImageMode = (CheckBox) content.findViewById(R.id.browser_mode_switcher);
		isMainPageImageMode.setChecked(mApplication.getIsMainPageImageMode());
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

}
