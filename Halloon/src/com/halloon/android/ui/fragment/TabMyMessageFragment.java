package com.halloon.android.ui.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import com.halloon.android.R;
import com.halloon.android.adapter.TabMessageFragmentPagerAdapter;
import com.halloon.android.util.DensityUtil;
import com.halloon.android.util.UnderLinePageIndicator;

public class TabMyMessageFragment extends SherlockFragment {

	private Context context;
	private ArrayList<Fragment> primaryFragmentContainer = new ArrayList<Fragment>();

	private ViewPager viewPager;
	private TabMessageFragmentPagerAdapter tmFragmentAdapter;
	private UnderLinePageIndicator tpIndicator;

	private String[] title;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.tab_my_message, null, false);

		context = getSherlockActivity();

		PrivateMessageFragment privateMessageFragment = new PrivateMessageFragment();
		AtListFragment atListFragment = new AtListFragment();

		primaryFragmentContainer.add(privateMessageFragment);
		primaryFragmentContainer.add(atListFragment);

		title = new String[] { context.getString(R.string.private_message), context.getString(R.string.reply_me)};

		viewPager = (ViewPager) root.findViewById(R.id.vpager);
		tmFragmentAdapter = new TabMessageFragmentPagerAdapter(((FragmentActivity) context).getSupportFragmentManager(), primaryFragmentContainer);
		tmFragmentAdapter.setPageTitle(title);
		viewPager.setAdapter(tmFragmentAdapter);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setCurrentItem(0);

		tpIndicator = (UnderLinePageIndicator) root.findViewById(R.id.page_indicator);
		tpIndicator.setLineWidthScale(0.7F);
		tpIndicator.setSelectedColor(0xFFFFFF);
		tpIndicator.setLineHeight(DensityUtil.dip2px(context, 2));
		tpIndicator.setFades(false);
		tpIndicator.setViewPager(viewPager);

		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
}
