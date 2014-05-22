package com.ilves.electricityproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

public class ElectriCityLocationClient implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener {

	private MainActivity		mContext;
	private LocationClient		mLocationClient;
	private LocationManager		mLocationManager;
	LocationResult				locationResult;
	boolean						gps_enabled					= false;
	boolean						network_enabled				= false;
	private boolean				mUpdatesRequested;
	// Milliseconds per second
	private static final int	MILLISECONDS_PER_SECOND		= 1000;
	// Update frequency in seconds
	public static final int		UPDATE_INTERVAL_IN_SECONDS	= 15;
	// Update frequency in milliseconds
	private static final long	UPDATE_INTERVAL				= MILLISECONDS_PER_SECOND
																	* UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int	FASTEST_INTERVAL_IN_SECONDS	= 1;
	// A fast frequency ceiling in milliseconds
	private static final long	FASTEST_INTERVAL			= MILLISECONDS_PER_SECOND
																	* FASTEST_INTERVAL_IN_SECONDS;
	// Define an object that holds accuracy and frequency parameters
	private LocationRequest		mLocationRequest;
	private SharedPreferences	mPrefs;
	private Editor				mEditor;

	public ElectriCityLocationClient(MainActivity context) {
		super();
		mContext = context;
		// TODO Auto-generated constructor stub
		// Open the shared preferences
		mPrefs = mContext.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		// Get a SharedPreferences editor
		mEditor = mPrefs.edit();
		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */
		mLocationClient = new LocationClient(context, this, this);
		// Start with updates turned off
		mUpdatesRequested = false;
		// Create the LocationRequest object
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
	}

	protected void makeUseOfNewLocation(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {
			// Report to the UI that the location was updated
			String msg = "LocationManager Location: "
					+ Double.toString(location.getLatitude()) + ","
					+ Double.toString(location.getLongitude());
			Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
			mContext.updateLocationInWikitude(location);
		}

	}

	public void onPause() {
		// Save the current setting for updates
		mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
		mEditor.commit();
	}

	public void onStart() {
		mLocationClient.connect();
	}

	public void onStop() {
		// If the client is connected
		if (mLocationClient.isConnected()) {
			/*
			 * Remove location updates for a listener. The current Activity is
			 * the listener, so the argument is "this".
			 */
			mLocationClient.removeLocationUpdates(this);
		}
		/*
		 * After disconnect() is called, the client is considered "dead".
		 */
		mLocationClient.disconnect();
	}

	public void onResume() {
		/*
		 * Get any previous setting for location updates Gets "false" if an
		 * error occurs
		 */
		if (mPrefs.contains("KEY_UPDATES_ON")) {
			mUpdatesRequested = mPrefs.getBoolean("KEY_UPDATES_ON", false);

			// Otherwise, turn off location updates
		} else {
			mEditor.putBoolean("KEY_UPDATES_ON", false);
			mEditor.commit();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		Log.i("Connection", "onConnected");
		Log.i("Connection", "" + connectionHint);
		if (connectionHint != null) {
			Log.i("Connection", connectionHint.toString());
		}
		Location location = mLocationClient.getLastLocation();

		if (location != null) {
			String msg = "Got Location: " + Double.toString(location.getLatitude()) + ","
					+ Double.toString(location.getLongitude());
			//Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
			Log.i("Location", msg);
			mContext.updateLocationInWikitude(location);
		}
		// If already requested, start periodic updates
		if (mUpdatesRequested) {
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {
			// Report to the UI that the location was updated
			String msg = "Updated Location: " + Double.toString(location.getLatitude())
					+ "," + Double.toString(location.getLongitude());
			//Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
			mContext.updateLocationInWikitude(location);
		}

	}

	public void setUpdates(boolean updatesRequested) {
		mUpdatesRequested = updatesRequested;
	}

	public static abstract class LocationResult {
		public abstract void gotLocation(Location location);
	}
}
