package com.ilves.electricityproject.fragments;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;
import com.ilves.electricityproject.AchievementAdapter;
import com.ilves.electricityproject.FragmentNotice;
import com.ilves.electricityproject.MainActivity;
import com.ilves.electricityproject.R;

public class ProfileFragment extends Fragment implements
		OnItemSelectedListener,
		View.OnClickListener,
		ConnectionCallbacks,
		OnConnectionFailedListener,
		FragmentNotice {
	
	private static final String TAG = "ProfileFragment";

	/**
	 * Games client
	 */
	private GoogleApiClient		mGoogleApiClient;
	// Request code to use when launching the resolution activity
	private static final int	REQUEST_RESOLVE_ERROR	= 1001;
	// Unique tag for the error dialog fragment
	private static final String	DIALOG_ERROR			= "dialog_error";
	// Bool to track whether the app is already resolving an error
	private boolean				mResolvingError			= false;
	private boolean				mExplicitSignOut		= false;
	private boolean				hasBeenActive;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		// Google play services
		mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(Games.API)
				.addScope(Games.SCOPE_GAMES)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.profile, container, false);
		GridView gridview = (GridView) v.findViewById(R.id.gridview);
		gridview.setAdapter(new AchievementAdapter(this.getActivity()
				.getApplicationContext()));

		v.findViewById(R.id.sign_in_button).setOnClickListener(this);
		Spinner spinner = (Spinner) v.findViewById(R.id.sign_out_spinner);
		// Create an ArrayAdapter using the string array and a default spinner
		ArrayList<CharSequence> list = new ArrayList<CharSequence>();
		list.add("Sebastian Ilves");
		list.add("Sign out");
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				getActivity(), android.R.layout.simple_spinner_item, list);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		return v;// super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onPause() {
		Log.i(TAG, "onPause");
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.i(TAG, "onResume");
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
		if (!mResolvingError) { // more about this later
			mGoogleApiClient.connect();
		}
	}

	@Override
	public void onStop() {
		Log.i(TAG, "onStop");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		// TODO Auto-generated method stub
		// Disconnect google api client
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
		hasBeenActive = false;
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		Log.i(TAG, "onDestroyView");
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		Log.i(TAG, "onDetach");
		// TODO Auto-generated method stub
		super.onDetach();
	}

	@Override
	public void onClick(View view) {
		Log.i(TAG, "onClick");
		if (view.getId() == R.id.sign_in_button) {
			Log.i("Click", "onClick");
			// start the asynchronous sign in flow
			// beginUserInitiatedSignIn();
			mGoogleApiClient.connect();
		}
	}

	/**
	 * OnItemSelectedListener
	 */

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if (position == 1) {
			// Sign out was pressed
			if (mGoogleApiClient.isConnected()) {
				mGoogleApiClient.disconnect();
			}
			Toast.makeText(getActivity(), "SIGN OUT!!!!", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        // The good stuff goes here.
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
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
 // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainActivity)getActivity()).onDialogDismissed();
        }
    }
    
    

	/**
	 * FRAGMENT NOTICE
	 */

	@Override
	public void noticeActive() {
		// TODO Auto-generated method stub
		hasBeenActive = true;
	}

	@Override
	public void noticeInactive() {
		// TODO Auto-generated method stub

	}

}
