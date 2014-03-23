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

public class ItemDetailPage extends Fragment {
	
	private static final String tag = "ItemDetailPage";
	private static int index = -1;
	
	public ItemDetailPage(int i) {
		index = i;
	}
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((RangerFLink)activity).setCurrentFragment(this);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = null;
		if(index < 0) {
			Log.d(tag, "index: "+index);
		} else {
			try {
				v = inflater.inflate(R.layout.dev_item_detail, container, false);
				ImageView iv = (ImageView)v.findViewById(R.id.dev_icon); 
				iv.setImageBitmap((RangerFLink.finders.get(index).getThumbnail()));
				iv.setOnClickListener(editItem);
				((TextView)v.findViewById(R.id.dev_name)).setText(RangerFLink.finders.get(index).getName());
			} catch(Throwable e) {
				Log.d(tag, e.getLocalizedMessage());
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
			ft.replace(R.id.fragment1, new ItemEditPage());
			ft.addToBackStack(null);
			ft.commit();
		}
	};
}
