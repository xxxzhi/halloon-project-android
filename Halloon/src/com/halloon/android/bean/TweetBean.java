package com.halloon.android.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class TweetBean implements Parcelable{

	
	private String openId;
	private String id;
	private String from;
	private String timestamp;
	private String text;
	private String tweetImage;
	private String videoImage;
	private String videoPlayer;
	private String videoUrl;
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
	
	public TweetBean(Parcel parcel){
		readFromParcel(parcel);
	}

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
	
	@Override
	public void writeToParcel(Parcel parcel, int flag){
		parcel.writeString(openId);
		parcel.writeString(id);
		parcel.writeString(from);
		parcel.writeString(timestamp);
		parcel.writeString(text);
		parcel.writeString(tweetImage.toString());
		parcel.writeString(videoImage);
		parcel.writeString(videoPlayer);
		parcel.writeString(videoUrl);
		parcel.writeParcelable(source, flag);
		parcel.writeString(head);
		parcel.writeString(nick);
		parcel.writeString(name);
		parcel.writeString(count);
		parcel.writeString(mCount);
		parcel.writeInt(isVip);
		parcel.writeString(mentionedUser.toString());
		parcel.writeString(longitude);
		parcel.writeString(latitude);
		parcel.writeString(geo);
	}
	
	private void readFromParcel(Parcel parcel){
		openId = parcel.readString();
		id = parcel.readString();
		from = parcel.readString();
		timestamp = parcel.readString();
		text = parcel.readString();
		tweetImage = parcel.readString();
		videoImage = parcel.readString();
		videoPlayer = parcel.readString();
		videoUrl = parcel.readString();
		source = parcel.readParcelable(null);
		head = parcel.readString();
		nick = parcel.readString();
		name = parcel.readString();
		count = parcel.readString();
		mCount = parcel.readString();
		isVip = parcel.readInt();
		mentionedUser = parcel.readString();
		longitude = parcel.readString();
		latitude = parcel.readString();
		geo = parcel.readString();
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
		
		if(null != tweetImage){
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

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<TweetBean> CREATOR = new Parcelable.Creator<TweetBean>(){

		@Override
		public TweetBean createFromParcel(Parcel source) {
			return new TweetBean(source);
		}

		@Override
		public TweetBean[] newArray(int size) {
			return new TweetBean[size];
		}
		
	};
}
