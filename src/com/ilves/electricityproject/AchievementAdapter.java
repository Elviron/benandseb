package com.ilves.electricityproject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class AchievementAdapter extends BaseAdapter {
	private Context mContext;
	
	public AchievementAdapter(Context c) {
        mContext = c;
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 39;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        if (position == 0) {
            imageView.setImageResource(R.drawable.ach_moneybag);
		} else if (position == 1) {
            imageView.setImageResource(R.drawable.ach_2);
		} else {
	        imageView.setImageResource(R.drawable.ach_locked);
		}
        return imageView;
	}

}
