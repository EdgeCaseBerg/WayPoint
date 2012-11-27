package com.example.waypoint;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Set up the map
        map = (MapView) findViewById(R.id.mapview);
        map.getController().setCenter(getPoint(44.479104,-73.197972));
        map.getController().setZoom(17);
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
		locationListener  = new MyLocationListener(itemizedoverlay);
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
	private WaypointItemizedOverlay iOverlay;
	
	public MyLocationListener(WaypointItemizedOverlay overlayitems){
		iOverlay = overlayitems;
	}
	
    public void onLocationChanged(Location loc) {
        if (loc != null) {
            Toast.makeText(getBaseContext(), 
                "Location changed : Lat: " + loc.getLatitude() + 
                " Lng: " + loc.getLongitude(), 
                Toast.LENGTH_SHORT).show();
            //Make the point
            GeoPoint gp = getPoint(loc.getLatitude(),loc.getLongitude());
            
            //Find out if we're close enough to a marker 
        }
    }

    public boolean comparePoints(GeoPoint fst, GeoPoint snd){
    	//If we're close enough to the other point then yes.
    	//44.478989 -73.198015 is on one side of votey and
    	//44.479104 -73.197972 is on the other
    	//44.479027 -73.197714 is next to that points
    	//So the euclidian distnace between them is approx 270, so I'll round to 300 being close!
    	double dist = Math.sqrt(Math.pow(fst.getLatitudeE6() -snd.getLatitudeE6(),2) + Math.pow(fst.getLongitudeE6() - snd.getLongitudeE6(),2));
    	
    	return dist < 300 ? true : false;
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
