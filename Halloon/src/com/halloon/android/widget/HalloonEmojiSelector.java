/*
 * 2013 哈恩项目  http://www.halloon.com
 * 
 * 表情选择组件
 * 
 * caifangmao8@gmail.com
 */
package com.halloon.android.widget;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.halloon.android.R;
import com.halloon.android.listener.OnEmojiSelectedListener;
import com.halloon.android.util.EmojiContainer;
import com.halloon.android.util.CirclePageIndicator;

public class HalloonEmojiSelector extends LinearLayout{
	
	private static final String TAG = "HalloonEmojiSelector";
	
	private CirclePageIndicator circlePageIndicator;
	private ViewPager pageContainer;
	private int emojiCount;
	
	private LayoutInflater inflater;
	
	private OnEmojiSelectedListener onEmojiSelectedListener;

	public HalloonEmojiSelector(Context context) {
		super(context);
		init(context);
	}
	
	public HalloonEmojiSelector(Context context, AttributeSet attrs){
		super(context, attrs);
		init(context);
	}
	
	public HalloonEmojiSelector(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		init(context);
	}
	
	public void setOnEmojiSelectedListener(OnEmojiSelectedListener onEmojiSelectedListener){
		this.onEmojiSelectedListener = onEmojiSelectedListener;
	}
	
	public OnEmojiSelectedListener getOnEmojiSelectedListener(){
		return onEmojiSelectedListener;
	}
	
	private void init(final Context context){
		emojiCount = EmojiContainer.emoNameContainer.length;
		inflater = LayoutInflater.from(getContext());
		View root = inflater.inflate(R.layout.emoji_selector, this);
		
		circlePageIndicator = (CirclePageIndicator) root.findViewById(R.id.indicator);
		pageContainer = (ViewPager) root.findViewById(R.id.emoji_pager);
		
		pageContainer.setAdapter(new PagerAdapter(){
			private View layout;

			@Override
			public int getCount() {
				//通过表情数量计算viewpager的页面数量， 每页23个表情  加  一个退格键
				int pageCount = emojiCount / 23;
				return (int) ((emojiCount % 23) == 0 ? pageCount : pageCount + 1);
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}
			
			@Override
			public int getItemPosition(Object object){
				return super.getItemPosition(object);
			}
			
			@Override
			public void destroyItem(View view, int position, Object object){
				View v = (View) object;
				((ViewPager) view).removeView(v);
				v = null;
				
			}
			
			@Override
			public Object instantiateItem(View view, int position){
				final int p = position;
				layout = inflater.inflate(R.layout.emoji_item, null);
				GridView gridView = (GridView)layout.findViewById(R.id.emoji_grid);
				gridView.setAdapter(new ImageAdapter(context, position));
				gridView.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if(position < 23 && position + (p * 23) < EmojiContainer.emoNameContainer.length){
							if(onEmojiSelectedListener != null){
								String emojiName = "/" + EmojiContainer.emoNameContainer[position + (p * 23)];
								EditText publishText = onEmojiSelectedListener.getEditText();
								
								int drawableId = EmojiContainer.getEmojiId(context, position + (p * 23));
								int size = (int) publishText.getPaint().getTextSize();
								
								Drawable drawable = context.getResources().getDrawable(drawableId);
								drawable.setBounds(0, 0, size, size);
								SpannableString ss = new SpannableString(emojiName);
								ss.setSpan(new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM), 0, emojiName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
								publishText.getText().replace(publishText.getSelectionStart(), publishText.getSelectionEnd(), ss);
							}
						}
					}
					
				});
				
				((ViewPager) view).addView(layout);
				
				return layout;
			}
			
			@Override
			public void restoreState(Parcelable parcelable, ClassLoader classLoader){
				
			}
			
			@Override
			public Parcelable saveState(){
				return null;
			}
			
			@Override
			public void startUpdate(View view){
			
			}
			
