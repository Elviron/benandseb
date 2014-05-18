/**
 * 
 */
package com.ilves.electricityproject;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.android.gms.games.Games;
import com.ilves.electricityproject.fragments.BusesFragment;
import com.ilves.electricityproject.fragments.ProfileFragment;
import com.ilves.electricityproject.fragments.TestFragment;
import com.ilves.electricityproject.fragments.TicketFragment;

/**
 * @author Seb
 * 
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {

	private MainActivity			mContext;
	private List<FragmentNotice>	list;
	private TicketFragment			tf;

	private ProfileFragment			pf;
	private BusesFragment	bf;

	public MainFragmentAdapter(FragmentManager fragmentManager, MainActivity mainActivity) {
		super(fragmentManager);
		mContext = mainActivity;
		list = new ArrayList<FragmentNotice>();
		// Add pages
		tf = new TicketFragment();
		list.add(tf);
		bf = new BusesFragment();
		list.add(bf);
		pf = new ProfileFragment();
		pf.addContext(mContext);
		list.add(pf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int position) {
		return (Fragment) list.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			return mContext.getString(R.string.ticket);
		case 1:
			return mContext.getString(R.string.buses);
		case 2:
			return mContext.getString(R.string.profile);
		default:
			return "None";
		}
	}

	public void setActive(int position) {
		// TODO Auto-generated method stub
		list.get(position).noticeActive();
	}

	public TicketFragment getTf() {
		return tf;
	}

	public ProfileFragment getPf() {
		return pf;
	}
	
	public void setName(String name) {
		pf.setName(name);
	}

	public void setIcon(Drawable drawable) {
		// TODO Auto-generated method stub
		pf.setIcon(drawable);
	}

}
