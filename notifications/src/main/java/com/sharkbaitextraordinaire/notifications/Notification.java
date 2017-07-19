package com.sharkbaitextraordinaire.notifications;


public class Notification {
	
	private String origin;
	private String title;
	private String message;
	
	public static class NotificationBuilder {
		private String origin;
		private String title;
		private String message;
		
		public NotificationBuilder origin(String origin) { this.origin = origin; return this; }
		public NotificationBuilder title(String title) { this.title = title; return this; }
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
