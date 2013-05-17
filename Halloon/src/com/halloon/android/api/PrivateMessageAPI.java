package com.halloon.android.api;

import org.apache.http.message.BasicNameValuePair;

import com.tencent.weibo.api.BasicAPI;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.QArrayList;
import com.tencent.weibo.utils.QHttpClient;

/**
 * 私信相关API
 * 
 * @see <a href=
 *      "http://wiki.open.t.qq.com/index.php/%E7%A7%81%E4%BF%A1%E7%9B%B8%E5%85%B3"
 *      >腾讯微博开放平台上私信相关的API文档<a>
 */

public class PrivateMessageAPI extends BasicAPI {

	private String privateAddUrl = apiBaseUrl + "/private/add";
	private String privateDelUrl = apiBaseUrl + "/private/del";
	private String privateRecvUrl = apiBaseUrl + "/private/recv";
	private String privateSendUrl = apiBaseUrl + "/private/send";
	private String privateHomeTimeLineUrl = apiBaseUrl
			+ "/private/home_timeline";
	private String privateUserTimeLineUrl = apiBaseUrl
			+ "/private/user_timeline";

	/**
	 * 使用完毕后，请调用 shutdownConnection() 关闭自动生成的连接管理器
	 * 
	 * @param OAuthVersion
	 *            根据OAuthVersion，配置通用请求参数
	 */
	public PrivateMessageAPI(String OAuthVersion) {
		super(OAuthVersion);
	}

	/**
	 * @param OAuthVersion
	 *            根据OAuthVersion，配置通用请求参数
	 * @param qHttpClient
	 *            使用已有的连接管理器
	 */
	public PrivateMessageAPI(String OAuthVersion, QHttpClient qHttpClient) {
		super(OAuthVersion, qHttpClient);
	}

