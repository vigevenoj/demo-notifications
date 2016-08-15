package com.sharkbaitextraordinaire.bootnotifier.model.multcobridges;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BridgeLift {
  @JsonProperty("id")
  private int id;
  @JsonProperty("bridgeId")
  private int bridgeId;
  @JsonProperty("upTime")
  private Date upTime;
  @JsonProperty("downTime")
  private Date downTime;
  @JsonProperty("createdAt")
  private Date createdAt;
  @JsonProperty("updatedAt")
  private Date updatedAt;


  public BridgeLift() {}

  public BridgeLift(int id, int bridgeId, Date upTime, Date downTime, Date createdAt, Date updatedAt) {
    this.id = id;
    this.bridgeId = bridgeId;
    this.upTime = upTime;
    this.downTime = downTime;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getBridgeId() {
    return this.bridgeId;
  }

  public void setBridgeId(int bridgeId) {
    this.bridgeId = bridgeId;
  }

  public Date getUpTime() {
    return this.upTime;
  }

  public void setUpTime(Date upTime) {
    this.upTime = upTime;
  }

  public Date getDownTime() {
    return this.downTime;
  }

  public void setDownTime(Date downTime) {
    this.downTime = downTime;
  }

  public Date getCreatedAt() {
    return this.createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return this.updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }
}
