package com.halloon.android.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockFragment;

import com.halloon.android.HalloonApplication;
import com.halloon.android.R;
import com.halloon.android.widget.HalloonTitleBar;

public abstract class BaseTitleBarFragment extends SherlockFragment {
	
	protected HalloonApplication mApplication;
	
	protected HalloonTitleBar mTitleBar;
	protected RelativeLayout mContent;
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		mApplication = (HalloonApplication) activity.getApplication();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		View root = inflater.inflate(R.layout.skeleton, null, false);
		
		mTitleBar = (HalloonTitleBar) root.findViewById(R.id.title_layout);
		mContent = (RelativeLayout) root.findViewById(R.id.layout_content);
		
		init(mTitleBar, mContent);
		
		return root;
	}
	
	protected abstract void init(HalloonTitleBar titleBar, RelativeLayout content);

}
