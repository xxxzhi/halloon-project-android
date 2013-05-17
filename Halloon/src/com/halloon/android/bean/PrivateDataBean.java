package com.halloon.android.bean;

import org.json.JSONArray;

public class PrivateDataBean {
	private String totalCount;
	private String unReadCount;
	private String roomId;
	private String name;
	private String openId;
	private String nick;
	private String head;
	private String myHead;
	private int isVip;
	private String text;
	private String pubTime;
	private String tweeted;
	private int readFlag;
	private int msgBox;
	private JSONArray image;

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setUnReadCount(String unReadCount) {
		this.unReadCount = unReadCount;
	}

	public String getUnReadCount() {
		return unReadCount;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getNick() {
		return nick;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getHead() {
		return head;
	}
	
	public void setMyHead(String myHead){
		this.myHead = myHead;
	}
	
	public String getMyHead(){
		return myHead;
	}

	public void setIsVip(int isVip) {
		this.isVip = isVip;
	}

	public int getIsVip() {
		return isVip;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setPubTime(String pubTime) {
		this.pubTime = pubTime;
	}

	public String getPubTime() {
		return pubTime;
	}

	public void setTweeted(String tweeted) {
		this.tweeted = tweeted;
	}

	public String getTweeted() {
		return tweeted;
	}

	public void setReadFlag(int readFlag) {
		this.readFlag = readFlag;
	}

	public int getReadFlag() {
		return readFlag;
	}

	public void setMsgBox(int msgBox) {
		this.msgBox = msgBox;
	}

	public int getMsgBox() {
		return msgBox;
	}

	public void setImage(JSONArray image) {
		this.image = image;
	}

	public JSONArray getImage() {
		return image;
	}
}
