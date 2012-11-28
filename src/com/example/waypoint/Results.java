package com.example.waypoint;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

public class Results extends ListActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        
        //Show a progress bar while we load the results
        /*
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);
        
        getListView().setEmptyView(progressBar);
        
        //Add progress bar to layout root
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);
        */
        
        //Make the stuff that will go into the items in the list.
        String[] waypointText = WaypointItemizedOverlay.getStringArray();
        
        this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,waypointText));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_results, menu);
        return true;
    }

	
}
