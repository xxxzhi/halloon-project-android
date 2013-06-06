package com.halloon.android.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.halloon.android.R;
import com.halloon.android.ui.activity.OAuthActivity;

public class LoginFragment extends Fragment {

	protected static final String TAG = "LoginActivity";
	private Button LoginButton;
	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_login, null, false);

		LoginButton = (Button) root.findViewById(R.id.button1);
		context = container.getContext();
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		LoginButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(context, OAuthActivity.class);
				startActivity(intent);

			}
		});

	}
}
