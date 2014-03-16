package com.halloon.android.ui.fragment;

import java.util.ArrayList;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.halloon.android.HalloonApplication;
import com.halloon.android.R;
import com.halloon.android.adapter.TweetContentAdapter;
import com.halloon.android.bean.ProfileBean;
import com.halloon.android.bean.TweetBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.DBManager;
import com.halloon.android.data.SettingsManager;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.listener.OnTitleBarClickListener;
import com.halloon.android.task.BaseCompatiableTask;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;
import com.halloon.android.ui.fragment.TabMainPageFragment.MainPageFragmentCallback;
import com.halloon.android.ui.fragment.TweetDetailFragment.TweetDetailFragmentCallback;
import com.halloon.android.util.ContentTransUtil;
import com.halloon.android.util.NumberUtil;
import com.halloon.android.util.TimeUtil;
import com.halloon.android.view.ScrollTextView;
import com.halloon.android.widget.ButtonStyleTextView;
import com.halloon.android.widget.HalloonTitleBar;
import com.halloon.android.widget.TagView;

public class TabProfileFragment extends BaseTitleBarFragment implements
		OnClickListener {

	static final class ProfileTweetViewHolder {
		ImageView headImage;
		TextView title;
		ImageView isVip;
		TextView timestamp;
		TextView from;
		TextView commentCount;
		TextView forwardCount;
		ButtonStyleTextView tweetContent;
		TextView tweetLocationText;
		ImageView tweetImage;
		ButtonStyleTextView forwardContent;
		TextView forwardLocationText;
		ImageView forwardImage;
		ImageView hasImage;
		RelativeLayout sourceLayout;
		RelativeLayout parent ; 
	}

	private ProfileFragmentCallback pCallback;
	private ImageView myHeadicon;
	private TextView myNick;
	private ImageView mySex;
	private TextView mySign;
	private Button editButton;
//	private TagView tagView;

	private Button tweetButton;
	private Button idolButton;
	private Button fanButton;
	private Button favButton;
	private Button tagButton;
	
private View tweetMore ;
	
	private Context context;

	private ProfileBean profileBean;

	public static final int ME = 0;
	public static final int OTHER = 1;

	private int type = ME;

	private String name;
	private String id;
	private String nick;
	
	private LinearLayout tweetLinear = null ;
	public interface ProfileFragmentCallback {
		public void setupTweetListFragment(Bundle bundle);

		public void setupEditProfileFragment(Bundle bundle);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		this.pCallback = (ProfileFragmentCallback) activity;

		if (getArguments().getString("name") != null)
			this.name = getArguments().getString("name");
		if (getArguments().getString("id") != null)
			this.id = getArguments().getString("id");
	}

	@Override
	public void init(HalloonTitleBar titleBar, RelativeLayout content) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content.addView(inflater.inflate(R.layout.tab_profile_v2, null, false));

		titleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_NORMAL);
		titleBar.setOnTitleBarClickListener(this);

		titleBar.getTitleTextView().setText(getString(R.string.tab_profile));

		myHeadicon = (ImageView) content.findViewById(R.id.my_headicon);
		myNick = (TextView) content.findViewById(R.id.my_nick);
		mySex = (ImageView) content.findViewById(R.id.my_sex);

		mySign = (ScrollTextView) content.findViewById(R.id.my_sign);
//		tagView = (TagView) content.findViewById(R.id.tag);

		tweetButton = (Button) content.findViewById(R.id.tweet);
		idolButton = (Button) content.findViewById(R.id.idol);
		fanButton = (Button) content.findViewById(R.id.fans);
		favButton = (Button) content.findViewById(R.id.fav);
		tagButton = (Button) content.findViewById(R.id.tag);
		
		editButton = titleBar.getRightButton(R.string.edit);

		
		//tweet list
//		tweetListView = (ListView) content.findViewById(R.id.listview_profile_tweet);
		tweetLinear = (LinearLayout) content.findViewById(R.id.linear_tweet);
		tweetMore = content.findViewById(R.id.tv_more);
