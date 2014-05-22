package com.ilves.electricityproject.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.ilves.electricityproject.MainActivity;
import com.ilves.electricityproject.R;

public class SettingsDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.dialog_settings, null);
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(v).setNeutralButton("Done", null);
		/*
		 * // Add action buttons .setPositiveButton("Done", new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int id) { //
		 * Send the positive button event back to the host // activity } })
		 * .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		 * public void onClick(DialogInterface dialog, int id) { // Send the
		 * negative button event back to the host // activity } });
		 */
		/*
		 * Button clearButton = (Button)
		 * v.findViewById(R.id.dialog_ticket_btn_clear);
		 * clearButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Log.i("MainActivity", "Clear coins."); } });
		 */
		Switch sw = ((Switch) v.findViewById(R.id.dialog_settings_sound_switch));
		sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean(MainActivity.prefs_sound, isChecked);
				editor.commit();
			}
		});
		SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		sw.setChecked(prefs.getBoolean(MainActivity.prefs_sound, true));
		return builder.create();
	}
}
