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
		return dao.findAll();
	}
	
	@RequestMapping(value = "/latest", method = RequestMethod.GET)
	public LocationUpdate latest() {
		return dao.findLatest();
	}
}
