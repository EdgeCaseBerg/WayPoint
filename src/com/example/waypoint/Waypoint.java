package com.example.waypoint;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Waypoint extends OverlayItem{
	private boolean visited = false;
	
	public Waypoint(GeoPoint arg0, String arg1, String arg2) {
		super(arg0, arg1, arg2);
	}
	
	public void markVisited(boolean mark){
		visited = mark;
	}
	
	public boolean isVisited(){
		return visited;
	}

	
	
	
}
