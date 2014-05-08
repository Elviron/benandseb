package com.ilves.electricityproject.fragments;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements.LoadAchievementsResult;
import com.ilves.electricityproject.AchievementAdapter;
import com.ilves.electricityproject.ElectriCityAchievement;
import com.ilves.electricityproject.FragmentNotice;
import com.ilves.electricityproject.MainActivity;
import com.ilves.electricityproject.R;

public class ProfileFragment extends Fragment implements
		FragmentNotice,
		ResultCallback<LoadAchievementsResult> {

	private static final String					TAG		= "ProfileFragment";

	private GridView							mGridview;
	private AchievementAdapter					mGridAdapter;
	private ArrayList<ElectriCityAchievement>	mGridList;

	private TextView							nameField;
	private String								mName	= null;

	private Drawable							mProfileImage;
	private ImageView							mProfileImageView;

	private ImageView							mRankImageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		// Google play services
		if (mGridList == null) {
			mGridList = new ArrayList<ElectriCityAchievement>();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.profile, container, false);
		mGridview = (GridView) v.findViewById(R.id.gridview);
		mGridAdapter = new AchievementAdapter(this.getActivity().getApplicationContext(),
				mGridList);
		mGridview.setAdapter(mGridAdapter);
		nameField = (TextView) v.findViewById(R.id.profile_player_name);
		if (mName != null) {
			nameField.setText(mName);
		}
		((TextView) v.findViewById(R.id.profile_ach_unlocked)).setText(mGridAdapter.getUnlocked());
		mProfileImageView = (ImageView) v.findViewById(R.id.profile_image);
		if (mProfileImage != null) {
			mProfileImageView.setImageDrawable(mProfileImage);
		} else {
			mProfileImageView.setImageResource(R.drawable.ic_user);
		}

		return v;// super.onCreateView(inflater, container, savedInstanceState);
	}

	public void setName(String name) {
		if (nameField != null) {
			nameField.setText(name);
		}
		mName = name;
	}

	public void setIcon(Drawable drawable) {
		if (drawable != null) {
			if (mProfileImageView != null) {
				mProfileImageView.setImageDrawable(drawable);
			}
		} else {
			mProfileImageView.setImageResource(R.drawable.ic_user);
		}
		mProfileImage = drawable;
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

	/**
	 * Callback for achievements
	 * 
	 * @param arg0
	 */
	@Override
	public void onResult(LoadAchievementsResult result) {
		mGridList = new ArrayList<ElectriCityAchievement>();
		AchievementBuffer aBuffer = result.getAchievements();
		Iterator<Achievement> aIterator = aBuffer.iterator();

		while (aIterator.hasNext()) {
			Achievement ach = aIterator.next();
			mGridList.add(new ElectriCityAchievement(getActivity(), ach));
		}
		aBuffer.close();
		if (mGridAdapter != null) {
			mGridAdapter.setList(mGridList);
		}
	}

}
