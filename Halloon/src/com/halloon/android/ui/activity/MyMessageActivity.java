package com.halloon.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.halloon.android.ui.fragment.PrivateDetailFragment;
import com.halloon.android.ui.fragment.PrivateMessageFragment.PrivateMessageCallback;
import com.halloon.android.ui.fragment.TabMyMessageFragment;

public class MyMessageActivity extends BaseMultiFragmentActivity implements PrivateMessageCallback{

	@Override
	protected Fragment onCreatePane() {
		TabMyMessageFragment mFragment = new TabMyMessageFragment();
		
		setIsFlipBackEnabled(false);
		
		return mFragment;
	}

	@Override
	protected void init() {
	}

	@Override
	public void setupPrivateDetailFragment(Bundle bundle) {
		setupFragment(new PrivateDetailFragment(), bundle);
	}

}