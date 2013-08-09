package com.halloon.android.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;

import com.halloon.android.R;
import com.halloon.android.ui.BaseActivity;
import com.halloon.android.ui.fragment.CommentFragment;
import com.halloon.android.ui.fragment.EditProfileFragment;
import com.halloon.android.ui.fragment.PublishFragment;
import com.halloon.android.ui.fragment.TabMainPageFragment;
import com.halloon.android.ui.fragment.TabMainPageFragment.MainPageFragmentCallback;
import com.halloon.android.ui.fragment.TabProfileFragment;
import com.halloon.android.ui.fragment.TabProfileFragment.ProfileFragmentCallback;
import com.halloon.android.ui.fragment.TweetDetailFragment;
import com.halloon.android.ui.fragment.TweetDetailFragment.TweetDetailFragmentCallback;
import com.halloon.android.util.PopupWindowManager;

public abstract class BaseMultiFragmentActivity extends BaseActivity implements MainPageFragmentCallback, 
                                                                                TweetDetailFragmentCallback,
		                                                                        ProfileFragmentCallback {
	
	protected PopupWindowManager mPopupWindowManager;

	protected static FragmentManager mFragmentManager;
	
	protected Fragment currentFragment;
	
	private int fragmentCount = 0;
	private String fragmentTag = "Halloon_Fragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singlepane_empty);
		mPopupWindowManager = new PopupWindowManager(this);
		mFragmentManager = getSupportFragmentManager();
		
		currentFragment = onCreatePane();
		
		try{
			//setArguments looking for a answer? support-v4
			currentFragment.setArguments(intentToFragmentArguments(getIntent()));
			
			mFragmentManager.beginTransaction().add(R.id.root_container, currentFragment, fragmentTag + fragmentCount).commit();
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
		}

		homeGoToDashboard = false;
		
		init();
	}
	
	protected abstract Fragment onCreatePane();
	
	protected abstract void init();

	/**
	 * 退栈，后退
	 */
	public void backStackAction() {
		mFragmentManager.popBackStack();
		fragmentCount--;
		currentFragment = mFragmentManager.findFragmentByTag(fragmentTag + fragmentCount);
		Log.d("BASE back", fragmentCount + "<<fragmentCount");
	}

	@Override
	public void setupTweetListFragment(Bundle bundle) {
		TabMainPageFragment fragment = new TabMainPageFragment();
		fragment.setTweetState(TabMainPageFragment.OTHER_TWEET);
		
		setupFragment(fragment, bundle);
	}
	
	@Override
	public void setupEditProfileFragment(Bundle bundle){
		setupFragment(new EditProfileFragment(), bundle);
	}

	@Override
	public void setupProfileFragment(Bundle bundle) {
		TabProfileFragment fragment = new TabProfileFragment();
		fragment.setType(TabProfileFragment.OTHER);
		
		setupFragment(fragment, bundle);
	}

	@Override
	public void setupDetailFragment(Bundle bundle) {
		setupFragment(new TweetDetailFragment(), bundle);
	}

	@Override
	public void setupCommentFragment(Bundle bundle) {
		setupFragment(new CommentFragment(), bundle);
	}
	
	protected void setupFragment(Fragment fragment, Bundle bundle){
		
		if(bundle != null) fragment.setArguments(bundle);
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_right_enter,
				                                R.anim.fragment_slide_left_exit,
				                                R.anim.fragment_slide_left_enter,
				                                R.anim.fragment_slide_right_exit);
		fragmentCount++;
		fragmentTransaction.add(R.id.root_container, fragment, fragmentTag + fragmentCount);
		fragmentTransaction.hide(currentFragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		
		Log.d("BASE", fragmentCount + "<<fragmentCount");
		
		currentFragment = fragment;
	}

	@Override
	public void setupPublishFragment() {
		setupFragment(new PublishFragment(), null);
	}

	@Override
	public void setupAroundTweetFragment() {
		TabMainPageFragment fragment = new TabMainPageFragment();
		fragment.setTweetState(TabMainPageFragment.AROUND_TWEET);
		
		setupFragment(fragment, null);
	}

	@Override
	public void setupPictureDialog(String addr, String preSize, Bitmap bitmap) {
		mPopupWindowManager.setupPicturePopup(addr, preSize, bitmap);
	}

	@Override
	public void mListChange(int status) {
		((TweetDetailFragment) currentFragment).changeMList(status);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && mFragmentManager.getBackStackEntryCount() >= 1) {
			backStackAction();
			return true;
		}

		return false;
	}

}
