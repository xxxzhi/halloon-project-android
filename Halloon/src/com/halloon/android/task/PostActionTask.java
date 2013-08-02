package com.halloon.android.task;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.halloon.android.R;
import com.halloon.android.data.ContentManager;
import com.halloon.android.data.SettingsManager;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;

public class PostActionTask extends BaseCompatiableTask<Void, Void, int[]> {
	private Context context;

	public static final int COMMENT = 0;
	public static final int RETWEET = 1;
	public static final int PUBLISH_TWEET = 2;
	public static final int PUBLISH_IMAGE_TWEET = 3;

	private int type;
	private String tweetId;
	private String content;
	private String longitude;
	private String latitude;
	private String syncFlag;
	private String imagePath;

	private String ip;

	private String[] tickerState;
	private String[] tickerString;
	
	private boolean isBack = true;

	private NotificationManager ntfManager;

	/**
	 * 
	 * @param context
	 * @param type
	 *            类型 (COMMENT,RETWEET,PUBLISH_TWEET,PUBLISH_IMAGE_TWEET)
	 * @param tweetId
	 *            回复及转发的 微博ID
	 * @param content
	 *            内容
	 * @param longitude
	 *            经度（可选）
	 * @param latitude
	 *            纬度(可选）
	 * @param syncFlag
	 *            是否同步 0x0同步 0x1不同步
	 * @param imagePath
	 *            图片路径 （可选）
	 */
	public PostActionTask(Context context, int type, String tweetId, String content, String longitude, String latitude, String syncFlag, String imagePath) {
		this.context = context;
		this.type = type;
		this.tweetId = tweetId;
		this.content = content;
		this.longitude = longitude;
		this.latitude = latitude;
		this.syncFlag = syncFlag;
		this.imagePath = imagePath;
		ntfManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		tickerState = new String[] {context.getString(R.string.sending), context.getString(R.string.sent) };
		tickerString = new String[] {context.getString(R.string.comment), context.getString(R.string.retweet), context.getString(R.string.publish), context.getString(R.string.publish)};
		ip = SettingsManager.getInstance(context).getDeviceIp();
	}

	public PostActionTask(Context context, int type, String content, String syncFlag, String imagePath) {
		this.context = context;
		this.type = type;
		this.content = content;
		this.syncFlag = syncFlag;
		this.imagePath = imagePath;
		ntfManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		tickerState = new String[] {context.getString(R.string.sending), context.getString(R.string.sent) };
		tickerString = new String[] {context.getString(R.string.comment), context.getString(R.string.retweet), context.getString(R.string.publish), context.getString(R.string.publish)};
		ip = SettingsManager.getInstance(context).getDeviceIp();
	}

	public PostActionTask(Context context, int type, String content) {
		this.context = context;
		this.type = type;
		this.content = content;
		ntfManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		tickerState = new String[] {context.getString(R.string.sending), context.getString(R.string.sent) };
		tickerString = new String[] {context.getString(R.string.comment), context.getString(R.string.retweet), context.getString(R.string.publish), context.getString(R.string.publish)};
		ip = SettingsManager.getInstance(context).getDeviceIp();
	}

	public PostActionTask(Context context, int type, String tweetId, String content) {
		this.context = context;
		this.type = type;
		this.tweetId = tweetId;
		this.content = content;
		ntfManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		tickerState = new String[] {context.getString(R.string.sending), context.getString(R.string.sent) };
		tickerString = new String[] {context.getString(R.string.comment), context.getString(R.string.retweet), context.getString(R.string.publish), context.getString(R.string.publish)};
		ip = SettingsManager.getInstance(context).getDeviceIp();
	}

	public PostActionTask(Context context, int type, String content, String longitude, String latitude, String syncFlag, String imagePath) {
		this.context = context;
		this.type = type;
		this.content = content;
		this.longitude = longitude;
		this.latitude = latitude;
		this.syncFlag = syncFlag;
		this.imagePath = imagePath;
		ntfManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		tickerState = new String[] {context.getString(R.string.sending), context.getString(R.string.sent) };
		tickerString = new String[] {context.getString(R.string.comment), context.getString(R.string.retweet), context.getString(R.string.publish), context.getString(R.string.publish)};
		ip = SettingsManager.getInstance(context).getDeviceIp();
	}

	@Override
	protected void onPreExecute() {
		Notification notification;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			notification = new Notification.Builder(context)
			        .setContentTitle(tickerState[0] + tickerString[type])
					.setContentText(content)
					.setSmallIcon(R.drawable.ic_launcher)
					.setTicker(tickerState[0])
					.setAutoCancel(true).getNotification();
		} else {
			notification = new Notification();
			notification.icon = R.drawable.ic_launcher;
			notification.tickerText = tickerState[0];
			notification.when = System.currentTimeMillis();
			PendingIntent intent = PendingIntent.getActivity(context, 0, new Intent(), 0);
			notification.setLatestEventInfo(context, tickerState[0], content, intent);
		}
		ntfManager.notify(0, notification);
	}

	@Override
	protected int[] doInBackground(Void... params) {
		switch (type) {
		case COMMENT:
			return ContentManager.getInstance(context).comment(content, tweetId, ip);
		case RETWEET:
			return ContentManager.getInstance(context).retweet(content, tweetId, ip);
		case PUBLISH_TWEET:
			if (longitude == null) {
				return ContentManager.getInstance(context).addText(content, ip);
			} else {
				return ContentManager.getInstance(context).addText(content, ip, longitude, latitude, syncFlag);
			}
		case PUBLISH_IMAGE_TWEET:
			if (longitude == null) {
				return ContentManager.getInstance(context).addImageTweet(content, imagePath, ip);
			} else {
				return ContentManager.getInstance(context).addImageTweet(content, imagePath, ip, longitude, latitude, syncFlag);
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(int[] result) {
		if (result[0] == 0) {
			String resultString = tickerString[type] + context.getString(R.string.success);
			Notification notification;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				notification = new Notification.Builder(context)
						.setContentTitle(resultString).setContentText(content)
						.setSmallIcon(R.drawable.ic_launcher)
						.setTicker(resultString).setAutoCancel(true)
						.getNotification();
			} else {
				notification = new Notification();
				notification.icon = R.drawable.ic_launcher;
				notification.tickerText = tickerState[1];
				notification.when = System.currentTimeMillis();
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				PendingIntent intent = PendingIntent.getActivity(context, 0, new Intent(), 0);
				notification.setLatestEventInfo(context, resultString, content, intent);
			}
			ntfManager.notify(0, notification);
			final long currentTime = System.currentTimeMillis();
			new Thread() {
				@Override
				public void run() {
					while (System.currentTimeMillis() - currentTime < 1000);
					ntfManager.cancel(0);
					return;
				}
			}.start();
			if(isBack) ((BaseMultiFragmentActivity) context).backStackAction();
			((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} else {
			ntfManager.cancel(0);
			if (((BaseMultiFragmentActivity) context).checkConnection()) {
				Toast.makeText(context, context.getString(R.string.send_failure), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, context.getString(R.string.send_failure) + " " + context.getString(R.string.connection_failure), Toast.LENGTH_LONG).show();
			}
		}

	}

	public int getType() {
		return type;
	}
	
	public void setIsBack(boolean isBack){
		this.isBack = isBack;
	}

}
