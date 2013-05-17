package com.halloon.android.ui.activity;

import android.support.v4.app.Fragment;

import com.halloon.android.ui.fragment.TabProfileFragment;

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
