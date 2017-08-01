package com.sharkbaitextraordinaire.notifications;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class NotificationPusherApplication extends Application<NotificationPusherConfiguration> {

    public static void main(final String[] args) throws Exception {
        new NotificationPusherApplication().run(args);
    }

    @Override
    public String getName() {
        return "NotificationPusher";
    }

    @Override
    public void initialize(final Bootstrap<NotificationPusherConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final NotificationPusherConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
