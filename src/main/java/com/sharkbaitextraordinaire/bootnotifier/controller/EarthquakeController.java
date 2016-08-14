package com.sharkbaitextraordinaire.bootnotifier.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
	
	@RequestMapping(method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Earthquake> findAll() {
		return dao.findAllEarthQuakes();
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Earthquake findById(@PathVariable String id) {
		return dao.findById(id);
	}
}
