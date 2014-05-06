/**
 * 
 */
package com.ilves.electricityproject;

import java.util.ArrayList;
import java.util.List;

import com.ilves.electricityproject.fragments.ProfileFragment;
import com.ilves.electricityproject.fragments.TestFragment;
import com.ilves.electricityproject.fragments.TicketFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author Seb
 * 
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {

	List<FragmentNotice>	list;
	TicketFragment	tf;
	ProfileFragment	pf;

	public MainFragmentAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
		list = new ArrayList<FragmentNotice>();
		// Add pages
		tf = new TicketFragment();
		list.add(tf);
		list.add(TestFragment.newInstance(1));
		pf = new ProfileFragment();
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
			return "Ticket";
		case 1:
			return "Buses";
		case 2:
			return "Profile";
		default:
			return "None";
		}
	}

	public void setActive(int position) {
		// TODO Auto-generated method stub
		list.get(position).noticeActive();
	}

}
