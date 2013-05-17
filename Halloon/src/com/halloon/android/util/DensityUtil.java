package com.halloon.android.util;

import android.content.Context;

import com.halloon.android.data.SettingsManager;

public class DensityUtil {
	public static int dip2px(Context context, float dipValue) {
		final float scale = SettingsManager.getInstance(context)
				.getScreenDensity();

		return (int) (dipValue * scale + 0.5F);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = SettingsManager.getInstance(context)
				.getScreenDensity();

		return (int) (pxValue / scale + 0.5F);
	}

}
