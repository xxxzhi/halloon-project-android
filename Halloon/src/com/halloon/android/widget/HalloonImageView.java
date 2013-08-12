/*
 * 2013 哈恩项目  http://www.halloon.com
 * 
 * 多点缩放图片组件
 * 
 * caifangmao8@gmail.com
 */
package com.halloon.android.widget;

import com.ant.liao.GifDecoder;
import com.halloon.android.data.SettingsManager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;

public class HalloonImageView extends ImageView {
	
	private static final String TAG = "HalloonImageView";
	
	private Matrix matrix;
	private Matrix savedMatrix;
	private PointF start;
	private PointF middle;
	private PointF scaleMid;
	private PointF newMid;
	private float oldDistance;
	private GifDecoder gifDecoder;
	private float scale;
	private float originScale;
	
	private int originWidth;
	private int originHeight;
	
	private int width;
	private int height;
	
	private float oldPosX;
	private float oldPosY;
	
	private float vx;
	private float vy;
	
	private static final long DOUBLE_CLICK_PERSISTENCE_TIME = 700L;
	private long firstClick = 0L;
	private boolean isScaled;
	private Handler handler;
	private MyRunnable runnable;
	
	private float[] mMatrix;
	
	private int notificationBarHeight;
	
	private AnimationDrawable animationDrawable;
	
	//image scale mode
	private static final int NONE = 0x00000000;
	private static final int DRAG = 0x00000001;
	private static final int ZOOM = 0x00000002;
	
	private int touch_mode = NONE;
	
	public static final int STATIC_MODE = 0x00000000;
	public static final int PLAY_GIF = 0x00000001;
	public static final int GESTURE_ENABLE = 0x00000002;
	
	private int mode = GESTURE_ENABLE;

	
	public HalloonImageView(Context context){
		this(context, null);
	}
	
	public HalloonImageView(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public HalloonImageView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
		if(!isInEditMode()) notificationBarHeight = SettingsManager.getInstance(context).getSystemBarHeight();
		
		setScaleType(ScaleType.MATRIX);
		
	}
	
	public void setMode(int mode){
		this.mode = mode;
		if((mode & GESTURE_ENABLE) == GESTURE_ENABLE ){
			matrix = new Matrix();
			savedMatrix = new Matrix();
			start = new PointF();
			middle = new PointF();
			scaleMid = new PointF();
			newMid = new PointF();
			
			scale = 1;
			
			originWidth = 0;
			originHeight = 0;
			
			width = getWidth();
			height = getHeight();
			
			vx = 0;
			vy = 0;
			
			oldPosX = 0;
			oldPosY = 0;
			
			isScaled = false;
			handler = new Handler();
			
			mMatrix = new float[9];
		}
	}
	
	private class MyRunnable implements Runnable{
		
		//here's a little trick to scale the image on the right coordinate by produce 2 fake touch point;
		private float fakeY1;
		private float fakeY2;
		
		private final float x;
		private final float y;
		
		private final float originalDistance;
		private final float destDistance;
		private float newDistance;
		
		private float scale;
		
		private PointF scalePoint;
		private PointF newPoint;
		
		public MyRunnable(float x, float y){
			savedMatrix.set(matrix);
			
			originalDistance = 20;
			newDistance = originalDistance;
			
			if(isScaled){
				destDistance = 10;
				isScaled = false;
			}else{
				destDistance = 40;
				isScaled = true;
			}
			
			this.x = x;
			this.y = y;
			
			fakeY1 = y - originalDistance / 2;
			fakeY2 = y + originalDistance / 2;
			
			scalePoint = new PointF();
			newPoint = new PointF();
		}
		
		@Override
		public void run(){
			matrix.set(savedMatrix);
			newDistance += (destDistance - newDistance) * 0.5F;
			scale = newDistance / originalDistance;
			matrix.postScale(scale, scale, 0, 0);
			scalePoint.x = x * scale;
			scalePoint.y = y * scale;
			newPoint = midPoint(new PointF(x, fakeY1), new PointF(x, fakeY2));
			matrix.postTranslate(newPoint.x - scalePoint.x, newPoint.y - scalePoint.y);
			
			setImageMatrix(matrix);
			
			if(newDistance != destDistance){
				handler.postDelayed(runnable, 1);
			}
		}
	}
	
