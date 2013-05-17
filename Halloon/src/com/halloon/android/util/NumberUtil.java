package com.halloon.android.util;

import android.content.res.Resources;

import com.halloon.android.R;

public class NumberUtil {

	public static String shortenNumericString(String number) {
		Resources resource = Resources.getSystem();
		if (number.length() >= 5 && number.length() < 9) {
			return number.substring(0, number.length() - 4) + resource.getString(R.string.ten_thousands);
		} else if (number.length() >= 9) {
			return number.substring(0, number.length() - 8) + resource.getString(R.string.one_hundred_million);
		}
		
		return number;
	}

	public static String formatBytesSize(long bytes) {
		Resources resource = Resources.getSystem();
		String size = "";
		if (bytes > 0 && bytes < 1024) {
			size = bytes + "B";
		} else if (bytes > 1024 && bytes < 1024 * 1024) {
			bytes /= 1024;
			size = bytes + "KB";
		} else if (bytes > 1024 * 1024 && bytes < 1024 * 1024 * 1024) {
			bytes /= (1024 * 1024);
			size = bytes + "MB";
		} else if (bytes > 1024 * 1024 * 1024 && bytes < 1024 * 1024 * 1024 * 1024) {
			bytes /= (1024 * 1024 * 1024);
			size = bytes + "GB";
		} else {
			size = resource.getString(R.string.file_size_out_of_range);
		}

		//System.out.println(bytes + ":" + size);

		return size;
	}
}
