package com.goldtek.rangefinder;

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

import com.goldtek.rangefinder.RangerFLink.ItemDetail;

public class ItemDetailPage extends Fragment {
	
	private static final String tag = "ItemDetailPage";
	
	public static ItemDetailPage newInstance(int i) {
		ItemDetailPage f = new ItemDetailPage();
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
	}
	
	ItemDetail iDev;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = null;
		if(getIndex() < 0) {
			//Log.d(tag, "index: "+getIndex());
		} else {
			try {
				iDev = RangerFLink.finders.get(getIndex());
				v = inflater.inflate(R.layout.dev_item_detail, container, false);
				//	Buzzer Button
				View buzzerButton = v.findViewById(R.id.dev_find); 
				buzzerButton.setOnClickListener(setBuzzerPower);
				//	Device Image
				ImageView iv = (ImageView)v.findViewById(R.id.dev_icon); 
				iv.setImageBitmap(iDev.getThumbnail());
				iv.setOnClickListener(editItem);
				//	Device Name
				((TextView)v.findViewById(R.id.dev_name))
					.setText(RangerFLink.finders.get(getIndex()).getName());
				//	Stop Button
				View stopButton = v.findViewById(R.id.dev_stop_alarm);
				stopButton.setOnClickListener(stopLossLinkAlarm);
				//	Check Device Lost
				if(((RangerFLink)getActivity()).checkDeviceLost(iDev.getMac())) {
					stopButton.setVisibility(View.VISIBLE);
					buzzerButton.setVisibility(View.GONE);
				} else {
					stopButton.setVisibility(View.GONE);
					buzzerButton.setVisibility(View.VISIBLE);
				}
				//	Check Device Connected
				if(((RangerFLink)getActivity()).checkDeviceConnected(iDev.getMac())) {
					stopButton.setEnabled(true);
					buzzerButton.setEnabled(true);
				} else {
					stopButton.setEnabled(false);
					buzzerButton.setEnabled(false);
				}
			} catch(Throwable e) {
				//Log.d(tag, e.getLocalizedMessage());
			}
		}
		return v;
	}
	
	View.OnClickListener editItem = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.fragment1, ItemEditPage.newInstance(getIndex()));
			ft.addToBackStack(null);
			ft.commit();
		}
	};

	View.OnClickListener setBuzzerPower = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			/*
			boolean isBuzzerOn = ((RangerFLink)getActivity())
					.getBuzzerState(iDev.getMac());
			if(isBuzzerOn) {
				isBuzzerOn = false;
			} else {
				isBuzzerOn = true;
			}*/
			((RangerFLink)getActivity())
				.buzzerOnOff(RangerFLink.finders.get(getIndex()).getMac(), false);
			try {
				Thread.sleep(100);
			} catch(Throwable e) {
			}
			((RangerFLink)getActivity())
				.buzzerOnOff(RangerFLink.finders.get(getIndex()).getMac(), true);
		}
	};
	
	View.OnClickListener stopLossLinkAlarm = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			((RangerFLink)getActivity())
				.resetFinder(RangerFLink.finders.get(getIndex()).getMac());
			
			FragmentManager fm = getFragmentManager();
			for(int i = 0; i < fm.getBackStackEntryCount();i++) {
				fm.popBackStack();
			}
			FragmentTransaction ftran = fm.beginTransaction();
			ftran.replace(R.id.fragment1, new ListPage());
			ftran.commit();
		}
	};
}
