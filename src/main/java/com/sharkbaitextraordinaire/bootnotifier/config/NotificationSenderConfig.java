package com.sharkbaitextraordinaire.bootnotifier.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="notifications")
public class NotificationSenderConfig {

	private String trustStorePath;
	private String trustStorePassword;
	private String keystorePath;
	private String keystorePassword;
	private String keyPassword;
	private String endpoint;
	private int requestTimeout;
	
	public String getTrustStorePath() {
		return trustStorePath;
	}
	public void setTrustStorePath(String trustStorePath) {
		this.trustStorePath = trustStorePath;
	}
	public String getTrustStorePassword() {
		return trustStorePassword;
	}
	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}
	public String getKeystorePath() {
		return keystorePath;
	}
	public void setKeystorePath(String keystorePath) {
		this.keystorePath = keystorePath;
	}
	public String getKeystorePassword() {
		return keystorePassword;
	}
	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}
	public String getKeyPassword() {
		return keyPassword;
	}
	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public int getRequestTimeout() {
		return requestTimeout;
	}
	public void setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
	}
}
