package com.sharkbaitextraordinaire.bootnotifier.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.sharkbaitextraordinaire.bootnotifier.model.Earthquake;

@Repository
public class EarthquakeDAO {

	@Autowired
    private JdbcTemplate jdbcTemplate;
	private static final Logger logger = LoggerFactory.getLogger(EarthquakeDAO.class);
	
	public List<Earthquake> findAllEarthQuakes() {
		logger.debug("querying for earthquakes");
		String sql = "select magnitude, place, earthquaketime, updatetime, tz, url, detail, felt, cdi, tsunami, sig, code, ids, type, title, id, longitude, latitude from earthquakes order by earthquaketime desc";
		List<Earthquake> earthquakes = jdbcTemplate.query(sql, new EarthquakeRowMapper());
		return earthquakes;
	}
	
	public Earthquake findById(String id) {
		String sql = "select magnitude, place, earthquaketime, updatetime, tz, url, detail, felt, cdi, tsunami, sig, code, ids, type, title, id, longitude, latitude from earthquakes where id = ?";
		try {
			return jdbcTemplate.queryForObject(sql, new Object[] { id }, new EarthquakeRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public void insert(Earthquake earthquake) {
		String sql = "insert into earthquakes "
				+ "(magnitude, place, earthquaketime, updatetime, tz, url, detail, felt, cdi, tsunami, sig, code, ids, type, title, id, longitude, latitude)"
				+ " values "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, new Object[] { earthquake.getMagnitude(), earthquake.getPlace(), earthquake.getEarthquaketime(),
				earthquake.getUpdate(), earthquake.getTz(), earthquake.getUrl(), earthquake.getDetail(), earthquake.getFelt(),
				earthquake.getCdi(), earthquake.getTsunami(), earthquake.getSig(), earthquake.getCode(), earthquake.getIds(),
				earthquake.getType(), earthquake.getTitle(), earthquake.getId(), earthquake.getLocation().getCoordinates().getLongitude(), 
				earthquake.getLocation().getCoordinates().getLatitude()} );
	}
	
}
