package com.halloon.android.adapter;

import java.util.ArrayList;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.halloon.android.R;
import com.halloon.android.api.StaticAPI;
import com.halloon.android.bean.TweetBean;
import com.halloon.android.data.DBManager;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.ui.fragment.TabMainPageFragment.MainPageFragmentCallback;
import com.halloon.android.ui.fragment.TabProfileFragment;
import com.halloon.android.ui.fragment.TweetDetailFragment.TweetDetailFragmentCallback;
import com.halloon.android.util.ContentTransUtil;
import com.halloon.android.util.DensityUtil;
import com.halloon.android.util.PopupWindowManager;
import com.halloon.android.util.TimeUtil;
import com.halloon.android.view.PinnedHeaderListView;
import com.halloon.android.view.PinnedHeaderListView.PinnedHeaderAdapter;

public class TweetDetailAdapter extends BaseAdapter implements OnClickListener {

	private Context context;
	private LayoutInflater layoutInflater;
	private TweetBean tweetBean;
	private ArrayList<TweetBean> commentList;
	private final int TWEET_HEAD = 0;
	private final int TWEET_CONTENT = 1;
	private final int TWEET_COMMENT_TITLE = 2;
	private final int TWEET_COMMENT = 3;
	private int commentState = 1;
	public static final int COMMENT_STATUS_COMMENT = 1;
	public static final int COMMENT_STATUS_FORWARD = 0;

	public TweetDetailAdapter(Context context, TweetBean tweetBean, ArrayList<TweetBean> commentList) {
		this.layoutInflater = LayoutInflater.from(context);
		this.context = context;
		this.tweetBean = tweetBean;
		this.commentList = commentList;
	}

	@Override
	public int getCount() {
		return commentList.size() + 3;
	}

	@Override
	public TweetBean getItem(int position) {
		if (position <= 2) {
			return tweetBean;
		} else {
			return commentList.get(position - 3);
		}
	}

