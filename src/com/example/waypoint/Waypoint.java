package com.example.waypoint;

import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Waypoint extends OverlayItem{
	private boolean visited = false;
	
	public Waypoint(GeoPoint arg0, String arg1, String arg2) {
		super(arg0, arg1, arg2);
	}
	
	public Waypoint(GeoPoint arg0, String arg1, String arg2,boolean marked) {
		super(arg0, arg1, arg2);
		visited = marked;
	}
	
	public Waypoint markVisited(boolean mark){
		Log.i("MARK",""+mark);
		this.visited = mark;
		return this;
	}
	
	public boolean isVisited(){
		return visited;
	}

	
	
	
}
