package com.halloon.android.image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.WeakHashMap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class NetImageViewCache extends WeakHashMap<String, Bitmap> {

	private static NetImageViewCache mNetImageViewCache = new NetImageViewCache();
	private static final String CACHE_FILE = "/weiboxy/Cache";

	private NetImageViewCache() {

	}

	public static NetImageViewCache getInstance() {
		return mNetImageViewCache;
	}

	/**
	 * 判断图片是否存在首先判断内存中是否存在然后判断本地是否存在
	 * 
	 * @param url
	 * @return
	 */
	public boolean isBitmapExit(String url) {
		boolean isExit = containsKey(url);
		if (false == isExit) {
			isExit = isLocalHasBmp(url);
		}
		return isExit;
	}

	/*
	 * 判断本地有没有
	 */
	private boolean isLocalHasBmp(String url) {
		boolean isExit = true;

		String name = changeUrlToName(url);
		String filePath = isCacheFileIsExit();

		File file = new File(filePath, name);

		if (file.exists()) {
			isExit = cacheBmpToMemory(file, url);
		} else {
			isExit = false;
		}
		return isExit;
	}

	/*
	 * 将本地图片缓存到内存中
	 */
	private boolean cacheBmpToMemory(File file, String url) {
		boolean sucessed = true;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			sucessed = false;
		}
		byte[] bs = getBytesFromStream(inputStream);
		Bitmap bitmap = BitmapFactory.decodeByteArray(bs, 0, bs.length);
		if (bitmap == null) {
			return false;
		}
		this.put(url, bitmap, false);
		return sucessed;
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

	/*
	 * 判断缓存文件夹是否存在如果存在怎返回文件夹路径，如果不存在新建文件夹并返回文件夹路径
	 */
	private String isCacheFileIsExit() {
		String filePath = "";
		String rootpath = "";

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			rootpath = Environment.getExternalStorageDirectory().toString();
		}
		filePath = rootpath + CACHE_FILE;
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return filePath;
	}

	/*
	 * 将url变成图片的地址
	 */
	private String changeUrlToName(String url) {
		String name = url.replaceAll(":", "_");
		name = name.replaceAll("//", "_");
		name = name.replaceAll("/", "_");
		name = name.replaceAll("=", "_");
		name = name.replaceAll(",", "_");
		name = name.replaceAll("&", "_");
		return name;
	}

	public Bitmap put(String key, Bitmap value) {
		String filePath = isCacheFileIsExit();
		String name = changeUrlToName(key);
		File file = new File(filePath, name);
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		value.compress(CompressFormat.JPEG, 100, outputStream);
		try {
			outputStream.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (null != outputStream) {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			outputStream = null;
		}

		return super.put(key, value);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @param isCacheToLocal
	 *            是否缓存到本地
	 * @return
	 */
	public Bitmap put(String key, Bitmap value, boolean isCacheToLocal) {
		if (isCacheToLocal) {
			return this.put(key, value);
		} else {
			return super.put(key, value);
		}
	}
}
