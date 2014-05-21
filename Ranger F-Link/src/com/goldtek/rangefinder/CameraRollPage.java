package com.goldtek.rangefinder;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.goldtek.rangefinder.RangerFLink.ItemDetail;

public class CameraRollPage extends Fragment {
	public static final String tag = "CameraRollPage";
	
	public static CameraRollPage newInstance(int  i) {
		CameraRollPage f = new CameraRollPage();
		Bundle b = new Bundle();
		b.putInt("index", i);
		f.setArguments(b);
		return f;
	}
	
	int getIndex() {
		return this.getArguments().getInt("index");
	}
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((RangerFLink)activity).setCurrentFragment(this);
	}
	
	CameraRollAdapter ca = null;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = null;
		try {
			Context c = getActivity();
			v = inflater.inflate(R.layout.activity_grid_view, container, false);
			GridView gv = (GridView)v.findViewById(R.id.the_view);
			gv.setOnItemClickListener(onClick);
			ca = new CameraRollAdapter(c);
			gv.setAdapter(ca);
		} catch(Throwable e) {
			//Log.d(tag, e.getLocalizedMessage());
		}
		return v;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ca.StopLoading();
	}

	AbsListView.OnItemClickListener onClick =
			new AbsListView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					//Log.d(tag, String.format("onItemClick(%d)", arg2));
					
					ca.StopLoading();
					if(getIndex() >= 0) {
						ItemDetail i = RangerFLink.finders.get(getIndex());
						i.SetThumbnail((Bitmap)ca.getItem(arg2));
						i.SetImage(ca.getBitmapUri(arg2).toString());
						
					}
					// TODO Auto-generated method stub
					FragmentManager fm = getActivity().getFragmentManager();
					fm.popBackStack();
				}
			};
}
