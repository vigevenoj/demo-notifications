package com.sharkbaitextraordinaire.bootnotifier.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="slack")
public class SlackConfig {
	/** The name of the team for which this configuration applies */
	private String teamName;
	/** The name of the channel for which this configuration applies */
	private String channelName;
	private String token;
	
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
