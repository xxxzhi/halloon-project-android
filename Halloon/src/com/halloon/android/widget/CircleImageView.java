/**
 * 
 */
package com.halloon.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author houzhi
 *
 */
public class CircleImageView extends ImageView {
	Path path ;
	
	private PaintFlagsDrawFilter mPaintFlagsDrawFilter ;
	Paint paint ; 
	
	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}

	public CircleImageView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		//skip reinit.
		if(mPaintFlagsDrawFilter != null) return ;
		
		
		mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setColor(Color.WHITE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		float h = getMeasuredHeight() - 3.0f;
		float w = getMeasuredWidth() - 3.0f ;
		
		if (path == null){
			path = new Path();
			path.addCircle(w/2, h/2, (float)Math.min(w/2, h/2), Path.Direction.CCW);
			path.close();
		}
		canvas.drawCircle(w/2, h/2, (float)Math.min(w/2, h/2) + 1.5f, paint);
		int saveCount = canvas.getSaveCount();
		canvas.save();
		
		canvas.setDrawFilter(mPaintFlagsDrawFilter);
		canvas.clipPath(path,Region.Op.REPLACE);
		canvas.setDrawFilter(mPaintFlagsDrawFilter);
		canvas.drawColor(Color.WHITE);
		
		super.onDraw(canvas);
		
		canvas.restoreToCount(saveCount);
	}
	
	
}
