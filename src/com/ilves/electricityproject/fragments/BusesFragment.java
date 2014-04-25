package com.ilves.electricityproject.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.ilves.electricityproject.AchievementAdapter;
import com.ilves.electricityproject.R;

public class BusesFragment extends Fragment {
	
	SurfaceView mSurface;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.buses, container, false);
		//mSurface = (SurfaceView) v.findViewById(R.id.surface_view);
		return v;// super.onCreateView(inflater, container, savedInstanceState);
	}

}
