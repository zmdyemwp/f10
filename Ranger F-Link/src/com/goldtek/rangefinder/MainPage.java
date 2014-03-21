package com.goldtek.rangefinder;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainPage extends Fragment {

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((RangerFLink)activity).setCurrentFragment(this);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.activity_grid_view, container, false);
		GridView gv = (GridView)v.findViewById(R.id.the_view);
		gv.setAdapter(new MainGridViewAdapter(this.getActivity()));
		gv.setOnItemClickListener(onClick);
		return v;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	AbsListView.OnItemClickListener onClick =
			new AbsListView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					
				}
			};

}
