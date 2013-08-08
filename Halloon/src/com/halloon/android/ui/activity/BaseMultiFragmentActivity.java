package com.halloon.android.ui.activity;

import java.util.ArrayList;
import java.lang.ArrayIndexOutOfBoundsException;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.halloon.android.R;
import com.halloon.android.ui.BaseActivity;
import com.halloon.android.ui.fragment.CommentFragment;
import com.halloon.android.ui.fragment.EditProfileFragment;
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
	
	protected ArrayList<Fragment> mFragmentContainer;
	protected PopupWindowManager mPopupWindowManager;

	protected FragmentManager mFragmentManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singlepane_empty);
		mFragmentContainer = new ArrayList<Fragment>();
		mPopupWindowManager = new PopupWindowManager(this);
		mFragmentManager = getSupportFragmentManager();
		
		mFragmentContainer.add(onCreatePane());
		
		try{
			//setArguments looking for a answer? support-v4
			getFragmentDec(1).setArguments(intentToFragmentArguments(getIntent()));
			
			getSupportFragmentManager().beginTransaction().add(R.id.root_container, getFragmentDec(1)).commit();
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
		mFragmentContainer.remove(getFragmentDec(1));
	}

	@Override
	public void setupTweetListFragment(Bundle bundle) {
		mFragmentContainer.add(new TabMainPageFragment());
		
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
		((TabMainPageFragment) mFragment1).setTweetState(TabMainPageFragment.OTHER_TWEET);
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
	
	@Override
	public void setupEditProfileFragment(Bundle bundle){
		mFragmentContainer.add(new EditProfileFragment());
		
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

	@Override
	public void setupProfileFragment(Bundle bundle) {
		mFragmentContainer.add(new TabProfileFragment());
		
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
		((TabProfileFragment) mFragment1).setType(TabProfileFragment.OTHER);
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

	@Override
	public void setupDetailFragment(Bundle bundle) {
		mFragmentContainer.add(new TweetDetailFragment());
		
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

	@Override
	public void setupCommentFragment(Bundle bundle) {
		mFragmentContainer.add(new CommentFragment());
		
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

	@Override
	public void setupPublishFragment() {
	}

	@Override
	public void setupAroundTweetFragment() {
	}

	@Override
	public void setupPictureDialog(String addr, String preSize, Bitmap bitmap) {
		mPopupWindowManager.setupPicturePopup(addr, preSize, bitmap);
	}

	@Override
	public void mListChange(int status) {
		((TweetDetailFragment) getFragmentDec(1)).changeMList(status);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && mFragmentContainer.size() > 1) {
			backStackAction();
			return true;
		}

		return false;
	}

	protected Fragment getFragmentDec(int decCount) throws ArrayIndexOutOfBoundsException {
		System.out.println(mFragmentContainer.size() + ":" + decCount);
		
		Fragment fragment = new Fragment();
		
		fragment = mFragmentContainer.get(mFragmentContainer.size() - decCount);
		
		return fragment;
	}

}
