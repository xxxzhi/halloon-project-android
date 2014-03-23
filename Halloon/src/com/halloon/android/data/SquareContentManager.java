package com.halloon.android.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.halloon.android.api.FavAPI;
import com.halloon.android.api.HalloonStatusesAPI;
import com.halloon.android.api.HalloonTAPI;
import com.halloon.android.api.LBSAPI;
import com.halloon.android.api.OtherAPI;
import com.halloon.android.api.PrivateMessageAPI;
import com.halloon.android.api.ShortUrlAPI;
import com.halloon.android.api.UserUpdateAPI;
import com.halloon.android.bean.PrivateDataBean;
import com.halloon.android.bean.ProfileBean;
import com.halloon.android.bean.TweetBean;
import com.halloon.android.bean.UserBean;
import com.halloon.android.util.Constants;
import com.tencent.weibo.api.FriendsAPI;
import com.tencent.weibo.api.StatusesAPI;
import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.utils.QArrayList;

public class SquareContentManager{
	private static SquareContentManager instance;
	private Context context;
	// private OAuthV1 preoauth;
	private OAuthV2 preoauth;
	private String[] sexType = { " ", "男", "女" };

	private static int REQNUM = 30;

	private SquareContentManager(Context context) {
		this.context = context;

		preoauth = (OAuthV2) ((Activity) context).getIntent()
				.getSerializableExtra("oauth");

	}

	public static SquareContentManager getInstance(Context context) {
		if (instance == null) {
			instance = new SquareContentManager(context);
		}
		return instance;
	}

