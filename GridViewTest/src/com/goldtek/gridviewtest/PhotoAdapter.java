package com.goldtek.gridviewtest;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PhotoAdapter extends BaseAdapter {

	private Context c;

	static final String tag = "PhotoAdapter";

	private ArrayList<File> photos = new ArrayList<File>();

	
	public PhotoAdapter(Context context) {
		c = context;
	}
	
	public int RefreshFiles() {
		return RefreshFiles(null);
	}
	public int RefreshFiles(String path) {
		try {
			File dir;
			if(null == path) {
				photos.clear();
				dir = Environment.getExternalStoragePublicDirectory(
			            Environment.DIRECTORY_DCIM);
			} else {
				dir = new File(path);
			}

			File[] photolist = dir.listFiles();
			if(null != photolist) {
				for(File f:photolist) {
					if(f.isDirectory()) {
						RefreshFiles(f.toString());
					} else {
						photos.add(f);
					}
				}
			}
			
		} catch (Throwable e) {
			Log.d(tag, e.getLocalizedMessage());
		}
		return photos.size();
	}

	public void ShowAll() {
		for(File f:photos) {
			Log.d(tag, f.toString());
		}
		Log.d(tag, "Total: "+photos.size());
	}	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return RefreshFiles();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		if(0 <= arg0 && arg0 < RefreshFiles()) {
			return photos.get(arg0);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView i;
		if(null == convertView) {
			i = new ImageView(c);
			/*i.setLayoutParams(new GridView.LayoutParams(45, 45));
            i.setAdjustViewBounds(false);*/
            i.setScaleType(ImageView.ScaleType.CENTER_CROP);
            i.setPadding(8, 8, 8, 8);
			i.setImageURI(Uri.fromFile(photos.get(position)));
			Log.d(tag, String.format("getView(%d)", photos.size()));
		} else {
			i = (ImageView)convertView;
		}
		return i;
	}

}
