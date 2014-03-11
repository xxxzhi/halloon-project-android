package com.halloon.android.api;

import org.apache.http.message.BasicNameValuePair;

import com.tencent.weibo.api.StatusesAPI;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.QArrayList;
import com.tencent.weibo.utils.QHttpClient;

public class HalloonStatusesAPI extends StatusesAPI {
	private final String statusesHomeVipTimelineUrl =apiBaseUrl+"/statuses/home_timeline_vip";
	private final String statusesPublicTimelineUrl = apiBaseUrl +"/statuses/public_timeline";
	private final String statusesSpecialTimelineUrl = apiBaseUrl +"/statuses/special_timeline";
	
	public HalloonStatusesAPI(String OAuthVersion, QHttpClient qHttpClient) {
		super(OAuthVersion, qHttpClient);
	}

	public HalloonStatusesAPI(String OAuthVersion) {
		super(OAuthVersion);
	}
	
	
   /**
	 * home vip 时间线
	 * 
	 * @param oAuth
	 * @param format 返回数据的格式 是（json或xml）
	 * @param pageflag 分页标识（0：第一页，1：向下翻页，2向上翻页）
	 * @param pagetime 本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间） 
	 * @param reqnum 每次请求记录的条数（1-70条）
	 * @param type 拉取类型 0x1 原创发表 0x2 转载 0x8 回复 0x10 空回 0x20 提及 0x40 点评 <br>
     *                          如需拉取多个类型请使用|，如(0x1|0x2)得到3，此时type=3即可，填零表示拉取所有类型 
	 * @param contenttype 内容过滤。0-表示所有类型，1-带文本，2-带链接，4-带图片，8-带视频，0x10-带音频 
	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E6%97%B6%E9%97%B4%E7%BA%BF/%E4%B8%BB%E9%A1%B5%E6%97%B6%E9%97%B4%E7%BA%BF">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String homeVipTimeline(OAuth oAuth, String format, String pageflag,
			String pagetime, String reqnum,String lastid) throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("pageflag", pageflag));
		paramsList.add(new BasicNameValuePair("pagetime", pagetime));
		paramsList.add(new BasicNameValuePair("reqnum", reqnum));
        paramsList.add(new BasicNameValuePair("lastid", lastid));
        
		return requestAPI.getResource(statusesHomeVipTimelineUrl,
				paramsList, oAuth);
	}
	
	   /**
		 * home vip 时间线
		 * 
		 * @param oAuth
		 * @param format 返回数据的格式 是（json或xml）
		 * @param pos 记录的起始位置（第一次请求时填0，继续请求时填上次请求返回的pos）
		 * @param reqnum 每次请求记录的条数（1-70条）
		 * @return
		 * @throws Exception
	     * @see <a href="http://wiki.open.t.qq.com/index.php/%E6%97%B6%E9%97%B4%E7%BA%BF/%E4%B8%BB%E9%A1%B5%E6%97%B6%E9%97%B4%E7%BA%BF">腾讯微博开放平台上关于此条API的文档</a>
		 */
		public String publicTimeline(OAuth oAuth, String format, String pos,
				  String reqnum ) throws Exception {
			QArrayList paramsList = new QArrayList();
			paramsList.add(new BasicNameValuePair("format", format));
			paramsList.add(new BasicNameValuePair("pos", pos));
			paramsList.add(new BasicNameValuePair("reqnum", reqnum));
			
			return requestAPI.getResource(statusesPublicTimelineUrl,
					paramsList, oAuth);
		}
	
	    /**
		 * 主页时间线
		 * 
		 * @param oAuth
		 * @param format 返回数据的格式 是（json或xml）
		 * @param pageflag 分页标识（0：第一页，1：向下翻页，2向上翻页）
		 * @param pagetime 本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间） 
		 * @param reqnum 每次请求记录的条数（1-70条）
		 * @param lastid 和@param pagetime配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录id，向下翻页：填上一次请求返回的最后一条记录id）
		 * @param type 拉取类型 0x1 原创发表 0x2 转载 0x8 回复 0x10 空回 0x20 提及 0x40 点评 <br>
	     *                          如需拉取多个类型请使用|，如(0x1|0x2)得到3，此时type=3即可，填零表示拉取所有类型 
		 * @param contenttype 内容过滤。0-表示所有类型，1-带文本，2-带链接，4-带图片，8-带视频，0x10-带音频 
		 * @return
		 * @throws Exception
	     * @see <a href="http://wiki.open.t.qq.com/index.php/%E6%97%B6%E9%97%B4%E7%BA%BF/%E4%B8%BB%E9%A1%B5%E6%97%B6%E9%97%B4%E7%BA%BF">腾讯微博开放平台上关于此条API的文档</a>
		 */
		public String specialTimeline(OAuth oAuth, String format, String pageflag,
				String pagetime, String reqnum,String lastid,String type, String contenttype) throws Exception {
			QArrayList paramsList = new QArrayList();
			paramsList.add(new BasicNameValuePair("format", format));
			paramsList.add(new BasicNameValuePair("pageflag", pageflag));
			paramsList.add(new BasicNameValuePair("pagetime", pagetime));
			paramsList.add(new BasicNameValuePair("reqnum", reqnum));
			paramsList.add(new BasicNameValuePair("lastid", lastid));
	        paramsList.add(new BasicNameValuePair("type", type));
	        paramsList.add(new BasicNameValuePair("contenttype", contenttype));
			
			return requestAPI.getResource(statusesSpecialTimelineUrl,
					paramsList, oAuth);
		}
		

}
