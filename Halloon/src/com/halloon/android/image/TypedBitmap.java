package com.halloon.android.image;

import android.graphics.Bitmap;

public class TypedBitmap {

	private Bitmap bitmap;
	private int type;
	
    public static final int TYPE_GIF = 0;
    public static final int TYPE_JPG = 1;
    public static final int TYPE_UNKNOWN = 2;
    
    public TypedBitmap(Bitmap bitmap, int type){
    	this.bitmap = bitmap;
    	this.type = type;
    }
    
    public Bitmap getBitmap(){
    	return bitmap;
    }
    
    public void setBitmap(Bitmap bitmap){
    	this.bitmap = bitmap;
    }
    
    public int getType(){
    	return type;
    }
	
    public void setType(int type){
    	this.type = type;
    }
    
}
