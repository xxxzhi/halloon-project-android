package com.halloon.android.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.format.Time;
import android.util.Log;

public class TimeUtil {

	/**
	 * 解析微博返回的timestamp数据
	 * 
	 * @param timestamp
	 *            微博返回的timestamp数据
	 * @param type
	 *            显示类型(0x0 全部以多少时间以前显示 ,0x1 一天以上显示正常时间,0x2 一个小时以上显示正常时间,0x3
	 *            一分钟以上显示正常时间,0x4 全部以正常时间显示)
	 * 
	 * @return String形式的数据
	 * 
	 */
	public static String converTime(String timestamp, int type) {
		long currentSeconds = System.currentTimeMillis() / 1000;
		long timestampN = 0;
		try {
			timestampN = Long.parseLong(timestamp);
		} catch (NumberFormatException e) {
			Log.d(Constants.LOG_TAG, "parseLong:" + timestampN + " error:" + e);
		}
		long timeGap = currentSeconds - timestampN;// 与现在时间相差秒数
		String timeStr = null;
		switch (type) {
		case 0:
			if (timeGap > 24 * 60 * 60) {// 1天以上
				timeStr = timeGap / (24 * 60 * 60) + "天前";
			} else if (timeGap > 60 * 60) {// 1小时-24小时
				timeStr = timeGap / (60 * 60) + "小时前";
			} else if (timeGap > 60) {// 1分钟-59分钟
				timeStr = timeGap / 60 + "分钟前";
			} else {// 1秒钟-59秒钟
				timeStr = "刚刚";
			}
			break;
		case 1:
			if (timeGap > 24 * 60 * 60) {// 1天以上
				timeStr = getStandardTime(timestamp);
			} else if (timeGap > 60 * 60) {// 1小时-24小时
				timeStr = timeGap / (60 * 60) + "小时前";
			} else if (timeGap > 60) {// 1分钟-59分钟
				timeStr = timeGap / 60 + "分钟前";
			} else {// 1秒钟-59秒钟
				timeStr = "刚刚";
			}
			break;
		case 2:
			if (timeGap > 60 * 60) {// 1小时以上
				timeStr = getStandardTime(timestamp);
			} else if (timeGap > 60) {// 1分钟-59分钟
				timeStr = timeGap / 60 + "分钟前";
			} else {// 1秒钟-59秒钟
				timeStr = "刚刚";
			}
			break;
		case 3:
			if (timeGap > 60) {// 1分钟以上
				timeStr = getStandardTime(timestamp);
			} else {// 1秒钟-59秒钟
				timeStr = "刚刚";
			}
			break;
		case 4:
			timeStr = getStandardTime(timestamp);
			break;
		}
		return timeStr;
	}

	public static String getStandardTime(String timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
		long timestampN = 0;
		try {
			timestampN = Long.parseLong(timestamp);
		} catch (NumberFormatException e) {
			Log.d(Constants.LOG_TAG, "parseLong:" + timestampN + " error:" + e);
		}
		Date date = new Date(timestampN * 1000);
		sdf.format(date);
		return sdf.format(date);
	}

	public static String getCurrentTime() {
		Time time = new Time();
		time.setToNow();

		return /* time.year + "-" + */(time.month + 1) + "-" + time.monthDay
				+ " " + time.hour + ":"
				+ (time.minute < 10 ? "0" + time.minute : time.minute) + ":"
				+ (time.second < 10 ? "0" + time.second : time.second);

	}

	public static String getCurrentTimeForFileName() {
		Time time = new Time();
		time.setToNow();

		return time.year + "-" + (time.month + 1) + "-" + time.monthDay + "-"
				+ time.hour + "-" + time.minute + "-" + time.second;
	}
}
