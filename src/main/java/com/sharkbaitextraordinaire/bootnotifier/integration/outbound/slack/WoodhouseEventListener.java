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
import com.sharkbaitextraordinaire.bootnotifier.dao.SlackUserToLocationNameMappingDAO;

import allbegray.slack.rtm.EventListener;
import allbegray.slack.type.User;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;

/**
 * Woodhouse handles configuration and sanity checks 
 *
 */
@Component
public class WoodhouseEventListener implements EventListener {
	
	public static Pattern WOODHOUSE_MENTION_PATTERN;
	private final Logger logger = LoggerFactory.getLogger(WoodhouseEventListener.class);
	private SlackWebApiClient slackClient;
	
	private User woodhouseUser;

	@Autowired SlackUserToLocationNameMappingDAO slackUserToLocationMapping;
	List<User> userList; // we'll need this for usernames
	
	public WoodhouseEventListener() {
	}
	
	public void setSlackClient(SlackWebApiClient slackClient) {
		this.slackClient = slackClient;
		updateUserList();
	}
	
	private void updateUserList() {
		if (userList == null) {
		userList = slackClient.getUserList();
		}
		for (User u : userList) {
			if (u.getName().equals("woodhouse")) {
				woodhouseUser = u;
				logger.warn("Using user id " + u.getId() + " as woodhouse user since they have name " + u.getName());
				WOODHOUSE_MENTION_PATTERN = Pattern.compile("^<@"+woodhouseUser.getId()+">", Pattern.CASE_INSENSITIVE);
			}
		}
	}
	
	@Override
	public void handleMessage(JsonNode json) {
		String messageText = json.get("text").textValue();
		logger.warn("trying to handle message text");
		logger.warn(messageText);
		if (checkForWoodhouseMention(messageText)) {
			// make eggs woodhouse and such
			logger.warn("making eggs woodhouse...");
			handleWoodhouseMention(json);
		} else return;
	}
	
	boolean checkForWoodhouseMention(String text) {
		Matcher m = WOODHOUSE_MENTION_PATTERN.matcher(text);
		return (m.matches() || text.startsWith("<@"+woodhouseUser.getId()+">"));
	}
	
	void handleWoodhouseMention(JsonNode json) {
		// @woodhouse use location "" for @slackuser
		// @woodhouse get locationname for @slackuser
		// @woodhouse get location mappings
		if (json.get("text").textValue().equalsIgnoreCase("<@"+ woodhouseUser.getId() +"> get location mappings")) {
			Map<String,String> mappings = slackUserToLocationMapping.getAllMappings();
			StringBuilder sb = new StringBuilder();
			for (String key : mappings.keySet()) {
				logger.warn(key + "-->" + mappings.get(key));
				sb.append(key).append(": ").append(mappings.get(key)).append('\n');
			}
			logger.warn(sb.toString());
			ChatPostMessageMethod postMessage = new ChatPostMessageMethod(json.get("channel").textValue(), sb.toString());
			postMessage.setUnfurl_links(true);
			postMessage.setUsername("woodhouse");
			postMessage.setAs_user(true);
			slackClient.postMessage(postMessage);
		}
	}
}
