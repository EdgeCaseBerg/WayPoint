package com.example.waypoint;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class WaypointItemizedOverlay extends ItemizedOverlay<Waypoint> {
	private Context mContext;
	
	//Start time for our run:
	public static long runStartTime = -1;
	
	//Dragging markers and removing items	
	private Waypoint dragItem = null;
	private Waypoint removeItem = null;
	private ImageView dragImage=null;
    private int xDragImageOffset=0;
    private int yDragImageOffset=0;
    private int xDragTouchOffset=0;
    private int yDragTouchOffset=0;
    private Drawable marker=null;
    
    //Stuff for Tapping
    private long startEvent = 0;
    private long endEvent = 1;
    private final long TAP_TIME = 250;
    
    //Stuff for long pressing:
    private int longClickMin = 1000;
    private long startTimeLongClick = 0;
    //Coordinates for long click
    private float xLongClick;
    private float yLongClick;
    //Tolerance variables (pixel box);
    private float tolerance = 10;
    //Box variables for seeing if we fell off
    private float xLow;
    private float xHigh;
    private float yLow;
    private float yHigh;
    
    
    
	public static ArrayList<Waypoint> mOverlays = new ArrayList<Waypoint>();
	
	public WaypointItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		
		this.marker = defaultMarker;
		
		//mOverlays.add(new OverlayItem(getPoint(44.4758,73.2125),"VT", "Burlington"));
		      
		populate();
		
	}
	
	public ArrayList<Waypoint> getOverlays(){
		return mOverlays;
	}
	
	public void setList(ArrayList<Waypoint> overs){
		mOverlays = overs;
		populate();
	}
	
	private GeoPoint getPoint(double lat, double lon) {
        return(new GeoPoint((int)(lat*1000000.0),(int)(lon*1000000.0)));
    }
	
	public WaypointItemizedOverlay(Drawable defaultMarker, Context context) {
		  super(boundCenterBottom(defaultMarker));
		  mContext = context;
		 
		  //Make the imageview from scratch because the R file refuses to work.
		  dragImage= new ImageView(context);  
		  RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				    RelativeLayout.LayoutParams.WRAP_CONTENT,
				    RelativeLayout.LayoutParams.WRAP_CONTENT);
		  dragImage.setLayoutParams(lp);
		  dragImage.setImageDrawable(defaultMarker);
		  dragImage.setImageResource(R.drawable.androidmarker);
		  
		 	  
		  xDragImageOffset=dragImage.getDrawable().getIntrinsicWidth()/2;
		  yDragImageOffset=dragImage.getDrawable().getIntrinsicHeight();
		  this.marker = defaultMarker;
			
	      
		  //mOverlays.add(new OverlayItem(getPoint(40.748963847316034,-73.96807193756104),"UN", "United Nations"));
		      
		  populate();    
	}


	@Override
	protected Waypoint createItem(int i) {
	  return mOverlays.get(i);
	}
	
	@Override
	public int size() {
	  return mOverlays.size();
	}
	
 
    public boolean onTouchEvent(MotionEvent event,MapView map) {
    	//Get information about the action
        final int action=event.getAction();
        final int x=(int)event.getX();
        final int y=(int)event.getY();
        //To be returned:
        boolean result=false;
        
        
        
        if (action==MotionEvent.ACTION_DOWN ) {
        	startEvent = event.getEventTime ();
        	startTimeLongClick = event.getEventTime();
        	xLongClick = x;
        	yLongClick = y;
            Log.i("EVENT TIME START",""+startEvent);
            
            for (Waypoint item : mOverlays) {
              Point p=new Point(0,0);
              
              //This places a point into p that is actually useful
              map.getProjection().toPixels(item.getPoint(), p);
              
              //Are we touching this item?
              if (hitTest(item, marker, x-p.x, y-p.y)) {
                  result=true;
                  dragItem =item;
                  removeItem = item;
                  mOverlays.remove(dragItem);
                  populate();

                  xDragTouchOffset=0;
                  yDragTouchOffset=0;
                  
                  setDragImagePosition(p.x, p.y);
                  dragImage.setVisibility(View.VISIBLE);

                  xDragTouchOffset=x-p.x;
                  yDragTouchOffset=y-p.y;
                  
                  break;
                }
            }
           
        }
        else if(action==MotionEvent.ACTION_MOVE && dragItem!=null){
        	//Are we dragging a marker or dragging the map?
            setDragImagePosition(x,y);
        	result = true;
        	if(event.getPointerCount()>1){
        		//This will not match downtime, no long click
        		startTimeLongClick = 0;
        	}else{
        		xLow = xLongClick - tolerance;
        		xHigh= xLongClick + tolerance;
        		yLow = yLongClick - tolerance;
        		yHigh= yLongClick + tolerance;
        		if( (x < xLow || x > xHigh) ||  (y < yLow || y > yHigh)  ){
        			//We've moved too much
        			startTimeLongClick = 0;
        		}
        	}
        }
        else if(action==MotionEvent.ACTION_UP && dragItem!=null){
        	dragImage.setVisibility(View.GONE);
            
            GeoPoint pt=map.getProjection().fromPixels(x-xDragTouchOffset,
                                                       y-yDragTouchOffset);
            Waypoint toDrop=new Waypoint(pt, dragItem.getTitle(),
                                               dragItem.getSnippet(),dragItem.isVisited()).setStamp(dragItem.getStamp());
            Log.i("DROP","Dropping waypoint down");
            //Interesting little hack to make the removes work.
            removeItem = toDrop;
            mOverlays.add(toDrop);
            populate();
            dragItem=null;
            result=true;
            endEvent = event.getEventTime();
            Log.i("END TIME DELETE", ""+endEvent);
            if(endEvent - startEvent < TAP_TIME){
            	//Toast.makeText(mContext, "Visited: " + removeItem.isVisited(), Toast.LENGTH_LONG).show();
            	showDeleteDialog();
            }
            
            
        }else if(action==MotionEvent.ACTION_UP && dragItem==null){
        	endEvent = event.getEventTime();
        	long downTime = event.getDownTime();
        	if(startTimeLongClick == downTime){
        		if((endEvent - startTimeLongClick) > longClickMin){
        			xLow = xLongClick - tolerance;
            		xHigh= xLongClick + tolerance;
            		yLow = yLongClick - tolerance;
            		yHigh= yLongClick + tolerance;
            		if (!( (x < xLow || x > xHigh) ||  (y < yLow || y > yHigh)  )){
            			//Reset waypoints
            			for(Waypoint item : mOverlays){
            				item.markVisited(false);
            			}
            			//Set the start time, a long press means we are starting out run!
            			runStartTime = System.currentTimeMillis();
            			Toast.makeText(mContext, "Starting Run", Toast.LENGTH_LONG).show();
            		}
        		}
        	}
            if(endEvent - startEvent < TAP_TIME){
            	showCreateDialog(x,y,map);
            }
        }
        populate();
        map.invalidate();
        return (result || super.onTouchEvent(event,map));
    }
    
    //Yoink! ( https://github.com/commonsguy/cw-advandroid/blob/master/Maps/NooYawkTouch/src/com/commonsware/android/maptouch/NooYawk.java )
    private void setDragImagePosition(int x, int y) {
        RelativeLayout.LayoutParams lp=
          (RelativeLayout.LayoutParams)dragImage.getLayoutParams();
              
        lp.setMargins(x-xDragImageOffset-xDragTouchOffset,y-yDragImageOffset-yDragTouchOffset, 0, 0);
        dragImage.setLayoutParams(lp);
      }
	
	public void addOverlay(Waypoint overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	private void showCreateDialog(final int x, final int y,final MapView map){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Create a Waypoint?")
               .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   Log.i("CREATE", "Creating new Waypoint");
                	   GeoPoint p=map.getProjection().fromPixels(x, y);
                	   Log.i("GEO", "LA "+p.getLatitudeE6()+" LO "+p.getLongitudeE6());
                	   mOverlays.add(new Waypoint(p,"","" ));
                	   populate();
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog do nothing
                   }
               });
        // Create the AlertDialog object and return it
        AlertDialog dia = builder.create();
        dia.show();
	}
	
	private void showDeleteDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Delete Waypoint?")
               .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   if(removeItem!=null){
                		   mOverlays.remove(removeItem);
                		   populate();
                		   
                		   removeItem= null;
                	   }
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog do nothing
                	   removeItem= null;
                   }
               });
        // Create the AlertDialog object and return it
        AlertDialog dia = builder.create();
        dia.show();
	}

	static public String [] getStringArray(){
		String[] arr = new String[mOverlays.size()];
		int i = 0;
		for(Waypoint item : mOverlays){
			arr[i] = item.toString();
			i++;
		}
		return arr;
	}
	
}
