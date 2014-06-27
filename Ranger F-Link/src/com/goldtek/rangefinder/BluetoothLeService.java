package com.goldtek.rangefinder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;

public class BluetoothLeService extends Service {

	private final static String TAG = BluetoothLeService.class.getSimpleName();

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	/*
	 * private String mBluetoothDeviceAddress; private BluetoothGatt
	 * mBluetoothGatt;
	 */
	private ArrayList<BluetoothGatt> mBluetoothGatts = new ArrayList<BluetoothGatt>();

	private int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";

	public final static String LOSS_LINK_ALARM = "com.goldtek.bluetooth.le.LOSS_LINK_ALARM";

	public final static String LOSS_LINK_ALARM_DISCONNECTION = "com.goldtek.bluetooth.le.LOSS_LINK_ALARM_DISCONNECTION";

	public final static String LOSS_LINK_ALARM_RECONNECTION = "com.goldtek.bluetooth.le.LOSS_LINK_ALARM_RECONNECTION";

	public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID
			.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

	// Implements callback methods for GATT events that the app cares about. For
	// example,
	// connection change and services discovered.
	boolean bCallBackBlock = false;
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			if(bCallBackBlock) {
				return ;
			}

			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				// Attempts to discover services after successful connection.
				gatt.discoverServices();
				// TODO: check if the device is belong to loss-link list
				// if it does, this is a reconnection
				final String mac = gatt.getDevice().getAddress();
				if (checkDevLost(mac)) {
					Intent i = new Intent();
					i.setClassName("com.goldtek.rangefinder",
							"com.goldtek.rangefinder.RangerFLink");
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.putExtra("page",
							BluetoothLeService.LOSS_LINK_ALARM_RECONNECTION);
					i.putExtra("mac", mac);
					i.setAction(Intent.ACTION_MAIN);
					i.addCategory(Intent.CATEGORY_LAUNCHER);
					startActivity(i);
					try {
						Thread.sleep(500);
					} catch (Throwable e) {
					}
					broadcastUpdate(LOSS_LINK_ALARM, gatt.getDevice()
							.getAddress(), LOSS_LINK_ALARM_RECONNECTION);
				}

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				broadcastUpdate(intentAction);
				// TODO: check if the device is belong to connect list
				// if not, this is a loss link!
				final String mac = gatt.getDevice().getAddress();
				if (checkGattExist(mac)) {
					if(!lostDev.contains(gatt)) {
						lostDev.add(gatt);
					}
					Intent i = new Intent();
					i.setClassName("com.goldtek.rangefinder",
							"com.goldtek.rangefinder.RangerFLink");
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.putExtra("page",
							BluetoothLeService.LOSS_LINK_ALARM_DISCONNECTION);
					i.putExtra("mac", mac);
					i.setAction(Intent.ACTION_MAIN);
					i.addCategory(Intent.CATEGORY_LAUNCHER);
					startActivity(i);
					try {
						Thread.sleep(500);
					} catch (Throwable e) {
					}
					broadcastUpdate(LOSS_LINK_ALARM, gatt.getDevice()
							.getAddress(), LOSS_LINK_ALARM_DISCONNECTION);
				}
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
			} else {
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
		}
	};

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action, final String address,
			final String status) {
		final Intent intent = new Intent(action);
		intent.putExtra("address", address);
		intent.putExtra("status", status);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action,
			final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);

		// This is special handling for the Heart Rate Measurement profile. Data
		// parsing is
		// carried out as per profile specifications:
		// http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
		if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
			int flag = characteristic.getProperties();
			int format = -1;
			if ((flag & 0x01) != 0) {
				format = BluetoothGattCharacteristic.FORMAT_UINT16;
			} else {
				format = BluetoothGattCharacteristic.FORMAT_UINT8;
			}
			final int heartRate = characteristic.getIntValue(format, 1);
			intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
		} else {
			// For all other profiles, writes the data formatted in HEX.
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(
						data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));
				intent.putExtra(EXTRA_DATA, new String(data) + "\n"
						+ stringBuilder.toString());
			}
		}
		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {
		BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (!bStart) {
			h.post(rReconnectThread);
			bStart = true;
			PowerManager pm = (PowerManager) getBaseContext().getSystemService(
					Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, TAG);
			wl.acquire();
		}
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that
		// BluetoothGatt.close() is called
		// such that resources are cleaned up properly. In this particular
		// example, close() is
		// invoked when the UI is disconnected from the Service.

		// h.removeCallbacks(rReconnectThread);
		// close();
		try {
			wl.release();
			return super.onUnbind(intent);
		} catch(Throwable e) {
			return false;
		}
	}

	static boolean bStart = false;
	PowerManager.WakeLock wl;

	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!bStart) {
			h.post(rReconnectThread);
			bStart = true;
			PowerManager pm = (PowerManager) getBaseContext().getSystemService(
					Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, TAG);
			wl.acquire();
		}
		bCallBackBlock = false;
		return Service.START_STICKY;
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			return false;
		}

		return true;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	boolean checkGattExist(final String address) {
		for (BluetoothGatt conn : mBluetoothGatts) {
			if (address.equals(conn.getDevice().getAddress())) {
				return true;
			}
		}
		return false;
	}

	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			return false;
		}
		// Previously connected device. Try to reconnect.
		// mBluetoothGatt object will be removed from array list.
		/*
		 * if (mBluetoothDeviceAddress != null &&
		 * address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
		 * //Log.d(TAG,
		 * "Trying to use an existing mBluetoothGatt for connection."); if
		 * (mBluetoothGatt.connect()) { mConnectionState = STATE_CONNECTING;
		 * return true; } else { return false; } }
		 */

		for (BluetoothGatt conn : mBluetoothGatts) {
			if (address.equals(conn.getDevice().getAddress())) {
				if (conn.connect()) {
					mConnectionState = STATE_CONNECTING;
					return true;
				}
			}
		}
		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			return false;
		}
		BluetoothGatt gattConn = device.connectGatt(this, false, mGattCallback);
		mBluetoothGatts.add(gattConn);
		if (gattConn.connect()) {
			mConnectionState = STATE_CONNECTING;
			return true;
		}
		return false;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	void removeConn(BluetoothGatt conn) {
		mBluetoothGatts.remove(conn);
		conn.disconnect();
		conn.close();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		removeAll();
	}
	
	public void removeAll() {
		bCallBackBlock = true;
		for (BluetoothGatt conn : mBluetoothGatts) {
			conn.disconnect();
			conn.close();
		}
		mBluetoothGatts.clear();
	}

	public void disconnect(final String address) {
		BluetoothGatt targetConn = null;
		for (BluetoothGatt conn : mBluetoothGatts) {
			if (address.equals(conn.getDevice().getAddress())) {
				targetConn = conn;
				break;
			}
		}
		if (null != targetConn) {
			removeConn(targetConn);
		}
		// mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		removeAll();
		/*
		 * if (mBluetoothGatt == null) { return; } mBluetoothGatt.close();
		 * mBluetoothGatt = null;
		 */
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	BluetoothGatt getConn(final String address) {
		BluetoothGatt result = null;
		for (BluetoothGatt conn : mBluetoothGatts) {
			if (address.equals(conn.getDevice().getAddress())) {
				result = conn;
			}
		}
		return result;
	}

	public void readCharacteristic(final String address,
			BluetoothGattCharacteristic characteristic) {
		BluetoothGatt conn = getConn(address);
		if (mBluetoothAdapter == null || conn == null) {
			return;
		}
		conn.readCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 * 
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public void setCharacteristicNotification(final String address,
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		BluetoothGatt conn = getConn(address);
		if (mBluetoothAdapter == null || conn == null) {
			return;
		}
		conn.setCharacteristicNotification(characteristic, enabled);

		// This is specific to Heart Rate Measurement.
		if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
			BluetoothGattDescriptor descriptor = characteristic
					.getDescriptor(UUID
							.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			conn.writeDescriptor(descriptor);
		}
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 * 
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices(
			final String address) {
		BluetoothGatt conn = getConn(address);
		if (conn == null)
			return null;

		return conn.getServices();
	}

	public ArrayList<BluetoothDevice> getConnectedDevices() {
		if (0 < mBluetoothGatts.size()) {
			ArrayList<BluetoothDevice> result;
			List<BluetoothDevice> devs = mBluetoothManager
					.getConnectedDevices(BluetoothGatt.GATT);
			result = new ArrayList<BluetoothDevice>(devs);
			return result;
		}
		return null;
	}

	/**
	 * Retrieve characteristic tree GATT services GATT characteristics
	 * */
	// service-characteristic for buzzer
	static final String IMMEDIATE_ALERT_SERVICE = "00001802-0000-1000-8000-00805f9b34fb";
	static final String IMMEDIATE_ALERT_CHARACTERISTIC = "00002a06-0000-1000-8000-00805f9b34fb";
	// service-characteristic for finder
	static final String LINK_LOSS_SERVICE = "00001803-0000-1000-8000-00805f9b34fb";
	static final String LINK_LOSS_CHARACTERISTIC = "00002a06-0000-1000-8000-00805f9b34fb";

	public boolean checkDeviceServiceReady(final String address) {
		BluetoothGatt conn = getConn(address);
		if(null != conn) {
			BluetoothGattService service =
					conn.getService(UUID.fromString(IMMEDIATE_ALERT_SERVICE));
			if(null != service) {
				return true;
			}
		}
		return false;
	}
	
	public void setBuzzerOnOff(final String address, boolean b) {
		BluetoothGatt conn = getConn(address);
		if (null != conn) {
			byte[] buf = new byte[] { (byte) ((b) ? 0x02 : 0x00), 0x00 };
			int err = 0;
			while (err < 3) {
				err++;
				BluetoothGattService service =
						conn.getService(UUID.fromString(IMMEDIATE_ALERT_SERVICE));
				if (null == service) {
					try {
						SystemClock.sleep(300);
					} catch (Throwable e) {
					}
					continue;
				}

				BluetoothGattCharacteristic cBuzzer = service
						.getCharacteristic(UUID
								.fromString(IMMEDIATE_ALERT_CHARACTERISTIC));
				if (null == cBuzzer) {
					try {
						SystemClock.sleep(300);
					} catch (Throwable e) {
					}
					continue;
				}
				cBuzzer.setValue(buf);
				conn.writeCharacteristic(cBuzzer);
				break;
			}
		}
	}

	public boolean getBuzzerState(final String address) {
		BluetoothGatt conn = getConn(address);
		if (null != conn) {
			try {
				BluetoothGattService service = conn.getService(UUID
						.fromString(IMMEDIATE_ALERT_SERVICE));
				if (null == service) {
					return false;
				}

				BluetoothGattCharacteristic cBuzzer = service
						.getCharacteristic(UUID
								.fromString(IMMEDIATE_ALERT_CHARACTERISTIC));
				if (null == cBuzzer) {
					return false;
				}

				byte[] v = cBuzzer.getValue();
				if (null != v && 0 < v[0]) {
					return true;
				}
			} catch (NullPointerException n) {
			} catch (Throwable e) {
			}
		}
		return false;
	}

	private void setFinder(final String address, boolean enable) {
		BluetoothGatt conn = getConn(address);
		if (null != conn) {
			try {
				final byte[] bb = new byte[] { (byte) ((enable) ? 0x02 : 0x00) };
				BluetoothGattService service = conn.getService(UUID
						.fromString(LINK_LOSS_SERVICE));
				if (null == service) {
					return;
				}

				BluetoothGattCharacteristic cFinder = service
						.getCharacteristic(UUID
								.fromString(LINK_LOSS_CHARACTERISTIC));
				if (null == cFinder) {
					return;
				}

				cFinder.setValue(bb);
				conn.writeCharacteristic(cFinder);
			} catch (NullPointerException n) {
			} catch (Throwable e) {
			}
		}
	}

	public void enableFinder(final String address) {
		setFinder(address, true);
	}

	public void resetFinder(final String address) {
		// setFinder(address, false);
		removeLostDev(address);
		setFinder(address, true);
	}

	void removeLostDev(final String address) {
		BluetoothGatt target = null;
		for (BluetoothGatt dev : lostDev) {
			if (dev.getDevice().getAddress().equals(address)) {
				// lostDev.remove(dev);
				target = dev;
				break;
			}
		}
		lostDev.remove(target);
	}

	/**
	 * Lost Link List Keep all lost link try to reconnect to the device wait for
	 * resetting of the finder remove the device from lost list after reset
	 * */
	ArrayList<BluetoothGatt> lostDev = new ArrayList<BluetoothGatt>();
	Handler h = new Handler();
	Runnable rReconnectThread = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			for (BluetoothGatt dev : lostDev) {
				if (!checkDevConnected(dev.getDevice().getAddress())) {
					if (dev.connect()) {
					} else {
					}
				}
			}
			h.postDelayed(rReconnectThread, 2000);
		}

	};

	boolean checkDevConnected(final String address) {
		ArrayList<BluetoothDevice> devs = this.getConnectedDevices();
		if (null == devs) {
			return false;
		}

		for (BluetoothDevice dev : devs) {
			if (dev.getAddress().equals(address)) {
				return true;
			}
		}
		return false;
	}

	ArrayList<BluetoothDevice> getLostDevices() {
		ArrayList<BluetoothDevice> result = new ArrayList<BluetoothDevice>();
		for (BluetoothGatt dev : lostDev) {
			result.add(dev.getDevice());
		}
		return result;
	}

	boolean checkDevLost(final String address) {
		for (BluetoothDevice dev : getLostDevices()) {
			if (dev.getAddress().equals(address)) {
				return true;
			}
		}
		return false;
	}

}
