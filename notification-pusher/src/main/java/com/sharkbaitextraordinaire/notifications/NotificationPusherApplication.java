package com.sharkbaitextraordinaire.notifications;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import com.sharkbaitextraordinaire.notifications.client.SlackIntegration;
import com.sharkbaitextraordinaire.notifications.resources.NotificationResource;

import io.dropwizard.Application;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class NotificationPusherApplication extends Application<NotificationPusherConfiguration> {

    public static void main(final String[] args) throws Exception {
        new NotificationPusherApplication().run(args);
    }
    
    // multiple-producer, single-consumer.
    private LinkedBlockingQueue<Notification> notifications;

    @Override
    public String getName() {
        return "NotificationPusher";
    }

    @Override
    public void initialize(final Bootstrap<NotificationPusherConfiguration> bootstrap) {
        notifications = new LinkedBlockingQueue<Notification>();
    }

    @Override
    public void run(final NotificationPusherConfiguration configuration,
                    final Environment environment) {
    	SlackConfiguration slackConfig = configuration.getSlackConfiguration();
    	ExecutorService notificationSendingService = environment.lifecycle().executorService("notification-sender").maxThreads(1).minThreads(1).build();
    	SlackIntegration slackIntegration = new SlackIntegration(slackConfig, notifications);
    	notificationSendingService.submit(slackIntegration);
    	
    	environment.jersey().register(new NotificationResource(notifications));
    }

}
