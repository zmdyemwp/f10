package com.goldtek.rangefinder;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *		This broadcast receiver is used to monitor the BT status
 *		when the BT is turned off, terminate the main Activity. 
 */

public class BTBroadcastRecver extends BroadcastReceiver {

	static private final String tag = "BTBroadcastRecver";
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
		if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
			switch(state) {
				case BluetoothAdapter.STATE_OFF:
					break;
				case BluetoothAdapter.STATE_ON:
					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					TerminateMainActivity(context);
					break;
				default:
					break;
			}
		}
	}
	
	void TerminateMainActivity(Context c) {
		Intent i = new Intent();
		i.setClassName("com.goldtek.rangefinder",
				"com.goldtek.rangefinder.RangerFLink");
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setAction(RangerFLink.TERMINATOR);
		i.addCategory(Intent.CATEGORY_LAUNCHER);
		c.startActivity(i);
	}

}
