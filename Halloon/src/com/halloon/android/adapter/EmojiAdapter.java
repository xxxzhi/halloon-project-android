package com.halloon.android.adapter;

import com.halloon.android.util.EmojiContainer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class EmojiAdapter extends BaseAdapter {
	
	private Context context;
	private String[] emoContainer;
	
	public EmojiAdapter(Context context){
		this.context = context;
		emoContainer = EmojiContainer.emoNameContainer;
	}

	@Override
	public int getCount() {
		return emoContainer.length;
	}

	@Override
	public String getItem(int position) {
		return emoContainer[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if(convertView != null){
			imageView = (ImageView) convertView;
		}else{
			imageView = new ImageView(context);
		}
		
		imageView.setImageDrawable(context.getResources().getDrawable(EmojiContainer.getEmojiId(context, position)));
		return imageView;
	}

}
