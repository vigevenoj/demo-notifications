package com.sharkbaitextraordinaire.bootnotifier.model;

import org.geojson.Point;

public interface MonitorableLocation {

	public String getName();
	
	public Point getLocation();
}
