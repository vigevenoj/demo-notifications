package com.sharkbaitextraordinaire.bootnotifier.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.sharkbaitextraordinaire.bootnotifier.model.LocationUpdate;

public class LocationUpdateRowMapper implements RowMapper<LocationUpdate> {

	@Override
	public LocationUpdate mapRow(ResultSet rs, int rowNum) throws SQLException {
		LocationUpdate location = new LocationUpdate();
		location.set_type(rs.getString("_type"));
		location.setLatitude(rs.getDouble("lat"));
		location.setLongitude(rs.getDouble("lon"));
		location.setAccuracy(rs.getString("acc"));
		location.setBattery(rs.getString("batt"));
		location.setTimestamp(rs.getLong("tst"));
		location.setEvent(rs.getString("event"));
		location.setName(rs.getString("name"));
		return location;
	}

}
