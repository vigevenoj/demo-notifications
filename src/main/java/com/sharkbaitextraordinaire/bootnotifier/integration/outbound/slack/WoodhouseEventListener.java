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
		String messageText = json.get("text").textValue();
		// @woodhouse use location "" for @slackuser
		// @woodhouse set location mapping for @slackuser to foo
		// example message: "<@U1LEHFD1N> set location mapping for <@U1LAMGD4N> to /owntracks/jacob/nexus5" 
		if (messageText.startsWith("<@"+woodhouseUser.getId()+"> set location mapping for ")) {
			logger.error("setting location mapping for user");
			// parse target userID
			String[] message = messageText.split("set location mapping for "); // TODO bounds check this array
			String userid = message[1].substring(2, message[1].lastIndexOf(">"));
			String locationname = messageText.split("<@[A-Z0-9]*?> to")[1]; // TODO bounds check this array

			// look up user's name
			// TODO confirm that userList contains a user where user.getName() returns the name we want
			for (User u : userList) {
				if (u.getId().equals(userid)) {
					slackUserToLocationMapping.addMapping(u.getName(), locationname.trim());
					StringBuilder sb = new StringBuilder();
					sb.append("Using '").append(locationname.trim())
					.append("' for location updates for ")
					.append("<@").append(u.getId()).append(">");
					ChatPostMessageMethod postMessage = new ChatPostMessageMethod(json.get("channel").textValue(), sb.toString());
					postMessage.setUnfurl_links(true);
					postMessage.setUsername("woodhouse");
					postMessage.setAs_user(true);
					slackClient.postMessage(postMessage);
					return;
				}
			}
			return;
		}
		// @woodhouse get locationname for @slackuser
		// example message: "<@U1LEHFD1N> get location mapping for <@U1LAMGD4N>"
		else if (messageText.startsWith("<@"+woodhouseUser.getId()+"> get location mapping for ")) {
			logger.info("getting location mapping for user");
			// parse target userID
			String[] message = messageText.split(" get location mapping for "); // TODO bounds checking this array
			for (String s : message) { 
				logger.error(s); // message[1] should contain a <@slackid> of the target user
			}
			String userid = message[1].substring(2, message[1].length()-1);
			// TODO confirm that userList contains a user where user.getName() returns the name we want
			for (User u : userList) {
				if (u.getId().equals(userid)) {
					logger.warn("looking up location mapping for user " + u.getId());
					StringBuilder sb = new StringBuilder();
					sb.append("<@").append(u.getId()).append("> is mapped to locationname ");
					String locationname = slackUserToLocationMapping.getLocationNameForSlackUsername(u.getName());
					sb.append(locationname);
					ChatPostMessageMethod postMessage = new ChatPostMessageMethod(json.get("channel").textValue(), sb.toString());
					postMessage.setUnfurl_links(true);
					postMessage.setUsername("woodhouse");
					postMessage.setAs_user(true);
					slackClient.postMessage(postMessage);
					return;
				}
			}
		}
		// @woodhouse get location mappings
		// example message: "<@U1LEHFD1N> get location mappings"
		else if (messageText.equalsIgnoreCase("<@"+ woodhouseUser.getId() +"> get location mappings")) {
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
