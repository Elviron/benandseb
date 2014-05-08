package com.ilves.electricityproject.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ilves.electricityproject.FragmentNotice;
import com.ilves.electricityproject.MainActivity;
import com.ilves.electricityproject.R;

public class TicketFragment extends Fragment implements
		FragmentNotice {

	private MainActivity	mContext;
	private ImageView mBattery;
	private Runnable		runnable;
	private Handler			handler		= new Handler();
	private int				updateTime	= 1000;
	private int				mLevel		= 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		runnable = new Runnable() {
			@Override
			public void run() {
				// do what you need to do
				updateViews();
				// and here comes the "trick"
				handler.postDelayed(this, updateTime);
			}
		};
		super.onCreate(savedInstanceState);
	}

	protected void updateViews() {
		
		// TODO Auto-generated method stub
		mBattery.setImageResource(getImageResource(mLevel));
		mLevel++;
		if (mLevel > 7) {
			mLevel = 0;
		}
	}

	private int getImageResource(int level) {
		// TODO Auto-generated method stub
		switch (mLevel) {
		case 0:
			return R.drawable.ic_battery_7;
		case 1:
			return R.drawable.ic_battery_6;
		case 2:
			return R.drawable.ic_battery_5;
		case 3:
			return R.drawable.ic_battery_4;
		case 4:
			return R.drawable.ic_battery_3;
		case 5:
			return R.drawable.ic_battery_2;
		case 6:
			return R.drawable.ic_battery_1;
		case 7:
			return R.drawable.ic_battery_0;
		default:
			break;
		}
		return level;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.ticket, container, false);
		mBattery = (ImageView) v.findViewById(R.id.ticket_battery_image);
		return v;// super.onCreateView(inflater, container, savedInstanceState);
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
		mContext = context;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		//handler.postDelayed(runnable, updateTime);
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		handler.removeCallbacks(runnable);
		super.onStop();
	}

}
