package com.halloon.android.widget;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class ButtonStyleTextView extends TextView {
	
	private OnTouchDownListener mOnTouchDownListener;
	
	public interface OnTouchDownListener{
		public void onDown(Spannable spannable, int start, int end);
	}

	public ButtonStyleTextView(Context context){
		super(context);
	}
	
	public ButtonStyleTextView(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public ButtonStyleTextView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
        if(getText() instanceof SpannableString && handleMotionEvent(event) && getMovementMethod() != null){
        	return super.onTouchEvent(event);
        }else{
        	setFocusable(false);
        	return false;
        }
	}
	
	private boolean handleMotionEvent(MotionEvent event){
		
		int action = event.getAction();
		SpannableString buffer = (SpannableString) getText();

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= getTotalPaddingLeft();
            y -= getTotalPaddingTop();

            x += getScrollX();
            y += getScrollY();

            Layout layout = getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                Log.d("TOUCH DOWN", "true");
                return true;
            }
        }
        
        return false;
	}
}
