package com.halloon.android.api;

import org.apache.http.message.BasicNameValuePair;

import com.tencent.weibo.api.BasicAPI;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.QArrayList;
import com.tencent.weibo.utils.QHttpClient;

public class OtherAPI extends BasicAPI{
	private String getEmotions = apiBaseUrl + "/other/get_emotions";
	private String getTopReAdd = apiBaseUrl + "/other/gettopreadd";
	private String qualityTransConv = apiBaseUrl + "/other/quality_trans_conv";
	private String urlConverge = apiBaseUrl + "/other/url_converge";
	private String vipTransConv = apiBaseUrl + "/other/vip_trans_conv";
	private String followerTransConv = apiBaseUrl + "/other/follower_trans_conv";
	private String getTopiczBrank = apiBaseUrl + "/other/gettopiczbrank";
	private String kownPerson = apiBaseUrl + "/other/kownperson";
	private String updateCount = apiBaseUrl + "/info/update";
	
	public OtherAPI(String OAuthVersion){
		super(OAuthVersion);
	}
	
	public OtherAPI(String OAuthVersion, QHttpClient qHttpClient){
		super(OAuthVersion, qHttpClient);
	}

	/**
	 * 本接口用于获取微博基本表情的简单信息， 比如:表情的名称，表图片对应的连接
	 * @param oAuth
	 * @param format 返回数据的格式(json或xml)
	 * @param type  支持的表情类型(0-17)
	 * @return
	 * @throws Exception
	 * 
	 * @see <a href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E5%85%B6%E4%BB%96/%E8%8E%B7%E5%8F%96%E8%A1%A8%E6%83%85%E6%8E%A5%E5%8F%A3">微博开放平台上关于此条API的文档</a>
	 */
	public String getEmotions(OAuth oAuth, String format, String type) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("type", type));
		
		return requestAPI.getResource(getEmotions, paramsList, oAuth);
	}
	
	/**
	 * 本接口用于获取一键转播组件转播次数靠前的微博消息列表，比如：一键转播次数最多的微博消息id，微博消息被一键转播的次数等
	 * @param oAuth
	 * @param format 返回数据的格式(json 或 xml)
	 * @param reqnum 请求数(最大300)
	 * @param type 所请求的热门转播排行的类型， 其值及其说明如下：
	 * type=5， 表示该请求用于查询同一网站的一键转播热门排行，此时get请求中需要提供查询网站的sourceid的参数值。 type=6， 表示该请求用于查询同一地区内的一键转播人们排行，此时get请求中需要提供country，province和city的参数值。
	 * @param sourceid 网站的来源id，type=5时使用， 需联系BD申请
	 * @param country 国家码（type=6时使用）
	 * @param province 省份码（type=6时使用）
	 * @param city 城市码（type=6时使用）
	 * @return
	 * @throws Exception
	 * 
	 * @see <a href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E5%85%B6%E4%BB%96/%E4%B8%80%E9%94%AE%E8%BD%AC%E6%92%AD%E7%83%AD%E9%97%A8%E6%8E%92%E8%A1%8C">微博开放平台上关于此条API的文档</a>
	 */
	public String getTopReList(OAuth oAuth, String format, String reqnum, String type, String sourceid, String country, String province, String city) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("reqnum", reqnum));
		paramsList.add(new BasicNameValuePair("type", type));
		paramsList.add(new BasicNameValuePair("sourceid", sourceid));
		paramsList.add(new BasicNameValuePair("country", country));
		paramsList.add(new BasicNameValuePair("province", province));
		paramsList.add(new BasicNameValuePair("city", city));
		
		return requestAPI.getResource(getTopReAdd, paramsList, oAuth);
	}
	
	/**
	 * 本机口用于获取根据微博消息的根节点ID获取精华转播微博消息列表， 比如：微博消息的发表内容， 发表时间， 发表人等
	 * @param oAuth
	 * @param format 返回数据的格式(json 或xml)
	 * @param rootid 转发或者回复的微博的根节点ID
	 * @param offset 起始偏移量， 分页用
	 * @param reqnum 用户请求的微博消息数目
	 * @return
	 * @throws Exception
	 * 
	 * @see <a href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E5%85%B6%E4%BB%96/%E6%8B%89%E5%8F%96%E7%B2%BE%E5%8D%8E%E8%BD%AC%E6%92%AD%E6%B6%88%E6%81%AF%E6%8E%A5%E5%8F%A3">微博开放平台上关于此条API的文档</a>
	 */
	public String getQualityReList(OAuth oAuth, String format, String rootid, String offset, String reqnum) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("rootid", rootid));
		paramsList.add(new BasicNameValuePair("offset", offset));
		paramsList.add(new BasicNameValuePair("reqnum", reqnum));
		
		return requestAPI.getResource(qualityTransConv, paramsList, oAuth);
	}
	
	/**
	 * 本接口用于根据微博长连接获取短连接相关信息， 比如:转换后获得的短URL， URL类型
	 * @param oAuth
	 * @param format 返回数据的格式(json或xml)
	 * @param url 用户提供的需要进行聚合查询的url字符串后缀， 目前只支持短url后缀
	 * @param pageflag 翻页标识 0：第一页  1向下翻页 2向上翻页
	 * @param pageTime 本页起始时间 和pageflag一起使用 精确定位翻页点 若不需要精确定位 只需给出pageTime
	 * @param tweetid 与pageflag和pagetime一起 用于翻页
	 * 第一页：填0  向上翻页：填上一次请求返回的第一条记录id 向下翻页：填上一次请求返回的最后一条记录id
	 * @param type 查询条件 0x01原创发表  0x02表示转载 0x40 点评类型
	 * @param words 字数限制， 非0返回微博内容字数大于或等于该值的微博信息  0  无须字数限制
	 * @param flag  按位使用 0x01-VIP查询  0x02-非VIP查询
	 * @param reqnum 请求的个数(最大50个)
	 * @param detaillevel 详细程度(1,2,4) 不同级别返回的微博相关信息包括：
	 * 1.tweetinfo = { tweetid + time} 
	 * 2.tweetinfo = { tweetid + time + contenttype + type + name + nick + openid}
	 * 3.tweetinfo = { tweetid + time + contenttype + type + name + nick + openid + rootid + parentid}
	 * 
	 * 如果用户不指定detaillevel的值， 则服务器默认返回第一种类型的微博信息
	 * @param referer  引用
	 * @return
	 * @throws Exception
	 * 
	 * @see <a href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E5%85%B6%E4%BB%96/%E7%9F%ADurl%E8%81%9A%E5%90%88">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String convertToShortUrl(OAuth oAuth, String format, String url, String pageflag, String pageTime, String tweetid, String type, String words, String flag, String reqnum, String detaillevel, String referer) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("url", url));
		paramsList.add(new BasicNameValuePair("pageflag", pageflag));
		paramsList.add(new BasicNameValuePair("pageTime", pageTime));
		paramsList.add(new BasicNameValuePair("tweetid", tweetid));
		paramsList.add(new BasicNameValuePair("type", type));
		paramsList.add(new BasicNameValuePair("words", words));
		paramsList.add(new BasicNameValuePair("flag", flag));
		paramsList.add(new BasicNameValuePair("reqnum", reqnum));
		paramsList.add(new BasicNameValuePair("detaillevel", detaillevel));
		paramsList.add(new BasicNameValuePair("referer", referer));
		
		return requestAPI.getResource(urlConverge, paramsList, oAuth);
	}
	
	/**
	 * 拉取vip用户的转播消息接口
	 * @param oAuth
	 * @param format 返回数据的格式(json或xml)
	 * @param rootid 转发或者回复的微博根节点ID
	 * @param pageflag 翻页标识  0第一页  1向下翻页  2向上翻页
	 * @param pagetime 本页起始时间  和pageflag一起使用  精确定位翻页点 如不需要精确定位 只需给出pagetime
	 * 
	 * 第一页 填0  向上翻页：填上一次请求返回的第一条记录时间  向下翻页：填上一次请求返回的最后一条记录时间
	 * @param tweetid 与pageflag 和pagetime 一起使用  用于翻页
	 * 
	 * 第一页：填0 向上翻页：填上一次请求返回的第一条记录ID  向下翻：填上一次请求返回的最后一条记录ID
	 * @param reqnum  用户请求的微博消息数目（最多50个）
	 * @return
	 * @throws Exception
	 * 
	 * @see <a href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E5%85%B6%E4%BB%96/%E6%8B%89%E5%8F%96vip%E7%94%A8%E6%88%B7%E7%9A%84%E8%BD%AC%E6%92%AD%E6%B6%88%E6%81%AF%E6%8E%A5%E5%8F%A3">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String getVipReList(OAuth oAuth, String format, String rootid, String pageflag, String pagetime, String tweetid, String reqnum) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("rootid", rootid));
		paramsList.add(new BasicNameValuePair("pageflag", pageflag));
		paramsList.add(new BasicNameValuePair("pagetime", pagetime));
		paramsList.add(new BasicNameValuePair("tweetid", tweetid));
		paramsList.add(new BasicNameValuePair("reqnum", reqnum));
		
		return requestAPI.getResource(vipTransConv, paramsList, oAuth);
	}
	
	/**
	 * 拉取我收听的用户的转播消息接口
	 * @param oAuth
	 * @param format 返回数据的格式（json或xml）
	 * @param rootid 转发或者回复的微博根节点ID
	 * @param pageflag 翻页标识  0 第一页 1向下翻页 2 向上翻页
	 * @param pagetime 本页起始时间 和pageflag一起使用  精确定位翻页点 若不需要精确定位  只需给出pagetime
	 * 第一页 填0  向上翻页 填上一次请求返回的第一条记录时间  向下翻页 填上一次请求返回的最后一条记录时间
	 * @param tweetid 与pageflag和pagetime一起使用 用于翻页
	 * 第一页 填0   向上翻页 填上一次请求返回的第一条记录id 向下翻页 填上一次请求返回的最后一条记录id
	 * @param reqnum 用户请求的微博消息数目（最多50个）
	 * @return
	 * @throws Exception
	 * 
	 * @see <a href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E5%85%B6%E4%BB%96/%E6%8B%89%E5%8F%96%E6%88%91%E6%94%B6%E5%90%AC%E7%9A%84%E7%94%A8%E6%88%B7%E7%9A%84%E8%BD%AC%E6%92%AD%E6%B6%88%E6%81%AF%E6%8E%A5%E5%8F%A3">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String getFollowerReList(OAuth oAuth, String format, String rootid, String pageflag, String pagetime, String tweetid, String reqnum) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("rootid", rootid));
		paramsList.add(new BasicNameValuePair("pageflag", pageflag));
		paramsList.add(new BasicNameValuePair("pagetime", pagetime));
		paramsList.add(new BasicNameValuePair("tweetid", tweetid));
		paramsList.add(new BasicNameValuePair("reqnum", reqnum));
		
		return requestAPI.getResource(followerTransConv, paramsList, oAuth);
	}
	
	/**
	 * 本接口用于获取相同话题下转播次数靠前的微博消息列表  比如：该话题下被转播次数靠前的原创消息id 该原创消息的作者微博帐号等
	 * @param oAuth
	 * @param format 返回数据的格式（json或xml）
	 * @param checktype 值为1时表示查询“全时段” 排行榜，2表示查询“单周内”排行榜， 3表示查询“单月内” 排行榜
	 * @param topictype 用户通过何种方式指定要查询的话题
	 * 
	 * 0 表示按照ID查询 通过topicid参数指定要查询的话题id  1 表示按照话题标题进行查询  topicname参数指定要查询的话题的标题
	 * 
	 * </br>要使用该接口  需先向微博开放平台提供要拉取的话题ID的话题名称，申请对该话题进行配置，联系企业QQ：800013811或直接联系BD
	 * @param topicid 要查询的话题ID
	 * @param topicname 要查询的话题的标题
	 * @param reqnum 用户请求的微博消息数目（最多100个）
	 * @return
	 * @throws Exception
	 * 
	 * @see <a href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E5%85%B6%E4%BB%96/%E5%90%8C%E8%AF%9D%E9%A2%98%E7%83%AD%E9%97%A8%E8%BD%AC%E6%92%AD%E6%8E%92%E8%A1%8C">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String getTopicBrank(OAuth oAuth, String format, String checktype, String topictype, String topicid, String topicname, String reqnum) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("checktype", checktype));
		paramsList.add(new BasicNameValuePair("topictype", topictype));
		paramsList.add(new BasicNameValuePair("topicid", topicid));
		paramsList.add(new BasicNameValuePair("topicname", topicname));
		paramsList.add(new BasicNameValuePair("reqnum", reqnum));
		
		return requestAPI.getResource(getTopiczBrank, paramsList, oAuth);
	}
	
	/**
	 * 本接口用于获取我可能认识的人的微博帐号信息的列表  比：帐号名  用户唯一ID 昵称等
	 * @param oAuth
	 * @param format 返回的数据类型(json或xml)
	 * @param reqnum 请求格式（1-200）， 默认30
	 * @param startindex 起始位置（第一页填0 继续向下翻页： 填【reqnum*(page - 1)])
	 * @return
	 * @throws Exception
	 * 
	 * @see <a href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E5%85%B6%E4%BB%96/%E6%88%91%E5%8F%AF%E8%83%BD%E8%AE%A4%E8%AF%86%E7%9A%84%E4%BA%BA">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String getProbablyKnowPersonList(OAuth oAuth, String format, String reqnum, String startindex) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("reqnum", reqnum));
		paramsList.add(new BasicNameValuePair("startindex", startindex));
		
		return requestAPI.getResource(kownPerson, paramsList, oAuth);
	}
	
	/**
	 * 本接口用于获取微博帐号相关未读的消息数量  譬如：首页未读消息计数， 死心页未读消息计数， 新增听众数等
	 * @param oAuth
	 * @param format 返回数据的类型（json 或xml）
	 * @param op 请求的方式
	 * 0：仅获取数据更新的条数
	 * 1.后去完毕后将相应的技术清零
	 * @param type 需获取的更新数据的类型
	 * 5：首页未读消息计数
	 * 6.@页未读消息计数
	 * 7.私信页未读消息计数
	 * 8.新增听众数
	 * 9。首页新增的原创广播数
	 * 
	 * op=0时不输入type  返回所有类型计数
	 * op=1时需输入type 返回所有类型计数 同时清除该type类型的技术
	 * @return
	 * @throws Exception
	 * 
	 * @see <a href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E6%95%B0%E6%8D%AE%E6%9B%B4%E6%96%B0%E6%8E%A5%E5%8F%A3/%E6%9F%A5%E7%9C%8B%E6%95%B0%E6%8D%AE%E6%9B%B4%E6%96%B0%E6%9D%A1%E6%95%B0">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String getUpdateCount(OAuth oAuth, String format, String op, String type) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("format", format));
		paramsList.add(new BasicNameValuePair("op", op));
		paramsList.add(new BasicNameValuePair("type", type));
		
		return requestAPI.getResource(updateCount, paramsList, oAuth);
	}
	
	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
		
		getEmotions = apiBaseUrl + "/other/get_emotions";
		getTopReAdd = apiBaseUrl + "/other/gettopreadd";
		qualityTransConv = apiBaseUrl + "/other/quality_trans_conv";
		urlConverge = apiBaseUrl + "/other/url_converge";
		vipTransConv = apiBaseUrl + "/other/vip_trans_conv";
		followerTransConv = apiBaseUrl + "/other/follower_trans_conv";
		getTopiczBrank = apiBaseUrl + "/other/gettopiczbrank";
		kownPerson = apiBaseUrl + "/other/kownperson";
		updateCount = apiBaseUrl + "/info/update";
	}
}
