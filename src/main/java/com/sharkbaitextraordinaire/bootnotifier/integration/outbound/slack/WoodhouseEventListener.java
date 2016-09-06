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
import com.sharkbaitextraordinaire.bootnotifier.dao.LocationUpdateDAO;
import com.sharkbaitextraordinaire.bootnotifier.dao.SlackUserToLocationNameMappingDAO;
import com.sharkbaitextraordinaire.bootnotifier.model.LocationUpdate;

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
	@Autowired LocationUpdateDAO locationUpdateDAO;
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

	private boolean checkForWoodhouseMention(String text) {
		Matcher m = WOODHOUSE_MENTION_PATTERN.matcher(text);
		return (m.matches() || text.startsWith("<@"+woodhouseUser.getId()+">"));
	}
	
	void handleWoodhouseMention(JsonNode json) {
		String messageText = json.get("text").textValue();
		 
		if (messageText.startsWith("<@"+woodhouseUser.getId()+"> set location mapping for ")) {
			// @woodhouse use location "" for @slackuser
			// @woodhouse set location mapping for @slackuser to foo
			// example message: "<@U1LEHFD1N> set location mapping for <@U1LAMGD4N> to /owntracks/jacob/nexus5"
			logger.error("setting location mapping for user");
			
			// parse target userID and location name
			String[] message = messageText.split("set location mapping for "); // TODO bounds check this array
			String userid = message[1].substring(2, message[1].lastIndexOf(">"));
			String locationname = messageText.split("<@[A-Z0-9]*?> to")[1]; // TODO bounds check this

			// look up user's name
			if (userList.stream().anyMatch(u -> u.getId().equals(userid))) {
				for (User u : userList) {
					if (u.getId().equals(userid)) {
						slackUserToLocationMapping.addMapping(u.getName(), locationname.trim());
						StringBuilder reply = new StringBuilder();
						reply.append("Using '").append(locationname.trim())
						.append("' for location updates for ")
						.append("<@").append(u.getId()).append(">");
						
						replyToRequest(json.get("channel").textValue(), reply.toString());
						return;
					}
				}
			} else {
				logger.warn("looked up a user who doesn't exist");
				replyToRequest(json.get("channel").textValue(), "That user doesn't exist");
			}
			return;
		} else if (messageText.startsWith("<@"+woodhouseUser.getId()+"> get location mapping for ")) {
			// @woodhouse get locationname for @slackuser
			// example message: "<@U1LEHFD1N> get location mapping for <@U1LAMGD4N>"
			logger.info("getting location mapping for user");
			
			// parse target userID
			String[] message = messageText.split(" get location mapping for "); // TODO bounds checking this array
			String userid = parseUserIdFromMention(message[1]);
			if (userList.stream().anyMatch(u -> u.getId().equals(userid))) {
				for (User u : userList) {
					if (u.getId().equals(userid)) {
						logger.warn("looking up location mapping for user " + u.getId());
						StringBuilder reply = new StringBuilder();
						reply.append("<@").append(u.getId()).append("> is mapped to locationname ");
						String locationname = slackUserToLocationMapping.getLocationNameForSlackUsername(u.getName());
						reply.append(locationname);
						
						replyToRequest(json.get("channel").textValue(), reply.toString());
						return;
					}
				}
			}
		} else if (messageText.equalsIgnoreCase("<@"+ woodhouseUser.getId() +"> get location mappings")) {
			// @woodhouse get location mappings
			// example message: "<@U1LEHFD1N> get location mappings"
			Map<String,String> mappings = slackUserToLocationMapping.getAllMappings();
			StringBuilder reply = new StringBuilder();
			for (String key : mappings.keySet()) {
				logger.warn(key + "-->" + mappings.get(key));
				reply.append(key).append(": ").append(mappings.get(key)).append('\n');
			}
			logger.warn(reply.toString());
			
			replyToRequest(json.get("channel").textValue(), reply.toString());
		} else if (messageText.startsWith("<@"+woodhouseUser.getId()+"> where is <@")) {
			// @woodhouse where is @jacob
			// example message: "<@U1LEHFD1N> where is <@U1LAMGD4N>"
			String userid = parseUserIdFromMention(messageText.split(" where is ")[1]);
			if (userList.stream().anyMatch(u -> u.getId().equals(userid))) {
				for (User u : userList) {
					if (u.getId().equals(userid)) {
						String locationname = slackUserToLocationMapping.getLocationNameForSlackUsername(u.getName());
						if (locationname == null) {
							String reply = "No location mapping set up for that user";
							replyToRequest(json.get("channel").textValue(), reply);
							return;
						} else {
							try {
								logger.warn("looking up latest location update for '" + locationname + "'");
								LocationUpdate latest = locationUpdateDAO.latestForUser(locationname);
								StringBuilder reply = new StringBuilder();
								reply.append("<@").append(u.getId()).append("> was at ")
								.append(latest.getLocation().toString()).append(" at ")
								.append(latest.getTimestamp());
	
								replyToRequest(json.get("channel").textValue(), reply.toString());
								return;
							} catch (org.springframework.dao.EmptyResultDataAccessException e) {
								logger.warn("No location updates for " + u.getName() + " using location named " + locationname);
								String reply = "There are no location updates for <@"+u.getId()+">";
								replyToRequest(json.get("channel").textValue(), reply);
								return;
							}
						}
					}
				}
			} else {
				replyToRequest(json.get("channel").textValue(), "That user doesn't exist");
			}
		}
	}
	
	private String parseUserIdFromMention(String mentiontext) {
		if (mentiontext.lastIndexOf("<@") == -1 || mentiontext.lastIndexOf(">") == -1) { return ""; }
		return mentiontext.substring(2, mentiontext.length()-1);
	}
	
	private String replyToRequest(String channel, String reply) {
		ChatPostMessageMethod postMessage = new ChatPostMessageMethod(channel, reply);
		postMessage.setUnfurl_links(true);
		postMessage.setUsername("woodhouse");
		postMessage.setAs_user(true);
		return slackClient.postMessage(postMessage);
	}
}
