package com.halloon.android.bean;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class TweetBean{

	
	private String openId;
	private String id;
	private String from;
	private String timestamp;
	private String text;
	private String tweetImage;
	private String videoImage;
	private String videoPlayer;
	private String videoUrl;
	private String musicAuthor;
	private String musicId;
	private String musicTitle;
	private String musicUrl;
	private TweetBean source;
	private String head;
	private String nick;
	private String name;
	private String count;
	private String mCount;
	private int isVip;
	private String mentionedUser;
	private String longitude;
	private String latitude;
	private String geo;
	
	public TweetBean(){}

	public void add(TweetBean tweetBean) {
		this.openId = tweetBean.openId;
		this.id = tweetBean.id;
		this.from = tweetBean.from;
		this.timestamp = tweetBean.timestamp;
		this.text = tweetBean.text;
		this.tweetImage = tweetBean.tweetImage;
		this.videoImage = tweetBean.videoImage;
		this.videoPlayer = tweetBean.videoPlayer;
		this.videoUrl = tweetBean.videoUrl;
		this.musicAuthor = tweetBean.musicAuthor;
		this.musicId = tweetBean.musicId;
		this.musicTitle = tweetBean.musicTitle;
		this.musicUrl = tweetBean.musicUrl;
		this.source.add(tweetBean.source);
		this.head = tweetBean.head;
		this.nick = tweetBean.nick;
		this.name = tweetBean.name;
		this.count = tweetBean.count;
		this.mCount = tweetBean.mCount;
		this.isVip = tweetBean.isVip;
		this.mentionedUser = tweetBean.mentionedUser;
		this.longitude = tweetBean.longitude;
		this.latitude = tweetBean.latitude;
		this.geo = tweetBean.geo;
	}
	
	public Bundle toBundle(){
		Bundle bundle = new Bundle();
		bundle.putString("openId", openId);
		bundle.putString("id", id);
		bundle.putString("from", from);
		bundle.putString("timestamp", timestamp);
		bundle.putString("text", text);
		bundle.putString("tweetImage", tweetImage);
		bundle.putString("videoImage", videoImage);
		bundle.putString("videoPlayer", videoPlayer);
		bundle.putString("videoUrl", videoUrl);
		bundle.putString("musicAuthor", musicAuthor);
		bundle.putString("musicId", musicId);
		bundle.putString("musicTitle", musicTitle);
		bundle.putString("musicUrl", musicUrl);
		if(source != null && source.getNick() != null) bundle.putBundle("source", source.toBundle());
		bundle.putString("head", head);
		bundle.putString("nick", nick);
		bundle.putString("name", name);
		bundle.putString("count", count);
		bundle.putString("mCount", mCount);
		bundle.putInt("isVip", isVip);
		bundle.putString("mentionedUser", mentionedUser);
		bundle.putString("longitude", longitude);
		bundle.putString("latitude", latitude);
		bundle.putString("geo", geo);
		
		return bundle;
	}
	
	public void decodeFromBundle(Bundle bundle){
		Log.d("TAG", "" + ((Boolean) (bundle == null)).toString());
		openId = bundle.getString("openId");
		id = bundle.getString("id");
		from = bundle.getString("from");
		timestamp = bundle.getString("timestamp");
		text = bundle.getString("text");
		tweetImage = bundle.getString("tweetImage");
		videoImage = bundle.getString("videoImage");
		videoPlayer = bundle.getString("videoPlayer");
		videoUrl = bundle.getString("videoUrl");
		musicAuthor = bundle.getString("musicAuthor");
		musicId = bundle.getString("musicId");
		musicTitle = bundle.getString("musicTitle");
		musicUrl = bundle.getString("musicUrl");
		if(bundle.containsKey("source")){
			source = new TweetBean();
			source.decodeFromBundle(bundle.getBundle("source"));
		}
		head = bundle.getString("head");
		nick = bundle.getString("nick");
		name = bundle.getString("name");
		count = bundle.getString("count");
		mCount = bundle.getString("mCount");
		isVip = bundle.getInt("isVip");
		mentionedUser = bundle.getString("mentionedUser");
		longitude = bundle.getString("longitude");
		latitude = bundle.getString("latitude");
		geo = bundle.getString("geo");
	}
	
	public void setOpenId(String openId){
		this.openId = openId;
	}
	
	public String getOpenId(){
		return openId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return from;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setTweetImage(String tweetImage) {
		this.tweetImage = tweetImage;
	}

	public JSONArray getTweetImage() {
		
		if(null != tweetImage && !tweetImage.equals("null")){
			JSONArray jsonArray = null;
			try{
				jsonArray = new JSONArray(tweetImage);
			}catch(JSONException e){
				e.printStackTrace();
			}
			
			return jsonArray;
		}else{
			return null;
		}
		
	}

	public void setVideoImage(String videoImage) {
		this.videoImage = videoImage;
	}

	public String getVideoImage() {
		return videoImage;
	}

	public void setVideoPlayer(String videoPlayer) {
		this.videoPlayer = videoPlayer;
	}

	public String getVideoPlayer() {
		return videoPlayer;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}
	
	public void setMusicAuthor(String author){
		musicAuthor = author;
	}
	
	public String getMusicAuthor(){
		return musicAuthor;
	}
	
	public void setMusicId(String id){
		musicId = id;
	}
	
	public String getMusicId(){
		return musicId;
	}
	
	public void setMusicTitle(String title){
		musicTitle = title;
	}
	
	public String getMusicTitle(){
		return musicTitle;
	}
	
	public void setMusicUrl(String url){
		musicUrl = url;
	}
	
	public String getMusicUrl(){
		return musicUrl;
	}

	public void setSource(TweetBean source) {
		this.source = source;
	}

	public TweetBean getSource() {
		return source;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getHead() {
		return head;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getNick() {
		return nick;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getCount() {
		return count;
	}

	public void setMCount(String mCount) {
		this.mCount = mCount;
	}

	public String getMCount() {
		return mCount;
	}

	public void setIsVip(int isVip) {
		this.isVip = isVip;
	}

	public int getIsVip() {
		return isVip;
	}

	public void setMentionedUser(String mentionedUser) {
		this.mentionedUser = mentionedUser;
	}

	public JSONObject getMentionedUser() {
		
		if(null != mentionedUser){
			JSONObject jsonObject = null;
			try{
				jsonObject = new JSONObject(mentionedUser);
			}catch(JSONException e){
				e.printStackTrace();
			}
			return jsonObject;
		}else{
			return null;
		}
		
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setGeo(String geo) {
		this.geo = geo;
	}

	public String getGeo() {
		return geo;
	}
	
}
