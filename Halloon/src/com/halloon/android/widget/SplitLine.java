package com.halloon.android.widget;

import com.halloon.android.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class SplitLine extends View{
	
	private static final String TAG = "SplitLine";
	
	private Paint paint;
	private Shader shader;
	
	private int color;
	private float margin;
	
	public SplitLine(Context context){
		this(context, null);
	}
	
	public SplitLine(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public SplitLine(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SplitLine);
		color = ta.getColor(R.styleable.SplitLine_color, 0xFF999999);
		margin = ta.getDimension(R.styleable.SplitLine_margin, 10F);
		ta.recycle();
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		shader = new LinearGradient(0, 0, margin * 2, 0, new int[]{0, 0, color, color}, new float[]{0F, 0.5F, 0.5F, 1F}, Shader.TileMode.REPEAT);
		
		paint.setShader(shader);
	}

	@Override
	public void onDraw(Canvas canvas){
		
		canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
	}
}
