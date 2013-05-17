package com.halloon.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.halloon.android.R;
import com.halloon.android.ui.BaseActivity;

public abstract class BaseSinglePaneActivity extends BaseActivity {
	protected Fragment mFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singlepane_empty);

		if (savedInstanceState == null) {
			mFragment = onCreatePane();
			mFragment.setArguments(intentToFragmentArguments(getIntent()));

			getSupportFragmentManager().beginTransaction()
					.add(R.id.root_container, mFragment).commit();

		}

		init();
	}

	/**
	 * Called in <code>onCreate</code> when the fragment constituting this
	 * activity is needed. The returned fragment's arguments will be set to the
	 * intent used to invoke this activity.
	 */
	protected abstract Fragment onCreatePane();

	protected abstract void init();
}