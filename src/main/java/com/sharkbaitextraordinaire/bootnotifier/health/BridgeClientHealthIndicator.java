package com.sharkbaitextraordinaire.bootnotifier.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import com.sharkbaitextraordinaire.bootnotifier.integration.inbound.BridgeClient;


public class BridgeClientHealthIndicator implements HealthIndicator {
	
	private final BridgeClient bridgeClient;
	
	@Autowired
	public BridgeClientHealthIndicator(BridgeClient bridgeClient) {
		this.bridgeClient = bridgeClient;
	}

	@Override
	public Health health() {
		if (bridgeClient.isConnected()) {
			return Health.up().build();
		} else {
			return Health.down().build();
		}
	}

	
}
