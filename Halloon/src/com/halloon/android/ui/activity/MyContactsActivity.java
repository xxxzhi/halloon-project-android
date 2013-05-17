package com.halloon.android.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.halloon.android.R;
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
		mFragmentContainer.add(new LBSContactFragment());
		
		Fragment mFragment1;
		Fragment mFragment2;
		
		try{
			mFragment1 = getFragmentDec(1);
			mFragment2 = getFragmentDec(2);
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			return;
		}
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.setCustomAnimations(
				R.anim.fragment_slide_right_enter,
				R.anim.fragment_slide_left_exit,
				R.anim.fragment_slide_left_enter,
				R.anim.fragment_slide_right_exit);
		fragmentTransaction.add(R.id.root_container, mFragment1);
		fragmentTransaction.hide(mFragment2);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
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
