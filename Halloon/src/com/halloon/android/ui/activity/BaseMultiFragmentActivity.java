package com.halloon.android.ui.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.halloon.android.R;
import com.halloon.android.bean.FamousBean;
import com.halloon.android.bean.TopicBean;
import com.halloon.android.bean.TweetBean;
import com.halloon.android.ui.BaseActivity;
import com.halloon.android.ui.fragment.CommentFragment;
import com.halloon.android.ui.fragment.EditProfileFragment;
import com.halloon.android.ui.fragment.PublishFragment;
import com.halloon.android.ui.fragment.SquareFamousListFragment;
import com.halloon.android.ui.fragment.SquareReListFragment;
import com.halloon.android.ui.fragment.TabMainPageFragment;
import com.halloon.android.ui.fragment.TabMainPageFragment.MainPageFragmentCallback;
import com.halloon.android.ui.fragment.TabProfileFragment;
import com.halloon.android.ui.fragment.TabProfileFragment.ProfileFragmentCallback;
import com.halloon.android.ui.fragment.TabSquareFragment.SquareFragmentCallback;
import com.halloon.android.ui.fragment.TweetDetailFragment;
import com.halloon.android.ui.fragment.TweetDetailFragment.TweetDetailFragmentCallback;
import com.halloon.android.ui.fragment.UserSimpleListFragment;
import com.halloon.android.util.GifDecoder;
import com.halloon.android.util.PopupWindowManager;

public abstract class BaseMultiFragmentActivity extends BaseActivity 
implements MainPageFragmentCallback,TweetDetailFragmentCallback,ProfileFragmentCallback,SquareFragmentCallback{
	

	protected PopupWindowManager mPopupWindowManager;

	protected FragmentManager mFragmentManager;
	
	protected Fragment currentFragment;
	
	private int fragmentCount = 0;
	private String fragmentTag = "Halloon_Fragment";
	
	private float mx;
	private float my;
	
	private int isPerformBackStackAction = 0;
	
	private boolean isEnableFlipBack = true;

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
		
		//JNI startup test,just ignore it
		Log.d("FROM JNI", GifDecoder.getTweetBean().getName());
	}
	
	protected abstract Fragment onCreatePane();
	
	protected abstract void init();
	
	public void setIsFlipBackEnabled(boolean enable){
		isEnableFlipBack = enable;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event){
		if(isEnableFlipBack){
			switch(event.getAction() & MotionEvent.ACTION_MASK){
			case MotionEvent.ACTION_DOWN:
				mx = event.getX();
				my = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				int dx = (int) (event.getX() - mx);
				int dy = (int) (event.getY() - my);
				if(Math.abs(dx) > Math.abs(dy) && dx > this.getWindow().getDecorView().getMeasuredWidth() * 0.2F && mFragmentManager.getBackStackEntryCount() >= 1){
					Log.d("BASE", "backStackAction perform touch");
					isPerformBackStackAction++;
					if(isPerformBackStackAction == 1){
						backStackAction();
					}
					return false;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				isPerformBackStackAction = 0;
				break;
			}
		}
		
		return super.dispatchTouchEvent(event);
	}
	

	/**
	 * 退栈，后退
	 */
	public void backStackAction() {
		mFragmentManager.popBackStack();
		fragmentCount--;
		currentFragment = mFragmentManager.findFragmentByTag(fragmentTag + fragmentCount);
		Log.d("BASE back", fragmentCount + "<<fragmentCount");
		
		changeSlideEnable();
	}
	
	private void changeSlideEnable(){
		//make sure that slidemenu is unenable. when this is not the top fragment stack
		Activity activity = getParent();
		if(activity != null && activity instanceof SlideHomeActivity){
			if(fragmentCount == 0 ){
				((SlideHomeActivity)activity).setSlidingEnabled(true);
			}else{
				((SlideHomeActivity)activity).setSlidingEnabled(false);
			}
		}
	}
	
	@Override
	public void setupTweetListFragment(Bundle bundle) {
		TabMainPageFragment fragment = new TabMainPageFragment();
		fragment.setTweetState(TabMainPageFragment.OTHER_TWEET);
		
		setupFragment(fragment, bundle);
		
	}
	

	@Override
	public void setSquareFamousList(ArrayList<FamousBean> famous) {
		SquareFamousListFragment fragment = new SquareFamousListFragment();
		fragment.setFamousBeans(famous);
		
		setupFragment(fragment, new Bundle());
	}

	@Override
	public void setSquareTopicList(ArrayList<TopicBean> topic) {
		
	}

	@Override
	public void setSquareHotReList(ArrayList<TweetBean> hotReBeans) {
		SquareReListFragment fragment = new SquareReListFragment();
		fragment.setHotReBeans(hotReBeans);
		
		setupFragment(fragment, new Bundle());
	}
	
	
	@Override
	public void setupEditProfileFragment(Bundle bundle){
		setupFragment(new EditProfileFragment(), bundle);
		
	}

	@Override
	public void setupProfileFragment(Bundle bundle) {
		TabProfileFragment fragment = new TabProfileFragment();
		
		//判断id是否跟自己的相同
		int type = bundle.getInt("type",TabProfileFragment.OTHER);
		fragment.setType(type);
		
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
		
		
		changeSlideEnable();
	}

	@Override
	public void setupPublishFragment() {
		setupFragment(new PublishFragment(), null);
		
		Activity activity = getParent();
		if(activity != null && activity instanceof SlideHomeActivity){
			((SlideHomeActivity)activity).setSlidingEnabled(false);
		}
	}

	@Override
	public void setupAroundTweetFragment() {
		TabMainPageFragment fragment = new TabMainPageFragment();
		fragment.setTweetState(TabMainPageFragment.AROUND_TWEET);
		
		setupFragment(fragment, null);
	}

	
	
	@Override
	public void setupIdolListFragment(String name,String fopenid) {
		UserSimpleListFragment fragment = new UserSimpleListFragment();
		
		Bundle bundle = new Bundle();
		fragment.setUserType( UserSimpleListFragment.IDOL);
//		bundle.putInt("type", UserSimpleListFragment.IDOL);
		bundle.putString("name",name);
		bundle.putString("fopenid",fopenid);
		setupFragment(fragment, bundle);
	}

	@Override
	public void setupFansListFragment(String name,String fopenid) {
		UserSimpleListFragment fragment = new UserSimpleListFragment();
		
		Bundle bundle = new Bundle();
		fragment.setUserType( UserSimpleListFragment.FANS);
		bundle.putString("name",name);
		bundle.putString("fopenid",fopenid);
		setupFragment(fragment, bundle);
	}

	public void setupUserListFragment(Bundle bundle) {
		
		
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
