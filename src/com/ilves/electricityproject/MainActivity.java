package com.ilves.electricityproject;

import java.io.IOException;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.ArchitectConfig;
import com.wikitude.architect.ArchitectView.ArchitectUrlListener;
import com.wikitude.architect.ArchitectView.SensorAccuracyChangeListener;

public class MainActivity extends FragmentActivity implements
		wikitude.ArchitectViewHolderInterface {

	ViewPager								mViewPager;
	/**
	 * holds the Wikitude SDK AR-View, this is where camera, markers, compass,
	 * 3D models etc. are rendered
	 */
	protected ArchitectView					architectView;

	/**
	 * sensor accuracy listener in case you want to display calibration hints
	 */
	protected SensorAccuracyChangeListener	sensorAccuracyListener;

	/**
	 * last known location of the user, used internally for content-loading
	 * after user location was fetched
	 */
	protected Location						lastKnownLocaton;

	/**
	 * sample location strategy, you may implement a more sophisticated approach
	 * too
	 */
	protected ILocationProvider				locationProvider;

	/**
	 * location listener receives location updates and must forward them to the
	 * architectView
	 */
	protected LocationListener				locationListener;

	/**
	 * urlListener handling "document.location= 'architectsdk://...' " calls in
	 * JavaScript"
	 */
	protected ArchitectUrlListener			urlListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// get viewpager
		switch (getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			Log.i("AR", "ORIENTATION_PORTRAIT");
			// Shit happens

			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(new MainFragmentAdapter(
					getSupportFragmentManager()));
			mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					// When swiping between pages, select the
					// corresponding tab.
					getActionBar().setSelectedNavigationItem(position);
				}
			});
			final ActionBar actionBar = getActionBar();
			// Specify that tabs should be displayed in the action bar.
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			// Create a tab listener that is called when the user changes tabs.
			ActionBar.TabListener tabListener = new ActionBar.TabListener() {
				public void onTabSelected(ActionBar.Tab tab,
						FragmentTransaction ft) {
					// show the given tab
					// When the tab is selected, switch to the
					// corresponding page in the ViewPager.
					mViewPager.setCurrentItem(tab.getPosition());
				}

				public void onTabUnselected(ActionBar.Tab tab,
						FragmentTransaction ft) {
					// hide the given tab
				}

				public void onTabReselected(ActionBar.Tab tab,
						FragmentTransaction ft) {
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
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			Log.i("AR", "ORIENTATION_LANDSCAPE");
			this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
			final ArchitectConfig config = new ArchitectConfig("" /* license key */);
			this.architectView.onCreate(config);
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
			this.architectView.onPostCreate();
			try {
				this.architectView.load("index.html");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// call mandatory live-cycle method of architectView
		if (this.architectView != null) {
			this.architectView.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// call mandatory live-cycle method of architectView
		if (this.architectView != null) {
			this.architectView.onPause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// call mandatory live-cycle method of architectView
		if (this.architectView != null) {
			this.architectView.onDestroy();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

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

	@Override
	public String getARchitectWorldPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArchitectUrlListener getUrlListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getContentViewId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getWikitudeSDKLicenseKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getArchitectViewId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ILocationProvider getLocationProvider(
			LocationListener locationListener) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SensorAccuracyChangeListener getSensorAccuracyListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getInitialCullingDistanceMeters() {
		// TODO Auto-generated method stub
		return 0;
	}
}
