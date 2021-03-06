package com.halloon.android.ui.fragment;

import java.util.ArrayList;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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
import com.halloon.android.ui.activity.MainPageActivity;
import com.halloon.android.ui.activity.SlideHomeActivity;
import com.halloon.android.ui.fragment.TabMainPageFragment.MainPageFragmentCallback;
import com.halloon.android.ui.fragment.TweetDetailFragment.TweetDetailFragmentCallback;
import com.halloon.android.util.Constants;
import com.halloon.android.util.ContentTransUtil;
import com.halloon.android.util.NumberUtil;
import com.halloon.android.util.PopupWindowManager;
import com.halloon.android.util.TimeUtil;
import com.halloon.android.view.ScrollTextView;
import com.halloon.android.widget.ButtonStyleTextView;
import com.halloon.android.widget.HalloonTitleBar;
import com.halloon.android.widget.TagView;

public class TabProfileFragment1 extends BaseTitleBarFragment implements
		OnClickListener {
	public static final int REQUEST_IMG = 2;

	private ProfileFragmentCallback pCallback;
	private ImageView myHeadicon;
	private TextView myNick;
	private ImageView mySex;
	private TextView mySign;
	private Button editButton;

	// private TagView tagView;

	private Button tweetButton;
	private Button idolButton;
	private Button fanButton;
	private Button favButton;
	private Button tagButton;

	private View tweetMore;

	private Context context;

	private ProfileBean profileBean;

	public static final int ME = 0;
	public static final int OTHER = 1;

	private int type = ME;

	private String name;
	private String id;


	public interface ProfileFragmentCallback {
		public void setupTweetListFragment(Bundle bundle);

		public void setupEditProfileFragment(Bundle bundle);
		
		public void setupDetailFragment(Bundle bundle);
		
		
		public void setupIdolListFragment(String name,String fopenid);
		
		public void setupFansListFragment(String name,String fopenid);
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
	
	LinearLayout linearLayout = null ;
	@Override
	public void init(HalloonTitleBar titleBar, RelativeLayout content) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content.addView(inflater.inflate(R.layout.tab_profile_v1, null, false));

		titleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_NORMAL);
		titleBar.setOnTitleBarClickListener(this);

		titleBar.getTitleTextView().setText(getString(R.string.tab_profile));

		myHeadicon = (ImageView) content.findViewById(R.id.my_headicon);
		myNick = (TextView) content.findViewById(R.id.my_nick);
		mySex = (ImageView) content.findViewById(R.id.my_sex);

		mySign = (ScrollTextView) content.findViewById(R.id.my_sign);
		// tagView = (TagView) content.findViewById(R.id.tag);

		tweetButton = (Button) content.findViewById(R.id.tweet);
		idolButton = (Button) content.findViewById(R.id.idol);
		fanButton = (Button) content.findViewById(R.id.fans);
		favButton = (Button) content.findViewById(R.id.fav);
		tagButton = (Button) content.findViewById(R.id.tag);

		editButton = titleBar.getRightButton(R.string.edit);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, parent, savedInstanceState);
		updateProfile();
		return view ;
	}

	@Override
	public void onResume() {
		super.onResume();
//		updateProfile();
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

		myHeadicon.setOnClickListener(this);

		if (type == ME) {
			mTitleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_HIDE_TITLE);
			View menuButton = mContent.findViewById(R.id.menu);
			menuButton.setVisibility(View.GONE);
			menuButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Activity parent = getActivity().getParent();
					if (parent instanceof SlideHomeActivity) {
						((SlideHomeActivity) parent).toggleSlideMenu();
					}
				}
			});

		} else {
			mTitleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE__BUTTON);
		}

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
						if (tmp_profileBean.getName() == null
								|| tmp_profileBean.getTweetBean() == null) {
							tmp_profileBean = ContentManager.getInstance(
									context).getMyProfile();
						}
					}
					name = tmp_profileBean.getName();
					id = tmp_profileBean.getOpenId();
				} else {
					tmp_profileBean = ContentManager.getInstance(context)
							.getOtherProfile(name, id);
					System.out.println(name + ":" + id);
				}
				publishProgress(1, tmp_profileBean);

