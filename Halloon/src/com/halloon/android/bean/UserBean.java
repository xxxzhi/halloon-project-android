package com.halloon.android.bean;

public class UserBean {
	private String openId;
	private String name;
	private String nick;
	private String head;
	private String sex;
	private TweetBean tweetBean;

	
	private String fansnum;
	private String idolnum;
	private String isfans;
	private String isvip;
	private String isidol;
	
	
//	name : 帐户名,
//	openid : 用户唯一id，与name相对应,
//	nick : 昵称,
//	head : 头像url,
//	sex : 用户性别，1-男，2-女，0-未填写,
//	location : 用户发表微博时的所在地,
//	country_code : 国家码,
//	province_code : 省份码,
//	city_code : 城市码,
//	tweet : 用户最近发的一条微博
//	{
//	text : 微博内容,
//	from : 来源,
//	id : 微博id,
//	timestamp : 微博时间戳
//},
//fansnum : 听众数,
//idolnum : 偶像数,
//isfans : 是否我的听众，0-不是，1-是,
//isvip : 是否名人用户,
//tag : 用户标签
//{
//	id : 标签id,
//	name : 标签名
//}
//}
//	isidol : 是否我的偶像，0-不是，1-是,
	/**
	 * 
	 * @return  是否我的偶像，0-不是，1-是,
	 */
	public String getIsidol() {
		return isidol;
	}

	public void setIsidol(String isidol) {
		this.isidol = isidol;
	}

	
	/**
	 * 
	 * @return
	 */
	public String getFansnum() {
		return fansnum;
	}

	public String getIdolnum() {
		return idolnum;
	}
	
	/**
	 * 
	 * @return : 是否我的听众，0-不是，1-是,
	 */
	public String getIsfans() {
		return isfans;
	}

	public String getIsvip() {
		return isvip;
	}

	public void setFansnum(String fansnum) {
		this.fansnum = fansnum;
	}

	public void setIdolnum(String idolnum) {
		this.idolnum = idolnum;
	}

	public void setIsfans(String isfans) {
		this.isfans = isfans;
	}

	public void setIsvip(String isvip) {
		this.isvip = isvip;
	}


	
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
