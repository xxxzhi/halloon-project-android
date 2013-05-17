package com.halloon.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.Window;
import com.halloon.android.ui.fragment.TabSettingsFragment;

public class SettingsActivity extends BaseSinglePaneActivity {

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
	protected void init() {

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

	}

}
