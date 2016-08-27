package com.sharkbaitextraordinaire.bootnotifier.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sharkbaitextraordinaire.bootnotifier.dao.MonitoredLocationDAO;
import com.sharkbaitextraordinaire.bootnotifier.model.MonitoredLocation;

@RestController
@RequestMapping("/monitored")
public class MonitoredLocationController {
	
	@Autowired
	private MonitoredLocationDAO dao;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<MonitoredLocation>> findAll() {
		List<MonitoredLocation> locations = dao.findAll();
		if (locations.isEmpty()) {
			return new ResponseEntity<List<MonitoredLocation>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<MonitoredLocation>>(locations, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{name}", method = RequestMethod.GET)
	public ResponseEntity<MonitoredLocation> findByName(@PathVariable String name) {
		MonitoredLocation location = dao.findByName(name);
		if (location == null) {
			return new ResponseEntity<MonitoredLocation>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<MonitoredLocation>(location, HttpStatus.OK);
		}
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> createMonitoredLocation(@Validated @RequestBody MonitoredLocation location) {
		if (dao.findByName(location.getName()) != null) {
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		} else {
			dao.insert(location);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setLocation(ServletUriComponentsBuilder
					.fromCurrentRequest().path("/{location}")
					.buildAndExpand(location.getName()).toUri());
			return new ResponseEntity<Void>(null, httpHeaders, HttpStatus.CREATED);
		}

	}
}
