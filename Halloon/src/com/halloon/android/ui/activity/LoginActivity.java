package com.halloon.android.ui.activity;

import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.halloon.android.ui.fragment.LoginFragment;

public class LoginActivity extends BaseSinglePaneActivity {

	private CommonsHttpOAuthConsumer httpOauthConsumer;
	private OAuthProvider httpOauthprovider;

	public static final String TAG = "LoginActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		homeGoToDashboard = false;

		Log.i(TAG, "LoginActivity onCreate!" + LoginActivity.this);

	}

	@Override
	protected Fragment onCreatePane() {
		Log.i(TAG, "LoginActivity Fragment!");

		return new LoginFragment();
	}

	@Override
	protected void init() {

		Log.i(TAG, "LoginActivity init!");

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		Log.i(TAG, "LoginActivity onPostCreate!");

	}

}
