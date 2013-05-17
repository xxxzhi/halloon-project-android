package com.halloon.android.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class ScrollTextView extends TextView {

	public ScrollTextView(Context context) {
		this(context, null);
	}

	public ScrollTextView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.textViewStyle);
	}

	public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFocusChanged(boolean focus, int direction, Rect previouslyFocusRect) {
		if (focus)
			super.onFocusChanged(focus, direction, previouslyFocusRect);
	}

	@Override
	public void onWindowFocusChanged(boolean focus) {
		if (focus)
			super.onWindowFocusChanged(focus);
	}

	@Override
	public boolean isFocused() {
		return true;
	}
}
