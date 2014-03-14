package com.halloon.android.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ant.liao.GifAction;
import com.ant.liao.GifDecoder;
import com.halloon.android.R;
import com.halloon.android.bean.UserBean;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.DBManager;
import com.halloon.android.data.SettingsManager;
import com.halloon.android.image.ImageLoader;
import com.halloon.android.image.TypedBitmap;
import com.halloon.android.image.Utils;
import com.halloon.android.image.ImageLoader.OnProcessListener;
import com.halloon.android.task.ImageLoadTask;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;
import com.halloon.android.ui.fragment.CommentFragment;
import com.halloon.android.ui.fragment.TabMainPageFragment.MainPageFragmentCallback;
import com.halloon.android.widget.HalloonProgressBar;
import com.halloon.android.widget.HalloonImageView;

public class PopupWindowManager {
	private Context context;

	private PopupWindow popupWindow;
	private ViewGroup container;

	// forPicturePopupOnly
	private HalloonImageView picturePopup;
	//private ProgressBar progressBar;
	private HalloonProgressBar progressBar;
	private ImageLoadTask picturePopupTask;
	//private ArrayList<Integer> imageContainer;
	private OnProcessListener onProcessListener;
	private GifDecoder gifDecoder;
	
	private MyHandler mHandler = new MyHandler(context);

	public static int TWEET_LIST = 0;
	public static int TWEET_CONTENT = 1;

	public PopupWindowManager(Context context) {
		this.context = context;
	}
	
	private static class MyHandler extends Handler{
		WeakReference<Activity> mContext;
		
		public MyHandler(Context context){
			this.mContext = new WeakReference<Activity>((Activity) context);
		}
		
		@Override
		public void handleMessage(Message msg){
			Activity context = mContext.get();
			
			switch(msg.what){
			case 0:
				Toast.makeText(context, context.getString(R.string.delete_failure), Toast.LENGTH_LONG).show();
				break;
			case 1:
				Toast.makeText(context, context.getString(R.string.delete_failure), Toast.LENGTH_LONG).show();
				break;
			case 2:
				Toast.makeText(context, context.getString(R.string.fav) + context.getString(R.string.success), Toast.LENGTH_LONG).show();
				break;
			case 3:
				Toast.makeText(context, context.getString(R.string.fav) + context.getString(R.string.failure), Toast.LENGTH_LONG).show();
				break;
			}
		    super.handleMessage(msg);
		}
	}
	
	public void setupImageSelectorPopup(){
		 new AlertDialog.Builder(context)
		                .setItems(new String[]{context.getString(R.string.take_a_shot), context.getString(R.string.find_in_album)}, new DialogInterface.OnClickListener(){
		                	@Override
		    				public void onClick(DialogInterface dialog, int which) {
		    					Intent intent;
		    					switch(which){
		    					case 0:
		    						intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		    						((Activity) context).startActivityForResult(intent, 1);
		    						break;
		    					case 1:
		    						intent = new Intent(Intent.ACTION_GET_CONTENT);
		    						intent.setType("image/*");
		    						((Activity) context).startActivityForResult(intent, 2);
		    					}
		    				}
		             	  
		               })
		               .create()
		               .show(); 
	}

	public void setupPicturePopup(final String addr, final String size, Bitmap bitmap) {
		container = (ViewGroup) ((Activity) context).getLayoutInflater().inflate(R.layout.popup_picture, null);
		OnClickListener buttonClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.back_button:
					popupWindowDismiss();
					break;
				case R.id.popup_image:
					popupWindowDismiss();
					break;
				case R.id.save_button:
					try {
						if (ImageLoader.imageSave(picturePopupTask.get()) != null) {
							Toast.makeText(context, context.getString(R.string.saved), Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(context, context.getString(R.string.could_not_save_check_sdcard), Toast.LENGTH_LONG).show();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		};
		
		
		container.findViewById(R.id.back_button).setOnClickListener(buttonClickListener);
		container.findViewById(R.id.save_button).setOnClickListener(buttonClickListener);
		popupWindow = new PopupWindow(container, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(-00000));
		popupWindow.setAnimationStyle(R.style.PopupWindowFade);
		popupWindow.showAtLocation(((Activity) context).findViewById(R.id.title_layout), Gravity.NO_GRAVITY, 0, SettingsManager.getInstance(context).getSystemBarHeight());

		picturePopup = (HalloonImageView) container.findViewById(R.id.popup_image);
		picturePopup.setMode(HalloonImageView.GESTURE_ENABLE|HalloonImageView.PLAY_GIF);
		progressBar = (HalloonProgressBar) container.findViewById(R.id.progress_bar);
		
		/*
		onProcessListener = new OnProcessListener(){

			@Override
			public void onProcessStarted() {
				((Activity) context).runOnUiThread(new LoadStartedRunnable(-1));
			}
			
			@Override
			public void onImageTypeGot(int type){
				
			}

			@Override
			public void onProcess(float f) {
				((Activity) context).runOnUiThread(new LoadProcessingRunnable(f));
			}
			
			@Override
			public void onProcessEnded(TypedBitmap bitmap){
				
				((Activity) context).runOnUiThread(new LoadEndedRunnable(bitmap));
			}
			
		};
		
		ImageLoader.getInstance(context).displayImage(addr + size, picturePopup, 0, onProcessListener);
		 */
		
		picturePopupTask = new ImageLoadTask(context, picturePopup, progressBar);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			picturePopupTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, addr + size);
		} else {
			picturePopupTask.execute(addr + size);
		}
		
	}
	
