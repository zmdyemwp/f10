package com.goldtek.rangefinder;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainGridViewAdapter extends BaseAdapter {

	private Context c = null;
	private static final String[] testmac = {
		"aa:bb:cc:dd:ee:ff",
		"ff:ee:dd:cc:bb:aa",
		"11:22:33:44:55:66",
		"00:00:00:00:00:00",
	};

	private static final String defaultImgUri =
			"android.resource://com.goldtek.rangefinder/drawable/dev_default";
	private static final String DEV_NAME = "NamePref";
	private static final String DEV_IMAGE = "ImagePref";

	private static int total = 0;
	private int column_width = 0;
	/************************************************************************/

	public MainGridViewAdapter(Context cc, int cwidth) {
		c = cc;
		column_width = cwidth;
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
		try {
			((ImageView)v.findViewById(R.id.imageView1))
				.setImageBitmap(finders.get(position).getThumbnail());
			((TextView)v.findViewById(R.id.textView1))
				.setText(finders.get(position).getName());
		} catch(Throwable e) {
			Log.d("MainGridViewAdapter", ""+position+": "+e.getLocalizedMessage());
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
	static ArrayList<ItemDetail> finders = new ArrayList<ItemDetail>();

	private class ItemDetail {
		private static final String tag = "ItemDetail";
		String mac = null;
		String name = null;
		String image = null;
		Bitmap thumbnail = null;
		public ItemDetail(String mac_address, String device_name, String image_uri) {
			mac = mac_address;
			name = device_name;
			image = image_uri;
			//	this is not a good place to do such time consumptive work
			createThumbnail();
		}
		private void createThumbnail() {
			if(null == image || 0 == image.length()) {
				image = defaultImgUri;
			}
			try {
				thumbnail = Bitmap.createScaledBitmap(
							MediaStore.Images.Media
								.getBitmap(	c.getContentResolver(),
											Uri.parse(image)),
											column_width,column_width,false);
				Log.d(tag, "createThumbnail()::"+image);
			} catch(Throwable e) {
				Log.d(tag, e.getLocalizedMessage());
			}
		}
		public String getMac() {
			return mac;
		}
		public String getName() {
			if(null == name || 0 == name.length()) {
				return c.getResources().getString(R.string.default_dev_name);
			}
			return name;
		}
		public String getImage() {
			return image;
		}
		public Bitmap getThumbnail() {
			return thumbnail;
		}
	}

	private class FinderListBuilder extends AsyncTask<Void,Void,Void> {

		@Override
		protected Void doInBackground(Void... params) {
			SharedPreferences devName = c.getSharedPreferences(DEV_NAME, 0);
			SharedPreferences devImage = c.getSharedPreferences(DEV_IMAGE, 0);

			{
				//	TODO: for TEST only, initialize the shared preference
				Map<String, ?> devs = devImage.getAll();
				if(0 == devs.size()) {
					//	TODO: it needs initialization
					SharedPreferences.Editor se = devImage.edit();
					for(String s:testmac) {
						se.putString(s, defaultImgUri);
					}
					se.commit();
				}
			}

			//	TODO: scan for all devices around,
			//		and restore settings from shared preference
			//		NOW use test data!
			finders.clear();
			for(String s:testmac) {
				finders.add(
					new ItemDetail(	s, devName.getString(s,""), devImage.getString(s,""))
				);
				total = finders.size();
				this.publishProgress();
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
