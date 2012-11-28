package com.example.waypoint;

import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Waypoint extends OverlayItem{
	private boolean visited = false;
	private long stamp = -1;
	
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

	public Waypoint setStamp(long s){
		this.stamp = s;
		return this;
	}
	
	public long getStamp(){
		return stamp;
	}
	
	public int compareTo(Waypoint other){
		if(this.stamp < other.getStamp()){
			return -1;
		}else{
			return this.stamp == other.getStamp() ? 0 : 1;
		}
	}
	
	public String toString(){
		return "WayPoint " + String.format("%d min, %d sec", 
			    TimeUnit.MILLISECONDS.toMinutes(stamp),
			    TimeUnit.MILLISECONDS.toSeconds(stamp) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(stamp))
			);
	}
	
	
}
