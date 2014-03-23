package com.goldtek.rangefinder;

import android.app.Fragment;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BottomButtons extends Fragment {
	static private final String tag = "BottomButtons";
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		Log.d(tag, "onCreateView");
		View result = inflater.inflate(R.layout.bottom_buttons, container, false);
		result.findViewById(R.id.button1).setOnClickListener(switch2Main);
		result.findViewById(R.id.button2).setOnClickListener(switch2List);
		result.findViewById(R.id.button3).setOnClickListener(back);
		return result;
	}
	
	View.OnClickListener switch2Main = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(((RangerFLink)getActivity()).getCurrentFragment().getClass().equals(MainPage.class)) {
				Log.d(tag, "It is ALREADY in the target page");
			} else {
				//	TODO: Goto MainPage
				FragmentManager fmgr = getFragmentManager();
				fmgr.popBackStack(ListPage.tag, 0);
				fmgr.popBackStack(MainPage.tag, 0);
				FragmentTransaction ftran = fmgr.beginTransaction();
				ftran.replace(R.id.fragment1, new MainPage());
				ftran.commit();
			}
		}
	};
	
	View.OnClickListener switch2List = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(((RangerFLink)getActivity()).getCurrentFragment().getClass().equals(ListPage.class)) {
				Log.d(tag, "It is ALREADY in the target page");
			} else {
				//	TODO: Goto ListPage
				FragmentManager fmgr = getFragmentManager();
				fmgr.popBackStack(MainPage.tag, 0);
				fmgr.popBackStack(ListPage.tag, 0);
				FragmentTransaction ftran = fmgr.beginTransaction();
				ftran.replace(R.id.fragment1, new ListPage());
				ftran.commit();
			}
		}
	};
	
	View.OnClickListener back = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		}
	};
}
