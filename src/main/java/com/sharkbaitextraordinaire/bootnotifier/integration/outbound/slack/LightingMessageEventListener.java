package com.sharkbaitextraordinaire.bootnotifier.integration.outbound.slack;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import allbegray.slack.rtm.EventListener;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;

public class LightingMessageEventListener implements EventListener{
	
	private final Logger logger = LoggerFactory.getLogger(LightingMessageEventListener.class);
	private SlackWebApiClient slackClient;
	private PHHueSDK huesdk;
	
	public LightingMessageEventListener(SlackWebApiClient slackClient, PHHueSDK huesdk) {
		this.slackClient = slackClient;
		this.huesdk = huesdk;
	}

	@Override
	public void handleMessage(JsonNode message) {
		String messageText = message.get("text").textValue();
		
		if (messageText.startsWith("lights ") || messageText.startsWith("light" ) ) {
			handleLightingIntegrationMessage(message);
		} else {
			logger.warn("Lighting integration received a message it couldn't handle: " + messageText);
		}
		
	}
	
	private void handleLightingIntegrationMessage(JsonNode message) {
		String messageText = message.get("text").textValue();
		String user = message.get("user").textValue();
		String messageChannel = message.get("channel").textValue();
		
		logger.info("Starting lighting integration command for '" + messageText + "'");
		
		PHBridge bridge = huesdk.getSelectedBridge();
		PHBridgeResourcesCache cache = bridge.getResourceCache();
		
		if (messageText.startsWith("lights list")) {
			logger.info("Listing lights for " + user + " in " + messageChannel);
			
			List<PHLight> lights = cache.getAllLights();
			StringBuilder sb = new StringBuilder();
			for (PHLight light : lights) {
				sb.append(light.getIdentifier()).append(" " ).append(light.getName());
				sb.append("\n");
			}
			ChatPostMessageMethod postMessage = new ChatPostMessageMethod(messageChannel, sb.toString());
			postMessage.setUnfurl_links(true);
			postMessage.setUsername("woodhouse");
			postMessage.setAs_user(true);
			
			String ts = slackClient.postMessage(postMessage);
			logger.info(ts);
		} else if (messageText.startsWith("lights on")) {
			logger.debug("turning the lights on for " + user);
			PHLightState lightstate = new PHLightState();
			lightstate.setOn(true);
			bridge.setLightStateForDefaultGroup(lightstate);
		} else if (messageText.startsWith("lights off")) {
			logger.debug("turning the lights off for " + user);
			PHLightState lightstate = new PHLightState();
			lightstate.setOn(false);
			bridge.setLightStateForDefaultGroup(lightstate);
		}
		
		if (messageText.startsWith("light " )) {
			// handle a single light
			// first word is light so drop it
			String[] line = messageText.split(" ", 2);
			// line[1] contains the rest of the string
			try {
				String[] args = line[1].split("\\s|,");
				String lightid = args[0];
				// Use this parse to see if lightid is an int (no throw) or a string (throws and is caught)
				Integer.parseInt(lightid);
				String onoff = args[1];
				PHLight light = bridge.getResourceCache().getLights().get(lightid);
				logger.debug("fetched light from cache, " + light.getIdentifier());
				toggleLightOnOff(light, onoff);
			} catch (NumberFormatException e) {
				// Argument list doesn't start with a number so treat it as the name of a light
				String lightname;
				String[] args = line[1].split(",");
				lightname = args[0].trim();
				String onoff = args[1].trim();
				List<PHLight> lights = bridge.getResourceCache().getAllLights();
				for (PHLight light : lights) {
					logger.debug("comparing '" + lightname + "' to '" + light.getName() + "'");
					if (light.getName().trim().equalsIgnoreCase(lightname)) {
						toggleLightOnOff(light, onoff);
						break;
					}
				}
			}
		}
	}
	
	private void toggleLightOnOff(PHLight light, String onoff) {
		logger.debug("Toggling " + light.getIdentifier() + " (" + light.getName() + ") " + onoff);
		PHLightState lightState = new PHLightState();
		if (onoff.equalsIgnoreCase("on")) {
			lightState.setOn(true);
		} else if (onoff.equalsIgnoreCase("off")) {
			lightState.setOn(false);
		}
		PHHueSDK.getInstance().getSelectedBridge().updateLightState(light, lightState);
	}
	
	private String getLightIdFromName(String name) {
		List<PHLight> lights = huesdk.getInstance().getSelectedBridge().getResourceCache().getAllLights();
		for (PHLight light : lights) {
			if (light.getName().equals(name)) {
				return light.getIdentifier();
			}
		}
		return null;
	}

}
