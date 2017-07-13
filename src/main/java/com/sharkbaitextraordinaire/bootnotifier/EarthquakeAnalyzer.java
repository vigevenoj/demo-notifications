package com.sharkbaitextraordinaire.bootnotifier;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Component;

import com.sharkbaitextraordinaire.bootnotifier.config.AppConfig;
import com.sharkbaitextraordinaire.bootnotifier.config.EarthquakeAnalysisConfig;
import com.sharkbaitextraordinaire.bootnotifier.config.PushoverConfig;
import com.sharkbaitextraordinaire.bootnotifier.config.SlackConfig;
import com.sharkbaitextraordinaire.bootnotifier.dao.LocationUpdateDAO;
import com.sharkbaitextraordinaire.bootnotifier.dao.MonitoredLocationDAO;
import com.sharkbaitextraordinaire.bootnotifier.model.Earthquake;
import com.sharkbaitextraordinaire.bootnotifier.model.LocationUpdate;
import com.sharkbaitextraordinaire.bootnotifier.model.MonitorableLocation;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.Channel;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;


@Component
public class EarthquakeAnalyzer {

	private ExecutorService executorService;
	private final Logger logger = LoggerFactory.getLogger(EarthquakeAnalyzer.class);

	@Autowired
	private SlackConfig slackConfig;
	@Autowired
	private PushoverConfig pushoverConfig; // TODO implement pushover setup
	@Autowired 
	LocationUpdateDAO locationDao;
	@Autowired
	private MonitoredLocationDAO monitoredLocationDao;
	@Autowired private AppConfig appConfig;
	@Autowired private EarthquakeAnalysisConfig analysisConfig;
	private LinkedBlockingQueue<Earthquake> queue;

	private Channel slackChannel;
	private SlackWebApiClient slackClient;
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
				logger.warn("Sleeping for five seconds at startup because earthquake queue is empty...");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		setUpSlack();

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
			// TODO send pushover notification
			postToSlack("Worrisome " + quake.getTitle() + " is " + nf.format(distance) + "km from " + location.getName()
			+ ". For more details see<" + quake.getUrl() + ">");
		} else if (distance <= analysisConfig.getInterestDistanceThreshold()) { 
			logger.error(quake.getTitle() + " is not worrisome but is interesting at " 
					+ nf.format(distance) + "km. ID " + quake.getId() + ": " + quake.getUrl());
			postToSlack("Interesting " + quake.getTitle() + " is " + nf.format(distance) + "km from " 
					+ location.getName() + ". For more details, see <" + quake.getUrl() + ">"); 
			counterService.increment("counter.earthquakes.processed.interesting.total");
		} else {
			// No-op, don't send a notification for quakes that are neither worrisome nor interesting
			counterService.increment("counter.earthquakes.processed.boring.total");
		}
	}


	private void setUpSlack() {
		String token = slackConfig.getToken();
		String channelName = slackConfig.getChannelName();
		slackClient = SlackClientFactory.createWebApiClient(token);
		slackClient.auth();

		logger.debug("looking for slack channel named " + channelName);

		slackChannel = slackClient.getChannelList().stream()
				.filter(c -> c.getName().equals(channelName))
				.collect(singletonCollector());
		logger.warn("Using channel " + slackChannel.getName() + " with ID " + slackChannel.getId());
	}
	
	private String postToSlack(String message) {
		ChatPostMessageMethod postMessage = new ChatPostMessageMethod(slackChannel.getId(), message);
		postMessage.setUnfurl_links(true);
		postMessage.setUsername("woodhouse");
		postMessage.setAs_user(true);
		
		String ts = slackClient.postMessage(postMessage);
		logger.warn("response from slack post message: " + ts);
		return ts;
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

	private static <T> Collector<T, ?, T> singletonCollector() {
		return Collectors.collectingAndThen(
				Collectors.toList(),
				list -> {
					if (list.size() != 1) {
						throw new IllegalStateException();
					}
					return list.get(0);
				}
				);
	}
}
