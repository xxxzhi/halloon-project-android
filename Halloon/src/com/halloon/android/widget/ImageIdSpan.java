package com.halloon.android.widget;

import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class ImageIdSpan extends ImageSpan {
	
	private int drawableId;
	
	public ImageIdSpan(Drawable drawable, int verticalAlignment){
		super(drawable, verticalAlignment);
	}
	
	public void setDrawableId(int id){
		this.drawableId = id;
	}
	
	public int getDrawableId(){
		return drawableId;
	}
}
