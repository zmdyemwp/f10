package com.goldtek.bluetoothleservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class BluetoothLeService extends Service {

	public static boolean bStart = false;
	private final LocalBinder mBinder = new LocalBinder();

	//	non-static variable to verify the existence of the service instance
	public int count = 0;
	
	public class LocalBinder extends Binder {
		BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}
	
	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		if( !bStart ) {
			bStart = true;
			h.post(r);
		}
		return Service.START_STICKY;
	}



	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	Handler h = new Handler();
	Runnable r = new Runnable() {

		@Override
		public void run() {
			count++;
			h.postDelayed(r, 1000);
		}
		
	};

	@Override
	public boolean onUnbind(Intent intent) {
		return this.onUnbind(intent);
	}
	
	/**
	 * 	Export Methods
	 * 		int GetCount();
	 * 
	 * */
	public int GetCount() {
		return this.count;
	}
}
