package com.example.waypoint;

import java.util.List;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MainActivity extends MapActivity {
	private List<Overlay> mapOverlays;
	private List<OverlayItem> waypoints;
	private MapView map=null;
	
	private OverlayItem dragItem = null;
	private ImageView dragImage=null;
    private int xDragImageOffset=0;
    private int yDragImageOffset=0;
    private int xDragTouchOffset=0;
    private int yDragTouchOffset=0;
    private Drawable marker=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        map = (MapView) findViewById(R.id.mapview);
        map.getController().setCenter(getPoint(40.76793169992044,-73.98180484771729));
        map.getController().setZoom(17);
        map.setBuiltInZoomControls(true);

        mapOverlays = map.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        
        //Set up the marker we'll use
        this.marker = this.getResources().getDrawable(R.drawable.androidmarker);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(),marker.getIntrinsicHeight());
        
        /*WaypointItemizedOverlay itemizedoverlay = new WaypointItemizedOverlay(drawable, this);
        
        GeoPoint point = new GeoPoint(19240000,-99120000);
        OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
     
        itemizedoverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedoverlay);*/
    }
    
    private GeoPoint getPoint(double lat, double lon) {
        return(new GeoPoint((int)(lat*1000000.0),
                              (int)(lon*1000000.0)));
      }
    
    boolean hitTest(OverlayItem overlayItem, Drawable drawable, int x, int y) {
        Rect bounds = drawable.getBounds();
        int newLeft = (int) (200 * ((double)bounds.left / (double)bounds.width())  ) ;
        int newTop = (int) (200 * ((double)bounds.top  / (double)bounds.height()) );
        Rect square = new Rect(newLeft, newTop, 200, 200);
        return square.contains(x, y);
    }

 
    public boolean onTouchEvent(MotionEvent event) {
    	//Get information about the action
        final int action=event.getAction();
        final int x=(int)event.getX();
        final int y=(int)event.getY();
        //To be returned:
        boolean result=false;
        
        
        if (action==MotionEvent.ACTION_DOWN) {
        	boolean createNewItem = false;
            for (OverlayItem item : waypoints) {
              Point p=new Point(0,0);
              
              //This places a point into p that is actually useful
              map.getProjection().toPixels(item.getPoint(), p);
              
              //Are we touching this item?
              if (hitTest(item, marker, x-p.x, y-p.y)) {
                  result=true;
                  dragItem =item;
                  mapOverlays.remove(dragItem);
                 

                  xDragTouchOffset=0;
                  yDragTouchOffset=0;
                  
                  setDragImagePosition(p.x, p.y);
                  dragImage.setVisibility(View.VISIBLE);

                  xDragTouchOffset=x-p.x;
                  yDragTouchOffset=y-p.y;
                  createNewItem = true;
                  break;
                }
            }
            if(createNewItem){
            	//Put in code for us making a new item where we clicked!
            	Log.i("Create New Item True", "We want to create an item");
            }
        }
        else if(action==MotionEvent.ACTION_MOVE && dragItem!=null){
        	setDragImagePosition(x,y);
        	result = true;
        }
        else if(action==MotionEvent.ACTION_UP && dragItem!=null){
        	dragImage.setVisibility(View.GONE);
            
            GeoPoint pt=map.getProjection().fromPixels(x-xDragTouchOffset,
                                                       y-yDragTouchOffset);
            OverlayItem toDrop=new OverlayItem(pt, dragItem.getTitle(),
                                               dragItem.getSnippet());
            
            waypoints.add(toDrop);
            
            
            dragItem=null;
            result=true;
        }
        return (result || super.onTouchEvent(event));
    }
    
    //Yoink! ( https://github.com/commonsguy/cw-advandroid/blob/master/Maps/NooYawkTouch/src/com/commonsware/android/maptouch/NooYawk.java )
    private void setDragImagePosition(int x, int y) {
        RelativeLayout.LayoutParams lp=
          (RelativeLayout.LayoutParams)dragImage.getLayoutParams();
              
        lp.setMargins(x-xDragImageOffset-xDragTouchOffset,
                        y-yDragImageOffset-yDragTouchOffset, 0, 0);
        dragImage.setLayoutParams(lp);
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
}
