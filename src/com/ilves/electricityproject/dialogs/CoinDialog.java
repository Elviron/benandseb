package com.ilves.electricityproject.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ilves.electricityproject.R;

public class CoinDialog extends DialogFragment {

	private Activity	mContext;
	private TextView	daysTextView;

	public int			days;

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
		// set numbers under seekbar
		LinearLayout ll = (LinearLayout) v.findViewById(R.id.dialog_coins_numbers);
		for (int i = 0; i < ll.getChildCount(); i++) {
			((TextView) ll.getChildAt(i)).setText("" + i);
		}
		// save reference to the textview containing the number of views
		daysTextView = (TextView) v.findViewById(R.id.dialog_coins_days);
		// set change listener on seekbar
		((SeekBar) v.findViewById(R.id.dialog_coins_seekbar)).setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				if (progress != 1) {
					daysTextView.setText(progress + " days");
				} else {
					daysTextView.setText(progress + " day");
				}
			}
		});
		;
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(v)
		// Add action buttons
				.setPositiveButton("Charge", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// sign in the user ...
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						CoinDialog.this.getDialog().cancel();
					}
				});
		return builder.create();
	}
}
