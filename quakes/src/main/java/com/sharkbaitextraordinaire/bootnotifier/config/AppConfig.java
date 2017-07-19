package com.sharkbaitextraordinaire.bootnotifier.config;

import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.sharkbaitextraordinaire.bootnotifier.model.Earthquake;
import com.sharkbaitextraordinaire.notifications.Notification;

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
	
	@Bean
	public LinkedBlockingQueue<Notification> notificationQueue() {
		return new LinkedBlockingQueue<Notification>();
	}

	/**
	 * Configuration for connecting to an MQTT broker for Owntracks updates
	 */
	@Bean
	public OwntracksConfig owntracksConfig() {
		return new OwntracksConfig();
	}
	
	/**
	 * Configuration for earthquake analysis and notifications
	 */
	@Bean EarthquakeAnalysisConfig earthquakeAnalysisConfig() {
		return new EarthquakeAnalysisConfig();
	}
	
	/**
	 * Configuration for sending notifications
	 */
	@Bean NotificationSenderConfig notificationConfig() {
		return new NotificationSenderConfig();
	}
}
