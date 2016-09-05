package com.sharkbaitextraordinaire.bootnotifier.model;

/**
 * slackUsername is the end-user-visible username in slack, not the internal ID 
 * locationName is the name of the location in the locationUpdates table
 */
public class SlackUsernameLocationNamePair {
	private String slackUsername;
	private String locationName;
	
	public SlackUsernameLocationNamePair() { }
	
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
