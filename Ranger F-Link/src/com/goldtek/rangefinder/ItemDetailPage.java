package com.goldtek.rangefinder;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
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
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = null;
		if(getIndex() < 0) {
			Log.d(tag, "index: "+getIndex());
		} else {
			try {
				ItemDetail iDev = RangerFLink.finders.get(getIndex());
				this.isBuzzerOn = ((RangerFLink)getActivity())
						.getBuzzerState(iDev.getMac());
				v = inflater.inflate(R.layout.dev_item_detail, container, false);
				v.findViewById(R.id.dev_find).setOnClickListener(setBuzzerPower);
				ImageView iv = (ImageView)v.findViewById(R.id.dev_icon); 
				iv.setImageBitmap(iDev.getThumbnail());
				iv.setOnClickListener(editItem);
				((TextView)v.findViewById(R.id.dev_name))
					.setText(RangerFLink.finders.get(getIndex()).getName());
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
	
	
	private boolean isBuzzerOn = false;
	View.OnClickListener setBuzzerPower = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(isBuzzerOn) {
				isBuzzerOn = false;
			} else {
				isBuzzerOn = true;
			}
			((RangerFLink)getActivity()) 
				.buzzerOnOff(RangerFLink.finders.get(getIndex()).getMac(), isBuzzerOn);
		}
	};
}
