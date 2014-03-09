/**
 * 
 */
package com.halloon.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author houzhi
 * 
 */
public class CircleImageView extends ImageView {
	Path path;

	private PaintFlagsDrawFilter mPaintFlagsDrawFilter;
	Paint paint;

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

	private void init() {
		// skip reinit.
		if (mPaintFlagsDrawFilter != null)
			return;

		mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0,
				Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setColor(Color.WHITE);
	}

	// @Override
	// protected void onDraw(Canvas canvas) {
	//
	// float h = getMeasuredHeight() - 3.0f;
	// float w = getMeasuredWidth() - 3.0f ;
	//
	// if (path == null){
	// path = new Path();
	// path.addCircle(w/2, h/2, (float)Math.min(w/2, h/2), Path.Direction.CCW);
	// path.close();
	// }
	// canvas.drawCircle(w/2, h/2, (float)Math.min(w/2, h/2) + 1.5f, paint);
	// int saveCount = canvas.getSaveCount();
	// canvas.save();
	//
	// canvas.setDrawFilter(mPaintFlagsDrawFilter);
	// canvas.clipPath(path,Region.Op.REPLACE);
	// canvas.setDrawFilter(mPaintFlagsDrawFilter);
	// canvas.drawColor(Color.WHITE);
	//
	// super.onDraw(canvas);
	//
	// canvas.restoreToCount(saveCount);
	// }
	//

	@Override
	protected void onDraw(Canvas canvas) {

		Drawable drawable = getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}

		Bitmap b = ((BitmapDrawable) drawable).getBitmap();
		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

		int w = getWidth(), h = getHeight();

		Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
		canvas.drawBitmap(roundBitmap, 0, 0, null);

	}

	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		Bitmap sbmp;
		if (bmp.getWidth() != radius || bmp.getHeight() != radius)
			sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		else
			sbmp = bmp;
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f,
				sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);

		return output;
	}

}
