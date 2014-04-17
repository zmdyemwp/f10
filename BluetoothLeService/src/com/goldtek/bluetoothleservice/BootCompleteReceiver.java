package com.goldtek.bluetoothleservice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			//	TODO: start service here
		}

	}

}
