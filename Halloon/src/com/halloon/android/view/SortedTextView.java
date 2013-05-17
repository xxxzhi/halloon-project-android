package com.halloon.android.view;

import java.util.Arrays;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class SortedTextView extends View {
	private Paint mPaint = new Paint();

	public SortedTextView(Context context) {
		this(context, null);
	}

	public SortedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SortedTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	private void init() {
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Style.STROKE);
		mPaint.setTextSize(getRawSize(TypedValue.COMPLEX_UNIT_DIP, 12));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// setMeasuredDimension(300, 200);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// canvas.drawColor(Color.WHITE);

		FontMetrics fm = mPaint.getFontMetrics();

		float baseline = fm.descent - fm.ascent;
		float x = 0;
		float y = baseline;

		String txt1 = getResources().getString(
				com.halloon.android.R.string.account);
		//
		//

		String[] text1s = autoSplit(txt1, mPaint, getWidth() - 15);
		//
		//

		System.out.printf("line indexs: %s\n", Arrays.toString(text1s));
		//
		//

		for (String text1 : text1s) {
			canvas.drawText(text1, x, y, mPaint);
			y += baseline + fm.leading;
		}
		/*
		 * y += 20;
		 */

	}

	private String[] autoSplit(String content, Paint p, float width) {
		int length = content.length();
		float textWidth = p.measureText(content);
		if (textWidth <= width) {
			return new String[] { content };
		}

		int start = 0, end = 1, i = 0;
		int lines = (int) Math.ceil(textWidth / width);
		String[] lineTexts = new String[lines];
		while (start < length) {
			if (p.measureText(content, start, end) > width) {
				lineTexts[i++] = (String) content.subSequence(start, end);
				break;
			}

			end += 1;

		}

		return lineTexts;
	}

	public float getRawSize(int unit, float size) {
		Context c = getContext();
		Resources r;

		if (c == null)
			r = Resources.getSystem();
		else
			r = c.getResources();

		return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
	}
}