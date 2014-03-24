package com.goldtek.rangefinder;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

public class RangerFLink extends Activity {
	static final String tag = "Ranger F-Link";
	public static int image_width = 0;
	Fragment currentFragment = null;
	/*************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //image_width = 320;
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        image_width = dm.widthPixels/3;
        Log.d(tag, "Metrics::widthPixels := "+dm.widthPixels);
        try {
        	new RangerFLink.FinderListBuilder().execute();
        	FragmentManager fragmentManager = getFragmentManager();
	        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	        fragmentTransaction.add(R.id.fragment1, new MainPage()).commit();
        } catch(Throwable e) {
        	Log.d(tag, e.getLocalizedMessage());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
    	/*AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("TEst");
        ab.create().show();*/
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
    	AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage(this.getResources().getString(R.string.version));
        ab.create().show();
        return true;
    }


    public void setCurrentFragment(Fragment f) {
    	this.currentFragment = f;
    }
    public Fragment getCurrentFragment() {
    	return this.currentFragment;
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
    private static final String[] testmac = {
		"aa:bb:cc:dd:ee:ff",
		"ff:ee:dd:cc:bb:aa",
		"11:22:33:44:55:66",
		"00:00:00:00:00:00",
		"12:23:34:45:56:67",
		"12:23:34:45:56:69",
	};

    public static final String DEV_NAME = "NamePref";
	public static final String DEV_IMAGE = "ImagePref";
	private static final String defaultImgUri = "android.resource://com.goldtek.rangefinder/drawable/dog144";
	private static int total = 0;
	public static ArrayList<ItemDetail> finders = new ArrayList<ItemDetail>();

	public static int getTotal() {
		return total;
	}
	public class ItemDetail {
		private static final String tag = "ItemDetail";
		String mac = null;
		String name = null;
		String image = null;
		Bitmap thumbnail = null;
		public ItemDetail(String mac_address, String device_name, String image_uri) {
			mac = mac_address;
			name = device_name;
			image = image_uri;
			Log.d(tag, String.format("new ItemDetail(%s,%s,%s)", mac,name,image));
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
								.getBitmap(	getContentResolver(),
											Uri.parse(image)),
											image_width,image_width,false);
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
				return getResources().getString(R.string.default_dev_name);
			}
			return name;
		}
		public String getImage() {
			return image;
		}
		public Bitmap getThumbnail() {
			return thumbnail;
		}
		
		public void SetName(String s) {
			name = s;
			SharedPreferences devName = getSharedPreferences(DEV_NAME, 0);
			SharedPreferences.Editor se = devName.edit();
			se.putString(mac, name).commit();
		}
		
		public void SetImage(String s) {
			image = s;
			SharedPreferences devImage = getSharedPreferences(DEV_IMAGE, 0);
			SharedPreferences.Editor se = devImage.edit();
			se.putString(mac, image).commit();
			Log.d(tag, "ItemDetail.SetImage()::"+s);
		}
		
		public void SetThumbnail(Bitmap b) {
			thumbnail = b;
		}
	}

	private class FinderListBuilder extends AsyncTask<Void,Void,Void> {
		@Override
		protected Void doInBackground(Void... params) {
			SharedPreferences devName = getSharedPreferences(DEV_NAME, 0);
			SharedPreferences devImage = getSharedPreferences(DEV_IMAGE, 0);

			{
				//	TODO: for TEST only, initialize the shared preference
				Map<String, ?> devs = devImage.getAll();
				if(0 == devs.size()) {
					Log.d(tag, "XXXXXXXXXXXXXXXXXX");
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
			//notifyDataSetChanged();
			if(null != currentFragment) {
				((BaseAdapter)((AbsListView)currentFragment
						.getView()
						.findViewById(R.id.the_view))
						.getAdapter()).notifyDataSetChanged();
			}
		}
		
	}
}
