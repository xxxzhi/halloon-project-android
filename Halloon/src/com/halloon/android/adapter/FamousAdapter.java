package com.halloon.android.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.halloon.android.HalloonApplication;
import com.halloon.android.R;
import com.halloon.android.bean.FamousBean;
import com.halloon.android.bean.FamousBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.task.BaseCompatiableTask;
import com.halloon.android.ui.fragment.TweetDetailFragment.TweetDetailFragmentCallback;
import com.halloon.android.ui.fragment.UserSimpleListFragment;

public class FamousAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater layoutInflater;
	private ArrayList<FamousBean> tweetBeans;
	private int userType ;
	private HalloonApplication application;
	private String myName ;
	public FamousAdapter(Context context, ArrayList<FamousBean> tweetBeans) {
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.tweetBeans = tweetBeans;
		application = (HalloonApplication) context.getApplicationContext();
		this.userType = userType;
		this.myName = myName;
	}

	@Override
	public int getCount() {
		return tweetBeans.size();
	}

	@Override
	public FamousBean getItem(int position) {
		if (position < tweetBeans.size()) {
			return tweetBeans.get(position);
		} else {
			return new FamousBean();
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		FamousViewHolder holder;
		final FamousBean userBean = getItem(position);

		if (userBean == null) {
			return null;
		}

		if (convertView == null) {
			holder = new FamousViewHolder();

			convertView = layoutInflater.inflate(R.layout.list_item_famous, null);

			holder.headImage = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			
			convertView.setTag(holder);
		} else {
			holder = (FamousViewHolder) convertView.getTag();
		}
		
		ImageLoader.getInstance(context).displayImage(userBean.getHead() + "/100", holder.headImage, 0, null);
		System.out.println("nick:"+userBean.getNick()+"---- " + userBean.getAccount());
		
		holder.title.setText(userBean.getNick()+"");
		holder.content.setText(userBean.getBrief());
		return convertView;
	}
	
	
	private static final class FamousViewHolder {
		
		ImageView headImage;
		TextView title;
		TextView content;
		
	}

}
