package com.sharkbaitextraordinaire.bootnotifier.integration.outbound.slack;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.sharkbaitextraordinaire.bootnotifier.dao.SlackUserToLocationNameMapping;

import allbegray.slack.rtm.EventListener;
import allbegray.slack.type.User;
import allbegray.slack.webapi.SlackWebApiClient;

/**
 * Woodhouse handles configuration and sanity checks 
 *
 */
@Component
public class WoodhouseEventListener implements EventListener {
	
	public static Pattern WOODHOUSE_MENTION_PATTERN = Pattern.compile("^@woodhouse ", Pattern.CASE_INSENSITIVE);
	private final Logger logger = LoggerFactory.getLogger(WoodhouseEventListener.class);
	private SlackWebApiClient slackClient;
	
	private User woodhouseUser;

	@Autowired SlackUserToLocationNameMapping slackUserToLocationMapping;
	List<User> userList; // we'll need this for usernames
	
	public WoodhouseEventListener(SlackWebApiClient slackClient) {
		if (userList == null) {
			userList = slackClient.getUserList();
		}
		for (User u : userList) {
			if (u.getName().equals("woodhouse")) {
				woodhouseUser = u;
			}
		}
	}
	
	public void setSlackClient(SlackWebApiClient slackClient) {
		this.slackClient = slackClient;
	}
	
	@Override
	public void handleMessage(JsonNode json) {
		String messageText = json.get("text").textValue();
		Matcher m = WOODHOUSE_MENTION_PATTERN.matcher(messageText);
		if (messageText.startsWith("@<"+woodhouseUser.getId()+"> ")) {
			// make eggs woodhouse and such
			logger.warn("making eggs woodhouse...");
			handleWoodhouseMention(json);
		} else return;
	}
	
	
	void handleWoodhouseMention(JsonNode json) {
		// @woodhouse use location "" for @slackuser
		// @woodhouse get locationname for @slackuser
		// @woodhouse get location mappings
		if (json.get("text").textValue().equalsIgnoreCase("@<"+ woodhouseUser.getId() +"> get location mappings")) {
			Map<String,String> mappings = slackUserToLocationMapping.getAllMappings();
			StringBuilder sb = new StringBuilder();
			for (String key : mappings.keySet()) {
				logger.warn(key + "-->" + mappings.get(key));
				sb.append(key).append(": ").append(mappings.get(key));
			}
			logger.warn(sb.toString());
		}
	}
}
