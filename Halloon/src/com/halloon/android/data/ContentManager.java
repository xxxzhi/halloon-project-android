package com.halloon.android.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.halloon.android.api.FavAPI;
import com.halloon.android.api.HalloonSearchAPI;
import com.halloon.android.api.HalloonStatusesAPI;
import com.halloon.android.api.HalloonTAPI;
import com.halloon.android.api.LBSAPI;
import com.halloon.android.api.OtherAPI;
import com.halloon.android.api.PrivateMessageAPI;
import com.halloon.android.api.ShortUrlAPI;
import com.halloon.android.api.TrendsAPI;
import com.halloon.android.api.UserUpdateAPI;
import com.halloon.android.bean.FamousBean;
import com.halloon.android.bean.PrivateDataBean;
import com.halloon.android.bean.ProfileBean;
import com.halloon.android.bean.TopicBean;
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

public class ContentManager {
	private static ContentManager instance;
	private Context context;
	// private OAuthV1 preoauth;
	private OAuthV2 preoauth;
	private String[] sexType = { " ", "男", "女" };
	private int pos = 0 ;
	public int getPos() {
		return pos;
	}
	
	public static void clear(){
		instance = null ;
	}
	
	private int errorCode = 0 ;
	
	public int getErrorCode() {
		return errorCode;
	}

	private static int REQNUM = 30;

	private ContentManager(Context context) {
		this.context = context;
		// preoauth = new OAuthV1();
		// preoauth.setOauthConsumerKey(Constants.CONSUMER_KEY);
		// preoauth.setOauthConsumerSecret(Constants.CONSUMER_SECRET);

		// preoauth.setOauthToken(SettingsManager.getInstance(context).getRequestToken());
		// preoauth.setOauthTokenSecret(SettingsManager.getInstance(context).getRequestTokenSecret());

		preoauth = (OAuthV2) ((Activity) context).getIntent()
				.getSerializableExtra("oauth");
		System.out.println("test exit : oauthv2 ContentManager "+preoauth.getAccessToken());
	}

