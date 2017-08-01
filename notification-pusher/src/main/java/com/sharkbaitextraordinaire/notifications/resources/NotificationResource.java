package com.sharkbaitextraordinaire.notifications.resources;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sharkbaitextraordinaire.notifications.core.Notification;

@Path(value="/notifications")
@Produces(MediaType.APPLICATION_JSON)
public class NotificationResource {
	
	private final Logger logger = LoggerFactory.getLogger(NotificationResource.class);

	/**
	 * POST a json-serialized "notification" message to this endpoint in order for it to be processed by this system
	 * The message should have the following format:
	 * origin
	 * title
	 * message
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(@NotNull @Valid Notification notification) {
		// do some work here to pop that onto a transport to send somewhere else
		// and then return that we've accepted it
		logger.warn("accepted a notification: " + notification.getTitle() + " from " + notification.getOrigin());
		return Response.accepted().build();
	}
	
}
