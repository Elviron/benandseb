package com.ilves.electricityproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.ilves.electricityproject.FragmentNotice;
import com.ilves.electricityproject.MainActivity;
import com.ilves.electricityproject.R;

public class BusesFragment extends Fragment implements FragmentNotice {
	
	private SurfaceView mSurface;
	private TabHost mTabHost;
	private Intent mIntent;

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

		return v;// super.onCreateView(inflater, container, savedInstanceState);
	}

	public void addIntent(Intent achievementsIntent) {
		// TODO Auto-generated method stub
		mIntent = achievementsIntent;
	}

	@Override
	public void noticeActive() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noticeInactive() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addContext(MainActivity context) {
		// TODO Auto-generated method stub
		
	}

}
