package com.example.waypoint;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

public class Results extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

	//Displays list data
	SimpleCursorAdapter adapter;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        
        //Show a progress bar while we load the results
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);
        
        //Add progress bar to layout root
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);
        
        //Make the stuff that will go into the items in the list.
        String[] waypointText = WaypointItemizedOverlay.getStringArray();
        int[] toViews = {android.R.id.text1};
        
        //Empty adapter to display, null at first then update onLoadFinished
        adapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,null,waypointText,toViews,0);
        setListAdapter(adapter);
        
        getLoaderManager().initLoader(0, null, this);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_results, menu);
        return true;
    }

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return new CursorLoader(this);
	}

	public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
		// TODO Auto-generated method stub
		adapter.swapCursor(data);
		
	}

	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		adapter.swapCursor(null);
		
	}
}
