package com.halloon.android.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.halloon.android.R;
import com.halloon.android.bean.UserBean;
import com.halloon.android.image.ImageLoader;

public class LBSContactAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater layoutInflater;
	private ArrayList<UserBean> userBeans;

	public LBSContactAdapter(Context context, ArrayList<UserBean> userBeans) {
		this.layoutInflater = LayoutInflater.from(context);
		this.context = context;
		this.userBeans = userBeans;
	}

	@Override
	public int getCount() {
		return userBeans.size();
	}

	@Override
	public UserBean getItem(int position) {
		return userBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		UserHolder holder = null;

		UserBean userBean = getItem(position);
		if (userBean == null) return null;

		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.lbs_contacts_item, null);

			holder = new UserHolder();
			holder.bottomBar = (RelativeLayout) convertView.findViewById(R.id.bottom_gray);
			holder.head = (ImageView) convertView.findViewById(R.id.head);
			holder.nick = (TextView) convertView.findViewById(R.id.distance);
			holder.sex = (ImageView) convertView.findViewById(R.id.sex);
			//holder.tweet = (TextView) convertView.findViewById(R.id.headline);

			convertView.setTag(holder);
		} else {
			holder = (UserHolder) convertView.getTag();
		}

		ImageLoader.getInstance(context).displayImage(userBean.getHead() + "/100", holder.head, 0, null);
		holder.nick.setText(userBean.getNick());
		if(userBean.getSex().equals("男")){
			holder.sex.setImageResource(R.drawable.wb_icon_male);
		}else if(userBean.getSex().equals("女")){
			holder.sex.setImageResource(R.drawable.wb_icon_female);
		}else{
			holder.sex.setImageDrawable(null);
		}
		//holder.tweet.setText(userBean.getTweetBean().getText());

		return convertView;
	}

	private static class UserHolder {
		RelativeLayout bottomBar;
		ImageView head;
		ImageView sex;
		TextView nick;
		//TextView tweet;
	}

}
