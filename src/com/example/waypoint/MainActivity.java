package com.example.waypoint;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MainActivity extends MapActivity {
	private List<Overlay> mapOverlays;
	private MapView map=null;
	
	//GPS STUFF
    private LocationManager lm;
    private LocationListener locationListener;
    private long CLOSENESS = 100;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Set up the map
        map = (MapView) findViewById(R.id.mapview);
        map.getController().setCenter(getPoint(44.479104,-73.197972));
        map.getController().setZoom(19);
        map.setBuiltInZoomControls(true);
        
        //Set up the marker
        mapOverlays = map.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        
        //Create the overlays
        WaypointItemizedOverlay itemizedoverlay = new WaypointItemizedOverlay(drawable, this);
        mapOverlays.add(itemizedoverlay);
        
        //MAKE THE GPS WORK
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);        
		locationListener  = new MyLocationListener(this);
	    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);        
		
        
    }
    
    private GeoPoint getPoint(double lat, double lon) {
        return(new GeoPoint((int)(lat*1000000.0),(int)(lon*1000000.0)));
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      if (keyCode == KeyEvent.KEYCODE_Z) {
        map.displayZoomControls(true);
        return(true);
      }
      
      return(super.onKeyDown(keyCode, event));
    }


private class MyLocationListener implements LocationListener  
{
	Context context;
	
	public MyLocationListener(Context c){
		context = c;
	}
	
    public void onLocationChanged(Location loc) {
        if (loc != null) {
            //Make the point
            GeoPoint gp = getPoint(loc.getLatitude(),loc.getLongitude());
            
          //Find out if we're close enough to a marker
            
            for(Waypoint item : WaypointItemizedOverlay.mOverlays){
            	if(comparePoints(gp,item.getPoint())){
            		//We're close enough! 
            		if(!item.isVisited()){
            			item.markVisited(true);
            			int index = WaypointItemizedOverlay.mOverlays.indexOf(item);
            			WaypointItemizedOverlay.mOverlays.set(index, item.markVisited(true).setStamp(System.currentTimeMillis()));
            			Toast.makeText(getBaseContext(), "Waypoint Reached", Toast.LENGTH_SHORT).show();
            			Log.i("CLOSE", "Close to a point!");
            			break;
            		}
            	}
            	Log.i("ITEMMARK",""+item.isVisited());
            }
            //Basically the end of the program I guess
            Log.i("ALL NODES",""+allNodesVisited());
            if(allNodesVisited()){
            	//Sort the nodes, subtract the start time from them all.
            	Collections.sort(WaypointItemizedOverlay.mOverlays, new Comparator<Waypoint>(){
            		public int compare(Waypoint a, Waypoint b){
            			return a.compareTo(b);
            		}
            	});
            	//Subtract the start time from each stamp to normalize it:
            	for(Waypoint item : WaypointItemizedOverlay.mOverlays){
            		item.setStamp(item.getStamp()-WaypointItemizedOverlay.runStartTime);
            		Log.i("Waypoint:",item.toString());
            	}
            	//Go go go
            	Intent result = new Intent( context ,Results.class);
            	startActivity(result);
            }
            
            
             
        }
    }
    
    public boolean allNodesVisited(){
    	boolean all = true;
    	for(Waypoint item : WaypointItemizedOverlay.mOverlays ){
    		all = all && item.isVisited();
    	}
    	return all;
    }

    public boolean comparePoints(GeoPoint fst, GeoPoint snd){
    	//If we're close enough to the other point then yes.
    	//44.478989 -73.198015 is on one side of votey and
    	//44.479104 -73.197972 is on the other
    	//44.479027 -73.197714 is next to that points
    	//So the euclidian distnace between them is approx 270, so I'll round to 300 being close!
    	double dist = Math.sqrt(Math.pow(fst.getLatitudeE6() -snd.getLatitudeE6(),2) + Math.pow(fst.getLongitudeE6() - snd.getLongitudeE6(),2));
    	Log.i("DISTANCE",""+dist);
    	return dist < CLOSENESS ? true : false;
    }
    
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
}   
}
