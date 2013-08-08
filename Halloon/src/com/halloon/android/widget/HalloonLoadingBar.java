/*
 * 2013 哈恩项目  http://www.halloon.com
 * 
 * 加载指示组件
 * 
 * caifangmao8@gmail.com
 */
package com.halloon.android.widget;

import com.halloon.android.util.DensityUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class HalloonLoadingBar extends View {
	
	private static final String TAG = "HalloonLoadingBar";
	
	private int viewWidth;
	private int viewHeight;
	
	private Paint paint  = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Shader shader;
	private Shader moveShader;
	private RectF rect = new RectF();
	private Matrix matrix = new Matrix();
	
	private boolean growDirection = true;
	private float growOffset;
	
	private int progressBarHeight;

	public HalloonLoadingBar(Context context){
		this(context, null);
	}
	
	public HalloonLoadingBar(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public HalloonLoadingBar(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
        progressBarHeight = DensityUtil.dip2px(context, 4);
		
		shader = new LinearGradient(0, viewHeight - progressBarHeight - 1, 0, progressBarHeight, new int[]{0xFF0085DC, 0xFF025388}, null, Shader.TileMode.REPEAT);
		moveShader = new LinearGradient(0, viewHeight - progressBarHeight - 1, progressBarHeight * 2, viewHeight + progressBarHeight, new int[]{0x90FFFFFF, 0x90FFFFFF, 0x00000000,0x00000000}, new float[]{0F, 0.5F, 0.5F, 1F}, Shader.TileMode.REPEAT);
		
	}
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		if(heightMeasureSpec < progressBarHeight){
			heightMeasureSpec = progressBarHeight;
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			super.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		}else{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
		
		viewWidth = getMeasuredWidth();
		viewHeight = getMeasuredHeight();
	}
	
	@Override
	public void onDraw(Canvas canvas){
		rect.left = 0;
		rect.top = 0;
		rect.right = viewWidth;
		rect.bottom = viewHeight;
        paint.setShader(shader);
		
		canvas.drawRoundRect(rect, progressBarHeight / 2, progressBarHeight / 2, paint);
		growOffset = rect.top;
		paint.setColor(0xFFFFFFFF);
		if(growDirection){
			growOffset -= 1F;
		}else{
			growOffset += 1F;
		}
		if(growOffset <= rect.top){
			growDirection = false;
		}else if(growOffset >= rect.bottom){
			growDirection = true;
		}
		matrix.postTranslate(0, growOffset);
		moveShader.setLocalMatrix(matrix);
		paint.setShader(moveShader);
		
		canvas.drawRoundRect(rect, progressBarHeight / 2, progressBarHeight / 2, paint);
		
		invalidate();
	}
	
}
