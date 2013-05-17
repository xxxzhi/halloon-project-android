package com.halloon.android.bean;

public class UserBean {
	private String openId;
	private String name;
	private String nick;
	private String head;
	private String sex;
	private TweetBean tweetBean;

	public UserBean(String openId, String name, String nick, String head,
			String sex, String tweet) {
		super();
		this.openId = openId;
		this.name = name;
		this.nick = nick;
		this.head = head;
		this.sex = sex;
		tweetBean = new TweetBean();
		tweetBean.setText(tweet);
	}

	public UserBean() {
		tweetBean = new TweetBean();
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public TweetBean getTweetBean() {
		return tweetBean;
	}

	public void setTweetBean(TweetBean tweetBean) {
		this.tweetBean = tweetBean;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		return openId + " " + name + " " + head + " " + sex;
	}
}
