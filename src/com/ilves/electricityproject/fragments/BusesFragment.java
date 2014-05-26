package com.ilves.electricityproject.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.ilves.electricityproject.BusesAdapter;
import com.ilves.electricityproject.R;

public class BusesFragment extends ListFragment implements
		OnTouchListener {

	private ScaleGestureDetector	mScaleDetector;
	private float					mScaleFactor	= 1.f;
	BusesAdapter					adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mScaleDetector = new ScaleGestureDetector(getActivity(), new ScaleListener());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.buses, container, false);
		// mSurface = (SurfaceView) v.findViewById(R.id.surface_view);
		adapter = new BusesAdapter(getActivity());
		setListAdapter(adapter);
		return v;// super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.i("MainActivity", "onTouch");
		// Let the ScaleGestureDetector inspect all events.
		mScaleDetector.onTouchEvent(event);
		adapter.notifyDataSetChanged();
		return false;
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

			adapter.setScale(mScaleFactor);
			// invalidate();
			return true;
		}
	}

}
