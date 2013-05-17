package com.halloon.android.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.halloon.android.R;
import com.halloon.android.data.SettingsManager;
import com.halloon.android.util.Constants;
import com.tencent.weibo.oauthv1.OAuthV1;
import com.tencent.weibo.oauthv1.OAuthV1Client;
import com.tencent.weibo.webview.OAuthV1AuthorizeWebView;

public class OAuthActivity extends Activity {

	protected static final String TAG = "OAuthActivity";
	private OAuthV1 oAuthV1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// ����û��Title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ȥ��bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.login);

		if (SettingsManager.getInstance(getApplicationContext())
				.getRequestToken() != null
				& SettingsManager.getInstance(getApplicationContext())
						.getRequestTokenSecret() != null) {
			handler.sendEmptyMessage(0);
		}
		ImageView Loginbutton = (ImageView) findViewById(R.id.login_background);

		Loginbutton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Log.i(TAG, "��ť����¼�����");
				oAuthV1 = new OAuthV1("null");
				oAuthV1.setOauthConsumerKey(Constants.CONSUMER_KEY);
				oAuthV1.setOauthConsumerSecret(Constants.CONSUMER_SECRET);

				Thread thread = new OAuthThread();
				thread.start();
			}
		});
	}

	private class OAuthThread extends Thread {

		@Override
		public void run() {
			Log.i(TAG, "OAuthThread run");

			try {
				oAuthV1 = OAuthV1Client.requestToken(oAuthV1);
				Intent intent = new Intent(OAuthActivity.this,
						OAuthV1AuthorizeWebView.class);
				intent.putExtra("oauth", oAuthV1);
				startActivityForResult(intent, 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ����WebViewActivity���ص���֤�룺verifier

		if (requestCode == 1) {
			if (resultCode == OAuthV1AuthorizeWebView.RESULT_CODE) {
				oAuthV1 = (OAuthV1) data.getExtras().getSerializable("oauth");

				try {
					oAuthV1 = OAuthV1Client.accessToken(oAuthV1);
					System.out.println("��Ȩ��ɣ�");

					SettingsManager.getInstance(getApplicationContext())
							.setRequestToken(oAuthV1.getOauthToken());
					SettingsManager.getInstance(getApplicationContext())
							.setRequestTokenSecret(
									oAuthV1.getOauthTokenSecret());

					Log.i(TAG, "ʹ��loginTask��ȡResult��ݣ�home_timeline");

					handler.sendEmptyMessage(0);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				Intent intent = new Intent();
				intent.setClass(OAuthActivity.this, HomeActivity.class);
				startActivity(intent);
				finish();

				break;

			default:
				break;
			}

		}

	};

}
