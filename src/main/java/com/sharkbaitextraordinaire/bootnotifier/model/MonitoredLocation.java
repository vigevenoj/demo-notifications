package com.sharkbaitextraordinaire.bootnotifier.model;

import org.geojson.Point;

public class MonitoredLocation implements MonitorableLocation {

	private Double latitude;
	private Double longitude;
	private String name;
	
	public MonitoredLocation() { }
	
	public MonitoredLocation(String name, Double longitude, Double latitude) {
		this.name = name;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public MonitoredLocation(Double longitude, Double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.name = "";
	}
	
	public Double getLatitude() {
		return this.latitude;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public Double getLongitude(){ 
		return this.longitude;
	}
	
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Point getLocation() {
		return new Point(longitude, latitude);
	}
	
	@Override
	public String toString() {
		return "MonitoredLocation [latitude=" + latitude + ", longitude=" + longitude + "]";
	}
}
