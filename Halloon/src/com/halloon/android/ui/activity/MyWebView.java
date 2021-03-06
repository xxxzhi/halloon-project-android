package com.halloon.android.ui.activity;

import java.lang.reflect.InvocationTargetException;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.halloon.android.R;
import com.halloon.android.ui.BaseActivity;
import com.halloon.android.view.ScrollTextView;
import com.halloon.android.widget.HalloonProgressBar;

@SuppressLint("SetJavaScriptEnabled") 
public class MyWebView extends BaseActivity {

	private ScrollTextView titleText;
	private WebView mWebView;
	private HalloonProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mywebview);
		
		titleText = (ScrollTextView) findViewById(R.id.title_text);
		
		findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mWebView != null){
					mWebView.removeAllViews();
					try {
						Class.forName("android.webkit.WebView").getMethod("onPause", (Class[]) null).invoke(mWebView, (Object[]) null);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				finish();
			}
		});
		
		progressBar = (HalloonProgressBar) findViewById(R.id.progress);
		
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.setWebViewClient(new WebViewClient(){
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView webView, String url){
				webView.loadUrl(url);
				
				return true;
			}
			
			@Override
			public void onPageFinished(WebView webView, String url){
				progressBar.setVisibility(View.GONE);
			}
		});
		
		mWebView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView webView, int progress){
				if(progressBar.getVisibility() == View.GONE) progressBar.setVisibility(View.VISIBLE);
				progressBar.setProgress(progress);
			}
			
			@Override
			public void onReceivedTitle(WebView webView, String title){
				titleText.setText(title);
			}
		});
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) mWebView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(getIntent().getStringExtra("url"));
	}
	
	
}
