package com.goldtek.rangefinder;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class RangerFLink extends Activity {
	static final String tag = "Ranger F-Link";
	public static int image_width = 0;
	Fragment currentFragment = null;
	BluetoothAdapter ba = null;
	private static final int REQUEST_ENABLE_BT = 1;
	/*************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devImage = getSharedPreferences(DEV_IMAGE, 0);
        devName = getSharedPreferences(DEV_NAME, 0);
        //	Bluetooth
     	//	Use this check to determine whether BLE is supported on the device.  Then you can
        //	selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        //	Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        //	BluetoothAdapter through BluetoothManager.
        BluetoothManager bm =
        		(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        ba = bm.getAdapter();
        if(null == ba) {
        	Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        image_width = dm.widthPixels/3;
        Log.d(tag, "Metrics::widthPixels := "+dm.widthPixels);
        try {
        	//	TODO: Execute AsyncTask to build finder list
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
        
        if( !MainPage.class.equals(currentFragment.getClass())) {
        	Log.d(tag, "CURRENT FRAGMENT: "+currentFragment.getClass());
        	menu.findItem(R.id.action_scanning).setVisible(false);
        	menu.findItem(R.id.action_stop_scanning).setVisible(false);
        } else if(mScanning) {
        	menu.findItem(R.id.action_scanning).setVisible(false);
        	menu.findItem(R.id.action_stop_scanning).setVisible(true);
        } else {
        	menu.findItem(R.id.action_scanning).setVisible(true);
        	menu.findItem(R.id.action_stop_scanning).setVisible(false);
        }
    	/*AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("TEst");
        ab.create().show();*/
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
    	if(R.id.action_settings == item.getItemId()) {
	    	AlertDialog.Builder ab = new AlertDialog.Builder(this);
	        ab.setMessage(this.getResources().getString(R.string.version));
	        ab.create().show();
    	} else if(R.id.action_scanning == item.getItemId()) {
    		this.scanLeDevice(true);
    	} else if(R.id.action_stop_scanning == item.getItemId()) {
    		this.scanLeDevice(false);
    	}
        return true;
    }

    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	//this.moveTaskToBack(true);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if (!ba.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Log.d(tag, "onResume()::REQUEST_ENABLE_BT");
        } else {
        	Log.d(tag, "onResume()::Bluetooth is ready to use!");
        }
    	//	Scan BLE devices
    	scanLeDevice(true);
    	//	Connect to BLE service
    	this.bindBleService();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        this.unbindBleService();
        scanLeDevice(false);
        finders.clear();
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setCurrentFragment(Fragment f) {
    	this.currentFragment = f;
    	this.invalidateOptionsMenu();
    }
    public Fragment getCurrentFragment() {
    	return this.currentFragment;
    }

    
    /**********************************************************************
     * 
     * */
    private static final long SCAN_PERIOD = 10000;
    boolean mScanning = false;
    Handler h = new Handler();
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
        	total = 0;
        	finders.clear();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    ba.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            ba.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            ba.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

 // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	if(!checkFinderExist(device.getAddress())) {
                		addItem(new ItemDetail(device));
                	}
                	if(null != currentFragment) {
                		try {
	                		((BaseAdapter)((AbsListView)currentFragment
	        						.getView()
	        						.findViewById(R.id.the_view)).getAdapter())
	        						.notifyDataSetChanged();
                		} catch(Throwable e) {
                			if(null != e.getLocalizedMessage()) {
                				Log.d(tag, e.getLocalizedMessage());
                			}
                		}
        			}
                }
            });
        }
    };
    /**********************************************************************
	 * class ItemDetail:
			keep the MAC address, customized name, and thumbnail of each finder.
			
	 * ArrayList<ItemDetail> finders
			finders: the list of all finders
			
	 * class FinderListBuilder extends AsyncTask:
	  		Asynchronously restore item data
	  		
	 * 
	 * **/
	public static final String DEV_IMAGE = "ImagePref";
	public static final String DEV_NAME = "NamePref";
	SharedPreferences devImage;
	SharedPreferences devName;
	private static final String defaultImgUri = "android.resource://com.goldtek.rangefinder/drawable/dev_default";
	private static int total = 0;
	public static ArrayList<ItemDetail> finders = new ArrayList<ItemDetail>();
	public static boolean checkFinderExist(String address) {
		for(ItemDetail i:finders) {
			if(i.getMac().equalsIgnoreCase(address)) {
				return true;
			} else {
				Log.d(tag, String.format("%s :: %s", i.getMac(), address));
			}
		}
		return false;
	}

	public static int getTotal() {
		return total;
	}

	public static void addItem(ItemDetail item) {
		finders.add(item);
		total++;
	}
	
	public static void delItem(int index) {
		finders.remove(index);
		total--;
	}

	public class ItemDetail {
		private static final String tag = "ItemDetail";
		BluetoothDevice device;
		String image = null;
		String name = null;
		Bitmap thumbnail = null;
		public ItemDetail(BluetoothDevice dev) {
			device = dev;
			image = devImage.getString(device.getAddress(), defaultImgUri);
			name = devName.getString(device.getAddress(),
					getResources().getString(R.string.default_dev_name));
			//	Check image settings
			createThumbnail();
		}
		
		//public ItemDetail()

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
				try {
					image = defaultImgUri;
					thumbnail = Bitmap.createScaledBitmap(
							MediaStore.Images.Media
							.getBitmap(	getContentResolver(),
										Uri.parse(image)),
										image_width,image_width,false);
				} catch(Throwable ee) {
					Log.d(tag, ee.getLocalizedMessage());
				}
			}
		}
		public String getMac() {
			return device.getAddress();
		}
		public String getName() {
			//return device.getName();
			//return device.getAddress();
			return name;
		}
		public String getImage() {
			return image;
		}
		public Bitmap getThumbnail() {
			return thumbnail;
		}
		
		public void SetName(String s) {
			//	TODO: set device friendly name
			name = s;
			SharedPreferences.Editor se = devName.edit();
			se.putString(device.getAddress(), name).commit();
		}
		
		public void SetImage(String s) {
			image = s;
			SharedPreferences devImage = getSharedPreferences(DEV_IMAGE, 0);
			SharedPreferences.Editor se = devImage.edit();
			se.putString(device.getAddress(), image).commit();
		}
		
		public void SetThumbnail(Bitmap b) {
			thumbnail = b;
		}
	}

	
	/*********************************************************************************************
	 * 		ServiceConnection mServiceConnection
	 * 		
	 * */
	private BluetoothLeService mBluetoothLeService;
	private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(tag, "Unable to initialize Bluetooth");
                finish();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    public void bindBleService() {
    	Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }
    public void unbindBleService() {
    	unbindService(mServiceConnection);
    }
    public ArrayList<String> getConnectionList() {
    	if(null == mBluetoothLeService) {
    		return null;
    	} else {
    		return mBluetoothLeService.getConnectedDevices();
    	}
    }
    public void connectBleDevice(final String address) {
    	if(null != mBluetoothLeService) {
    		mBluetoothLeService.connect(address);
    	}
    }

    public void disconnectBleDevice(final String address) {
    	mBluetoothLeService.disconnect(address);
    }
}