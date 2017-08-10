package com.sharkbaitextraordinaire.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Notification.NotificationBuilder.class)
public class Notification {
	
	private String origin;
	private String title;
	private String message;
	
	@JsonPOJOBuilder
	public static class NotificationBuilder {
		private String origin;
		private String title;
		private String message;
		
		@JsonProperty("origin")
		public NotificationBuilder origin(String origin) { this.origin = origin; return this; }
		@JsonProperty("title")
		public NotificationBuilder title(String title) { this.title = title; return this; }
		@JsonProperty("message")
		public NotificationBuilder message(String message) { this.message = message; return this; }
		
		public Notification build() {
			return new Notification(this);
		}
	}
	
	private Notification(NotificationBuilder builder) {
		this.origin = builder.origin;
		this.title = builder.title;
		this.message = builder.message;
	}
	
	public Notification() {}
	
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