	/**
	 * 
	 * @param myInfo
	 * @return
	 * @throws JSONException
	 */
	private ProfileBean paraseMyProfileBean(String myInfo) {

		ProfileBean profileBean = new ProfileBean();
		try {
			JSONObject jsonObject = new JSONObject(myInfo);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");

			profileBean.setNick(dataJsonObject.getString("nick"));
			profileBean.setName(dataJsonObject.getString("name"));
			profileBean.setHead(dataJsonObject.getString("head"));
			profileBean.setSex(sexType[dataJsonObject.getInt("sex")]);
			profileBean.setOpenId(dataJsonObject.getString("openid"));
			profileBean.setIntroduction(dataJsonObject
					.getString("introduction"));
			profileBean.setLocation(dataJsonObject.getString("location"));
			profileBean.setTweetNum(dataJsonObject.getString("tweetnum"));
			profileBean.setFansNum(dataJsonObject.getString("fansnum"));
			profileBean.setFavNum(dataJsonObject.getString("favnum"));
			profileBean.setIdolNum(dataJsonObject.getString("idolnum"));

			JSONArray tagArray = dataJsonObject.optJSONArray("tag");
			if (tagArray != null) {
				String[] temp_tag = new String[tagArray.length()];
				for (int i = 0; i < tagArray.length(); i++) {
					JSONObject tagObject = tagArray.getJSONObject(i);
					temp_tag[i] = tagObject.getString("name");
				}
				profileBean.setTag(temp_tag);
			} else {
				profileBean.setTag(new String[0]);
			}
			TweetBean tweetBean = new TweetBean();
			JSONArray tweetArray = dataJsonObject.getJSONArray("tweetinfo");
			if (tweetArray != null && tweetArray.length() > 0) {
				JSONObject tweetJsonObject = tweetArray.getJSONObject(0);

//				tweetBean.setText(tweetJsonObject.getString("text"));
//				tweetBean.setId(tweetJsonObject.getString("id"));
//				tweetBean.setFrom(tweetJsonObject.getString("from"));
//				tweetBean.setTimestamp(tweetJsonObject.getString("timestamp"));
				tweetBean = getTweetFromJSON(tweetJsonObject);
				profileBean.setTweetBean(tweetBean);
			}
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_PROFILE_ERROR:" + e);
		}
		return profileBean;
	}

	
	
	
	
	
	/**
	 * 获取广播时间线
	 * 
	 * @param pageFlag
	 *            分页标识(0x0:第一页,0x1向下翻页,0x2向上翻页)
	 * @param pageTime
	 *            本页其实时间(第一页填:0x0,向上翻页:填上一次请求返回的第一条记录时间,向下翻页:填上一次请求返回的最后一条记录时间)
	 * @param requestNum
	 *            每次请求的记录条数(1-70);
	 * @param pos
	 *            记录的起始位置（第一次请求时填0，继续请求时填上次请求返回的pos）
	 * @return
	 */
	public ArrayList<TweetBean> getPublicTimeLine(String pageFlag,
			String pageTime, int requestNum, int pos) {
		HalloonStatusesAPI statusesAPI = new HalloonStatusesAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> tweetContainer = new ArrayList<TweetBean>(
				requestNum);
		try {
			String tweetInfo;
			tweetInfo = statusesAPI.publicTimeline(preoauth, "json", pos + "",
					requestNum + "");

			JSONObject jsonObject = new JSONObject(tweetInfo);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			JSONArray tweetInfoArray = dataJsonObject.getJSONArray("info");
			String userInfoObject = dataJsonObject.optString("user");
			for (int i = 0; i < tweetInfoArray.length(); i++) {
				JSONObject tweetInfoObject = tweetInfoArray.getJSONObject(i);
				TweetBean tb = getTweetFromJSON(tweetInfoObject);
				tb.setMentionedUser(userInfoObject);
				tweetContainer.add(tb);
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_USER_TIMELINE_ERROR:" + e);
		}

		return tweetContainer;
	}

	/**
	 * 本接口用于获取特别收听的人发表的微博消息
	 * 
	 * @param pageFlag
	 *            分页标识 (0x0:第一页,0x1向下翻页,0x2向上翻页)
	 * @param pageTime
	 *            本页起始时间(第一页填:0x0,向上翻页:填上一次请求返回的第一条记录时间,向下翻页:填上一次请求返回的最后一条记录时间)
	 * @param requestNum
	 *            每次请求的记录条数(1-70)
	 * @param lastid
	 *            和pagetime配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录id，向下翻页：
	 *            填上一次请求返回的最后一条记录id）
	 * @param type
	 *            拉取类型 0x1 原创发表 0x2 转载
	 *            如需拉取多个类型请使用|，如(0x1|0x2)得到3，则type=3即可，填零表示拉取所有类型
	 * @param contentType
	 *            内容过滤。0-表示所有类型，1-带文本，2-带链接，4-带图片，8-带视频，0x10-带音频
	 *            建议不使用contentType为1的类型，如果要拉取只有文本的微博，建议使用0x80
	 * @return
	 */
	public ArrayList<TweetBean> getSpecialTimeLine(String pageFlag,
			String pageTime, int requestNum, String lastid, String type,
			String contentType) {
		HalloonStatusesAPI statusesAPI = new HalloonStatusesAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> tweetContainer = new ArrayList<TweetBean>(
				requestNum);
		try {
			String tweetInfo;
			tweetInfo = statusesAPI.specialTimeline(preoauth, "json", pageFlag,
					pageTime, requestNum + "", lastid, type, contentType);

			JSONObject jsonObject = new JSONObject(tweetInfo);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			JSONArray tweetInfoArray = dataJsonObject.getJSONArray("info");
			String userInfoObject = dataJsonObject.optString("user");
			for (int i = 0; i < tweetInfoArray.length(); i++) {
				JSONObject tweetInfoObject = tweetInfoArray.getJSONObject(i);
				TweetBean tb = getTweetFromJSON(tweetInfoObject);
				tb.setMentionedUser(userInfoObject);
				tweetContainer.add(tb);
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_HOME_TIME_LINE_TWEET_ERROR:" + e);
		}

		return tweetContainer;

	}

	private TweetBean getTweetFromJSON(JSONObject tweetInfoObject) {
		TweetBean temp_tweet = new TweetBean();

		try {
			if(tweetInfoObject.has("openid"))
			temp_tweet.setOpenId(tweetInfoObject.getString("openid"));
			if(tweetInfoObject.has("id"))
			temp_tweet.setId(tweetInfoObject.getString("id"));
			if(tweetInfoObject.has("head"))
			temp_tweet.setHead(tweetInfoObject.getString("head"));
			if(tweetInfoObject.has("nick"))
			temp_tweet.setNick(tweetInfoObject.getString("nick"));
			if(tweetInfoObject.has("name"))
			temp_tweet.setName(tweetInfoObject.getString("name"));
			if(tweetInfoObject.has("origtext"))
			temp_tweet.setText(tweetInfoObject.getString("origtext"));
			if(tweetInfoObject.has("image"))
			temp_tweet.setTweetImage(tweetInfoObject.optString("image"));
			temp_tweet.setLongitude(tweetInfoObject.optString("longitude"));
			temp_tweet.setLatitude(tweetInfoObject.optString("latitude"));
			temp_tweet.setGeo(tweetInfoObject.getString("geo"));

			JSONObject videoObject = tweetInfoObject.optJSONObject("video");
			if (videoObject != null) {
				temp_tweet.setVideoImage(videoObject.getString("picurl"));
				temp_tweet.setVideoPlayer(videoObject.getString("player"));
				temp_tweet.setVideoUrl(videoObject.getString("shorturl"));
			}
			JSONObject musicObject = tweetInfoObject.optJSONObject("music");
			if (musicObject != null) {
				temp_tweet.setMusicAuthor(musicObject.getString("author"));
				temp_tweet.setMusicId(musicObject.getString("id"));
				temp_tweet.setMusicTitle(musicObject.getString("title"));
				temp_tweet.setMusicUrl(musicObject.getString("url"));
			}

			JSONObject sourceTweetObject = tweetInfoObject
					.optJSONObject("source");
			if (sourceTweetObject != null && sourceTweetObject.length() > 0) {
				TweetBean sourceTweetBean = new TweetBean();

				sourceTweetBean.setHead(sourceTweetObject.getString("head"));
				sourceTweetBean.setNick(sourceTweetObject.getString("nick"));
				sourceTweetBean.setName(sourceTweetObject.getString("name"));
				sourceTweetBean
						.setText(sourceTweetObject.getString("origtext"));
				sourceTweetBean.setTweetImage(sourceTweetObject
						.optString("image"));
				sourceTweetBean.setLongitude(sourceTweetObject
						.optString("longitude"));
				sourceTweetBean.setLatitude(sourceTweetObject
						.optString("latitude"));
				sourceTweetBean.setGeo(sourceTweetObject.getString("geo"));

				JSONObject sourceVideoObject = sourceTweetObject
						.optJSONObject("video");
				if (sourceVideoObject != null) {
					sourceTweetBean.setVideoImage(sourceVideoObject
							.getString("picurl"));
					sourceTweetBean.setVideoPlayer(sourceVideoObject
							.getString("player"));
					sourceTweetBean.setVideoUrl(sourceVideoObject
							.getString("shorturl"));
				}

				JSONObject sourceMusicObject = sourceTweetObject
						.optJSONObject("music");
				if (sourceMusicObject != null) {
					sourceTweetBean.setMusicAuthor(sourceMusicObject
							.getString("author"));
					sourceTweetBean.setMusicId(sourceMusicObject
							.getString("id"));
					sourceTweetBean.setMusicTitle(sourceMusicObject
							.getString("title"));
					sourceTweetBean.setMusicUrl(sourceMusicObject
							.getString("url"));
				}

				temp_tweet.setSource(sourceTweetBean);
			}
			
			temp_tweet.setFrom(tweetInfoObject.getString("from"));
			temp_tweet.setTimestamp(tweetInfoObject.getString("timestamp"));
			if(tweetInfoObject.has("count"))
			temp_tweet.setCount(tweetInfoObject.getString("count"));
			if(tweetInfoObject.has("mcount"))
			temp_tweet.setMCount(tweetInfoObject.getString("mcount"));
			if(tweetInfoObject.has("isvip"))
			temp_tweet.setIsVip(tweetInfoObject.getInt("isvip"));

		} catch (JSONException e) {
			Log.d(Constants.LOG_TAG, "GET_TWEET_FROM_JSON_ERROR:" + e);
		}

		return temp_tweet;

	}
}