//				ArrayList<TweetBean> listTemp = ContentManager.getInstance(context).
//						getMicroAlbum(15, name, 
//						id, "0", "0", "0");
//				System.out.println(name + ":" + id+": "+listTemp);
//				publishProgress(3,listTemp);
//				
				return tmp_profileBean;
			}

			@Override
			protected void onProgressUpdate(Object... values) {
				if (values != null && values.length > 1) {
					int type = (Integer) values[0];
					switch (type) {
					case 1:
						// update profile
						updateProfile((ProfileBean) values[1]);
						break;
					case 2:
						// update profile tweet
//						if (values[1] instanceof ArrayList<?>)
//							showTweetList((ArrayList<TweetBean>) values[1]);
						break;
					case 3:
//						if(values[1] instanceof ArrayList<?>){
//							showMicroAlbum((ArrayList<TweetBean>) values[1]);
//						}
						break;
					default:
						break;
					}
				}
			}
			
			private void showMicroAlbum(ArrayList<TweetBean> list){
				LayoutInflater inflater = getActivity().getLayoutInflater();
				
				linearLayout.removeAllViews();
				for(TweetBean tweetBean : list){
					ImageView iv = new ImageView(context);
					
					int side = getResources().getDimensionPixelOffset(R.dimen.scroll_img_side);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(side, side);
					params.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.padding_small);
					params.topMargin = params.bottomMargin;
					params.leftMargin = params.leftMargin;
					params.rightMargin = params.rightMargin ;
					
					iv.setLayoutParams(params);
					iv.setScaleType(ScaleType.CENTER);
					
					final String imgUrl = tweetBean.getTweetImageStr();
					final TweetBean tempBean = tweetBean ;
					iv.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							Bundle bundle = new Bundle();
							bundle.putString("id", tempBean.getId());
//							bundle.putBundle("tweetBean",
//									tempBean.toBundle());
							pCallback.setupDetailFragment(bundle);
							
						}
					});
					
					System.out.println(""+tweetBean.getTweetImageStr() + "/120");
					ImageLoader.getInstance(context).displayImage(tweetBean.getTweetImageStr() + "/120", 
							iv,0, null,R.drawable.ic_launcher);
					linearLayout.addView(iv);
					
//					params = new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.MATCH_PARENT);
//					params.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.padding_small);
//					params.topMargin = params.bottomMargin;
//					
//					View line = new View(context);
//					line.setLayoutParams(params);
//					line.setBackgroundResource(R.color.line_partition);
//					
//					linearLayout.addView(line);
					
				}
				
//				linearLayout.removeViewAt(linearLayout.getChildCount() - 1);
			}

			private void setListViewHeight(ListView listview) {
				ListAdapter adapter = listview.getAdapter();

				int height = 0;
				for (int i = 0; i != adapter.getCount(); ++i) {
					View item = adapter.getView(i, null, listview);

					item.measure(0, 0);

					height += item.getMeasuredHeight();

				}

				LayoutParams params = listview.getLayoutParams();
				params.height = height + listview.getDividerHeight()
						* (adapter.getCount() - 1);
				listview.setLayoutParams(params);
			}

			private void updateProfile(ProfileBean result) {
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
					int tags = 0;
					if (profileBean.getTag() != null) {
						tags = profileBean.getTag().length;
					}
					tagButton.setText(context.getString(R.string.tag)
							+ "\n"
							+ NumberUtil.shortenNumericString(context, tags
									+ ""));
					// tagView.setContents(profileBean.getTag());

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
			bundle.putString("nick", profileBean.getNick());
			pCallback.setupTweetListFragment(bundle);
			break;
		case R.id.idol:
			if(profileBean == null )
				break;
			pCallback.setupIdolListFragment(profileBean.getNick(), id);
			break;
		case R.id.fans:
			if(profileBean == null )
				break;
			pCallback.setupFansListFragment(profileBean.getNick(), id);
			break;
		case R.id.background:
			PopupWindowManager popupWindowManager = new PopupWindowManager(
					mActivity);
			popupWindowManager.setupImageSelectorPopup(this);

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

	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String imagePath = "";
		if (resultCode == Activity.RESULT_OK) {

			switch (requestCode) {
			case PopupWindowManager.PICK_IMG_1:
				Bundle bundle = data.getExtras();
				final Bitmap photo = (Bitmap) bundle.get("data");
				if (photo != null) {
					Intent intent = new Intent();
					intent.setAction(Constants.GET_IMAGE_PATH);
					imagePath = ImageLoader.imageSave(photo);
				}
				break;
			case PopupWindowManager.PICK_IMG_2:
				try {
					final Uri uri = data.getData();
					if(null != uri){
					  String[] filePathColumn = { MediaStore.Images.Media.DATA };
				        Cursor cursor = getActivity().getContentResolver().query(uri,
				                filePathColumn, null, null, null);
				        cursor.moveToFirst();
				        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				        imagePath = cursor.getString(columnIndex);
				        cursor.close();

					}
					      
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}

			if (imagePath != null && imagePath.length() != 0 ) {
				SettingsManager.getInstance(mActivity).setProfileBackGroundImg(
						imagePath);
			}
		}
	}

}
