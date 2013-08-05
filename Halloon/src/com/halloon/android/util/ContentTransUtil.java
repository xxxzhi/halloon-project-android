package com.halloon.android.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.halloon.android.HalloonApplication;
import com.halloon.android.R;
import com.halloon.android.bean.TweetBean;

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
		content = convert(content);
		if(isSource) content = tweetBean.getSource().getNick() + ":" + content;
		
		final SpannableString ss = new SpannableString(content);
		
		if(isSource){
			ss.setSpan(new ForegroundColorSpan(0xFF0085DF), 0, tweetBean.getSource().getNick().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		String MENTION_PATTERN = "@[\\w\\p{InCJKUnifiedIdeographs}-]{1,20}";
		String TOPIC_PATTERN = "#([^\\#|.]+)#";
		String SEARCH_PATTERN = "\\{([^\\#\\{\\}|.]+)\\}";
		String ADDR_PATTERN = "http://url\\.cn/[a-zA-Z0-9]+";
		String EMOJI_PATTERN = "\\/[\\p{InCJKUnifiedIdeographs}]{0,3}";//or\\/[\u4e00-\u9fa5]{0,3}
		
		tv.setText("");
		final int textWidth = (int) tv.getTextSize() + 4;
		
		Pattern pattern = Pattern.compile(MENTION_PATTERN + "|" + 
		                                  TOPIC_PATTERN + "|" + 
				                          SEARCH_PATTERN + "|" +
				                          EMOJI_PATTERN + "|" + 
		                                  ADDR_PATTERN);
		final Matcher matcher = pattern.matcher(content);
		while(matcher.find()){
			final String group = matcher.group();
			final int start = matcher.start();
			final int end = matcher.end();
			
			if(group.startsWith("/")){
				final int length = EmojiContainer.emoNameContainer.length;
				int i = 0;
				do{
					String emojiName = EmojiContainer.emoNameContainer[i];
					if(group.contains(emojiName)){
						Drawable drawable = context.getResources().getDrawable(EmojiContainer.getEmojiId(context, i));
						drawable.setBounds(0, 0, textWidth, textWidth);
						if(group.length() == emojiName.length() + 1){
							ss.setSpan(new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						}else{
							ss.setSpan(new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE), start, start + emojiName.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						break;
					}
				}while(++i < length);
			}else if(group.startsWith("@")){
				/*
				if(context instanceof BaseMultiFragmentActivity){
					ss.setSpan(new ClickableSpan(){

						@Override
						public void onClick(View widget) {
							Bundle bundle = new Bundle();
							bundle.putString("name", nick);
						    ((BaseMultiFragmentActivity) context).setupProfileFragment(bundle);
						}
						
					}, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				 */
				ss.setSpan(new ForegroundColorSpan(0xFF0085DF), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
			}else if(group.startsWith("#")){
				ss.setSpan(new ForegroundColorSpan(0xFF0085DF), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}else if(group.startsWith("{")){
				ss.setSpan(new ForegroundColorSpan(0xFF0085DF), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ss.setSpan(new ClickableSpan(){
					@Override
					public void onClick(View widget) {
						Toast.makeText(context, "clickSpan", Toast.LENGTH_LONG).show();
						ss.setSpan(new ForegroundColorSpan(0xFFFFFFFF), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					
					@Override
					public void updateDrawState(TextPaint tp){}
					
				}, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}else if(group.startsWith("http")){
				final String link = group;
				final String shortUrl = group.substring(group.lastIndexOf("/") + 1);
				if(isLink){
					tv.setMovementMethod(LinkMovementMethod.getInstance());
					
					ss.setSpan(new ClickableSpan(){
						@Override
						public void onClick(View widget){
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							Uri uri = Uri.parse(link);
							intent.setData(uri);
							context.startActivity(intent);
						}
					}, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				
				Drawable drawable = null;
				int id = R.drawable.button_link;
				HashMap<String, String> shortList = ((HalloonApplication) ((Activity) context).getApplication()).getShortList();
				if(shortList != null){
					String shortMatch = shortList.get(shortUrl);
					if(shortMatch != null){
						if(shortMatch.startsWith("http://music.qq.com/qqmusic.html?id")){
							id = R.drawable.button_music_link;
						}else if(shortMatch.startsWith("http://v.youku.com/") ||
								 shortMatch.startsWith("http://www.tudou.com/") || 
								 shortMatch.startsWith("http://v.qq.com") || 
								 shortMatch.startsWith("http://v.ku6.com") || 
								 shortMatch.startsWith("http://view.inews.qq.com")){
							id = R.drawable.button_video_link;
						}
					}
				}
				
				drawable = context.getResources().getDrawable(id);
				drawable.setBounds(0, 0, (int) (textWidth * 3.5), textWidth);
				ss.setSpan(new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
	
	private static String convert(String target){
		/*
		return target.replaceAll("\\&gt\\;", "＞")
				     .replaceAll("\\&lt\\;", "＜")
				     .replaceAll("\\&amp\\;", "＆")
				     .replaceAll("\\&apos\\;|\\&\\#39\\;", "＇")
				     .replaceAll("\\&quot\\;", "＂")
				     .replaceAll("\\&nbsp\\;", "　")
				     .replaceAll("\\&cent\\;", "¢")
				     .replaceAll("\\&pound\\;", "£")
				     .replaceAll("\\&yen\\;", "¥")
				     .replaceAll("\\&euro\\;", "€")
				     .replaceAll("\\&sect\\;", "§")
				     .replaceAll("\\&copy\\;", "©")
				     .replaceAll("\\&reg\\;", "®")
				     .replaceAll("\\&times\\;", "×")
				     .replaceAll("\\&divide\\;", "÷")
				     .replaceAll("\\&trade\\;", "™");
		 */
		
		return target.replaceAll("\\&gt\\;", ">")
			     .replaceAll("\\&lt\\;", "<")
			     .replaceAll("\\&amp\\;", "&")
			     .replaceAll("\\&apos\\;|\\&\\#39\\;", "'")
			     .replaceAll("\\&quot\\;", "＂")
			     .replaceAll("\\&nbsp\\;", " ")
			     .replaceAll("\\&cent\\;", "¢")
			     .replaceAll("\\&pound\\;", "£")
			     .replaceAll("\\&yen\\;", "¥")
			     .replaceAll("\\&euro\\;", "€")
			     .replaceAll("\\&sect\\;", "§")
			     .replaceAll("\\&copy\\;", "©")
			     .replaceAll("\\&reg\\;", "®")
			     .replaceAll("\\&times\\;", "×")
			     .replaceAll("\\&divide\\;", "÷")
			     .replaceAll("\\&trade\\;", "™");
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
