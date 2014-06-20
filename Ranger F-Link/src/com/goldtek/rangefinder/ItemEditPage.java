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

public class ItemEditPage extends Fragment {
	private static final String tag = "ItemEditPage";
	private TextView tv = null;

	public static ItemEditPage newInstance(String i) {
		ItemEditPage f = new ItemEditPage();
		Bundle b = new Bundle();
		b.putString("address", i);
		f.setArguments(b);
		return f;
	}
	
	private String getAddress() {
		return this.getArguments().getString("address");
	}
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((RangerFLink)activity).setCurrentFragment(this);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = null;
		if(null == getAddress()) {
			//Log.d(tag, "index: "+getIndex());
		} else {
			try {
				ItemDetail iDev = ((RangerFLink)getActivity()).getItem(getAddress());
				v = inflater.inflate(R.layout.dev_item_edit, container, false);
				((ImageView)v.findViewById(R.id.dev_icon)) 
					.setImageBitmap(iDev.getThumbnail());
				((TextView)v.findViewById(R.id.dev_name))
					.setText(iDev.getName());
				v.findViewById(R.id.camera_roll).setOnClickListener(openCameraRoll);
				v.findViewById(R.id.gallery).setOnClickListener(openGallery);
				tv = (TextView)v.findViewById(R.id.dev_name);
				v.findViewById(R.id.commit).setOnClickListener(applyChange);
			} catch(Throwable e) {
				//Log.d(tag, e.getLocalizedMessage());
			}
		}
		return v;
	}
	
	View.OnClickListener openCameraRoll = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.fragment1, CameraRollPage.newInstance(getAddress()));
			ft.addToBackStack(null);
			ft.commit();
		}
	};

	View.OnClickListener openGallery = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.fragment1, GalleryPage.newInstance(getAddress()));
			ft.addToBackStack(null);
			ft.commit();
		}
	};
	
	View.OnClickListener applyChange = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ItemDetail iDev = ((RangerFLink)getActivity()).getItem(getAddress());
			try {
				iDev.SetName(tv.getText().toString());
			} catch(Throwable e) {
				//Log.d(tag, e.getLocalizedMessage());
				iDev.SetName("");
			}
			FragmentManager fm = getFragmentManager();
			fm.popBackStack();
		}
	};
}
