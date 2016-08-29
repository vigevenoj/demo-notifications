package com.sharkbaitextraordinaire.bootnotifier.model;

import org.geojson.Point;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationUpdate implements MonitorableLocation {

	String _type;
	@JsonProperty("lat")
	double latitude;
	@JsonProperty("lon")
	double longitude;
	@JsonProperty("acc")
	String accuracy;
	@JsonProperty("batt")
	String battery;
	@JsonProperty("tst")
	Long timestamp;
	String event;
	String name; // Name is set outside of the constructor. See OwntracksMqttClient for an example

	public LocationUpdate() {
	}

	public LocationUpdate(String _type, double lat, double lon, String acc, String batt, Long tst) {
		this._type = _type;
		this.latitude = lat;
		this.longitude = lon;
		this.accuracy = acc;
		this.battery = batt;
		this.timestamp = tst;
	}

	public LocationUpdate(String _type, double lat, double lon, String acc, String batt, Long tst, String event) {
		this._type = _type;
		this.latitude = lat;
		this.longitude = lon;
		this.accuracy = acc;
		this.battery = batt;
		this.timestamp = tst;
		this.event = event;
	}

	public String get_type() {
		return _type;
	}

	public void set_type(String _type) {
		this._type = _type;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}

	public String getBattery() {
		return battery;
	}

	public void setBattery(String battery) {
		this.battery = battery;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	@Override
	public String toString() {
		return "LocationUpdate [name=" + name + " _type=" + _type + ", latitude=" + latitude + ", longitude=" + longitude + ", accuracy="
				+ accuracy + ", battery=" + battery + ", timestamp=" + timestamp + ", event=" + event + "]";
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Point getLocation() {
		return new Point(longitude, latitude);
	}
	
}
