package com.ilves.electricityproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;

public class GameHelper implements ConnectionCallbacks, OnConnectionFailedListener {

	private MainActivity mContext;
	private GoogleApiClient	mGoogleApiClient;
	// Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

	public GameHelper(MainActivity context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(Drive.API)
																.addScope(Drive.SCOPE_FILE)
																.addConnectionCallbacks(this)
																.addOnConnectionFailedListener(this)
																.build();
	}

	public void connect() {
		if (!mResolvingError) { // more about this later
			mGoogleApiClient.connect();
		}
	}

	public void disconnect() {
		mGoogleApiClient.disconnect();
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
		// This callback is important for handling errors that
		// may occur while attempting to connect with Google.
		//
		// More about this in the next section.
		if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(mContext, REQUEST_RESOLVE_ERROR);
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
	
	/* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(mContext.getSupportFragmentManager(), "errordialog");
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
}
