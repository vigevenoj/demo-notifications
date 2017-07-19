package com.sharkbaitextraordinaire.bootnotifier;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Component;

import com.sharkbaitextraordinaire.bootnotifier.config.AppConfig;
import com.sharkbaitextraordinaire.bootnotifier.config.EarthquakeAnalysisConfig;
import com.sharkbaitextraordinaire.bootnotifier.dao.LocationUpdateDAO;
import com.sharkbaitextraordinaire.bootnotifier.dao.MonitoredLocationDAO;
import com.sharkbaitextraordinaire.bootnotifier.model.Earthquake;
import com.sharkbaitextraordinaire.bootnotifier.model.LocationUpdate;
import com.sharkbaitextraordinaire.bootnotifier.model.MonitorableLocation;
import com.sharkbaitextraordinaire.notifications.Notification;

@Component
public class EarthquakeAnalyzer {

	private ExecutorService executorService;
	private final Logger logger = LoggerFactory.getLogger(EarthquakeAnalyzer.class);

	@Autowired 
	LocationUpdateDAO locationDao;
	@Autowired
	private MonitoredLocationDAO monitoredLocationDao;
	@Autowired private AppConfig appConfig;
	@Autowired private EarthquakeAnalysisConfig analysisConfig;
	private LinkedBlockingQueue<Earthquake> queue;
	private LinkedBlockingQueue<Notification> outboundNotifications;

	private NumberFormat nf = NumberFormat.getIntegerInstance();
	private volatile boolean stopThread = false;
	private final CounterService counterService;

	@Autowired
	public EarthquakeAnalyzer(CounterService counterService) {
		this.counterService = counterService;
	}

	@PostConstruct
	public void init() {

		queue = appConfig.earthquakeQueue();
		outboundNotifications = appConfig.notificationQueue();

		executorService = Executors.newSingleThreadExecutor();
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				doTask();
			}
		});
		executorService.shutdown();
	}

	private void doTask() {
		logger.warn("Starting earthquake analysis thread with " 
				+ queue.size() 
				+ " quakes queued for analysis.");
		if (queue.isEmpty()) {
			try {
				logger.warn("Sleeping for five seconds because earthquake queue is empty...");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		while (!stopThread) {
			try {
				Earthquake quake = queue.take();
				logger.debug("Took an earthquake from the queue: " + quake.getId() + ": " + quake.getTitle());
				if (quake.getId() == null) {
					logger.error("Took a quake without an ID");
					logger.debug("Queue size is " + queue.size());
					continue;
				}
				LocationUpdate location = locationDao.findLatest();
				HashSet<MonitorableLocation> monitoredAndUpdatedLocations = new HashSet<MonitorableLocation>();
				monitoredAndUpdatedLocations.add(location);
				monitoredAndUpdatedLocations.addAll(monitoredLocationDao.findAll());

				MonitorableLocation closestLocation = determineClosestLocationToEarthquake(quake, monitoredAndUpdatedLocations);
				analyzeQuake(quake, closestLocation);
			} catch (InterruptedException e) {
				logger.error("Interrupted while taking earthquake from queue");
				// TODO set stopThread shutdown check to true?
			} catch (NullPointerException e) {
				// Most frequently because the latest location update is null
				// because we haven't gotten an update from the mqtt broker yet
			} catch (Exception e) {
				logger.error("some kind of problem", e);
			}
		}
	}

	private void analyzeQuake(Earthquake quake, MonitorableLocation location) {
		double distance = Haversine.distance(quake.getLocation(), location.getLocation());

		counterService.increment("counter.earthquakes.processed.total");
		if (distance <= analysisConfig.getWorryDistanceThreshold()) { 
			// send notification
			logger.error(quake.getTitle() + " is within WORRY threshold at " + nf.format(distance) + "km");
			counterService.increment("earthquakes.processed.worrisome.total");
			Notification notification = new Notification.NotificationBuilder().origin("quakes").title(quake.getTitle())
					.message("Worrisome " + quake.getTitle() + " is " + nf.format(distance) + " km from " + location.getName() + 
							". For more details see <" + quake.getUrl() + ">").build();
			try {
				outboundNotifications.put(notification);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (distance <= analysisConfig.getInterestDistanceThreshold()) { 
			logger.error(quake.getTitle() + " is not worrisome but is interesting at " 
					+ nf.format(distance) + "km. ID " + quake.getId() + ": " + quake.getUrl());
			counterService.increment("counter.earthquakes.processed.interesting.total");
			Notification notification = new Notification.NotificationBuilder().origin("quakes").title(quake.getTitle())
			.message("Interesting " + quake.getTitle() + " is " + nf.format(distance) + "km from " 
					+ location.getName() + ". For more details, see <" + quake.getUrl() + ">").build();
			try {
				outboundNotifications.put(notification);
			} catch (InterruptedException e) {
				
			}
			
		} else {
			// No-op, don't send a notification for quakes that are neither worrisome nor interesting
			counterService.increment("counter.earthquakes.processed.boring.total");
		}
	}


	private MonitorableLocation determineClosestLocationToEarthquake(Earthquake quake, Collection<MonitorableLocation> locations) {
		MonitorableLocation closest = null;
		double closestDistance = Double.MAX_VALUE;
		for (MonitorableLocation location : locations) {
			double distance = Haversine.distance(quake.getLocation(), location.getLocation());
			if (distance < closestDistance) {
				closest = location;
				closestDistance = distance;
			}
		}
		return closest;
	}


	@PreDestroy
	public void beandestroy() {
		this.stopThread = true;

		if(executorService != null){
			try {
				// wait 1 second for closing all threads
				executorService.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
