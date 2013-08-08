package com.halloon.android.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.halloon.android.R;
import com.halloon.android.listener.OnTitleBarClickListener;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;
import com.halloon.android.widget.HalloonTitleBar;

public class SettingsAboutFragment extends BaseTitleBarFragment implements OnTitleBarClickListener{

	private Context context;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.context = activity;
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	@Override
	protected void init(HalloonTitleBar titleBar, RelativeLayout content) {
		titleBar.setTitleStyle(HalloonTitleBar.TITLE_STYLE_BACK_BUTTON_ONLY);
		titleBar.setOnTitleBarClickListener(this);
		titleBar.getTitleTextView().setText(R.string.about_halloon);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content.addView(inflater.inflate(R.layout.about_fragment, null, false));
	}

	@Override
	public void onTitleContentClick(int contentEnum) {
		switch(contentEnum){
		case OnTitleBarClickListener.LEFT_BUTTON:
			((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			((BaseMultiFragmentActivity) context).backStackAction();
			break;
		}
	}
	
	

}
