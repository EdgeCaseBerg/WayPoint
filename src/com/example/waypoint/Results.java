package com.example.waypoint;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class Results extends ListActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        
        Button newResult = (Button)findViewById(R.id.btnNewRun);
        newResult.setVisibility(View.VISIBLE);
       
        newResult.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0){
        		//Set the result to be done with so we can go on back!
        		Intent mapIntent = new Intent(Results.this, MainActivity.class);
        		mapIntent.putExtra("result", true);
        		setResult(RESULT_OK,mapIntent);
        		finish();
        		
        	}
        });
        
        ListView theList = (ListView)findViewById(android.R.id.list);
        theList.addFooterView(newResult);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_results, menu);
        return true;
    }

	
}
