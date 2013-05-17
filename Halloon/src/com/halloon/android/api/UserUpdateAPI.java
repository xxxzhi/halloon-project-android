package com.halloon.android.api;

import org.apache.http.message.BasicNameValuePair;

import com.tencent.weibo.api.BasicAPI;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.QArrayList;
import com.tencent.weibo.utils.QHttpClient;

public class UserUpdateAPI extends BasicAPI {

	private String update = apiBaseUrl + "/user/update";
	private String updateEdu = apiBaseUrl + "/user/update_edu";
	private String updateHead = apiBaseUrl + "/user/update_head";
	private String verify = apiBaseUrl + "/user/verify";
	
	public UserUpdateAPI(String OAuthVersion){
		super(OAuthVersion);
	}
	
	public UserUpdateAPI(String OAuthVersion, QHttpClient qHttpClient){
		super(OAuthVersion, qHttpClient);
	}
	
	/**
	 * 本接口用于更新用户基本信息，包括出生日期， 地区码及个人介绍等
	 * 
	 * @param oAuth
	 * @param format
	 * @param nick
	 * @param sex
	 * @param year
	 * @param month
	 * @param day
	 * @param countryCode
	 * @param provinceCode
	 * @param cityCode
	 * @param introduction
	 * @return
	 */
	public String updateProfile(OAuth oAuth, String format, String nick, String sex, String year, String month, String day, String countryCode, String provinceCode, String cityCode, String introduction)throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("nick", nick));
		paramsList.add(new BasicNameValuePair("sex", sex));
		paramsList.add(new BasicNameValuePair("year", year));
		paramsList.add(new BasicNameValuePair("month", month));
		paramsList.add(new BasicNameValuePair("day", day));
		paramsList.add(new BasicNameValuePair("countrycode", countryCode));
		paramsList.add(new BasicNameValuePair("provincecode", provinceCode));
		paramsList.add(new BasicNameValuePair("citycode", cityCode));
		paramsList.add(new BasicNameValuePair("introduction", introduction));
		
		return requestAPI.postContent(update, paramsList, oAuth);
	}
	
	public String updateEducation(OAuth oAuth, String format, String fieldId, String year, String schoolId, String departmentId, String level)throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("fieldid", fieldId));
		paramsList.add(new BasicNameValuePair("year", year));
		paramsList.add(new BasicNameValuePair("schoolid", schoolId));
		paramsList.add(new BasicNameValuePair("departmentid", departmentId));
		paramsList.add(new BasicNameValuePair("level", level));
		
		return requestAPI.postContent(updateEdu, paramsList, oAuth);
	}
	
	public String updateHead(OAuth oAuth, String format, String pic)throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("pic", pic));
		
		return requestAPI.postContent(updateHead, paramsList, oAuth);
	}

	public String verify(OAuth oAuth, String format, String name, String fOpenId)throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("name", name));
		paramsList.add(new BasicNameValuePair("fopenid", fOpenId));
		
		return requestAPI.postContent(verify, paramsList, oAuth);
	}
	
	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
		
		update = apiBaseUrl + "/user/update";
		updateEdu = apiBaseUrl + "/user/update_edu";
		updateHead = apiBaseUrl + "/user/update_head";
		verify = apiBaseUrl + "/user/verify";
	}
}
