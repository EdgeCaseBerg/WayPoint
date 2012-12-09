package com.example.waypoint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StartPage extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        
        Button theButton = (Button)findViewById(R.id.startapp);
        
        theButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0){
        		Intent result = new Intent(StartPage.this ,MainActivity.class);
            	startActivity(result);
        	
        		
        	}
        });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_start_page, menu);
        return true;
    }
}
