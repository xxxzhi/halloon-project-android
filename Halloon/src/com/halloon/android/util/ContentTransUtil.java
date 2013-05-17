package com.halloon.android.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.widget.TextView;

public class ContentTransUtil {
	private Context context;
	private static ContentTransUtil instance;

	public ContentTransUtil(Context context) {
		this.context = context;
	}

	public static ContentTransUtil getInstance(Context context) {
		if (instance == null) {
			instance = new ContentTransUtil(context);
		}
		return instance;
	}

	public void displaySpannableString(String content, TextView tv, JSONObject user) {
		tv.setText("");
		if (content.contains("/") || content.contains("@") || content.contains("<a href")) {
			SpannableString sString;
			String replacedContent = content;
			if (user != null && user.optString("null") != "null") {
				JSONArray userContainer = user.names();
				for (int i = 0; i < user.length(); i++) {
					try {
						replacedContent = replacedContent.replaceAll("@" + userContainer.getString(i), "<font color='0x0085DF'>@" + user.getString(userContainer.getString(i)) + "</font>");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			sString = new SpannableString(Html.fromHtml(replacedContent));

			String regex = "\\/";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(sString);
			int textWidth = ((int) tv.getTextSize()) + 4;
			while (matcher.find()) {
				int length = EmojiContainer.emoNameContainer.length;
				for (int i = 0; i < length; i++) {
					String temp_string;
					if (matcher.start() + EmojiContainer.emoNameContainer[i].length() <= sString.length() - 1) {
						temp_string = sString.toString().substring(matcher.start(), matcher.start() + EmojiContainer.emoNameContainer[i].length() + 1);
					} else {
						temp_string = sString.toString().substring(matcher.start(), sString.length() - 1);
					}
					if (temp_string.contains(EmojiContainer.emoNameContainer[i])) {

						int id = EmojiContainer.getEmojiId(context, i);

						Drawable drawable = context.getResources().getDrawable(id);
						drawable.setBounds(0, 0, textWidth, textWidth);
						sString.setSpan(new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE), matcher.start(), matcher.start() + EmojiContainer.emoNameContainer[i].length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
						break;
					}
				}
			}
			tv.setText(sString);
		} else {
			tv.setText(content);
		}
	}

	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
	
	public void copyToClipBoard(String content){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			android.content.ClipboardManager cbManagerForApi11 = (android.content.ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
			ClipData clipData = ClipData.newPlainText("tweet_content", content);
			cbManagerForApi11.setPrimaryClip(clipData);
		} else {
			android.text.ClipboardManager cbManager = (android.text.ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
			cbManager.setText(content);
		}
	}

}
