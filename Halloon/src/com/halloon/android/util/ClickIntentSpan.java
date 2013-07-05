package com.halloon.android.util;

import android.content.Context;
import android.content.Intent;
import android.text.style.ClickableSpan;
import android.view.View;

public class ClickIntentSpan extends ClickableSpan{

	private Intent intent;
	private Context context;

	public ClickIntentSpan(Context context, Intent intent) {
		this.intent = intent;
		this.context = context;
	}

	@Override
	public void onClick(View widget) {
		context.startActivity(intent);
	}

}
