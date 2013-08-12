package com.halloon.android.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.halloon.android.R;
import com.halloon.android.bean.TweetBean;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.util.ContentTransUtil;
import com.halloon.android.util.TimeUtil;

public class AtListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater layoutInflater;

	private ArrayList<TweetBean> list;

	public AtListAdapter(Context context, ArrayList<TweetBean> list) {
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public TweetBean getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup container) {
		AtListHolder atListHolder = null;

		TweetBean tweetBean = getItem(position);

		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.private_content, null);
			atListHolder = new AtListHolder();
			atListHolder.headImage = (ImageView) convertView.findViewById(R.id.head);
			atListHolder.nickName = (TextView) convertView.findViewById(R.id.nick);
			atListHolder.content = (TextView) convertView.findViewById(R.id.content);
			atListHolder.timeStamp = (TextView) convertView.findViewById(R.id.time_stamp);

			convertView.setTag(atListHolder);
		} else {
			atListHolder = (AtListHolder) convertView.getTag();
		}

		ImageLoader.getInstance(context).displayImage(tweetBean.getHead() + "/40", atListHolder.headImage, 1, null);
		atListHolder.nickName.setText(tweetBean.getNick());
		ContentTransUtil.getInstance(context).displaySpannableString(tweetBean.getText(), atListHolder.content, tweetBean, false, false);
		atListHolder.timeStamp.setText(TimeUtil.converTime(tweetBean.getTimestamp(), 2));

		return convertView;
	}

	private static class AtListHolder {
		ImageView headImage;
		TextView nickName;
		TextView content;
		TextView timeStamp;
	}

}
