package com.halloon.android.listener;

public interface OnTitleBarClickListener {
	public static final int LEFT_BUTTON = 0;
	public static final int RIGHT_BUTTON = 1;
	public static final int LEFT_IMAGE_BUTTON = 2;
	public static final int RIGHT_IMAGE_BUTTON = 3;
	public static final int TITLE_TEXT_VIEW = 4;
	
	public void onTitleContentClick(int contentEnum);
}
