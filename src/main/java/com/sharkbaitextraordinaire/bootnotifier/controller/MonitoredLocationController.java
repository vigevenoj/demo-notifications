package com.sharkbaitextraordinaire.bootnotifier.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sharkbaitextraordinaire.bootnotifier.model.MonitoredLocation;

@RestController
@RequestMapping("/monitored")
public class MonitoredLocationController {

	
	@RequestMapping(method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<MonitoredLocation> findAll() {
		return null;
	}
}
