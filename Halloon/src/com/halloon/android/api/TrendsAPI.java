package com.halloon.android.api;

import org.apache.http.message.BasicNameValuePair;

import com.tencent.weibo.api.BasicAPI;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.QArrayList;
import com.tencent.weibo.utils.QHttpClient;

public class TrendsAPI extends BasicAPI {

	private String famousList = apiBaseUrl + "/trends/famouslist";
	private String hotList = apiBaseUrl + "/trends/ht";
	private String reHotList = apiBaseUrl + "/trends/t";
	
	public TrendsAPI(String OAuthVersion){
		super(OAuthVersion);
	}
	
	public TrendsAPI(String OAuthVersion, QHttpClient qHttpClient){
		super(OAuthVersion, qHttpClient);
	}
	
	/**
	 * 本接口用于根据请求名人的类别id获取当前类别的推荐名人列表信息，  譬如： 推荐名人的用户帐号， 昵称。用户介绍等
	 * @param oAuth
	 * @param format 返回的数据格式(json 或 xml)
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
	public String getFamousList(OAuth oAuth, String format, String classid, String subclassid) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("classid", classid));
		paramsList.add(new BasicNameValuePair("subclassid", subclassid));
		
		return requestAPI.getResource(famousList, paramsList, oAuth);
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
	public String getHotList(OAuth oAuth, String format, String reqnum, String pos) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("reqnum", reqnum));
		paramsList.add(new BasicNameValuePair("pos", pos));
		
		return requestAPI.getResource(hotList, paramsList, oAuth);
	}

	/**
	 * 本接口用于获取转播次数开前的微博消息列表， 譬如:微博内容， 微博ID等
	 * @param oAuth
	 * @param format
	 * @param reqnum 每次请求记录的条数（1-100条）
	 * @param pos 翻页标识
	 * @param type 微博消息类型
	 * 0x1 戴文本 0x2带链接 0x4带图片 0x8 带视频
	 * 如需拉取多个类型请使用| 如(0x1|0x2) 得到3， 此时type = 3 即可， 填零便是拉取所有类型
	 * @return
	 * @throws Exception
	 * 
	 * @see <a href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E7%83%AD%E5%BA%A6%EF%BC%8C%E8%B6%8B%E5%8A%BF/%E8%BD%AC%E6%92%AD%E7%83%AD%E6%A6%9C">微博开放平台上关于此条API的文档</a>
	 */
	public String getReHotList(OAuth oAuth, String format, String reqnum, String pos, String type) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("reqnum", reqnum));
		paramsList.add(new BasicNameValuePair("pos", pos));
		paramsList.add(new BasicNameValuePair("type", type));
		
		return requestAPI.getResource(reHotList, paramsList, oAuth);
	}
	
	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
		
		famousList = apiBaseUrl + "/trends/famouselist";
		hotList = apiBaseUrl + "/trends/ht";
		reHotList = apiBaseUrl + "/trends/t";
	}
}
