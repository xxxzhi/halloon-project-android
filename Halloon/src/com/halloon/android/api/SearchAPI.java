package com.halloon.android.api;

import org.apache.http.message.BasicNameValuePair;

import com.tencent.weibo.api.BasicAPI;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.QArrayList;
import com.tencent.weibo.utils.QHttpClient;

public class SearchAPI extends BasicAPI {

	private String searchWeibo = apiBaseUrl + "/search/t";
	private String searchUser = apiBaseUrl + "/search/user";

	public SearchAPI(String OAuthVersion) {
		super(OAuthVersion);
	}

	public SearchAPI(String OAuthVersion, QHttpClient qHttpClient) {
		super(OAuthVersion, qHttpClient);
	}

	/**
	 * 本接口用于根据微博的搜索关键字搜索带有关键字微博的相关信息，譬如：微博内容、微博被转次数、点评次数等；
	 * 每次调用搜索接口最多支持1500条微博数据的返回。
	 * 
	 * @param format
	 *            是 返回数据的格式（json或xml）
	 * @param keyword
	 *            是 搜索关键字（1-128字节）
	 * @param pagesize
	 *            是 每页大小（1-30个）
	 * @param page
	 *            是 页码
	 * @param contenttype
	 *            是 消息的正文类型（按位使用）
	 *            0-所有，0x01-纯文本，0x02-包含url，0x04-包含图片，0x08-包含视频，0x10-包含音频
	 * @param sorttype
	 *            是 排序方式 1-表示先按时间排序（最新）；2-表示按照转播次数排序（最热）；4-表示按相关性排序(最优)
	 * @param msgtype
	 *            是 消息的类型（按位使用） 0-所有，1-原创发表，2
	 *            转载，8-回复(针对一个消息，进行对话)，0x10-空回(点击客人页，进行对话)
	 * @param searchtype
	 *            是 搜索类型 0-默认搜索类型（现在为模糊搜索）
	 *            1-模糊搜索：时间参数starttime和endtime间隔小于一小时，时间参数会调整为starttime前endtime后的整点
	 *            ，即调整间隔为1小时 8-实时搜索：选择实时搜索，起止时间如果设置为0的话，默认返回最近15分钟之内的微博数据，否则，
	 *            起止时间参数的区间需要设置为最近的十五分钟范围内才生效。
	 * @param starttime
	 *            是 开始时间，用UNIX时间表示（从1970年1月1日0时0分0秒起至现在的总秒数）
	 * @param endtime
	 *            是 结束时间，与starttime一起使用（必须大于starttime）
	 * @param longitue
	 *            是 经度，（实数）*1000000
	 * @param latitude
	 *            是 纬度，（实数）*1000000
	 * @param radius
	 *            是 半径（整数，单位米，不大于20000）
	 * @param province
	 *            是 发表微博的用户所在的省代码。 省份名称与代码的对应关系请参见 国家省份名称对应代码列表。
	 *            可选。如果不填默认为忽略地点搜索。
	 * @param city
	 *            是 发表微博的用户所在的市代码。 城市名称与代码的对应关系请参见 国家城市名称对应代码列表。 可选。如果不填默认为按省搜索。
	 * @param needdup
	 *            是 用于重复微博过滤，0：不排重 1：排重。默认为0。 vip 否 用于名人搜索，0：非名人；1：名人。默认为0。
	 * @return
	 * @throws Exception
	 * @see <a
	 *      href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E6%90%9C%E7%B4%A2%E6%8E%A5%E5%8F%A3/%E6%90%9C%E7%B4%A2%E5%BE%AE%E5%8D%9A">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String searchWeibo(OAuth oAuth, String format, String keyword,
			int pagesize, int page,String contenttype,int sorttype, int searchtype, long starttime,
			long endtime,String longitue,String latitude,String radius,
			String province,String city,int needdup) throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("keyword", keyword));
		paramsList.add(new BasicNameValuePair("pagesize", pagesize+""));
		paramsList.add(new BasicNameValuePair("page", page+""));
		paramsList.add(new BasicNameValuePair("contentType", contenttype+""));
		paramsList.add(new BasicNameValuePair("sorttype", sorttype+""));
		paramsList.add(new BasicNameValuePair("searchtype", searchtype+""));
		paramsList.add(new BasicNameValuePair("starttime", starttime+""));
		paramsList.add(new BasicNameValuePair("endtime", endtime+""));
		paramsList.add(new BasicNameValuePair("longitue", longitue));
		paramsList.add(new BasicNameValuePair("latitude", latitude));
		paramsList.add(new BasicNameValuePair("radius", radius));
		paramsList.add(new BasicNameValuePair("province", province));
		paramsList.add(new BasicNameValuePair("city", city));
		paramsList.add(new BasicNameValuePair("needdup", needdup+""));

		return requestAPI.getResource(searchWeibo, paramsList, oAuth);
	}

	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;

		String appendix = "/search/";

		searchWeibo = apiBaseUrl + appendix + "t";
		searchUser = apiBaseUrl + appendix + "user";
	}

}
