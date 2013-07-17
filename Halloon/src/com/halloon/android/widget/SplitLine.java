package com.halloon.android.widget;

import com.halloon.android.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class SplitLine extends View{
	
	private static final String TAG = "SplitLine";
	
	private Paint paint;
	private Shader shader;
	
	public SplitLine(Context context){
		this(context, null);
	}
	
	public SplitLine(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public SplitLine(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SplitLine);
	}

	
}
