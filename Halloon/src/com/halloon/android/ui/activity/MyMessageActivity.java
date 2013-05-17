package com.halloon.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.halloon.android.R;
import com.halloon.android.ui.fragment.PrivateDetailFragment;
import com.halloon.android.ui.fragment.PrivateMessageFragment.PrivateMessageCallback;
import com.halloon.android.ui.fragment.TabMyMessageFragment;

public class MyMessageActivity extends BaseMultiFragmentActivity implements PrivateMessageCallback{

	@Override
	protected Fragment onCreatePane() {
		TabMyMessageFragment mFragment = new TabMyMessageFragment();
		
		return mFragment;
	}

	@Override
	protected void init() {
	}

	@Override
	public void setupPrivateDetailFragment(Bundle bundle) {
        mFragmentContainer.add(new PrivateDetailFragment());
		
		Fragment mFragment1;
		Fragment mFragment2;
		
		try{
			mFragment1 = getFragmentDec(1);
			mFragment2 = getFragmentDec(2);
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			return;
		}
		
		mFragment1.setArguments(bundle);
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_right_enter,
				                                R.anim.fragment_slide_left_exit,
				                                R.anim.fragment_slide_left_enter,
				                                R.anim.fragment_slide_right_exit);
		fragmentTransaction.add(R.id.root_container, mFragment1);
		fragmentTransaction.hide(mFragment2);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

}