package com.sharkbaitextraordinaire.bootnotifier.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.sharkbaitextraordinaire.bootnotifier.model.LocationUpdate;

@Component
public class LocationUpdateDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	private static final Logger logger = LoggerFactory.getLogger(LocationUpdateDAO.class);
	
	public List<LocationUpdate> findAll() {
		String sql = "select _type, lat, lon, acc, tst, batt, event from locationupdates order by tst desc";
		return jdbcTemplate.query(sql, new LocationUpdateRowMapper());
	}
	
	public LocationUpdate findLatest() {
		String sql = "select _type, lat, lon, acc, tst, batt, event from locationupdates order by tst desc limit 1";
		return jdbcTemplate.queryForObject(sql, new LocationUpdateRowMapper());
	}
	
	public void insert(LocationUpdate location) {
		String sql = "insert into locationupdates "
				+ "(_type, lat, lon, acc, tst, batt, event) "
				+ "values "
				+ "(?, ?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, new Object[] {location.get_type(), 
				location.getLatitude(), location.getLongitude(),
				location.getAccuracy(), location.getTimestamp(),
				location.getBattery(), location.getEvent() });
	}
}
