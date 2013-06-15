package com.halloon.android.task;

import android.os.AsyncTask;
import android.os.Build;

public abstract class BaseCompatiableTask<Param, Progress, Result> extends AsyncTask<Param, Progress, Result> {
	
	public AsyncTask<Param, Progress, Result> taskExecute(Param... params){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			return super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		}else{
			return super.execute(params);
		}
	}

}
