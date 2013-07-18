package com.halloon.android.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.halloon.android.bean.ProfileBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.DBManager;
import com.halloon.android.listener.OnTitleBarClickListener;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;
import com.halloon.android.widget.HalloonTitleBar;
import com.halloon.android.R;

public class EditProfileFragment extends BaseTitleBarFragment implements OnClickListener,
                                                                         OnTitleBarClickListener{
	
	private Context context;
	
	public static final int EDIT_MODE_NICK = 0;
	public static final int EDIT_MODE_SEX = 1;
	public static final int EDIT_MODE_NAME = 2;
	public static final int EDIT_MODE_ADDRESS = 3;
	public static final int EDIT_MODE_SIGNATURE = 4;
	public static final int EDIT_MODE_INTEREST = 5;
	
	/*
	private String nick;
	private String sex;
	private String name;
	private String address;
	private String signature;
	private String interest;
	*/
	
	private int editState = EDIT_MODE_NICK;
	
	private int[] strings = {R.string.nick, R.string.sex, R.string.name, R.string.address, R.string.signature, R.string.interest};
	private String[] contents = new String[6];
	
	private EditText editContent;
	private Button confirmButton;
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		
		context = activity;
		
		editState = getArguments().getInt("editState");
	}
	@Override
	protected void init(HalloonTitleBar titleBar, RelativeLayout content) {
		titleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_BACK_BUTTON_ONLY);
		
		titleBar.getTitleTextView().setText(getString(strings[editState]));
		titleBar.setOnTitleBarClickListener(this);
		
		ProfileBean profileBean = DBManager.getInstance(getActivity()).getProfile();
		contents[0] = profileBean.getNick();
		contents[1] = profileBean.getSex();
		contents[2] = profileBean.getName();
		contents[3] = profileBean.getLocation();
		contents[4] = profileBean.getIntroduction();
		contents[5] = profileBean.getTag().toString();
		
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content.addView(inflater.inflate(R.layout.fragment_edit_profile, null, false));
		
		editContent = (EditText) content.findViewById(R.id.edit_content);
		editContent.setText(contents[editState]);
		confirmButton = (Button) content.findViewById(R.id.confirm);
		confirmButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v){
		if(editContent.getText().length() > 0){
			AsyncTask<Integer, Void, int[]> task = new AsyncTask<Integer, Void, int[]>(){
				
				@Override
				protected int[] doInBackground(Integer... params) {
					
					switch(params[0]){
					case EDIT_MODE_NICK:
						contents[0] = String.valueOf(editContent.getText());
						break;
					case EDIT_MODE_SEX:
						contents[1] = String.valueOf(editContent.getText());
						break;
					case EDIT_MODE_NAME:
						contents[2] = String.valueOf(editContent.getText());
						break;
					case EDIT_MODE_ADDRESS:
						contents[3] = String.valueOf(editContent.getText());
						break;
					case EDIT_MODE_SIGNATURE:
						contents[4] = String.valueOf(editContent.getText());
						break;
					case EDIT_MODE_INTEREST:
						contents[5] = String.valueOf(editContent.getText());
						break;
					}
					
					return ContentManager.getInstance(getActivity()).updateProfile(contents[0], null, null, null, null, null, null, null, contents[4]);
				}
			};
			
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, editState);
			}else{
				task.execute(editState);
			}
		}
	}
	@Override
	public void onTitleContentClick(int contentEnum) {
		switch(contentEnum){
		case OnTitleBarClickListener.LEFT_BUTTON:
			((BaseMultiFragmentActivity) context).backStackAction();
			break;
		}
	}
	
}
