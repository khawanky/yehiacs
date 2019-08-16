package com.yehia.c3s.models;

import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int senderId;
	private String messageType;
	private String messagePayload;
	private boolean isLeader;
	
	public Message(int senderId, String messageType, String messagePayload, boolean isLeader) {
		super();
		this.senderId = senderId;
		this.messageType = messageType;
		this.messagePayload = messagePayload;
		this.isLeader = isLeader;
	}
	public int getSenderId() {
		return senderId;
	}
	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getMessagePayload() {
		return messagePayload;
	}
	public void setMessagePayload(String messagePayload) {
		this.messagePayload = messagePayload;
	}
	public boolean isLeader() {
		return isLeader;
	}
	public void setLeader(boolean isLeader) {
		this.isLeader = isLeader;
	}
}
