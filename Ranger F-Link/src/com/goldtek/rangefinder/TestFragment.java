package com.goldtek.rangefinder;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TestFragment extends Fragment {
	static private final String tag = "TestFragment";
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		Log.d(tag, "onCreateView");
		View result = inflater.inflate(R.layout.bottom_buttons, container, false);
		result.findViewById(R.id.button1).setOnClickListener(switch2Main);
		return result;
	}
	
	View.OnClickListener switch2Main = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d(tag, "onClick::TEST");
		}
	};
}
