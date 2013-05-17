package com.halloon.android.task;

/*******************************************************************************
 * Copyright (c) 2010 marvin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     marvin - initial API and implementation
 *     modified by 7heaven
 ******************************************************************************/

import java.util.Calendar;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.halloon.android.R;
import com.halloon.android.listener.OnLocationSeekListener;
import com.halloon.android.util.Constants;

public class LocationTask extends AsyncTask<Void, String, Location> {

	private static final long MAX_WAIT_TIME = 30 * 1000;
	private static final long SLEEP_TIME = 5000;

	// private static final int NUMBER_ADDRESSES = 5;

	private LocationManager mLocationManager;
	private LocationResult mLocationResult;

	private Location mGpsLocation;
	private Location mNetworkLocation;

	private MyLocationListener mGpsLocationListener;
	private MyLocationListener mNetworkLocationListener;
	
	private OnLocationSeekListener mLocationSeekListener;

	private final Context mContext;

	public static abstract class LocationResult {
		public abstract void gotLocation(Location location, String provider);
	}
	
	public LocationTask(final Context context) {
		mContext = context;
	}

	@Override
	protected void onPreExecute() {
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		mLocationResult = new LocationResult() {
			@Override
			public void gotLocation(Location l, String provider) {
				if (provider == LocationManager.GPS_PROVIDER)
					mGpsLocation = l;
				if (provider == LocationManager.NETWORK_PROVIDER)
					mNetworkLocation = l;
			}
		};

		if (isGpsEnabled()) {
			Log.d(Constants.LOG_TAG, "GPS enabled");
			mGpsLocationListener = new MyLocationListener(
					LocationManager.GPS_PROVIDER);
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, mGpsLocationListener);
		}

		if (isNetworkEnabled()) {
			Log.d(Constants.LOG_TAG, "network location enabled");
			mNetworkLocationListener = new MyLocationListener(
					LocationManager.NETWORK_PROVIDER);
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0,
					mNetworkLocationListener);
		}
	}

	@Override
	protected Location doInBackground(Void... params) {
		Location location = getLocation();
		if (isCancelled()) {
			return null;
		} else {
			return location;
		}
	}

	@Override
	protected void onProgressUpdate(String... text) {
		Toast.makeText(mContext, text[0], Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onPostExecute(final Location l) {
		if (!isCancelled()) {
			if (l != null) {
				if (mGpsLocationListener != null)
					mLocationManager.removeUpdates(mGpsLocationListener);
				if (mNetworkLocationListener != null)
					mLocationManager.removeUpdates(mNetworkLocationListener);
				Log.d(Constants.LOG_TAG, "[LocationTask] stopped all location updates");

				// mGpsLocationListener = null;
				// mNetworkLocationListener = null;
				// mLocationManager = null;
				// TODO Do we need to delete these?

				setLocationValues(l);

			}
		}
	}

	private boolean isGpsEnabled() {
		return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	private boolean isNetworkEnabled() {
		return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	public Location getLocation() {
		Long t = Calendar.getInstance().getTimeInMillis();

		while (Calendar.getInstance().getTimeInMillis() - t < MAX_WAIT_TIME
				&& mGpsLocation == null
				&& (mNetworkLocation == null || isGpsEnabled())
				&& (isNetworkEnabled() || isGpsEnabled()))
			SystemClock.sleep(SLEEP_TIME);

		if (mGpsLocation != null) {
			publishProgress(mContext.getString(R.string.location_accurate));
		    Log.d(Constants.LOG_TAG, "get a GPS location");
			return mGpsLocation;
		}

		if (mNetworkLocation != null) {
			publishProgress(mContext.getString(R.string.location_no_accurate));
			Log.d(Constants.LOG_TAG, "got a network location");
			return mNetworkLocation;
		}

		publishProgress(mContext.getString(R.string.location_no_up_to_date));
		Log.d(Constants.LOG_TAG, "no up-to-date location");

		mGpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		mNetworkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		if (mGpsLocation != null && mNetworkLocation == null)
			return mGpsLocation;
		if (mGpsLocation == null && mNetworkLocation != null)
			return mNetworkLocation;
		if (mGpsLocation != null && mNetworkLocation != null
				&& mGpsLocation.getTime() > mNetworkLocation.getTime())
			return mGpsLocation;
		else
			return mNetworkLocation;
	}
	
	public void setOnLocationSeekListener(OnLocationSeekListener mLocationSeekListener){
		this.mLocationSeekListener = mLocationSeekListener;
	}
	
	public OnLocationSeekListener getOnLocationSeekListener(){
		return mLocationSeekListener;
	}

	private void setLocationValues(Location l) {
		if (l == null) {
			// Log.d(Constants.LOG_TAG, "location: null");
			Toast.makeText(mContext, mContext.getString(R.string.no_location), Toast.LENGTH_LONG).show();
			return;
		}

		Log.d(Constants.LOG_TAG, "longitude:" + l.getLongitude() + ", latitude:" + l.getLatitude());
		
		mLocationSeekListener.onLocationGot(l.getLongitude(), l.getLatitude());

		/*
		 * //用google map api 得到街道位置 //Log.d(Constants.LOG_TAG, "location: " +
		 * l.getLatitude() ", " + l.getLongitude()); try{ final Geocoder gc =
		 * new Geocoder(mContext); List<Address> addresses =
		 * gc.getFromLocation(l.getLatitude(), l.getLongitude(),
		 * NUMBER_ADDRESSES); //Log.d(Constants.LOG_TAG, "Geocoder got " +
		 * addresses.size() + " address(es)");
		 * 
		 * if(addresses.size() == 0) Toast.makeText(mContext,
		 * mContext.getString(R.string.no_address) + ": " + l.getLatitude() +
		 * ", " + l.getLongitude() + ".", Toast.LENGTH_LONG).show();
		 * 
		 * for(Address a : addresses){ if(a == null) continue;
		 * 
		 * ((EbtNewNote) mContext).setLocationValues( new
		 * LocationValues(a.getCountryName(), a.getLocality(),
		 * a.getPostalCode(), true));
		 * 
		 * } }catch(IOException e){ Log.e(Constants.LOG_TAG,
		 * "Geocoder IOException: " + e.getMessage()); Toast.makeText(mContext,
		 * mContext.getString(R.string.geocoder_exception) + ": " +
		 * e.getMessage() + ".", Toast.LENGTH_LONG).show(); }
		 */
	}

	private final class MyLocationListener implements LocationListener {
		private String mProvider;

		public MyLocationListener(String provider) {
			mProvider = provider;
		}

		public void onLocationChanged(Location l) {
			// Log.d(Constants.LOG_TAG, "location changed");
			mLocationResult.gotLocation(l, mProvider);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}

	}
}
