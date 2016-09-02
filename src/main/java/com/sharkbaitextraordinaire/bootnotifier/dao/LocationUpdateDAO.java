package com.sharkbaitextraordinaire.bootnotifier.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.sharkbaitextraordinaire.bootnotifier.model.LocationUpdate;

@Repository
public class LocationUpdateDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<LocationUpdate> findAll() {
		String sql = "select _type, lat, lon, acc, tst, batt, event, name from locationupdates order by tst desc";
		return jdbcTemplate.query(sql, new LocationUpdateRowMapper());
	}
	
	public List<LocationUpdate> findAllForUser(String name) {
		String sql = "select _type, lat, lon, acc, tst, batt, event, name from locationupdates where name = ? order by tst desc";
		return jdbcTemplate.query(sql, new Object[] { name }, new LocationUpdateRowMapper());
	}
	
	public LocationUpdate findLatest() {
		String sql = "select _type, lat, lon, acc, tst, batt, event, name from locationupdates order by tst desc limit 1";
		return jdbcTemplate.queryForObject(sql, new LocationUpdateRowMapper());
	}
	
	public LocationUpdate latestForUser(String name) {
		String sql = "select _type, lat, lon, acc, tst, batt, event, name from locationupdates where name = ? order by tst desc limit 1";
		return jdbcTemplate.queryForObject(sql, new Object[] { name }, new LocationUpdateRowMapper());
	}
	
	public void insert(LocationUpdate location) {
		String sql = "insert into locationupdates "
				+ "(_type, lat, lon, acc, tst, batt, event, name) "
				+ "values "
				+ "(?, ?, ?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, new Object[] {location.get_type(), 
				location.getLatitude(), location.getLongitude(),
				location.getAccuracy(), location.getTimestamp(),
				location.getBattery(), location.getEvent(), location.getName() });
	}
}
