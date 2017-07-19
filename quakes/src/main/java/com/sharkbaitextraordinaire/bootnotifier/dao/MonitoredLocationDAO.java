package com.sharkbaitextraordinaire.bootnotifier.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.sharkbaitextraordinaire.bootnotifier.model.MonitoredLocation;

@Repository
public class MonitoredLocationDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<MonitoredLocation> findAll() {
		String sql = "select name, longitude, latitude from monitoredlocations";
		return jdbcTemplate.query(sql, 
				new BeanPropertyRowMapper<MonitoredLocation>(MonitoredLocation.class));
	}
	
	public MonitoredLocation findByName(String name) {
		String sql = "select name, longitude, latitude from monitoredlocations where name = ?";
		try {
			return jdbcTemplate.queryForObject(sql, new Object[] { name }, 
					new BeanPropertyRowMapper<MonitoredLocation>(MonitoredLocation.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
		
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
	
	public void update(MonitoredLocation location) {
		String sql = "update monitoredlocations set latitude = ?, "
				+ "longitude = ? "
				+ "where name = ?";
		jdbcTemplate.update(sql, new Object[] { 
				location.getLatitude(), 
				location.getLongitude(), 
				location.getName() });
	}
}
