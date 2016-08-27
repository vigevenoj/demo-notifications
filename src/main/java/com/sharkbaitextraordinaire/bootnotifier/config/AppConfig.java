package com.sharkbaitextraordinaire.bootnotifier.config;

import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.sharkbaitextraordinaire.bootnotifier.model.Earthquake;

@Configuration
public class AppConfig {
	
	@Bean
	public WebMvcConfigurerAdapter forwardToIndex() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addViewControllers(ViewControllerRegistry registry) {
				registry.addViewController("/").setViewName("forward:/index.html");
			}
		};
	}
	
	/** 
	 * A shared queue for earthquake analysis
	 * 
	 */
	// TODO this should probably be in a message queue (mqtt or jms)
	@Bean
    public LinkedBlockingQueue<Earthquake> earthquakeQueue() {
        return new LinkedBlockingQueue<Earthquake>();
    }

	/**
	 * Configuration for Multnomah County bridge lift API
	 */
	@Bean
	public BridgeClientConfig bridgeClientConfig() {
		return new BridgeClientConfig();
	}
	
	/**
	 * Configuration for connecting to an MQTT broker for Owntracks updates
	 */
	@Bean
	public OwntracksConfig owntracksConfig() {
		return new OwntracksConfig();
	}
	
	/**
	 * Configuration for sending notifications via Pushover.net 
	 */
	@Bean
	public PushoverConfig pushoverConfig() {
		return new PushoverConfig();
	}
	
	/**
	 * Configuration for Slack integration
	 */
	@Bean
	public SlackConfig slackConfig() {
		return new SlackConfig();
	}
	
	/**
	 * Configuration for Philips Hue lighting integration
	 */
	@Bean
	public HueConfig hueConfig() {
		return new HueConfig();
	}
}
