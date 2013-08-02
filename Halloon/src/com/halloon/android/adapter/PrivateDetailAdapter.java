package com.halloon.android.adapter;

import java.util.ArrayList;

import com.halloon.android.R;
import com.halloon.android.bean.PrivateDataBean;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.util.ContentTransUtil;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PrivateDetailAdapter extends BaseAdapter {
	
	private Context context;
	private LayoutInflater layoutInflater;
	private ArrayList<PrivateDataBean> privateData;
	
	public PrivateDetailAdapter(Context context, ArrayList<PrivateDataBean> privateData){
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.privateData = privateData;
	}

	@Override
	public int getCount() {
		return privateData.size();
	}

	@Override
	public PrivateDataBean getItem(int position) {
		System.out.println(position + ":" + privateData.get(position).getMsgBox());
		return privateData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PrivateOtherHolder privateOtherHolder = null;
		PrivateMeHolder privateMeHolder = null;
		
		PrivateDataBean privateDataBean = getItem(position);
		System.out.println(privateDataBean.getMsgBox());
		if(privateDataBean.getMsgBox() == 1){
			if(convertView == null || !convertView.getTag().equals(privateOtherHolder)){
				convertView = layoutInflater.inflate(R.layout.private_message_other_content, null);
				privateOtherHolder = new PrivateOtherHolder();
				privateOtherHolder.headImage = (ImageView) convertView.findViewById(R.id.private_head);
				privateOtherHolder.content = (TextView) convertView.findViewById(R.id.private_content);
				convertView.setTag(privateOtherHolder);
			}else{
				privateOtherHolder = (PrivateOtherHolder) convertView.getTag();
			}
			ImageLoader.getInstance(context).displayImage(privateDataBean.getHead() + "/50", privateOtherHolder.headImage, 1);
			privateOtherHolder.content.setMovementMethod(LinkMovementMethod.getInstance());
			ContentTransUtil.getInstance(context).displaySpannableString(privateDataBean.getText(), privateOtherHolder.content, null, false, true);
		}else{
			if(convertView == null || !convertView.getTag().equals(privateMeHolder)){
				convertView = layoutInflater.inflate(R.layout.private_message_me_content, null);
				privateMeHolder = new PrivateMeHolder();
				privateMeHolder.headImage = (ImageView) convertView.findViewById(R.id.private_head);
				privateMeHolder.content = (TextView) convertView.findViewById(R.id.private_content);
				convertView.setTag(privateMeHolder);
			}else{
				privateMeHolder = (PrivateMeHolder) convertView.getTag();
			}
			ImageLoader.getInstance(context).displayImage(privateDataBean.getMyHead() + "/50", privateMeHolder.headImage, 1);
			privateMeHolder.content.setMovementMethod(LinkMovementMethod.getInstance());
			ContentTransUtil.getInstance(context).displaySpannableString(privateDataBean.getText(), privateMeHolder.content, null, false, true);
		}
	    
	    return convertView;
	}
	
	private static class PrivateOtherHolder{
		ImageView headImage;
		TextView content;
	}
	
	private static class PrivateMeHolder{
		ImageView headImage;
		TextView content;
	}

}
