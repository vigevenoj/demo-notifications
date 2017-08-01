This was originally a project to see how to do different things using Spring Boot as compared to Dropwizard, and then I started breaking the project up into different services.

Currently there is an earthquake analysis service that determines if any of the earthquakes from the USGS feed are of a large enough magnitude and close enough to either a pre-configured location or a location provided via connection to an MQTT broker with Owntracks topics. If the earthquakes are "interesting" or "worrisome", the service posts a notification about the earthquake to a service that sends out some notifications.

See the [readme in quakes](quakes/README.md) for more information about the earthquake service

See the readme in [notification-pusher/readme.md](notification-pusher/README.md) for more information about the 
