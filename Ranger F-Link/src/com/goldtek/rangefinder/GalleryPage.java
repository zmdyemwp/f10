package com.goldtek.rangefinder;

import com.goldtek.rangefinder.RangerFLink.ItemDetail;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

public class GalleryPage extends Fragment {
	public static final String tag = "GalleryPage";
	private int index = -1;
	
	public GalleryPage(int i) {
		index = i;
	}
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((RangerFLink)activity).setCurrentFragment(this);
	}
	
	GalleryAdapter ga = null;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = null;
		try {
			Context c = getActivity();
			v = inflater.inflate(R.layout.activity_grid_view, container, false);
			GridView gv = (GridView)v.findViewById(R.id.the_view);
			gv.setOnItemClickListener(onClick);
			ga = new GalleryAdapter(c);
			gv.setAdapter(ga);
		} catch(Throwable e) {
			Log.d(tag, e.getLocalizedMessage());
		}
		return v;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	AbsListView.OnItemClickListener onClick =
			new AbsListView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Log.d(tag, String.format("onItemClick(%d)", arg2));
					
					ga.StopLoading();
					if(index >= 0) {
						ItemDetail i = RangerFLink.finders.get(index);
						i.SetThumbnail((Bitmap)ga.getItem(arg2));
						i.SetImage(ga.getBitmapUri(arg2).toString());
						
					}
					// TODO Auto-generated method stub
					FragmentManager fm = getActivity().getFragmentManager();
					fm.popBackStack();
				}
			};
}
