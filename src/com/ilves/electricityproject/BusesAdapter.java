package com.ilves.electricityproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BusesAdapter extends ArrayAdapter<Integer> {

	private Context		mContext;
	private String[]	mObjects;
	private float		mScaleFactor	= 1.f;
	private int			minHeight		= 250;
	private int			maxHeight		= minHeight * 4;
	private boolean		hasHeight		= false;

	public BusesAdapter(Context context) {
		super(context, 0);
		// TODO Auto-generated constructor stub
		mContext = context;
		mObjects = context.getResources().getStringArray(R.array.stations_array);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Log.i("MainActivity", "getView");
		View rowView = convertView;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if ((position % 2) == 0) { // is station
			rowView = inflater.inflate(R.layout.buses_list_station, parent, false);
			TextView text = (TextView) rowView.findViewById(R.id.buses_list_station_name);
			int position2 = position / 2;
			text.setText(mObjects[position2]);
			ImageView image = (ImageView) rowView.findViewById(R.id.buses_list_station_image);
			if (position2 == 1) {
				image.setImageResource(R.drawable.buses_station_bus_left);
			} else if (position2 == 3) {
				image.setImageResource(R.drawable.buses_station_bus_right);
			}
		} else { // is road
			rowView = inflater.inflate(R.layout.buses_list_road, parent, false);
		}
		// image.getHeight();
		// rowView.setScaleY(mScaleFactor);
		if ((position % 2) == 1) {
			// odd number, i.e. roads
			if (!hasHeight) {
				// maxHeight = rowView.getHeight() * 3;
				Log.i("MainActivity", "maxHeight: " + maxHeight);
				// minHeight = rowView.getHeight();
				Log.i("MainActivity", "minHeight: " + minHeight);
				if (minHeight != 0) {
					hasHeight = true;
				} else {
					return rowView;
				}
			}
			int newHeight = Math.round(minHeight * mScaleFactor);
			Log.i("MainActivity", "newHeight 1: " + newHeight);
			newHeight = Math.max(minHeight, Math.min(newHeight, maxHeight));
			Log.i("MainActivity", "newHeight 2: " + newHeight);
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(
					rowView.getLayoutParams().width, newHeight);
			rowView.setLayoutParams(params);
			LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
					rowView.getLayoutParams().width, newHeight);
			// image.setLayoutParams(params2);
			// swithc up the buses
			if (position == 1) {
				((ImageView)rowView.findViewById(R.id.buses_right_bus)).setVisibility(View.VISIBLE);
				((RelativeLayout)rowView.findViewById(R.id.buses_right_bus_text)).setVisibility(View.VISIBLE);
			} else if (position == 5) {
				((ImageView)rowView.findViewById(R.id.buses_left_bus)).setVisibility(View.VISIBLE);
				((RelativeLayout)rowView.findViewById(R.id.buses_left_bus_text)).setVisibility(View.VISIBLE);
			}
			rowView.requestLayout();
		}
		return rowView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mObjects.length + mObjects.length - 1;
	}

	public void setScale(float mScaleFactor2) {
		Log.i("MainActivity", "onScale: " + mScaleFactor2);
		// Do not wet new scalefactor every time
		if (mScaleFactor-mScaleFactor2 > 0.05 || mScaleFactor-mScaleFactor2 < -0.05) {
			mScaleFactor = mScaleFactor2;

			notifyDataSetChanged();
		}
	}

}
