package com.goldtek.rangefinder;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldtek.rangefinder.RangerFLink.ItemDetail;

public class LossLinkNotification extends Fragment {
	
	private static final String tag = "LossLinkNotification";
	
	public static LossLinkNotification newInstance(int i) {
		LossLinkNotification f = new LossLinkNotification();
		Bundle b = new Bundle();
		b.putInt("index", i);
		f.setArguments(b);
		return f;
	}
	
	private int getIndex() {
		return this.getArguments().getInt("index");
	}
	
	public static void stopAlarm() {
		try {
			if(null != mp) {
				h.removeCallbacks(r);
				mp.stop();
				mp.reset();
				mp.release();
			}
		} catch(Throwable e) {
		}
	}

	static Activity parent;
	static MediaPlayer mp;

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		parent = activity;
		((RangerFLink)activity).setCurrentFragment(this);
		/* 	Start Alarm
		mp = MediaPlayer.create(getActivity(), R.raw.beep);
		mp.setLooping(true);
		mp.start();
		h.post(r);*/
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		//	TODO: Stop Alarm
		h.removeCallbacks(r);
		mp.stop();
		mp.reset();
		mp.release();
	}
	
	static Handler h = new Handler();
	static Runnable r = new Runnable() {

		@Override
		public void run() {
			try {
				Vibrator vb = (Vibrator)parent//getActivity()
						.getSystemService(Context.VIBRATOR_SERVICE);
		    	vb.vibrate(300);
			} catch(NullPointerException n) {
				//Log.d(tag, n.getLocalizedMessage());
			} catch(Throwable e) {
				//Log.d(tag, e.getLocalizedMessage());
			}
	    	h.postDelayed(r, 500);
		}

	};
	
	ItemDetail iDev;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.loss_link_notification, container, false);
//		Device Image
		iDev = RangerFLink.finders.get(getIndex());
		ImageView iv = (ImageView)v.findViewById(R.id.dev_icon); 
		iv.setImageBitmap(iDev.getThumbnail());
		//	Device Name
		((TextView)v.findViewById(R.id.dev_name))
			.setText(RangerFLink.finders.get(getIndex()).getName());
		//	Stop Notification Button
		View stopNotification = v.findViewById(R.id.dev_stop_notification);
		stopNotification.setOnClickListener(stopNotificationClick);
		
		// TODO: Start Alarm
		mp = MediaPlayer.create(getActivity(), R.raw.beep);
		mp.setLooping(true);
		mp.start();
		h.post(r);

		return v;
	}

	View.OnClickListener stopNotificationClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			FragmentManager fmgr = fmgrClear();
			FragmentTransaction ftran = fmgr.beginTransaction();
			ftran.replace(R.id.fragment1, new MainPage());
			ftran.commit();
		}
	};

	FragmentManager fmgrClear() {
		FragmentManager fm = getFragmentManager();
		for(int i = 0; i < fm.getBackStackEntryCount();i++) {
			fm.popBackStack();
		}
		return fm;
	}
	
}
