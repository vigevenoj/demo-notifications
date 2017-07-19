package com.sharkbaitextraordinaire.bootnotifier.integration.outbound;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharkbaitextraordinaire.bootnotifier.config.AppConfig;
import com.sharkbaitextraordinaire.bootnotifier.config.NotificationSenderConfig;
import com.sharkbaitextraordinaire.notifications.Notification;


public class NotificationSender {

	private LinkedBlockingQueue<Notification> notifications;
	private ExecutorService executorService;
	private final Logger logger = LoggerFactory.getLogger(NotificationSender.class);
	private final CounterService counterService;
	@Autowired private AppConfig appConfig;
	@Autowired private NotificationSenderConfig config;
	private volatile boolean stopThread = false;
	private ObjectMapper mapper;
	
	private HttpClient httpClient; 
	
	@Autowired
	public NotificationSender(CounterService counterService) {
		this.counterService = counterService;
	}
	
	@PostConstruct
	public void init() {
		notifications = appConfig.notificationQueue();
		mapper = new ObjectMapper();
		
		try {
			httpClient = httpClient();
		} catch (Exception e) {
			logger.error("Error while configuring outbound notification http client: " + e.getMessage());
		}
		
		executorService = Executors.newSingleThreadExecutor();
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				processQueue();
			}
		});
		executorService.shutdown();
	}
	
	private void processQueue() {
		
		if (notifications.isEmpty()) {
			try {
				logger.warn("Sleeping for five seconds because earthquake queue is empty...");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while (!stopThread) {
			
			try {
				Notification notification = notifications.take();
				counterService.increment("counter.notifications.taken");
				
				HttpPost post = new HttpPost(config.getEndpoint());
				post.setEntity(new StringEntity(mapper.writeValueAsString(notification)));
				post.setHeader("Content-type", "application/json");
				HttpResponse response = httpClient.execute(post);
				
				// TODO better error handling
				logger.info(response.getStatusLine().toString());
			} catch (InterruptedException | IOException e) {
				logger.error("Had a problem sending notification " + e.getMessage());
			} finally {
				
			}
		}
	}
	
	private CloseableHttpClient httpClient() throws Exception {
		File truststoreFile = new File(config.getTrustStorePath());
		File keystoreFile = new File(config.getKeystorePath());
		SSLContext sslContext = SSLContexts.custom()
				.loadTrustMaterial(truststoreFile, 
						config.getTrustStorePassword().toCharArray())
				.loadKeyMaterial(keystoreFile, 
						config.getKeystorePassword().toCharArray(), 
						config.getKeyPassword().toCharArray()).build();
		SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"TLSv1.2"},
                null,
                new NoopHostnameVerifier() // For testing only
//                SSLConnectionSocketFactory.getDefaultHostnameVerifier()
                );
        return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
	}
}
