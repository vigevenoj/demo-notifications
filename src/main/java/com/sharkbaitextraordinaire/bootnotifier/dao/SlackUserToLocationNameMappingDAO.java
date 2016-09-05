package com.sharkbaitextraordinaire.bootnotifier.dao;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.sharkbaitextraordinaire.bootnotifier.model.SlackUsernameLocationNamePair;

@Component
public class SlackUserToLocationNameMappingDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private final Logger logger = LoggerFactory.getLogger(SlackUserToLocationNameMappingDAO.class);
	
	private static final String SELECT_BY_SLACK_USERNAME = "select locationname from slacktolocationmapping where slackusername = ?";
	private static final String SELECT_ALL_MAPPINGS = "select slackusername, locationname from slacktolocationmapping";
	private static final String INSERT_MAPPING = "insert into slacktolocationmapping (slackusername, locationname) values (?, ?)";
	private static final String UPDATE_MAPPING = "update slacktolocationmapping set locationname = ? where slackusername = ?";
	
	/**
	 * Add or update a Slack user name to location name mapping
	 * @param slackUsername Slack user name
	 * @param locationName The name of the location data associated with this Slack user name
	 */
	public void addMapping(String slackUsername, String locationName) {
		try {
			jdbcTemplate.update(INSERT_MAPPING, 
					new Object[] { slackUsername, locationName } );
		} catch (DuplicateKeyException e) {
			jdbcTemplate.update(UPDATE_MAPPING,
					new Object[] { locationName, slackUsername } );
		}
	}
	
	/**
	 * Get the location name associated with a given Slack user name
	 * @param slackUsername A Slack user name
	 * @return the name of the location data associated with this Slack user name
	 */
	public String getLocationNameForSlackUsername(String slackUsername) {
		try {
			return jdbcTemplate.queryForObject(SELECT_BY_SLACK_USERNAME, 
					new Object[] { slackUsername }, 
					String.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (IncorrectResultSizeDataAccessException e) {
			logger.error("User has two mappings?", e);
			return null;
		}
	}
	
	/**
	 * Get all currently-configured mappings between Slack user names and location names
	 * The map returned is Slack user names, not the internal ID, so you'll need to look 
	 * up the user ID in order to really use this information
	 * @return A map of Slack username keys to location name values 
	 */
	public Map<String,String> getAllMappings() {
		try {
			LinkedHashMap<String,String> mappings = new LinkedHashMap<String,String>();
			List<SlackUsernameLocationNamePair> rows = jdbcTemplate.query(SELECT_ALL_MAPPINGS, 
					new BeanPropertyRowMapper<SlackUsernameLocationNamePair>(SlackUsernameLocationNamePair.class));
			for (SlackUsernameLocationNamePair pair : rows) {
				mappings.put(pair.getSlackUsername(), pair.getLocationName());
			}
			return mappings;
		} catch (DataAccessException e) {
			logger.info(e.getMessage(), e);
			return Collections.emptyMap();
		} 
	}
}
