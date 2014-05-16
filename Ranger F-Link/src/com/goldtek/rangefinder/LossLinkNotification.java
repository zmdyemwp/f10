package com.goldtek.rangefinder;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
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
	
	MediaPlayer mp;
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((RangerFLink)activity).setCurrentFragment(this);
		// TODO: Start Alarm
		mp = MediaPlayer.create(getActivity(), R.raw.beep);
		mp.setLooping(true);
		mp.start();
		h.post(r);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		//	TODO: Stop Alarm
		h.removeCallbacks(r);
		mp.stop();
		mp.release();
	}
	
	Handler h = new Handler();
	Runnable r = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Vibrator vb = (Vibrator)getActivity()
						.getSystemService(((Context)getActivity()).VIBRATOR_SERVICE);
		    	vb.vibrate(300);
			} catch(NullPointerException n) {
				Log.d(tag, n.getLocalizedMessage());
			} catch(Throwable e) {
				Log.d(tag, e.getLocalizedMessage());
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
		return v;
	}

	View.OnClickListener stopNotificationClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			getActivity().getFragmentManager().popBackStack();
		}
	};
	
}