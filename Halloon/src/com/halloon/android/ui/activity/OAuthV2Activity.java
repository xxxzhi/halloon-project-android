package com.halloon.android.ui.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.halloon.android.R;
import com.halloon.android.util.Constants;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.oauthv2.OAuthV2Client;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;

public class OAuthV2Activity extends Activity {

	private OAuthV2 oAuthV2;
	private File file = null;

	private MyHandler handler = new MyHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.login);

		if (isOauth()) {
			handler.sendEmptyMessage(0);
			System.out.println(isOauth());
		}

		ImageView imageView = (ImageView) findViewById(R.id.login_background);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				oAuthV2 = new OAuthV2(Constants.REDIRECT_URL);
				oAuthV2.setClientId(Constants.APP_KEY);
				oAuthV2.setClientSecret(Constants.APP_SECRET);

				new Thread() {
					@Override
					public void run() {
						OAuthV2Client.getQHttpClient().shutdownConnection();
						Intent intent = new Intent(OAuthV2Activity.this,
								OAuthV2AuthorizeWebView.class);
						intent.putExtra("oauth", oAuthV2);
						startActivityForResult(intent, 1);
					}
				}.start();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == OAuthV2AuthorizeWebView.RESULT_CODE) {
				oAuthV2 = (OAuthV2) data.getExtras().getSerializable("oauth");
				if (oAuthV2 != null && oAuthV2.getStatus() == 0) {
					FileOutputStream fos = null;
					ObjectOutputStream oos = null;
					try {
						fos = new FileOutputStream(file);
						oos = new ObjectOutputStream(fos);
						oos.writeObject(oAuthV2);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (oos != null) {
							try {
								oos.close();
								oos = null;
							} catch (IOException e) {
								e.printStackTrace();
							}
							if (fos != null) {
								try {
									fos.close();
									fos = null;
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}

					handler.sendEmptyMessage(0);
				}
			}
		}
	}

	private static class MyHandler extends Handler {
		WeakReference<OAuthV2Activity> activity;

		public MyHandler(OAuthV2Activity activity) {
			this.activity = new WeakReference<OAuthV2Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			OAuthV2Activity mActivity = activity.get();

			switch (msg.what) {
			case 0:
				Intent intent = new Intent();
				intent.putExtra("accessToken",
						mActivity.oAuthV2.getAccessToken());
				intent.putExtra("oauth", mActivity.oAuthV2);
				intent.putExtra("flag", 1);
				intent.setClass(mActivity, SlideHomeActivity.class);
				mActivity.startActivity(intent);
				mActivity.finish();

				break;

			default:
				break;
			}

		}

	};

	private boolean isOauth() {
		boolean token = false;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			openFileOutput("halloon_content", Context.MODE_PRIVATE);
			oauthPersistent();
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			oAuthV2 = (OAuthV2) ois.readObject();
			if (oAuthV2 != null) {
				token = true;
			}
		} catch (Exception e) {
			token = false;
		} finally {
			if (ois != null) {
				try {
					ois.close();
					ois = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
					fis = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return token;
	}

	private void oauthPersistent() {
		String s = getFileStreamPath("halloon_content").getAbsolutePath();
		for (String ss : fileList()) {
			System.out.println("ss==" + ss);
		}
		String x = "";
		try {
			x = s.substring(0, s.lastIndexOf("/"));
		} catch (Exception e) {
			e.printStackTrace();
			x = "/data/data/com.halloon.android";
		}

		try {
			file = new File(x + "/oauth.data");
			if (!file.exists()) {
				new File(x).mkdirs();
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
