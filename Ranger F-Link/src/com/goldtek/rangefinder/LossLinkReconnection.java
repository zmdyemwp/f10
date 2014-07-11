package com.goldtek.rangefinder;

import com.goldtek.rangefinder.RangerFLink.ItemDetail;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LossLinkReconnection extends Fragment {
private static final String tag = "LossLinkReconnection";
	
	public static LossLinkReconnection newInstance(int i) {
		LossLinkReconnection f = new LossLinkReconnection();
		Bundle b = new Bundle();
		b.putInt("index", i);
		f.setArguments(b);
		return f;
	}
	
	private int getIndex() {
		return this.getArguments().getInt("index");
	}
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((RangerFLink)activity).setCurrentFragment(this);
		// Start Alarm
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		//	Stop Alarm
	}
	
	ItemDetail iDev;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//	TODO: Stop Notification Alarm
		LossLinkNotification.stopAlarm();
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.loss_link_reconnection, container, false);
//		Device Image
		iDev = RangerFLink.finders.get(getIndex());
		ImageView iv = (ImageView)v.findViewById(R.id.dev_icon); 
		iv.setImageBitmap(iDev.getThumbnail());
		//	Device Name
		((TextView)v.findViewById(R.id.dev_name))
			.setText(RangerFLink.finders.get(getIndex()).getName());
		//	Stop Notification Button
		View stopNotification = v.findViewById(R.id.dev_reconnection_confirm);
		stopNotification.setOnClickListener(confirmReconnection);
		return v;
	}

	View.OnClickListener confirmReconnection = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			((RangerFLink)getActivity())
				.resetFinder(RangerFLink.finders.get(getIndex()).getMac());
			FragmentManager fmgr = fmgrClear();
			FragmentTransaction ftran = fmgr.beginTransaction();
			ftran.replace(R.id.fragment1, new ListPage());
			ftran.commit();
		}
		FragmentManager fmgrClear() {
			FragmentManager fm = getFragmentManager();
			for(int i = 0; i < fm.getBackStackEntryCount();i++) {
				fm.popBackStack();
			}
			return fm;
		}
	};
}
