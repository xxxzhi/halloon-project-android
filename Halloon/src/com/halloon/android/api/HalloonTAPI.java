package com.halloon.android.api;

import org.apache.http.message.BasicNameValuePair;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.QArrayList;
import com.tencent.weibo.utils.QHttpClient;

public class HalloonTAPI extends TAPI {
	 private String tLikeUrl=apiBaseUrl+"/t/like";
	 private String tUnLikeUrl=apiBaseUrl+"/t/unlike";
	 private String tHasLikeUrl=apiBaseUrl+"/t/has_like";
	
    /**
     * 使用完毕后，请调用 shutdownConnection() 关闭自动生成的连接管理器
     * @param OAuthVersion 根据OAuthVersion，配置通用请求参数
     */
    public HalloonTAPI(String OAuthVersion) {
        super(OAuthVersion);
    }

    /**
     * @param OAuthVersion 根据OAuthVersion，配置通用请求参数
     * @param qHttpClient 使用已有的连接管理器
     */
    public HalloonTAPI(String OAuthVersion, QHttpClient qHttpClient) {
        super(OAuthVersion, qHttpClient);
    }

    
	/**
	 * 赞一条微博
	 * 
	 * @param oAuth
	 * @param format 返回数据的格式 是（json或xml）
	 * @param id  微博id
	 */
	public String like(OAuth oAuth, String format,String id)
			throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("id", id));
		
		return requestAPI.postContent(tLikeUrl, paramsList,
				oAuth);
	}
    
	
	/**
	 * 取消赞一条微博
	 * 
	 * @param oAuth
	 * @param format 返回数据的格式 是（json或xml）
	 * @param id  微博id
	 * @param favoriteId 
	 */
	public String unlike(OAuth oAuth, String format,String id,String favoriteId)
			throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("id", id));
		paramsList.add(new BasicNameValuePair("favoriteId", favoriteId));
		return requestAPI.postContent(tUnLikeUrl, paramsList,
				oAuth);
	}
	
	/**
	 * 获取自己是否赞了一条微博
	 * 
	 * @param oAuth
	 * @param format 返回数据的格式 是（json或xml）
	 * @param id  微博id
	 * @param name
	 * @param openid
	 */
	public String haslike(OAuth oAuth, String format,String id,String name,String openid)
			throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("id", id));
		paramsList.add(new BasicNameValuePair("names", name));
		paramsList.add(new BasicNameValuePair("fopenids", openid));
		return requestAPI.postContent(tHasLikeUrl, paramsList,
				oAuth);
	}
	
}
