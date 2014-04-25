package com.ilves.electricityproject.fragments;

import com.ilves.electricityproject.AchievementAdapter;
import com.ilves.electricityproject.R;
import com.ilves.electricityproject.R.id;
import com.ilves.electricityproject.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class ProfileFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.profile, container, false);
		GridView gridview = (GridView) v.findViewById(R.id.gridview);
		gridview.setAdapter(new AchievementAdapter(this.getActivity().getApplicationContext()));
		return v;// super.onCreateView(inflater, container, savedInstanceState);
	}

}
