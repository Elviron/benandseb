package com.ilves.electricityproject.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ilves.electricityproject.BusesAdapter;
import com.ilves.electricityproject.R;

public class BusesFragment extends ListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.buses, container, false);
		//mSurface = (SurfaceView) v.findViewById(R.id.surface_view);
		setListAdapter(new BusesAdapter(getActivity()));
		return v;// super.onCreateView(inflater, container, savedInstanceState);
	}

}
