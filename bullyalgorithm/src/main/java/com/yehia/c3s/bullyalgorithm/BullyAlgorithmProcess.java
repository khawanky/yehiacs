package com.yehia.c3s.bullyalgorithm;

import javax.swing.JOptionPane;

public class BullyAlgorithmProcess {
	public static final long waitTimeMs=2000L;
	
	public static void main(String[] args) {
		int processId = Integer.parseInt((String)JOptionPane.showInputDialog("Please enter process Id"));
		System.out.println(processId);
		process(processId);
	}
	
	public static void process(int pId) {
		try {
			String messageReceived = "";
			while (true) {
				// Every process initially waits to see if there is a Victory message ,
				Thread.sleep(waitTimeMs);
				
				messageReceived = MessageCommunication.receiveMessage("SharedMemoryFile");
				if (messageReceived.equals("")) {
					// if not receive any, consider itself as the coordinator and send Victory message
					MessageCommunication.sendMessage("SharedMemoryFile", pId+"_"+"VICTORY");
				} else {
					String[] content = messageReceived.toString().split("_");
					int senderPId = Integer.parseInt(content[0]);
					String msgType = content[1];
										
					if(senderPId == pId) { // The same process, send new heart beat
						MessageCommunication.sendMessage("SharedMemoryFile", pId+"_"+"VICTORY");
						continue;
					} else {
						if(msgType.equals("VICTORY")) {
							continue;
						}
						else { // TODO: Begin Election process
							continue;
						}
					}
				}
			}
			
//			if(processId == 1) 
//				MessageCommunication.sendMessage("SharedMemoryFile", processId+"_"+"ELECTION");
//			
//			if(processId == 2) {
//				String messageReceived = "";
//				while(true) {
//					
//					
//					messageReceived = MessageCommunication.receiveMessage("SharedMemoryFile");
//					if (messageReceived.equals("")) {
//						continue;
//					} else {
//						String[] content = messageReceived.split("_");
//						int senderPId = Integer.parseInt(content[0]);
//						
//						System.out.println(messageReceived);
//					}
//				}
				
//				if(content[1].equals("ELECTION")) {
//					if(processId > senderPId) {
//						// TODO Stop sending and wait for Victory message
//						
//					} else {
//						// TODO Send Alive message
//						
//						
//						// TODO Send Election message
//						MessageCommunication.sendMessage("SharedMemoryFile", processId+"_"+"ELECTION");
//						
//						// TODO Wait for Alive message
//						boolean received = false;
//						if(false) { // Received Alive 
//							// TODO Stop sending and wait for Victory message
//						} else {
//							MessageCommunication.sendMessage("SharedMemoryFile", processId+"_"+"VICTORY");
//
//						}
//						
//					}
//				} else if(content[1].equals("ALIVE")) {
//					// TODO Stop sending and wait for Victory message
//					
//				} else if(content[1].equals("VICTORY")) {
//					// TODO Consider the sender the coordinator, wait for Heart beat message
//					
//				}
				
//			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
}
