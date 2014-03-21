package com.goldtek.rangefinder;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ListPage extends Fragment {
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((RangerFLink)activity).setCurrentFragment(this);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.activity_list_view, container, false);
		ListView lv = (ListView)v.findViewById(R.id.the_view);
		lv.setAdapter(new MainListViewAdapter(this.getActivity()));
		return v;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
}
