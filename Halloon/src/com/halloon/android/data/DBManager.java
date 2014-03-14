package com.halloon.android.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.halloon.android.bean.PrivateDataBean;
import com.halloon.android.bean.ProfileBean;
import com.halloon.android.bean.TweetBean;
import com.halloon.android.bean.UserBean;
import com.lhws.android.uitl.StringMatcher;

public class DBManager extends SQLiteOpenHelper {
	private static Context context;
	private static DBManager _instance;

	private static final int DATABASE_VERSION = 18;

	public final static int CONTACT_STATUS_INIT = 0;
	public final static int CONTACT_STATUS_TO_UPDATE = 1;
	public final static int CONTACT_STATUS_READY = 2;
	
	//profile status I has change the init state to 1
	public final static int PROFILE_STATUS_INIT = 0;
	public final static int PROFILE_STATUS_READY = 1;

	public final static int TWEET_LIST_STATUS_INIT = 0;
	public final static int TWEET_LIST_STATUS_READY = 1;

	public final static int PRIVATE_MESSAGE_STATUS_INIT = 0;
	public final static int PRIVATE_MESSAGE_STATUS_READY = 1;

	public final static int AT_LIST_STATUS_INIT = 0;
	public final static int AT_LIST_STATUS_READY = 1;

	// Database Name
	private static final String HALLOON_DATABASE = "halloon_data";

	// Contacts table name
	public static final String TABLE_CONTACTS_ME = "contacts_me";
	public static final String TABLE_MY_PROFILE = "my_profile";
	public static final String TABLE_TWEET_LIST = "tweet_list";
	public static final String TABLE_PRIVATE_LIST = "private_list";
	public static final String TABLE_AT_LIST = "at_list";

	// Contacts Table Columns names
	private class ContactsColumns {
		private static final String KEY_OPEN_ID = "id";
		private static final String KEY_NAME = "name";
		private static final String KEY_NICK = "nick";
		private static final String KEY_NAME_FORMATTED = "name_formatted";
		private static final String KEY_HEAD = "head";
		private static final String KEY_SEX = "sex";
		private static final String KEY_TWEET = "tweet";
	}

	private class ProfileColumns {
		private static final String PROFILE_OPEN_ID = "id";
		private static final String PROFILE_NICK = "nick";
		private static final String PROFILE_NAME = "name";
		private static final String PROFILE_HEAD = "head";
		private static final String PROFILE_SEX = "sex";
		private static final String PROFILE_INTRO = "introduction";
		private static final String PROFILE_LOCATION = "location";
		private static final String PROFILE_TAG = "tag";
		private static final String PROFILE_TWEETNUM = "tweet_num";
		private static final String PROFILE_IDOLNUM = "idol_num";
		private static final String PROFILE_FANSNUM = "fans_num";
		private static final String PROFILE_FAVNUM = "fav_num";
	}

	private class TweetsColumns {
		private static final String TWEET_USER_ID = "openid";
		private static final String TWEET_ID = "id";
		private static final String TWEET_HEAD = "head";
		private static final String TWEET_NICK = "nick";
		private static final String TWEET_NAME = "name";
		private static final String TWEET_TIMESTAMP = "timestamp";
		private static final String TWEET_FROM = "send_from";
		private static final String TWEET_CONTENT = "tweet_content";
		private static final String TWEET_IMAGE = "tweet_image";
		private static final String TWEET_VIDEO_IMAGE = "tweet_video_image";
		private static final String TWEET_VIDEO_PLAYER = "tweet_video_player";
		private static final String TWEET_VIDEO_URL = "tweet_video_url";
		private static final String TWEET_MUSIC_AUTHOR = "tweet_music_author";
		private static final String TWEET_MUSIC_ID = "tweet_music_id";
		private static final String TWEET_MUSIC_TITLE = "tweet_music_title";
		private static final String TWEET_MUSIC_URL = "tweet_music_url";
		private static final String COMMENT_COUNT = "comment_count";
		private static final String R_COUNT = "r_count";
		private static final String SOURCE_NICK = "source_nick";
		private static final String SOURCE_CONTENT = "source_content";
		private static final String SOURCE_IMAGE = "source_image";
		private static final String SOURCE_VIDEO_IMAGE = "source_video_image";
		private static final String SOURCE_VIDEO_PLAYER = "source_video_player";
		private static final String SOURCE_VIDEO_URL = "source_video_url";
		private static final String SOURCE_MUSIC_AUTHOR = "source_music_author";
		private static final String SOURCE_MUSIC_ID = "source_music_id";
		private static final String SOURCE_MUSIC_TITLE = "source_music_title";
		private static final String SOURCE_MUSIC_URL = "source_music_url";
		private static final String SOURCE_LONGITUDE = "source_longitude";
		private static final String SOURCE_LATITUDE = "source_latitude";
		private static final String SOURCE_GEO = "source_geo";
		private static final String TWEET_MUSER = "mentioned_user";
		private static final String TWEET_IS_VIP = "is_vip";
		private static final String TWEET_LONGITUDE = "tweet_longitude";
		private static final String TWEET_LATITUDE = "tweet_latitude";
		private static final String TWEET_GEO = "tweet_geo";
	}

