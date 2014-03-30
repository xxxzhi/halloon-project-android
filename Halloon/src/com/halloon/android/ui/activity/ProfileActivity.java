package com.halloon.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.halloon.android.data.SettingsManager;
import com.halloon.android.ui.fragment.TabProfileFragment;
import com.halloon.android.ui.fragment.TweetDetailFragment.TweetDetailFragmentCallback;

public class ProfileActivity extends BaseMultiFragmentActivity {

	@Override
	protected Fragment onCreatePane() {
		TabProfileFragment mFragment = new TabProfileFragment();
	
		return mFragment;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}

}
