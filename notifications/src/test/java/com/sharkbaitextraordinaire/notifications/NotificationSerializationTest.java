package com.sharkbaitextraordinaire.notifications;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.sharkbaitextraordinaire.notifications.Notification.NotificationBuilder;
import com.sharkbaitextraordinaire.notifications.Notification.Level;

public class NotificationSerializationTest {
	
	private static ObjectMapper mapper = new ObjectMapper();
	private final String expectedDefaultLevelJson = "{\"origin\":\"test\",\"title\":\"test\",\"message\":\"test message, default level\",\"level\":\"INTERESTING\"}";
	private final String expectedExplicitLevelJson = "{\"origin\":\"test\",\"title\":\"title\",\"message\":\"test message, explicit level\",\"level\":\"URGENT\"}";
	private final String expectedNoLevelJson = "{\"origin\":\"test\",\"title\":\"test\",\"message\":\"test message, no level\"}";
	

	@Test
	public void TestSerializeNotificationWithDefaultLevel() throws Exception {
		Notification.NotificationBuilder b = new NotificationBuilder();
		Notification notificationWithDefaultLevel = b.origin("test")
				.title("test")
				.message("test message, default level")
				.build();
		
		assertThat(mapper.writeValueAsString(notificationWithDefaultLevel)).isEqualTo(expectedDefaultLevelJson);
	}
	
	@Test
	public void TestSerializeNotificationWithExplicitLevel()  throws Exception {
		Notification.NotificationBuilder b = new NotificationBuilder();
		Notification notificationWithExplicitLevel = b.origin("test")
				.title("title")
				.message("test message, explicit level")
				.level(Level.URGENT)
				.build();
		
		assertThat(mapper.writeValueAsString(notificationWithExplicitLevel)).isEqualTo(expectedExplicitLevelJson);
	}
	
	@Test
	public void TestDeserializeNotificationWithDefaultLevel() throws Exception {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Notification n = mapper.readValue(expectedNoLevelJson, Notification.class);
		
		NotificationBuilder nb = new NotificationBuilder();
		nb.origin("test").title("test").message("test message, no level");
		Notification defaultLevelNotification = nb.build();
		
		assertNotNull(n);
		assertThat(n).isEqualTo(defaultLevelNotification);
	}
	
	@Test
	public void TestDeserializeNotificationWithExplicitLevel() throws Exception {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Notification n = mapper.readValue(expectedExplicitLevelJson, Notification.class);
		
		assertNotNull(n);
		assertThat(mapper.writeValueAsString(n)).isEqualTo(expectedExplicitLevelJson);
	}
}
