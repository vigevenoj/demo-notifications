package com.sharkbaitextraordinaire.bootnotifier.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sharkbaitextraordinaire.bootnotifier.dao.LocationUpdateDAO;
import com.sharkbaitextraordinaire.bootnotifier.model.LocationUpdate;

@RestController
@RequestMapping("/location")
public class LocationUpdateController {

	@Autowired
	LocationUpdateDAO dao;
	
	@RequestMapping(method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<LocationUpdate> findAll() {
		List<LocationUpdate> locations = dao.findAll();
		if (locations.isEmpty()) {
			return null;
			// return 204 no content
		}
		return locations;
	}
	
	@RequestMapping(value = "/latest", method = RequestMethod.GET)
	public LocationUpdate latest() {
		LocationUpdate location = dao.findLatest();
		if (location == null) {
			return null;
			// return 204 no content or 404 not found?
		}
		return dao.findLatest();
	}
	
	@RequestMapping(value="/{name}", method=RequestMethod.GET)
	public List<LocationUpdate> findAllByName(String name) {
		return dao.findAllForUser(name);
	}
	
	@RequestMapping(value="/latest/{name}", method=RequestMethod.GET)
	public LocationUpdate findLatestForUser(String name) {
		return dao.latestForUser(name);
	}
}
