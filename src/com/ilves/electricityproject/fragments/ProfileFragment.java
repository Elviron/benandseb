package com.ilves.electricityproject.fragments;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ilves.electricityproject.FragmentNotice;
import com.ilves.electricityproject.MainActivity;
import com.ilves.electricityproject.R;
import com.ilves.electricityproject.utils.GlobalStrings;
import com.ilves.electricityproject.utils.PrefsHelper;

public class ProfileFragment extends Fragment implements
		FragmentNotice, OnSharedPreferenceChangeListener {

	private static final String	TAG		= "ProfileFragment";

	private String				mName	= null;

	private Drawable			mProfileImage;
	private ImageView			mProfileImageView;
	private TextView			mProfileTextView;

	private TextView	mAmountTextView;

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
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.titles_array, R.layout.spinner_textview);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
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
		//Editor editor = mSharedPrefs.edit();
		populateFields();
		return v;// super.onCreateView(inflater, container, savedInstanceState);
	}

	public void setName(String name) {
		mName = name;
		SharedPreferences mSharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		Editor editor = mSharedPrefs.edit();
		editor.putString("profile_name", name);
		if (mProfileTextView != null) {
			mProfileTextView.setText(mName);
		}
	}

	public void setIcon(Drawable drawable) {
		//mProfileImage = drawable;
		if (mProfileImageView != null) {
			pasteIcon();
		}
	}

	public void pasteIcon() {
		File file = new File(getActivity().getFilesDir(), MainActivity.profileImageFilename);
		Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
		if (bitmap != null) {
			mProfileImageView.setImageBitmap(bitmap);
			//mProfileImageView.setImageDrawable(mProfileImage);
			//mProfileTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, mProfileImage, null, null);
		} else {
			mProfileImageView.setImageResource(R.drawable.ic_user);
			//mProfileTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_user, 0, 0);
		}
	}
	
	/**
	 * Update info from shared prefs and file system
	 * image and name
	 */
	public void populateFields() {
		Log.i("ProfileFragment", "populateFields");
		File file = new File(getActivity().getFilesDir(), MainActivity.profileImageFilename);
		Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
		if (bitmap != null) {
			mProfileImageView.setImageBitmap(bitmap);
			//mProfileImageView.setImageDrawable(mProfileImage);
			//mProfileTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, mProfileImage, null, null);
		} else {
			mProfileImageView.setImageResource(R.drawable.ic_user);
			//mProfileTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_user, 0, 0);
		}
		// put name
		SharedPreferences mSharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		String name = mSharedPrefs.getString(getString(R.string.prefs_name), getString(R.string.profile_placeholder_name));
		mProfileTextView.setText(name);
		// put amount
		mSharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		int amount = mSharedPrefs.getInt(getString(R.string.prefs_amount), 0);
		mProfileTextView.setText(String.valueOf(amount));
	}

	/**
	 * FRAGMENT NOTICE
	 */

	@Override
	public void noticeActive() {
	}

	@Override
	public void noticeInactive() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addContext(MainActivity context) {
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub
		if (key.equalsIgnoreCase(getString(R.string.prefs_amount))) {
			mAmountTextView.setText(String.valueOf(sharedPreferences.getInt(GlobalStrings.prefs_amount, 0)));
		} else {
			// user logged in or out
			populateFields();
		}
	}

}
