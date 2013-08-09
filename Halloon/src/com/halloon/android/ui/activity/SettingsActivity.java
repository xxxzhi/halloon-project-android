package com.halloon.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;

import com.halloon.android.ui.fragment.SettingsAboutFragment;
import com.halloon.android.ui.fragment.TabSettingsFragment;
import com.halloon.android.ui.fragment.TabSettingsFragment.SettingAboutCallback;

public class SettingsActivity extends BaseMultiFragmentActivity implements SettingAboutCallback{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		homeGoToDashboard = false;

	}

	@Override
	protected Fragment onCreatePane() {

		return new TabSettingsFragment();
	}

	
	

	@Override
	public void setSettingAboutFragment() {
		setupFragment(new SettingsAboutFragment(), null);
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}
	
	

}
