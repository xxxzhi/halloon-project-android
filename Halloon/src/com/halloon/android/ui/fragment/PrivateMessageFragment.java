package com.halloon.android.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.halloon.android.R;
import com.halloon.android.adapter.PrivateMessageListAdapter;
import com.halloon.android.bean.PrivateDataBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.DBManager;
import com.halloon.android.data.SettingsManager;
import com.halloon.android.util.Constants;
import com.halloon.android.widget.HalloonPullableView;
import com.halloon.android.widget.HalloonPullableView.OnFooterRefreshListener;
import com.halloon.android.widget.HalloonPullableView.OnHeaderRefreshListener;

public class PrivateMessageFragment extends SherlockFragment implements OnHeaderRefreshListener,
                                                                        OnFooterRefreshListener{

	private HalloonPullableView pullAndDrop;
	
	private ListView list;
	private PrivateMessageListAdapter adapter;
	private ArrayList<PrivateDataBean> privateDataBeans;

	private PrivateMessageCallback pmCallback;
	private Context context;
	
	public interface PrivateMessageCallback{
		public void setupPrivateDetailFragment(Bundle bundle);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		pmCallback = (PrivateMessageCallback) activity;
		this.context = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.private_message_fragment, null, false);

		pullAndDrop = (HalloonPullableView) root.findViewById(R.id.pull_and_drop);
		pullAndDrop.setOnHeaderRefreshListener(this);
		pullAndDrop.setOnFooterRefreshListener(this);
		
		list = (ListView) root.findViewById(R.id.private_message_list);
		privateDataBeans = new ArrayList<PrivateDataBean>();
		Log.d(Constants.LOG_TAG, privateDataBeans.size() + "");
		adapter = new PrivateMessageListAdapter(context, privateDataBeans);
		list.setAdapter(adapter);
		loadData();

		root.setId(0);

		return root;
	}

	public void loadData() {
		AsyncTask<Void, Void, ArrayList<PrivateDataBean>> task = new AsyncTask<Void, Void, ArrayList<PrivateDataBean>>() {
			@Override
			protected ArrayList<PrivateDataBean> doInBackground(Void... params) {
				ArrayList<PrivateDataBean> tmp_list;

				if (SettingsManager.getInstance(context).getPrivateMessageStatus() == DBManager.PRIVATE_MESSAGE_STATUS_INIT) {
					tmp_list = ContentManager.getInstance(context).getPrivateHomeTimeLine("0", "0", "20", "0");
					if (tmp_list.size() != 0) {
						HashMap<String, PrivateDataBean> privateLists = new HashMap<String, PrivateDataBean>();
						for (int i = 0; i < tmp_list.size(); i++) {
							privateLists.put(tmp_list.get(i).getPubTime(), tmp_list.get(i));
						}

						DBManager.getInstance(context).addPrivateMessage(privateLists, true);
					}
				} else {
					tmp_list = DBManager.getInstance(context).getPrivateMessage();
				}

				return tmp_list;
			}

			@Override
			protected void onPostExecute(ArrayList<PrivateDataBean> result) {
				
				privateDataBeans.clear();
				privateDataBeans.addAll(result);
				adapter.notifyDataSetChanged();
			}

		};

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			task.execute();
		}
	}

	private void refreshData() {
		AsyncTask<Void, Void, ArrayList<PrivateDataBean>> task = new AsyncTask<Void, Void, ArrayList<PrivateDataBean>>() {

			@Override
			protected ArrayList<PrivateDataBean> doInBackground(Void... params) {
				ArrayList<PrivateDataBean> tmp_list = ContentManager.getInstance(context).getPrivateHomeTimeLine("0", "0", "20", "0");

				if (tmp_list.size() != 0) {
					HashMap<String, PrivateDataBean> privateBeans = new HashMap<String, PrivateDataBean>();
					for (int i = 0; i < tmp_list.size(); i++) {
						privateBeans.put(tmp_list.get(i).getPubTime(), tmp_list.get(i));
					}

					DBManager.getInstance(context).addPrivateMessage(privateBeans, true);
					return tmp_list;

				}

				return null;
			}

			@Override
			protected void onPostExecute(ArrayList<PrivateDataBean> result) {
				if (result != null) {

					privateDataBeans.clear();
					privateDataBeans.addAll(result);
				}
				adapter.notifyDataSetChanged();
				pullAndDrop.onHeaderRefreshComplete();
			}
		};

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			task.execute();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putString("id", adapter.getItem(position).getOpenId());
				bundle.putString("name", adapter.getItem(position).getName());
				
				pmCallback.setupPrivateDetailFragment(bundle);
			
			}
			
		});
	}

	@Override
	public void onFooterRefresh(HalloonPullableView view) {}

	@Override
	public void onHeaderRefresh(HalloonPullableView view) {refreshData();}
}
