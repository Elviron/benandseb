package com.ilves.electricityproject;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class BusesAdapter extends ArrayAdapter<Integer> {

	private Context				mContext;
	private ArrayList<Integer>	mObjects;

	public BusesAdapter(Context context) {
		super(context, 0);
		// TODO Auto-generated constructor stub
		mContext = context;
		mObjects = new ArrayList<Integer>();
		mObjects.add(R.drawable.stop_lindholmen);
		mObjects.add(R.drawable.road_bus);
		mObjects.add(R.drawable.stop_gotaplatsen);
		mObjects.add(R.drawable.road_empty);
		mObjects.add(R.drawable.stop_gotaplatsen);
		mObjects.add(R.drawable.road_empty);
		mObjects.add(R.drawable.stop_gotaplatsen);
		mObjects.add(R.drawable.road_empty);
		mObjects.add(R.drawable.stop_gotaplatsen);
		mObjects.add(R.drawable.road_empty);
		mObjects.add(R.drawable.stop_johanneberg);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
	    if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.buses_list_item, parent, false);
		}
		ImageView image = (ImageView) rowView.findViewById(R.id.buses_list_item_image);
		image.setImageResource(mObjects.get(position));
		return rowView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mObjects.size();
	}
	
}
