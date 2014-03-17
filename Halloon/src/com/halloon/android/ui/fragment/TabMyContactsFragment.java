package com.halloon.android.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.halloon.android.R;
import com.halloon.android.adapter.ContactAdapter;
import com.halloon.android.bean.UserBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.DBManager;
import com.halloon.android.data.SettingsManager;
import com.halloon.android.listener.OnTitleBarClickListener;
import com.halloon.android.task.BaseCompatiableTask;
import com.halloon.android.ui.activity.SlideHomeActivity;
import com.halloon.android.util.Constants;
import com.halloon.android.util.PopupWindowManager;
import com.halloon.android.widget.HalloonTitleBar;
import com.lhws.components.widget.indexer.IndexableListView;

@SuppressLint("ResourceAsColor")
public class TabMyContactsFragment extends BaseTitleBarFragment implements OnTouchListener, OnItemClickListener, OnTitleBarClickListener, OnClickListener {
	private Context context;
	private IndexableListView listView;
	private View loadingView;
	private EditText searchEditText;
	private Button deleteButton;
	private Button addButton;
	private TextView titleText;
	private ArrayList<UserBean> listItems;
	private ContactAdapter adapter;
	private ContactsFragmentCallback cfCallback;

	public interface ContactsFragmentCallback {
		public void setupLBSContactFragment();

		public void setupProfileFragment(Bundle bundle);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.cfCallback = (ContactsFragmentCallback) activity;
	}

	@Override
	protected void init(HalloonTitleBar titleBar, RelativeLayout content) {
		titleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_RIGHT_BUTTON_ONLY);
		titleBar.setOnTitleBarClickListener(this);

		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content.addView(inflater.inflate(R.layout.tab_my_contacts, null, false));

		listView = (IndexableListView) content.findViewById(R.id.listview);
		loadingView = content.findViewById(R.id.loading);
		searchEditText = (EditText) content.findViewById(R.id.search_editText);
		deleteButton = (Button) content.findViewById(R.id.deleteButton);
		addButton = titleBar.getRightButton(R.string.add);
		titleText = titleBar.getTitleTextView();
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		titleText.setText(R.string.tab_contacts);
		context = getActivity();

		listItems = new ArrayList<UserBean>();
		adapter = new ContactAdapter(context, R.layout.list_item_contact, listItems);
		listView.setAdapter(adapter);
		listView.setFastScrollEnabled(true);
		listView.setOnTouchListener(this);
		listView.setOnItemClickListener(this);
		listView.setAreHeadersSticky(false);
		
		listView.setIndexPaintBackgroundColor(R.color.indexer_bar_bkg);
		listView.setPreviewPaintBackgroundColor(R.color.indexer_preview_text_bkg);
		listView.setPreviewPaintTextColor(R.color.indexer_preview_text_color);
		
		searchEditText.addTextChangedListener(textWatcher);
		deleteButton.setOnClickListener(this);

