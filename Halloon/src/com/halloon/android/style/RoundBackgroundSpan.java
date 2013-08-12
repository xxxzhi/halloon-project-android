package com.halloon.android.style;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.halloon.android.widget.ButtonStyleTextView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.Layout;
import android.util.Log;
import android.widget.TextView;

/**
 * 
 * <B>RoundBackgroundSpan</B> provide a roundRect background for Text to present a click effect just like official tecent microblog client;</br></br>
 * 
 * to achieve this effect, it recalculate layout params for each lines in the TextView that being clicked.</br>
 * 
 * for me it's just a temporally solution, still finding a better way.</br></br>
 * 
 * also, I hope someone in our <B>Halloon</B> group can figure out a better way to do that.</br>
 * 
 * @author airbus</br></br>
 */

public class RoundBackgroundSpan {
	
	private RoundRectShape mShape;
	
	private int lineStart;
	private int lineEnd;
	
	private Rect[] lines;
	
	private Paint paint;
	
	private ButtonStyleTextView tv;
	
	private int start;
	private int end;

	public RoundBackgroundSpan(ButtonStyleTextView tv, int color, float round, int start, int end) {
		
		this.tv = tv;
		
		this.start = start;
		this.end = end;
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		
		mShape = new RoundRectShape(new float[]{round, round, round, round,
				                                round, round, round, round}, null, null);
		
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
					}else{
						float[] width = new float[1];
						String t = layout.getText().subSequence(layout.getLineEnd(i) - 1, layout.getLineEnd(i)).toString();
						layout.getPaint().getTextWidths(t, width);
						rect.right = (int) (layout.getSecondaryHorizontal(layout.getLineEnd(i) - 1) + width[0]);
					}
					
					
					Log.d("RECT", rect.toString());
					
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
			
			//calculate x & y for padding
			int x = tv.getCompoundPaddingLeft();
			int y = tv.getTotalPaddingTop();
			
			canvas.save();
			
			canvas.translate(x, y);
			
			for(int i = 0; i <= lineEnd - lineStart; i++){
				Rect rect = lines[i];
				mShape.resize(rect.width(), rect.height());
				
				if(i == 0){
					canvas.translate(rect.left, rect.top);
				}else{
					canvas.translate(-lines[i - 1].left, -lines[i - 1].top);
					canvas.translate(rect.left, rect.top);
				}
				
				mShape.draw(canvas, paint);
				
			}
			
			canvas.restore();
		}
	}


}
