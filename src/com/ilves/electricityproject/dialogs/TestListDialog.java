package com.ilves.electricityproject.dialogs;

import com.ilves.electricityproject.R;
import com.ilves.electricityproject.R.array;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class TestListDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle("Pick color:")
	           .setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
	               @Override
				public void onClick(DialogInterface dialog, int which) {
	               // The 'which' argument contains the index position
	               // of the selected item
	           }
	    });
	    return builder.create();
	}

}