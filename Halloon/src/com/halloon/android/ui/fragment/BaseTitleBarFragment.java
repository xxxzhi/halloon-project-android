package com.halloon.android.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.halloon.android.HalloonApplication;
import com.halloon.android.R;
import com.halloon.android.listener.OnTitleBarClickListener;
import com.halloon.android.ui.activity.SlideHomeActivity;
import com.halloon.android.widget.HalloonTitleBar;

/**
 * 基础标题框架
 * @author 7heaven
 *
 */
public abstract class BaseTitleBarFragment extends Fragment implements OnTitleBarClickListener {
	
	protected HalloonApplication mApplication;
	
	protected HalloonTitleBar mTitleBar;
	protected RelativeLayout mContent;
	
	protected Activity mActivity;
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		mActivity = activity;
		mApplication = (HalloonApplication) activity.getApplication();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		final View root = inflater.inflate(R.layout.skeleton, null, false);
		
		mTitleBar = (HalloonTitleBar) root.findViewById(R.id.title_layout);
		mContent = (RelativeLayout) root.findViewById(R.id.layout_content);
		
		init(mTitleBar, mContent);
		
		return root;
	}

	protected abstract void init(HalloonTitleBar titleBar, RelativeLayout content);
	
	@Override
	public void onTitleContentClick(int contentEnum) {}
}
