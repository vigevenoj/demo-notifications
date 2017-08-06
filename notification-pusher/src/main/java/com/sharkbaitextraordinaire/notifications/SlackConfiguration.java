package com.sharkbaitextraordinaire.notifications;

public class SlackConfiguration {
	private String teamName;
	private String channelNames;
	private String token;
	
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public String getChannelName() {
		return channelNames;
	}
	public void setTeamNames(String channelNames) {
		this.channelNames = channelNames;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
