package com.halloon.android.widget;

import com.halloon.android.R;
import com.halloon.android.style.ImageIdSpan;
import com.halloon.android.style.RoundBackgroundSpan;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class ButtonStyleTextView extends TextView {
	
	private OnTouchDownListener mOnTouchDownListener;
	
	private ClickableSpan linkSpan;
	private RoundBackgroundSpan backgroundSpan;
	
	private boolean isRoundBackgroundSpanDraw = false;
	
	private int style = 0;
	
	public static final int      SPAN_STYLE_TOPIC_MENTION = 0x00000001;
	public static final int             SPAN_STYLE_SEARCH = 0x00000002;
	public static final int          SPAN_STYLE_VIDEOADDR = 0x00000004;
	public static final int          SPAN_STYLE_MUSICADDR = 0x00000008;
	public static final int         SPAN_STYLE_NORMALADDR = 0x00000010;
	
	public interface OnTouchDownListener{
		public void onDown(ButtonStyleTextView tv, SpannableString spannable, int start, int end, int style);
		public void onUp(ButtonStyleTextView tv, SpannableString spannable, int start, int end, int style);
	}

	public ButtonStyleTextView(Context context){
		this(context, null);
	}
	
	public ButtonStyleTextView(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public ButtonStyleTextView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
        if(getText() instanceof SpannableString && handleMotionEvent(event) && getMovementMethod() != null){
        	return super.onTouchEvent(event);
        }else{
        	return false;
        }
	}
	
	public void setOnTouchDownListener(OnTouchDownListener onTouchDownListener){
	    mOnTouchDownListener = onTouchDownListener;
	}
	
	public OnTouchDownListener getOnDownListener(){
		return mOnTouchDownListener;
	}
	
	private boolean handleMotionEvent(MotionEvent event){
		
		int action = event.getAction();
		SpannableString buffer = (SpannableString) getText();
		
		if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL){
			isRoundBackgroundSpanDraw = false;
			if(style != 0){
				mOnTouchDownListener.onUp(this, buffer, buffer.getSpanStart(linkSpan), buffer.getSpanEnd(linkSpan), style);
				style = 0;
				return true;
				
			}
		}

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
            ForegroundColorSpan[] foregroundColor = buffer.getSpans(off, off, ForegroundColorSpan.class);
            RoundBackgroundSpan[] backgroundSpans = buffer.getSpans(off, off, RoundBackgroundSpan.class);
            ImageIdSpan[] image = buffer.getSpans(off, off, ImageIdSpan.class);
            
            if(backgroundSpans.length != 0){
            	isRoundBackgroundSpanDraw = true;
            	backgroundSpan = backgroundSpans[0];
            }
            
            if (link.length != 0) {
            	linkSpan = link[0];
            	if(mOnTouchDownListener != null){
            		if(foregroundColor.length != 0){
            			if(foregroundColor[0].getForegroundColor() == 0xFF0085DF){
            				style = SPAN_STYLE_TOPIC_MENTION;
            			}else{
            				style = SPAN_STYLE_SEARCH;
            			}
            		}
            		
            		if(image.length != 0){
            			switch(image[0].getDrawableId()){
            			case R.drawable.button_link:
            				style = SPAN_STYLE_NORMALADDR;
            				break;
            			case R.drawable.button_video_link:
            				style = SPAN_STYLE_VIDEOADDR;
            				break;
            			case R.drawable.button_music_link:
            				style = SPAN_STYLE_MUSICADDR;
            				break;
            			}
            		}
            		
            		if(style != 0){
            			switch(action){
            			case MotionEvent.ACTION_DOWN:
            				mOnTouchDownListener.onDown(this, buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]), style);
            				break;
            			case MotionEvent.ACTION_CANCEL:
            			case MotionEvent.ACTION_UP:
            				mOnTouchDownListener.onUp(this, buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]), style);
            				break;
            			}
            			
            		}
            		
            	}
                return true;
            }
        }
        
        return false;
	}
	
	@Override
	public void onDraw(Canvas canvas){
		
		if(isRoundBackgroundSpanDraw && backgroundSpan != null){
			backgroundSpan.updateDrawState(canvas);
		}
		
		super.onDraw(canvas);
	}
	
}
