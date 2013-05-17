package com.halloon.android.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabMessageFragmentPagerAdapter extends FragmentPagerAdapter {
	private String[] title;
	private ArrayList<Fragment> fragmentList;

	public TabMessageFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	public TabMessageFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList) {
		super(fm);
		this.fragmentList = fragmentList;
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		return fragmentList.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragmentList.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public void setPageTitle(String[] title) {
		this.title = title;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return title[position];
	}

}
