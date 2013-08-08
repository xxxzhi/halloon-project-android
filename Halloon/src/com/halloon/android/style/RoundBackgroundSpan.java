package com.halloon.android.style;

import java.lang.reflect.Method;

import com.halloon.android.widget.ButtonStyleTextView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.Layout;
import android.widget.TextView;

public class RoundBackgroundSpan {
	
	private RoundRectShape mShape;
	
	private int lineStart;
	private int lineEnd;
	
	private Rect[] lines;
	
	private Paint paint;
	
	private int color;
	
	private ButtonStyleTextView tv;
	
	private int start;
	private int end;

	public RoundBackgroundSpan(ButtonStyleTextView tv, int color, float round, int start, int end) {
		
		this.tv = tv;
		
		this.start = start;
		this.end = end;
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		
		this.color = color;
		
		mShape = new RoundRectShape(new float[]{round, round, round, round,
				                                round, round, round, round}, null, null);
		
	}
	
	public int getColor(){
		return paint.getColor();
	}
	
	public void setColor(int color){
		paint.setColor(color);
	}
	
	public void setOriginalColor(){
		paint.setColor(color);
	}
	
	public void updateDrawState(Canvas canvas){
		Layout layout = tv.getLayout();
		
		if(layout != null){
			lineStart = layout.getLineForOffset(start);
			lineEnd = layout.getLineForOffset(end);
			
			if(lineStart != lineEnd){
				lines = new Rect[(lineEnd + 1) - lineStart];
				
				for(int i = lineStart; i <= lineEnd; i++){
					Rect rect = new Rect();
					layout.getLineBounds(i, rect);
					if(i == lineStart){
						rect.left = (int) layout.getPrimaryHorizontal(start);
					}
					if(i == lineEnd){
						rect.right = (int) layout.getSecondaryHorizontal(end);
					}
					
					lines[i - lineStart] = rect;
				}
			}else{
				Rect rect = new Rect();
				lines = new Rect[1];
				layout.getLineBounds(lineStart, rect);
				rect.left = (int) layout.getPrimaryHorizontal(start);
				rect.right = (int) layout.getSecondaryHorizontal(end);
				
				lines[0] = rect;
			}
			
			canvas.save();
			
			int x = tv.getCompoundPaddingLeft();
			int y = tv.getExtendedPaddingTop();
			
			try{
				Method method = TextView.class.getDeclaredMethod("getVerticalOffset", Boolean.class);
				method.setAccessible(true);
				Integer returnParam = (Integer) method.invoke(tv, false);
				y += returnParam;
			}catch(Exception e){}
			
			canvas.translate(x, y);
			
			for(int i = 0; i <= lineEnd - lineStart; i++){
				Rect rect = lines[i];
				mShape.resize(rect.width(), rect.height());
				
				if(i == 0){
					canvas.translate(rect.left, rect.top);
				}else{
					canvas.translate(-lines[0].left, rect.height());
				}
				
				mShape.draw(canvas, paint);
				
			}
			canvas.restore();
		}
	}


}
