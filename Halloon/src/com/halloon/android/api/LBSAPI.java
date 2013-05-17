package com.halloon.android.api;

import org.apache.http.message.BasicNameValuePair;

import com.tencent.weibo.api.BasicAPI;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.QArrayList;
import com.tencent.weibo.utils.QHttpClient;

public class LBSAPI extends BasicAPI {

	// private String lbsBaseUrl = "http://ugc.map.soso.com/";

	private String lbsUpdatePos = apiBaseUrl + "/lbs/update_pos";
	private String lbsDelPos = apiBaseUrl + "/lbs/del_pos";
	private String lbsGetPOI = apiBaseUrl + "/lbs/get_poi";
	private String lbsGetAroundNew = apiBaseUrl + "/lbs/get_around_new";
	private String lbsGetAroundPeople = apiBaseUrl + "/lbs/get_around_people";
	private String lbsRGeoc = apiBaseUrl + "/lbs/rgeoc";
	private String lbsGeoc = apiBaseUrl + "/lbs/geoc";

	// private String lbsRGeoc = lbsBaseUrl + "rgeoc";
	// private String lbsGeoc = lbsBaseUrl + "geoc";

	public LBSAPI(String OAuthVersion) {
		super(OAuthVersion);
	}

	public LBSAPI(String OAuthVersion, QHttpClient qHttpClient) {
		super(OAuthVersion, qHttpClient);
	}

