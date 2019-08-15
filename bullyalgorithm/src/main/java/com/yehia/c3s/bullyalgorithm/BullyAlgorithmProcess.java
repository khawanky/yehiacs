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
				int senderPId = Integer.parseInt(content[0]);
				
				
				if(content[1].equals("ELECTION")) {
					if(processId > senderPId) {
						// TODO Stop sending and wait for Victory message
						
					} else {
						// TODO Send Alive message
						
						// TODO Send Election message
					}
					
				} else if(content[1].equals("ALIVE")) {
					// TODO Stop sending and wait for Victory message
					
				} else if(content[1].equals("VICTORY")) {
					// TODO Consider the sender the coordinator
					
				}
				
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
}
