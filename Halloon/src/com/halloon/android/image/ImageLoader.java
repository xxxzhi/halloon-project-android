package com.halloon.android.image;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.halloon.android.R;
import com.halloon.android.util.ImageUtil;
import com.halloon.android.util.TimeUtil;

public class ImageLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	Context context;

	private static ImageLoader instance;
	private int pixel;
	
	public static final String FORMAT_GIF = ".gif";
	public static final String FORMAT_JPG = ".jpg";
	public static final String FORMAT_PNG = ".png";
	
	public static final int TYPE_GIF = 0;
	public static final int TYPE_JPG = 1;
	public static final int TYPE_UNKNOWN = -1;
	
	public interface OnProcessListener{
		public void onProcessStarted(int type);
		public void onProcess(float f);
		public void onProcessEnded(Bitmap bitmap, int type);
	}

	private ImageLoader(Context context) {
		this.context = context;
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	public static ImageLoader getInstance(Context context) {
		if (instance == null) {
			instance = new ImageLoader(context);
		}
		return instance;
	}

	final int stub_id = R.drawable.ic_launcher;

	public static String imageSave(Bitmap bmp) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			File f = new File(android.os.Environment.getExternalStorageDirectory(), "/Halloon/download/image");
			File sf = new File(f, TimeUtil.getCurrentTimeForFileName() + ".jpg");
			try {
				f.mkdirs();
				sf.createNewFile();
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(sf));
				bmp.compress(CompressFormat.JPEG, 100, bos);
				bos.flush();
				bos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return sf.getPath();
		} else {
			return null;
		}
	}

	public void displayImage(String url, ImageView imageView, int pixel, OnProcessListener mOnProcessListener) {
		this.pixel = pixel;
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			queuePhoto(url, imageView, mOnProcessListener);
			imageView.setImageResource(stub_id);
		}
	}

	private void queuePhoto(String url, ImageView imageView, OnProcessListener mOnProcessListener) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		PhotosLoader pl = new PhotosLoader(p);
		pl.setOnProcessListener(mOnProcessListener);
		executorService.submit(pl);
	}

	private Bitmap getBitmap(String url, OnProcessListener mOnProcessListener) {
		File f = fileCache.getFile(url);
		
		int type = -1;

		// from SD cache
		Bitmap b = decodeFile(f, mOnProcessListener, type);
		if (b != null)
			return b;

		// from web
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			
			OutputStream os = new FileOutputStream(f);
			type = Utils.CopyStream(is, os, mOnProcessListener, conn.getContentLength());
			os.close();
			bitmap = decodeFile(f, mOnProcessListener, type);
			return bitmap;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		}
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f, OnProcessListener mOnProcessListener, int type) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 300;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			if(mOnProcessListener != null) mOnProcessListener.onProcessEnded(bitmap, type);
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;
		OnProcessListener mOnProcessListener;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}
		
		public void setOnProcessListener(OnProcessListener mOnProcessListener){
			this.mOnProcessListener = mOnProcessListener;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url, mOnProcessListener);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageResource(stub_id);

			photoToLoad.imageView.setImageBitmap(ImageUtil.getRoundedCornerBitmap(((BitmapDrawable) photoToLoad.imageView.getDrawable()).getBitmap(), pixel));
		}

	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

}
