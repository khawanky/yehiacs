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
			MessageCommunication.sendMessage("SharedMemoryFile", processId+"_"+"ELECTION");
			MessageCommunication.receiveMessage("SharedMemoryFile");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
}
