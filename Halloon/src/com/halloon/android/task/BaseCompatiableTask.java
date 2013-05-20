package com.halloon.android.task;

import android.os.AsyncTask;
import android.os.Build;

public abstract class BaseCompatiableTask<Param, Progress, Result> extends AsyncTask<Param, Progress, Result> {
	
	public void taskExecute(Param... params){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		}else{
			super.execute(params);
		}
	}
}
