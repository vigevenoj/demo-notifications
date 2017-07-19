This is a demo application I put together for me to compare how to do things in Spring boot vs Dropwizard. For the Dropwizard example, see [https://github.com/vigevenoj/quakes]. This application shares most of the requirements with the ones listed in [https://github.com/vigevenoj/quakes/blob/master/README.md]

Requirements
---
1. Location updates
  * [Owntracks](http://owntracks.org) on your phone
  * An MQTT broker you can access from wherever you run this application
1. [Pushover](https://pushover.net) notifications
  * Pushover account
  * Pushover user token
  * Define a pushover application for use with this application
1. Multnomah County bridge status
  * Request access to the bridge lift API at https://multco.us/it/webform/request-access-bridges-public-api 
  * Read the [documentation](https://api.multco.us/bridges/docs) about how the bridge lift API works
1. Slack integration
  * Team name
  * API token
  * Channel name for earthquake updates
1. Philips Hue integration
  * Bridge address
  * Username, which will need to be generated separately
  * Name of the application (this should go away in a future update)
  * Name of the device (this should go away in a future update)

How to start the application
---
1. Clone the repository `git clone https://github.com/vigevenoj/springboot-demo-notifications.git`
1. Run `mvn clean install` to install dependencies
1. Edit the configuration file or add environment variables to provide the application configuration
1. Generate a jar or use the Spring Boot Maven Plugin
  * To generate a jar and use that to run,
    * Run `mvn clean package` to generate a jar
    * If using a packaged jar, `java -jar target/springboot-demo-0.0.1-SNAPSHOT.jar -Dspring.config.name="file:/path/to/application.properties"
  * To use the Spring Boot Maven Plugin
    * Run `mvn spring-boot:run -Dspring.config.location=your.properties`


Health Checks
---
Currently unimplemented

Behavior while runing
---


To Do
---
* Re-implement hue-slack integration from dropwizard project
* Re-implement pushover notifications from dropwizard project
* Refactor Multnomah bridge lift integration into separate shared library
* Prune out-of-date location and earthquake data instead of persisting them forever