//		content.findViewById(R.id.tweet_head).setVisibility(View.GONE);
//
//		tweetViewHolder.headImage = (ImageView) content
//				.findViewById(R.id.tweet_head);
//		tweetViewHolder.title = (TextView) content
//				.findViewById(R.id.tweet_title);
//		tweetViewHolder.isVip = (ImageView) content.findViewById(R.id.is_vip);
//		tweetViewHolder.timestamp = (TextView) content
//				.findViewById(R.id.tweet_timestamp);
//		tweetViewHolder.from = (TextView) content.findViewById(R.id.tweet_from);
//		tweetViewHolder.commentCount = (TextView) content
//				.findViewById(R.id.comment_count);
//		tweetViewHolder.forwardCount = (TextView) content
//				.findViewById(R.id.forward_count);
//		tweetViewHolder.tweetContent = (ButtonStyleTextView) content
//				.findViewById(R.id.tweet_content);
//		tweetViewHolder.tweetLocationText = (TextView) content
//				.findViewById(R.id.tweet_location_text);
//		tweetViewHolder.tweetImage = (ImageView) content
//				.findViewById(R.id.tweet_image);
//		tweetViewHolder.forwardContent = (ButtonStyleTextView) content
//				.findViewById(R.id.forward_content);
//		tweetViewHolder.forwardLocationText = (TextView) content
//				.findViewById(R.id.forward_location_text);
//		tweetViewHolder.forwardImage = (ImageView) content
//				.findViewById(R.id.forward_image);
//		tweetViewHolder.hasImage = (ImageView) content
//				.findViewById(R.id.image_icon);
//		tweetViewHolder.sourceLayout = (RelativeLayout) content
//				.findViewById(R.id.relativeLayout1);
//		tweetViewHolder.parent = (RelativeLayout) content.findViewById(R.id.parent);
	}

	private ProfileTweetViewHolder tweetViewHolder = new ProfileTweetViewHolder();

	@Override
	public void onResume() {
		super.onResume();
		updateProfile();
		Log.i("test", "onResume");
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		updateProfile();
		Log.i("test", "onHiddenChanged");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();

		myNick.setClickable(true);
		mySex.setClickable(true);
		mySign.setClickable(true);

		myNick.setOnClickListener(this);
		mySex.setOnClickListener(this);
		mySign.setOnClickListener(this);
		tweetButton.setOnClickListener(this);
		idolButton.setOnClickListener(this);
		fanButton.setOnClickListener(this);
		favButton.setOnClickListener(this);
		tagButton.setOnClickListener(this);
		
		tweetMore.setOnClickListener(this);
		myHeadicon.setOnClickListener(this);

		if (type == ME) {
			mTitleBar
					.setTitleStyle(HalloonTitleBar.TITLE_STYLE_RIGHT_BUTTON_ONLY);
		} else {
			mTitleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_NORMAL);
		}