			@Override
			public void finishUpdate(View view){
				
			}
		});
		
		pageContainer.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}

			@Override
			public void onPageSelected(int arg0) {
				
			}
			
		});
		
		circlePageIndicator.setFillColor(0xFF888888);
		circlePageIndicator.setViewPager(pageContainer);
		
	}
	
	private class ImageAdapter extends BaseAdapter{
		
		private Context context;
		private final int[] imageList = new int[24];
		
		public ImageAdapter(Context context, int i){
			this.context = context;
			for(int j = 0; j < 23; j++){
				if((Integer) EmojiContainer.getEmojiId(context, j + (i * 23)) == null) break;
				imageList[j] = EmojiContainer.getEmojiId(context, j + (i * 23));
			}
		}

		@Override
		public int getCount() {
			return 24;
		}

		@Override
		public Integer getItem(int position) {
			if(position < 23){
				return imageList[position];
			}else{
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if(position < 23){
				convertView = inflater.inflate(R.layout.emoji_popup_item, null);
				ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
				if(getItem(position) != null){
					imageView.setImageResource(getItem(position));
				}else{
					imageView.setImageDrawable(null);
				}
			}else{
				convertView = inflater.inflate(R.layout.emoji_backspace_item, null);
				final Button backButton = (Button) convertView.findViewById(R.id.backspace_button);
				
				//重写退格键的onTouch，以实现长按连续退格效果
				backButton.setOnTouchListener( new OnTouchListener(){
					MyHandler mHandler = new MyHandler(onEmojiSelectedListener);
					Thread thread = null;
					boolean isTouched = false;

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch(event.getAction()){
						case MotionEvent.ACTION_DOWN:
							backButton.setBackgroundResource(R.drawable.arrow_left_button_pressed);
							isTouched = true;
							//实现退格键长按效果
							thread = new Thread(){
								int countDown = 1000;
								@Override
								public void run(){
									while(isTouched){
										System.out.println("thread is running!");
										Message msg = new Message();
										msg.what = 0;
										mHandler.sendMessage(msg);
										if(countDown >= 70) countDown *= 0.6F;
										try {
											Thread.sleep(countDown);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
									return;
								}
							};
							thread.start();
							break;
						case MotionEvent.ACTION_UP:
							backButton.setBackgroundResource(R.drawable.arrow_left_button_normal);
							isTouched = false;
							break;
						case MotionEvent.ACTION_CANCEL:
							backButton.setBackgroundResource(R.drawable.arrow_left_button_normal);
							isTouched = false;
						}
						
						return true;
					}
					
				});
			}
			
			return convertView;
		}
		
	}
	
	private static class MyHandler extends Handler{
		WeakReference<OnEmojiSelectedListener> onEmojiSelectedListener;
		
		public MyHandler(OnEmojiSelectedListener onEmojiSelectedListener){
			this.onEmojiSelectedListener = new WeakReference<OnEmojiSelectedListener>(onEmojiSelectedListener);
		}
		@Override
		public void handleMessage(Message msg){
			OnEmojiSelectedListener mOnEmojiSelectedListener = onEmojiSelectedListener.get();
			switch(msg.what){
			case 0:
				if(mOnEmojiSelectedListener != null){
					EditText publishText = mOnEmojiSelectedListener.getEditText();
					if(publishText.length() > 0){
						int start = publishText.getSelectionStart();
						int end = publishText.getSelectionEnd();
						if(start != end){
							publishText.getText().delete(start, end);
						}else{
							
							if(start != 0){
								Editable text = publishText.getText();
								ImageSpan[] imageSpans = text.getSpans(start - 1, end, ImageSpan.class);
								if(imageSpans.length != 0){
									ImageSpan imageSpan = imageSpans[0];
									int spanStart = text.getSpanStart(imageSpan);
									int spanEnd = text.getSpanEnd(imageSpan);
									text.removeSpan(imageSpans[0]);
									text.delete(spanStart, spanEnd);
									break;
								}
								
								publishText.getText().delete(start - 1, start);
							}
							
						}
					}
				}
				break;
			}
		}
	}
}
