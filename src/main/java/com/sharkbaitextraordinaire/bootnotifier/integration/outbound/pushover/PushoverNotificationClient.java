package com.sharkbaitextraordinaire.bootnotifier.integration.outbound.pushover;

import net.pushover.client.PushoverClient;
import net.pushover.client.PushoverException;
import net.pushover.client.PushoverMessage;
import net.pushover.client.PushoverRestClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sharkbaitextraordinaire.bootnotifier.config.PushoverConfig;

@Component
public class PushoverNotificationClient {

	// TODO should probably make this a singleton or obtained from a factory
	private PushoverClient client = new PushoverRestClient();
	@Autowired
	private PushoverConfig config;
	
	private final Logger logger = LoggerFactory.getLogger(PushoverNotificationClient.class);
	
	public void sendMessage(String message) {
		try {
			client.pushMessage(PushoverMessage.builderWithApiToken(config.getApplicationToken())
					.setUserId(config.getUserToken())
					.setMessage(message)
					.build());
		} catch (PushoverException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void sendMessage(String message, String url) {
		try {
			client.pushMessage(PushoverMessage.builderWithApiToken(config.getApplicationToken())
					.setUserId(config.getUserToken())
					.setMessage(message)
					.setUrl(url)
					.build());
		} catch (PushoverException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
