package com.halloon.android.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.halloon.android.R;
import com.halloon.android.bean.ProfileBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.DBManager;
import com.halloon.android.data.SettingsManager;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.listener.OnTitleBarClickListener;
import com.halloon.android.task.BaseCompatiableTask;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;
import com.halloon.android.ui.fragment.TabMainPageFragment.MainPageFragmentCallback;
import com.halloon.android.util.NumberUtil;
import com.halloon.android.view.ScrollTextView;
import com.halloon.android.widget.HalloonTitleBar;

public class TabProfileFragment extends BaseTitleBarFragment implements OnClickListener,
                                                                        OnTitleBarClickListener{

	private ProfileFragmentCallback pCallback;
	private ImageView myHeadicon;
	private TextView myNick;
	private TextView myName;
	private ImageView mySex;
	private TextView myAddress;
	private TextView mySign;
	private TextView myInter;
	private TextView tweetNum;
	private TextView fansNum;
	private TextView idolNum;
	private TextView favNum;
	private Button sendButton;
	private Button editButton;
	// private TextView last_tweet;
	private Context context;

	private ProfileBean profileBean;

	public static final int ME = 0;
	public static final int OTHER = 1;

	private int type = ME;

	private String name;
	private String id;
	private String nick;

	public interface ProfileFragmentCallback {
		public void setupTweetListFragment(Bundle bundle);
		public void setupEditProfileFragment(Bundle bundle);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		this.pCallback = (ProfileFragmentCallback) activity;
		
		if (getArguments().getString("name") != null) this.name = getArguments().getString("name");
		if (getArguments().getString("id") != null) this.id = getArguments().getString("id");
	}
	
	@Override
	public void init(HalloonTitleBar titleBar, RelativeLayout content){
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content.addView(inflater.inflate(R.layout.tab_profile, null, false));
		
		titleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_NORMAL);
		titleBar.setOnTitleBarClickListener(this);
		
		titleBar.getTitleTextView().setText(getString(R.string.tab_profile));
		
		myHeadicon = (ImageView) content.findViewById(R.id.my_headicon);
		myNick = (TextView) content.findViewById(R.id.my_nick);
		mySex = (ImageView) content.findViewById(R.id.my_sex);
		myName = (TextView) content.findViewById(R.id.my_name);
		myAddress = (TextView) content.findViewById(R.id.my_address);
		mySign = (ScrollTextView) content.findViewById(R.id.my_sign);
		myInter = (TextView) content.findViewById(R.id.my_inter);
		tweetNum = (TextView) content.findViewById(R.id.tweet_num);
		fansNum = (TextView) content.findViewById(R.id.fans_num);
		idolNum = (TextView) content.findViewById(R.id.idol_num);
		favNum = (TextView) content.findViewById(R.id.fav_num);
		sendButton = (Button) content.findViewById(R.id.send_message_button);
		editButton = titleBar.getRightButton(R.string.edit);
		
		updateProfile();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();

		myNick.setClickable(true);
		mySex.setClickable(true);
		myName.setClickable(true);
		myAddress.setClickable(true);
		mySign.setClickable(true);
		myInter.setClickable(true);
		tweetNum.setClickable(true);
		
		myNick.setOnClickListener(this);
		mySex.setOnClickListener(this);
		myName.setOnClickListener(this);
		myAddress.setOnClickListener(this);
		mySign.setOnClickListener(this);
		myInter.setOnClickListener(this);
		tweetNum.setOnClickListener(this);
		editButton.setOnClickListener(this);
		sendButton.setOnClickListener(this);
		if (type == OTHER) {
			mTitleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_NORMAL);
			myHeadicon.setOnClickListener(this);
			if(!name.equals(DBManager.getInstance(context).getProfile().getName())){
				sendButton.setText("发消息");
				editButton.setText(context.getString(R.string.idol));
			}else{
				sendButton.setText("发现");
				editButton.setText(context.getString(R.string.edit));
			}
		} else {
			mTitleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_RIGHT_BUTTON_ONLY);
			sendButton.setText("发现");
			editButton.setText(context.getString(R.string.edit));
		}
	}

	public void updateProfile() {
		new BaseCompatiableTask<Void, Void, ProfileBean>() {
			
			@Override
			protected ProfileBean doInBackground(Void... arg0) {
				ProfileBean tmp_profileBean;

				if (type == ME) {
					if (SettingsManager.getInstance(context).getProfileStatus() == DBManager.PROFILE_STATUS_INIT) {
						tmp_profileBean = ContentManager.getInstance(context).getMyProfile();
					} else {
						if(DBManager.getInstance(context).getProfile().getName() != null){
							tmp_profileBean = DBManager.getInstance(context).getProfile();
						}else{
							tmp_profileBean = ContentManager.getInstance(context).getMyProfile();
						}
					}
					
				} else {
					tmp_profileBean = ContentManager.getInstance(context).getOtherProfile(null, id);
					System.out.println(name + ":" + id);
				}

				return tmp_profileBean;
			}

			@Override
			protected void onPostExecute(ProfileBean result) {

				if(result != null){
					profileBean = result;
					if(type == ME) DBManager.getInstance(context).upgradeProfile(profileBean);
					ImageLoader.getInstance(context).displayImage(profileBean.getHead() + "/100", myHeadicon, 12);
					myNick.setText(profileBean.getNick());
					if(profileBean.getSex() != null){
						if (profileBean.getSex().equals("男")) {
							mySex.setVisibility(View.VISIBLE);
							mySex.setImageResource(R.drawable.wb_icon_male);
						} else if (profileBean.getSex().equals("女")) {
							mySex.setVisibility(View.VISIBLE);
							mySex.setImageResource(R.drawable.wb_icon_female);
						} else {
							mySex.setVisibility(View.GONE);
						}
					}
					myName.setText(context.getString(R.string.account) + ":" + profileBean.getName());
					myAddress.setText(profileBean.getLocation());
					mySign.setText(profileBean.getIntroduction());
					myInter.setText(profileBean.getTag());

					tweetNum.setText(NumberUtil.shortenNumericString(context, profileBean.getTweetNum()));
					fansNum.setText(NumberUtil.shortenNumericString(context, profileBean.getFansNum()));
					idolNum.setText(NumberUtil.shortenNumericString(context, profileBean.getIdolNum()));
					favNum.setText(NumberUtil.shortenNumericString(context, profileBean.getFavNum()));
					// last_tweet.append(profileBean.getTweetBean().getText());

				}
				super.onPostExecute(result);
			}
		}.taskExecute();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tweet_num:
			Bundle bundle = new Bundle();
			bundle.putString("name", name);
			bundle.putString("nick", nick);
			pCallback.setupTweetListFragment(bundle);
			break;
		case R.id.send_message_button:
			break;
		case R.id.my_headicon:
			try{
				((MainPageFragmentCallback) context).setupPictureDialog( profileBean.getHead(), "/100", v.getDrawingCache());
			}catch(NullPointerException e){
				e.printStackTrace();
			}
			break;
		default:
		    editProfile(v.getId());
		    break;
		}
	}
	
	private void editProfile(int id){
		if(type == ME || name.equals(DBManager.getInstance(context).getProfile().getName())){
			Bundle bundle = new Bundle();
			switch(id){
			case R.id.my_nick:
				bundle.putInt("editState", EditProfileFragment.EDIT_MODE_NICK);
				break;
			case R.id.my_sex:
				bundle.putInt("editState", EditProfileFragment.EDIT_MODE_SEX);
				break;
			case R.id.my_name:
				bundle.putInt("editState", EditProfileFragment.EDIT_MODE_NAME);
				break;
			case R.id.my_address:
				bundle.putInt("editState", EditProfileFragment.EDIT_MODE_ADDRESS);
				break;
			case R.id.my_sign:
				bundle.putInt("editState", EditProfileFragment.EDIT_MODE_SIGNATURE);
				break;
			case R.id.my_inter:
				bundle.putInt("editState", EditProfileFragment.EDIT_MODE_INTEREST);
				break;
			}
			
			pCallback.setupEditProfileFragment(bundle);
		}
	}

	public void setType(int type) {
		this.type = type;
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
