package com.halloon.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.view.Window;
import com.halloon.android.R;
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
		// TODO Auto-generated method stub
		mFragmentContainer.add(new SettingsAboutFragment());
		
		Fragment mFragment1;
		Fragment mFragment2;
		
		try{
			mFragment1 = getFragmentDec(1);
			mFragment2 = getFragmentDec(2);
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			return;
		}
		
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
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
	protected void init() {
		// TODO Auto-generated method stub
		
	}
	
	

}
