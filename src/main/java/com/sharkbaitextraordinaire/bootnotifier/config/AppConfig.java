package com.sharkbaitextraordinaire.bootnotifier.config;

import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sharkbaitextraordinaire.bootnotifier.model.Earthquake;

@Configuration
public class AppConfig {

	@Bean
    public LinkedBlockingQueue<Earthquake> earthquakeQueue() {
        return new LinkedBlockingQueue<Earthquake>();
    }
	
	@Bean
	public BridgeClientConfig bridgeClientConfig() {
		return new BridgeClientConfig();
	}
}
