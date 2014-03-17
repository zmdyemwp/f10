package com.goldtek.rangefinder;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MainGridViewAdapter extends BaseAdapter {

	private Context c = null;
	private static final String[] Key_Ext = {
		"_image_name",
		"_image_id",
		"_device_name",
	};
	private static final String Pref_Name = "RangerPref";
	public MainGridViewAdapter(Context cc) {
		c = cc;
		//	Initialize shared preference if needed
		SharedPreferences devices = c.getSharedPreferences(Pref_Name, 0);
		Map<String, ?> devs = devices.getAll();
		if(0 == devs.size()) {
			//	TODO: it needs initialization
		} else {
			
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
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
		return null;
	}

}
