package com.goldtek.bluetoothleservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class TestActivity extends Activity {

	private static final String tag = "TestActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.test_main);
		
		Intent i = new Intent(this, BluetoothLeService.class);
		this.startService(i);
		this.bindService(i, serviceConn, BIND_AUTO_CREATE);
		
		findViewById(R.id.button1).setOnClickListener(l);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		while(null == mService) {
			SystemClock.sleep(1000);
		}
		TextView tv = (TextView)findViewById(R.id.button1);
		tv.setText(""+mService.GetCount());
	}
	
	BluetoothLeService mService = null;
	
	ServiceConnection serviceConn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.d(tag, "onServiceConnected");
			mService = ((BluetoothLeService.LocalBinder)service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	View.OnClickListener l = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			TextView tv = (TextView)findViewById(R.id.button1);
			tv.setText(""+mService.GetCount());
		}
	};
}
