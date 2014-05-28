package com.ilves.electricityproject.fragments;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ilves.electricityproject.MainActivity;
import com.ilves.electricityproject.R;
import com.ilves.electricityproject.utils.GlobalStrings;

public class ProfileFragment extends Fragment implements
		OnSharedPreferenceChangeListener, OnItemSelectedListener {

	private static final String	TAG		= "ProfileFragment";

	private String				mName	= null;

	private ImageView			mProfileImageView;
	private TextView			mProfileTextView;

	private TextView			mAmountTextView;
	
	private ImageView mProfileTrackImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		// Google play services
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.profile, container, false);
		Spinner spinner = (Spinner) v.findViewById(R.id.planets_spinner);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.titles_array,
				R.layout.spinner_textview);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		mProfileTrackImage = (ImageView) v.findViewById(R.id.profile_track_image);
		selectImage(spinner.getSelectedItemPosition());
		mProfileTextView = (TextView) v.findViewById(R.id.profile_player_name);
		if (mName != null) {
			mProfileTextView.setText(mName);
		}
		// ((TextView)
		// v.findViewById(R.id.profile_ach_unlocked)).setText(mGridAdapter.getUnlocked());
		mProfileImageView = (ImageView) v.findViewById(R.id.profile_image);
		mProfileTextView = (TextView) v.findViewById(R.id.profile_player_name);
		mAmountTextView = (TextView) v.findViewById(R.id.profile_electricoin_amount);
		SharedPreferences mSharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
		// Editor editor = mSharedPrefs.edit();
		populateFields();
		return v;// super.onCreateView(inflater, container, savedInstanceState);
	}

	public void setName(String name) {
		mName = name;
		SharedPreferences mSharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		Editor editor = mSharedPrefs.edit();
		editor.putString("profile_name", name);
		editor.commit();
		if (mProfileTextView != null) {
			mProfileTextView.setText(mName);
		}
	}

	/**
	 * Update info from shared prefs and file system image and name
	 */
	public void populateFields() {
		Log.i("ProfileFragment", "populateFields");
		if (getActivity() != null) {
			File file = new File(getActivity().getFilesDir(),
					MainActivity.profileImageFilename);
			if (file.exists()) {
				Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
				if (bitmap != null) {
					mProfileImageView.setImageBitmap(bitmap);
					// mProfileImageView.setImageDrawable(mProfileImage);
					// mProfileTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
					// mProfileImage, null, null);
				}
			} else {
				mProfileImageView.setImageResource(R.drawable.ic_user);
				// mProfileTextView.setCompoundDrawablesWithIntrinsicBounds(0,
				// R.drawable.ic_user, 0, 0);
			}
			// put name
			SharedPreferences mSharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
			String name = mSharedPrefs.getString(MainActivity.prefs_name,
					getString(R.string.profile_placeholder_name));
			mProfileTextView.setText(name);
			// put amount
			mSharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
			int amount = mSharedPrefs.getInt(MainActivity.prefs_amount, 0);
			mAmountTextView.setText(String.valueOf(amount));
		}
	}

	/**
	 * Preferences listener
	 */

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// Was sound pref edited?
		if (key.equalsIgnoreCase(MainActivity.prefs_sound)) {
			// then do nothing
		} else if (key.equalsIgnoreCase(MainActivity.prefs_amount)) {
			// update amount field
			if (mAmountTextView != null) {
				mAmountTextView.setText(String.valueOf(sharedPreferences.getInt(GlobalStrings.prefs_amount,
						0)));
			}
		} else {
			// user logged in or out, update fields
			populateFields();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		selectImage(position);
	}
	
	private void selectImage(int position) {
		switch (position) {
		case 0:
			mProfileTrackImage.setImageResource(R.drawable.ic_track_enviromentalist);
			break;
		case 1:
			mProfileTrackImage.setImageResource(R.drawable.ic_track_achievaholic);
			break;
		case 2:
			mProfileTrackImage.setImageResource(R.drawable.ic_track_zombie);
			break;
		case 3:
			mProfileTrackImage.setImageResource(R.drawable.ic_track_big_hearted);
			break;
		case 4:
			mProfileTrackImage.setImageResource(R.drawable.ic_track_king);
			break;
		default:
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

}
