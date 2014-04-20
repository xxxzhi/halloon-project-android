package com.halloon.android.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.halloon.android.bean.UserBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.task.BaseCompatiableTask;
import com.halloon.android.ui.fragment.TweetDetailFragment.TweetDetailFragmentCallback;
import com.halloon.android.ui.fragment.UserSimpleListFragment;

public class SimpleUserAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater layoutInflater;
	private ArrayList<UserBean> tweetBeans;
	private int userType ;
	private HalloonApplication application;
	private String myName ;
	public SimpleUserAdapter(Context context, ArrayList<UserBean> tweetBeans,int userType,String myName) {
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
	public UserBean getItem(int position) {
		if (position < tweetBeans.size()) {
			return tweetBeans.get(position);
		} else {
			return new UserBean();
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		TweetViewHolder holder;
		final UserBean userBean = getItem(position);

		if (userBean == null) {
			return null;
		}

		if (convertView == null) {
			holder = new TweetViewHolder();

			convertView = layoutInflater.inflate(R.layout.listitem_simple_user, null);

			holder.headImage = (ImageView) convertView.findViewById(R.id.tweet_head);
			holder.title = (TextView) convertView.findViewById(R.id.user_nick);
			holder.sex = (ImageView) convertView.findViewById(R.id.my_sex);
			holder.from = (TextView) convertView.findViewById(R.id.user_tweet);
			holder.fans = (Button) convertView.findViewById(R.id.bt_fans);
			
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
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		holder.headImage.setOnClickListener(tweetListClickListener);
		
		Log.i("simpelUserAdapter", "sex:"+userBean.getSex());
		if (userBean.getSex() != null) {
			if (userBean.getSex().equals("1") || userBean.getSex().equals("男")) {
				holder.sex.setVisibility(View.VISIBLE);
				holder.sex.setImageResource(R.drawable.wb_icon_male);
			} else if (userBean.getSex().equals("0") || userBean.getSex().equals("女")) {
				holder.sex.setVisibility(View.VISIBLE);
				holder.sex.setImageResource(R.drawable.wb_icon_female);
			} else {
				holder.sex.setVisibility(View.GONE);
			}
		}else{
			holder.sex.setVisibility(View.GONE);
		}
		
		ImageLoader.getInstance(context).displayImage(userBean.getHead() + "/100", holder.headImage, 0, null);
		holder.title.setText(userBean.getNick());
		holder.from.setText(userBean.getTweetBean().getText());
		switch (userType) {
		case UserSimpleListFragment.FANS:
			if(userBean.getIsfans() != null ){
			if(userBean.getIsfans().equals("1")){
				holder.fans.setText(R.string.is_fans);
			}else{
				holder.fans.setVisibility(View.GONE);
			}
			}
			break;
		case UserSimpleListFragment.IDOL:
			if(userBean.getIsidol() != null ){
			if(userBean.getIsidol().equals("1")){
				holder.fans.setText(R.string.is_idol);
			}else{
				holder.fans.setText(R.string.not_idol);
			}
			}
			break;
		default:
			holder.fans.setVisibility(View.GONE);
			break;
		}
		
		return convertView;
	}
	
	private void clickHandle(final Button b,final UserBean userBean){
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (userType) {
				case UserSimpleListFragment.FANS:
					if(userBean.getIsfans().equals("1")){
						b.setText(R.string.is_fans);
					}else{
						b.setVisibility(View.GONE);
					}
					break;
				case UserSimpleListFragment.IDOL:
					if(userBean.getIsidol().equals("1")){
						b.setText(R.string.is_idol);
					}else{
						b.setText(R.string.not_idol);
					}
					
					if (userBean.getIsidol().equals("0")) {

						new BaseCompatiableTask<Void, Void, int[]>() {

							@Override
							protected int[] doInBackground(Void... params) {
								return ContentManager.getInstance(context)
										.addIdol(myName, userBean.getOpenId());
							}

							@Override
							protected void onPostExecute(int[] result) {
								if (result[0] != 0) {
									Toast.makeText(
											context,
											context.getString(R.string.idol)
													+ context
															.getString(R.string.failure),
											Toast.LENGTH_LONG).show();
								} else {
									userBean.setIsidol("1");
									b.setText(R.string.is_idol);
									Toast.makeText(
											context,
											context.getString(R.string.idol)
													+ context
															.getString(R.string.success),
											Toast.LENGTH_LONG).show();
								}
							}
						}.taskExecute();
					} else {
						new BaseCompatiableTask<Void, Void, int[]>() {

							@Override
							protected int[] doInBackground(Void... params) {
								return ContentManager.getInstance(context)
										.delIdol(myName, userBean.getOpenId());
							}

							@Override
							protected void onPostExecute(int[] result) {
								if (result[0] != 0) {
									Toast.makeText(
											context,
											context.getString(R.string.cancel)
													+ context
															.getString(R.string.idol)
													+ context
															.getString(R.string.failure),
											Toast.LENGTH_LONG).show();
								} else {
									userBean.setIsidol("0");
									b.setText(R.string.not_idol);
									Toast.makeText(
											context,
											context.getString(R.string.cancel)
													+ context
															.getString(R.string.idol)
													+ context
															.getString(R.string.success),
											Toast.LENGTH_LONG).show();
								}
							}
						}.taskExecute();
					}
					
					break;
				default:
					b.setVisibility(View.GONE);
					break;
				}
			}
		});
	}
	
	private static final class TweetViewHolder {
		
		ImageView headImage;
		TextView title;
		ImageView sex;
		TextView from;
		
		Button fans;
	}

}