	/**
	 * 本接口用于给用户发送私信，私信只有通信双方或私信群中成员能够看到，其他用户不能看到。
	 * 
	 * @param oAuth
	 * @param format
	 *            返回数据的格式（json或xml）
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
	 * @throws Exception
	 * @see <a
	 *      href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E7%A7%81%E4%BF%A1%E7%9B%B8%E5%85%B3/%E5%8F%91%E7%A7%81%E4%BF%A1">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String add(OAuth oAuth, String format, String content,
			String clientip, String name, String fopenid, String contentflag,
			String pic) throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("content", content));
		paramsList.add(new BasicNameValuePair("clientip", clientip));
		paramsList.add(new BasicNameValuePair("name", name));
		paramsList.add(new BasicNameValuePair("fopenid", fopenid));
		paramsList.add(new BasicNameValuePair("contentflag", contentflag));
		paramsList.add(new BasicNameValuePair("pic", pic));

		return requestAPI.postContent(privateAddUrl, paramsList, oAuth);
	}

	/**
	 * 删除一条微博
	 * 
	 * @param oAuth
	 * @param format
	 *            返回数据的格式(json或xml)
	 * @param id
	 *            私信ID
	 * @return
	 * @throws Exception
	 * @see <a
	 *      href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E7%A7%81%E4%BF%A1%E7%9B%B8%E5%85%B3/%E5%88%A0%E9%99%A4%E4%B8%80%E6%9D%A1%E7%A7%81%E4%BF%A1">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String del(OAuth oAuth, String format, String id) throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("id", id));

		return requestAPI.postContent(privateDelUrl, paramsList, oAuth);
	}

	/**
	 * 获取私信收件箱列表
	 * 
	 * @param oAuth
	 * @param format
	 *            返回数据的格式（json或xml）
	 * @param pageflag
	 *            分页标识（0：第一页，1：向下翻页，2向上翻页）
	 * @param pagetime
	 *            本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间）
	 * @param reqnum
	 *            每次请求记录的条数（1-20条）
	 * @param lastid
	 *            用于翻页，和pagetime配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录id，向下翻页：
	 *            填上一次请求返回的最后一条记录id）
	 * @param contenttype
	 *            内容过滤。0-所有类型，1-带文本，2-带链接，4-带图片，8-带视频，16-带音频，默认为0
	 * @return
	 * @throws Exception
	 * @see <a
	 *      href="http://wiki.open.t.qq.com/index.php/%E7%A7%81%E4%BF%A1%E7%9B%B8%E5%85%B3/%E6%94%B6%E4%BB%B6%E7%AE%B1">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String recv(OAuth oAuth, String format, String pageflag,
			String pagetime, String reqnum, String lastid, String contenttype)
			throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("pageflag", pageflag));
		paramsList.add(new BasicNameValuePair("pagetime", pagetime));
		paramsList.add(new BasicNameValuePair("reqnum", reqnum));
		paramsList.add(new BasicNameValuePair("lastid", lastid));
		paramsList.add(new BasicNameValuePair("contenttype", contenttype));

		return requestAPI.getResource(privateRecvUrl, paramsList, oAuth);
	}

	/**
	 * 获取私信发件箱列表
	 * 
	 * @param oAuth
	 * @param format
	 *            返回数据的格式（json或xml）
	 * @param pageflag
	 *            分页标识（0：第一页，1：向下翻页，2向上翻页）
	 * @param pagetime
	 *            本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间）
	 * @param reqnum
	 *            每次请求记录的条数（1-20条）
	 * @param lastid
	 *            用于翻页，和pagetime配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录id，向下翻页：
	 *            填上一次请求返回的最后一条记录id）
	 * @param contenttype
	 *            内容过滤。0-所有类型，1-带文本，2-带链接，4-带图片，8-带视频，16-带音频，默认为0
	 * @return
	 * @throws Exception
	 * @see <a
	 *      href="http://wiki.open.t.qq.com/index.php/%E7%A7%81%E4%BF%A1%E7%9B%B8%E5%85%B3/%E5%8F%91%E4%BB%B6%E7%AE%B1">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String send(OAuth oAuth, String format, String pageflag,
			String pagetime, String reqnum, String lastid, String contenttype)
			throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("pageflag", pageflag));
		paramsList.add(new BasicNameValuePair("pagetime", pagetime));
		paramsList.add(new BasicNameValuePair("reqnum", reqnum));
		paramsList.add(new BasicNameValuePair("lastid", lastid));
		paramsList.add(new BasicNameValuePair("contenttype", contenttype));

		return requestAPI.getResource(privateSendUrl, paramsList, oAuth);
	}

	/**
	 * 本接口用于获取私信的首页会话列表，包括未读信息数、会话人账号等信息。
	 * 
	 * @param oAuth
	 * @param format
	 *            返回数据的格式（json或xml）
	 * @param pageflag
	 *            分页标识（0：第一页，1：向下翻页，2：向上翻页）
	 * @param pagetime
	 *            本页起始时间，用于翻页，和pageflag、lastid配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，
	 *            向下翻页：填上一次请求返回的最后一条记录时间）
	 * @param reqnum
	 *            每次请求记录的条数（最多70条）
	 * @param lastid
	 *            用于翻页，和pageflag、pagetime配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录id，向下翻页：
	 *            填上一次请求返回的最后一条记录id）
	 * @return
	 * @throws Exception
	 */
	public String homeTimeline(OAuth oAuth, String format, String pageflag,
			String pagetime, String reqnum, String lastid) throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("pageflag", pageflag));
		paramsList.add(new BasicNameValuePair("pagetime", pagetime));
		paramsList.add(new BasicNameValuePair("reqnum", reqnum));
		paramsList.add(new BasicNameValuePair("lastid", lastid));

		return requestAPI
				.getResource(privateHomeTimeLineUrl, paramsList, oAuth);
	}

	/**
	 * 本接口用于获取与某人的私信会话列表，包括私信内容、对话人信息等。
	 * 
	 * @param oAuth
	 * @param format
	 *            返回数据的格式（json或xml）
	 * @param pageflag
	 *            分页标识（0：第一页，1：向下翻页，2：向上翻页）
	 * @param pagetime
	 *            本页起始时间，用于翻页，和pageflag、lastid配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，
	 *            向下翻页：填上一次请求返回的最后一条记录时间）
	 * @param reqnum
	 *            每次请求记录的条数（最多70条）
	 * @param lastid
	 *            用于翻页，和pageflag、pagetime配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录id，向下翻页：
	 *            填上一次请求返回的最后一条记录id）
	 * @param name
	 *            会话人微博账号（可选） name和fopenid至少选一个，若同时存在则以name值为主
	 * @param fopenid
	 *            他人的openid（可选）name和fopenid至少选一个，若同时存在则以name值为主
	 * @return
	 * @throws Exception
	 */
	public String userTimeline(OAuth oAuth, String format, String pageflag,
			String pagetime, String reqnum, String lastid, String name,
			String fopenid) throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("pageflag", pageflag));
		paramsList.add(new BasicNameValuePair("pagetime", pagetime));
		paramsList.add(new BasicNameValuePair("reqnum", reqnum));
		paramsList.add(new BasicNameValuePair("lastid", lastid));
		paramsList.add(new BasicNameValuePair("name", name));
		paramsList.add(new BasicNameValuePair("fopenid", fopenid));

		return requestAPI
				.getResource(privateUserTimeLineUrl, paramsList, oAuth);
	}

	public void setAPIBaseUrl(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
		privateAddUrl = apiBaseUrl + "/private/add";
		privateDelUrl = apiBaseUrl + "/private/del";
		privateRecvUrl = apiBaseUrl + "/private/recv";
		privateSendUrl = apiBaseUrl + "/private/send";
		privateHomeTimeLineUrl = apiBaseUrl + "/private/home_timeline";
		privateUserTimeLineUrl = apiBaseUrl + "/private/user_timeline";

	}
}
