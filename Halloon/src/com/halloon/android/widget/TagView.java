package com.halloon.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.halloon.android.R;

public class TagView extends View {
	
	private static final String TAG = "TagView";
	
	private static final int[] colorArray = {0xFFFBE9B7, 0xFFD8F5B3, 0xFFC2E5Fb};
	
	private String[] contents;
	
	private int width, height;
	private int singleWidth, singleHeight;
	private int margin;
	
	private Drawable foreGroundDrawable;
	
	private Paint paint;
	private Rect bound;
	
	public TagView(Context context){
		this(context, null);
	}
	
	public TagView(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public TagView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagView);
		singleHeight = ta.getDimensionPixelOffset(R.styleable.TagView_singleHeight, 20);
		margin = ta.getDimensionPixelOffset(R.styleable.TagView_margin, 2);
		ta.recycle();
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(singleHeight * 0.5F);
		foreGroundDrawable = context.getResources().getDrawable(R.drawable.background_tag);
		
		bound = new Rect();
	}
	
	public void setContents(String[] contents){
		this.contents = contents;
		
		invalidate();
		requestLayout();
	}
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		width = getMeasuredWidth();
		height = getMeasuredHeight();
		
		singleWidth = (width - margin * 3) / 4;
		
		if(null != contents && contents.length > 0){
			int line = (int) Math.ceil(contents.length / 4);
			int miniHeight = singleHeight * line + margin * (line - 1);
			if(height < miniHeight){
				setMeasuredDimension(width, miniHeight);
			}
		}
	}
	
	@Override
	public void onDraw(Canvas canvas){
		if(null != contents && contents.length > 0){
			int drawWidth = singleWidth + margin;
			int drawHeight = singleHeight + margin;
			
			int i = 0;
			do{
				int line = (int) Math.floor(i / 4);
				
				int left = drawWidth * (i % 4);
				int top = drawHeight * line;
				
				paint.setColor(colorArray[i % 3]);
				paint.setAlpha(0xFF);
				canvas.drawRect(left, top, left + singleWidth, top + singleHeight, paint);
				foreGroundDrawable.setBounds(left, top, left + singleWidth, top + singleHeight);
				foreGroundDrawable.draw(canvas);
				
				paint.getTextBounds(contents[i], 0, contents[i].length(), bound);
				paint.setColor(0x000000);
				paint.setAlpha(0x88);
				canvas.drawText(contents[i], left + (singleWidth - bound.width()) / 2, top + singleHeight - (singleHeight - bound.height()) / 2, paint);
			}while(++i < contents.length);
		}
	}

}
