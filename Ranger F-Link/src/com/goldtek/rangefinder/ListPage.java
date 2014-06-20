package com.goldtek.rangefinder;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ListPage extends Fragment {
	
	public static final String tag = "ListPage";
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((RangerFLink)activity).setCurrentFragment(this);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.activity_list_view, container, false);
		ListView lv = (ListView)v.findViewById(R.id.the_view);
		lv.setAdapter(new MainListViewAdapter(this.getActivity()));
		lv.setOnItemClickListener(onClick);
		lv.setOnItemLongClickListener(onLongClick);
		return v;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	void Refresh() {
		((BaseAdapter) ((ListView)this.getView()
				.findViewById(R.id.the_view))
				.getAdapter())
				.notifyDataSetChanged();
	}
	
	AbsListView.OnItemClickListener onClick =
			new AbsListView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					//Log.d("ListPage", String.format("onItemClick(%d)", arg2));
					// TODO Auto-generated method stub
					FragmentManager fm = getActivity().getFragmentManager();
					FragmentTransaction tran = fm.beginTransaction();
					String address = MainListViewAdapter.listDevices.get(arg2).getAddress();
					tran.replace(R.id.fragment1, ItemDetailPage.newInstance(address));
					tran.addToBackStack(null);
					tran.commit();
				}
			};
	AdapterView.OnItemLongClickListener onLongClick =
			new AdapterView.OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					//Log.d(tag, "Item Long Pressed: "+position);
					((RangerFLink)getActivity())
						.disconnectBleDevice(MainListViewAdapter.listDevices.get(position).getAddress());
					Refresh();
					return false;
				}
			};
}