//		/* tweet */
//		OnClickListener tweetListClickListener = new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				try {
//					switch (v.getId()) {
//					case R.id.tweet_head:
//						Bundle bundle = new Bundle();
//						bundle.putString("name", profileBean.getTweetBean()
//								.getName());
//						bundle.putString("id", profileBean.getTweetBean()
//								.getOpenId());
//						((TweetDetailFragmentCallback) context)
//								.setupProfileFragment(bundle);
//						break;
//					case R.id.tweet_image:
//						if (profileBean.getTweetBean().getTweetImage() != null
//								&& profileBean.getTweetBean().getTweetImage()
//										.length() > 0)
//							((MainPageFragmentCallback) context)
//									.setupPictureDialog(profileBean
//											.getTweetBean().getTweetImage()
//											.getString(0), "/2000",
//											v.getDrawingCache());
//						if (profileBean.getTweetBean().getVideoImage() != null) {
//							Intent intent = new Intent();
//							intent.setAction("android.intent.action.VIEW");
//							Uri uri = Uri.parse(profileBean.getTweetBean()
//									.getVideoUrl());
//							intent.setData(uri);
//							context.startActivity(intent);
//						}
//						break;
//					case R.id.forward_image:
//						if (profileBean.getTweetBean().getSource()
//								.getTweetImage() != null
//								&& profileBean.getTweetBean().getSource()
//										.getTweetImage().length() > 0)
//							((MainPageFragmentCallback) context)
//									.setupPictureDialog(profileBean
//											.getTweetBean().getSource()
//											.getTweetImage().getString(0),
//											"/2000", v.getDrawingCache());
//						if (profileBean.getTweetBean().getSource()
//								.getVideoImage() != null) {
//							Intent intent = new Intent();
//							intent.setAction("android.intent.action.VIEW");
//							Uri uri = Uri.parse(profileBean.getTweetBean()
//									.getSource().getVideoUrl());
//							intent.setData(uri);
//							context.startActivity(intent);
//						}
//						break;
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		};
//
//		tweetViewHolder.headImage.setOnClickListener(tweetListClickListener);
//		tweetViewHolder.tweetImage.setOnClickListener(tweetListClickListener);
//		tweetViewHolder.forwardImage.setOnClickListener(tweetListClickListener);
//
//		tweetViewHolder.sourceLayout.setVisibility(View.GONE);

	}

	public void updateProfile() {
		new BaseCompatiableTask<Void, Object, ProfileBean>() {

			@Override
			protected ProfileBean doInBackground(Void... arg0) {
				ProfileBean tmp_profileBean;

				if (type == ME) {
					if (SettingsManager.getInstance(context).getProfileStatus() == DBManager.PROFILE_STATUS_INIT) {
						tmp_profileBean = ContentManager.getInstance(context)
								.getMyProfile();
					} else {
						tmp_profileBean = DBManager.getInstance(context)
								.getProfile();
						if (tmp_profileBean
								.getName() == null || tmp_profileBean.getTweetBean() == null) {
							tmp_profileBean = ContentManager.getInstance(
									context).getMyProfile();
						}
					}

				} else {
					tmp_profileBean = ContentManager.getInstance(context)
							.getOtherProfile(name, id);
					System.out.println(name + ":" + id);
				}
				publishProgress(1,tmp_profileBean);
				if(tweetLinear.getChildCount() !=3){
					ArrayList<TweetBean> tmpArrayList = ContentManager.getInstance(context)
							.getOtherTimeLine("", "", 3, "",
									name, null, "", "");
					if(tmpArrayList != null ){
						publishProgress(2,tmpArrayList);
					}
				}
				
				return tmp_profileBean;
			}
			
			@Override
			protected void onProgressUpdate(Object... values) {
				if(values != null && values.length >1 ){
					int type = (Integer)values[0];
					switch (type) {
					case 1:
						//update profile
						updateProfile((ProfileBean)values[1]);
						break;
					case 2:
						//update profile tweet
						if(values[1] instanceof ArrayList<?>)
							showTweetList((ArrayList<TweetBean>)values[1]);
						break;
					default:
						break;
					}
				}
			}
			
			private void showTweetList(ArrayList<TweetBean> list){
				
				TweetContentAdapter tweetContentAdapter;
				tweetContentAdapter = new TweetContentAdapter(context, list);
//				tweetListView.setAdapter(tweetContentAdapter);
				
				
				//repair tweetlist height ;
				
				
				for(int i = 0 ; i!= tweetContentAdapter.getCount() ; ++ i){
					View item = tweetContentAdapter.getView(i, null, tweetLinear);
					
					tweetLinear.addView(item);
					
				}
				if(tweetContentAdapter.getCount()==3){
					tweetMore.setVisibility(View.VISIBLE);
				}
//				LayoutParams params = listview.getLayoutParams() ;
//				params.height = height + listview.getDividerHeight() * (adapter.getCount()-1) ;
//				listview.setLayoutParams(params);
//				setListViewHeight(tweetListView);
			}
			
			
			
			private void setListViewHeight(ListView listview){
				ListAdapter adapter = listview.getAdapter();
				
				int height = 0 ; 
				for(int i = 0 ; i!= adapter.getCount() ; ++ i){
					View item = adapter.getView(i, null, listview);
					
					item.measure(0, 0);
					
					height += item.getMeasuredHeight();
					
				}
				
				LayoutParams params = listview.getLayoutParams() ;
				params.height = height + listview.getDividerHeight() * (adapter.getCount()-1) ;
				listview.setLayoutParams(params);
			}
			
			
			private void updateProfile(ProfileBean result){
				if (result != null) {
					profileBean = result;
					if (type == ME) {
						DBManager.getInstance(context).upgradeProfile(
								profileBean);
					} else {
						mTitleBar.getTitleTextView().setText(
								profileBean.getNick());
					}
					ImageLoader.getInstance(context)
							.displayImage(profileBean.getHead() + "/100",
									myHeadicon, 1, null);
					myNick.setText(profileBean.getNick());
					if (profileBean.getSex() != null) {
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
					mySign.setText(profileBean.getIntroduction());
					tweetButton.setText(context.getString(R.string.wblog)
							+ "\n"
							+ NumberUtil.shortenNumericString(context,
									profileBean.getTweetNum()));
					idolButton.setText(context.getString(R.string.idol)
							+ "\n"
							+ NumberUtil.shortenNumericString(context,
									profileBean.getIdolNum()));
					fanButton.setText(context.getString(R.string.fan)
							+ "\n"
							+ NumberUtil.shortenNumericString(context,
									profileBean.getFansNum()));
					favButton.setText(context.getString(R.string.fav)
							+ "\n"
							+ NumberUtil.shortenNumericString(context,
									profileBean.getFavNum()));
					tagButton.setText(context.getString(R.string.tag)
							+ "\n"
							+ NumberUtil.shortenNumericString(context,
									profileBean.getTag().length+""));
//					tagView.setContents(profileBean.getTag());

					if (type == OTHER) {
						if (!result.isSelf()) {
							if (result.isMyIdol()) {
								editButton.setText("取消");
							} else {
								editButton.setText(context
										.getString(R.string.idol));
							}
						} else {
							editButton
									.setText(context.getString(R.string.edit));
						}
					}
				}
			}
			
			@Override
			protected void onPostExecute(ProfileBean result) {

				
//					if (profileBean.getTweetBean() != null) {
//						tweetViewHolder.sourceLayout
//								.setVisibility(View.VISIBLE);
//						/* tweet init */
//						HalloonApplication application = (HalloonApplication) context
//								.getApplicationContext();
//						ImageLoader.getInstance(context).displayImage(
//								profileBean.getTweetBean().getHead() + "/100",
//								tweetViewHolder.headImage, 0, null);
//						tweetViewHolder.title.setText(profileBean
//								.getTweetBean().getNick());
//						if (profileBean.getTweetBean().getIsVip() == 1) {
//							tweetViewHolder.isVip.setVisibility(View.VISIBLE);
//						} else {
//							tweetViewHolder.isVip.setVisibility(View.GONE);
//						}
//						tweetViewHolder.timestamp.setText(TimeUtil.converTime(
//								profileBean.getTweetBean().getTimestamp(), 1));
//						tweetViewHolder.from.setText(context
//								.getString(R.string.from)
//								+ profileBean.getTweetBean().getFrom());
//						tweetViewHolder.commentCount.setText(profileBean
//								.getTweetBean().getMCount());
//						tweetViewHolder.forwardCount.setText(profileBean
//								.getTweetBean().getCount());
//						if (profileBean.getTweetBean().getGeo() != null
//								&& profileBean.getTweetBean().getGeo().length() > 0) {
//							tweetViewHolder.tweetLocationText
//									.setVisibility(View.VISIBLE);
//							tweetViewHolder.tweetLocationText.setText(context
//									.getString(R.string.i_am)
//									+ profileBean.getTweetBean().getGeo());
//						} else {
//							tweetViewHolder.tweetLocationText
//									.setVisibility(View.GONE);
//						}
//						if (profileBean.getTweetBean().getSource() != null
//								&& profileBean.getTweetBean().getText()
//										.length() == 0) {
//							tweetViewHolder.tweetContent.setText(context
//									.getString(R.string.re_tweet));
//						} else {
//							ContentTransUtil.getInstance(context)
//									.displaySpannableString(
//											profileBean.getTweetBean()
//													.getText(),
//											tweetViewHolder.tweetContent,
//											profileBean.getTweetBean(), false,
//											true);
//						}
//
//						if (profileBean.getTweetBean().getTweetImage() != null) {
//							tweetViewHolder.hasImage
//									.setVisibility(View.VISIBLE);
//							if (application.getIsMainPageImageMode()) {
//								tweetViewHolder.tweetImage
//										.setVisibility(View.VISIBLE);
//								try {
//									ImageLoader.getInstance(context)
//											.displayImage(
//													profileBean.getTweetBean()
//															.getTweetImage()
//															.getString(0)
//															+ "/120",
//													tweetViewHolder.tweetImage,
//													0, null);
//								} catch (JSONException e) {
//									e.printStackTrace();
//								}
//							} else {
//								tweetViewHolder.tweetImage
//										.setVisibility(View.GONE);
//							}
//						} else if (profileBean.getTweetBean().getVideoImage() != null) {
//							tweetViewHolder.hasImage.setVisibility(View.GONE);
//							if (application.getIsMainPageImageMode()) {
//								tweetViewHolder.tweetImage
//										.setVisibility(View.VISIBLE);
//								ImageLoader.getInstance(context).displayImage(
//										profileBean.getTweetBean()
//												.getVideoImage(),
//										tweetViewHolder.tweetImage, 0, null);
//							} else {
//								tweetViewHolder.tweetImage
//										.setVisibility(View.GONE);
//							}
//						} else {
//							tweetViewHolder.tweetImage.setVisibility(View.GONE);
//							tweetViewHolder.hasImage.setVisibility(View.GONE);
//						}
//
//						// 是否转发
//						if (profileBean.getTweetBean().getSource() != null) {
//							tweetViewHolder.sourceLayout
//									.setVisibility(View.VISIBLE);
//							String tmp_source_text = profileBean.getTweetBean()
//									.getSource().getText();
//							if (profileBean.getTweetBean().getSource().getGeo() != null
//									&& profileBean.getTweetBean().getSource()
//											.getGeo().length() > 0) {
//								tweetViewHolder.forwardLocationText
//										.setVisibility(View.VISIBLE);
//								tweetViewHolder.forwardLocationText
//										.setText(context
//												.getString(R.string.i_am)
//												+ profileBean.getTweetBean()
//														.getSource().getGeo());
//							} else {
//								tweetViewHolder.forwardLocationText
//										.setVisibility(View.GONE);
//							}
//							ContentTransUtil.getInstance(context)
//									.displaySpannableString(tmp_source_text,
//											tweetViewHolder.forwardContent,
//											profileBean.getTweetBean(), true,
//											true);
//							if (profileBean.getTweetBean().getSource()
//									.getTweetImage() != null
//									&& application.getIsMainPageImageMode()) {
//								tweetViewHolder.forwardImage
//										.setVisibility(View.VISIBLE);
//								try {
//									ImageLoader
//											.getInstance(context)
//											.displayImage(
//													profileBean.getTweetBean()
//															.getSource()
//															.getTweetImage()
//															.getString(0)
//															+ "/120",
//													tweetViewHolder.forwardImage,
//													1, null);
//								} catch (JSONException e) {
//									e.printStackTrace();
//								}
//							} else if (profileBean.getTweetBean().getSource()
//									.getVideoImage() != null
//									&& ((HalloonApplication) getActivity()
//											.getApplication())
//											.getIsMainPageImageMode()) {
//								tweetViewHolder.forwardImage
//										.setVisibility(View.VISIBLE);
//								ImageLoader.getInstance(context).displayImage(
//										profileBean.getTweetBean().getSource()
//												.getVideoImage(),
//										tweetViewHolder.forwardImage, 1, null);
//							} else {
//								tweetViewHolder.forwardImage
//										.setVisibility(View.GONE);
//							}
//						} else {
//							tweetViewHolder.sourceLayout
//									.setVisibility(View.GONE);
//						}
//
//					}else{
//						
//					}
				super.onPostExecute(result);
			}
		}.taskExecute();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tweet_num:
			break;
		case R.id.send_message_button:
			break;
		case R.id.my_headicon:
			try {
				((MainPageFragmentCallback) context).setupPictureDialog(
						profileBean.getHead(), "/100", v.getDrawingCache());
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			break;
		case R.id.tv_more:
		case R.id.tweet:
			Bundle bundle = new Bundle();
			bundle.putString("name", name);
			bundle.putString("nick", nick);
			pCallback.setupTweetListFragment(bundle);
			break;
		case R.id.idol:
			break;
		case R.id.fans:
			break;
		case R.id.fav:
			break;
		case R.id.tag:
		default:
			editProfile(v.getId());
			break;
		}
	}

	private void editProfile(int id) {
		if (type == ME
				|| name.equals(DBManager.getInstance(context).getProfile()
						.getName())) {
			Bundle bundle = new Bundle();
			switch (id) {
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
				bundle.putInt("editState",
						EditProfileFragment.EDIT_MODE_ADDRESS);
				break;
			case R.id.my_sign:
				bundle.putInt("editState",
						EditProfileFragment.EDIT_MODE_SIGNATURE);
				break;
			case R.id.my_inter:
				bundle.putInt("editState",
						EditProfileFragment.EDIT_MODE_INTEREST);
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
		Log.d("TATATAG", "IDOLING!!!!!!!");
		switch (contentEnum) {
		case OnTitleBarClickListener.LEFT_BUTTON:
			((BaseMultiFragmentActivity) context).backStackAction();
			break;
		case OnTitleBarClickListener.RIGHT_BUTTON:

			if (!profileBean.isSelf()) {
				if (profileBean != null) {
					if (!profileBean.isMyIdol()) {

						new BaseCompatiableTask<Void, Void, int[]>() {

							@Override
							protected int[] doInBackground(Void... params) {
								return ContentManager.getInstance(context)
										.addIdol(name, id);
							}

							@Override
							protected void onPostExecute(int[] result) {
								if (result[0] != 0) {
									Toast.makeText(
											context,
											context.getString(R.string.idol)
													+ context
															.getString(R.string.failure),
											Toast.LENGTH_LONG).show();
								} else {
									profileBean.setIsMyIdol(1);
									editButton.setText(context
											.getString(R.string.cancel));
									Toast.makeText(
											context,
											context.getString(R.string.idol)
													+ context
															.getString(R.string.success),
											Toast.LENGTH_LONG).show();
								}
							}
						}.taskExecute();
					} else {
						new BaseCompatiableTask<Void, Void, int[]>() {

							@Override
							protected int[] doInBackground(Void... params) {
								return ContentManager.getInstance(context)
										.delIdol(name, id);
							}

							@Override
							protected void onPostExecute(int[] result) {
								if (result[0] != 0) {
									Toast.makeText(
											context,
											context.getString(R.string.cancel)
													+ context
															.getString(R.string.idol)
													+ context
															.getString(R.string.failure),
											Toast.LENGTH_LONG).show();
								} else {
									profileBean.setIsMyIdol(0);
									editButton.setText(context
											.getString(R.string.idol));
									Toast.makeText(
											context,
											context.getString(R.string.cancel)
													+ context
															.getString(R.string.idol)
													+ context
															.getString(R.string.success),
											Toast.LENGTH_LONG).show();
								}
							}
						}.taskExecute();
					}
				}
			}
			break;
		}
	}

}
