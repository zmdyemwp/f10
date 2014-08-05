package com.goldtek.rangefinder;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;


public class MainLifeCycle implements ActivityLifecycleCallbacks {

	static boolean sNowVisible = true;
	
	static public boolean isVisible() {
		return sNowVisible;
	}
	
	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityPaused(Activity activity) {
		// TODO Auto-generated method stub
		if(activity.getClass().toString().contains("RangerFLink")) {
			sNowVisible = false;
		}
	}

	@Override
	public void onActivityResumed(Activity activity) {
		// TODO Auto-generated method stub
		if(activity.getClass().toString().contains("RangerFLink")) {
			sNowVisible = true;
		}
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityStarted(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityStopped(Activity activity) {
		// TODO Auto-generated method stub

	}

}
