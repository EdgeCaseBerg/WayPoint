package com.example.waypoint;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class WaypointItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private Context mContext;
	
	private OverlayItem dragItem = null;
	private OverlayItem removeItem = null;
	private ImageView dragImage=null;
    private int xDragImageOffset=0;
    private int yDragImageOffset=0;
    private int xDragTouchOffset=0;
    private int yDragTouchOffset=0;
    private Drawable marker=null;
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	public WaypointItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		
		this.marker = defaultMarker;
		
		//mOverlays.add(new OverlayItem(getPoint(44.4758,73.2125),"VT", "Burlington"));
		      
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
	protected OverlayItem createItem(int i) {
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
        	
            for (OverlayItem item : mOverlays) {
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
                  
                  showDeleteDialog();
                  
                  break;
                }
            }
           
        }
        else if(action==MotionEvent.ACTION_MOVE && dragItem!=null){
        	//Are we dragging a marker or dragging the map?
            setDragImagePosition(x,y);
        	result = true;
        }
        else if(action==MotionEvent.ACTION_UP && dragItem!=null){
        	dragImage.setVisibility(View.GONE);
            
            GeoPoint pt=map.getProjection().fromPixels(x-xDragTouchOffset,
                                                       y-yDragTouchOffset);
            OverlayItem toDrop=new OverlayItem(pt, dragItem.getTitle(),
                                               dragItem.getSnippet());
            Log.i("DROP","Dropping waypoint down");
            //Interesting little hack to make the removes work.
            removeItem = toDrop;
            mOverlays.add(toDrop);
            populate();
            
            dragItem=null;
            result=true;
        }else if(action==MotionEvent.ACTION_UP && dragItem==null){
        	showCreateDialog(x,y,map);
        }
        return (result || super.onTouchEvent(event,map));
    }
    
    //Yoink! ( https://github.com/commonsguy/cw-advandroid/blob/master/Maps/NooYawkTouch/src/com/commonsware/android/maptouch/NooYawk.java )
    private void setDragImagePosition(int x, int y) {
        RelativeLayout.LayoutParams lp=
          (RelativeLayout.LayoutParams)dragImage.getLayoutParams();
              
        lp.setMargins(x-xDragImageOffset-xDragTouchOffset,y-yDragImageOffset-yDragTouchOffset, 0, 0);
        dragImage.setLayoutParams(lp);
      }
	
	public void addOverlay(OverlayItem overlay) {
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
                	   mOverlays.add(new OverlayItem(p,"","" ));
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
                		   Log.i("DELETE", "Trying to remove remoteItem");
                		   Log.i("DELETEINT",""+mOverlays.indexOf(removeItem));
                		   Log.i("DELETEBOOL",""+mOverlays.remove(removeItem));
                	   
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

}