	/**
	 * 更新地理位置
	 * 
	 * @param oAuth
	 * @param format
	 *            返回的数据是 (json 或 xml)
	 * @param longitude
	 *            经度
	 * @param latitude
	 *            纬度
	 * @return
	 * @throws Exception
	 * 
	 * @see <a
	 *      href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/LBS%E7%9B%B8%E5%85%B3/%E6%9B%B4%E6%96%B0%E5%9C%B0%E7%90%86%E4%BD%8D%E7%BD%AE">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String updatePosition(OAuth oAuth, String format, String longitude,
			String latitude) throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("longitude", longitude));
		paramsList.add(new BasicNameValuePair("latitude", latitude));

		return requestAPI.postContent(lbsUpdatePos, paramsList, oAuth);
	}

	/**
	 * 删除最后更新位置
	 * 
	 * @param oAuth
	 * @param format
	 *            返回的数据是 (json 或 xml)
	 * @return
	 * @throws Exception
	 * 
	 * @see <a
	 *      href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/LBS%E7%9B%B8%E5%85%B3/%E5%88%A0%E9%99%A4%E6%9C%80%E5%90%8E%E6%9B%B4%E6%96%B0%E4%BD%8D%E7%BD%AE">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String delPosition(OAuth oAuth, String format) throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));

		return requestAPI.postContent(lbsDelPos, paramsList, oAuth);
	}

	/**
	 * 获取POI (point of interest)
	 * 
	 * @param oAuth
	 * @param format
	 *            返回的数据是 (json 或 xml)
	 * @param longitude
	 *            经度
	 * @param latitude
	 *            纬度
	 * @param reqnum
	 *            每次请求记录的条数 (1-25条)
	 * @param radius
	 *            POI的半径(单位为米),取值范围为100-1000,为达到比较好的搜索结果,建议设置为200
	 * @param position
	 *            上次查询返回的位置,用于翻页(第一次请求时填0)
	 * @return
	 * @throws Exception
	 * 
	 * @see <a
	 *      href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/LBS%E7%9B%B8%E5%85%B3/%E8%8E%B7%E5%8F%96POI(Point_of_Interest)">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String getPOI(OAuth oAuth, String format, String longitude,
			String latitude, String reqnum, String radius, String position)
			throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("longitude", longitude));
		paramsList.add(new BasicNameValuePair("latitude", latitude));
		paramsList.add(new BasicNameValuePair("reqnum", reqnum));
		paramsList.add(new BasicNameValuePair("radius", radius));
		paramsList.add(new BasicNameValuePair("position", position));

		return requestAPI.postContent(lbsGetPOI, paramsList, oAuth);
	}

	/**
	 * 获取身边最新的微博
	 * 
	 * @param oAuth
	 * @param format
	 *            返回的数据是 (json 或 xml)
	 * @param longitude
	 *            经度
	 * @param latitude
	 *            纬度
	 * @param pageinfo
	 *            翻页参数,由上次请求返回(第一次请求时请填空)
	 * @param pagesize
	 *            请求的每页个数(1-50),建议25
	 * @return
	 * @throws Exception
	 * 
	 * @see <a
	 *      href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/LBS%E7%9B%B8%E5%85%B3/%E8%8E%B7%E5%8F%96%E8%BA%AB%E8%BE%B9%E6%9C%80%E6%96%B0%E7%9A%84%E5%BE%AE%E5%8D%9A">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String getAroundNew(OAuth oAuth, String format, String longitude,
			String latitude, String pageinfo, String pagesize) throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("longitude", longitude));
		paramsList.add(new BasicNameValuePair("latitude", latitude));
		paramsList.add(new BasicNameValuePair("pageinfo", pageinfo));
		paramsList.add(new BasicNameValuePair("pagesize", pagesize));

		return requestAPI.postContent(lbsGetAroundNew, paramsList, oAuth);
	}

	/**
	 * 获取身边的人
	 * 
	 * @param oAuth
	 * @param format
	 *            返回的数据是 (json 或 xml)
	 * @param longitude
	 *            经度
	 * @param latitude
	 *            纬度
	 * @param pageinfo
	 *            翻页参数,由上次请求返回(第一次请求时请填空)
	 * @param pagesize
	 *            请求的每页个数(1-25个)
	 * @param gender
	 *            性别, 0x0全部, 0x1男, 0x2女
	 * @return
	 * @throws Exception
	 * 
	 * @see <a
	 *      href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/LBS%E7%9B%B8%E5%85%B3/%E8%8E%B7%E5%8F%96%E8%BA%AB%E8%BE%B9%E7%9A%84%E4%BA%BA">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String getAroundPeople(OAuth oAuth, String format, String longitude,
			String latitude, String pageinfo, String pagesize, String gender)
			throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("longitude", longitude));
		paramsList.add(new BasicNameValuePair("latitude", latitude));
		paramsList.add(new BasicNameValuePair("pageinfo", pageinfo));
		paramsList.add(new BasicNameValuePair("pagesize", pagesize));
		paramsList.add(new BasicNameValuePair("gender", gender));

		return requestAPI.postContent(lbsGetAroundPeople, paramsList, oAuth);
	}

	/**
	 * 通过经纬度获取地理位置
	 * 
	 * @param oAuth
	 * @param lnglat
	 *            经纬度值,采用经度在前,纬度在后,经纬度之间用","隔开的方式
	 * @param reqsrc
	 *            请求来源,请填写"wb"
	 * @return
	 * @throws Exception
	 * 
	 * @see <a
	 *      href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/LBS%E7%9B%B8%E5%85%B3/%E9%80%9A%E8%BF%87%E7%BB%8F%E7%BA%AC%E5%BA%A6%E8%8E%B7%E5%8F%96%E5%9C%B0%E7%90%86%E4%BD%8D%E7%BD%AE">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String rGeoc(OAuth oAuth, String lnglat, String reqsrc)
			throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("lnglat", lnglat));
		paramsList.add(new BasicNameValuePair("reqsrc", reqsrc));

		return requestAPI.getResource(lbsRGeoc, paramsList, oAuth);
	}

	/**
	 * 通过地理位置获取经纬度
	 * 
	 * @param oAuth
	 * @param addr
	 *            地理位置
	 * @param reqsrc
	 *            请求来源,请填写"wb"
	 * @return
	 * @throws Exception
	 * 
	 * @see <a
	 *      href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/LBS%E7%9B%B8%E5%85%B3/%E9%80%9A%E8%BF%87%E5%9C%B0%E7%90%86%E4%BD%8D%E7%BD%AE%E8%8E%B7%E5%8F%96%E7%BB%8F%E7%BA%AC%E5%BA%A6">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String geoc(OAuth oAuth, String addr, String reqsrc)
			throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("addr", addr));
		paramsList.add(new BasicNameValuePair("reqsrc", reqsrc));

		return requestAPI.getResource(lbsGeoc, paramsList, oAuth);
	}

	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;

		lbsUpdatePos = apiBaseUrl + "/lbs/update_pos";
		lbsDelPos = apiBaseUrl + "/lbs/del_pos";
		lbsGetPOI = apiBaseUrl + "/lbs/get_poi";
		lbsGetAroundNew = apiBaseUrl + "/lbs/get_around_new";
		lbsGetAroundPeople = apiBaseUrl + "/lbs/get_around_people";
		lbsRGeoc = apiBaseUrl + "/lbs/rgeoc";
		lbsGeoc = apiBaseUrl + "/lbs/geoc";
	}

}
