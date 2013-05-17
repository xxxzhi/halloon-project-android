package com.halloon.android.listener;

public interface OnMultiClickListener {
  public void onSingleClick();
  public void onDoubleClick(float x, float y);
  
  /*
  private static class MyHandler extends Handler{
	  WeakReference<OnMultiClickListener> listener;
	  WeakReference<View> v;
	  WeakReference<MotionEvent> event;
	  
	  public MyHandler(OnMultiClickListener listener, View v, MotionEvent event){
		  this.listener = new WeakReference<OnMultiClickListener>(listener);
		  this.v = new WeakReference<View>(v);
		  this.event = new WeakReference<MotionEvent>(event);
	  }
	  
	  @Override
	  public void handleMessage(Message msg){
		  OnMultiClickListener mListener = listener.get();
		  View mV = this.v.get();
		  MotionEvent mEvent = this.event.get();
		  switch(msg.what){
		  case 0:
			  if(mListener.count == 2) mListener.onDoubleClick(mV, mEvent.getX(), mEvent.getY());
			  break;
		  case 1:
			  if(mEvent.getAction() != MotionEvent.ACTION_MOVE && mListener.count == 1) mListener.onSingleClick(mV, mEvent.getX(), mEvent.getY());
			  break;
		  }
	  }
  }*/
}
