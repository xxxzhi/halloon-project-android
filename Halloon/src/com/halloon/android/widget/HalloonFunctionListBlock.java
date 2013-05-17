package com.halloon.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.halloon.android.R;

public class HalloonFunctionListBlock extends RelativeLayout {
	
	private static final String TAG = HalloonFunctionListBlock.class.getSimpleName();
	
	private TextView descriptionTitle;
	private ImageView arrow;
	private CheckBox checkBox;

	public HalloonFunctionListBlock(Context context){
		super(context);
		init(context);
	}
	
	public HalloonFunctionListBlock(Context context, AttributeSet attrs){
		super(context, attrs);
		init(context);
	}
	
	public HalloonFunctionListBlock(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context){
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View root = layoutInflater.inflate(R.layout.function_list_block, null);
		addView(root);
		
		descriptionTitle = (TextView) root.findViewById(R.id.text);
		arrow = (ImageView) root.findViewById(R.id.arrow);
		checkBox = (CheckBox) root.findViewById(R.id.check);
	}
	
	public TextView getDescriptionTitle(){
		return descriptionTitle;
	}
	
	public ImageView getArrow(){
		return arrow;
	}
	
	public CheckBox getCheckBox(){
		return checkBox;
	}
}
