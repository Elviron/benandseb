package com.ilves.electricityproject.fragments;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ilves.electricityproject.MainActivity;
import com.ilves.electricityproject.R;

public class TicketFragment extends Fragment implements
		OnSharedPreferenceChangeListener {

	private MainActivity	mContext;
	private FrameLayout		mBattery;
	private TextView		mDaysLeftTextView;
	private TextView		mDayTextView;
	private TextView		mMonthTextView;
	private Runnable		runnable;
	private Handler			handler		= new Handler();
	private int				updateTime	= 1000;
	private int				mLevel		= 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		runnable = new Runnable() {
			@Override
			public void run() {
				// do what you need to do
				updateViews();
				// and here comes the "trick"
				handler.postDelayed(this, updateTime);
			}
		};
		SharedPreferences mSharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
		super.onCreate(savedInstanceState);
	}

	private String capitalize(String line) {
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}

	protected void updateViews() {

		// TODO Auto-generated method stub
		// mBattery.setImageResource(getImageResource(mLevel));
		mLevel++;
		if (mLevel > 7) {
			mLevel = 0;
		}
	}

	private int getImageResource(int level) {
		// TODO Auto-generated method stub
		switch (mLevel) {
		case 0:
			return R.drawable.ic_battery_7;
		case 1:
			return R.drawable.ic_battery_6;
		case 2:
			return R.drawable.ic_battery_5;
		case 3:
			return R.drawable.ic_battery_4;
		case 4:
			return R.drawable.ic_battery_3;
		case 5:
			return R.drawable.ic_battery_2;
		case 6:
			return R.drawable.ic_battery_1;
		case 7:
			return R.drawable.ic_battery_0;
		default:
			break;
		}
		return level;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.ticket, container, false);
		// mBattery = (ImageView) v.findViewById(R.id.ti); +id/ticket_day
		mBattery = (FrameLayout) v.findViewById(R.id.ticket_battery_bg);
		// get all textviews
		mDaysLeftTextView = (TextView) v.findViewById(R.id.ticket_days_left);
		mDayTextView = (TextView) v.findViewById(R.id.ticket_day);
		mMonthTextView = ((TextView) v.findViewById(R.id.ticket_month));
		SharedPreferences mSharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		checkAndFillDate(mSharedPrefs);
		return v;// super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		// handler.postDelayed(runnable, updateTime);
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		handler.removeCallbacks(runnable);
		super.onStop();
	}

	private void checkAndFillDate(SharedPreferences sharedPreferences) {
		// fetch date from shared preferences
		// do we have a filled card?
		String datetimeString = sharedPreferences.getString(MainActivity.prefs_end_of_card,
				null);
		String dayText, monthText, daysleftText;
		if (datetimeString != null) {
			// battery
			DateTime dt = DateTime.parse(datetimeString);
			dayText = dt.dayOfMonth().getAsString();
			monthText = capitalize(dt.monthOfYear().getAsShortText());
			// days left
			daysleftText = daysToEndOfCard(dt) + " "
					+ getResources().getString(R.string.ticket_days_left);
			mBattery.setBackgroundResource(R.drawable.battery);
		} else {
			// battery
			dayText = getResources().getString(R.string.ticket_fill);
			monthText = getResources().getString(R.string.ticket_up);
			// days left
			daysleftText = "0 " + getResources().getString(R.string.ticket_days_left);
			mBattery.setBackgroundResource(R.drawable.battery_empty);
		}
		// battery
		mDayTextView.setText(dayText);
		mMonthTextView.setText(monthText);
		// days left
		mDaysLeftTextView.setText(daysleftText);
	}

	private int daysToEndOfCard(DateTime endDate) {
		// TODO Auto-generated catch block
		LocalDate todaysDate = new LocalDate(DateTimeZone.forID("Europe/Stockholm"));
		return Days.daysBetween(todaysDate, endDate.toLocalDate()).getDays() + 1;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// see if the key is for battery or single ticket
		if (key == MainActivity.prefs_end_of_card) {
			checkAndFillDate(sharedPreferences);
		}
	}

	private void debugLog(String message) {
		Log.i(MainActivity.TAG, message);
	}
}
