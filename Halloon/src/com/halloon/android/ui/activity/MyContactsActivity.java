package com.halloon.android.ui.activity;

import android.support.v4.app.Fragment;

import com.halloon.android.ui.fragment.LBSContactFragment;
import com.halloon.android.ui.fragment.LBSContactFragment.LBSContactFragmentCallback;
import com.halloon.android.ui.fragment.TabMyContactsFragment;
import com.halloon.android.ui.fragment.TabMyContactsFragment.ContactsFragmentCallback;

public class MyContactsActivity extends BaseMultiFragmentActivity implements
		LBSContactFragmentCallback, ContactsFragmentCallback {

	@Override
	protected Fragment onCreatePane() {
		TabMyContactsFragment mFragment = new TabMyContactsFragment();

		return mFragment;
	}

	@Override
	public void setupLBSContactFragment() {
		setupFragment(new LBSContactFragment(), null);
	}

	@Override
	public void destroyLBSFragment() {
		backStackAction();
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}

}
