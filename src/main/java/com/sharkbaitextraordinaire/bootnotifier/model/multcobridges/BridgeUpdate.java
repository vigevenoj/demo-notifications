package com.sharkbaitextraordinaire.bootnotifier.model.multcobridges;

import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BridgeUpdate {

	public static final String BROADWAY = "broadway";
	public static final String BURNSIDE = "burnside";
	public static final String MORRISON = "morrison";
	public static final String HAWTHORNE = "hawthorne";
	private static final String CHANGED = "changed";

	@JsonProperty(CHANGED)
	private Map<String, String> changed;
	@JsonProperty(HAWTHORNE)
	private SingleBridgeUpdate hawthorne;
	@JsonProperty(MORRISON)
	private SingleBridgeUpdate morrison;
	@JsonProperty(BURNSIDE)
	private SingleBridgeUpdate burnside;
	@JsonProperty(BROADWAY)
	private SingleBridgeUpdate broadway;

	// Convenience object might not be necessary
	private Map<String, SingleBridgeUpdate> bridgeUpdates;

	public BridgeUpdate(Map<String, Object> json) {
		this.changed = (Map<String, String>) json.get(CHANGED);
		this.bridgeUpdates = (Map<String, SingleBridgeUpdate>) json.remove(CHANGED);
	}

	public BridgeUpdate() {
		this.bridgeUpdates = new HashMap<String,SingleBridgeUpdate>();
	}

	public String getChangedBridge() {
		return changed.get("bridge");
	}

	public String getChangedItem() {
		return changed.get("item"); // this is hopefully "status"
	}

	// public SingleBridgeUpdate getBridge(String bridgeName) {
	// return bridgeUpdates.get(bridgeName);
	// }

	public Map<String, String> getChanged() {
		return this.changed;
	}

	public void setChanged(Map<String, String> changed) {
		this.changed = changed;
	}

	public Map<String, SingleBridgeUpdate> getBridgeUpdates() {
		return this.bridgeUpdates;
	}

	public void setBridgeUpdates(Map<String, SingleBridgeUpdate> bridgeUpdates) {
		this.bridgeUpdates = bridgeUpdates;
	}

	public SingleBridgeUpdate getHawthorne() {
		return hawthorne;
	}

	public void setHawthorne(SingleBridgeUpdate hawthorne) {
		this.hawthorne = hawthorne;
		this.bridgeUpdates.put(HAWTHORNE, hawthorne);
	}

	public SingleBridgeUpdate getMorrison() {
		return morrison;
	}

	public void setMorrison(SingleBridgeUpdate morrison) {
		this.morrison = morrison;
		this.bridgeUpdates.put(MORRISON, morrison);
	}

	public SingleBridgeUpdate getBurnside() {
		return burnside;
	}

	public void setBurnside(SingleBridgeUpdate burnside) {
		this.burnside = burnside;
		this.bridgeUpdates.put(BURNSIDE, burnside);
	}

	public SingleBridgeUpdate getBroadway() {
		return broadway;
	}

	public void setBroadway(SingleBridgeUpdate broadway) {
		this.broadway = broadway;
		this.bridgeUpdates.put(BROADWAY, broadway);
	}
}