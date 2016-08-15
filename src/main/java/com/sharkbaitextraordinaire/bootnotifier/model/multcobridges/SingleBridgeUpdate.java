package com.sharkbaitextraordinaire.bootnotifier.model.multcobridges;

import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SingleBridgeUpdate {
  @JsonProperty("status")
  private Boolean status;
  @JsonProperty("scheduledLifts")
  private ArrayList<BridgeLift> scheduledLifts;
  @JsonProperty("lastFive")
  private ArrayList<BridgeLift> lastFive;
  @JsonProperty("upTime")
  private Date upTime;


  public SingleBridgeUpdate() {}

  public Boolean getStatus() {
    return this.status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public ArrayList<BridgeLift> getScheduledLifts() {
    return this.scheduledLifts;
  }

  public void setScheduledLifts(ArrayList<BridgeLift> scheduledLifts) {
    this.scheduledLifts = scheduledLifts;
  }

  public ArrayList<BridgeLift> getLastFive() {
    return this.lastFive;
  }

  public void setLastFive(ArrayList<BridgeLift> lastFive) {
    this.lastFive = lastFive;
  }
  
  public Date getUpTime() {
	  return this.upTime;
  }
  
  public void setUpTime(Date upTime) {
	  this.upTime = upTime;
  }
}
