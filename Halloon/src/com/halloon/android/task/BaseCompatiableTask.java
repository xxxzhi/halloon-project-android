package com.halloon.android.task;

import android.os.AsyncTask;
import android.os.Build;

public abstract class BaseCompatiableTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	
	public AsyncTask<Params, Progress, Result> taskExecute(Params... params){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			return super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		}else{
			return super.execute(params);
		}
	}

}