	private class PrivateColumns {
		private static final String PRIVATE_ID = "id";
		private static final String PRIVATE_HEAD = "head";
		private static final String PRIVATE_NAME = "name";
		private static final String PRIVATE_NICK = "nick";
		private static final String PRIVATE_CONTENT = "content";
		private static final String PRIVATE_IMAGE = "image";
		private static final String PRIVATE_TIMESTAMP = "timestamp";
	}

	public static DBManager getInstance(Context context) {
		DBManager.context = context;
		if (_instance == null) {
			_instance = new DBManager(context);
		}

		return _instance;
	}

	private DBManager(Context context) {
		super(context, HALLOON_DATABASE, null, DATABASE_VERSION);

	}

	public void upgradeProfile(ProfileBean profileBean) {
		ContentValues cv = createProfileContent(profileBean);
		SQLiteDatabase db = getWritableDatabase();

		long rt = db.replace(TABLE_MY_PROFILE, null, cv);
		
		//if insert success ,update the profile status
		if(rt != - 1){
			SettingsManager.getInstance(context).setProfileStatus(DBManager.PROFILE_STATUS_READY);
		}
		
		//update tweet
		ArrayList<TweetBean> tb = new ArrayList<TweetBean>();
		tb.add(profileBean.getTweetBean());
		addTweetListContent(tb, false);
		
	}

	public ProfileBean getProfile() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_MY_PROFILE;
		Cursor cursor = db.rawQuery(sql, null);

		ProfileBean profileBean = new ProfileBean();

		if (cursor.moveToFirst()) {
			profileBean.setOpenId(cursor.getString(cursor.getColumnIndex(ProfileColumns.PROFILE_OPEN_ID)));
			profileBean.setNick(cursor.getString(cursor.getColumnIndex(ProfileColumns.PROFILE_NICK)));
			profileBean.setName(cursor.getString(cursor.getColumnIndex(ProfileColumns.PROFILE_NAME)));
			profileBean.setHead(cursor.getString(cursor.getColumnIndex(ProfileColumns.PROFILE_HEAD)));
			profileBean.setSex(cursor.getString(cursor.getColumnIndex(ProfileColumns.PROFILE_SEX)));
			profileBean.setIntroduction(cursor.getString(cursor.getColumnIndex(ProfileColumns.PROFILE_INTRO)));
			profileBean.setLocation(cursor.getString(cursor.getColumnIndex(ProfileColumns.PROFILE_LOCATION)));
			profileBean.obtainTag(cursor.getString(cursor.getColumnIndex(ProfileColumns.PROFILE_TAG)));
			profileBean.setTweetNum(cursor.getString(cursor.getColumnIndex(ProfileColumns.PROFILE_TWEETNUM)));
			profileBean.setIdolNum(cursor.getString(cursor.getColumnIndex(ProfileColumns.PROFILE_IDOLNUM)));
			profileBean.setFansNum(cursor.getString(cursor.getColumnIndex(ProfileColumns.PROFILE_FANSNUM)));
			profileBean.setFavNum(cursor.getString(cursor.getColumnIndex(ProfileColumns.PROFILE_FAVNUM)));

		}
		
