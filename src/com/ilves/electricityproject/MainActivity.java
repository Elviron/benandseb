package com.ilves.electricityproject;

import java.io.IOException;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.common.images.ImageManager.OnImageLoadedListener;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.request.GameRequest;
import com.ilves.electricityproject.GameHelper.GameHelperListener;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.ArchitectConfig;
import com.wikitude.architect.ArchitectView.ArchitectUrlListener;
import com.wikitude.architect.ArchitectView.SensorAccuracyChangeListener;

public class MainActivity extends FragmentActivity implements
		GameHelperListener,
		OnImageLoadedListener {

	private static final String				TAG						= "MainActivity";

	private boolean							isPortrait;
	private ViewPager						mViewPager;
	private MainFragmentAdapter				mAdapter;
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
	GameHelper								mHelper;
	// Request code to use when launching the resolution activity
	private static final int				REQUEST_RESOLVE_ERROR	= 1001;
	// Unique tag for the error dialog fragment
	private static final String				DIALOG_ERROR			= "dialog_error";
	// Bool to track whether the app is already resolving an error
	private boolean							mResolvingError			= false;

	private View							connectedButton;

	private View							disconnectedButton;

	private static final String				STATE_RESOLVING_ERROR	= "resolving_error";
	private static final int				REQUEST_ACHIEVEMENTS	= 40001;
	private static final int				REQUEST_LEADERBOARD		= 40002;

	private String							LEADERBOARD_ID;

	private int								SEND_GIFT_CODE			= 41001;
	
	private String android_id; 

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
		mHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		mHelper.setup(this);

		// Get log in button

		// get viewpager
		switch (getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			isPortrait = true;
			Log.i("AR", "ORIENTATION_PORTRAIT");
			// Shit happens

			mViewPager = (ViewPager) findViewById(R.id.pager);
			mAdapter = new MainFragmentAdapter(getSupportFragmentManager(), this);
			mViewPager.setAdapter(mAdapter);
			mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					// When swiping between pages, select the
					// corresponding tab.
					getActionBar().setSelectedNavigationItem(position);
					((MainFragmentAdapter) mViewPager.getAdapter()).setActive(position);
					if (position == 2) {
						if (!mResolvingError) { // more about this later
							// mHelper.connect();
						}
					}
				}
			});
			final ActionBar actionBar = getActionBar();
			// Specify that tabs should be displayed in the action bar.
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			// Create a tab listener that is called when the user changes tabs.
			ActionBar.TabListener tabListener = new ActionBar.TabListener() {
				@Override
				public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
					// show the given tab
					// When the tab is selected, switch to the
					// corresponding page in the ViewPager.
					mViewPager.setCurrentItem(tab.getPosition());
					if (tab.getPosition() == 2) {
						if (!mResolvingError) { // more about this later
							// mHelper.connect();
						}
					}
				}

				@Override
				public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
					// hide the given tab
				}

				@Override
				public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
					// probably ignore this event
				}
			};

			actionBar.addTab(actionBar.newTab()
					.setText(getString(R.string.ticket))
					.setTabListener(tabListener));
			actionBar.addTab(actionBar.newTab()
					.setText(getString(R.string.buses))
					.setTabListener(tabListener));
			actionBar.addTab(actionBar.newTab()
					.setText(getString(R.string.profile))
					.setTabListener(tabListener));
			// request updates of location
			mLocationClient.setUpdates(false);
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			isPortrait = false;
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
		/*
		 * if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ==
		 * ConnectionResult.SUCCESS) { Toast.makeText(this, "Google play!",
		 * Toast.LENGTH_SHORT).show(); }
		 */
		android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		debugLog("id: "+android_id);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// TODO Auto-generated method stub
		if (isPortrait) {

		} else {
			this.mArchitectView.onPostCreate();
			try {
				// http://electricity-project.herokuapp.com/
				// https://googledrive.com/host/0B3u2WUMfD8yLVldaaDBTRGRuVkE/index.html
				this.mArchitectView.load("https://googledrive.com/host/0B3u2WUMfD8yLVldaaDBTRGRuVkE/index.html");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		if (!mResolvingError) { // more about this later
			mHelper.connect();
		}
		// mHelper.onStart(MainActivity.this);
	}

	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.onStop();
		// Disconnect google api client
		mHelper.onStop();
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

	/**
	 * ========================================================================
	 * ================= MENU STUFF
	 * ========================================================================
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if (mHelper.isSignedIn()) {
			menu.findItem(R.id.action_connected).setVisible(true);
			menu.findItem(R.id.action_disconnected).setVisible(false);
		} else {
			menu.findItem(R.id.action_connected).setVisible(false);
			menu.findItem(R.id.action_disconnected).setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_connected:
			// Show dialog to prompt user if want to log in
			SignOutDialog d = new SignOutDialog();
			d.show(getSupportFragmentManager(), "TestDialogFragment");
			return true;
		case R.id.action_disconnected:
			// Connect to Google games

			mHelper.connect();
			mHelper.onStart(MainActivity.this);
			return true;
		case R.id.action_login:
			// Start login activity
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
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

	public void onAch(View v) {
		if (mHelper.isSignedIn()) {
			startActivityForResult(Games.Achievements.getAchievementsIntent(mHelper.getApiClient()),
					REQUEST_ACHIEVEMENTS);
		}
	}

	public void onLead(View v) {
		if (mHelper.isSignedIn()) {
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mHelper.getApiClient(),
					getString(R.string.leaderboard_who_has_the_most_coins)),
					REQUEST_LEADERBOARD);
		}
	}

	public void onGift(View v) {
		Intent intent = Games.Requests.getSendIntent(mHelper.getApiClient(),
				GameRequest.TYPE_GIFT,
				"".getBytes(),
				2,
				BitmapFactory.decodeResource(getResources(), R.drawable.ic_games_gifts),
				"Send stuff");
		startActivityForResult(intent, SEND_GIFT_CODE);
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

	/**
	 * GAME HELPER CALLBACKS
	 */

	@Override
	public void onSignInFailed() {
		Log.i(TAG, "onSignInFailed");
		// TODO Auto-generated method stub
		// Switch icons in menu
		invalidateOptionsMenu();
		mAdapter.setName("Please sign in");
	}

	@Override
	public void onSignInSucceeded() {
		Log.i(TAG, "onSignInSucceeded");
		// Switch icons in menu
		invalidateOptionsMenu();
		// Notify profile fragment that we are signed in, show log out button
		// mAdapter.getPf().signInSuccessful();
		Toast.makeText(this,
				Games.Players.getCurrentPlayer(mHelper.getApiClient()).getDisplayName(),
				Toast.LENGTH_SHORT).show();
		Player p = getPlayer();
		ImageManager iManager = ImageManager.create(this);
		iManager.loadImage(this, p.getIconImageUri());
		// Log all info from player
		debugLog("Name:            " + p.getDisplayName());
		debugLog("ID:              " + p.getPlayerId());
		debugLog("HiResImageUrl:   " + p.getHiResImageUrl());
		debugLog("IconImageUrl:    " + p.getIconImageUrl());
		debugLog("LastPlWith:      " + p.getLastPlayedWithTimestamp());
		debugLog("RetrievedTimestamp:      " + p.getRetrievedTimestamp());

		if (isPortrait) {
			// Set this as callback for the achievements
			mAdapter.setName(p.getDisplayName());
		}
		//AppStateManager.load(mHelper.getApiClient(), 1);
	}

	public void signOut() {
		// TODO Auto-generated method stub
		mHelper.disconnect();
	}

	public void signIn() {
		// TODO Auto-generated method stub
		mHelper.connect();
	}

	public class SignOutDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Do you really want to sign out?")
					.setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							// Call sign out
							Log.i(TAG, "PositiveButton");
							mHelper.signOut();
							mAdapter.setName("Please sign in");
							mAdapter.setIcon(null);
							invalidateOptionsMenu();
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
						}
					});
			// Create the AlertDialog object and return it
			return builder.create();
		}

	}

	@Override
	public void onImageLoaded(Uri uri, Drawable drawable, boolean isRequestedDrawable) {
		// Send profile image to profile fragment
		debugLog("onImageLoaded");
		if (isPortrait) {
			mAdapter.setIcon(drawable);
		}
	}

	/**
	 * 
	 * @param message
	 */

	private void debugLog(String message) {
		Log.i(TAG, message);
	}

	public Player getPlayer() {
		return Games.Players.getCurrentPlayer(mHelper.getApiClient());
	}
}
