package com.goldtek.rangefinder;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;
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

	private static ArrayList<BluetoothDevice> lostDevices;
	private static ArrayList<BluetoothDevice> connectedDevices;
	public static ArrayList<BluetoothDevice>
					listDevices = new ArrayList<BluetoothDevice>();

	boolean checkDeviceLost(final String address) {
		for(BluetoothDevice dev:lostDevices) {
			if(dev.getAddress().equals(address)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		lostDevices = ((RangerFLink)c).getLostDevices();
		connectedDevices = ((RangerFLink)c).getConnectionList();
		listDevices.clear();
		/*if(null != lostDevices) {
			listDevices.addAll(lostDevices);
		}*/
		if(null != connectedDevices) {
			listDevices.addAll(connectedDevices);
		}

		if(null == listDevices) {
			return 0;
		}
		for(BluetoothDevice dev:listDevices) {
			if( ! RangerFLink.checkFinderExist(dev)) {
				listDevices.remove(dev);
			}
		}
		return listDevices.size();
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
			if(this.checkDeviceLost(listDevices.get(position).getAddress())) {
				v.setBackground(c.getResources().getDrawable(R.drawable.red_round_rect));
			}
			ItemDetail i = getFinder(listDevices.get(position).getAddress());
			((ImageView)v.findViewById(R.id.imageView1)).setImageBitmap(i.getThumbnail());
			((TextView)v.findViewById(R.id.textView1)).setText(i.getName());
		} catch(Throwable e) {
			Log.d(tag, ""+position+": "+e.getLocalizedMessage());
		}
		return v;
	}

}
