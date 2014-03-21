package com.goldtek.rangefinder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainListViewAdapter extends MainAdapter {

	public MainListViewAdapter(Context cc) {
		super(cc);
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
			((ImageView)v.findViewById(R.id.imageView1))
				.setImageBitmap(RangerFLink.finders.get(position).getThumbnail());
			((TextView)v.findViewById(R.id.textView1))
				.setText(RangerFLink.finders.get(position).getName());
		} catch(Throwable e) {
			Log.d("MainGridViewAdapter", ""+position+": "+e.getLocalizedMessage());
		}
		return v;
	}

}
