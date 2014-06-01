package com.ilves.electricityproject.dialogs;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ilves.electricityproject.MainActivity;
import com.ilves.electricityproject.R;

public class CoinDialog extends DialogFragment implements
		OnClickListener, OnSeekBarChangeListener {

	private Activity	mContext;
	private TextView	daysTextView;

	public int			days;
	private SeekBar		seekbar;
	private TextView	amountTextView;
	private TextView	subtractTextView;
	private TextView	equalsTextView;
	private SharedPreferences	mSharedPrefs;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View v = inflater.inflate(R.layout.dialog_coin, null);
		// save reference to the textview containing the number of views
		daysTextView = (TextView) v.findViewById(R.id.dialog_coin_days);
		amountTextView = (TextView) v.findViewById(R.id.dialog_coin_amount);
		mSharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		int amount = mSharedPrefs.getInt(MainActivity.prefs_amount, 0);
		amountTextView.setText(""+amount);
		subtractTextView = (TextView) v.findViewById(R.id.dialog_coin_amount_subtract);
		equalsTextView = (TextView) v.findViewById(R.id.dialog_coin_amount_equals);
		equalsTextView.setText(""+(mSharedPrefs.getInt(MainActivity.prefs_amount, 0)-1)+" left");
		// set change listener on seekbar
		seekbar = ((SeekBar) v.findViewById(R.id.dialog_coins_seekbar));
		if (amount == 0) {
			seekbar.setEnabled(false);
		} else if (amount < 7) {
			seekbar.setMax(amount);
		}
		seekbar.setOnSeekBarChangeListener(this);
		;
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(v)
		// Add action buttons
				.setPositiveButton("Charge", this)
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						CoinDialog.this.getDialog().cancel();
					}
				});
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		String datetimeString = mSharedPrefs.getString(MainActivity.prefs_end_of_card,
				null);
		int days_to_add = seekbar.getProgress()+1;
		DateTime dt;
		if (datetimeString != null) {
			// we have a end date for the card
			dt = DateTime.parse(datetimeString).plusDays(days_to_add);
		} else {
			// the card is empty
			// get current moment in default time zone
			dt = new DateTime();
			// translate to Stockholm/Sweden local time
			dt = dt.withZone(DateTimeZone.forID("Europe/Stockholm"))
					.withMillisOfDay(0)
					.plusDays(days_to_add - 1);
		}
		Editor editor = mSharedPrefs.edit();
		editor.putString(MainActivity.prefs_end_of_card, dt.toString());
		int coins = mSharedPrefs.getInt(MainActivity.prefs_amount, 0) - days_to_add;
		editor.putInt(MainActivity.prefs_amount, coins);
		editor.commit();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		progress++;
		if (progress != 1) {
			daysTextView.setText(progress + " days");
		} else {
			daysTextView.setText(progress + " day");
		}
		subtractTextView.setText(""+progress);
		equalsTextView.setText(""+(mSharedPrefs.getInt(MainActivity.prefs_amount, 0)-progress)+" left");
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
}
