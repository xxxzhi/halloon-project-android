package com.halloon.android.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.halloon.android.R;
import com.halloon.android.bean.ProfileBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.DBManager;
import com.halloon.android.listener.OnTitleBarClickListener;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;
import com.halloon.android.widget.HalloonTitleBar;

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
		titleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_EDIT);
		
		titleBar.getTitleTextView().setText(getString(strings[editState]));
		titleBar.setOnTitleBarClickListener(this);
		((Button)titleBar.findViewById(R.id.right_button)).setText(R.string.save);
		ProfileBean profileBean = DBManager.getInstance(getActivity()).getProfile();
		if(profileBean != null){
			contents[0] = profileBean.getNick();
			contents[1] = profileBean.getSex();
			contents[2] = profileBean.getName();
			contents[3] = profileBean.getLocation();
			contents[4] = profileBean.getIntroduction();
			if(profileBean.getTag() == null){
				contents[5] = "";
			}else{
				
				contents[5] = profileBean.getTag().toString();
			}
		}
		
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content.addView(inflater.inflate(R.layout.fragment_edit_profile, null, false));
		
		editContent = (EditText) content.findViewById(R.id.edit_content);
		editContent.setText(contents[editState]);
		confirmButton = (Button) content.findViewById(R.id.confirm);
		confirmButton.setOnClickListener(this);
		
		TextView tvHint = (TextView) content.findViewById(R.id.tv_input_hint);
		switch (editState) {
		case EDIT_MODE_NICK:
			tvHint.setHint(R.string.input_name_hint);
			break;
		case EDIT_MODE_SEX:
			break;
		case EDIT_MODE_NAME:
			break;
		case EDIT_MODE_ADDRESS:
			break;
		case EDIT_MODE_SIGNATURE:
			tvHint.setHint(R.string.input_sign_hint);
			break;
		case EDIT_MODE_INTEREST:
			break;
		}
	}
	
	@Override
	public void onClick(View v){
		if(editContent.getText().length() > 0){
			AsyncTask<Integer, Void, int[]> task = new AsyncTask<Integer, Void, int[]>(){
				ProgressDialog dialog = new ProgressDialog(mActivity);
				@Override
				protected void onPostExecute(int[] result) {
					super.onPostExecute(result);
					if(result[0] != 0){
						Toast.makeText(mActivity, "error "+result[0], Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(mActivity, R.string.save_success, Toast.LENGTH_SHORT).show();
					}
					dialog.dismiss();
				}

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					dialog.show();
				}

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
		case OnTitleBarClickListener.RIGHT_BUTTON:
			onClick(null);
			break;
		}
	}
	
}
