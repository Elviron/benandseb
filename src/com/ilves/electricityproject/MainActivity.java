package com.ilves.electricityproject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.common.images.ImageManager.OnImageLoadedListener;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.request.GameRequest;
import com.ilves.electricityproject.GameHelper.GameHelperListener;
import com.ilves.electricityproject.dialogs.CoinDialog;
import com.ilves.electricityproject.dialogs.PeriodDialog;
import com.ilves.electricityproject.dialogs.SettingsDialog;
import com.ilves.electricityproject.dialogs.TicketDialog;
import com.ilves.electricityproject.utils.ImageHelper;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.ArchitectConfig;
import com.wikitude.architect.ArchitectView.ArchitectUrlListener;
import com.wikitude.architect.ArchitectView.SensorAccuracyChangeListener;

public class MainActivity extends FragmentActivity implements
		GameHelperListener,
		OnImageLoadedListener,
		ArchitectUrlListener {

	public static final String				TAG						= "MainActivity";
	public static final String				profileImageFilename	= "profile_image.png";
	public static final String				prefs_amount			= "electricoins_amount";
	public static final String				prefs_sound				= "sound_on";
	public static final String				prefs_logged_in			= "logged_in";
	public static final String				prefs_name				= "profile_name";
	public static final String				prefs_end_of_card		= "end_of_card";

	private static final int				REQUEST_ACHIEVEMENTS	= 40001;
	private static final int				REQUEST_LEADERBOARD		= 40002;
	private static final int				REQUEST_LOGIN			= 50001;

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
	private GameHelper						mHelper;
	// Request code to use when launching the resolution activity
	private static final int				REQUEST_RESOLVE_ERROR	= 1001;
	// Unique tag for the error dialog fragment
	private static final String				DIALOG_ERROR			= "dialog_error";
	// Bool to track whether the app is already resolving an error
	private boolean							mResolvingError			= false;

	private static final String				STATE_RESOLVING_ERROR	= "resolving_error";

	private int								SEND_GIFT_CODE			= 41001;

	private MediaPlayer						mediaPlayer;

	private SharedPreferences				mSharedPrefs;
	private Object							mBatteryDate;

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
		// get prefs
		mSharedPrefs = getPreferences(Context.MODE_PRIVATE);
		// Location stuff
		mLocationClient = new ElectriCityLocationClient(this);
		// Google play services
		mHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		mHelper.setup(this);

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
			// Url listener for architectview
			this.mArchitectView.registerUrlListener(this);
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
		// Sound
		mediaPlayer = MediaPlayer.create(this, R.raw.coin_sound);
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
		// is user logged in?
		// mPrefs = getSharedPreferences("SharedPreferences",
		// Context.MODE_PRIVATE);

		boolean loggedin = mSharedPrefs.getBoolean(prefs_logged_in, false);
		if (!loggedin) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, REQUEST_LOGIN);
		}
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
			SignOutDialog dialogSignout = new SignOutDialog();
			dialogSignout.show(getSupportFragmentManager(), "TestDialogFragment");
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
		case R.id.action_settings:
			SettingsDialog dialogSettings = new SettingsDialog();
			dialogSettings.show(getSupportFragmentManager(), "SettingsDialog");
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
		TicketDialog d = new TicketDialog();
		d.show(getSupportFragmentManager(), "TestDialogFragment");
	}

	public void onCard(View v) {
		PeriodDialog d = new PeriodDialog();
		d.show(getSupportFragmentManager(), "CreditcardDialog");
	}

	public void onCoin(View v) {
		CoinDialog d = new CoinDialog();
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
					getString(R.string.leaderboard_most_coins)),
					REQUEST_LEADERBOARD);
		}
	}

	public void onGift(View v) {
		Intent intent = Games.Requests.getSendIntent(mHelper.getApiClient(),
				GameRequest.TYPE_GIFT,
				"".getBytes(),
				2,
				BitmapFactory.decodeResource(getResources(), R.drawable.ic_electricoins),
				"Send stuff");
		startActivityForResult(intent, SEND_GIFT_CODE);
	}

	public void onClearCoins(View v) {
		debugLog("Clear coins");
		SharedPreferences.Editor editor = mSharedPrefs.edit();
		editor.putInt(prefs_amount, 0);
		editor.commit();
	}

	public void onAddCoins(View v) {
		debugLog("Clear coins");
		int coins = mSharedPrefs.getInt(prefs_amount, 0) + 10;
		SharedPreferences.Editor editor = mSharedPrefs.edit();
		editor.putInt(prefs_amount, coins);
		editor.commit();
	}

	public void onLogoutVasttrafik(View v) {
		SharedPreferences.Editor editor = mSharedPrefs.edit();
		editor.putBoolean(prefs_logged_in, false);
		editor.commit();
	}

	public void onLogoutGoogle(View v) {
		mHelper.signOut();
	}

	public void onDepleat(View v) {
		Editor editor = mSharedPrefs.edit();
		editor.remove(prefs_end_of_card);
		editor.commit();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_LOGIN) {
			if (resultCode == RESULT_OK) {
				// The user picked a contact.
				// The Intent's data Uri identifies which contact was selected.
				// Do something with the contact here (bigger example below)
				SharedPreferences.Editor editor = mSharedPrefs.edit();
				editor.putBoolean(prefs_logged_in, true);
				editor.commit();
			} else {
				finish();
			}
		}
	}

	/**
	 * GAME HELPER CALLBACKS
	 */

	@Override
	public void onSignInFailed() {
		Log.i(TAG, "onSignInFailed");
		// Switch icons in actionbar / menu
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
		/*
		 * Toast.makeText(this,
		 * Games.Players.getCurrentPlayer(mHelper.getApiClient
		 * ()).getDisplayName(), Toast.LENGTH_SHORT).show();
		 */
		Player p = getPlayer();
		// Check if profile image exists, if not, download
		File file = new File(getFilesDir(), profileImageFilename);
		if (!file.exists()) {
			ImageManager iManager = ImageManager.create(this);
			iManager.loadImage(this, p.getIconImageUri());
		}
		// Log all info from player
		debugLog("Name:            " + p.getDisplayName());
		debugLog("ID:              " + p.getPlayerId());
		debugLog("HiResImageUrl:   " + p.getHiResImageUrl());
		debugLog("IconImageUrl:    " + p.getIconImageUrl());
		debugLog("LastPlWith:      " + p.getLastPlayedWithTimestamp());
		debugLog("RetrievedTimestamp:      " + p.getRetrievedTimestamp());

		if (isPortrait) {
			// Set this as callback for the achievements
			// mAdapter.setName(p.getDisplayName());
			// Save name to prefs
			Editor editor = mSharedPrefs.edit();
			editor.putString(prefs_name, p.getDisplayName());
			editor.commit();
		}
		// AppStateManager.load(mHelper.getApiClient(), 1);
	}

	public void signOut() {
		mHelper.disconnect();
	}

	public void signIn() {
		mHelper.connect();
	}

	@Override
	public void onImageLoaded(Uri uri, Drawable drawable, boolean isRequestedDrawable) {
		// Send profile image to profile fragment
		debugLog("onImageLoaded");
		File file = new File(getFilesDir(), profileImageFilename);
		if (isPortrait) {
			FileOutputStream out = null;
			Bitmap bmp = ImageHelper.drawableToBitmap(drawable);
			debugLog("drawable getWidth: " + drawable.getIntrinsicWidth());
			debugLog("bmp getWidth: " + bmp.getWidth());
			Bitmap bmpRound = ImageHelper.getRoundedCornerBitmap(bmp, bmp.getWidth() / 2);
			try {
				out = new FileOutputStream(file);
				bmpRound.compress(Bitmap.CompressFormat.PNG, 90, out);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (Throwable ignore) {
				}
			}
			mAdapter.populateFields();
		}
	}

	/**
	 * 
	 * @param message
	 */

	private void debugLog(String message) {
		Log.i(MainActivity.TAG, message);
	}

	public Player getPlayer() {
		return Games.Players.getCurrentPlayer(mHelper.getApiClient());
	}

	/**
	 * Architect url listener (wikitude)
	 */
	@Override
	public boolean urlWasInvoked(String arg0) {
		debugLog("Clicked Coin: " + arg0);
		debugLog("Clicked Coin: " + arg0.substring("architectsdk://".length()));

		boolean sound = mSharedPrefs.getBoolean(prefs_sound, false);
		if (sound) {
			mediaPlayer.start();
		}
		int coins = mSharedPrefs.getInt(prefs_amount, 0);
		coins++;
		SharedPreferences.Editor editor = mSharedPrefs.edit();
		editor.putInt(prefs_amount, coins);
		editor.commit();
		return false;
	}

	public class SignOutDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Do you really want to sign out from Google Play?")
					.setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							// Call sign out
							Log.i(TAG, "PositiveButton");
							mHelper.signOut();
							// remove profile image
							File file = new File(getFilesDir(), profileImageFilename);
							file.delete();
							// remove name
							Editor editor = mSharedPrefs.edit();
							editor.remove(prefs_name);
							editor.commit();
							// update profile fragment
							// mAdapter.populateFields();
							// update action bar
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
}
