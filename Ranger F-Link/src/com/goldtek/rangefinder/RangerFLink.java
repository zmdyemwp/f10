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
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class RangerFLink extends Activity {

	static final String tag = "Ranger F-Link";
	public static int image_width = 0;
	Fragment currentFragment = null;
	BluetoothAdapter ba = null;
	private static final int REQUEST_ENABLE_BT = 1;
	private static boolean bCancle = false;
	/*************************************************************************/
	public static final String TERMINATOR = "RANGER_W_TERMINATOR";
	BroadcastReceiver bc = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(BluetoothLeService.LOSS_LINK_ALARM)) {
				if(intent.getStringExtra("status")
						.equals(BluetoothLeService.LOSS_LINK_ALARM_DISCONNECTION)) {
					int index = getIndex(intent.getStringExtra("address"));
					if(0 <= index) {
						FragmentManager fm = getFragmentManager();
						for(int i = 0; i < fm.getBackStackEntryCount();i++) {
							fm.popBackStack();
						}
						FragmentTransaction tran = fm.beginTransaction();
						tran.replace(R.id.fragment1, new MainPage());
						tran.replace(R.id.fragment1, LossLinkNotification.newInstance(index));
						tran.addToBackStack(null);
						tran.commit();
					}
				} else if(intent.getStringExtra("status")
						.equals(BluetoothLeService.LOSS_LINK_ALARM_RECONNECTION)) {
					int index = getIndex(intent.getStringExtra("address")); 
					if(0 <= index) {
						FragmentManager fm = getFragmentManager();
						FragmentTransaction tran = fm.beginTransaction();
						tran.replace(R.id.fragment1, LossLinkReconnection.newInstance(index));
						tran.addToBackStack(null);
						tran.commit();
					}
				}
			} else if(intent.getAction().equals(TERMINATOR)) {
				finish();
			}
		}
	};
	
	FragmentManager fmgrClear() {
		FragmentManager fm = getFragmentManager();
		for(int i = 0; i < fm.getBackStackEntryCount();i++) {
			fm.popBackStack();
		}
		return fm;
	}
	
	boolean InitBluetooth() {
		//		Bluetooth
     	//	Use this check to determine whether BLE is supported on the device.  Then you can
        //	selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        //	Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        //	BluetoothAdapter through BluetoothManager.
        BluetoothManager bm =
        		(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        ba = bm.getAdapter();
        if(null == ba) {
        	Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        //	Ask to turn on Bluetooth
        if (!ba.isEnabled()) {
        	bCancle = false;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        return true;
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = new Intent();
		i.setClassName("com.goldtek.rangefinder", "com.goldtek.rangefinder.BluetoothLeService");
		startService(i);
        devImage = getSharedPreferences(DEV_IMAGE, 0);
        devName = getSharedPreferences(DEV_NAME, 0);

        setContentView(R.layout.activity_main);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        image_width = dm.widthPixels/3;

        try {
        	//	Execute AsyncTask to build finder list
	        FragmentManager fragmentManager = fmgrClear();
	        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

	        Intent intent = this.getIntent();
	        String page = intent.getStringExtra("page");
	        String mac = intent.getStringExtra("mac");

        	//	TODO: onCreate
	        Log.d("onCreate", page+"::"+mac);
	        if(null != page &&
	        		page.contains(BluetoothLeService.LOSS_LINK_ALARM_DISCONNECTION)) {
	        	Log.d("++++++LINK LOST!!", page+"::"+mac);
	        	fragmentTransaction.replace(R.id.fragment1,
	        			LossLinkNotification.newInstance(getIndex(mac)));
	        	fragmentTransaction.addToBackStack(null).commit();
	        } else if(null != page &&
	        		page.contains(BluetoothLeService.LOSS_LINK_ALARM_RECONNECTION)) {
	        	fragmentTransaction.replace(R.id.fragment1,
	        			LossLinkReconnection.newInstance(getIndex(mac)));
	        	fragmentTransaction.addToBackStack(null).commit();
	        } else {
	        	fragmentTransaction.add(R.id.fragment1, new MainPage());
		        fragmentTransaction.commit();
		        //scanLeDevice(true);
		    }
        } catch(Throwable e) {
        }
    }

    @Override
    protected void onResume() {
    	super.onResume();

    	Log.d(tag, "++++++++OnResume()");
    	
    	if(bCancle) {
    		bCancle = false;
    		finish();
    		return;
    	}

    	if( !InitBluetooth() ) {
    		return;
    	}
    	//	Connect to BLE service
    	this.bindBleService();
    	//	Register Receiver
    	registerReceiver(bc, new IntentFilter(BluetoothLeService.LOSS_LINK_ALARM));

    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
        	if( !bCancle) {
		        unregisterReceiver(bc);
		        this.unbindBleService();
		        scanLeDevice(false);
        	}
        } catch(Throwable e) {
        }
        //finders.clear();
	}

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	Intent i = new Intent();
		i.setClassName("com.goldtek.rangefinder", "com.goldtek.rangefinder.BluetoothLeService");
    	stopService(i);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        if( !MainPage.class.equals(currentFragment.getClass())) {
        	menu.findItem(R.id.action_scanning).setVisible(false);
        	menu.findItem(R.id.action_stop_scanning).setVisible(false);
        } else if(mScanning) {
        	menu.findItem(R.id.action_scanning).setVisible(false);
        	menu.findItem(R.id.action_stop_scanning).setVisible(true);
        } else {
        	menu.findItem(R.id.action_scanning).setVisible(true);
        	menu.findItem(R.id.action_stop_scanning).setVisible(false);
        }
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
    	//super.onBackPressed();
    	FragmentManager fm = getFragmentManager();
    	if(0 < fm.getBackStackEntryCount()) {
    		fm.popBackStack();
		} else {
			this.moveTaskToBack(true);
		}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
        	bCancle = true;
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
    Runnable r = new Runnable() {
        @Override
        public void run() {
            mScanning = false;
            ba.stopLeScan(mLeScanCallback);
            invalidateOptionsMenu();
        }
    };
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
        	total = 0;
        	finders.clear();
        	//	Add connected devices first
        	ArrayList<BluetoothDevice> devs = getConnectionList();
        	if(null != devs) {
	        	for(BluetoothDevice dev:devs) {
	        		addItem(new ItemDetail(dev));
	        	}
        	}
        	
        	try {
        		((BaseAdapter)((AbsListView)currentFragment
    				.getView()
					.findViewById(R.id.the_view)).getAdapter())
					.notifyDataSetChanged();
        	} catch(NullPointerException n) {
        	} catch(Throwable e) {
        	}

        	//	TODO: Add loss-link devices
        	/*ArrayList<BluetoothDevice> lostdevs = getLostDevices();
        	if(null != lostdevs) {
        		for(BluetoothDevice dev:lostdevs) {
        			addItem(new ItemDetail(dev));
        		}
        	}*/

        	//	Add scanned devices
            h.postDelayed(r, SCAN_PERIOD);

            mScanning = true;
            ba.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            ba.stopLeScan(mLeScanCallback);
            h.removeCallbacks(r);
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
                	if(!checkFinderExist(device)) {
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
	static SharedPreferences devImage;
	static SharedPreferences devName;
	private static final String defaultImgUri = "android.resource://com.goldtek.rangefinder/drawable/dev_default";
	private static int total = 0;
	public static ArrayList<ItemDetail> finders = new ArrayList<ItemDetail>();
	public static boolean checkFinderExist(BluetoothDevice dev) {
		for(ItemDetail i:finders) {
			if(i.getMac().equalsIgnoreCase(dev.getAddress())) {
				return true;
			} else {
			}
		}
		return false;
	}

	public int getIndex(final String address) {
		for(ItemDetail i:finders) {
			if(i.getMac().equals(address)) {
				return finders.indexOf(i);
			}
		}
		//	TODO: if NO Device found...
		ItemDetail temp = new ItemDetail(address); 
		finders.add(temp);
		return finders.indexOf(temp);
		//return -1;
	}
	
	public ItemDetail getItem(final String address) {
		for(ItemDetail i:finders) {
			if(i.getMac().equals(address)) {
				return i;
			}
		}
		ItemDetail result = new ItemDetail(address); 
		finders.add(result);
		return result;
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
		BluetoothDevice device;
		String address = "";
		String image = "";
		String name = "";
		Bitmap thumbnail = null;
		public ItemDetail(BluetoothDevice dev) {
			device = dev;
			image = devImage.getString(device.getAddress(), defaultImgUri);
			name = devName.getString(device.getAddress(),
					getResources().getString(R.string.default_dev_name));
			//	Check image settings
			createThumbnail();
		}
		
		public ItemDetail(final String mac) {
			device = null;
			address = mac;
			image = devImage.getString(mac, defaultImgUri);
			name = devName.getString(mac,
					getResources().getString(R.string.default_dev_name));
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
			} catch(Throwable e) {
				try {
					image = defaultImgUri;
					thumbnail = Bitmap.createScaledBitmap(
							MediaStore.Images.Media
							.getBitmap(	getContentResolver(),
										Uri.parse(image)),
										image_width,image_width,false);
				} catch(Throwable ee) {
				}
			}
		}
		public String getMac() {
			String result = "";
			try {
				if(null == device) {
					result = address;
				} else {
					result = device.getAddress();
				}
			} catch(Throwable e) {
				result = "";
			}
			return result; 
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
			//	set device friendly name
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

    public ArrayList<BluetoothDevice> getConnectionList() {
    	if(null == mBluetoothLeService) {
    		return null;
    	} else {
    		return mBluetoothLeService.getConnectedDevices();
    	}
    }

    public boolean checkDeviceConnected(final String address) {
    	ArrayList<BluetoothDevice> list = getConnectionList();
    	if(null == list) {
    		return false;
    	}
    	for(BluetoothDevice dev:list) {
    		if(dev.getAddress().equals(address)) {
    			return true;
    		}
    	}
    	return false;
    }
    public boolean checkDeviceServiceReady(final String address) {
    	if(null != mBluetoothLeService) {
    		return mBluetoothLeService.checkDeviceServiceReady(address);
    	}
    	return false;
    }
    
    public ArrayList<BluetoothDevice> getLostDevices() {
    	if(null == mBluetoothLeService) {
    		return null;
    	} else {
    		return mBluetoothLeService.getLostDevices();
    	}
    }
    
    public boolean checkDeviceLost(final String address) {
    	ArrayList<BluetoothDevice> devs =
    			getLostDevices();
    	for(BluetoothDevice dev:devs) {
    		if(dev.getAddress().equals(address)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public void connectBleDevice(final String address) {
    	if(null != mBluetoothLeService) {
    		if( ! mBluetoothLeService.checkDevConnected(address)) {
    			mBluetoothLeService.connect(address);
    		}
    	}
    }

    public void disconnectBleDevice(final String address) {
    	if(null != mBluetoothLeService) {
    		mBluetoothLeService.disconnect(address);
    	}
    }
    
    public void buzzerOnOff(final String address, boolean b) {
    	if(null != mBluetoothLeService) {
    		mBluetoothLeService.setBuzzerOnOff(address, b);
    	}
    }
    
    public boolean getBuzzerState(final String address) {
    	boolean result = false;
    	if(null != mBluetoothLeService) {
    		result = mBluetoothLeService.getBuzzerState(address);
    	}
    	return result;
    }
    
    public void enableFinder(final String address) {
    	if(null != mBluetoothLeService) {
    		mBluetoothLeService.enableFinder(address);
    	}
    }
    
    public void resetFinder(final String address) {
    	if(null != mBluetoothLeService) {
    		mBluetoothLeService.resetFinder(address);
    	}
    }
}
