package com.sharkbaitextraordinaire.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Notification.NotificationBuilder.class)
public class Notification {
	
	public enum Level {
		URGENT, INTERESTING, UNIMPORTANT
	}
	
	private String origin;
	private String title;
	private String message;
	private Level level;
	
	@JsonPOJOBuilder
	public static class NotificationBuilder {
		private String origin;
		private String title;
		private String message;
		private Level level;
		
		@JsonProperty("origin")
		public NotificationBuilder origin(String origin) { this.origin = origin; return this; }
		@JsonProperty("title")
		public NotificationBuilder title(String title) { this.title = title; return this; }
		@JsonProperty("message")
		public NotificationBuilder message(String message) { this.message = message; return this; }
		@JsonProperty("level")
		public NotificationBuilder level(Level level) { this.level = level; return this; }
		
		public Notification build() {
			if (null == this.level) {
				this.level = Level.INTERESTING;
			}
			return new Notification(this);
		}
	}
	
	private Notification(NotificationBuilder builder) {
		this.origin = builder.origin;
		this.title = builder.title;
		this.message = builder.message;
		this.level = builder.level;
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
	public String getLevel() {
		return this.level.toString();
	}
	public void setLevel(Level level) {
		this.level = level;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!Notification.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		final Notification other = (Notification) obj;
		if ((this.level == null) ? (other.level != null) : !this.level.equals(other.level)) {
			return false;
		}
		if (!this.origin.equals(other.origin) 
				|| !this.title.equals(other.title) 
				|| !this.message.equals(other.message)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 37;
		int result = 3;
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		return result;
	}
}
