package com.halloon.android.api;

import org.apache.http.message.BasicNameValuePair;

import com.tencent.weibo.api.BasicAPI;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.QArrayList;
import com.tencent.weibo.utils.QHttpClient;

public class ShortUrlAPI extends BasicAPI {
	
	private String expand = apiBaseUrl + "/short_url/expand";
	private String shareCounts = apiBaseUrl + "/short_url/share_counts";
	private String shorten = apiBaseUrl + "/short_url/shorten";

	public ShortUrlAPI(String OAuthVersion){
		super(OAuthVersion);
	}

	public ShortUrlAPI(String OAuthVersion, QHttpClient qHttpClient) {
		super(OAuthVersion, qHttpClient);
	}
	
	public String getExpandedUrl(OAuth oAuth, String format, String shortUrl) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("short_url", shortUrl));
		
		return requestAPI.getResource(expand, paramsList, oAuth);
	}
	
	public String getShareCounts(OAuth oAuth, String format, String shortUrl, String type) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("short_url", shortUrl));
		paramsList.add(new BasicNameValuePair("type", type));
		
		return requestAPI.getResource(shareCounts, paramsList, oAuth);
	}
	
	public String getShortenedUrl(OAuth oAuth, String format, String longUrl) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("long_url", longUrl));
		
		return requestAPI.getResource(shorten, paramsList, oAuth);
	}

	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
		
		expand = apiBaseUrl + "/short_url/expand";
		shareCounts = apiBaseUrl + "/short_url/share_counts";
		shorten = apiBaseUrl + "/short_url/shorten";
	}

}
