package com.goldtek.rangefinder;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GalleryAdapter extends BaseAdapter {

	private static final String tag = "GalleryAdapter";
	private static ArrayList<String> icons = new ArrayList<String>();
	private static Integer[] icon_ids = new Integer[6];
	static {
		icons.clear();
		icons.add("android.resource://com.goldtek.rangefinder/drawable/big_dog");
		icons.add("android.resource://com.goldtek.rangefinder/drawable/suitcase");
		icons.add("android.resource://com.goldtek.rangefinder/drawable/boy");
		icons.add("android.resource://com.goldtek.rangefinder/drawable/small_dog");
		icons.add("android.resource://com.goldtek.rangefinder/drawable/purse");
		icons.add("android.resource://com.goldtek.rangefinder/drawable/girl");
		
		icon_ids[0] = R.drawable.big_dog;
		icon_ids[1] = R.drawable.suitcase;
		icon_ids[2] = R.drawable.boy;
		icon_ids[3] = R.drawable.small_dog;
		icon_ids[4] = R.drawable.purse;
		icon_ids[5] = R.drawable.girl;
	}
	
	private Context c;
	public GalleryAdapter(Context cc) {
		c = cc;
	}
	
	public String getUriString(int index) {
		return icons.get(index);
	}
	
	public Bitmap createBitmap(int index) {
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createScaledBitmap(
						MediaStore.Images.Media
							.getBitmap(	c.getContentResolver(),
										Uri.parse(icons.get(index))),
										RangerFLink.image_width,
										RangerFLink.image_width,false);
		} catch(Throwable e) {
			Log.d(tag, e.getLocalizedMessage());
		}
		return bitmap;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return icon_ids[position];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView i = (ImageView)convertView;
		if(null ==  convertView) {
			i = new ImageView(c);
		}
		i.setImageResource(icon_ids[position]);
		return i;
	}

}
