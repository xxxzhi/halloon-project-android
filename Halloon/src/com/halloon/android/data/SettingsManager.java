package com.halloon.android.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {

	private static Context context;

	private final static String SETTINGS_FILENAME = "hallon_settings";

	private final static String REQUEST_TOKEN = "requestToken";
	private final static String REQUEST_TOKEN_SECRET = "requestTokenSecret";
	private final static String ACCESS_TOKEN = "access_token";
	private final static String CONTACT_STATUS = "contact_status";
	private final static String PROFILE_STATUS = "profile_status";
	private final static String TWEETLIST_STATUS = "tweet_list_status";
	private final static String PRIVATEMESSGAE_STATUS = "private_message_status";
	private final static String ATLIST_STATUS = "at_list_status";
	private final static String LAST_UPDATE_TIME = "last_update_time";
	private final static String SCREEN_WIDTH = "screen_width";
	private final static String SCREEN_HEIGHT = "screen_height";
	private final static String SCREEN_DENSITY = "screen_density";
	private final static String SYSTEM_BAR_HEIGHT = "system_bar_height";
	private final static String DEVICE_IP = "device_ip";
	private final static String DATABASE_VERSION = "database_version";
	
	private final static String PROFILE_IMG_PATH = "profile_img_path";
	
	private final static String IS_MAINPAGE_IMAGE_MODE = "isMainPageImageMode";

	private static SettingsManager _instance;
	private SharedPreferences settings;

	private SettingsManager() {
		// Defeats instantiationaurelien
	}

	public static SettingsManager getInstance(Context context) {
		SettingsManager.context = context;
		if (_instance == null) {
			_instance = new SettingsManager();
		}
		return _instance;
	}

	private SharedPreferences getSettings() {
		if (this.settings == null) {
			this.settings = context.getSharedPreferences(SETTINGS_FILENAME,
					Context.MODE_PRIVATE);
		}
		return this.settings;
	}

	// /////////////////////////////////////////
	// SAVER HELPERcm
	// /////////////////////////////////////////

	private void saveString(String key, String value) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(key, value);
		editor.commit();
	}

	private void saveBool(String key, boolean value) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	private void saveInt(String key, int value) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putInt(key, value);
		editor.commit();
	}

	private void saveLong(String key, long value) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putLong(key, value);
		editor.commit();
	}

	private void saveFloat(String key, float value) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	// /////////////////////////////////////////
	// GETTER SETTER
	// /////////////////////////////////////////

	public void setRequestToken(String token) {
		saveString(REQUEST_TOKEN, token);
	}

	public String getRequestToken() {
		return getSettings().getString(REQUEST_TOKEN, null);
	}

	public void setRequestTokenSecret(String token) {
		saveString(REQUEST_TOKEN_SECRET, token);
	}

	public String getRequestTokenSecret() {
		return getSettings().getString(REQUEST_TOKEN_SECRET, null);
	}

	public void setContactStatus(int token) {
		saveInt(CONTACT_STATUS, token);
	}

	public int getContactStatus() {
		return getSettings().getInt(CONTACT_STATUS, 0);
	}

	public void setProfileStatus(int token) {
		saveInt(PROFILE_STATUS, token);
	}

	public int getProfileStatus() {
		return getSettings().getInt(PROFILE_STATUS, 0);
	}

	public void setTweetListStatus(int token) {
		saveInt(TWEETLIST_STATUS, token);
	}

	public int getTweetListStatus() {
		return getSettings().getInt(TWEETLIST_STATUS, 0);
	}

	public void setPrivateMessageStatus(int token) {
		saveInt(PRIVATEMESSGAE_STATUS, token);
	}

	public int getPrivateMessageStatus() {
		return getSettings().getInt(PRIVATEMESSGAE_STATUS, 0);
	}

	public void setAtListStatus(int token) {
		saveInt(ATLIST_STATUS, token);
	}

	public int getAtListStatus() {
		return getSettings().getInt(ATLIST_STATUS, 0);
	}

	public void setLastUpdateTime(String token) {
		saveString(LAST_UPDATE_TIME, token);
	}

	public String getLastUpdateTime() {
		return getSettings().getString(LAST_UPDATE_TIME, "");
	}

	public void setScreenWidth(int width) {
		saveInt(SCREEN_WIDTH, width);
	}

	public int getScreenWidth() {
		return getSettings().getInt(SCREEN_WIDTH, 480);
	}

	public void setScreenHeight(int height) {
		saveInt(SCREEN_HEIGHT, height);
	}

	public int getScreenHeight() {
		return getSettings().getInt(SCREEN_HEIGHT, 800);
	}

	public void setScreenDensity(float density) {
		saveFloat(SCREEN_DENSITY, density);
	}

	public float getScreenDensity() {
		float density = 1 ;
		try{
			density=  getSettings().getFloat(SCREEN_DENSITY, 1);
		}catch(Exception e){
			
		}
		return density;
	}

	public void setSystemBarHeight(int height) {
		saveInt(SYSTEM_BAR_HEIGHT, height);
	}

	public int getSystemBarHeight() {
		return getSettings().getInt(SYSTEM_BAR_HEIGHT, 0);
	}

	public void setDeviceIp(String ip) {
		saveString(DEVICE_IP, ip);
	}

	public String getDeviceIp() {
		return getSettings().getString(DEVICE_IP, "127.0.0.1");
	}

	public void setDatabaseVersion(int version) {
		saveInt(DATABASE_VERSION, version);
	}

	public int getDatabaseVersion() {
		return getSettings().getInt(DATABASE_VERSION, 0);
	}

	public void setAccessToken(String accesstoken) {
		saveString(ACCESS_TOKEN, accesstoken);
	}

	public String getAccessToken() {
		return getSettings().getString(ACCESS_TOKEN, "");
	}
	
	//Settings_function
	public void setIsMainPageImageMode(boolean isMainPageImageMode){
		saveBool(IS_MAINPAGE_IMAGE_MODE, isMainPageImageMode);
	}
	
	public boolean getIsMainPageImageMode(){
		return getSettings().getBoolean(IS_MAINPAGE_IMAGE_MODE, true);
	}
	
	/**
	 * profile 的 背景图片
	 * @return
	 */
	public String getProfileBackGroundImg(){
		return getSettings().getString(PROFILE_IMG_PATH, "");
	}
	
	public void setProfileBackGroundImg(String img){
		saveString(PROFILE_IMG_PATH, img);
	}
}
