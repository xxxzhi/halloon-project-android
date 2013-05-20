package com.halloon.android.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockFragment;
import com.halloon.android.R;
import com.halloon.android.adapter.LBSContactAdapter;
import com.halloon.android.bean.UserBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.task.BaseCompatiableTask;
import com.halloon.android.task.LocationTask;
import com.halloon.android.listener.OnLocationSeekListener;
import com.halloon.android.util.Constants;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;

public class LBSContactFragment extends SherlockFragment implements OnClickListener,
                                                                    OnLocationSeekListener{

	private LBSContactFragmentCallback lcCallback;
	private Context context;
	private GridView listView;
	private Button backButton;
	private LBSContactAdapter lbsAdapter;
	private ArrayList<UserBean> userBeans;

	private LocationTask locationTask;

	public interface LBSContactFragmentCallback {
		public void destroyLBSFragment();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.lcCallback = (LBSContactFragmentCallback) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.lbs_contacts, null, false);

		context = getActivity();

		listView = (GridView) root.findViewById(R.id.lbs_contacts);
		backButton = (Button) root.findViewById(R.id.back_button);
		backButton.setOnClickListener(this);
		userBeans = new ArrayList<UserBean>();
		lbsAdapter = new LBSContactAdapter(context, userBeans);
		listView.setAdapter(lbsAdapter);
		locationTask = new LocationTask(context);
		locationTask.setOnLocationSeekListener(this);
		locationTask.taskExecute();
		
		return root;
	}

	@Override
	public void onStart() {
		super.onStart();
		
		Intent intent = new Intent();
		intent.setAction(Constants.GLOBAL_TAB_VISIBILITY);
		Bundle bundle = new Bundle();
		bundle.putBoolean("isTabShow", false);
		bundle.putBoolean("isCoverShow", false);
		intent.putExtras(bundle);
		context.sendBroadcast(intent);
	}

	private void loadData(final String longitude, final String latitude) {
		new BaseCompatiableTask<Void, Void, ArrayList<UserBean>>() {
			@Override
			protected void onPreExecute() {

			}

			@Override
			protected ArrayList<UserBean> doInBackground(Void... params) {
				ArrayList<UserBean> temp_userBean = ContentManager.getInstance(context).getAroundPeople(longitude, latitude, "", "25", "2");
		
				temp_userBean.addAll(ContentManager.getInstance(context).getAroundPeople(longitude, latitude, "", "25", "1"));

				
				Comparator<UserBean> comparator = new Comparator<UserBean>(){

					@Override
					public int compare(UserBean userBeanA, UserBean userBeanB) {
						
						return userBeanA.getName().compareToIgnoreCase(userBeanB.getName());
					}
					
				};
				
				Collections.sort(temp_userBean, comparator);

				return temp_userBean;
			}

			@Override
			protected void onPostExecute(ArrayList<UserBean> result) {
				userBeans.clear();
				userBeans.addAll(result);
				lbsAdapter.notifyDataSetChanged();
			}
		}.taskExecute();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		listView.setOnItemClickListener( new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(!lbsAdapter.getItem(position).getHead().equals("")){
					Bundle bundle = new Bundle();
					bundle.putString("name", lbsAdapter.getItem(position).getName());
					bundle.putString("id", lbsAdapter.getItem(position).getOpenId());
					((BaseMultiFragmentActivity) context).setupProfileFragment(bundle);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		lcCallback.destroyLBSFragment();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (locationTask != null) locationTask.cancel(true);
		
		Intent intent = new Intent();
		intent.setAction(Constants.GLOBAL_TAB_VISIBILITY);
		Bundle bundle = new Bundle();
		bundle.putBoolean("isTabShow", true);
		bundle.putBoolean("isCoverShow", false);
		intent.putExtras(bundle);
		context.sendBroadcast(intent);
	}
	
	@Override
	public void onLocationSeeking(){
		
	}

	@Override
	public void onLocationGot(double longitude, double latitude) {
		loadData(String.valueOf(longitude), String.valueOf(latitude));
	}

}
