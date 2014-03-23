package com.halloon.android.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SquarePagerAdapter extends PagerAdapter {

	ArrayList<View> list = new ArrayList<View>();
	public SquarePagerAdapter(Context context){
		for(int i = 0 ; i != 3; ++ i){
			ImageView iv = new ImageView(context);
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
			iv.setLayoutParams(params);
			list.add( iv);
		}
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(list.get(position));
		return list.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(list.get(position));
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

}
