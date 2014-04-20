package com.halloon.android.bean;

public class TopicBean {
	private String name = "";
	private String keywords ="";
	
	private String id = "";
	
	private String favnum = "";
	
	private String tweetnum = "";

	public String getName() {
		return name;
	}

	public String getKeywords() {
		return keywords;
	}

	public String getId() {
		return id;
	}

	/**
	 * 话题被收藏次数,
	 * @return
	 */
	public String getFavnum() {
		return favnum;
	}

	/**
	 * 话题下微博数
	 * @return
	 */
	public String getTweetnum() {
		return tweetnum;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFavnum(String favnum) {
		this.favnum = favnum;
	}

	public void setTweetnum(String tweetnum) {
		this.tweetnum = tweetnum;
	}
}
