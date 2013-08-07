/*
 * 2013 哈恩项目  http://www.halloon.com
 * 
 * 进度条组件
 * 
 * caifangmao8@gmail.com
 */
package com.halloon.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.halloon.android.R;
import com.halloon.android.util.DensityUtil;

public class HalloonProgressBar extends View {

	private static final String TAG = "HalloonProgressBar";

	private int brightColor;
	private int darkColor;

	private int maxValue;
	private int minValue;
	private float currentValue;
	private int tagSpace;
	private int tagWidth;
	private int tagHeight;

	private int tagMargin;

	private int viewWidth = 200;
	private int viewHeight = 4;

	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint foreGroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Shader shader;
	private Shader moveShader;

	private Matrix matrix = new Matrix();

	private RectF rect = new RectF();
	private Rect fontRect = new Rect();

	private int progressBarHeight;

	private boolean growDirection = true;
	private float growOffset;
	private String progressPercent;

	private boolean showProgressTag = true;

	public HalloonProgressBar(Context context) {
		this(context, null);
	}

	public HalloonProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HalloonProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HalloonProgressBar);

		brightColor = typedArray.getColor(R.styleable.HalloonProgressBar_brightColor, 0xFF0085DC);
		darkColor = typedArray.getColor(R.styleable.HalloonProgressBar_darkColor, 0xFF025388);
		maxValue = typedArray.getInt(R.styleable.HalloonProgressBar_maxValue, 100);
		minValue = typedArray.getInt(R.styleable.HalloonProgressBar_minValue, 0);
		currentValue = typedArray.getFloat(R.styleable.HalloonProgressBar_currentValue, 0);

		showProgressTag = typedArray.getBoolean(R.styleable.HalloonProgressBar_showTag, true);

		if (!isInEditMode()) {
			progressBarHeight = DensityUtil.dip2px(context, 4F);
		} else {
			progressBarHeight = 4;
		}

		if (!isInEditMode()) {
			tagMargin = DensityUtil.dip2px(context, 6F);
		} else {
			tagMargin = 6;
		}

		int tmp_tagSpace = (int) typedArray.getDimension(R.styleable.HalloonProgressBar_heightToTag, -1);
		if (tmp_tagSpace != -1) {
			tagSpace = tmp_tagSpace;
		} else {
			if (!isInEditMode()) {
				tagSpace = DensityUtil.dip2px(context, 10F);
			} else {
				tagSpace = 10;
			}
		}

		int tmp_tagHeight = (int) typedArray.getDimension(R.styleable.HalloonProgressBar_tagHeight, -1);

		typedArray.recycle();

		if (tmp_tagHeight != -1) {
			tagHeight = tmp_tagHeight;
		} else {
			if (!isInEditMode()) {
				tagHeight = DensityUtil.dip2px(context, 20F);
			} else {
				tagHeight = 20;
			}
		}

		shader = new LinearGradient(0, viewHeight - progressBarHeight - 1, 0, progressBarHeight, new int[] { brightColor, darkColor }, null, Shader.TileMode.REPEAT);
		moveShader = new LinearGradient(0, viewHeight - progressBarHeight - 1, progressBarHeight * 2, viewHeight + progressBarHeight, new int[] { 0x50000000, 0x50000000, 0x000000, 0x000000 }, new float[] { 0F, 0.5F, 0.5F, 1F }, Shader.TileMode.REPEAT);

	}

	public void showProgressTag(boolean show) {
		showProgressTag = show;
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if (heightMeasureSpec <= tagHeight + tagSpace + progressBarHeight && showProgressTag) {
			heightMeasureSpec = tagHeight + tagSpace + progressBarHeight;
			setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		}

		if (heightMeasureSpec <= progressBarHeight && !showProgressTag) {
			heightMeasureSpec = progressBarHeight;
			setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		}

		viewWidth = getMeasuredWidth();
		viewHeight = getMeasuredHeight();

	}

	@Override
	protected void onDraw(Canvas canvas) {
		float progress = currentValue / (maxValue - minValue) * viewWidth;
		System.out.println(currentValue / (maxValue - minValue));

		textPaint.setColor(0xFF111111);
		textPaint.setTextSize(tagHeight * 0.8F);
		progressPercent = currentValue + "%";

		textPaint.getTextBounds(progressPercent, 0, progressPercent.length(), fontRect);

		tagWidth = (int) (fontRect.width() * 1.2F);

		rect.top = viewHeight - progressBarHeight;
		rect.left = 0;
		rect.right = viewWidth;
		rect.bottom = viewHeight;
		paint.setColor(0xFF000000);
		canvas.drawRoundRect(rect, progressBarHeight / 2, progressBarHeight / 2, paint);

		rect.right = progress;
		rect.bottom = viewHeight;
		foreGroundPaint.setShader(shader);

		canvas.drawRoundRect(rect, progressBarHeight / 2, progressBarHeight / 2, foreGroundPaint);

		if (currentValue >= maxValue) {
			growOffset = rect.top;
			paint.setColor(0xFFFFFFFF);
			if (growDirection) {
				growOffset -= 1F;
			} else {
				growOffset += 1F;
			}
			if (growOffset <= rect.top) {
				growDirection = false;
			} else if (growOffset >= rect.bottom) {
				growDirection = true;
			}
			matrix.postTranslate(0, growOffset);
			moveShader.setLocalMatrix(matrix);
			foreGroundPaint.setShader(moveShader);

			canvas.drawRoundRect(rect, progressBarHeight / 2, progressBarHeight / 2, foreGroundPaint);

			invalidate();
		}

		if (showProgressTag) {
			if (currentValue < maxValue) {
				rect.top = 0;
				if (progress < (tagWidth / 2) + tagMargin) {
					rect.left = tagMargin;
				} else if (progress > viewWidth - ((tagWidth / 2) + tagMargin)) {
					rect.left = viewWidth - tagMargin - tagWidth;
				} else {
					rect.left = progress - (tagWidth / 2);
				}
				rect.right = rect.left + tagWidth;
				rect.bottom = tagHeight;
				paint.setColor(0xFFCCCCCC);
				paint.setShadowLayer(4F, 0F, 5F, 0x99000000);
				canvas.drawRoundRect(rect, progressBarHeight / 2, progressBarHeight / 2, paint);

				paint.setShadowLayer(0, 0, 0, 0);

				if (progress < (tagWidth / 2) + tagMargin) {
					rect.left = tagMargin + (tagWidth - fontRect.width()) / 2;
				} else if (progress > viewWidth - ((tagWidth / 2) + tagMargin)) {
					rect.left = viewWidth - tagMargin - tagWidth + (tagWidth - fontRect.width()) / 2;
				} else {
					rect.left = progress - fontRect.width() / 2;
				}

				canvas.drawText(progressPercent, rect.left, tagHeight - (tagHeight - fontRect.height()) / 2, textPaint);
			}
		}

	}

	public void setProgress(float per) {
		if (minValue > per) {
			per = minValue;
		} else if (per > maxValue) {
			per = maxValue;
		} else {
			currentValue = per;
		}
		invalidate();
	}

}
