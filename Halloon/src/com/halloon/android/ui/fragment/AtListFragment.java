package com.halloon.android.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halloon.android.HalloonApplication;
import com.halloon.android.R;
import com.halloon.android.adapter.TweetContentAdapter;
import com.halloon.android.bean.TweetBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.DBManager;
import com.halloon.android.data.SettingsManager;
import com.halloon.android.task.BaseCompatiableTask;
import com.halloon.android.view.PullToRefreshListView;
import com.halloon.android.view.PullToRefreshListView.OnRefreshListener;

public class AtListFragment extends Fragment {

	private PullToRefreshListView list;
	private TweetContentAdapter adapter;

	private ArrayList<TweetBean> arrayList = new ArrayList<TweetBean>();

	private Context context;
	
	private HalloonApplication application;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.at_list_fragment, null, false);

		list = (PullToRefreshListView) root.findViewById(R.id.list);
		adapter = new TweetContentAdapter(context, arrayList);
		list.setAdapter(adapter);
		
		application = (HalloonApplication) ((Activity) context).getApplication();

		loadData();

		root.setId(2);

		return root;
	}

	private void loadData() {
		new BaseCompatiableTask<Void, Void, ArrayList<TweetBean>>() {

			@Override
			protected void onPreExecute() {
				list.prepareForRefresh();
				list.onRefresh();
			}

			@Override
			protected ArrayList<TweetBean> doInBackground(Void... arg0) {
				ArrayList<TweetBean> tmp_list;
				if (SettingsManager.getInstance(context).getAtListStatus() == DBManager.AT_LIST_STATUS_INIT) {
					tmp_list = ContentManager.getInstance(context).getMentionsHomeTimeLine("0", "0", "20", "0", "0", "0");
					if (tmp_list.size() != 0) {
						HashMap<String, TweetBean> atLists = new HashMap<String, TweetBean>();
						for (int i = 0; i < tmp_list.size(); i++) {
							atLists.put(tmp_list.get(i).getTimestamp(), tmp_list.get(i));
						}

						DBManager.getInstance(context).addAtList(tmp_list, true);
					}
				} else {
					tmp_list = DBManager.getInstance(context).getAtList();
				}
				
				if(tmp_list != null){
					int i = 0;
					do{
						String text = tmp_list.get(i).getText();
						String ADDR_PATTERN = "http://url\\.cn/[a-zA-Z0-9]+";
						Pattern pattern = Pattern.compile(ADDR_PATTERN);
						Matcher matcher = pattern.matcher(text);
						if(tmp_list.get(i).getMusicUrl() != null || tmp_list.get(i).getVideoImage() != null){
							while(matcher.find()){
								String group = matcher.group();
								group = group.substring(group.lastIndexOf("/") + 1);
								if(application.getShortList().get(group) == null){
									String longUrl = ContentManager.getInstance(context).getExpandedUrl(group);
									application.getShortList().put(group, longUrl);
								}
							}
							
						}
						
						if(tmp_list.get(i).getSource() != null && (tmp_list.get(i).getSource().getMusicUrl() != null || tmp_list.get(i).getSource().getVideoImage() != null)){
							matcher = pattern.matcher(tmp_list.get(i).getSource().getText());
							while(matcher.find()){
								
								String group = matcher.group();
								group = group.substring(group.lastIndexOf("/") + 1);
								if(application.getShortList().get(group) == null){
									String longUrl = ContentManager.getInstance(context).getExpandedUrl(group);
									application.getShortList().put(group, longUrl);
								}
							}
						}
					}while(++i < tmp_list.size());
				}
				
				return tmp_list;
			}

			@Override
			protected void onPostExecute(ArrayList<TweetBean> result) {
				
				arrayList.clear();
				arrayList.addAll(result);
				adapter.notifyDataSetChanged();
				list.onRefreshComplete();
			}

		}.taskExecute();
	}

	private void refreshData() {
		new BaseCompatiableTask<Void, Void, ArrayList<TweetBean>>() {
			@Override
			protected ArrayList<TweetBean> doInBackground(Void... params) {
				ArrayList<TweetBean> tmp_list = ContentManager.getInstance(context).getMentionsHomeTimeLine("0", "0", "20", "0", "0", "0");
				if (tmp_list.size() != 0) {
					HashMap<String, TweetBean> atLists = new HashMap<String, TweetBean>();
					for (int i = 0; i < tmp_list.size(); i++) {
						atLists.put(tmp_list.get(i).getTimestamp(), tmp_list.get(i));
					}

					DBManager.getInstance(context).addAtList(tmp_list, true);
					
					if(tmp_list != null){
						int i = 0;
						do{
							String text = tmp_list.get(i).getText();
							String ADDR_PATTERN = "http://url\\.cn/[a-zA-Z0-9]+";
							Pattern pattern = Pattern.compile(ADDR_PATTERN);
							Matcher matcher = pattern.matcher(text);
							if(tmp_list.get(i).getMusicUrl() != null || tmp_list.get(i).getVideoImage() != null){
								while(matcher.find()){
									String group = matcher.group();
									group = group.substring(group.lastIndexOf("/") + 1);
									if(application.getShortList().get(group) == null){
										String longUrl = ContentManager.getInstance(context).getExpandedUrl(group);
										application.getShortList().put(group, longUrl);
									}
								}
								
							}
							
							if(tmp_list.get(i).getSource() != null && (tmp_list.get(i).getSource().getMusicUrl() != null || tmp_list.get(i).getSource().getVideoImage() != null)){
								matcher = pattern.matcher(tmp_list.get(i).getSource().getText());
								while(matcher.find()){
									
									String group = matcher.group();
									group = group.substring(group.lastIndexOf("/") + 1);
									if(application.getShortList().get(group) == null){
										String longUrl = ContentManager.getInstance(context).getExpandedUrl(group);
										application.getShortList().put(group, longUrl);
									}
								}
							}
						}while(++i < tmp_list.size());
					}
					
					return tmp_list;
				}

				return null;
			}

			@Override
			protected void onPostExecute(ArrayList<TweetBean> result) {
				if (result != null) {
					arrayList.clear();
					arrayList.addAll(result);
				}

				list.onRefreshComplete();
				adapter.notifyDataSetChanged();
			}
		}.taskExecute();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		list.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				refreshData();
			}
		});

	}
}
