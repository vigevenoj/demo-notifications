package com.sharkbaitextraordinaire.bootnotifier.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sharkbaitextraordinaire.bootnotifier.dao.MonitoredLocationDAO;
import com.sharkbaitextraordinaire.bootnotifier.model.MonitoredLocation;

@RestController
@RequestMapping("/monitored")
public class MonitoredLocationController {
	
	@Autowired
	private MonitoredLocationDAO dao;

	
	@RequestMapping(method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<MonitoredLocation> findAll() {
		return dao.findAll();
	}
	
	@RequestMapping(value = "/{locationName}", method = RequestMethod.GET)
	public MonitoredLocation findByName(@PathVariable String name) {
		MonitoredLocation location = dao.findByName(name);
		if (location == null) {
			return location;
		} else {
			return null; // TODO return a 404			
		}
	}
	
	@RequestMapping(value = "/{locationName}", method = RequestMethod.POST)
	public MonitoredLocation createMonitoredLocation(@RequestBody MonitoredLocation location) {
		if (dao.findByName(location.getName()) != null) {
			return null; // TODO return 409 conflict
		} else {
			dao.insert(location);
		}
		return location;
	}
}