		loadData();

	}

	public void loadData() {
		Log.d(Constants.LOG_TAG, "loadData");

		new BaseCompatiableTask<Void, Void, ArrayList<UserBean>>() {
			@Override
			protected void onPreExecute() {
				loadingView.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			}

			@Override
			protected ArrayList<UserBean> doInBackground(Void... arg0) {

				long a = System.currentTimeMillis();
				ArrayList<UserBean> tmpArrayList;
				if (SettingsManager.getInstance(context).getContactStatus() == DBManager.CONTACT_STATUS_INIT) {
					Log.d(Constants.LOG_TAG, "loadData Net");
					tmpArrayList = ContentManager.getInstance(context).getContacts();

					HashMap<String, UserBean> unduplicatedContacts = new HashMap<String, UserBean>();
					for (int i = 0; i < tmpArrayList.size(); i++) {
						unduplicatedContacts.put(tmpArrayList.get(i).getOpenId(), (UserBean) tmpArrayList.get(i));

					}

					DBManager.getInstance(context).addContacts(tmpArrayList, false);
					SettingsManager.getInstance(context).setContactStatus(DBManager.CONTACT_STATUS_READY);
				}
				tmpArrayList = DBManager.getInstance(context).getAllContacts();
				long b = System.currentTimeMillis();

				Log.d(Constants.LOG_TAG, ">>>>>>>>>>>>>>>request time in : " + (b - a) + " ms");

				return tmpArrayList;
			}

			@Override
			protected void onPostExecute(ArrayList<UserBean> result) {
				if (result != null) {
					listItems.clear();
					listItems.addAll(result);
				}
				adapter.notifyDataSetChanged();

				if (SettingsManager.getInstance(context).getContactStatus() == DBManager.CONTACT_STATUS_TO_UPDATE) {
					updateContacts();
				} else if (SettingsManager.getInstance(context).getContactStatus() == DBManager.CONTACT_STATUS_READY) {
					SettingsManager.getInstance(context).setContactStatus(DBManager.CONTACT_STATUS_TO_UPDATE);
				}
				loadingView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				searchEditText.setHint("共有" + listItems.size() + "位联系人");
			}

		}.taskExecute();
	}

	public void updateContacts() {
		Log.d(Constants.LOG_TAG, "updateContacts");

		new BaseCompatiableTask<Void, Void, ArrayList<UserBean>>() {

			@Override
			protected ArrayList<UserBean> doInBackground(Void... arg0) {
				ArrayList<UserBean> tmpArrayList = ContentManager.getInstance(context).getContacts();
				if (tmpArrayList.size() != 0) {
					HashMap<String, UserBean> unduplicatedContacts = new HashMap<String, UserBean>();
					for (int i = 0; i < tmpArrayList.size(); i++) {
						unduplicatedContacts.put(tmpArrayList.get(i).getOpenId(), (UserBean) tmpArrayList.get(i));

					}
					DBManager.getInstance(context).addContacts(tmpArrayList, true);

				} else {
					return null;
				}
				return DBManager.getInstance(context).getAllContacts();
			}

			@Override
			protected void onPostExecute(ArrayList<UserBean> result) {

				if (result != null) {
					listItems.clear();
					listItems.addAll(result);
					adapter.notifyDataSetChanged();
					Toast.makeText(context, "update finish", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "update failure", Toast.LENGTH_SHORT).show();
				}

				searchEditText.setHint("共有" + listItems.size() + "位联系人");

			}

		}.taskExecute();
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Log.d(Constants.LOG_TAG, "onTextChanged " + s.toString());
			ArrayList<UserBean> tmpArrayList = DBManager.getInstance(context).getContactsSearch(s.toString());
			listItems.clear();
			listItems.addAll(tmpArrayList);
			adapter.notifyDataSetChanged();
			if (s.length() == 0) {
				deleteButton.setVisibility(View.INVISIBLE);
			} else {
				deleteButton.setVisibility(View.VISIBLE);
			}
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		public void afterTextChanged(Editable s) {

		}

	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Bundle bundle = new Bundle();
		bundle.putString("name", listItems.get(position).getName());
		bundle.putString("id", listItems.get(position).getOpenId());
		try{
			((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			}catch(Exception e){
			e.printStackTrace();
		}
		cfCallback.setupProfileFragment(bundle);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.deleteButton:
			searchEditText.setText("");
			break;
		}
	}

	@Override
	public void onTitleContentClick(int contentEnum) {
		switch (contentEnum) {
		case OnTitleBarClickListener.RIGHT_BUTTON:
			((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			cfCallback.setupLBSContactFragment();
			break;
		case OnTitleBarClickListener.TITLE_TEXT_VIEW:
			PopupWindowManager pwManager = new PopupWindowManager(context);
			ArrayList<String> arrayList = new ArrayList<String>();
			arrayList.add("好友");
			arrayList.add("关注");
			arrayList.add("粉丝");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, arrayList);
			pwManager.setupTitleListPopup(R.id.title_text, adapter);
			break;
		case OnTitleBarClickListener.LEFT_IMAGE_BUTTON:
			Activity parent = getActivity().getParent() ;
			if(parent instanceof SlideHomeActivity){
				((SlideHomeActivity)parent).toggleSlideMenu();
			}
			break;
		}
	}
}
