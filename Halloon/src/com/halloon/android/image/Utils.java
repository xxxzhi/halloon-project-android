package com.halloon.android.image;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import com.halloon.android.image.ImageLoader.OnProcessListener;

public class Utils {
	public static int CopyStream(InputStream is, OutputStream os, OnProcessListener mOnProcessListener, long length) {
		final int buffer_size = 1024;
		int type = -1;
		try {
			byte[] bytes = new byte[buffer_size];
			int count = 0;
			int read = -1;
			read = is.read(bytes);
			os.write(bytes, 0, read);
			count = read;
			if(mOnProcessListener != null){
				if(isGif(bytes)){
					type = TypedBitmap.TYPE_GIF;
				}else{
					type = TypedBitmap.TYPE_JPG;
				}
				
				mOnProcessListener.onImageTypeGot(type);
			}
			while((read = is.read(bytes, 0, buffer_size)) != -1){
				
				os.write(bytes, 0, read);
				count += read;
				if(mOnProcessListener != null) mOnProcessListener.onProcess(count * 1.0F / length);
			}
			
		} catch (Exception ex) {
		}
		
		return type;
	}
	
	public static boolean isGif(byte[] is) {
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
}