package com.ilves.electricityproject;

import java.io.IOException;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;
import com.google.android.gms.location.LocationClient;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.ArchitectConfig;
import com.wikitude.architect.ArchitectView.ArchitectUrlListener;
import com.wikitude.architect.ArchitectView.SensorAccuracyChangeListener;

public class MainActivity extends FragmentActivity implements
		ConnectionCallbacks,
		OnConnectionFailedListener {

	private static final String				TAG						= "MainActivity";

	private ViewPager						mViewPager;
	/**
	 * holds the Wikitude SDK AR-View, this is where camera, markers, compass,
	 * 3D models etc. are rendered
	 */
	protected ArchitectView					mArchitectView;

	/**
	 * sensor accuracy listener in case you want to display calibration hints
	 */
	protected SensorAccuracyChangeListener	mSensorAccuracyListener;

	/**
	 * last known location of the user, used internally for content-loading
	 * after user location was fetched
	 */
	protected Location						lastKnownLocaton;
	protected ElectriCityLocationClient		mLocationClient;

	/**
	 * urlListener handling "document.location= 'architectsdk://...' " calls in
	 * JavaScript"
	 */
	protected ArchitectUrlListener			urlListener;
	boolean									mUpdatesRequested;

	/**
	 * Games client
	 */
	private GoogleApiClient					mGoogleApiClient;
	private ElectriCityGoogleApiClient		mmGoogleApiClient;
	// Request code to use when launching the resolution activity
	private static final int				REQUEST_RESOLVE_ERROR	= 1001;
	// Unique tag for the error dialog fragment
	private static final String				DIALOG_ERROR			= "dialog_error";
	// Bool to track whether the app is already resolving an error
	private boolean							mResolvingError			= false;
	private static final String				STATE_RESOLVING_ERROR	= "resolving_error";
	private static final int				REQUEST_ACHIEVEMENTS	= 40001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mResolvingError = savedInstanceState != null
				&& savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
		/*
		 * this enables remote debugging of a WebView on Android 4.4+ when
		 * debugging = true in AndroidManifest.xml If you get a compile time
		 * error here, ensure to have SDK 19+ used in your ADT/Eclipse. You may
		 * even delete this block in case you don't need remote debugging or
		 * don't have an Android 4.4+ device in place. Details:
		 * https://developers
		 * .google.com/chrome-developer-tools/docs/remote-debugging
		 */
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
				WebView.setWebContentsDebuggingEnabled(true);
			}
		}
		// Location stuff
		mLocationClient = new ElectriCityLocationClient(this);
		// Google play services
		mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Games.API)
				.addScope(Games.SCOPE_GAMES)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

		// Get log in button

		// get viewpager
		switch (getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			Log.i("AR", "ORIENTATION_PORTRAIT");
			// Shit happens

			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(new MainFragmentAdapter(getSupportFragmentManager()));
			mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					// When swiping between pages, select the
					// corresponding tab.
					getActionBar().setSelectedNavigationItem(position);
					((MainFragmentAdapter) mViewPager.getAdapter()).setActive(position);
					if (position == 2) {
						if (!mResolvingError) { // more about this later
							mGoogleApiClient.connect();
						}
					}
				}
			});
			final ActionBar actionBar = getActionBar();
			// Specify that tabs should be displayed in the action bar.
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			// Create a tab listener that is called when the user changes tabs.
			ActionBar.TabListener tabListener = new ActionBar.TabListener() {
				public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
					// show the given tab
					// When the tab is selected, switch to the
					// corresponding page in the ViewPager.
					mViewPager.setCurrentItem(tab.getPosition());
					if (tab.getPosition() == 2) {
						if (!mResolvingError) { // more about this later
							mGoogleApiClient.connect();
						}
					}
				}

				public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
					// hide the given tab
				}

				public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
					// probably ignore this event
				}
			};

			actionBar.addTab(actionBar.newTab()
					.setText("Ticket")
					.setTabListener(tabListener));
			actionBar.addTab(actionBar.newTab()
					.setText("Buses")
					.setTabListener(tabListener));
			actionBar.addTab(actionBar.newTab()
					.setText("Profile")
					.setTabListener(tabListener));
			// request updates of location
			mLocationClient.setUpdates(false);
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			Log.i("AR", "ORIENTATION_LANDSCAPE");
			this.mArchitectView = (ArchitectView) this.findViewById(R.id.architectView);
			final ArchitectConfig config = new ArchitectConfig("" /* license key */);
			this.mArchitectView.onCreate(config);
			// listener passed over to locationProvider, any location update is
			// handled here
			// request updates of location
			mLocationClient.setUpdates(true);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// TODO Auto-generated method stub
		switch (getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			this.mArchitectView.onPostCreate();
			try {
				this.mArchitectView.load("index.html");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	/**
	 * ========================================================================
	 * ================ LIFE CYCLE EVENTS
	 * ========================================================================
	 */
	@Override
	protected void onResume() {
		super.onResume();
		// call mandatory live-cycle method of architectView
		if (this.mArchitectView != null) {
			this.mArchitectView.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// call mandatory live-cycle method of architectView
		if (this.mArchitectView != null) {
			this.mArchitectView.onPause();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.onStart();
	}

	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.onStop();
		// Disconnect google api client
		mGoogleApiClient.disconnect();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// call mandatory live-cycle method of architectView
		if (this.mArchitectView != null) {
			this.mArchitectView.onDestroy();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * ========================================================================
	 * ================= ACTIVITY RESULTS
	 * ========================================================================
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_RESOLVE_ERROR) {
			mResolvingError = false;
			if (resultCode == RESULT_OK) {
				// Make sure the app is not already connected or attempting to
				// connect
				if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
					mGoogleApiClient.connect();
				}
			}
		}
	}

	/**
	 * ========================================================================
	 * ================= BUTTONS
	 * ========================================================================
	 */
	public void onSMS(View v) {
		TestDialog d = new TestDialog();
		d.show(getSupportFragmentManager(), "TestDialogFragment");
	}

	public void onCard(View v) {
		TestListDialog d = new TestListDialog();
		d.show(getSupportFragmentManager(), "TestDialogFragment");
	}

	public void onCoin(View v) {
		TestDialog d = new TestDialog();
		d.show(getSupportFragmentManager(), "TestDialogFragment");
	}

	public void onSignIn() {
		// start the asynchronous sign in flow
		// beginUserInitiatedSignIn();
		Log.i("CLICK", "CLICK");
		if (mGoogleApiClient.isConnected()) {
			startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
					REQUEST_ACHIEVEMENTS);
		}
	}

	/**
	 * ========================================================================
	 * ================= CONNECTION
	 * ========================================================================
	 */

	@Override
	public void onConnected(Bundle connectionHint) {
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		Log.i("Connection", "onConnectionSuspended");

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i("Connection", "onConnectionFailed");
		// TODO Auto-generated method stub
		if (mResolvingError) {
			// Already attempting to resolve an error.
			return;
		} else if (result.hasResolution()) {
			try {
				mResolvingError = true;
				result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
			} catch (SendIntentException e) {
				// There was an error with the resolution intent. Try again.
				mGoogleApiClient.connect();
			}
		} else {
			// Show dialog using GooglePlayServicesUtil.getErrorDialog()
			showErrorDialog(result.getErrorCode());
			mResolvingError = true;
		}
	}

	/**
	 * ========================================================================
	 * ================= HELPERS
	 * ========================================================================
	 */

	public void updateLocationInWikitude(Location location) {
		// sore last location as member, in case it is needed
		// somewhere (in e.g. your adjusted project)
		this.lastKnownLocaton = location;
		if (this.mArchitectView != null) {
			// check if location has altitude at certain
			// accuracy level & call right architect method (the
			// one with altitude information)
			if (location.hasAltitude() && location.hasAccuracy()
					&& location.getAccuracy() < 7) {
				this.mArchitectView.setLocation(location.getLatitude(),
						location.getLongitude(),
						location.getAltitude(),
						location.getAccuracy());
			} else {
				this.mArchitectView.setLocation(location.getLatitude(),
						location.getLongitude(),
						location.hasAccuracy() ? location.getAccuracy() : 1000);
			}
		}
	}

	// The rest of this code is all about building the error dialog
	/* Creates a dialog for an error message */
	private void showErrorDialog(int errorCode) {
		// Create a fragment for the error dialog
		ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
		// Pass the error that should be displayed
		Bundle args = new Bundle();
		args.putInt(DIALOG_ERROR, errorCode);
		dialogFragment.setArguments(args);
		dialogFragment.show(getSupportFragmentManager(), "errordialog");
	}

	/* Called from ErrorDialogFragment when the dialog is dismissed. */
	public void onDialogDismissed() {
		mResolvingError = false;
	}

	/* A fragment to display an error dialog */
	public static class ErrorDialogFragment extends DialogFragment {
		public ErrorDialogFragment() {
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Get the error code and retrieve the appropriate dialog
			int errorCode = this.getArguments().getInt(DIALOG_ERROR);
			return GooglePlayServicesUtil.getErrorDialog(errorCode,
					this.getActivity(),
					REQUEST_RESOLVE_ERROR);
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			((MainActivity) getActivity()).onDialogDismissed();
		}
	}

}
