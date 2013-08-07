package com.halloon.android.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.Layout;
import android.text.style.LineBackgroundSpan;
import android.text.style.ParagraphStyle;
import android.util.Log;

public class RoundBackgroundSpan implements LineBackgroundSpan, ParagraphStyle {
	
	private RoundRectShape mShape;
	
	private int lineStart;
	private int lineEnd;
	
	private Rect[] lines;
	
	private int color;

	public RoundBackgroundSpan(Layout layout, int color, float round, int start, int end) {
		
		this.color = color;
		
		mShape = new RoundRectShape(new float[]{round, round, round, round,
				                                round, round, round, round}, null, null);
		
		lineStart = layout.getLineForOffset(start);
		lineEnd = layout.getLineForOffset(end);
		
		if(lineStart != lineEnd){
			lines = new Rect[(lineEnd + 1) - lineStart];
			
			Log.d("ROUND BACKGROUND", "lineStart:" + lineStart + " lineEnd:" + lineEnd);
			
			for(int i = lineStart; i <= lineEnd; i++){
				Rect rect = new Rect();
				layout.getLineBounds(i, rect);
				if(i == lineStart){
					rect.left = (int) layout.getPrimaryHorizontal(start);
				}
				if(i == lineEnd){
					rect.right = (int) layout.getSecondaryHorizontal(end);
				}
				
				Log.d("RECT" + i, rect.toString());
				
				lines[i - lineStart] = rect;
			}
		}else{
			Log.d("ROUND BACKGROUND", "lineStart:" + lineStart + " lineEnd:" + lineEnd);
			lines = new Rect[1];
			Rect rect = new Rect();
			layout.getLineBounds(lineStart, rect);
			rect.left = (int) layout.getPrimaryHorizontal(start);
			rect.right = (int) layout.getSecondaryHorizontal(end);
			
			Log.d("RECT0", rect.toString());
			
			lines[0] = rect;
		}
	}

	@Override
	public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
		
		int origColor = p.getColor();
		p.setColor(color);
		
		c.save();
		for(int i = 0; i <= lineEnd - lineStart; i++){
			Rect rect = lines[i];
			mShape.resize(rect.width(), rect.height());
			
			if(i == 0){
				c.translate(rect.left, rect.top);
			}else{
				c.translate(-lines[0].left, rect.height());
			}
			
			mShape.draw(c, p);
			
		}
		c.restore();
		
		p.setColor(origColor);
	}

}
