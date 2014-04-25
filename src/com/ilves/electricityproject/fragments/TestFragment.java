package com.ilves.electricityproject.fragments;

import com.ilves.electricityproject.R;
import com.ilves.electricityproject.R.id;
import com.ilves.electricityproject.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestFragment extends Fragment {
	

	int mNum;
	
	/**
     * Create a new instance of TestFragment, providing "num"
     * as an argument.
     */
    public static TestFragment newInstance(int num) {
    	TestFragment f = new TestFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNum = getArguments() != null ? getArguments().getInt("num") : 1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.test_fragment_layout, container, false);
		TextView tv = (TextView) v.findViewById(R.id.text);
		tv.setText(""+(mNum+1)+" Content");
		return v;
	}
}
