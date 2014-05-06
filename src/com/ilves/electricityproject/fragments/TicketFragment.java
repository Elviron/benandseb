package com.ilves.electricityproject.fragments;

import com.ilves.electricityproject.FragmentNotice;
import com.ilves.electricityproject.R;
import com.ilves.electricityproject.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TicketFragment extends Fragment implements FragmentNotice {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.ticket, container, false);
		return v;//super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void noticeActive() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noticeInactive() {
		// TODO Auto-generated method stub
		
	}
}
