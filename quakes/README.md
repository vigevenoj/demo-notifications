This is a demo application I put together for me to compare how to do things in Spring boot vs Dropwizard. For the Dropwizard example, see [https://github.com/vigevenoj/quakes]. This application shares most of the requirements with the ones listed in [https://github.com/vigevenoj/quakes/blob/master/README.md]

Requirements
---
1. Location updates
  * [Owntracks](http://owntracks.org) on your phone
  * An MQTT broker you can access from wherever you run this application
1. A web service that accepts POSTed JSON in the form '{"origin": "", "title": "", "message": "" }'
1. A certificate authority in order to generate the following certificates
  * A CA certificate that both this project and the "destination" accept
  * A certificate to for the destination service to use
    * The CA certificate and destination certificate and key need to be packaged into a keystore for this service to trust them
  * A certificate for this project to use as a client certifiicate for authentication
    * The CA certificate, client certificate and client key will need to be packaged into a keystore for this service to provide them to the destination service

How to start the application
---
1. Clone the repository `git clone https://github.com/vigevenoj/springboot-demo-notifications.git`
1. Run `mvn clean install` to install dependencies
1. Edit the configuration file or add environment variables to provide the application configuration
1. From the top-level of the project, `mvn install && mvn spring-boot:run -Dspring.config.location=./quakes/application.properties -pl quakes`
1. Generate a jar or use the Spring Boot Maven Plugin
  * To generate a jar and use that to run,
    * Run `mvn clean package` to generate a jar
    * If using a packaged jar, `java -jar target/springboot-demo-0.0.1-SNAPSHOT.jar -Dspring.config.name="file:/path/to/application.properties"
  * To use the Spring Boot Maven Plugin
    * Run `mvn spring-boot:run -Dspring.config.location=your.properties`


Health Checks
---
* The connection to the MQTT broker is monitored, and if lost, the health check will fail while the application attempts to reconnect

Behavior while runing
---
* Fetching the USGS feed logs a message and increases a counter
* Each distinct earthquake seen in the feed is logged and increases a counter
* Each earthquake that is interesting or worrisome will increase a counter and be logged

To Do
---
* Healthchecks
  * Mark the earthquake-fetching component as unhealthy if there are any errors when requesting the USGS feed or while processing the feed itself
* Prune out-of-date location and earthquake data instead of persisting them forever
