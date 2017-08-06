package com.sharkbaitextraordinaire.notifications.client;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sharkbaitextraordinaire.notifications.Notification;
import com.sharkbaitextraordinaire.notifications.SlackConfiguration;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

// TODO we should probably rename this class to something sensible once we have additional notification methods
// ie, pushover and whatever else.
public class SlackIntegration implements Runnable {
	
	private SlackConfiguration slackConfig;
	private SlackSession session ;
	private SlackChannel channel;
	private LinkedBlockingQueue<Notification> notifications;
	private Logger logger = LoggerFactory.getLogger(SlackIntegration.class);
	
	public SlackIntegration(SlackConfiguration slackConfig, LinkedBlockingQueue<Notification> notifications) {
		this.slackConfig = slackConfig;
		this.notifications = notifications;
	}

	public void run() {
		logger.warn("Starting notification processor with " + notifications.size() + " pending notifications");
		if (notifications.isEmpty()) {
			try {
				logger.warn("Notification queue is empty as expected during startup, sleeping for five seconds");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				logger.debug(e.getMessage());
			}
		}
		
		
		try {
			session = SlackSessionFactory.createWebSocketSlackSession(slackConfig.getToken());
			session.connect();
			channel = session.findChannelByName(slackConfig.getChannelName());
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
		}
		
		while (true) {
			try {
				Notification notification = notifications.take();
				logger.debug("Took a notification from the queue: " + notification.getOrigin() + ": " + notification.getTitle());
				
				session.sendMessage(channel, notification.getMessage());
				
			} catch (InterruptedException e) {
				logger.error("Interrupted while taking notification from queue");
			}
		}
	}
}
