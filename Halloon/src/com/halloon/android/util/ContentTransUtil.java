package com.halloon.android.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.halloon.android.R;
import com.halloon.android.bean.TweetBean;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;

@SuppressLint("ServiceCast")
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
	
	public void displaySpannableString(String content, TextView tv, TweetBean tweetBean, boolean isSource, boolean isLink){
		if(isSource) content = tweetBean.getSource().getNick() + ":" + content;
		
		SpannableString ss = new SpannableString(content);
		
		if(isSource){
			ss.setSpan(new ForegroundColorSpan(0xFF0085DF), 0, tweetBean.getSource().getNick().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		String MENTION_PATTERN = "@[\\w\\p{InCJKUnifiedIdeographs}-]{1,20}";
		String TOPIC_PATTERN = "#([^\\#|.]+)#";
		String ADDR_PATTERN = "http://url\\.cn/[a-zA-Z0-9]+";
		String EMOJI_PATTERN = "\\/[\\p{Alnum}\\p{InCJKUnifiedIdeographs}]{0,3}";
		
		tv.setText("");
		final int textWidth = (int) tv.getTextSize() + 4;
		
		Pattern pattern = Pattern.compile(MENTION_PATTERN + "|" + 
		                                  TOPIC_PATTERN + "|" + 
				                          ADDR_PATTERN + "|" + 
		                                  EMOJI_PATTERN);
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()){
			String group = matcher.group();
			if(group.startsWith("/")){
				final int length = EmojiContainer.emoNameContainer.length;
				int i = 0;
				do{
					String emojiName = EmojiContainer.emoNameContainer[i];
					if(group.contains(emojiName)){
						Drawable drawable = context.getResources().getDrawable(EmojiContainer.getEmojiId(context, i));
						drawable.setBounds(0, 0, textWidth, textWidth);
						if(group.length() == emojiName.length()){
							ss.setSpan(new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE), matcher.start(), matcher.end() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						}else{
							ss.setSpan(new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE), matcher.start(), matcher.start() + emojiName.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						break;
					}
				}while(++i < length);
			}else if(group.startsWith("@")){
				final String nick = group;
				/*
				if(context instanceof BaseMultiFragmentActivity){
					ss.setSpan(new ClickableSpan(){

						@Override
						public void onClick(View widget) {
							Bundle bundle = new Bundle();
							bundle.putString("name", nick);
						    ((BaseMultiFragmentActivity) context).setupProfileFragment(bundle);
						}
						
					}, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				 */
				ss.setSpan(new ForegroundColorSpan(0xFF0085DF), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
			}else if(group.startsWith("#")){
				ss.setSpan(new ForegroundColorSpan(0xFF0085DF), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}else if(group.startsWith("http")){
				if(isLink){
					final String link = group;
					ss.setSpan(new ClickableSpan(){
						@Override
						public void onClick(View widget){
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							Uri uri = Uri.parse(link);
							intent.setData(uri);
							context.startActivity(intent);
						}
					}, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				Drawable drawable = context.getResources().getDrawable(R.drawable.button_link);
				drawable.setBounds(0, 0, (int) (textWidth * 3.5), textWidth);
				ss.setSpan(new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		
		tv.setText(ss);
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
	
	@Deprecated
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
