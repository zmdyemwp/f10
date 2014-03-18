package com.goldtek.rangefinder;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MainGridViewAdapter extends BaseAdapter {

	private Context c = null;
	private static final String[] testmac = {
		"aa:bb:cc:dd:ee:ff",
		"ff:ee:dd:cc:bb:aa",
		"11:22:33:44:55:66",
		"00:00:00:00:00:00",
	};
	private static final String ext_image = "#image_uri";
	private static final String ext_name = "#filename";
	private static final String defaultImgUri = "android.resource://com.goldtek.rangefinder/drawable/dev_default";
	private static final String Pref_Name = "RangerPref";

	private static int total = 0;
	/************************************************************************/

	public MainGridViewAdapter(Context cc) {
		c = cc;
		//	Initialize shared preference if needed
		new FinderListBuilder().execute();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return total;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		if(null == convertView) {
			LayoutInflater inflater = (LayoutInflater)c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.dev_item, null);
		}
		
		return v;
	}

	/**********************************************************************
	 * class ItemDetail:
			keep the MAC address, customized name, and thumbnail of each finder.
			
	 * ArrayList<ItemDetail> finders
			finders: the list of all finders
			
	 * class FinderListBuilder extends AsyncTask:
	  		Asynchronously restore item data
	  		
	 * 
	 * **/
	ArrayList<ItemDetail> finders = new ArrayList<ItemDetail>();

	private class ItemDetail {
		String mac;
		String name;
		String image;
		public ItemDetail(String mac_address, String device_name, String image_uri) {
			mac = mac_address;
			name = device_name;
			image = image_uri;
		}
		public String getMac() {
			return mac;
		}
		public String getName() {
			return name;
		}
		public String getImage() {
			return image;
		}
	}

	private class FinderListBuilder extends AsyncTask<Void,Void,Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO retrieve information in shared preference
			SharedPreferences devices = c.getSharedPreferences(Pref_Name, 0);
			Map<String, ?> devs = devices.getAll();
			if(0 == devs.size()) {
				//	TODO: it needs initialization
				SharedPreferences.Editor se = devices.edit();
				for(String s:testmac) {
					se.putString(s+ext_image, "android.resource://com.goldtek.rangefinder/drawable/dev_default");
					se.putString(s+ext_name, "Range Finder");
					se.commit();
				}
				devs = devices.getAll();
			}
			//	TODO: restore settings from shared preference
			total = devs.size()/2;
			for(Map.Entry<String, ?> e:devs.entrySet()) {
				
			}
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Void... params) {
			super.onProgressUpdate();
			notifyDataSetChanged();
		}
		
	}
	
}