	private class LoadStartedRunnable implements Runnable{
		
		private int type;
		
		public LoadStartedRunnable(int type){
			this.type = type;
		}
		
		@Override
		public void run(){
			picturePopup.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.VISIBLE);
		}
	}
	
	private class LoadProcessingRunnable implements Runnable{
		private float progress;
		
		public LoadProcessingRunnable(float progress){
			this.progress = progress;
		}
		
		@Override
		public void run(){
			progressBar.setProgress(Math.round(progress * 100));
		}
	}
	
	private class LoadEndedRunnable implements Runnable{
		private TypedBitmap bitmap;
		
		public LoadEndedRunnable(TypedBitmap bitmap){
			this.bitmap = bitmap;
		}
		
		@Override
		public void run(){
			int type = bitmap.getType();
			Bitmap bitmapOriginal = bitmap.getBitmap();
			switch(type){
			case TypedBitmap.TYPE_GIF:
				gifDeploy(bitmapOriginal, picturePopup, progressBar);
				break;
			case TypedBitmap.TYPE_JPG:
				jpgDeploy(bitmapOriginal, picturePopup, progressBar);
				break;
			case TypedBitmap.TYPE_UNKNOWN:
				Log.d("POPUP", "work?");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmapOriginal.compress(CompressFormat.JPEG, 100, baos);
				if(Utils.isGif(baos.toByteArray())){
					gifDeploy(bitmapOriginal, picturePopup, progressBar);
				}else{
					jpgDeploy(bitmapOriginal, picturePopup, progressBar);
				}
				
				try{
					baos.close();
				}catch(Exception e){
					
				}
				break;
			}
		}
	}
	
	private void gifDeploy(Bitmap bitmap, final HalloonImageView imageView, final HalloonProgressBar progressBar){
		imageView.requestLayout();
		Toast.makeText(context, "gif", Toast.LENGTH_LONG).show();
		progressBar.setProgress(100);
		GifAction gifAction = new GifAction(){

			@Override
			public void parseOk(boolean parseStatus, int frameIndex) {
				if(imageView.getVisibility() == View.INVISIBLE || imageView.getVisibility() == View.GONE){
					imageView.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
				}
				Log.d("POPUP", "parseOK");
				if (parseStatus) {
					if (frameIndex == -1) {
						if (gifDecoder.getFrameCount() > 1) {
							imageView.setGifDecoder(gifDecoder);
							imageView.setOriginLayout(gifDecoder.getFrameImage(0).getWidth(), gifDecoder.getFrameImage(0).getHeight());
						}
					}
				}
			}
			
		};
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, baos);
		gifDecoder = new GifDecoder(baos.toByteArray(), gifAction);
		
		gifDecoder.start();
		
	}
	
	private void jpgDeploy(Bitmap bitmap, HalloonImageView imageView, HalloonProgressBar progressBar){
		imageView.requestLayout();
		imageView.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		imageView.setImageBitmap(bitmap);
		imageView.setOriginLayout(bitmap.getWidth(), bitmap.getHeight());
		Log.d("POPUP", "bitmap_width: " + bitmap.getWidth() + " bitmap_height:" + bitmap.getHeight());
	}

	public void setupCommentBar(int y) {
		container = (ViewGroup) ((Activity) context).getLayoutInflater().inflate(R.layout.comment_bar, null);
		popupWindow = new PopupWindow(container, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(-00000));
		popupWindow.setAnimationStyle(R.style.CommentBarZoom);
		popupWindow.showAtLocation(((Activity) context).findViewById(R.id.title_layout), Gravity.NO_GRAVITY, 0, y);
	}

	public void setupCommentFunctionPopup(final String tweetId, final boolean isMe, final String content, final int type, final int positionInList) {
		container = (ViewGroup) ((Activity) context).getLayoutInflater().inflate(R.layout.comment_popup, null);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
		adapter.add(context.getString(R.string.comment));
		adapter.add(context.getString(R.string.retweet));
		adapter.add(context.getString(R.string.copy));
		if (!isMe){
		    adapter.add(context.getString(R.string.fav));
		}else{
		    adapter.add(context.getString(R.string.delete));
		}
		System.out.println(isMe);

		ListView list = (ListView) container.findViewById(R.id.functional);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();

				switch (position) {
				case 0:
					bundle.putInt("type", CommentFragment.COMMENT);
					bundle.putString("id", tweetId);
					if(type == TWEET_CONTENT){
						bundle.putBoolean("reShowTab", false);
					}else{
						bundle.putBoolean("reShowTab", true);
					}
					((MainPageFragmentCallback) context).setupCommentFragment(bundle);
					popupWindow.dismiss();
					break;
				case 1:
					bundle.putInt("type", CommentFragment.RETWEET);
					bundle.putString("id", tweetId);
					if(type == TWEET_CONTENT){
						bundle.putBoolean("reShowTab", false);
					}else{
						bundle.putBoolean("reShowTab", true);
					}
					((MainPageFragmentCallback) context).setupCommentFragment(bundle);
					popupWindow.dismiss();
					break;
				case 2:
					ContentTransUtil.getInstance(context).copyToClipBoard(content);

					Toast.makeText(context, context.getString(R.string.already_copy), Toast.LENGTH_LONG).show();
					popupWindow.dismiss();
					break;
				case 3:
					if (isMe) {
						if (!((BaseMultiFragmentActivity) context).checkConnection()) {
							System.out.println(false);
							Toast.makeText(context, context.getString(R.string.delete_failure) + " " + context.getString(R.string.connection_failure), Toast.LENGTH_LONG).show();
							break;
						}
						if(type == TWEET_LIST){
							new AlertDialog.Builder(context)
	                        .setCancelable(true)
	                        .setMessage(context.getString(R.string.confirm_delete))
							.setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									new Thread() {
										@Override
										public void run() {
											if (ContentManager.getInstance(context).delTweet(tweetId)[0] == 0) {
												Intent intent = new Intent();
												Bundle bundle = new Bundle();
												bundle.putInt("position", positionInList);
												intent.setAction(Constants.DEL_CONFIRM);
												intent.putExtras(bundle);
												context.sendBroadcast(intent);
												DBManager.getInstance(context).delTweetContent(tweetId);
											} else {
												Message msg = new Message();
												msg.what = 0;
												mHandler.sendMessage(msg);
											}
											return;
										}
									}.start();
									popupWindow.dismiss();
								}
							})
							.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							})
							.create()
							.show();
						}else{
							new AlertDialog.Builder(context)
							.setCancelable(true)
							.setMessage(context.getString(R.string.confirm_delete))
							.setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface dialog, int which){
									new Thread(){
										@Override
										public void run(){
											if(ContentManager.getInstance(context).delTweet(tweetId)[0] == 0){
												((BaseMultiFragmentActivity) context).backStackAction();
											}else{
												Message msg = new Message();
												msg.what = 1;
												mHandler.sendMessage(msg);
												
											}
											return;
										}
										
									}.start();
									popupWindow.dismiss();
								}
							})
							.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface dialog, int which){
									
								}
							})
							.create()
							.show();
						}
				    }else{
				    	new Thread(){
				    		@Override
				    		public void run(){
				    			Message msg = new Message();
				    			if(ContentManager.getInstance(context).addFav(tweetId)[0] == 0){
				    				msg.what = 2;
				    				mHandler.sendMessage(msg);
				    			}else{
				    				msg.what = 3;
				    				mHandler.sendMessage(msg);
				    			}
				    			
				    			return;
				    			
				    		}
				    	}.start();
				    	popupWindow.dismiss();
				    }
					break;
				}
			}

		});

		popupWindow = new PopupWindow(container, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(-00000));
		popupWindow.setAnimationStyle(R.style.PopupWindowYAxisSlide);
		popupWindow.showAtLocation(((Activity) context).findViewById(R.id.title_layout), Gravity.NO_GRAVITY, 0, SettingsManager.getInstance(context).getSystemBarHeight());
	}

	
	public void setupTitleListPopup(int parentViewRes, BaseAdapter adapter) {
		container = (ViewGroup) ((Activity) context).getLayoutInflater().inflate(R.layout.title_list, null);
		ListView list = (ListView) container.findViewById(R.id.list);
		list.setAdapter(adapter);
		popupWindow = new PopupWindow(container, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(-00000));
		popupWindow.setAnimationStyle(R.style.PopupWindowSlideFade);
		popupWindow.showAtLocation(((Activity) context).findViewById(parentViewRes), Gravity.CENTER | Gravity.TOP, 0, ((Activity) context).findViewById(parentViewRes).getBottom() + SettingsManager.getInstance(context).getSystemBarHeight());
		
	}
	
	
	/**
	 * 
	 * @param parentViewRes
	 * @param adapter
	 * @param onItemClickListener
	 */
	public void setupTitleListPopup(int parentViewRes, BaseAdapter adapter,final AdapterView.OnItemClickListener onItemClickListener) {
		
		container = (ViewGroup) ((Activity) context).getLayoutInflater().inflate(R.layout.title_list, null);
		ListView list = (ListView) container.findViewById(R.id.list);
		list.setAdapter(adapter);
		popupWindow = new PopupWindow(container, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(-00000));
		popupWindow.setAnimationStyle(R.style.PopupWindowSlideFade);
		popupWindow.showAtLocation(((Activity) context).findViewById(parentViewRes), Gravity.CENTER | Gravity.TOP, 0, ((Activity) context).findViewById(parentViewRes).getBottom() + SettingsManager.getInstance(context).getSystemBarHeight());
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(onItemClickListener != null){
					onItemClickListener.onItemClick(parent, view, position, id);
				}
				popupWindow.dismiss();
			}
			
		});
	}
	
	public void setupAtListPopup(int parentViewRes, int x, int y, String searchString){
		container = (ViewGroup) ((Activity) context).getLayoutInflater().inflate(R.layout.title_list, null);
		ListView list = (ListView) container.findViewById(R.id.list);
		ArrayList<UserBean> arrayList = new ArrayList<UserBean>();
		if(SettingsManager.getInstance(context).getProfileStatus() == DBManager.PROFILE_STATUS_READY){
			if(searchString != null){
				arrayList = DBManager.getInstance(context).getContactsSearch(searchString);
			}else{
				arrayList = DBManager.getInstance(context).getContactsSearch(searchString);
			}
		}else{
			
		}
		
		class AtListAdapter extends BaseAdapter{
			
			private ArrayList<UserBean> arrayList;
			
			public AtListAdapter(ArrayList<UserBean> arrayList){
				this.arrayList = arrayList;
			}

			@Override
			public int getCount() {
				return arrayList.size();
			}

			@Override
			public UserBean getItem(int position) {
				return arrayList.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder viewHolder;
				if(convertView == null){
					viewHolder = new ViewHolder();
					LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
					viewHolder.textView = (TextView) convertView.findViewById(R.id.text1);
					convertView.setTag(viewHolder);
				}else{
					viewHolder = (ViewHolder) convertView.getTag();
				}
				
				viewHolder.textView.setText(getItem(position).getNick());
				
				return convertView;
			}
			
			class ViewHolder{
				TextView textView;
			}
			
		}
		AtListAdapter atListAdapter = new AtListAdapter(arrayList);
		list.setAdapter(atListAdapter);

		popupWindow = new PopupWindow(container, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(-00000));
		popupWindow.setAnimationStyle(R.style.PopupWindowSlideFade);
		popupWindow.showAtLocation(((Activity) context).findViewById(parentViewRes), Gravity.CENTER | Gravity.TOP, x, y);
	}
	
	public void dismiss(){
		if(popupWindow != null && popupWindow.isShowing()){
			popupWindow.dismiss();
		}
	}
	
	public void popupWindowDismiss() {
		if (picturePopupTask != null && picturePopupTask.getStatus() == AsyncTask.Status.RUNNING) picturePopupTask.cancel(true);
		popupWindow.dismiss();
		if(onProcessListener != null) onProcessListener = null;
		//picturePopupTask.clear();
	}

	public boolean isShowing() {
		return popupWindow.isShowing();
	}
}
