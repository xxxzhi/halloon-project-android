package com.halloon.android.ui.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import com.halloon.android.R;
import com.halloon.android.adapter.PrivateDetailAdapter;
import com.halloon.android.bean.PrivateDataBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;

public class PrivateDetailFragment extends SherlockFragment implements OnClickListener {
	
	private Context context;
	private ListView list;
	private TextView title;
	private PrivateDetailAdapter adapter;
	private ArrayList<PrivateDataBean> privateList;
	private Button backButton;
	
	private String name;
	private String openId;
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		name = getArguments().getString("name");
		openId = getArguments().getString("id");
		this.context = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = inflater.inflate(R.layout.private_message_detail_fragment, null, false);
		
		backButton = (Button) root.findViewById(R.id.back_button);
		backButton.setOnClickListener(this);
		title = (TextView) root.findViewById(R.id.title_text);
		list = (ListView) root.findViewById(R.id.private_message_list);
		privateList = new ArrayList<PrivateDataBean>();
		adapter = new PrivateDetailAdapter(context, privateList);
		list.setAdapter(adapter);
		getData();
		
		return root;
	}
	
	private void getData(){
		AsyncTask<Void, Void, ArrayList<PrivateDataBean>> task = new AsyncTask<Void, Void, ArrayList<PrivateDataBean>>(){
			@Override
			protected ArrayList<PrivateDataBean> doInBackground(Void... params){
				ArrayList<PrivateDataBean> tmp_list = new ArrayList<PrivateDataBean>();
				tmp_list = ContentManager.getInstance(context).getPrivateConversation("0", "0", "20", "0", name, openId);
				
				return tmp_list;
			}
			
			@Override
			protected void onPostExecute(ArrayList<PrivateDataBean> result){
				if(result != null && result.size() > 0){
					privateList.clear();
					privateList.addAll(result);
					adapter.notifyDataSetChanged();
					title.setText(result.get(0).getNick());
				}
			}
		};
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}else{
			task.execute();
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back_button:
			((BaseMultiFragmentActivity) context).backStackAction();
			break;
		}
	}

}
