package com.halloon.android.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Toast;

import com.ant.liao.GifAction;
import com.ant.liao.GifDecoder;
import com.halloon.android.R;
import com.halloon.android.widget.HalloonProgressBar;
import com.halloon.android.widget.HalloonImageView;

public class ImageLoadTask extends BaseCompatiableTask<String, Float, Bitmap> implements GifAction {
	//private ProgressBar progressBar;
	private HalloonProgressBar progressBar;
	private HalloonImageView pdView;
	private Context context;
	private GifDecoder gifDecoder;
	private int i = 0;

	private boolean isGif;

	private boolean decodeFinished = false;
	
	private boolean running = true;


	public ImageLoadTask(Context context, HalloonImageView pdView, HalloonProgressBar progressBar) {
		this.context = context;
		this.pdView = pdView;
		this.progressBar = progressBar;
	}

	@Override
	protected void onPreExecute() {
		pdView.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(params[0]);
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			printHttpResponse(httpResponse);
			HttpEntity httpEntity = httpResponse.getEntity();
			long length = httpEntity.getContentLength();
			is = httpEntity.getContent();
			if (is != null) {
				baos = new ByteArrayOutputStream();
				byte[] buf = new byte[128];
				int read = -1;
				int count;
				read = is.read(buf);
				count = read;
				publishProgress(count * 1.0F / length);
				if(isGif(buf)){
					isGif = true;
				}else{
					isGif = false;
				}
				
				do{
					baos.write(buf, 0, read);
					count += read;
					publishProgress(count * 1.0F / length);
				}while((read = is.read(buf)) != -1);
				
				byte[] data = baos.toByteArray();
				Bitmap bit;
				bit = BitmapFactory.decodeByteArray(data, 0, data.length);
				if (isGif) {
					gifDecoder = new GifDecoder(data, this);
					gifDecoder.start();
					while(!decodeFinished){
						if(isCancelled()){
							gifDecoder = null;
							bit = null;
							break;
						}
					}
				}
				
				return bit;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Float... progress) {
		progressBar.setProgress(Math.round(progress[0] * 100));
	}

	@Override
	protected void onPostExecute(Bitmap bit) {
		if (!isCancelled()) {
			if (bit != null) {
				pdView.setVisibility(View.VISIBLE);
				pdView.setMode(HalloonImageView.GESTURE_ENABLE|HalloonImageView.PLAY_GIF);
				if (isGif) {
					if (gifDecoder.parseOk()) {
						System.out.println(gifDecoder.getFrameCount());
						System.out.println(gifDecoder.parseOk());
						
						pdView.setGifDecoder(gifDecoder);

						/*
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							animationDrawable = new AnimationDrawable();
							for (int i = 0; i < gifDecoder.getFrameCount(); i++) {
								animationDrawable.addFrame(new BitmapDrawable(pdView.getResources(), gifDecoder.getFrameImage(i)), gifDecoder.getDelay(i));
							}

							pdView.setImageDrawable(animationDrawable);
							animationDrawable.start();
							animationDrawable.setOneShot(false);
						} else {
							new Thread() {
								@Override
								public void run() {
									while (true) {
										if (i < gifDecoder.getFrameCount()) {
											Message msg = new Message();
											msg.what = 0;
											handler.sendMessage(msg);
											try {
												Thread.sleep(gifDecoder.getDelay(i));
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
											i++;
										} else {
											i = 0;
										}
										
										if(running = false){
											return;
										}
									}
								}
							}.start();
						}*/

						pdView.setOriginLayout(gifDecoder.getFrameImage(0).getWidth(), gifDecoder.getFrameImage(0).getHeight());
					}
				} else {
					pdView.setImageBitmap(bit);
					pdView.setOriginLayout(bit.getWidth(), bit.getHeight());
				}

				progressBar.setVisibility(View.GONE);
			} else {
				Toast.makeText(context, context.getString(R.string.load_image_failure), Toast.LENGTH_LONG).show();
			}
		}
	}


	public void drawImage() {
		pdView.setImageBitmap(gifDecoder.getFrameImage(i));
	}

	private void printHttpResponse(HttpResponse httpResponse) {
		Header[] headerArr = httpResponse.getAllHeaders();
		for (int i = 0; i < headerArr.length; i++) {
			Header header = headerArr[i];
		}
		HttpParams params = httpResponse.getParams();
	}

	private boolean isGif(byte[] is) {
		if (is != null) {
			String id = "";
			for (int i = 0; i < 6; i++) {
				int curByte = 0;
				try {
					curByte = is[i];
				} catch (Exception e) {
					e.printStackTrace();
				}

				id += (char) curByte;
			}
			if (!id.toUpperCase(Locale.ENGLISH).startsWith("GIF")) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void parseOk(boolean parseStatus, int frameIndex) {
		if (parseStatus) {
			if (gifDecoder != null) {
				if (frameIndex == -1) {
					if (gifDecoder.getFrameCount() > 1) {
						decodeFinished = true;
					}
				}
			}
		}
	}
	
	public void clear(){
		running = false;
		Bitmap bmp = pdView.getDrawingCache();
		pdView.setImageBitmap(null);
		if(bmp != null){
			bmp.recycle();
		}	
		
		System.gc();
	}
}
