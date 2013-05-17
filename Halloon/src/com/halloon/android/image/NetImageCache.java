package com.halloon.android.image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.halloon.android.util.ImageUtil;

public class NetImageCache {
	private String mPicUrl;
	private ImageView imageViewCache;
	private int pix = 0;

	public void setImageUrl(String url, ImageView imageView) {
		mPicUrl = url;
		imageViewCache = imageView;
		loadImage(url);
	}

	public void setheadUrl(String url, ImageView imageView, int pixels) {
		mPicUrl = url;
		imageViewCache = imageView;
		pix = pixels;
		loadImage(url, pixels);
	}

	private void loadImage(String url, int pixels) {

		// 判断图片是否存在
		if (NetImageViewCache.getInstance().isBitmapExit(url)) {
			Bitmap bitmap = NetImageViewCache.getInstance().get(url);
			bitmap = ImageUtil.getRoundedCornerBitmap(bitmap, pixels);
			imageViewCache.setImageBitmap(bitmap);
		} else {
			// 如果不存在，开启异步线程下载
			new NetImageDownLoad().execute(mPicUrl);
		}

	}

	private void loadImage(String url) {
		// 判断图片是否存在
		if (NetImageViewCache.getInstance().isBitmapExit(url)) {
			Bitmap bitmap = NetImageViewCache.getInstance().get(url);
			imageViewCache.setImageBitmap(bitmap);
		} else {
			// 如果不存在，开启异步线程下载
			new NetImageDownLoad().execute(mPicUrl);
		}
	}

	private byte[] getBytesFromStream(InputStream inputStream) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;
		while (len != -1) {
			try {
				len = inputStream.read(b);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (len != -1) {
				baos.write(b, 0, len);
			}
		}

		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return baos.toByteArray();
	}

	private class NetImageDownLoad extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			URL url = null;
			InputStream inputStream = null;
			HttpURLConnection urlConnection = null;
			Bitmap bmp = null;
			try {
				url = new URL(params[0]);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setConnectTimeout(10000);
				inputStream = urlConnection.getInputStream();
				byte[] bt = getBytesFromStream(inputStream);
				bmp = BitmapFactory.decodeByteArray(bt, 0, bt.length);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != inputStream) {
					try {
						inputStream.close();
						inputStream = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (null != urlConnection) {
					urlConnection.disconnect();
					urlConnection = null;
				}
			}
			return bmp;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (null != result) {
				if (pix != 0) {
					result = ImageUtil.getRoundedCornerBitmap(result, pix);
					imageViewCache.setImageBitmap(result);
				} else {
					imageViewCache.setImageBitmap(result);
				}
				// 下载完图片后存入缓存与SD卡
				NetImageViewCache.getInstance().put(mPicUrl, result, true);
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}
}
