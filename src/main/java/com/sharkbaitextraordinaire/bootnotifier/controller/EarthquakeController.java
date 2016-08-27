package com.sharkbaitextraordinaire.bootnotifier.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sharkbaitextraordinaire.bootnotifier.dao.EarthquakeDAO;
import com.sharkbaitextraordinaire.bootnotifier.model.Earthquake;

/**
 * Read-only view of earthquakes
 *
 */
@RestController
@RequestMapping("/quakes")
public class EarthquakeController {
	
	@Autowired
	EarthquakeDAO dao;
	
	private static final Logger logger = LoggerFactory.getLogger(EarthquakeController.class);
	
	@RequestMapping(method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Earthquake>> findAll() {
		List<Earthquake> earthquakes = dao.findAllEarthQuakes();
		if (earthquakes.isEmpty()) {
			logger.warn("No earthquakes found in database");
			return new ResponseEntity<List<Earthquake>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Earthquake>>(earthquakes, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Earthquake> findById(@PathVariable String id) {
		Earthquake earthquake = dao.findById(id);
		if (earthquake == null) {
			return new ResponseEntity<Earthquake>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Earthquake>(earthquake, HttpStatus.OK);
	}
}
