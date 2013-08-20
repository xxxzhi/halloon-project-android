package com.halloon.android.util;

import android.graphics.Bitmap;
import android.util.Log;

import com.halloon.android.bean.TweetBean;
import com.halloon.android.listener.DelayListener;

//it was just a test, not a real GifDecoder yet.
//I'm working on it now.
public class GifDecoder {
	
	static{
		System.loadLibrary("halloon-jni");
	}
	
	public static native TweetBean init();
	public static native void callInTime(int i, DelayListener delayListener);//this function will call DelayListener.onDelay() in C
	public static native void reverseBitmap(Bitmap sourceBitmap, Bitmap destBitmap);
	public static native void convertToGray(Bitmap bitmapColor, Bitmap bitmapGray);
	public static native void colorMatrix(Bitmap sourceBitmap, Bitmap destBitmap, float[] matrix);
	
	public static TweetBean getTweetBean(){
		//callInTime(1, delayListener);
		return init();
	}
	
	private static DelayListener delayListener = new DelayListener(){

		@Override
		public void onDelay(int i) {
			Log.d("FROM C", "this interface was called from C:" + i);
		}
		
	};
}
