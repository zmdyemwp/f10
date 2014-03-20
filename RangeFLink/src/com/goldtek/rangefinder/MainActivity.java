package com.goldtek.rangefinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	static final String tag = "Ranger F-Link";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
        	Log.d(tag, "yyyy");
        	FragmentManager fragmentManager = getFragmentManager();
	        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	        fragmentTransaction.add(R.id.fragment1, new MainPage()).commit();
        } catch(Throwable e) {
        	Log.d(tag, e.getLocalizedMessage());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
    	/*AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("TEst");
        ab.create().show();*/
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
    	AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage(this.getResources().getString(R.string.version));
        ab.create().show();
        return true;
    }
    
}