	@Override
	public long getItemId(int position) {
		if (position <= 2) {
			return position;
		} else {
			return Long.valueOf(commentList.get(position - 3).getId());
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (position > 2) {
			return TWEET_COMMENT;
		} else {
			return position;
		}
	}

	public void setCommentState(int commentState) {
		this.commentState = commentState;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		TweetHeadHolder tweetHeadHolder = null;
		TweetContentHolder tweetContentHolder = null;
		TweetCommentTitleHolder tweetCommentTitleHolder = null;
		TweetCommentHolder tweetCommentHolder = null;

		int type = getItemViewType(position);

		switch (type) {
		case TWEET_HEAD:
			if (convertView == null
					|| convertView.getTag().equals(tweetHeadHolder) == false) {
				convertView = layoutInflater.inflate(
						R.layout.tweet_detail_head, null);

				tweetHeadHolder = new TweetHeadHolder();

				tweetHeadHolder.tweetHead = (ImageView) convertView
						.findViewById(R.id.tweet_head);
				tweetHeadHolder.tweetNick = (TextView) convertView
						.findViewById(R.id.tweet_nick);
				tweetHeadHolder.isVip = (ImageView) convertView
						.findViewById(R.id.is_vip);
				tweetHeadHolder.tweetFrom = (TextView) convertView
						.findViewById(R.id.tweet_from);
				tweetHeadHolder.tweetTimestamp = (TextView) convertView
						.findViewById(R.id.tweet_timestamp);

				convertView.setTag(tweetHeadHolder);
			} else {
				tweetHeadHolder = (TweetHeadHolder) convertView.getTag();
			}

			ImageLoader.getInstance(context).displayImage(
					tweetBean.getHead() + "/50", tweetHeadHolder.tweetHead, 6);

			tweetHeadHolder.tweetNick.setText(tweetBean.getNick());
			tweetHeadHolder.tweetFrom.setText(context.getString(R.string.from)
					+ tweetBean.getFrom());
			tweetHeadHolder.tweetTimestamp.setText(TimeUtil.converTime(
					tweetBean.getTimestamp(), 2));
			if (tweetBean.getIsVip() == 1) {
				tweetHeadHolder.isVip.setVisibility(View.VISIBLE);
			} else {
				tweetHeadHolder.isVip.setVisibility(View.GONE);
			}
			break;
		case TWEET_CONTENT:
			if (convertView == null
					|| convertView.getTag().equals(tweetContentHolder) == false) {
				convertView = layoutInflater.inflate(
						R.layout.tweet_detail_content, null);

				tweetContentHolder = new TweetContentHolder();

				tweetContentHolder.tweetContent = (TextView) convertView
						.findViewById(R.id.tweet_content);
				tweetContentHolder.tweetImage = (ImageView) convertView
						.findViewById(R.id.tweet_image);
				tweetContentHolder.forwardContainer = (RelativeLayout) convertView
						.findViewById(R.id.forward_background);
				tweetContentHolder.forwardContent = (TextView) convertView
						.findViewById(R.id.forward_content);
				tweetContentHolder.forwardImage = (ImageView) convertView
						.findViewById(R.id.forward_image);
				tweetContentHolder.forwardLocationImage = (ImageView) convertView
						.findViewById(R.id.forward_location_image);
				tweetContentHolder.locationImage = (ImageView) convertView
						.findViewById(R.id.location_image);

				convertView.setTag(tweetContentHolder);
			} else {
				tweetContentHolder = (TweetContentHolder) convertView.getTag();
			}

			tweetContentHolder.tweetContent
					.setMovementMethod(LinkMovementMethod.getInstance());
			if (tweetBean.getSource() != null
					&& tweetBean.getText().length() == 0) {
				tweetContentHolder.tweetContent.setText(context
						.getString(R.string.re_tweet));
			} else {
				ContentTransUtil.getInstance(context).displaySpannableString(
						tweetBean.getText(), tweetContentHolder.tweetContent,
						tweetBean.getMentionedUser());
			}
			
			tweetContentHolder.tweetContent.setLongClickable(true);
			tweetContentHolder.tweetContent.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View v) {
					TweetBean tempBean = TweetDetailAdapter.this.getItem(position);
					boolean me = tempBean.getName() == DBManager.getInstance(context).getProfile().getName();
					PopupWindowManager popupWindowManager = new PopupWindowManager(context);
					popupWindowManager.setupCommentFunctionPopup(tempBean.getId(), me, tempBean.getText(), PopupWindowManager.TWEET_CONTENT, 0);
					System.out.println(tempBean.getText());
					return true;
				}
				
			});
			if (tweetBean.getTweetImage() != null
					&& !tweetBean.getTweetImage().toString().equals("[]")) {
				tweetContentHolder.tweetImage.setVisibility(View.VISIBLE);
				try {
					ImageLoader.getInstance(context).displayImage(
							tweetBean.getTweetImage().getString(0) + "/460",
							tweetContentHolder.tweetImage, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				tweetContentHolder.tweetImage
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								try {
									((MainPageFragmentCallback) context).setupPictureDialog(tweetBean.getTweetImage().getString(0), "/2000", v.getDrawingCache());
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
			} else if (tweetBean.getVideoImage() != null
					&& !tweetBean.getVideoImage().toString().equals("null")) {
				tweetContentHolder.tweetImage.setVisibility(View.VISIBLE);
				ImageLoader.getInstance(context).displayImage(
						tweetBean.getVideoImage(),
						tweetContentHolder.tweetImage, 0);
				tweetContentHolder.tweetImage
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent();
								intent.setAction("android.intent.action.VIEW");
								Uri uri = Uri.parse(tweetBean.getVideoUrl());
								intent.setData(uri);
								context.startActivity(intent);
							}
						});
			} else {
				tweetContentHolder.tweetImage.setVisibility(View.GONE);
			}
			if (tweetBean.getSource() != null) {
				tweetContentHolder.forwardContainer.setVisibility(View.VISIBLE);
				tweetContentHolder.forwardContent
						.setMovementMethod(LinkMovementMethod.getInstance());
				ContentTransUtil.getInstance(context).displaySpannableString(
						"<font color='0x0085DF'>"
								+ tweetBean.getSource().getNick() + ":</font>"
								+ tweetBean.getSource().getText(),
						tweetContentHolder.forwardContent,
						tweetBean.getMentionedUser());
				tweetContentHolder.forwardContent.setLongClickable(true);
				tweetContentHolder.forwardContent.setOnLongClickListener(new OnLongClickListener(){
					@Override
					public boolean onLongClick(View v){
						TweetBean tempBean = TweetDetailAdapter.this.getItem(position);
						boolean me = tempBean.getSource().getName() == DBManager.getInstance(context).getProfile().getName();
						PopupWindowManager popupWindowManager = new PopupWindowManager(context);
						popupWindowManager.setupCommentFunctionPopup(tempBean.getSource().getId(), me, tempBean.getSource().getText(), PopupWindowManager.TWEET_CONTENT, 0);
						
						return true;
					}
				});
				if (tweetBean.getSource().getTweetImage() != null
						&& !tweetBean.getSource().getTweetImage().toString()
								.equals("[]")) {
					tweetContentHolder.forwardImage.setVisibility(View.VISIBLE);
					try {
						ImageLoader.getInstance(context).displayImage(
								tweetBean.getSource().getTweetImage()
										.getString(0)
										+ "/460",
								tweetContentHolder.forwardImage, 0);
					} catch (Exception e) {
						e.printStackTrace();
					}
					tweetContentHolder.forwardImage
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									try {
										((MainPageFragmentCallback) context).setupPictureDialog(tweetBean.getSource().getTweetImage().getString(0), "/2000", v.getDrawingCache());
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
				} else if (tweetBean.getSource().getVideoImage() != null
						&& !tweetBean.getSource().getVideoImage().toString()
								.equals("null")) {
					tweetContentHolder.forwardImage.setVisibility(View.VISIBLE);
					ImageLoader.getInstance(context).displayImage(
							tweetBean.getSource().getVideoImage(),
							tweetContentHolder.forwardImage, 0);
					tweetContentHolder.forwardImage
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									Uri uri = Uri.parse(tweetBean.getSource()
											.getVideoUrl());
									intent.setData(uri);
									context.startActivity(intent);
								}
							});
				} else {
					tweetContentHolder.forwardImage.setVisibility(View.GONE);
				}
				if (!tweetBean.getSource().getLongitude().equals("0")
						&& !tweetBean.getSource().getLatitude().equals("0")) {
					System.out.println(tweetBean.getSource().getLongitude()
							+ "," + tweetBean.getSource().getLatitude());
					ImageLoader.getInstance(context).displayImage(StaticAPI.getSOSOStaticMap(tweetBean.getSource().getLatitude().toString(), tweetBean.getSource().getLongitude().toString(), 15, DensityUtil.dip2px(context, 300), DensityUtil.dip2px(context, 80)), tweetContentHolder.forwardLocationImage, 1);
				}
			} else {
				tweetContentHolder.forwardContainer.setVisibility(View.GONE);
			}
			if ((!tweetBean.getLongitude().equals("0") && !tweetBean
					.getLatitude().equals("0"))) {
				System.out.println(tweetBean.getLongitude() + ","
						+ tweetBean.getLatitude());
				ImageLoader.getInstance(context).displayImage(
						StaticAPI.getSOSOStaticMap(tweetBean.getLatitude()
								.toString(), tweetBean.getLongitude()
								.toString(), 15, DensityUtil.dip2px(context,
								300), DensityUtil.dip2px(context, 80)),
						tweetContentHolder.locationImage, 1);
			}
			break;
		case TWEET_COMMENT_TITLE:
			if (convertView == null || convertView.getTag().equals(tweetCommentTitleHolder) == false) {
				convertView = layoutInflater.inflate(R.layout.tweet_detail_mcount, null);

				tweetCommentTitleHolder = new TweetCommentTitleHolder();

				tweetCommentTitleHolder.tweetCommentCountTitle = (TextView) convertView.findViewById(R.id.comment_count_title);
				tweetCommentTitleHolder.tweetCommentCount = (TextView) convertView.findViewById(R.id.comment_count);
				tweetCommentTitleHolder.forwardCountTitle = (TextView) convertView.findViewById(R.id.forward_count_title);
				tweetCommentTitleHolder.forwardCount = (TextView) convertView.findViewById(R.id.forward_count);
				tweetCommentTitleHolder.commentArrowLayout = (RelativeLayout) convertView.findViewById(R.id.comment_arrow_layout);
				tweetCommentTitleHolder.forwardArrowLayout = (RelativeLayout) convertView.findViewById(R.id.forward_arrow_layout);

				convertView.setTag(tweetCommentTitleHolder);
			} else {
				tweetCommentTitleHolder = (TweetCommentTitleHolder) convertView
						.getTag();
			}
			tweetCommentTitleHolder.tweetCommentCount.setText(tweetBean
					.getMCount());
			tweetCommentTitleHolder.forwardCount.setText(tweetBean.getCount());
			tweetCommentTitleHolder.tweetCommentCountTitle.setClickable(true);
			tweetCommentTitleHolder.forwardCountTitle.setClickable(true);
			tweetCommentTitleHolder.tweetCommentCountTitle
					.setOnClickListener(this);
			tweetCommentTitleHolder.forwardCountTitle.setOnClickListener(this);
			if (commentState == COMMENT_STATUS_FORWARD) {
				tweetCommentTitleHolder.tweetCommentCountTitle.setTextColor(0xFFAAAAAA);
				tweetCommentTitleHolder.tweetCommentCount.setTextColor(0xFFAAAAAA);
				tweetCommentTitleHolder.forwardCountTitle.setTextColor(0xFF777777);
				tweetCommentTitleHolder.forwardCount.setTextColor(0xFF777777);
				tweetCommentTitleHolder.commentArrowLayout.setVisibility(View.GONE);
				tweetCommentTitleHolder.forwardArrowLayout.setVisibility(View.VISIBLE);
			} else {
				tweetCommentTitleHolder.tweetCommentCountTitle.setTextColor(0xFF777777);
				tweetCommentTitleHolder.tweetCommentCount.setTextColor(0xFF777777);
				tweetCommentTitleHolder.forwardCountTitle.setTextColor(0xFFAAAAAA);
				tweetCommentTitleHolder.forwardCount.setTextColor(0xFFAAAAAA);
				tweetCommentTitleHolder.commentArrowLayout.setVisibility(View.VISIBLE);
				tweetCommentTitleHolder.forwardArrowLayout.setVisibility(View.GONE);
			}
			break;
		case TWEET_COMMENT:
			if (convertView == null || convertView.getTag().equals(tweetCommentHolder) == false) {
				convertView = layoutInflater.inflate(R.layout.tweet_detail_mlist, null);

				tweetCommentHolder = new TweetCommentHolder();

				tweetCommentHolder.commentHead = (ImageView) convertView.findViewById(R.id.comment_head);
				tweetCommentHolder.tweetNick = (TextView) convertView.findViewById(R.id.comment_nick);
				tweetCommentHolder.tweetCommentContent = (TextView) convertView.findViewById(R.id.comment_content);
				tweetCommentHolder.tweetTimestamp = (TextView) convertView.findViewById(R.id.comment_timestamp);

				convertView.setTag(tweetCommentHolder);
			} else {
				tweetCommentHolder = (TweetCommentHolder) convertView.getTag();
			}
			TweetBean cList = getItem(position);
			if (cList.getText().length() <= 0) {
				tweetCommentHolder.tweetCommentContent.setText(context
						.getString(R.string.re_tweet));
			} else {
				ContentTransUtil.getInstance(context).displaySpannableString(
						cList.getText(),
						tweetCommentHolder.tweetCommentContent,
						cList.getMentionedUser());
			}
			ImageLoader.getInstance(context).displayImage(
					cList.getHead() + "/30", tweetCommentHolder.commentHead, 0);
			tweetCommentHolder.commentHead
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Bundle bundle = new Bundle();
							bundle.putInt("type", TabProfileFragment.OTHER);
							bundle.putString("name", getItem(position).getName());
							bundle.putString("id", getItem(position).getOpenId());
							((TweetDetailFragmentCallback) context).setupProfileFragment(bundle);
						}
					});
			tweetCommentHolder.tweetNick.setText(cList.getNick());
			tweetCommentHolder.tweetTimestamp.setText(TimeUtil.converTime(cList.getTimestamp(), 2));
			break;
		}

		return convertView;
	}

	private static class TweetHeadHolder {
		public ImageView tweetHead;
		public TextView tweetNick;
		public ImageView isVip;
		public TextView tweetTimestamp;
		public TextView tweetFrom;
	}

	private static class TweetContentHolder {
		public TextView tweetContent;
		public ImageView tweetImage;
		public RelativeLayout forwardContainer;
		public TextView forwardContent;
		public ImageView forwardImage;
		public ImageView forwardLocationImage;
		public ImageView locationImage;
	}

	private static class TweetCommentTitleHolder {
		public TextView tweetCommentCountTitle;
		public TextView tweetCommentCount;
		public TextView forwardCountTitle;
		public TextView forwardCount;
		public RelativeLayout commentArrowLayout;
		public RelativeLayout forwardArrowLayout;
	}

	private static class TweetCommentHolder {
		public ImageView commentHead;
		public TextView tweetNick;
		public TextView tweetTimestamp;
		public TextView tweetCommentContent;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.comment_count_title:
			commentState = COMMENT_STATUS_COMMENT;
			((TweetDetailFragmentCallback) context)
					.mListChange(COMMENT_STATUS_COMMENT);
			break;
		case R.id.forward_count_title:
			commentState = COMMENT_STATUS_FORWARD;
			((TweetDetailFragmentCallback) context)
					.mListChange(COMMENT_STATUS_FORWARD);
			break;
		}
	}

}
