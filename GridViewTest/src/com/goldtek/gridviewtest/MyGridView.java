package com.goldtek.gridviewtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.GridView;

public class MyGridView extends Activity {

	public GridView gv;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PhotoAdapter pa = new PhotoAdapter(this);
        pa.RefreshFiles();
        pa.ShowAll();
        gv = (GridView)findViewById(R.id.gridView1); 
        gv.setAdapter(pa);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
