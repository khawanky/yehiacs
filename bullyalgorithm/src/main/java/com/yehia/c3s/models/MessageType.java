package com.yehia.c3s.models;

public enum MessageType {
	ELECTION, 
	ALIVE, // OK message
	VICTORY, // Coordinator
	DATA; // Data transfer message
}
