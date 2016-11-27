package com.sharkbaitextraordinaire.bootnotifier.integration.inbound;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharkbaitextraordinaire.bootnotifier.config.BridgeClientConfig;
import com.sharkbaitextraordinaire.multcobridges.BridgeUpdate;
import com.sharkbaitextraordinaire.multcobridges.SingleBridgeUpdate;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.slf4j.Logger;

@Component
public class BridgeClient {

	private Client client;
	private final Logger logger = LoggerFactory.getLogger(BridgeClient.class);
	@Autowired
	private BridgeClientConfig config;

	private AtomicBoolean isOpen = new AtomicBoolean(false);

	private ExecutorService executorService;
	private volatile boolean stopThread = false;

	public BridgeClient() {
	}

	@PostConstruct
	public void init() {

		executorService = Executors.newSingleThreadExecutor();
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				doTask();
			}
		});
		executorService.shutdown();
	}
	
	private void doTask() {
		logger.info("Bridge lift status client starting up...");
		logger.info("Using " + config.getApiUrl() + " as target");

		client = JerseyClientBuilder.newBuilder().register(SseFeature.class).build();
		WebTarget target = client.target(config.getApiUrl() + "?accessToken=" + config.getApiKey());

		EventInput eventInput = target.request().get(EventInput.class);
		while (!eventInput.isClosed()) {
			InboundEvent inboundEvent = eventInput.read();
			isOpen.set(true);
			if (inboundEvent == null) {
				// connection has been closed
				// TODO fail healthcheck?
				isOpen.set(false);
				break;
			}
			parseBridgeUpdate(inboundEvent);
			// Check to see if we are shutting down
			if (stopThread) {
				return;
			}
		}
	}
	
	private void parseBridgeUpdate(InboundEvent inboundEvent) {
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		if (inboundEvent.getName() == null || inboundEvent.getName().equals("null")) {
			// this is a keep-alive message, and should be seen every 20 seconds
			logger.debug("Bridge event (name is null): '" + inboundEvent.getName() + "'");
		} else if (inboundEvent.getName().equals("bridge data")) {
			// Bridge Data:
			// A json object consisting of possible updates to bridge statuses
			logger.info(inboundEvent.getName() + "; " + inboundEvent.readData(String.class));
			BridgeUpdate bu;
			try {
				bu = mapper.readValue(inboundEvent.readData(), BridgeUpdate.class);
				String changedBridge = bu.getChangedBridge();
				String bridgeEventTime = "";
				String event = "";

				if (bu.getChanged().equals("status")) {
					if (changedBridge != null && !("".equals(changedBridge))) {
						logger.warn("Getting bridge update for " + changedBridge);
						SingleBridgeUpdate sbu = bu.getBridgeUpdates().get(changedBridge);
						if (sbu == null) {
							logger.warn("Bridge update for " + changedBridge + " was null");
						} else {
							logger.warn("Up time for bridge is " + sbu.getUpTime());
							bridgeEventTime = sbu.getUpTime().toString();
						}
					} else {
						logger.warn("Changed bridge was null");
					}
					event = "raised";
				} else {
					bridgeEventTime = bu.getBridgeUpdates().get(changedBridge).getLastFive().get(0).getDownTime().toString();
					event = "lowered";
				}
				logger.error(changedBridge + " " + event + " at " + bridgeEventTime);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// This events name is "null;" and is the keepalive?
			logger.info("bridge event (in else): '" + inboundEvent.getName() + "'");
			logger.debug(inboundEvent.toString());
		}
	}
	
	 @PreDestroy
	 public void beandestroy() {
	  this.stopThread = true;
	 
	  if(executorService != null){
	   try {
	    // wait 1 second for closing all threads
	    executorService.awaitTermination(1, TimeUnit.SECONDS);
	   } catch (InterruptedException e) {
	    Thread.currentThread().interrupt();
	   }
	  }
	 }
	 
	 public boolean isConnected() {
		 return isOpen.get();
	 }
	 
}
