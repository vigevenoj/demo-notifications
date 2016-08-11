package com.sharkbaitextraordinaire;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharkbaitextraordinaire.dao.EarthquakeDAO;
import com.sharkbaitextraordinaire.model.Earthquake;

@Component
public class EathquakeFetcher {
	
	private static final Logger logger = LoggerFactory.getLogger(EathquakeFetcher.class);

	private Client client;
	private EarthquakeDAO dao;
	private LinkedBlockingQueue<Earthquake> queue;

	@Scheduled(fixedRate = 5 * 60 * 1000) // every five minutes
	public void fetchEarthquakeFeed() {
		logger.warn("this thread would be fetching an earthquake feed");
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		WebTarget target = client.target("");
		Invocation.Builder invocationBuilder = target.request();
		Response response = invocationBuilder.get();
		
		int status = response.getStatus();
		if (status == 200) {
			
			String feedString = response.readEntity(String.class);
			try {
				FeatureCollection fc = mapper.readValue(feedString, FeatureCollection.class);
				for (Feature feature : fc.getFeatures()) {
					GeoJsonObject g = feature.getGeometry();
					if (g instanceof Point) {
						try {
							Earthquake quake = new Earthquake(feature);
							if (dao.findById(quake.getId()) == null) {
								dao.insert(quake);
								queue.put(quake);
								logger.debug("queued a quake (" + queue.size() + ")");
							} else {
								logger.debug("quake " + quake.getId() + " was already seen");
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
}
