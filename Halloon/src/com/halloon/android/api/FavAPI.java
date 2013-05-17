package com.halloon.android.api;

import org.apache.http.message.BasicNameValuePair;

import com.tencent.weibo.api.BasicAPI;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.QArrayList;
import com.tencent.weibo.utils.QHttpClient;

public class FavAPI extends BasicAPI {

	private String addFav = apiBaseUrl + "/fav/addt";

	public FavAPI(String OAuthVersion) {
		super(OAuthVersion);
	}

	public FavAPI(String OAuthVersion, QHttpClient qHttpClient) {
		super(OAuthVersion, qHttpClient);
	}

	public String addFav(OAuth oAuth, String format, String id)
			throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("id", id));

		return requestAPI.postContent(addFav, paramsList, oAuth);
	}

	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;

		addFav = apiBaseUrl + "/fav/addt";
	}

}
