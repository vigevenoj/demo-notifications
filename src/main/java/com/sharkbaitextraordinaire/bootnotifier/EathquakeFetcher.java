package com.sharkbaitextraordinaire.bootnotifier;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.Point;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharkbaitextraordinaire.bootnotifier.config.AppConfig;
import com.sharkbaitextraordinaire.bootnotifier.dao.EarthquakeDAO;
import com.sharkbaitextraordinaire.bootnotifier.model.Earthquake;

@Component
public class EathquakeFetcher {
	
	private static final Logger logger = LoggerFactory.getLogger(EathquakeFetcher.class);

	private Client client;
	@Autowired
	private EarthquakeDAO dao;
	@Autowired
	private AppConfig appconfig;
	
	public EathquakeFetcher() {
		client = new JerseyClientBuilder().build();
	}

	@Scheduled(fixedRate = 5 * 60 * 1000) // every five minutes
	public void fetchEarthquakeFeed() {
		
		logger.info("Fetching an earthquake feed");
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		WebTarget target = client.target("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");
		Invocation.Builder invocationBuilder = target.request();
		Response response = invocationBuilder.get();
		
		int status = response.getStatus();
		if (status == 200) {
			
			logger.debug("USGS feed fetched with 200 response");
			String feedString = response.readEntity(String.class);
			try {
				FeatureCollection fc = mapper.readValue(feedString, FeatureCollection.class);
				for (Feature feature : fc.getFeatures()) {
					GeoJsonObject g = feature.getGeometry();
					if (g instanceof Point) {
						try {
							Earthquake quake = new Earthquake(feature);
							// This try-catch on empty data exception feels dirty
							try {
								dao.findById(quake.getId());
								logger.debug("quake " + quake.getId() + " was already seen");
							} catch (EmptyResultDataAccessException e) {
								// The quake does not exist, so insert it
								dao.insert(quake);
								logger.debug("inserted quake with id '" + quake.getId() + "' into database");
								appconfig.earthquakeQueue().put(quake);
								logger.debug("queued a quake (" + appconfig.earthquakeQueue().size() + ")");
							}
						} catch (InterruptedException e) {
							logger.error("interrupted while adding quake to queue");
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							logger.error("Illegal argument while creating earthquake? " + feature.getProperty("title"));
							e.printStackTrace();
						}
					} else { 
						logger.error("Earthquake " + feature.getProperty("title") + " did not have a suitable location");
					}
				}
			} catch (JsonParseException e) {
				logger.error(e.getMessage());
			} catch (JsonMappingException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public void setEarthquakeDAO(EarthquakeDAO dao) {
		this.dao = dao;
	}
}
