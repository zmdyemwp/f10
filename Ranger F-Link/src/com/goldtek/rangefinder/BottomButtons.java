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
		result.findViewById(R.id.dev_scanned).setOnClickListener(switch2Main);
		result.findViewById(R.id.dev_connected).setOnClickListener(switch2List);
		result.findViewById(R.id.back_button).setOnClickListener(back);
		return result;
	}

	FragmentManager fmgrClear() {
		FragmentManager fm = getFragmentManager();
		for(int i = 0; i < fm.getBackStackEntryCount();i++) {
			fm.popBackStack();
		}
		return fm;
	}

	View.OnClickListener switch2Main = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(((RangerFLink)getActivity()).getCurrentFragment().getClass().equals(MainPage.class)) {
				Log.d(tag, "It is ALREADY in the target page");
			} else {
				//	TODO: Goto MainPage
				FragmentManager fmgr = fmgrClear();
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
				FragmentManager fmgr = fmgrClear();
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
			FragmentManager fmgr = getFragmentManager();
			if(0 < fmgr.getBackStackEntryCount()) {
				fmgr.popBackStack();
			} else {
				getActivity().finish();
			}
		}
	};
}
