package com.halloon.android.image;

import java.io.InputStream;
import java.io.OutputStream;

import com.halloon.android.image.ImageLoader.OnProcessListener;

public class Utils {
	public static void CopyStream(InputStream is, OutputStream os, OnProcessListener mOnProcessListener) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			int count = 0;
			int read = -1;
			while((read = is.read(bytes, 0, buffer_size)) != -1){
				count += read;
				os.write(bytes, 0, read);
			}
			for (;;) {
				count += is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}
}