		if (!cursor.isClosed())
			cursor.close();
		if(profileBean.getOpenId() != null ){
			sql = "select * from " + TABLE_TWEET_LIST + " where "+ TweetsColumns.TWEET_USER_ID +
					"= ? ORDER BY " + TweetsColumns.TWEET_TIMESTAMP + " desc limit 1";
			cursor = db.rawQuery(sql, new String[]{profileBean.getOpenId()});
			ArrayList<TweetBean> tweetList = getTweetByCursor(cursor);
			if(tweetList.size() > 0){
				profileBean.setTweetBean(tweetList.get(0));
			}
		}else{
			profileBean.setTweetBean(new TweetBean());
		}
		return profileBean;
	}

	public void addTweetListContent(ArrayList<TweetBean> tweetBeans, boolean isUpdate) {
		SQLiteDatabase db = getWritableDatabase();

		if (isUpdate)
			db.delete(TABLE_TWEET_LIST, null, null);

		int i = 0;
		do {
			ContentValues cv = createTweetsContent(tweetBeans.get(i));

			db.insert(TABLE_TWEET_LIST, null, cv);
		} while (++i < tweetBeans.size());

		SettingsManager.getInstance(context).setTweetListStatus(TWEET_LIST_STATUS_READY);
	}

	public String getTimestamp(String status) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT " + TweetsColumns.TWEET_TIMESTAMP + " FROM " + TABLE_TWEET_LIST + " ORDER BY " + TweetsColumns.TWEET_TIMESTAMP + " ASC";
		Cursor cursor = db.rawQuery(sql, null);

		if (status == "FIRST")
			cursor.moveToFirst();
		else
			cursor.moveToLast();

		String timeStamp = cursor.getString(0);

		if (!cursor.isClosed())
			cursor.close();

		return timeStamp;
	}

	public ArrayList<TweetBean> getAllTweetList() {

		return getAllTweetList("DESC");
	}
	
	public ArrayList<TweetBean> getAllTweetList(String order) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_TWEET_LIST + " ORDER BY " + TweetsColumns.TWEET_TIMESTAMP + " " + order;
		Cursor cursor = db.rawQuery(sql, null);
		
		return getTweetByCursor(cursor);
	}

	private ArrayList<TweetBean> getTweetByCursor(Cursor cursor) {
		ArrayList<TweetBean> tweetLists = new ArrayList<TweetBean>();
		if (cursor.moveToFirst()) {
			do {
				TweetBean tweetList;

				tweetList = new TweetBean();
				tweetList.setId(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_ID)));
				tweetList.setHead(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_HEAD)));
				tweetList.setNick(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_NICK)));
				tweetList.setName(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_NAME)));
				tweetList.setTimestamp(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_TIMESTAMP)));
				tweetList.setFrom(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_FROM)));
				tweetList.setText(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_CONTENT)));
				tweetList.setTweetImage(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_IMAGE)));
				tweetList.setVideoImage(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_VIDEO_IMAGE)));
				tweetList.setVideoPlayer(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_VIDEO_PLAYER)));
				tweetList.setVideoUrl(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_VIDEO_URL)));
				tweetList.setMusicAuthor(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_MUSIC_AUTHOR)));
				tweetList.setMusicId(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_MUSIC_ID)));
				tweetList.setMusicTitle(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_MUSIC_TITLE)));
				tweetList.setMusicUrl(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_MUSIC_URL)));
				tweetList.setMCount(cursor.getString(cursor.getColumnIndex(TweetsColumns.COMMENT_COUNT)));
				tweetList.setCount(cursor.getString(cursor.getColumnIndex(TweetsColumns.R_COUNT)));
				if (null != cursor.getString(cursor.getColumnIndex(TweetsColumns.SOURCE_NICK))) {
					TweetBean source = new TweetBean();
					source.setNick(cursor.getString(cursor.getColumnIndex(TweetsColumns.SOURCE_NICK)));
					source.setText(cursor.getString(cursor.getColumnIndex(TweetsColumns.SOURCE_CONTENT)));
					source.setTweetImage(cursor.getString(cursor.getColumnIndex(TweetsColumns.SOURCE_IMAGE)));
					source.setVideoImage(cursor.getString(cursor.getColumnIndex(TweetsColumns.SOURCE_VIDEO_IMAGE)));
					source.setVideoPlayer(cursor.getString(cursor.getColumnIndex(TweetsColumns.SOURCE_VIDEO_PLAYER)));
					source.setVideoUrl(cursor.getString(cursor.getColumnIndex(TweetsColumns.SOURCE_VIDEO_URL)));
					source.setMusicAuthor(cursor.getString(cursor.getColumnIndex(TweetsColumns.SOURCE_MUSIC_AUTHOR)));
					source.setMusicId(cursor.getString(cursor.getColumnIndex(TweetsColumns.SOURCE_MUSIC_ID)));
					source.setMusicTitle(cursor.getString(cursor.getColumnIndex(TweetsColumns.SOURCE_MUSIC_TITLE)));
					source.setMusicUrl(cursor.getString(cursor.getColumnIndex(TweetsColumns.SOURCE_MUSIC_URL)));
					source.setLongitude(cursor.getString(cursor.getColumnIndex(TweetsColumns.SOURCE_LONGITUDE)));
					source.setLatitude(cursor.getString(cursor.getColumnIndex(TweetsColumns.SOURCE_LATITUDE)));
					source.setGeo(cursor.getString(cursor.getColumnIndex(TweetsColumns.SOURCE_GEO)));

					tweetList.setSource(source);
				}
				tweetList.setMentionedUser(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_MUSER)));
				tweetList.setIsVip(cursor.getInt(cursor.getColumnIndex(TweetsColumns.TWEET_IS_VIP)));
				tweetList.setLongitude(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_LONGITUDE)));
				tweetList.setLatitude(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_LATITUDE)));
				tweetList.setGeo(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_GEO)));
				tweetList.setOpenId(cursor.getString(cursor.getColumnIndex(TweetsColumns.TWEET_USER_ID)));

				tweetLists.add(tweetList);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed())
			cursor.close();

		return tweetLists;
	}

	public void delTweetContent(String id) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_TWEET_LIST, TweetsColumns.TWEET_ID + "=?", new String[] { id });
	}

	public void addPrivateMessage(ArrayList<PrivateDataBean> privateBeans, boolean isUpdate) {
		SQLiteDatabase db = getWritableDatabase();

		if (isUpdate)
			db.delete(TABLE_PRIVATE_LIST, null, null);

		int i = 0;
		do {
			ContentValues cv = createPrivateContent(privateBeans.get(i));

			db.insert(TABLE_PRIVATE_LIST, null, cv);

		} while (++i < privateBeans.size());

		SettingsManager.getInstance(context).setPrivateMessageStatus(PRIVATE_MESSAGE_STATUS_READY);
	}

	public ArrayList<PrivateDataBean> getPrivateMessage() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_PRIVATE_LIST + " ORDER BY " + PrivateColumns.PRIVATE_TIMESTAMP + " DESC";
		Cursor cursor = db.rawQuery(sql, null);

		ArrayList<PrivateDataBean> privateList = new ArrayList<PrivateDataBean>();

		if (cursor.moveToFirst()) {
			do {
				PrivateDataBean privateDataBean = new PrivateDataBean();
				privateDataBean.setOpenId(cursor.getString(cursor.getColumnIndex(PrivateColumns.PRIVATE_ID)));
				privateDataBean.setHead(cursor.getString(cursor.getColumnIndex(PrivateColumns.PRIVATE_HEAD)));
				privateDataBean.setName(cursor.getString(cursor.getColumnIndex(PrivateColumns.PRIVATE_NAME)));
				privateDataBean.setNick(cursor.getString(cursor.getColumnIndex(PrivateColumns.PRIVATE_NICK)));
				privateDataBean.setText(cursor.getString(cursor.getColumnIndex(PrivateColumns.PRIVATE_CONTENT)));
				privateDataBean.setImage(cursor.getString(cursor.getColumnIndex(PrivateColumns.PRIVATE_IMAGE)));
				privateDataBean.setPubTime(cursor.getString(cursor.getColumnIndex(PrivateColumns.PRIVATE_TIMESTAMP)));

				privateList.add(privateDataBean);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed())
			cursor.close();

		return privateList;
	}

	public void addAtList(ArrayList<TweetBean> tweetBeans, boolean isUpdate) {
		SQLiteDatabase db = getWritableDatabase();

		int i = 0;
		do {
			ContentValues cv = createTweetsContent(tweetBeans.get(i));

			db.insert(TABLE_AT_LIST, null, cv);
		} while (++i < tweetBeans.size());

		SettingsManager.getInstance(context).setAtListStatus(AT_LIST_STATUS_READY);
	}

	public ArrayList<TweetBean> getAtList() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_AT_LIST + " ORDER BY " + TweetsColumns.TWEET_TIMESTAMP + " DESC";

		Cursor cursor = db.rawQuery(sql, null);

		return getTweetByCursor(cursor);
	}

	public void addContacts(ArrayList<UserBean> userBeans, boolean isUpdate) {
		SQLiteDatabase db = getWritableDatabase();

		int i = 0;
		do {
			ContentValues cv = createContactsContent(userBeans.get(i));

			db.insert(TABLE_CONTACTS_ME, "null", cv);
		} while (++i < userBeans.size());

		SettingsManager.getInstance(context).setContactStatus(CONTACT_STATUS_READY);

	}

	// Adding new contact
	public void addContact(UserBean userBean) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = createContactsContent(userBean);

		// Inserting Row
		db.replace(TABLE_CONTACTS_ME, null, values);

	}

	public UserBean getContact(String id) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_CONTACTS_ME + " WHERE " + ContactsColumns.KEY_OPEN_ID + "=?";
		String[] selectionArgs = { id };

		Cursor cursor = db.rawQuery(sql, selectionArgs);

		UserBean contact = new UserBean();

		if (cursor.moveToFirst()) {
			contact = new UserBean(cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_OPEN_ID)), cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_NAME)), cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_NICK)), cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_HEAD)), cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_SEX)), cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_TWEET)));
		}

		if (!cursor.isClosed())
			cursor.close();

		return contact;
	}

	public ArrayList<UserBean> getAllContacts() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_CONTACTS_ME + " ORDER BY " + ContactsColumns.KEY_NAME_FORMATTED + " COLLATE LOCALIZED COLLATE NOCASE ASC";

		Cursor cursor = db.rawQuery(sql, null);

		ArrayList<UserBean> userBeans = new ArrayList<UserBean>();

		if (cursor.moveToFirst()) {
			do {
				UserBean contact = new UserBean(cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_OPEN_ID)), cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_NAME)), cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_NICK)), cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_HEAD)), cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_SEX)), cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_TWEET)));

				userBeans.add(contact);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed())
			cursor.close();

		return userBeans;
	}

	public ArrayList<UserBean> getContactsSearch(String args) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_CONTACTS_ME;
		if (args != null && args.length() > 0) {
			ArrayList<UserBean> contactList = new ArrayList<UserBean>();
			sql += " WHERE " + ContactsColumns.KEY_NAME_FORMATTED + " LIKE '%" + args + "%'";
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.moveToFirst()) {
				do {
					UserBean contact = new UserBean(cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_OPEN_ID)), cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_NAME)), cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_NICK)), cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_HEAD)), cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_SEX)), cursor.getString(cursor.getColumnIndex(ContactsColumns.KEY_TWEET)));

					contactList.add(contact);
				} while (cursor.moveToNext());
			}

			if (!cursor.isClosed())
				cursor.close();

			return contactList;
		} else {
			return getAllContacts();
		}
	}

	private ContentValues createTweetsContent(TweetBean tweetBean) {
		ContentValues cv = new ContentValues();
		cv.put(TweetsColumns.TWEET_USER_ID, tweetBean.getOpenId());
		cv.put(TweetsColumns.TWEET_ID, tweetBean.getId());
		cv.put(TweetsColumns.TWEET_HEAD, tweetBean.getHead());
		cv.put(TweetsColumns.TWEET_NAME, tweetBean.getName());
		cv.put(TweetsColumns.TWEET_NICK, tweetBean.getNick());
		cv.put(TweetsColumns.TWEET_TIMESTAMP, tweetBean.getTimestamp());
		cv.put(TweetsColumns.TWEET_CONTENT, tweetBean.getText());
		cv.put(TweetsColumns.TWEET_FROM, tweetBean.getFrom());
		if (null != tweetBean.getTweetImage())
			cv.put(TweetsColumns.TWEET_IMAGE, tweetBean.getTweetImage().toString());
		if (null != tweetBean.getVideoImage()) {
			cv.put(TweetsColumns.TWEET_VIDEO_IMAGE, tweetBean.getVideoImage());
			cv.put(TweetsColumns.TWEET_VIDEO_PLAYER, tweetBean.getVideoPlayer());
			cv.put(TweetsColumns.TWEET_VIDEO_URL, tweetBean.getVideoUrl());
		}
		if (null != tweetBean.getMusicUrl()) {
			cv.put(TweetsColumns.TWEET_MUSIC_AUTHOR, tweetBean.getMusicAuthor());
			cv.put(TweetsColumns.TWEET_MUSIC_ID, tweetBean.getMusicId());
			cv.put(TweetsColumns.TWEET_MUSIC_TITLE, tweetBean.getMusicTitle());
			cv.put(TweetsColumns.TWEET_MUSIC_URL, tweetBean.getMusicUrl());
		}
		cv.put(TweetsColumns.COMMENT_COUNT, tweetBean.getMCount());
		cv.put(TweetsColumns.R_COUNT, tweetBean.getCount());
		if (null != tweetBean.getSource()) {
			cv.put(TweetsColumns.SOURCE_NICK, tweetBean.getSource().getNick());
			cv.put(TweetsColumns.SOURCE_CONTENT, tweetBean.getSource().getText());
			if (null != tweetBean.getSource().getTweetImage())
				cv.put(TweetsColumns.SOURCE_IMAGE, tweetBean.getSource().getTweetImage().toString());
			if (null != tweetBean.getSource().getVideoImage()) {
				cv.put(TweetsColumns.SOURCE_VIDEO_IMAGE, tweetBean.getSource().getVideoImage());
				cv.put(TweetsColumns.SOURCE_VIDEO_PLAYER, tweetBean.getSource().getVideoPlayer());
				cv.put(TweetsColumns.SOURCE_VIDEO_URL, tweetBean.getSource().getVideoUrl());
			}
			if (null != tweetBean.getSource().getMusicUrl()) {
				cv.put(TweetsColumns.SOURCE_MUSIC_AUTHOR, tweetBean.getSource().getMusicAuthor());
				cv.put(TweetsColumns.SOURCE_MUSIC_ID, tweetBean.getSource().getMusicId());
				cv.put(TweetsColumns.SOURCE_MUSIC_TITLE, tweetBean.getSource().getMusicTitle());
				cv.put(TweetsColumns.SOURCE_MUSIC_URL, tweetBean.getSource().getMusicUrl());
			}
			if (null != tweetBean.getSource().getLongitude()) {
				cv.put(TweetsColumns.SOURCE_LONGITUDE, tweetBean.getSource().getLongitude());
				cv.put(TweetsColumns.SOURCE_LATITUDE, tweetBean.getSource().getLatitude());
				cv.put(TweetsColumns.SOURCE_GEO, tweetBean.getSource().getGeo());
			}
		}
		if(tweetBean.getMentionedUser() != null)
		cv.put(TweetsColumns.TWEET_MUSER, tweetBean.getMentionedUser().toString());
		cv.put(TweetsColumns.TWEET_IS_VIP, tweetBean.getIsVip());
		if (null != tweetBean.getLongitude()) {
			cv.put(TweetsColumns.TWEET_LONGITUDE, tweetBean.getLongitude());
			cv.put(TweetsColumns.TWEET_LATITUDE, tweetBean.getLatitude());
			cv.put(TweetsColumns.TWEET_GEO, tweetBean.getGeo());
		}

		return cv;
	}

	private ContentValues createProfileContent(ProfileBean profileBean) {
		ContentValues cv = new ContentValues();
		cv.put(ProfileColumns.PROFILE_OPEN_ID, profileBean.getOpenId());
		cv.put(ProfileColumns.PROFILE_HEAD, profileBean.getHead());
		cv.put(ProfileColumns.PROFILE_NAME, profileBean.getName());
		cv.put(ProfileColumns.PROFILE_NICK, profileBean.getNick());
		cv.put(ProfileColumns.PROFILE_SEX, profileBean.getSex());
		cv.put(ProfileColumns.PROFILE_TWEETNUM, profileBean.getTweetNum());
		cv.put(ProfileColumns.PROFILE_FANSNUM, profileBean.getFansNum());
		cv.put(ProfileColumns.PROFILE_IDOLNUM, profileBean.getIdolNum());
		cv.put(ProfileColumns.PROFILE_FAVNUM, profileBean.getFavNum());
		cv.put(ProfileColumns.PROFILE_INTRO, profileBean.getIntroduction());
		cv.put(ProfileColumns.PROFILE_LOCATION, profileBean.getLocation());
		cv.put(ProfileColumns.PROFILE_TAG, profileBean.composeTag());

		return cv;
	}

	private ContentValues createContactsContent(UserBean userBean) {
		ContentValues cv = new ContentValues();
		cv.put(ContactsColumns.KEY_HEAD, userBean.getHead());
		cv.put(ContactsColumns.KEY_NAME, userBean.getName());
		cv.put(ContactsColumns.KEY_NAME_FORMATTED, StringMatcher.cn2Spell(userBean.getNick()));
		cv.put(ContactsColumns.KEY_NICK, userBean.getNick());
		cv.put(ContactsColumns.KEY_OPEN_ID, userBean.getOpenId());
		cv.put(ContactsColumns.KEY_SEX, userBean.getSex());
		cv.put(ContactsColumns.KEY_TWEET, userBean.getTweetBean().getText());

		return cv;
	}

	private ContentValues createPrivateContent(PrivateDataBean privateBean) {
		ContentValues cv = new ContentValues();
		cv.put(PrivateColumns.PRIVATE_ID, privateBean.getOpenId());
		cv.put(PrivateColumns.PRIVATE_HEAD, privateBean.getHead());
		cv.put(PrivateColumns.PRIVATE_NAME, privateBean.getName());
		cv.put(PrivateColumns.PRIVATE_NICK, privateBean.getNick());
		cv.put(PrivateColumns.PRIVATE_CONTENT, privateBean.getText());
		if (null != privateBean.getImage()) cv.put(PrivateColumns.PRIVATE_IMAGE, privateBean.getImage().toString());
		cv.put(PrivateColumns.PRIVATE_TIMESTAMP, privateBean.getPubTime());

		return cv;
	}

	public int getDBCount(String tableName) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT COUNT(*) FROM " + tableName;
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();

		int returnValue = cursor.getInt(0);

		if (!cursor.isClosed())
			cursor.close();

		return returnValue;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/*
		 * Log.w(Constants.LOG_TAG, "CREATE DB--" + TABLE_CONTACTS_ME + "|" +
		 * TABLE_MY_PROFILE + "|" + TABLE_TWEET_LIST + "|" + TABLE_PRIVATE_LIST
		 * + "|" + TABLE_AT_LIST);
		 */

		String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + 
		                                TABLE_CONTACTS_ME + "(" + 
				                        ContactsColumns.KEY_OPEN_ID + " TEXT PRIMARY KEY," + 
		                                ContactsColumns.KEY_NAME + " TEXT," + 
				                        ContactsColumns.KEY_NICK + " TEXT," + 
		                                ContactsColumns.KEY_NAME_FORMATTED + " TEXT," + 
				                        ContactsColumns.KEY_HEAD + " TEXT," + 
		                                ContactsColumns.KEY_SEX + " TEXT," + 
				                        ContactsColumns.KEY_TWEET + " TEXT)";

		String CREATE_PROFILE_TABLE = "CREATE TABLE IF NOT EXISTS " + 
		                               TABLE_MY_PROFILE + "(" + 
				                       ProfileColumns.PROFILE_OPEN_ID + " TEXT PRIMARY KEY," + 
		                               ProfileColumns.PROFILE_NICK + " TEXT," + 
				                       ProfileColumns.PROFILE_NAME + " TEXT," + 
		                               ProfileColumns.PROFILE_HEAD + " TEXT," + 
				                       ProfileColumns.PROFILE_SEX + " TEXT," + 
		                               ProfileColumns.PROFILE_INTRO + " TEXT," + 
				                       ProfileColumns.PROFILE_LOCATION + " TEXT," + 
		                               ProfileColumns.PROFILE_TAG + " TEXT," + 
				                       ProfileColumns.PROFILE_TWEETNUM + " TEXT," + 
		                               ProfileColumns.PROFILE_IDOLNUM + " TEXT," + 
				                       ProfileColumns.PROFILE_FANSNUM + " TEXT," + 
		                               ProfileColumns.PROFILE_FAVNUM + " TEXT)";

		String CREATE_TWEET_LIST_TABLE = "CREATE TABLE IF NOT EXISTS " + 
		                                  TABLE_TWEET_LIST + "(" + 
				                          TweetsColumns.TWEET_ID + " TEXT PRIMARY KEY," + 
		                                  TweetsColumns.TWEET_USER_ID + " TEXT," + 
				                          TweetsColumns.TWEET_HEAD + " TEXT," + 
		                                  TweetsColumns.TWEET_NICK + " TEXT," + 
				                          TweetsColumns.TWEET_NAME + " TEXT," + 
		                                  TweetsColumns.TWEET_TIMESTAMP + " INTEGER," + 
				                          TweetsColumns.TWEET_FROM + " TEXT," + 
		                                  TweetsColumns.TWEET_CONTENT + " TEXT," + 
				                          TweetsColumns.TWEET_IMAGE + " TEXT," + 
		                                  TweetsColumns.TWEET_VIDEO_IMAGE + " TEXT," + 
				                          TweetsColumns.TWEET_VIDEO_PLAYER + " TEXT," + 
		                                  TweetsColumns.TWEET_VIDEO_URL + " TEXT," + 
				                          TweetsColumns.TWEET_MUSIC_AUTHOR + " TEXT," + 
		                                  TweetsColumns.TWEET_MUSIC_ID + " TEXT," + 
				                          TweetsColumns.TWEET_MUSIC_TITLE + " TEXT," + 
		                                  TweetsColumns.TWEET_MUSIC_URL + " TEXT," + 
				                          TweetsColumns.COMMENT_COUNT + " TEXT," + 
		                                  TweetsColumns.R_COUNT + " TEXT," + 
				                          TweetsColumns.SOURCE_NICK + " TEXT," + 
		                                  TweetsColumns.SOURCE_CONTENT + " TEXT," + 
				                          TweetsColumns.SOURCE_IMAGE + " TEXT," + 
		                                  TweetsColumns.SOURCE_VIDEO_IMAGE + " TEXT," + 
				                          TweetsColumns.SOURCE_VIDEO_PLAYER + " TEXT," + 
		                                  TweetsColumns.SOURCE_VIDEO_URL + " TEXT," + 
				                          TweetsColumns.SOURCE_MUSIC_AUTHOR + " TEXT," + 
		                                  TweetsColumns.SOURCE_MUSIC_ID + " TEXT," + 
				                          TweetsColumns.SOURCE_MUSIC_TITLE + " TEXT," + 
		                                  TweetsColumns.SOURCE_MUSIC_URL + " TEXT," + 
				                          TweetsColumns.SOURCE_LONGITUDE + " TEXT," + 
		                                  TweetsColumns.SOURCE_LATITUDE + " TEXT," + 
				                          TweetsColumns.SOURCE_GEO + " TEXT," + 
		                                  TweetsColumns.TWEET_MUSER + " TEXT," + 
				                          TweetsColumns.TWEET_IS_VIP + " TEXT," + 
		                                  TweetsColumns.TWEET_LONGITUDE + " TEXT," + 
				                          TweetsColumns.TWEET_LATITUDE + " TEXT," + 
		                                  TweetsColumns.TWEET_GEO + " TEXT)";

		String CREATE_PRIVATE_LIST_TABLE = "CREATE TABLE IF NOT EXISTS " + 
		                                    TABLE_PRIVATE_LIST + "(" + 
				                            PrivateColumns.PRIVATE_ID + " TEXT_PRIMARY KEY," + 
		                                    PrivateColumns.PRIVATE_HEAD + " TEXT," + 
				                            PrivateColumns.PRIVATE_NAME + " TEXT," + 
		                                    PrivateColumns.PRIVATE_NICK + " TEXT," + 
				                            PrivateColumns.PRIVATE_CONTENT + " TEXT," + 
		                                    PrivateColumns.PRIVATE_IMAGE + " TEXT," + 
				                            PrivateColumns.PRIVATE_TIMESTAMP + " TEXT)";

		String CREATE_AT_LIST_TABLE = "CREATE TABLE IF NOT EXISTS " + 
		                               TABLE_AT_LIST + "(" + 
				                       TweetsColumns.TWEET_ID + " TEXT PRIMARY KEY," + 
		                               TweetsColumns.TWEET_USER_ID + " TEXT," + 
				                       TweetsColumns.TWEET_HEAD + " TEXT," + 
		                               TweetsColumns.TWEET_NICK + " TEXT," + 
				                       TweetsColumns.TWEET_NAME + " TEXT," + 
		                               TweetsColumns.TWEET_TIMESTAMP + " INTEGER," + 
				                       TweetsColumns.TWEET_FROM + " TEXT," + 
		                               TweetsColumns.TWEET_CONTENT + " TEXT," + 
				                       TweetsColumns.TWEET_IMAGE + " TEXT," + 
		                               TweetsColumns.TWEET_VIDEO_IMAGE + " TEXT," + 
				                       TweetsColumns.TWEET_VIDEO_PLAYER + " TEXT," + 
		                               TweetsColumns.TWEET_VIDEO_URL + " TEXT," + 
				                       TweetsColumns.TWEET_MUSIC_AUTHOR + " TEXT," + 
		                               TweetsColumns.TWEET_MUSIC_ID + " TEXT," + 
				                       TweetsColumns.TWEET_MUSIC_TITLE + " TEXT," + 
		                               TweetsColumns.TWEET_MUSIC_URL + " TEXT," + 
				                       TweetsColumns.COMMENT_COUNT + " TEXT," + 
		                               TweetsColumns.R_COUNT + " TEXT," + 
				                       TweetsColumns.SOURCE_NICK + " TEXT," + 
		                               TweetsColumns.SOURCE_CONTENT + " TEXT," + 
				                       TweetsColumns.SOURCE_IMAGE + " TEXT," + 
		                               TweetsColumns.SOURCE_VIDEO_IMAGE + " TEXT," + 
				                       TweetsColumns.SOURCE_VIDEO_PLAYER + " TEXT," + 
		                               TweetsColumns.SOURCE_VIDEO_URL + " TEXT," + 
				                       TweetsColumns.SOURCE_MUSIC_AUTHOR + " TEXT," + 
		                               TweetsColumns.SOURCE_MUSIC_ID + " TEXT," + 
				                       TweetsColumns.SOURCE_MUSIC_TITLE + " TEXT," + 
		                               TweetsColumns.SOURCE_MUSIC_URL + " TEXT," + 
				                       TweetsColumns.SOURCE_LONGITUDE + " TEXT," + 
		                               TweetsColumns.SOURCE_LATITUDE + " TEXT," + 
				                       TweetsColumns.SOURCE_GEO + " TEXT," + 
		                               TweetsColumns.TWEET_MUSER + " TEXT," + 
				                       TweetsColumns.TWEET_IS_VIP + " TEXT," + 
		                               TweetsColumns.TWEET_LONGITUDE + " TEXT," + 
				                       TweetsColumns.TWEET_LATITUDE + " TEXT," + 
		                               TweetsColumns.TWEET_GEO + " TEXT)";

		db.execSQL(CREATE_CONTACTS_TABLE);
		SettingsManager.getInstance(context).setContactStatus(CONTACT_STATUS_INIT);
		db.execSQL(CREATE_PROFILE_TABLE);
		SettingsManager.getInstance(context).setProfileStatus(PROFILE_STATUS_INIT);
		db.execSQL(CREATE_TWEET_LIST_TABLE);
		SettingsManager.getInstance(context).setTweetListStatus(TWEET_LIST_STATUS_INIT);
		db.execSQL(CREATE_PRIVATE_LIST_TABLE);
		SettingsManager.getInstance(context).setPrivateMessageStatus(PRIVATE_MESSAGE_STATUS_INIT);
		db.execSQL(CREATE_AT_LIST_TABLE);
		SettingsManager.getInstance(context).setAtListStatus(AT_LIST_STATUS_INIT);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		// Log.w(Constants.LOG_TAG, "Upgrade DB Version " + DATABASE_VERSION);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS_ME);
		SettingsManager.getInstance(context).setContactStatus(CONTACT_STATUS_INIT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MY_PROFILE);
		SettingsManager.getInstance(context).setProfileStatus(PROFILE_STATUS_INIT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TWEET_LIST);
		SettingsManager.getInstance(context).setTweetListStatus(TWEET_LIST_STATUS_INIT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRIVATE_LIST);
		SettingsManager.getInstance(context).setPrivateMessageStatus(PRIVATE_MESSAGE_STATUS_INIT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_AT_LIST);
		SettingsManager.getInstance(context).setAtListStatus(AT_LIST_STATUS_INIT);

		onCreate(db);
	}

}
