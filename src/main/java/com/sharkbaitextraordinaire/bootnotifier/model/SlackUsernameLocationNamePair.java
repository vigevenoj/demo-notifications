package com.sharkbaitextraordinaire.bootnotifier.model;

public class SlackUsernameLocationNamePair {
	private String slackUsername;
	private String locationName;
	
	public SlackUsernameLocationNamePair(String slackUsername, String locationName) {
		this.slackUsername = slackUsername;
		this.locationName = locationName;
	}
	
	public String getSlackUsername() {
		return slackUsername;
	}
	
	public void setSlackUsername(String slackUsername) {
		this.slackUsername = slackUsername;
	}
	
	public String getLocationName() {
		return locationName;
	}
	
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
}
