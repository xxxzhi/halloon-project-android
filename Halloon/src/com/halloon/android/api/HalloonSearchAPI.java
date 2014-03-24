package com.halloon.android.api;

import org.apache.http.message.BasicNameValuePair;

import com.tencent.weibo.api.BasicAPI;
import com.tencent.weibo.api.SearchAPI;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.QArrayList;
import com.tencent.weibo.utils.QHttpClient;

public class HalloonSearchAPI extends SearchAPI {

	private String searchUser = apiBaseUrl + "/search/user";

	public HalloonSearchAPI(String OAuthVersion) {
		super(OAuthVersion);
	}

	public HalloonSearchAPI(String OAuthVersion, QHttpClient qHttpClient) {
		super(OAuthVersion, qHttpClient);
	}


	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		super.setAPIBaseUrl(apiBaseUrl);

		String appendix = "/search/";

		searchUser = apiBaseUrl + appendix + "user";
	}

}
