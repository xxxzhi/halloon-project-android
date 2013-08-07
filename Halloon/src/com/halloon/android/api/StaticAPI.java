package com.halloon.android.api;

public class StaticAPI {

	/**
	 * 获取谷歌静态地图
	 * 
	 * @param latitude
	 *            纬度
	 * @param longitude
	 *            经度
	 * @param zoom
	 *            放大倍数
	 * @param width
	 *            宽度（像素）
	 * @param height
	 *            高度（像素）
	 * @param markerColor
	 *            标签颜色
	 * @param mapType
	 *            地图类型（roadmap 标准路线图,satellite卫星地图,hybrid 综合地图,terrain 地形图)
	 * @return
	 */
	public static String getGoogleStaticMap(String latitude, String longitude, int zoom, int width, int height, int markerColor, String mapType) {
		return "http://maps.googleapis.com/maps/api/staticmap?center="
				+ latitude + "," + longitude + "&zoom=" + String.valueOf(zoom)
				+ "&size=" + String.valueOf(width) + "x"
				+ String.valueOf(height) + "&markers=color:"
				+ String.valueOf(markerColor) + "%7C" + latitude + ","
				+ longitude + "&language=cn&maptype=" + mapType
				+ "&sensor=false";

	}

	/**
	 * 获取SOSO静态地图
	 * 
	 * @param latitude
	 *            纬度
	 * @param longitude
	 *            经度
	 * @param zoom
	 *            放大倍数
	 * @param width
	 *            宽度（像素）
	 * @param height
	 *            高度（像素）
	 * @return
	 */
	public static String getSOSOStaticMap(String latitude, String longitude, int zoom, int width, int height) {
		return "http://st.map.soso.com/api?size=" + String.valueOf(width) + "*"
				+ String.valueOf(height) + "&center=" + longitude + ","
				+ latitude + "&zoom=" + String.valueOf(zoom) + "&markers="
				+ longitude + "," + latitude;
	}

	/**
	 * 获取百度静态地图
	 * 
	 * @param latitude
	 *            纬度
	 * @param longitude
	 *            经度
	 * @param zoom
	 *            放大倍数
	 * @param width
	 *            宽度（像素）
	 * @param height
	 *            高度（像素）
	 * @return
	 */
	public static String getBaiduStaticMap(String latitude, String longitude, int zoom, int width, int height) {
		return "http://api.map.baidu.com/staticimage?center=" + longitude + ","
				+ latitude + "&width=" + String.valueOf(width) + "&height="
				+ String.valueOf(height) + "&zoom=" + String.valueOf(zoom)
				+ "&markers=" + longitude + "," + latitude
				+ "&markerStyles=l,O";
	}

	/**
	 * 生成二维码（谷歌）
	 * 
	 * @param width
	 *            宽度
	 * @param height
	 *            长度
	 * @param content
	 *            内容
	 * @param encodeType
	 *            编码类型 （默认为UTF-8)
	 * @param chld
	 *            容错 分为L(7%)M(15%)Q(25%)H(30%)
	 * @return
	 */
	public static String generateQRCodeFromGoogle(int width, int height, String content, String encodeType, String chld) {
		if (content == null)
			content = "UTF-8";

		return "http://chart.apis.google.com/chart?cht=qr&chs="
				+ String.valueOf(width) + "x" + String.valueOf(height)
				+ "&chl=" + content + "&choe=" + encodeType + "&chld=" + chld;
	}
}
