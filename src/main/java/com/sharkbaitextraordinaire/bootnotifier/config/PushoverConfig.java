package com.sharkbaitextraordinaire.bootnotifier.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="pushover")
public class PushoverConfig {
	
	private String applicationToken;
	private String userToken;
	
	public String getApplicationToken() {
		return this.applicationToken;
	}
	
	public void setApplicationToken(String applicationToken) {
		this.applicationToken = applicationToken;
	}
	
	public String getUserToken() {
		return this.userToken;
	}
	
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
}