	public void setGifDecoder(GifDecoder gifDecoder){
		this.gifDecoder = gifDecoder;
		animationDrawable = new AnimationDrawable();
	}
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		width = getMeasuredWidth();
		height = getMeasuredHeight();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if((mode & GESTURE_ENABLE) == GESTURE_ENABLE){
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				oldPosX = event.getX();
				oldPosY = event.getY();
				if(firstClick == 0L){
					firstClick = System.currentTimeMillis();
				}else{
					if(System.currentTimeMillis() - firstClick < DOUBLE_CLICK_PERSISTENCE_TIME){
						performDoubleClickEffect(oldPosX, oldPosY);
					}
					firstClick = 0L;
				}
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
				touch_mode = DRAG;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				oldPosX = event.getX();
				oldPosY = event.getY();
				
				oldDistance = spacing(event);
				if (oldDistance > 10f) {
					savedMatrix.set(matrix);
					middle = midPoint(event);
					touch_mode = ZOOM;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				stopDoubleClickEffectPerformance();
				
				if (touch_mode == DRAG) {
					matrix.set(savedMatrix);
					matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
					vx = event.getX() - oldPosX;
					vy = event.getY() - oldPosY;
					
					oldPosX = event.getX();
					oldPosY = event.getY();
				} else if (touch_mode == ZOOM) {
					float newDistance = spacing(event);
					if (newDistance > 10f) {
						matrix.set(savedMatrix);
						scale = newDistance / oldDistance;
						matrix.postScale(scale, scale, 0, 0);
						scaleMid.x = middle.x * scale;
						scaleMid.y = middle.y * scale;
						newMid = midPoint(event);
						matrix.postTranslate(newMid.x - scaleMid.x, newMid.y - scaleMid.y);
					}
					
				}
				
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_POINTER_UP:
				touch_mode = NONE;
				if(vx != 0 || vy != 0) invalidate();
				break;
			}
			setImageMatrix(matrix);
		}
		
		return true;
	}
	
	//双击
	public void performDoubleClickEffect(float x, float y){
		runnable = new MyRunnable(x, y);
		handler.post(runnable);
	}
	
	public void stopDoubleClickEffectPerformance(){
		handler.removeCallbacks(runnable);
	}
	
	//初始化缩放
	public void setOriginLayout(int width, int height){
		this.originWidth = width;
		this.originHeight = height;
		
		initImage();
	}
	
	private void initImage(){
		if(((mode & PLAY_GIF) == PLAY_GIF) && gifDecoder != null && gifDecoder.parseOk()){
			for(int i = 0; i < gifDecoder.getFrameCount(); i++){
				animationDrawable.addFrame(new BitmapDrawable(getResources(), gifDecoder.getFrameImage(i)), gifDecoder.getDelay(i));
			}
			setImageDrawable(animationDrawable);
			animationDrawable.start();
			animationDrawable.setOneShot(false);
		}
		matrix = new Matrix();
		
		if(originHeight > height && width / originWidth * originHeight > height){
			scale = (float) width / (float) originWidth;
			matrix.postScale(scale, scale, 0, 0);
			matrix.postTranslate((width - (originWidth * scale)) / 2, 0);
		}else{
			if(width / originWidth * originHeight < height){
				scale = (float) width / (float) originWidth;
			}else{
				scale = (float) height / (float) originHeight;
			}
			matrix.postScale(scale, scale, 0, 0);
			matrix.postTranslate((width - (originWidth * scale)) / 2, (height - (originHeight * scale)) / 2);
		}
		originScale = scale;
		
		setImageMatrix(matrix);
	}
	
	//重写setImageMatrix以规定图片缩放和移动的范围
	@Override
	public void setImageMatrix(Matrix matrix){
		matrix.getValues(mMatrix);
		
		if(mMatrix[0] < originScale || mMatrix[4] < originScale) mMatrix[0] = mMatrix[4] = originScale;
		
		if(mMatrix[0] * originWidth >= width){
			if(mMatrix[2] > 0) mMatrix[2] = 0;
			if(-mMatrix[2] > (mMatrix[0] * originWidth) - width) mMatrix[2] = -((mMatrix[0] * originWidth) - width);
		}else{
			if(mMatrix[2] < 0) mMatrix[2] = 0;
			if(-mMatrix[2] < (mMatrix[0] * originWidth) - width) mMatrix[2] = -((mMatrix[0] * originWidth) - width);
		}
		
		if(mMatrix[4] * originHeight >= height){
			if(mMatrix[5] > notificationBarHeight) mMatrix[5] = notificationBarHeight;
			if(-mMatrix[5] > (mMatrix[0] * originHeight) - height) mMatrix[5] = -((mMatrix[0] * originHeight) - height);
		}else{
			if(mMatrix[5] < notificationBarHeight) mMatrix[5] = notificationBarHeight;
			if(-mMatrix[5] < (mMatrix[0] * originHeight) - height) mMatrix[5] = -((mMatrix[0] * originHeight) - height);
		}
		
		matrix.setValues(mMatrix);
		
		super.setImageMatrix(matrix);
		
	}
	
	//重写onDraw以实现图片惯性移动效果
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
		 if(touch_mode == NONE){
			matrix.postTranslate(vx, vy);
			vx *= 0.95F;
			vy *= 0.95F;
			
			setImageMatrix(matrix);
			invalidate();
		}
		 
	}
	
	//得到两个触摸点的距离
	private float spacing(MotionEvent event){
		PointF point1 = new PointF(event.getX(0), event.getY(0));
		PointF point2 = new PointF(event.getX(1), event.getY(1));
		
		return spacing(point1, point2);
	}
	
	private float spacing(PointF point1, PointF point2){
	    float x = point1.x - point2.x;
	    float y = point1.y - point2.y;
	    
	    return FloatMath.sqrt(x * x + y * y);
	}
	
	//得到两个触摸点的中点
	private PointF midPoint( MotionEvent event){
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		
		return new PointF(x / 2, y / 2);
	}
	
	private PointF midPoint(PointF point1, PointF point2){
		float x = point1.x + point2.x;
		float y = point1.y + point2.y;
		
		return new PointF(x / 2, y / 2);
	}
	
}
