package com.sharkbaitextraordinaire.bootnotifier.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.sharkbaitextraordinaire.bootnotifier.model.MonitoredLocation;

@Component
public class MonitoredLocationDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<MonitoredLocation> findAll() {
		String sql = "select name, longitude, latitude from monitoredlocations";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<MonitoredLocation>(MonitoredLocation.class));
	}
	
	public MonitoredLocation findByName(String name) {
		String sql = "select name, longitude, latitude from monitoredlocations where name = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] { name }, new BeanPropertyRowMapper<MonitoredLocation>(MonitoredLocation.class));
	}
	
	public void insert(MonitoredLocation location) {
		String sql = "insert into monitoredlocations "
				+ "(name, longitude, latitude) "
				+ "values "
				+ "(?, ?, ?)";
		jdbcTemplate.update(sql, new Object[] { 
				location.getName(), location.getLongitude(), location.getLatitude() 
				});
				
	}
}
