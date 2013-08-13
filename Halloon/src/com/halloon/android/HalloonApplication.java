package com.halloon.android;

import java.util.HashMap;

import com.halloon.android.data.SettingsManager;

import android.app.Application;

public class HalloonApplication extends Application {
	
	private boolean mainPageStateChanged = false;
	private boolean messagePageStateChanged = false;
	private boolean profilePageStateChanged = false;
	private boolean contactPageStateChanged = false;
	
	private HashMap<String, String> shortList;
	
	@Override
	public void onCreate(){
		super.onCreate();
		
		shortList = new HashMap<String, String>();
	}
	
	
	public void setIsMainPageImageMode(boolean isMainPageImageMode){
		SettingsManager.getInstance(this).setIsMainPageImageMode(isMainPageImageMode);
	}
	
	public boolean getIsMainPageImageMode(){
		return SettingsManager.getInstance(this).getIsMainPageImageMode();
	}
	
	public boolean getMainPageState(){
		return mainPageStateChanged;
	}
	
	public void setMainPageState(boolean mainPageStateChanged){
		this.mainPageStateChanged = mainPageStateChanged;
	}
	
	public boolean getMessagePageState(){
		return messagePageStateChanged;
	}
	
	public void setMessagePageState(boolean messagePageStateChanged){
		this.messagePageStateChanged = messagePageStateChanged;
	}
	
	public boolean getProfilePageState(){
		return profilePageStateChanged;
	}
	
	public void setProfilePageState(boolean profilePageStateChanged){
		this.profilePageStateChanged = profilePageStateChanged;
	}
	
	public boolean getContactPageState(){
		return contactPageStateChanged;
	}
	
	public void setContactPageState(boolean contactPageStateChanged){
		this.contactPageStateChanged = contactPageStateChanged;
	}
	
	public HashMap<String, String> getShortList(){
		return shortList;
	}
}
