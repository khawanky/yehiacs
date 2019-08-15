package com.yehia.c3s.bullyalgorithm;

public class BullyAlgorithmProcess {

	public static void main(String[] args) {
		
		int processId = Integer.parseInt(args[0]);
		
		// Every process initially send and Election message
		// Then wait for any other messages
		initProcess(processId);

	}
	
	public static void initProcess(int processId) {
		try {
			if(processId == 1) 
				MessageCommunication.sendMessage("SharedMemoryFile", processId+"_"+"ELECTION");
			
			if(processId == 2) {
				String messageReceived = MessageCommunication.receiveMessage("SharedMemoryFile");
				String[] content = messageReceived.split("_");
				
				if(content[1].equals("ELECTION")) {
					
				} else if(content[1].equals("ALIVE")) {
					// Stop sending and wait for Victory message TODO
					
				} else if(content[1].equals("VICTORY")) {
					// Consider the sender the coordinator TODO
					
				}
				
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
}
