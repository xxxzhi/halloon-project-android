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
import com.halloon.android.bean.PrivateDataBean;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.util.ContentTransUtil;
import com.halloon.android.util.TimeUtil;

public class PrivateMessageListAdapter extends BaseAdapter {
	private ArrayList<PrivateDataBean> list = new ArrayList<PrivateDataBean>();
	private LayoutInflater layoutInflater;
	private Context context;

	public PrivateMessageListAdapter(Context context,
			ArrayList<PrivateDataBean> privateDataBeans) {
		this.context = context;
		this.list = privateDataBeans;
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public PrivateDataBean getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PrivateContentHolder privateContentHolder = null;

		PrivateDataBean privateDataBean = getItem(position);
		if (convertView == null) {
			convertView = layoutInflater
					.inflate(R.layout.private_content, null);
			privateContentHolder = new PrivateContentHolder();
			privateContentHolder.headImage = (ImageView) convertView
					.findViewById(R.id.head);
			privateContentHolder.nickName = (TextView) convertView
					.findViewById(R.id.nick);
			privateContentHolder.content = (TextView) convertView
					.findViewById(R.id.content);
			privateContentHolder.timeStamp = (TextView) convertView
					.findViewById(R.id.time_stamp);

			convertView.setTag(privateContentHolder);
		} else {
			privateContentHolder = (PrivateContentHolder) convertView.getTag();
		}
		ImageLoader.getInstance(context).displayImage(privateDataBean.getHead() + "/100", privateContentHolder.headImage, 4, null);
		privateContentHolder.nickName.setText(privateDataBean.getNick());
		privateContentHolder.content.setText(privateDataBean.getText());
		ContentTransUtil.getInstance(context).displaySpannableString(privateDataBean.getText(), privateContentHolder.content, null, false, false);
		privateContentHolder.timeStamp.setText(TimeUtil.converTime(privateDataBean.getPubTime(), 2));

		return convertView;
	}

	private static class PrivateContentHolder {
		ImageView headImage;
		TextView nickName;
		TextView content;
		TextView timeStamp;
	}

}
