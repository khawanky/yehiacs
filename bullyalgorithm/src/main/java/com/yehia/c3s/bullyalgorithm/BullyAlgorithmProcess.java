package com.yehia.c3s.bullyalgorithm;

import javax.swing.JOptionPane;

public class BullyAlgorithmProcess {
	public static final long waitTimeMs=500L;
	
	public static void main(String[] args) {
		int processId = Integer.parseInt((String)JOptionPane.showInputDialog("Please enter process Id"));
		process(processId);
	}
	
	public static void process(int pId) {
		try {
			// Every process initially send a dummy message at the beginning to clear previous data
			MessageCommunication.sendMessage("SharedMemoryFile", pId+"_"+"DUMMY");
			System.out.println("PID="+pId+", send DUMMY message");
			
			String messageReceived = "";
			boolean iAmTheLeader = false;
			while (true) {
				System.out.println("PID="+pId+", begin");
				
				// Then waits to see if there is a Victory message from another process
				Thread.sleep(waitTimeMs);
				
				if(iAmTheLeader) {
					System.out.println("PID="+pId+", is still the leader!");
					MessageCommunication.sendMessage("SharedMemoryFile", pId+"_"+"VICTORY");
					System.out.println("PID="+pId+", send another VICTORY message");
				} else {
					messageReceived = MessageCommunication.receiveMessage("SharedMemoryFile");
					if (messageReceived.equals("")) {
						// if not receive any, consider itself as the coordinator and send Victory message
						MessageCommunication.sendMessage("SharedMemoryFile", pId+"_"+"VICTORY");
						System.out.println("PID="+pId+", send VICTORY message");
					} else {
						System.out.println("PID="+pId+", receives message");
						String[] content = messageReceived.toString().split("_");
						int senderPId = Integer.parseInt(content[0]);
						String msgType = content[1];
						if(senderPId == pId) { // The same process, send new heart beat
							if(msgType.equals("VICTORY") || msgType.equals("DUMMY")) {
								System.out.println("PID="+pId+", "+msgType+" message received from myself, so I AM THE LEADEEEERRR !! :P");
		
								MessageCommunication.sendMessage("SharedMemoryFile", pId+"_"+"VICTORY");
								System.out.println("PID="+pId+", send another VICTORY message");
								
								iAmTheLeader = true;
							}
	//						continue;
						} else {
							if(msgType.equals("VICTORY")) {
								System.out.println("PID="+pId+", receives VICTORY message from pId="+senderPId);
	//							continue;
							}
							else { // TODO: not victory message (Alive, or Election
								System.out.println("PID="+pId+", receives Other message from pId="+senderPId);
	//							continue;
							}
						}
					}
				}
				
				System.out.println("----------------------------------------------------");
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
