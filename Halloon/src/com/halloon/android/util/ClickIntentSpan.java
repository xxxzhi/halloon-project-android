package com.halloon.android.util;

import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;

public class ClickIntentSpan extends ClickableSpan implements OnClickListener {

	private final OnClickListener listener;

	public ClickIntentSpan(OnClickListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View widget) {
		listener.onClick(widget);
	}

}
