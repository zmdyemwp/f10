package com.goldtek.rangefinder;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldtek.rangefinder.RangerFLink.ItemDetail;

public class MainListViewAdapter extends MainAdapter {

	private static final String tag = "MainListViewAdapter";

	public MainListViewAdapter(Context cc) {
		super(cc);
	}

	public static ArrayList<String> connectDevices;
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		connectDevices = ((RangerFLink)c).getConnectionList();
		if(null == connectDevices) {
			return 0;
		}
		for(String s:connectDevices) {
			if( ! RangerFLink.checkFinderExist(s)) {
				connectDevices.remove(s);
			}
		}
		return connectDevices.size();
	}

	ItemDetail getFinder(final String address) {
		for(ItemDetail i:RangerFLink.finders) {
			if(i.getMac().equals(address)) {
				return i;
			}
		}
		return null;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		View v = convertView;
		if(null == convertView) {
			LayoutInflater inflater = (LayoutInflater)c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.listview_dev_item, null);
		}
		try {
			ItemDetail i = getFinder(connectDevices.get(position));
			((ImageView)v.findViewById(R.id.imageView1)).setImageBitmap(i.getThumbnail());
			((TextView)v.findViewById(R.id.textView1)).setText(i.getName());
		} catch(Throwable e) {
			Log.d("MainGridViewAdapter", ""+position+": "+e.getLocalizedMessage());
		}
		return v;
	}

}
