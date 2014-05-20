package com.ilves.electricityproject.fragments;

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

public class ProfileFragment extends Fragment implements
		FragmentNotice {

	private static final String	TAG		= "ProfileFragment";

	private TextView			nameField;
	private String				mName	= null;

	private Drawable			mProfileImage;
	private ImageView			mProfileImageView;

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
		nameField = (TextView) v.findViewById(R.id.profile_player_name);
		if (mName != null) {
			nameField.setText(mName);
		}
		// ((TextView)
		// v.findViewById(R.id.profile_ach_unlocked)).setText(mGridAdapter.getUnlocked());
		mProfileImageView = (ImageView) v.findViewById(R.id.profile_image);
		pasteIcon();
		return v;// super.onCreateView(inflater, container, savedInstanceState);
	}

	public void setName(String name) {
		mName = name;
		if (nameField != null) {
			nameField.setText(mName);
		}
	}

	public void setIcon(Drawable drawable) {
		mProfileImage = drawable;
		if (mProfileImageView != null) {
			pasteIcon();
		}
	}

	public void pasteIcon() {
		if (mProfileImage != null) {
			mProfileImageView.setImageDrawable(mProfileImage);
		} else {
			mProfileImageView.setImageResource(R.drawable.ic_user);
		}
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

}
