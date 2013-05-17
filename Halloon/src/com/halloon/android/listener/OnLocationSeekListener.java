package com.halloon.android.listener;

public interface OnLocationSeekListener {
	
	public void onLocationSeeking();
	
    public void onLocationGot(double longitude, double latitude);
}
