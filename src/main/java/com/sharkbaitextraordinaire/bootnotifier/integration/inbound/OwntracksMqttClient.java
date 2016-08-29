package com.sharkbaitextraordinaire.bootnotifier.integration.inbound;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharkbaitextraordinaire.bootnotifier.config.OwntracksConfig;
import com.sharkbaitextraordinaire.bootnotifier.dao.LocationUpdateDAO;
import com.sharkbaitextraordinaire.bootnotifier.model.LocationUpdate;

@Component
public class OwntracksMqttClient implements CommandLineRunner, MqttCallback {

	private final Logger logger = LoggerFactory.getLogger(OwntracksMqttClient.class);
	
	@Autowired
	private LocationUpdateDAO dao;
	@Autowired
	private OwntracksConfig config;
	MqttClient client;
	MqttConnectOptions connectionOptions;
	ObjectMapper mapper;
	
	public OwntracksMqttClient() {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	@Override
	public void run(String... arg0) throws Exception {
		String clientID = config.getClientID();
		String brokerUrl = config.getBrokerUrl();
		
		connectionOptions = new MqttConnectOptions();
		connectionOptions.setKeepAliveInterval(120);
		connectionOptions.setUserName(config.getUserName());
		connectionOptions.setPassword(config.getPassword().toCharArray());
		
		try {
			InputStream trustStoreInput = Thread.currentThread().getContextClassLoader().getResourceAsStream(config.getTrustStore());
			if (null == trustStoreInput) {
				logger.error("Owntracks client trust store input is null");
			}
			setSSLFactories(trustStoreInput);
			trustStoreInput.close();
			
			connectionOptions.setSocketFactory(SSLContext.getDefault().getSocketFactory());
		} catch (NoSuchAlgorithmException e) {
			logger.error("No such algorithm exception", e);
		} catch (KeyManagementException e) {
			logger.error("Key management exception", e);
		} catch (CertificateException e) {
			logger.error("Certificate exception", e);
		} catch (IOException e) {
			logger.error("Problem doing IO during Owntracks MQTT setup", e);
		}
		
		try {
			client = new MqttClient(brokerUrl, clientID);
			client.setCallback(this);
			client.connect(connectionOptions);
			
			if (client.isConnected()) {
				logger.error("Connected to MQTT broker for Owntracks location updates");
//				MqttTopic topic = client.getTopic(config.getTopic());
				
				int subQoS = 0;
				client.subscribe(config.getTopic(), subQoS);
			} else {
				logger.error("NOT CONNECTED to mqtt broker");
			}
		} catch (MqttException e) {
			logger.error("Exception while subscribing to location update topic", e);
		}
	}
	
	@PreDestroy
	public void shutDown() {
		try {
			client.disconnect();
			client.close();
		} catch (MqttException e) {
			logger.info(e.getMessage());
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		logger.error("Connection to MQTT broker was lost");
		logger.info(cause.getMessage());
		try {
			logger.warn("Reconnecting to MQTT broker for location updates");
			client.connect(connectionOptions);
		} catch (MqttException e) {
			logger.error("Failed to reconnect to broker");
			logger.debug(e.getMessage());
		}
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String payload = new String(message.getPayload());
		logger.info("New message from owntracks mqtt broker:");
		logger.info(payload);
		logger.info("topic: " + topic);
		
		try {
			LocationUpdate update = mapper.readValue(payload, LocationUpdate.class);
			logger.info(update.toString());
			update.setName(topic);
			
			dao.insert(update);
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// no-op because we never post any updates
	}
	
	private void setSSLFactories(InputStream trustStream) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException {
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		char[] trustStorePassword = null; 
		if (null == config.getTrustStorePassword() 
				|| "".equals(config.getTrustStorePassword())
				|| config.getTrustStorePassword().isEmpty()) {
			trustStorePassword = null;
		} else {
			config.getTrustStorePassword().toCharArray();
		}
		trustStore.load(trustStream, trustStorePassword);
		
		TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustFactory.init(trustStore);
		
		TrustManager[] trustManagers = trustFactory.getTrustManagers();
		
		SSLContext sslContext = SSLContext.getInstance("SSL"); // TODO is this right?
		sslContext.init(null, trustManagers, null);
		SSLContext.setDefault(sslContext);
	}

}
