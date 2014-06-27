package com.goldtek.rangefinder;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainPage extends Fragment {

	public static final String tag = "MainPage";
	private static RangerFLink home;
	private static String currentSelected;
	private static int currentSelectedIndex;
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		home = (RangerFLink)activity;
		home.setCurrentFragment(this);
		home.invalidateOptionsMenu();
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.activity_grid_view, container, false);
		GridView gv = (GridView)v.findViewById(R.id.the_view);
		gv.setAdapter(new MainGridViewAdapter(home));
		gv.setOnItemClickListener(onClick);
		return v;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	public static ProgressDialog spd;
	AbsListView.OnItemClickListener onClick =
			new AbsListView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO: switch to device detail page
					currentSelectedIndex = arg2;
					currentSelected = RangerFLink.finders.get(arg2).getMac();
					if( home.checkDeviceConnected(currentSelected) ) {
						FragmentManager fm = home.getFragmentManager();
						FragmentTransaction tran = fm.beginTransaction();
						tran.replace(R.id.fragment1, ItemDetailPage.newInstance(currentSelected));
						tran.addToBackStack(tag);
						tran.commit();
					} else {
						// TODO: connect to device
						home.connectBleDevice(RangerFLink.finders.get(arg2).getMac());
						spd = ProgressDialog.show(home,
								home.getResources().getString(R.string.connecting_title),
								RangerFLink.finders.get(arg2).getName());
						h.postDelayed(r, 300);
						timeout = 300;
					}
					//	TODO: switch to connected list page
					/*FragmentManager fm = getActivity().getFragmentManager();
					FragmentTransaction tran = fm.beginTransaction();
					tran.replace(R.id.fragment1, new ListPage()).commit();*/
				}
			};

	static final int max_timeout = 6000;
	int timeout = 0;
	Handler h = new Handler();
	Runnable r = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//if( home.checkDeviceConnected(currentSelected) ) {
			if( home.checkDeviceServiceReady(currentSelected) ) {
				timeout = 0;
				if(null != spd) {
					spd.dismiss();
					spd = null;
				}
				FragmentManager fm = home.getFragmentManager();
				FragmentTransaction tran = fm.beginTransaction();
				tran.replace(R.id.fragment1, ItemDetailPage.newInstance(currentSelected));
				tran.addToBackStack(tag);
				tran.commit();
			}
			else if(timeout > max_timeout && null != spd) {
				home.disconnectBleDevice(currentSelected);
				spd.dismiss();
				spd = null;
			} else if(null != spd) {
				timeout += 300;
				h.postDelayed(r, 300);
			}
		}
	};
}