	public static ContentManager getInstance(Context context) {
		if (instance == null) {
			instance = new ContentManager(context);
		}
		return instance;
	}
	
	
	/**
	 * 
	 * @param content
	 *            私信内容
	 * @param clientip
	 *            用户ip(以分析用户所在地)
	 * @param name
	 *            对方用户名（可选）
	 * @param fopenid
	 *            对方openid（可选）name和fopenid至少选一个，若同时存在则以name值为主
	 * @param contentflag
	 *            私信类型标识，1-普通私信，2-带图私信，此时需带参数pic（默认为1）
	 * @param pic
	 *            文件域表单名。本字段不要放在签名的参数中，不然请求时会出现签名错误，图片大小限制在4M。
	 * @return
	 */
	public int[] addPrivateMsg(String content,
			String clientip, String name, String fopenid, String contentflag,
			String pic){
		PrivateMessageAPI privateApi = new PrivateMessageAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		String myInfo = "";
		int ret = 0 ;
		int errcode = 0 ;
		try {
			myInfo = privateApi.add(preoauth, "json", content, clientip, name, fopenid, contentflag, pic);
			
			JSONObject jsonObject = new JSONObject(myInfo);
			errcode = jsonObject.getInt("errcode");
			ret = jsonObject.getInt("ret");
//			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			
			
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_PROFILE_ERROR:" + e);
		}

		return new int []{ret,errcode};
	}
	
	
	// 获取 通讯录
	public ArrayList<UserBean> getContacts() {
		ArrayList<UserBean> beans = new ArrayList<UserBean>();

		FriendsAPI userAPI = new FriendsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int page = 1;
		while (true) {
			try {
				String response = userAPI.idollist(preoauth, "json", REQNUM
						+ "", REQNUM * (page - 1) + "", "");
				// String response = userAPI.fanslist(preoauth, "json", REQNUM +
				// "", REQNUM * (page - 1) + "", "", "0");

				JSONObject jsonObject = new JSONObject(response);
				JSONObject dataJsonObject = jsonObject.getJSONObject("data");
				JSONArray jsonArray = dataJsonObject.getJSONArray("info");

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObject = jsonArray.getJSONObject(i);
					/* if (jObject.getBoolean("isidol")) { */
					UserBean userBean = new UserBean();
					userBean.setOpenId(jObject.getString("openid"));
					userBean.setName(jObject.getString("name"));
					userBean.setNick(jObject.getString("nick"));
					userBean.setHead(jObject.getString("head"));
					userBean.setSex(sexType[jObject.getInt("sex")]);

					JSONArray tweetArray = jObject.getJSONArray("tweet");
					for (int j = 0; j < tweetArray.length(); j++) {
						JSONObject tweetObject = tweetArray.getJSONObject(j);
						TweetBean tweetBean = new TweetBean();
						tweetBean.setId(tweetObject.getString("id"));
						tweetBean.setText(tweetObject.getString("text"));
						tweetBean.setFrom(tweetObject.getString("from"));
						tweetBean.setTimestamp(tweetObject
								.getString("timestamp"));	
						userBean.setTweetBean(tweetBean);
					}
					beans.add(userBean);
					// }

				}
			} catch (Exception e) {
				Log.d(Constants.LOG_TAG, "GET_CONTACTS_ERROR:" + e);
				return beans;
			}
			page++;
		}

	}

	// 获取他人资料
	public ProfileBean getOtherProfile(String name, String id) {
		ProfileBean profileBean = new ProfileBean();
		UserAPI userAPI = new UserAPI(OAuthConstants.OAUTH_VERSION_2_A);

		try {
			String otherInfo = userAPI.otherInfo(preoauth, "json", name, id);

			JSONObject jsonObject = new JSONObject(otherInfo);
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

			profileBean.setIsMyIdol(dataJsonObject.getInt("ismyidol"));
			profileBean.setIsMyFan(dataJsonObject.getInt("ismyfans"));
			profileBean.setSelf(dataJsonObject.getInt("self"));

			JSONArray tagArray = dataJsonObject.optJSONArray("tag");
			String[] temp_tag = new String[tagArray.length()];
			for (int i = 0; i < tagArray.length(); i++) {
				JSONObject tagObject = tagArray.getJSONObject(i);
				temp_tag[i] = tagObject.getString("name");
			}
			profileBean.setTag(temp_tag);

			TweetBean tweetBean = new TweetBean();
			JSONArray tweetArray = dataJsonObject.getJSONArray("tweetinfo");
			JSONObject tweetJsonObject = tweetArray.getJSONObject(0);

			tweetBean.setText(tweetJsonObject.getString("text"));
			tweetBean.setId(tweetJsonObject.getString("id"));
			tweetBean.setFrom(tweetJsonObject.getString("from"));
			tweetBean.setTimestamp(tweetJsonObject.getString("timestamp"));

			profileBean.setTweetBean(tweetBean);

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_OTHER_INFO_ERROR" + e);
		}

		return profileBean;
	}

	// 获取本人资料
	public ProfileBean getMyProfile() {
		ProfileBean profileBean = null;
		UserAPI userAPI = new UserAPI(OAuthConstants.OAUTH_VERSION_2_A);
		String myInfo = "";
		System.out.println("test exit : preoauth  " + preoauth.getAccessToken());
		try {
			myInfo = userAPI.info(preoauth, "json");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_PROFILE_ERROR:" + e);
		}
		profileBean = paraseMyProfileBean(myInfo);
		if(profileBean != null && profileBean.getTweetBean() != null){
			profileBean.getTweetBean().setNick(profileBean.getNick());
		}
		return profileBean;
	}
	/**
	 * @param format
	 *            是 返回数据的格式（json或xml）
	 * @param reqnum
	 *            是 每次请求记录的条数（1-200条，默认为50条，最多200条，超过200则仅返回50条）
	 * @param name
	 *            否 需要获取微相册的用户的用户名（可选，可以是当前用户或其他用户）
	 * @param fopenid
	 *            否 需要获取微相册的用户的openid（可选，可以是当前用户或其他用户）
	 *  name和fopenid至少选一个
	 *            ，若同时存在则以name值为主
	 * @param pageflag
	 *            是 分页标识（0：第一页，1：向下翻页，2：向上翻页）
	 * @param pagetime
	 *            是 本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间）
	 * @param lastid
	 *            否 获取其他用户微相册时使用，用于精确翻页，和pagetime配合使用（第一页：填0，向上翻页：
	 *            填上一次请求返回的第一条记录id，向下翻页：填上一次请求返回的最后一条记录id），当获取当前用户微相册时，不需要使用该参数
	 * @return
	 */
	public ArrayList<TweetBean> getMicroAlbum(int requestNum,String name,String fopenid,String pageFlag,
			String pageTime,  String lastId){
		HalloonStatusesAPI statusesAPI = new HalloonStatusesAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		
		ArrayList<TweetBean> tweetContainer = new ArrayList<TweetBean>(
				requestNum);
		
		
		try {
			String tweetInfo;
			tweetInfo = statusesAPI.getMicroAlbum(preoauth, "json",requestNum+"", name,
					fopenid, pageFlag,
					pageTime, lastId);
			
			JSONObject jsonObject = new JSONObject(tweetInfo);
			int errcode = jsonObject.getInt("errcode");
			String seqid = jsonObject.getString("seqid");
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			int hasnext = dataJsonObject.getInt("hasnext");
			JSONArray tweetInfoArray = dataJsonObject.getJSONArray("info");
			for (int i = 0; i < tweetInfoArray.length(); i++) {
				JSONObject tweetInfoObject = tweetInfoArray.getJSONObject(i);
				
				TweetBean tb = new TweetBean();
				tb.setId(tweetInfoObject.getString("id"));
				tb.setTimestamp(tweetInfoObject.getString("pubtime"));
				tb.setTweetImage(tweetInfoObject.getString("image"));
				tweetContainer.add(tb);
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_USER_TIMELINE_ERROR:" + e);
		}
		return tweetContainer;
	}
	
	
	public int[] updateProfile(String nick, String sex, String year,
			String month, String day, String countryCode, String provinceCode,
			String cityCode, String introduction) {
		UserUpdateAPI userUpdateApi = new UserUpdateAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInt = new int[2];
		try {
			String myInfo = userUpdateApi.updateProfile(preoauth, "json", nick,
					sex, year, month, day, countryCode, provinceCode, cityCode,
					introduction);
			JSONObject feedBack = new JSONObject(myInfo);

			ProfileBean profileBean = null;

			try {

				profileBean = paraseMyProfileBean(myInfo);

			} catch (Exception e) {
				Log.d(Constants.LOG_TAG, "GET_PROFILE_ERROR:" + e);
			}

			if (profileBean != null)
				DBManager.getInstance(context).upgradeProfile(profileBean);

			returnInt[0] = feedBack.getInt("ret");
			returnInt[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "UPDATE_PROFILE_ERROR:" + e);
		}

		return returnInt;
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

	public int[] updateEducation(String fieldId, String year, String schoolId,
			String departmentId, String level) {
		UserUpdateAPI userUpdateApi = new UserUpdateAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInt = new int[2];
		try {
			JSONObject feedBack = new JSONObject(userUpdateApi.updateEducation(
					preoauth, "json", fieldId, year, schoolId, departmentId,
					level));

			returnInt[0] = feedBack.getInt("ret");
			returnInt[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "UPDATE_EDUCATION_ERROR:" + e);
		}

		return returnInt;
	}

	public int[] updateHead(String pic) {
		UserUpdateAPI userUpdateApi = new UserUpdateAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInt = new int[2];
		try {
			JSONObject feedBack = new JSONObject(userUpdateApi.updateHead(
					preoauth, "json", pic));

			returnInt[0] = feedBack.getInt("ret");
			returnInt[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "UPDATE_HEAD_ERROR:" + e);
		}

		return returnInt;
	}

	/**
	 * 获取主页时间线
	 * 
	 * @param pageFlag
	 *            分页标识 (0x0:第一页,0x1向下翻页,0x2向上翻页)
	 * @param pageTime
	 *            本页起始时间(第一页填:0x0,向上翻页:填上一次请求返回的第一条记录时间,向下翻页:填上一次请求返回的最后一条记录时间)
	 * @param requestNum
	 *            每次请求的记录条数(1-70)
	 * @param type
	 *            拉取类型 0x1 原创发表 0x2 转载
	 *            如需拉取多个类型请使用|，如(0x1|0x2)得到3，则type=3即可，填零表示拉取所有类型
	 * @param contentType
	 *            内容过滤。0-表示所有类型，1-带文本，2-带链接，4-带图片，8-带视频，0x10-带音频
	 *            建议不使用contentType为1的类型，如果要拉取只有文本的微博，建议使用0x80
	 * @return
	 */
	public ArrayList<TweetBean> getHomeTimeLineTweet(String pageFlag,
			String pageTime, int requestNum, String type, String contentType) {
		StatusesAPI statusesAPI = new StatusesAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> tweetContainer = new ArrayList<TweetBean>(
				requestNum);
		try {
			String tweetInfo;
			tweetInfo = statusesAPI.homeTimeline(preoauth, "json", pageFlag,
					pageTime, requestNum + "", type, contentType);

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

	/**
	 * 本接口用于根据话题ID获取话题的相关信息， 譬如：话题ID， 话题名字等
	 * @param oAuth
	 * @param format
	 * @param reqnum 请求个数（最多20)
	 * @param pos 请求位置，第一次请求时填0，继续填上次返回的pos
	 * @return
	 * @throws Exception
	 * 
	 * @see <a href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E7%83%AD%E5%BA%A6%EF%BC%8C%E8%B6%8B%E5%8A%BF/%E8%AF%9D%E9%A2%98%E7%83%AD%E6%A6%9C">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public ArrayList<TopicBean> getHotTopic( int requestNum, int pos){
		TrendsAPI statusesAPI =new TrendsAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TopicBean> topicContainer = new ArrayList<TopicBean>(
				requestNum);
		try {
			String tweetInfo;
			tweetInfo = statusesAPI.getHotList(preoauth, "json",
					requestNum + "", pos+"");

			JSONObject jsonObject = new JSONObject(tweetInfo);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			this.pos = dataJsonObject.getInt("pos");
			JSONArray topicInfoArray = dataJsonObject.getJSONArray("info");
			for (int i = 0; i < topicInfoArray.length(); i++) {
				JSONObject topicObject = topicInfoArray.getJSONObject(i);
//				TopicBean tb = getTweetFromJSON(tweetInfoObject);
				TopicBean tb = new TopicBean();
				tb.setName(topicObject.getString("name"));
				tb.setKeywords(topicObject.getString("keywords"));
				tb.setId(topicObject.getString("id"));
				tb.setFavnum(topicObject.getString("favnum"));
				tb.setTweetnum(topicObject.getString("tweetnum"));
				
				topicContainer.add(tb);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.d(Constants.LOG_TAG, ""+ e);
		}

		return topicContainer;
	}
	
	
	
	/**
	 * 本接口用于根据请求名人的类别id获取当前类别的推荐名人列表信息，  譬如： 推荐名人的用户帐号， 昵称。用户介绍等
	 * @param classid 请求的名人类别id，
	 * @param subclassid 当classid=268 或 294 时使用， 表示请求的名人类别的子类别id，详细信息请参见下表“classid和subclassid类别说明”
	 * 例如：媒体机构类别下子类别广播id为：subclass_959。  若为空， 则 默认第一个子类别； 没有子类别的， 此处为空
	 * 
	 * </br><a href="http://mat1.gtimg.com/app/api_debuger/html/get_famouslist_class.htm" target="_blank">查看classid和subclassid类别说明表</a>
	 * @return
	 * @throws Exception
	 * 
	 * @see <a href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E7%83%AD%E5%BA%A6%EF%BC%8C%E8%B6%8B%E5%8A%BF/%E6%8E%A8%E8%8D%90%E5%90%8D%E4%BA%BA%E5%88%97%E8%A1%A8">微博开放平台上关于此条API的文档</a>
	 */
	public ArrayList<FamousBean> getFamousList( String classid, String subclassid) {
		TrendsAPI statusesAPI =new TrendsAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<FamousBean> tweetContainer = new ArrayList<FamousBean>();
		try {
			String info;
			info = statusesAPI.getFamousList(preoauth, "json",classid, subclassid);

			JSONObject jsonObject = new JSONObject(info);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			JSONArray tweetInfoArray = dataJsonObject.getJSONArray("info");
			String userInfoObject = dataJsonObject.optString("user");
			for (int i = 0; i < tweetInfoArray.length(); i++) {
				JSONObject tweetInfoObject = tweetInfoArray.getJSONObject(i);
				
				FamousBean userBean = new FamousBean();
				
				userBean.setAccount(tweetInfoObject.getString("account"));
				userBean.setNick(tweetInfoObject.getString("nick"));
				userBean.setBrief(tweetInfoObject.getString("brief"));
				userBean.setHead(tweetInfoObject.getString("head"));
				userBean.setPromocycle(tweetInfoObject.getString("promocycle"));
				userBean.setPromovalue(tweetInfoObject.getString("promovalue"));
				userBean.setMaxfollowers(tweetInfoObject.getString("maxfollowers"));
				
				tweetContainer.add(userBean);
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_BROADCAST_TIMELINE_ERROR:" + e);
		}

		return tweetContainer;
	}
	
	/**
	 * 本接口用于获取转播次数开前的微博消息列表， 譬如:微博内容， 微博ID等
	 * @param requestNum 每次请求记录的条数（1-100条）
	 * @param pos 翻页标识
	 * @param type 微博消息类型
	 * 0x1 戴文本 0x2带链接 0x4带图片 0x8 带视频
	 * 如需拉取多个类型请使用| 如(0x1|0x2) 得到3， 此时type = 3 即可， 填零便是拉取所有类型
	 * @return
	 */
	public ArrayList<TweetBean> getReHotTweetBean( int requestNum, int pos, String type
			) {
		TrendsAPI statusesAPI =new TrendsAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> tweetContainer = new ArrayList<TweetBean>(
				requestNum);
		try {
			String tweetInfo;
			tweetInfo = statusesAPI.getReHotList(preoauth, "json",
					requestNum + "", pos+"", type);

			JSONObject jsonObject = new JSONObject(tweetInfo);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			this.pos = dataJsonObject.getInt("pos");
			JSONArray tweetInfoArray = dataJsonObject.getJSONArray("info");
			String userInfoObject = dataJsonObject.optString("user");
			for (int i = 0; i < tweetInfoArray.length(); i++) {
				JSONObject tweetInfoObject = tweetInfoArray.getJSONObject(i);
				TweetBean tb = getTweetFromJSON(tweetInfoObject);
				tb.setMentionedUser(userInfoObject);
				tweetContainer.add(tb);
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_BROADCAST_TIMELINE_ERROR:" + e);
		}

		return tweetContainer;
	}
	
	
	/**
	 * 获取个人时间线
	 * 
	 * @param pageFlag
	 *            分页标识 (0x0:第一页,0x1向下翻页,0x2向上翻页)
	 * @param pageTime
	 *            本页起始时间(第一页填:0x0,向上翻页:填上一次请求返回的第一条记录时间,向下翻页:填上一次请求返回的最后一条记录时间)
	 * @param requestNum
	 *            每次请求的记录条数(1-200)
	 * @param lastId
	 *            和pagetime配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录id，向下翻页：
	 *            填上一次请求返回的最后一条记录id）
	 * @param type
	 *            拉取类型 0x1 原创发表 0x2 转载
	 *            如需拉取多个类型请使用|，如(0x1|0x2)得到3，则type=3即可，填零表示拉取所有类型
	 * @param contentType
	 *            内容过滤 填零表示所有类型 1-带文本 2-带链接 4图片 8-带视频 0x10-带音频
	 * @return
	 */
	public ArrayList<TweetBean> getBroadcastTimeLine(String pageFlag,
			String pageTime, int requestNum, String lastId, String type,
			String contentType) {
		StatusesAPI statusesAPI = new StatusesAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> tweetContainer = new ArrayList<TweetBean>(
				requestNum);
		try {
			String tweetInfo;
			tweetInfo = statusesAPI.broadcastTimeline(preoauth, "json",
					pageFlag, pageTime, requestNum + "", lastId, type,
					contentType);

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
			Log.d(Constants.LOG_TAG, "GET_BROADCAST_TIMELINE_ERROR:" + e);
		}

		return tweetContainer;
	}

	
	 /**
	 * 搜索微博
	 * 
	 * @param keyword 搜索关键字
	 * @param pagesize 本次请求的记录条数（1-30个）
	 * @param page 请求的页码，从0开始 页码
	 * @param contenttype 内容过滤。0-表示所有类型，1-带文本，2-带链接，4-带图片，8-带视频，0x10-带音频 
	 * @param sorttype 排序方式 0-表示按默认方式排序(即时间排序(最新)) 
	 * @param msgtype 消息的类型（按位使用） 0-所有，1-原创发表，2 转载，8-回复(针对一个消息，进行对话)，0x10-空回(点击客人页，进行对话) 
	 * @param searchtype 搜索类型 
     *               <li>0-默认搜索类型（现在为模糊搜索） 
     *               <li>1-模糊搜索：时间参数starttime和endtime间隔小于一小时，时间参数会调整为starttime前endtime后的整点，即调整间隔为1小时 
     *               <li>8-实时搜索：选择实时搜索，只返回最近几分钟的微博，时间参数需要设置为最近的几分钟范围内才生效，并且不会调整参数间隔 
     * @param starttime  开始时间，用UNIX时间表示（从1970年1月1日0时0分0秒起至现在的总秒数） endtime  结束时间，与starttime一起使用（必须大于starttime）  
     * @param endtime  结束时间，与starttime一起使用（必须大于starttime） 
     * @param province  省编码（不填表示忽略地点搜索）  
     * @param city  市编码（不填表示按省搜索）  
     * @param longitue  经度，（实数）*1000000  
     * @param latitude  纬度，（实数）*1000000  
     * @param radius  半径（整数，单位米，不大于20000）  

	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E6%90%9C%E7%B4%A2%E7%9B%B8%E5%85%B3/%E6%90%9C%E7%B4%A2%E5%BE%AE%E5%8D%9A">腾讯微博开放平台上关于此条API的文档</a>
	 */
public ArrayList<TweetBean> searchTweet(String keyword,
		int pagesize, int page,int contenttype,String sorttype,
		int msgtype,String searchtype,String starttime, 
		String endtime) {
	HalloonSearchAPI searchAPI = new HalloonSearchAPI(
			OAuthConstants.OAUTH_VERSION_2_A);
	ArrayList<TweetBean> tweetContainer = new ArrayList<TweetBean>(
			pagesize);
	try {
		String tweetInfo;
		tweetInfo = searchAPI.t(preoauth, "json", keyword, pagesize+"", 
				page+"", contenttype+"", sorttype, msgtype+"", searchtype,
				starttime, endtime, "", "", "", "", "");

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
		Log.d(Constants.LOG_TAG, "GET_BROADCAST_TIMELINE_ERROR:" + e);
	}

	return tweetContainer;
}

	
	
	 /**
		 * 搜索微博
		 * 
		 * @param keyword 搜索关键字
		 * @param pagesize 本次请求的记录条数（1-30个）
		 * @param page 请求的页码，从0开始 页码
		 * @param contenttype 内容过滤。0-表示所有类型，1-带文本，2-带链接，4-带图片，8-带视频，0x10-带音频 
		 * @param sorttype 排序方式 0-表示按默认方式排序(即时间排序(最新)) 
		 * @param msgtype 消息的类型（按位使用） 0-所有，1-原创发表，2 转载，8-回复(针对一个消息，进行对话)，0x10-空回(点击客人页，进行对话) 
		 * @param searchtype 搜索类型 
	     *               <li>0-默认搜索类型（现在为模糊搜索） 
	     *               <li>1-模糊搜索：时间参数starttime和endtime间隔小于一小时，时间参数会调整为starttime前endtime后的整点，即调整间隔为1小时 
	     *               <li>8-实时搜索：选择实时搜索，只返回最近几分钟的微博，时间参数需要设置为最近的几分钟范围内才生效，并且不会调整参数间隔 
	     * @param starttime  开始时间，用UNIX时间表示（从1970年1月1日0时0分0秒起至现在的总秒数） endtime  结束时间，与starttime一起使用（必须大于starttime）  
	     * @param endtime  结束时间，与starttime一起使用（必须大于starttime） 
	     * @param province  省编码（不填表示忽略地点搜索）  
	     * @param city  市编码（不填表示按省搜索）  
	     * @param longitue  经度，（实数）*1000000  
	     * @param latitude  纬度，（实数）*1000000  
	     * @param radius  半径（整数，单位米，不大于20000）  

		 * @return
		 * @throws Exception
	     * @see <a href="http://wiki.open.t.qq.com/index.php/%E6%90%9C%E7%B4%A2%E7%9B%B8%E5%85%B3/%E6%90%9C%E7%B4%A2%E5%BE%AE%E5%8D%9A">腾讯微博开放平台上关于此条API的文档</a>
		 */
	public ArrayList<TweetBean> searchTweet(String keyword,
			int pagesize, String page,String contenttype,String sorttype,
			String msgtype ,String searchtype,String starttime, 
			String endtime, String province,String city, String longitue,
			String latitude,String radius) {
		HalloonSearchAPI searchAPI = new HalloonSearchAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> tweetContainer = new ArrayList<TweetBean>(
				pagesize);
		try {
			String tweetInfo;
			tweetInfo = searchAPI.t(preoauth, "json", keyword, pagesize+"", 
					page, contenttype, sorttype, msgtype, searchtype,
					starttime, endtime, province, city, longitue, latitude, radius);

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
			Log.d(Constants.LOG_TAG, "GET_BROADCAST_TIMELINE_ERROR:" + e);
		}

		return tweetContainer;
	}
	
	
	
	/**
	 * 获取他人微博时间线
	 * 
	 * @param pageFlag
	 *            分页标识(0x0:第一页,0x1向下翻页,0x2向上翻页)
	 * @param pageTime
	 *            本页其实时间(第一页填:0x0,向上翻页:填上一次请求返回的第一条记录时间,向下翻页:填上一次请求返回的最后一条记录时间)
	 * @param requestNum
	 *            每次请求的记录条数(1-70);
	 * @param lastId
	 *            和pagetime配合使用(第一页:填0,向上翻页:填上一次请求返回的第一条记录id,向下翻页:
	 *            填上一次请求返回的最后一条记录id)
	 * @param name
	 *            你需要赌球的用户的用户名
	 * @param fopenId
	 *            你需要读取的用户ID name和id至少选一个 如果同时存在则以name为主
	 * @param type
	 *            拉取类型 0x1 原创发表 0x2 转载 0x8 回复 0x10 空回 0x20 提及 0x40 点评
	 *            如需拉取多个类型请使用|，如(0x1|0x2)得到3，此时type=3即可，填零表示拉取所有类型
	 * @param contentType
	 *            内容过滤。0-表示所有类型，1-带文本，2-带链接，4-带图片，8-带视频，0x10-带音频
	 * @return
	 */
	public ArrayList<TweetBean> getOtherTimeLine(String pageFlag,
			String pageTime, int requestNum, String lastId, String name,
			String fopenId, String type, String contentType) {
		StatusesAPI statusesAPI = new StatusesAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> tweetContainer = new ArrayList<TweetBean>(
				requestNum);
		try {
			String tweetInfo;
			tweetInfo = statusesAPI.userTimeline(preoauth, "json", pageFlag,
					pageTime, requestNum + "", lastId, name, fopenId, type,
					contentType);

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
	 * 获取认证用户时间线
	 * 
	 * @param pageFlag
	 *            分页标识(0x0:第一页,0x1向下翻页,0x2向上翻页)
	 * @param pageTime
	 *            本页其实时间(第一页填:0x0,向上翻页:填上一次请求返回的第一条记录时间,向下翻页:填上一次请求返回的最后一条记录时间)
	 * @param requestNum
	 *            每次请求的记录条数(1-70);
	 * @param lastId
	 *            和pagetime配合使用(第一页:填0,向上翻页:填上一次请求返回的第一条记录id,向下翻页:
	 *            填上一次请求返回的最后一条记录id)
	 * @return
	 */
	public ArrayList<TweetBean> getVipTimeLine(String pageFlag,
			String pageTime, int requestNum, String lastId) {
		HalloonStatusesAPI statusesAPI = new HalloonStatusesAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> tweetContainer = new ArrayList<TweetBean>(
				requestNum);
		try {
			String tweetInfo;
			tweetInfo = statusesAPI.homeVipTimeline(preoauth, "json", pageFlag,
					pageTime, requestNum + "", lastId);

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

	public boolean getTweetLike(String id,String name){
		return false;
	}
	
	/**
	 * 从微博唯一ID获取单条微博
	 * 
	 * @param id
	 *            微博唯一ID
	 * @return TweetBean
	 */
	public TweetBean getTweetFromId(String id) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		TweetBean temp_tweet = new TweetBean();

		try {
			String tweetInfo = tApi.show(preoauth, "json", id);
			Log.d(Constants.LOG_TAG, tweetInfo);

			JSONObject jsonObject = new JSONObject(tweetInfo);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			String userInfoObject = dataJsonObject.optString("user");
			temp_tweet = getTweetFromJSON(dataJsonObject);
			temp_tweet.setMentionedUser(userInfoObject);
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_TWEET_FROM_ID_ERROR:" + e);
		}

		return temp_tweet;
	}

	/**
	 * 从微博唯一ID获取回复或者转播
	 * 
	 * @param id
	 * @return
	 */
	public ArrayList<TweetBean> getTweetCommentFromId(String id, String type,
			String pageFlag, String pageTime, String requestNum) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> commentTweetBean = new ArrayList<TweetBean>();
		try {
			String commentInfo = tApi.reList(preoauth, "json", type, id,
					pageFlag, pageTime, requestNum, "0");

			JSONObject jsonObject = new JSONObject(commentInfo);
			if (jsonObject.getString("msg") == "have no tweet") {
				return null;
			} else {
				JSONObject dataJsonObject = jsonObject.getJSONObject("data");
				JSONArray commentInfoArray = dataJsonObject
						.getJSONArray("info");
				String userInfoObject = dataJsonObject.optString("user");
				for (int i = 0; i < commentInfoArray.length(); i++) {
					TweetBean temp_comment = new TweetBean();
					JSONObject commentInfoObject = commentInfoArray
							.getJSONObject(i);
					temp_comment.setNick(commentInfoObject.getString("nick"));
					temp_comment.setName(commentInfoObject.getString("name"));
					temp_comment.setHead(commentInfoObject.getString("head"));
					temp_comment.setText(commentInfoObject
							.getString("origtext"));
					temp_comment.setTimestamp(commentInfoObject
							.getString("timestamp"));
					temp_comment.setTweetImage(commentInfoObject
							.optString("image"));
					temp_comment.setFrom(commentInfoObject.getString("from"));
					temp_comment.setId(commentInfoObject.getString("id"));
					temp_comment.setOpenId(commentInfoObject
							.getString("openid"));
					temp_comment.setMentionedUser(userInfoObject);
					Log.i(Constants.LOG_TAG, temp_comment.getMentionedUser()
							.toString());
					if (commentInfoObject.optJSONObject("source") != null) {
						TweetBean temp_source = new TweetBean();
						JSONObject temp_source_object = commentInfoObject
								.getJSONObject("source");
						temp_source.setId(temp_source_object.getString("id"));
						temp_source.setOpenId(temp_source_object
								.getString("openid"));
						temp_source.setText(temp_source_object
								.getString("origtext"));
						temp_source.setHead(temp_source_object
								.getString("head"));
						temp_source.setNick(temp_source_object
								.getString("nick"));
						temp_source.setName(temp_source_object
								.getString("name"));
						temp_source.setTimestamp(temp_source_object
								.getString("timestamp"));
						temp_source.setTweetImage(temp_source_object
								.optString("image"));
						temp_source.setFrom(temp_source_object
								.getString("from"));
						temp_source.setMentionedUser(userInfoObject);
						temp_comment.setSource(temp_source);
					}
					commentTweetBean.add(temp_comment);
				}
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_TWEET_COMMENT_ERROR:" + e);
		}

		return commentTweetBean;

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

	/**
	 * 获取附近的人
	 * 
	 * @param longitude
	 *            经度
	 * @param latitude
	 *            纬度
	 * @param pageinfo
	 *            翻页参数,由上次请求返回(第一次请求时请填空)
	 * @param num
	 *            人数
	 * @param gender
	 *            性别, 0x0全部, 0x1男, 0x2女
	 * @return
	 */
	public ArrayList<UserBean> getAroundPeople(String longitude,
			String latitude, String pageinfo, String num, String gender) {
		LBSAPI lbsApi = new LBSAPI(OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<UserBean> userBeans = new ArrayList<UserBean>();

		try {
			String lbsAround = lbsApi.getAroundPeople(preoauth, "json",
					longitude, latitude, pageinfo, num, gender);
			JSONObject jsonObject = new JSONObject(lbsAround);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			JSONArray userInfoArray = dataJsonObject.getJSONArray("info");
			for (int i = 0; i < userInfoArray.length(); i++) {
				UserBean userBean = new UserBean();

				JSONObject userInfoObject = userInfoArray.getJSONObject(i);
				userBean.setHead(userInfoObject.getString("head"));
				userBean.setName(userInfoObject.getString("name"));
				userBean.setNick(userInfoObject.getString("nick"));
				userBean.setOpenId(userInfoObject.getString("openid"));
				userBean.setSex(sexType[Integer.valueOf(gender)]);

				userBeans.add(userBean);
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_AROUND_PEOPLE_ERROR:" + e);
		}

		return userBeans;
	}

	/**
	 * 发布纯文字微博
	 * 
	 * @param text
	 * @param ip
	 */
	public int[] addText(String text, String ip) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.add(preoauth, "json",
					text, ip));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "TEXT_TWEET_PUBLISH_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 发布带经纬度微博
	 * 
	 * @param text
	 * @param ip
	 * @param longitude
	 * @param latitude
	 * @param syncFlag
	 *            微博同步到空间分享标记（可选，0-同步，1-不同步，默认为0）
	 */
	public int[] addText(String text, String ip, String longitude,
			String latitude, String syncFlag) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.add(preoauth, "json",
					text, ip, longitude, latitude, syncFlag));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "LOCATION_TEXT_TWEET_PUBLISH_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 发布图片微博
	 * 
	 * @param text
	 * @param imagePath
	 * @param ip
	 */
	public int[] addImageTweet(String text, String imagePath, String ip) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.addPic(preoauth, "json",
					text, ip, imagePath));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "IMAGE_TWEET_PUBLISH_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 发布带经纬度 图片微博
	 * 
	 * @param text
	 * @param imagePath
	 * @param ip
	 * @param longitude
	 * @param latitude
	 * @param syncFlag
	 *            微博同步到空间分享标记（可选，0-同步，1-不同步，默认为0）
	 */
	public int[] addImageTweet(String text, String imagePath, String ip,
			String longitude, String latitude, String syncFlag) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.addPic(preoauth, "json",
					text, ip, longitude, latitude, imagePath, syncFlag));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "LOCATION_IMAGE_TWEET_PUBLISH_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 删除单条微博
	 * 
	 * @param id
	 *            微博ID
	 */
	public int[] delTweet(String id) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.del(preoauth, "json", id));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "DEL_TWEET_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 回复微博
	 * 
	 * @param content
	 * @param id
	 * @param ip
	 * @return
	 */
	public int[] comment(String content, String id, String ip) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.comment(preoauth, "json",
					content, ip, id));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "COMMENT_ERROR:" + e);
		}

		return returnInts;
	}

	
	/**
	 * 赞一条微博
	 * 
	 * @param id  微博id
	 */
	public boolean like(String id)
			throws Exception {
		
		HalloonTAPI tapi = new HalloonTAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		returnInts[0] = 1;
		try {
			JSONObject feedBack = new JSONObject(tapi.like(preoauth, "json", id));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "COMMENT_ERROR:" + e);
		}
		
		return returnInts[0] == 0 || returnInts[0] == 4 && returnInts[1] == 6;
	}
    
	
	/**
	 * 取消赞一条微博
	 * 
	 * @param id  微博id
	 * @param favoriteId 
	 */
	public boolean unlike(String id,String favoriteId)
			throws Exception {
		HalloonTAPI tapi = new HalloonTAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		returnInts[0] = 1;
		try {
			JSONObject feedBack = new JSONObject(tapi.unlike(preoauth, "json", id,favoriteId));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "COMMENT_ERROR:" + e);
		}
		
		return returnInts[0] == 0 ;
	}
	
	/**
	 * 获取自己是否赞了一条微博
	 * 
	 * @param id  微博id
	 * @param name
	 * @param openid
	 */
	public boolean haslike(OAuth oAuth, String format,String id,String name,String openid)
			throws Exception {
		HalloonTAPI tapi = new HalloonTAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		returnInts[0] = 1;
		try {
			JSONObject feedBack = new JSONObject(tapi.haslike(preoauth, "json", id,name,openid));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "COMMENT_ERROR:" + e);
		}
		
		return returnInts[0] == 0 ;
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 回复带经纬度微博
	 * 
	 * @param content
	 * @param id
	 * @param ip
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public int[] comment(String content, String id, String ip,
			Double longitude, Double latitude) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.comment(preoauth, "json",
					content, ip, String.valueOf(longitude),
					String.valueOf(latitude), id));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "LOCATION_COMMENT_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 转发微博
	 * 
	 * @param content
	 * @param id
	 * @param ip
	 * @return
	 */
	public int[] retweet(String content, String id, String ip) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.reAdd(preoauth, "json",
					content, ip, id));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "RETWEET_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 转发带经纬度微博
	 * 
	 * @param content
	 * @param id
	 * @param ip
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public int[] retweet(String content, String id, String ip,
			Double longitude, Double latitude) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.reAdd(preoauth, "json",
					content, ip, String.valueOf(longitude),
					String.valueOf(latitude), id));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "LOCATION_RETWEET_ERROR:" + e);
		}

		return returnInts;
	}

	public int[] addFav(String id) {
		FavAPI favApi = new FavAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(favApi.addFav(preoauth,
					"json", id));

			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "ADD_FAV_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 获取未读计数
	 * 
	 * @param op
	 *            请求的方式 0：仅获取数据更新的条数 1.后去完毕后将相应的技术清零
	 * @param type
	 *            需获取的更新数据的类型 5：首页未读消息计数 6.@页未读消息计数 7.私信页未读消息计数 8.新增听众数
	 *            9。首页新增的原创广播数 op=0时不输入type 返回所有类型计数 op=1时需输入type 返回所有类型计数
	 *            同时清除该type类型的技术
	 * @return
	 */
	public HashMap<String, String> getUnreadCount(String op, String type) {
		OtherAPI otherApi = new OtherAPI(OAuthConstants.OAUTH_VERSION_2_A);
		HashMap<String, String> tmpHash = new HashMap<String, String>();
		try {
			String count = otherApi.getUpdateCount(preoauth, "json", op, type);
			JSONObject countJson = new JSONObject(count);
			JSONObject data = countJson.getJSONObject("data");
			tmpHash.put("home", data.getString("home"));
			tmpHash.put("private", data.getString("private"));
			tmpHash.put("fans", data.getString("fans"));
			tmpHash.put("mentions", data.getString("mentions"));
			tmpHash.put("create", data.getString("create"));
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_COUNT_ERROR:" + e);
		}

		return tmpHash;
	}

	/**
	 * 获取私信的首页会话列表，包括未读信息数、会话人账号等信息。
	 * 
	 * @param pageFlag
	 *            分页标识（0：第一页，1：向下翻页，2：向上翻页）
	 * @param pageTime
	 *            本页起始时间，用于翻页，和pageflag、lastid配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，
	 *            向下翻页：填上一次请求返回的最后一条记录时间）
	 * @param requestNum
	 *            每次请求记录的条数（最多70条）
	 * @param lastId
	 *            用于翻页，和pageflag、pagetime配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录id，向下翻页：
	 *            填上一次请求返回的最后一条记录id）
	 * @return
	 */
	public ArrayList<PrivateDataBean> getPrivateHomeTimeLine(String pageFlag,
			String pageTime, String requestNum, String lastId) {
		PrivateMessageAPI privateApi = new PrivateMessageAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<PrivateDataBean> privateDataBeans = new ArrayList<PrivateDataBean>();
		try {
			String privateHomeTimeLine = privateApi.homeTimeline(preoauth,
					"json", pageFlag, pageTime, requestNum, lastId);
			JSONObject privateMessageJsonObject = new JSONObject(
					privateHomeTimeLine);
			JSONObject dataJsonObject = privateMessageJsonObject
					.getJSONObject("data");
			JSONArray privateInfoArray = dataJsonObject.getJSONArray("info");
			for (int i = 0; i < privateInfoArray.length(); i++) {
				JSONObject privateDataObject = privateInfoArray
						.getJSONObject(i);
				PrivateDataBean privateDataBean = new PrivateDataBean();
				privateDataBean.setHead(privateDataObject.getString("tohead"));
				privateDataBean.setName(privateDataObject.getString("toname"));
				privateDataBean.setNick(privateDataObject.getString("tonick"));
				privateDataBean.setImage(privateDataObject.optString("image"));
				privateDataBean.setIsVip(privateDataObject.getInt("toisvip"));
				privateDataBean.setTotalCount(privateDataObject
						.getString("totalcnt"));
				privateDataBean.setUnReadCount(privateDataObject
						.getString("unreadcnt"));
				privateDataBean.setOpenId(privateDataObject
						.getString("toopenid"));
				privateDataBean.setTweeted(privateDataObject
						.getString("tweetid"));
				privateDataBean.setPubTime(privateDataObject
						.getString("pubtime"));
				privateDataBean.setMsgBox(privateDataObject.getInt("msgbox"));
				privateDataBean.setReadFlag(privateDataObject
						.getInt("readflag"));
				privateDataBean
						.setRoomId(privateDataObject.optString("roomid"));
				privateDataBean
						.setText(privateDataObject.getString("origtext"));

				privateDataBeans.add(privateDataBean);
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_PRIVATE_HOME_TIME_LINE_ERROR:" + e);
		}

		return privateDataBeans;
	}

	/**
	 * 获取私信详细对话内容
	 * 
	 * @param pageFlag
	 * @param pageTime
	 * @param requestNum
	 * @param lastId
	 * @param name
	 * @param fopenId
	 * @return
	 */
	public ArrayList<PrivateDataBean> getPrivateConversation(String pageFlag,
			String pageTime, String requestNum, String lastId, String name,
			String fopenId) {
		PrivateMessageAPI privateApi = new PrivateMessageAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<PrivateDataBean> privateDataBeans = new ArrayList<PrivateDataBean>();
		try {
			String privateConversation = privateApi.userTimeline(preoauth,
					"json", pageFlag, pageTime, requestNum, lastId, name,
					fopenId);
			JSONObject privateConversationJsonObject = new JSONObject(
					privateConversation);
			JSONObject dataJsonObject = privateConversationJsonObject
					.getJSONObject("data");

			JSONArray privateInfoArray = dataJsonObject.getJSONArray("info");

			for (int i = 0; i < privateInfoArray.length(); i++) {
				JSONObject privateDataObject = privateInfoArray
						.getJSONObject(i);
				PrivateDataBean privateDataBean = new PrivateDataBean();

				privateDataBean.setHead(privateConversationJsonObject
						.getString("tohead"));
				privateDataBean.setMyHead(privateConversationJsonObject
						.getString("head"));
				privateDataBean.setName(privateConversationJsonObject
						.getString("toname"));
				privateDataBean.setNick(privateConversationJsonObject
						.getString("tonick"));
				privateDataBean.setOpenId(privateConversationJsonObject
						.getString("toopenid"));
				privateDataBean.setIsVip(privateConversationJsonObject
						.getInt("isvip"));

				privateDataBean.setPubTime(privateDataObject
						.getString("pubtime"));
				privateDataBean.setMsgBox(privateDataObject.getInt("msgbox"));
				privateDataBean
						.setText(privateDataObject.getString("origtext"));
				privateDataBean.setTweeted(privateDataObject
						.getString("tweetid"));
				privateDataBean.setReadFlag(privateDataObject
						.getInt("readflag"));

				privateDataBeans.add(0, privateDataBean);
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_PRIVATE_USER_TIME_LINE_ERROR:" + e);
		}

		return privateDataBeans;
	}

	/**
	 * 用户提及时间线
	 * 
	 * @param pageFlag
	 *            分页标识（0：第一页，1：向下翻页，2向上翻页）
	 * @param pageTime
	 *            本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间）
	 * @param requestNum
	 *            每次请求记录的条数（1-100条）
	 * @param lastId
	 *            和pagetime配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录id，向下翻页：
	 *            填上一次请求返回的最后一条记录id）
	 * @param type
	 *            拉取类型 0x1 原创发表 0x2 转载 0x8 回复 0x10 空回 0x20 提及 0x40 点评
	 *            如需拉取多个类型请使用|，如(0x1|0x2)得到3，此时type=3即可，填零表示拉取所有类型
	 * @param contentType
	 *            内容过滤。0-表示所有类型，1-带文本，2-带链接，4-带图片，8-带视频，0x10-带音频
	 * @return
	 */
	public ArrayList<TweetBean> getMentionsHomeTimeLine(String pageFlag,
			String pageTime, String requestNum, String lastId, String type,
			String contentType) {
		StatusesAPI statusesApi = new StatusesAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> tweetBeans = new ArrayList<TweetBean>();
		try {
			String mentionsHomeTimeLine = statusesApi.mentionsTimeline(
					preoauth, "json", pageFlag, pageTime, requestNum, lastId,
					type, contentType);
			JSONObject mentionsJsonObject = new JSONObject(mentionsHomeTimeLine);
			JSONObject dataJsonObject = mentionsJsonObject
					.getJSONObject("data");
			JSONArray mentionsJsonArray = dataJsonObject.getJSONArray("info");
			String userJsonObject = dataJsonObject.optString("user");
			for (int i = 0; i < mentionsJsonArray.length(); i++) {
				TweetBean tweetBean = new TweetBean();
				JSONObject mentionsObject = mentionsJsonArray.getJSONObject(i);
				tweetBean = getTweetFromJSON(mentionsObject);
				tweetBean.setMentionedUser(userJsonObject);

				tweetBeans.add(tweetBean);
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_MENTIONS_HOME_LINE_ERROR:" + e);
		}

		return tweetBeans;
	}

	/**
	 * 获取周边微博
	 * 
	 * @param longitude
	 * @param latitude
	 * @param pageInfo
	 *            翻页参数,由上次请求返回(第一次请求时请填空)
	 * @param pageSize
	 *            请求的每页个数(1-50),建议25
	 * @return
	 */
	public ArrayList<TweetBean> getAroundTweet(String longitude,
			String latitude, String pageInfo, String pageSize) {
		LBSAPI LBSApi = new LBSAPI(OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> tweetBeans = new ArrayList<TweetBean>();
		try {
			String aroundTweet = LBSApi.getAroundNew(preoauth, "json",
					longitude, latitude, pageInfo, pageSize);
			JSONObject aroundTweetJsonObject = new JSONObject(aroundTweet);
			JSONObject dataJsonObject = aroundTweetJsonObject
					.getJSONObject("data");
			JSONArray aroundJsonArray = dataJsonObject.getJSONArray("info");
			String userJsonObject = dataJsonObject.optString("user");
			for (int i = 0; i < aroundJsonArray.length(); i++) {
				TweetBean tweetBean = new TweetBean();
				JSONObject aroundObject = aroundJsonArray.getJSONObject(i);
				tweetBean = getTweetFromJSON(aroundObject);
				tweetBean.setMentionedUser(userJsonObject);

				tweetBeans.add(tweetBean);
			}
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_AROUND_NEW_ERROR:" + e);
		}

		return tweetBeans;
	}

	/**
	 * 获取长连接
	 * 
	 * @param url
	 * @return
	 */
	public String getExpandedUrl(String url) {
		ShortUrlAPI shortUrlApi = new ShortUrlAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		String longUrl = null;
		try {
			longUrl = shortUrlApi.getExpandedUrl(preoauth, "json", url);
			JSONObject longUrlObject = new JSONObject(longUrl);
			longUrl = longUrlObject.getJSONObject("data").getString("long_url");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_SHORT_URL_ERROR: " + e);
		}

		return longUrl;
	}

	public int[] addIdol(String name, String openId) {
		FriendsAPI friendsApi = new FriendsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedback = new JSONObject(friendsApi.add(preoauth,
					"json", name, openId));
			returnInts[0] = feedback.getInt("ret");
			returnInts[1] = feedback.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "ADD IDOL ERROR: " + e);
		}

		return returnInts;
	}

	public int[] delIdol(String name, String openId) {
		FriendsAPI friendsApi = new FriendsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedback = new JSONObject(friendsApi.del(preoauth,
					"json", name, openId));
			returnInts[0] = feedback.getInt("ret");
			returnInts[1] = feedback.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "DEL IDOL ERROR: " + e);
		}

		return returnInts;
	}
	
	
	
	//
	public ArrayList<UserBean> getUserFans(String name,String fopenid,int startindex) {
		ArrayList<UserBean> beans = new ArrayList<UserBean>();

		FriendsAPI friendsAPI = new FriendsAPI(OAuthConstants.OAUTH_VERSION_2_A);
			try {
				String response = friendsAPI.userFanslist(preoauth, "json", REQNUM
						+ "",startindex+ "", name,fopenid,"0","0");

				JSONObject jsonObject = new JSONObject(response);
				JSONObject dataJsonObject = jsonObject.getJSONObject("data");
				JSONArray jsonArray = dataJsonObject.getJSONArray("info");

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObject = jsonArray.getJSONObject(i);
					/* if (jObject.getBoolean("isidol")) { */
					UserBean userBean = new UserBean();
					
//					name : 帐户名,
//					openid : 用户唯一id，与name相对应,
//					nick : 昵称,
//					head : 头像url,
//					sex : 用户性别，1-男，2-女，0-未填写,
//					location : 用户发表微博时的所在地,
//					country_code : 国家码,
//					province_code : 省份码,
//					city_code : 城市码,
//					tweet : 用户最近发的一条微博
//					{
//						text : 微博内容,
//						from : 来源,
//						id : 微博id,
//						timestamp : 微博时间戳
//					},
//					fansnum : 听众数,
//					idolnum : 偶像数,
//					isfans : 是否我的听众，0-不是，1-是,
//					isvip : 是否名人用户,
//					tag : 用户标签
//					{
//						id : 标签id,
//						name : 标签名
//					}
//				}
					
					
					userBean.setOpenId(jObject.getString("openid"));
					userBean.setName(jObject.getString("name"));
					userBean.setNick(jObject.getString("nick"));
					userBean.setHead(jObject.getString("head"));
					userBean.setSex(sexType[jObject.getInt("sex")]);
					
					userBean.setFansnum(jObject.getString("fansnum"));
					userBean.setIdolnum(jObject.getString("idolnum"));
					userBean.setIsfans(jObject.getString("isfans"));
					userBean.setIsvip(jObject.getString("isvip"));
					
					JSONArray tweetArray = jObject.getJSONArray("tweet");
					for (int j = 0; j < tweetArray.length(); j++) {
						JSONObject tweetObject = tweetArray.getJSONObject(j);
						TweetBean tweetBean = new TweetBean();
						tweetBean.setId(tweetObject.getString("id"));
						tweetBean.setText(tweetObject.getString("text"));
						tweetBean.setFrom(tweetObject.getString("from"));
						tweetBean.setTimestamp(tweetObject
								.getString("timestamp"));	
						userBean.setTweetBean(tweetBean);
					}
					
					//fansnum : xxx,
//					idolnum : xxx,
//					isidol : xxx,
//					isvip : xxx,
//					tag : [
//					{
//						id : xxx,
//						name : xxx
//					}]
					
					
					beans.add(userBean);

				}
			} catch (Exception e) {
				Log.d(Constants.LOG_TAG, "GET_CONTACTS_ERROR:" + e);
			}
			return beans;
	}
	
	//
	public ArrayList<UserBean> getUserIdol(String name,String fopenid,int startindex) {
		ArrayList<UserBean> beans = new ArrayList<UserBean>();

		FriendsAPI friendsAPI = new FriendsAPI(OAuthConstants.OAUTH_VERSION_2_A);
			try {
				String response = friendsAPI.userIdollist(preoauth, "json", REQNUM
						+ "",startindex+ "", name,fopenid,"0");

				JSONObject jsonObject = new JSONObject(response);
				JSONObject dataJsonObject = jsonObject.getJSONObject("data");
				JSONArray jsonArray = dataJsonObject.getJSONArray("info");

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObject = jsonArray.getJSONObject(i);
					/* if (jObject.getBoolean("isidol")) { */
					UserBean userBean = new UserBean();
					userBean.setOpenId(jObject.getString("openid"));
					userBean.setName(jObject.getString("name"));
					userBean.setNick(jObject.getString("nick"));
					userBean.setHead(jObject.getString("head"));
					userBean.setSex(sexType[jObject.getInt("sex")]);

					

					userBean.setFansnum(jObject.getString("fansnum"));
					userBean.setIdolnum(jObject.getString("idolnum"));
					userBean.setIsidol(jObject.getString("isidol"));
					userBean.setIsvip(jObject.getString("isvip"));
					
					
					JSONArray tweetArray = jObject.getJSONArray("tweet");
					for (int j = 0; j < tweetArray.length(); j++) {
						JSONObject tweetObject = tweetArray.getJSONObject(j);
						TweetBean tweetBean = new TweetBean();
						tweetBean.setId(tweetObject.getString("id"));
						tweetBean.setText(tweetObject.getString("text"));
						tweetBean.setFrom(tweetObject.getString("from"));
						tweetBean.setTimestamp(tweetObject
								.getString("timestamp"));	
						userBean.setTweetBean(tweetBean);
					}
					
					//fansnum : xxx,
//					idolnum : xxx,
//					isidol : xxx,
//					isvip : xxx,
//					tag : [
//					{
//						id : xxx,
//						name : xxx
//					}]
					
					
					beans.add(userBean);

				}
			} catch (Exception e) {
				Log.d(Constants.LOG_TAG, "GET_CONTACTS_ERROR:" + e);
			}
			return beans;
	}
	
}
