package com.halloon.android.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.halloon.android.HalloonApplication;
import com.halloon.android.R;
import com.halloon.android.bean.TweetBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.DBManager;
import com.halloon.android.data.SettingsManager;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.task.BaseCompatiableTask;
import com.halloon.android.ui.fragment.TabMainPageFragment.MainPageFragmentCallback;
import com.halloon.android.ui.fragment.TweetDetailFragment.TweetDetailFragmentCallback;
import com.halloon.android.util.Constants;
import com.halloon.android.util.ContentTransUtil;
import com.halloon.android.util.GifDecoder;
import com.halloon.android.util.TimeUtil;
import com.halloon.android.widget.ButtonStyleTextView;

public class TweetContentAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater layoutInflater;
	private ArrayList<TweetBean> tweetBeans;
	
	private HalloonApplication application;

	public TweetContentAdapter(Context context, ArrayList<TweetBean> tweetBeans) {
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.tweetBeans = tweetBeans;
		application = (HalloonApplication) context.getApplicationContext();
	}

	@Override
	public int getCount() {
		return tweetBeans.size();
	}

	@Override
	public TweetBean getItem(int position) {
		if (position < tweetBeans.size()) {
			return tweetBeans.get(position);
		} else {
			return new TweetBean();
		}
	}

	@Override
	public long getItemId(int position) {
		return Long.valueOf(tweetBeans.get(position).getId());
	}

	public void removeItem(int position) {
		tweetBeans.remove(position);
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		TweetViewHolder holder;
		final TweetBean tweetBean = getItem(position);

		if (tweetBean == null) {
			return null;
		}

		if (convertView == null) {
			holder = new TweetViewHolder();

			convertView = layoutInflater.inflate(R.layout.tweet_content, null);

			holder.headImage = (ImageView) convertView.findViewById(R.id.tweet_head);
			holder.title = (TextView) convertView.findViewById(R.id.tweet_title);
			holder.isVip = (ImageView) convertView.findViewById(R.id.is_vip);
			holder.timestamp = (TextView) convertView.findViewById(R.id.tweet_timestamp);
			holder.from = (TextView) convertView.findViewById(R.id.tweet_from);
			holder.commentCount = (TextView) convertView.findViewById(R.id.comment_count);
			holder.forwardCount = (TextView) convertView.findViewById(R.id.forward_count);
			holder.like = (TextView) convertView.findViewById(R.id.like_count);
			holder.tweetContent = (ButtonStyleTextView) convertView.findViewById(R.id.tweet_content);
			holder.tweetLocationText = (TextView) convertView.findViewById(R.id.tweet_location_text);
			holder.tweetImage = (ImageView) convertView.findViewById(R.id.tweet_image);
			holder.forwardContent = (ButtonStyleTextView) convertView.findViewById(R.id.forward_content);
			holder.forwardLocationText = (TextView) convertView.findViewById(R.id.forward_location_text);
			holder.forwardImage = (ImageView) convertView.findViewById(R.id.forward_image);
			holder.hasImage = (ImageView) convertView.findViewById(R.id.image_icon);
			
			holder.sourceLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout1);
			convertView.setTag(holder);
		} else {
			holder = (TweetViewHolder) convertView.getTag();
		}

		OnClickListener tweetListClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					switch (v.getId()) {
					case R.id.tweet_head:
						Bundle bundle = new Bundle();
						bundle.putString("name", getItem(position).getName());
						bundle.putString("id", getItem(position).getOpenId());
						((TweetDetailFragmentCallback) context).setupProfileFragment(bundle);
						break;
					case R.id.tweet_image:
						if (getItem(position).getTweetImage() != null && getItem(position).getTweetImage().length() > 0)
							((MainPageFragmentCallback) context).setupPictureDialog(tweetBean.getTweetImage().getString(0), "/2000", v.getDrawingCache());
						if (getItem(position).getVideoImage() != null) {
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							Uri uri = Uri.parse(getItem(position).getVideoUrl());
							intent.setData(uri);
							context.startActivity(intent);
						}
						break;
					case R.id.forward_image:
						if (tweetBean.getSource().getTweetImage() != null && tweetBean.getSource().getTweetImage().length() > 0)
							((MainPageFragmentCallback) context).setupPictureDialog(tweetBean.getSource().getTweetImage().getString(0), "/2000", v.getDrawingCache());
						if (tweetBean.getSource().getVideoImage() != null) {
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							Uri uri = Uri.parse(tweetBean.getSource().getVideoUrl());
							intent.setData(uri);
							context.startActivity(intent);
						}
						break;
					case R.id.like_count:
						
						new BaseCompatiableTask<Void, Void, Boolean>() {

							@Override
							protected  Boolean doInBackground(Void... arg0) {
								boolean res = false;
								try {
									if(!tweetBean.isLike()){
										res = ContentManager.getInstance(application).like(tweetBean.getId());
									}else{
										res = ContentManager.getInstance(application).like(tweetBean.getId());
									}
								} catch (Exception e) {
									e.printStackTrace();
									res = false;
								}
								return res;
							}

							@Override
							protected void onPostExecute( Boolean result) {
								super.onPostExecute(result);
								if(result){
									tweetBean.setLike(!tweetBean.isLike());
								}
								
								notifyDataSetChanged();
							}
						}.taskExecute();
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		holder.headImage.setOnClickListener(tweetListClickListener);
		holder.tweetImage.setOnClickListener(tweetListClickListener);
		holder.forwardImage.setOnClickListener(tweetListClickListener);
		holder.like.setOnClickListener(tweetListClickListener);

		if(tweetBean.isLike()){
			holder.like.setText("+1");
		}else{
			holder.like.setText("");
		}
		
		
		ImageLoader.getInstance(context).displayImage(tweetBean.getHead() + "/100", holder.headImage, 0, null);
		holder.title.setText(tweetBean.getNick());
		if (tweetBean.getIsVip() == 1) {
			holder.isVip.setVisibility(View.VISIBLE);
		} else {
			holder.isVip.setVisibility(View.GONE);
		}
		holder.timestamp.setText(TimeUtil.converTime(tweetBean.getTimestamp(), 1));
		holder.from.setText(context.getString(R.string.from) + tweetBean.getFrom());
		holder.commentCount.setText(tweetBean.getMCount());
		holder.forwardCount.setText(tweetBean.getCount());
		if (tweetBean.getGeo() != null && tweetBean.getGeo().length() > 0) {
			holder.tweetLocationText.setVisibility(View.VISIBLE);
			holder.tweetLocationText.setText(context.getString(R.string.i_am) + tweetBean.getGeo());
		} else {
			holder.tweetLocationText.setVisibility(View.GONE);
		}
		if (tweetBean.getSource() != null && tweetBean.getText().length() == 0) {
			holder.tweetContent.setText(context.getString(R.string.re_tweet));
		}else{
			ContentTransUtil.getInstance(context).displaySpannableString(tweetBean.getText(), holder.tweetContent, tweetBean, false, true);
		}
		
		if (tweetBean.getTweetImage() != null ) {
//			holder.hasImage.setVisibility(View.VISIBLE);
			if(application.getIsMainPageImageMode()){
				holder.tweetImage.setVisibility(View.VISIBLE);
				try {
					ImageLoader.getInstance(context).displayImage(tweetBean.getTweetImage().getString(0) + "/120", holder.tweetImage, 0, null);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				holder.tweetImage.setVisibility(View.GONE);
			}
		} else if (tweetBean.getVideoImage() != null) {
//			holder.hasImage.setVisibility(View.GONE);
			if(application.getIsMainPageImageMode()){
				holder.tweetImage.setVisibility(View.VISIBLE);
				ImageLoader.getInstance(context).displayImage(tweetBean.getVideoImage(), holder.tweetImage, 0, null);
			}else{
				holder.tweetImage.setVisibility(View.GONE);
			}
		} else {
			holder.tweetImage.setVisibility(View.GONE);
			holder.hasImage.setVisibility(View.GONE);
		}

		// 是否转发
		if (tweetBean.getSource() != null) {
			holder.sourceLayout.setVisibility(View.VISIBLE);
			String tmp_source_text = tweetBean.getSource().getText();
			if (tweetBean.getSource().getGeo() != null && tweetBean.getSource().getGeo().length() > 0) {
				holder.forwardLocationText.setVisibility(View.VISIBLE);
				holder.forwardLocationText.setText(context.getString(R.string.i_am) + tweetBean.getSource().getGeo());
			} else {
				holder.forwardLocationText.setVisibility(View.GONE);
			}
			ContentTransUtil.getInstance(context).displaySpannableString(tmp_source_text, holder.forwardContent, tweetBean, true, true);
			if (tweetBean.getSource().getTweetImage() != null && application.getIsMainPageImageMode()) {
				holder.forwardImage.setVisibility(View.VISIBLE);
				try {
					ImageLoader.getInstance(context).displayImage(tweetBean.getSource().getTweetImage().getString(0) + "/120", holder.forwardImage, 1, null);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (tweetBean.getSource().getVideoImage() != null && application.getIsMainPageImageMode()) {
				holder.forwardImage.setVisibility(View.VISIBLE);
				ImageLoader.getInstance(context).displayImage(tweetBean.getSource().getVideoImage(), holder.forwardImage, 1, null);
			} else {
				holder.forwardImage.setVisibility(View.GONE);
			}
		} else {
			holder.sourceLayout.setVisibility(View.GONE);
		}

		return convertView;
	}

	private static final class TweetViewHolder {
		
		ImageView headImage;
		TextView title;
		ImageView isVip;
		TextView timestamp;
		TextView from;
		TextView like;
		TextView commentCount;
		TextView forwardCount;
		ButtonStyleTextView tweetContent;
		TextView tweetLocationText;
		ImageView tweetImage;
		ButtonStyleTextView forwardContent;
		TextView forwardLocationText;
		ImageView forwardImage;
		ImageView hasImage;
		RelativeLayout sourceLayout;
	}

}
