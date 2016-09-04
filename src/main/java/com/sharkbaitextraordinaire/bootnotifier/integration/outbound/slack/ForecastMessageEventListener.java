package com.sharkbaitextraordinaire.bootnotifier.integration.outbound.slack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.dvdme.ForecastIOLib.FIOCurrently;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.sharkbaitextraordinaire.bootnotifier.config.ForecastConfig;
import com.sharkbaitextraordinaire.bootnotifier.dao.LocationUpdateDAO;
import com.sharkbaitextraordinaire.bootnotifier.model.LocationUpdate;

import allbegray.slack.rtm.EventListener;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;

@Component
public class ForecastMessageEventListener implements EventListener {
	
	@Autowired
	private LocationUpdateDAO dao;
	@Autowired
	private ForecastConfig forecastConfig;
	private SlackWebApiClient slackClient;
	private final Logger logger = LoggerFactory.getLogger(ForecastMessageEventListener.class);
	
	public ForecastMessageEventListener(LocationUpdateDAO dao, ForecastConfig config) {
		this.dao = dao;
		this.forecastConfig = config;
	}
	
	public void setSlackClient(SlackWebApiClient slackClient) {
		this.slackClient = slackClient;
	}
	
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
		LocationUpdate latest = dao.findLatest();
		
		String messageChannel = json.get("channel").textValue();
		
		
		// TODO better null check here- 0 is a valid lat/lng but not 0,0.
		if (latest == null || (latest.getLatitude() == 0 && latest.getLongitude() == 0)) {
			logger.error("No latest update for any user, aborting request");
			String no_user_locations = "Can't give a forecast because nobody has a location";
			ChatPostMessageMethod postMessage = new ChatPostMessageMethod(messageChannel, no_user_locations);
			postMessage.setUnfurl_links(true);
			postMessage.setUsername("woodhouse");
			postMessage.setAs_user(true);
			slackClient.postMessage(postMessage);
			return;
		}
		
		ForecastIO fio = new ForecastIO(forecastConfig.getApiKey());
		fio.setExcludeURL("minutely");
		fio.getForecast(String.valueOf(latest.getLatitude()), String.valueOf(latest.getLongitude()));
		FIOCurrently currently = new FIOCurrently(fio);
		StringBuffer sb = new StringBuffer();
		for (String s : currently.get().getFieldsArray()) {
			sb.append(s).append(": ")
			.append(currently.get().getByKey(s) + "\n");
		}
		logger.warn("currently");
		logger.warn(sb.toString());
		
		ChatPostMessageMethod postMessage = new ChatPostMessageMethod(messageChannel, "Check logs for your forecast");
		postMessage.setUnfurl_links(true);
		postMessage.setUsername("woodhouse");
		postMessage.setAs_user(true);
		slackClient.postMessage(postMessage);
	}

}
