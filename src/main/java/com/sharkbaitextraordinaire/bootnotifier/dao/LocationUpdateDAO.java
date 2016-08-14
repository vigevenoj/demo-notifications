package com.sharkbaitextraordinaire.bootnotifier.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.sharkbaitextraordinaire.bootnotifier.model.LocationUpdate;

@Component
public class LocationUpdateDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<LocationUpdate> findAll() {
		String sql = "select _type, lat, lon, acc, tst, batt, event from locationupdates order by tst asc";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<LocationUpdate>(LocationUpdate.class));
	}
	
	public LocationUpdate findLatest() {
		String sql = "select _type, lat, lon, acc, tst, batt from locationupdates order by tst desc limit 1";
		return jdbcTemplate.queryForObject(sql, LocationUpdate.class);
	}
	
	public void insert(LocationUpdate location) {
		String sql = "insert into locationupdates "
				+ "(_type, lat, lon, acc, tst, batt) "
				+ "values "
				+ "(?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, new Object[] {location.get_type(), 
				location.getLatitude(), location.getLongitude(),
				location.getAccuracy(), location.getTimestamp(),
				location.getBattery() });
	}
}
