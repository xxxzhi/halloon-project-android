package com.halloon.android.adapter;

import java.util.ArrayList;

import com.halloon.android.R;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class SquarePagerAdapter extends PagerAdapter {
	int r[] = new int []{R.drawable.ad1,R.drawable.ad2,R.drawable.ad3};
	ArrayList<View> list = new ArrayList<View>();
	public SquarePagerAdapter(Context context){
		for(int i = 0 ; i != 3; ++ i){
			ImageView iv = new ImageView(context);
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
			iv.setLayoutParams(params);
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setImageResource(r[i]);
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
