package com.goldtek.rangefinder;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class MainPage extends Fragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.activity_grid_view, container, false);
		GridView gv = (GridView)v.findViewById(R.id.gridView1);
		gv.setAdapter(new MainGridViewAdapter(this.getActivity()));
		return v;
	}

}
