package com.halloon.android.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.halloon.android.R;

public class Skeleton extends LinearLayout{
	
	public static final int ENVIRONMENT_MAINPAGE = 0;
	public static final int ENVIRONMENT_MESSAGEPAGE = 1;
	public static final int ENVIRONMENT_SELF_PROFILE = 2;
	public static final int ENVIRONMENT_PROFILE = 3;
	public static final int ENVIRONMENT_CONTACTS = 4;
	public static final int ENVIRONMENT_LBS_CONTACTS = 5;
	public static final int ENVIRONMENT_SURROUND_TWEET_PAGE = 6;
	
	private int environment;
	
	private Button buttonMainPage;
	private Button buttonMessagePage;
	private Button buttonProfilePage;
	private Button buttonContactsPage;
	private Button buttonSettingsPage;
	
	public Skeleton(Context context){
		this(context, null);
	}
	
	public Skeleton(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public Skeleton(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
		inflater.inflate(R.layout.skeleton, this, true);
	}

}
