package com.halloon.android.bean;

public class FamousBean {
	//
	// account : 用户账号,
	// nick : 昵称,
	// brief : 用户介绍,
	// head : 头像url,
	// promocycle : 推广结束时间,
	// promovalue : 每日听众增量最大值,
	// maxfollowers : 期望听众总量（为0表示无限制）
	//

	/**
	 * 用户账号,
	 */
	private String account;

	/**
	 * 用户账号,
	 */
	public String getAccount() {
		return account;
	}

	/**
	 *  昵称
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * 用户介绍,
	 */
	public String getBrief() {
		return brief;
	}

	/**
	 * 头像url,
	 */
	public String getHead() {
		return head;
	}

	/**
	 * 推广结束时间,
	 */
	public String getPromocycle() {
		return promocycle;
	}

	/**
	 *  每日听众增量最大值,
	 */
	public String getPromovalue() {
		return promovalue;
	}

	/**
	 *  期望听众总量（为0表示无限制）
	 */
	public String getMaxfollowers() {
		return maxfollowers;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public void setPromocycle(String promocycle) {
		this.promocycle = promocycle;
	}

	public void setPromovalue(String promovalue) {
		this.promovalue = promovalue;
	}

	public void setMaxfollowers(String maxfollowers) {
		this.maxfollowers = maxfollowers;
	}

	private String nick;
	private String brief;
	private String head;
	private String promocycle;
	private String promovalue;
	private String maxfollowers;
}
