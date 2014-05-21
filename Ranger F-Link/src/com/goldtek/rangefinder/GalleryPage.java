package com.goldtek.rangefinder;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.goldtek.rangefinder.RangerFLink.ItemDetail;

public class GalleryPage extends Fragment {

	private static final String tag = "GalleryPage";
	
	public static GalleryPage newInstance(int i) {
		GalleryPage f = new GalleryPage();
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
			//Log.d(tag, e.getLocalizedMessage());
		}
		return v;
	}
	
	AbsListView.OnItemClickListener onClick =
			new AbsListView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if(getIndex() >= 0) {
						ItemDetail i = RangerFLink.finders.get(getIndex());
						i.SetThumbnail(ga.createBitmap(arg2));
						i.SetImage(ga.getUriString(arg2));
					}
					FragmentManager fm = getActivity().getFragmentManager();
					fm.popBackStack();
				}
	};
}
