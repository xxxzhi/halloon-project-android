package com.halloon.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class ImageUtil {

	private Context context;
	private static ImageUtil instance;

	public Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private PointF newMid = new PointF();
	private PointF scaleMid = new PointF();
	private float oldDist;

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	private int mode = NONE;

	public ImageUtil(Context context) {
		this.context = context;
	}

	public static ImageUtil getInstance(Context context) {
		if (instance == null) {
			instance = new ImageUtil(context);
		}
		return instance;
	}

	public ImageView.OnTouchListener getImageTranslateListener() {
		ImageView.OnTouchListener touchListener = new ImageView.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_UP:
					break;
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						matrix.set(savedMatrix);
						matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
					} else if (mode == ZOOM) {
						float newDist = spacing(event);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, 0, 0);
							scaleMid.x = mid.x * scale;
							scaleMid.y = mid.y * scale;
							midPoint(newMid, event);
							matrix.postTranslate(newMid.x - scaleMid.x, newMid.y - scaleMid.y);
						}
					}
					break;
				}
				((ImageView) v).setImageMatrix(matrix);
				return true;
			}

			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return FloatMath.sqrt(x * x + y * y);
			}

			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}
		};

		return touchListener;
	}

	/**
	 * 把图片转换成圆角图片
	 * 
	 * @param bitmap
	 *            要转换的图片
	 * @param pixels
	 *            圆角的大小
	 * 
	 * @return 圆角Bitmap
	 * 
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, pixels, pixels, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

}
