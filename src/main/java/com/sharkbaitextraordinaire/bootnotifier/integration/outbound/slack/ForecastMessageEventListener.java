package com.sharkbaitextraordinaire.bootnotifier.integration.outbound.slack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.sharkbaitextraordinaire.bootnotifier.config.ForecastConfig;
import com.sharkbaitextraordinaire.bootnotifier.dao.LocationUpdateDAO;
import com.sharkbaitextraordinaire.bootnotifier.model.LocationUpdate;

import allbegray.slack.rtm.EventListener;

public class ForecastMessageEventListener implements EventListener {
	
	@Autowired
	private LocationUpdateDAO locationUpdateDAO;
	@Autowired
	private ForecastConfig forecastConfig;
	private final Logger logger = LoggerFactory.getLogger(ForecastMessageEventListener.class);
	
	@Override
	public void handleMessage(JsonNode json) {
		
		// determine who the message was from and look up their last location.
		// if there wasn't a location for this user, try the last updated location in general
		// and let them know that we aren't sure about where they are
		// https://api.forecast.io/forecast/APIKEY/LATITUDE,LONGITUDE is the format for the lookup
		String messageText = json.get("text").textValue();
		
		if (messageText.startsWith("forecast")) {
			handleForecastRequest(json);
		} else if (messageText.startsWith("sunset")) {
			// handle sunset request
		} else if (messageText.startsWith("sunrise")) {
			// handle sunrise request
		} else {
			logger.warn("Received a message we don't know how to handle");
		}
	}
	
	private void handleForecastRequest(JsonNode json) {
		LocationUpdate latest = locationUpdateDAO.findLatest();
    if (latest == null) {
      logger.error("No latest update for any user, aborting request");
      return;
    }
		
		ForecastIO fio = new ForecastIO(forecastConfig.getApiKey());
		fio.setExcludeURL("minutely");
		fio.getForecast(String.valueOf(latest.getLatitude()), String.valueOf(latest.getLongitude()));
	}

}
