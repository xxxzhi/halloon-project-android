package com.halloon.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.halloon.android.R;
import com.halloon.android.listener.OnTitleBarClickListener;

public class HalloonTitleBar extends RelativeLayout implements OnClickListener{
	
	private static final String TAG = "HalloonTitleBar";
	
	private Button leftImageButton;
	private Button leftButton;
	private Button rightImageButton;
	private Button rightButton;
	
	private TextView titleText;
	
	private RelativeLayout spliceLine;
	
	private OnTitleBarClickListener onTitleBarClickListener;
	
	public static final int TITLE_STYLE_IMAGE = 0;
	public static final int TITLE_STYLE_NORMAL = 1;
	public static final int TITLE_STYLE_BACK_BUTTON_ONLY = 2;
	public static final int TITLE_STYLE_RIGHT_BUTTON_ONLY = 3;
	public static final int TITLE_STYLE_NONE = 4;
	public static final int TITLE_STYLE_OTHERWISE = 5;
	
	/**
	 * 隐藏标题栏
	 */
	public static final int TITLE_STYLE_HIDE_TITLE = 6;
	
	
	private int titleStyle = TITLE_STYLE_IMAGE;

	public HalloonTitleBar(Context context){
		super(context);
		init(context);
	}
	
	public HalloonTitleBar(Context context, AttributeSet attrs){
		super(context,attrs);
		init(context);
	}
	
	public HalloonTitleBar(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		init(context);
	}
	
	private View root;
	
	private void init(Context context){
		
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		root = layoutInflater.inflate(R.layout.halloon_title_bar, this);
		
		leftImageButton = (Button) root.findViewById(R.id.left_image_button);
		leftButton = (Button) root.findViewById(R.id.left_button);
		rightImageButton = (Button) root.findViewById(R.id.right_image_button);
		rightButton = (Button) root.findViewById(R.id.right_button);
		titleText = (TextView) root.findViewById(R.id.title_text);
		
		spliceLine = (RelativeLayout) root.findViewById(R.id.spliceline_layout);
		
	}
	
	public void setTitleStyle(int titleStyle){
		this.titleStyle = titleStyle;
		
		switch(titleStyle){
		case TITLE_STYLE_IMAGE:
			leftButton.setVisibility(View.GONE);
			rightButton.setVisibility(View.GONE);
			leftImageButton.setVisibility(View.VISIBLE);
			rightImageButton.setVisibility(View.VISIBLE);
			spliceLine.setVisibility(View.VISIBLE);
			break;
		case TITLE_STYLE_NORMAL:
			leftButton.setVisibility(View.VISIBLE);
			rightButton.setVisibility(View.VISIBLE);
			leftImageButton.setVisibility(View.VISIBLE);
			rightImageButton.setVisibility(View.GONE);
			spliceLine.setVisibility(View.GONE);
			break;
		case TITLE_STYLE_BACK_BUTTON_ONLY:
			leftButton.setVisibility(View.VISIBLE);
			rightButton.setVisibility(View.GONE);
			leftImageButton.setVisibility(View.GONE);
			rightImageButton.setVisibility(View.GONE);
			spliceLine.setVisibility(View.GONE);
			break;
		case TITLE_STYLE_RIGHT_BUTTON_ONLY:
			leftButton.setVisibility(View.GONE);
			rightButton.setVisibility(View.VISIBLE);
			leftImageButton.setVisibility(View.GONE);
			rightImageButton.setVisibility(View.GONE);
			spliceLine.setVisibility(View.GONE);
			break;
		case TITLE_STYLE_NONE:
			leftButton.setVisibility(View.GONE);
			leftImageButton.setVisibility(View.GONE);
			rightButton.setVisibility(View.GONE);
			rightImageButton.setVisibility(View.GONE);
			spliceLine.setVisibility(View.GONE);
			break;
		case TITLE_STYLE_HIDE_TITLE:
			root.setVisibility(View.GONE);
			break;
		}
	}
	
	public Button getLeftButton(){
		leftButton.setText(getContext().getString(R.string.back));
		return leftButton;
	}
	
	public Button getRightButton(int id){
		rightButton.setText(getContext().getString(id));
		return rightButton;
	}
	
	public Button getLeftImageButton(){
		return leftImageButton;
	}
	
	public Button getRightImageButton(){
		return rightImageButton;
	}
	
	public TextView getTitleTextView(){
		return titleText;
	}
	
	public void setOnTitleBarClickListener(OnTitleBarClickListener onTitleBarClickListener){
		this.onTitleBarClickListener = onTitleBarClickListener;
		leftButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
		leftImageButton.setOnClickListener(this);
		rightImageButton.setOnClickListener(this);
		titleText.setClickable(true);
		titleText.setOnClickListener(this);
	}
	
	public OnTitleBarClickListener getOnTitleBarClickListener(){
		return onTitleBarClickListener;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.title_text:
			onTitleBarClickListener.onTitleContentClick(OnTitleBarClickListener.TITLE_TEXT_VIEW);
			break;
		case R.id.left_button:
			onTitleBarClickListener.onTitleContentClick(OnTitleBarClickListener.LEFT_BUTTON);
			break;
		case R.id.right_button:
			onTitleBarClickListener.onTitleContentClick(OnTitleBarClickListener.RIGHT_BUTTON);
			break;
		case R.id.left_image_button:
			onTitleBarClickListener.onTitleContentClick(OnTitleBarClickListener.LEFT_IMAGE_BUTTON);
			break;
		case R.id.right_image_button:
			onTitleBarClickListener.onTitleContentClick(OnTitleBarClickListener.RIGHT_IMAGE_BUTTON);
			break;
		}
	}
}
