package com.ilves.electricityproject;

import java.util.ArrayList;

import com.google.android.gms.games.achievement.Achievement;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class AchievementAdapter extends BaseAdapter {
	private Context mContext;
	private int mCount = 18;
	private ArrayList<ElectriCityAchievement> mList;
	private int mCountUnlocked = 0;

	public AchievementAdapter(Context context, ArrayList<ElectriCityAchievement> mGridList) {
        mContext = context;
        mList = mGridList;
    }
	
	public void setList(ArrayList<ElectriCityAchievement> list) {
		Log.i("ProfileFragment", "setList");
		mList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
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
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

    	imageView.setImageDrawable(mList.get(position).getDrawable());
    	/*
        if (position > 2) {
	        imageView.setImageResource(R.drawable.ach_locked);
		} else if (position == 2) {
            imageView.setImageResource(R.drawable.ach_moneybag);
		} else if (position == 1) {
            imageView.setImageResource(R.drawable.ach_2);
		} else { // (position == 0)
            imageView.setImageResource(R.drawable.ach_bingo);
		}
		*/
        return imageView;
	}
	
	public String getUnlocked() {
		return mCountUnlocked+"/"+getCount();
	}

}
