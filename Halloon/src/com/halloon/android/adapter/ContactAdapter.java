package com.halloon.android.adapter;

import java.util.ArrayList;
import java.util.Comparator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.halloon.android.R;
import com.halloon.android.bean.UserBean;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.util.Constants;
import com.lhws.android.uitl.StringMatcher;
import com.lhws.components.widget.header.StickyListHeadersBaseAdapter;

public class ContactAdapter extends StickyListHeadersBaseAdapter implements
		SectionIndexer {
	protected LayoutInflater _inflater;
	protected ArrayList<UserBean> _items;
	protected int _layoutResourceId;

	private String mSections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#";

	public ContactAdapter(Context context, int textViewResourceId,
			ArrayList<UserBean> items) {
		super(context);
		_inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_layoutResourceId = textViewResourceId;
		_items = items;
		// Collections.sort(_items, new MyComparator());

	}

	public UserBean getItem(int position) {
		return _items.get(position);
	}

	public ArrayList<UserBean> getItems() {
		return _items;
	}

	public int getCount() {
		return _items.size();
	}

	protected static class ContactRowViewHolder {

		ImageView picture;
		TextView fullname;
		TextView headline;
		ImageView sex;

	}

	public long getItemId(int position) {
		return position;
	}

	public int getPositionForSection(int section) {
		for (int i = section; i >= 0; i--) {
			for (int j = 0; j < getCount(); j++) {
				if (i == 0) {
					// For numeric section
					for (int k = 0; k <= 9; k++) {
						if (StringMatcher.match(
								String.valueOf(StringMatcher.cn2FirstSpell(
										getItem(j).getNick()).charAt(0)),
								String.valueOf(k)))
							return j;
					}
				} else {
					if (StringMatcher.match(
							String.valueOf(StringMatcher.cn2FirstSpell(
									getItem(j).getNick()).charAt(0)),
							String.valueOf(mSections.charAt(i))))
						return j;
				}
			}
		}
		return 0;
	}

	public int getSectionForPosition(int arg0) {
		return 0;
	}

	public Object[] getSections() {
		String[] sections = new String[mSections.length()];
		for (int i = 0; i < mSections.length(); i++)
			sections[i] = String.valueOf(mSections.charAt(i));
		return sections;
	}

	@Override
	public long getHeaderId(int position) {
		return StringMatcher.cn2FirstSpell(getItem(position).getNick())
				.subSequence(0, 1).charAt(0);

	}

	@Override
	public View getHeaderView(int position, View convertView) {
		HeaderViewHolder holder;

		if (convertView == null) {
			holder = new HeaderViewHolder();
			convertView = _inflater.inflate(R.layout.contact_section_header,
					null);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}

		// set header text as first char in name
		holder.text.setText(StringMatcher.cn2FirstSpell(
				getItem(position).getNick()).subSequence(0, 1));

		return convertView;
	}

	class HeaderViewHolder {
		TextView text;
	}

	private class MyComparator implements Comparator<UserBean> {

		public int compare(UserBean arg0, UserBean arg1) {
			return StringMatcher.cn2FirstSpell(arg0.getNick()).compareToIgnoreCase(StringMatcher.cn2FirstSpell(arg1.getNick()));

		}

	}

	@Override
	protected View getView(int position, View convertView) {
		ContactRowViewHolder holder;

		UserBean user = getItem(position);
		if (user == null) {
			Log.d(Constants.LOG_TAG, "getView null");
			return null;
		}

		if (convertView == null) {

			holder = new ContactRowViewHolder();

			convertView = _inflater.inflate(_layoutResourceId, null);
			holder.picture = (ImageView) convertView.findViewById(R.id.picture);
			holder.fullname = (TextView) convertView
					.findViewById(R.id.fullname);
			holder.headline = (TextView) convertView
					.findViewById(R.id.headline);
			holder.sex = (ImageView) convertView.findViewById(R.id.sex);
			convertView.setTag(holder);

		} else {

			holder = (ContactRowViewHolder) convertView.getTag();
		}
		holder.fullname.setText(user.getNick());
		holder.headline.setText(user.getTweetBean().getText());
		holder.sex.setVisibility(View.GONE);
		/*
		 * if(user.getSex().equals("男")){
		 * holder.sex.setVisibility(View.VISIBLE);
		 * holder.sex.setImageResource(R.drawable.wb_icon_male); }else
		 * if(user.getSex().equals("女")){
		 * holder.sex.setVisibility(View.VISIBLE);
		 * holder.sex.setImageResource(R.drawable.wb_icon_female); }else{
		 * holder.sex.setVisibility(View.GONE); }
		 */
		ImageLoader.getInstance(getContext()).displayImage(
				user.getHead() + "/100", holder.picture, 4);
		return convertView;
	}

	// public void sortItems() {
	// Collections.sort(_items, new MyComparator());
	//
	// }

}
