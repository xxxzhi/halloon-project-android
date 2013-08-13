package com.halloon.android.image;

import java.io.File;

import android.content.Context;

public class FileCache {

	public static final long MAX_SIZE = 1024 * 1024 * 100;
	
	private File cacheDir;

	public FileCache(Context context) {
		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "/Halloon/data/cache");
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public File getFile(String url) {
		// I identify images by hashcode. Not a perfect solution, good for the
		// demo.
		String filename = String.valueOf(url.hashCode());
		// Another possible solution (thanks to grantland)
		// String filename = URLEncoder.encode(url);
		File f = new File(cacheDir, filename);
		return f;

	}
	
	public long checkSize(){
		long size = 0;
		File[] files = cacheDir.listFiles();
		if(files == null)
			return 0;
		for(File f : files){
			size += f.length();
		}
		
		return size;
	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}

}