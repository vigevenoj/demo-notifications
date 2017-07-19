package com.sharkbaitextraordinaire.bootnotifier.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import com.sharkbaitextraordinaire.bootnotifier.integration.inbound.OwntracksMqttClient;

public class OwntracksHealthIndicator implements HealthIndicator {

	private final OwntracksMqttClient client;
	
	@Autowired
	public OwntracksHealthIndicator(OwntracksMqttClient client) {
		this.client = client;
	}
	
	@Override
	public Health health() {
		if (client.getClient().isConnected()) {
			return Health.up().build();
		} else {
			return Health.down().build();
		}
	}

}
