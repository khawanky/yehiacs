package com.yehia.c3s.bullyalgorithm.process;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import com.yehia.c3s.bullyalgorithm.communication.MessageCommunication;
import com.yehia.c3s.bullyalgorithm.gui.BullyAlgorithmUI;
import com.yehia.c3s.bullyalgorithm.models.MessageType;
import com.yehia.c3s.bullyalgorithm.models.ProcessStatus;
import com.yehia.c3s.bullyalgorithm.utils.Utils;

public class ElectionBullyAlgorithmProcess {
	public static final long WAIT_TIME_MS = 1000L;
	public static final String ELECTION_PROCESS_SHARED_MEMORY_FILE_NAME = "ElectionProcessSharedMemory";
	public static final String MESSAGE_DELIMITER = "#";
	public static final String MESSAGE_CONTENT_DELIMITER = "_";
	
    public static boolean processRunning = true;
    public static BullyAlgorithmUI bullyAlgorithmUI;
	
	public static void start(final int pId, BullyAlgorithmUI bullyAlgorithmUI) {
        ElectionBullyAlgorithmProcess.bullyAlgorithmUI = bullyAlgorithmUI;
		try {
			String computingMemory = "";
			String messageReceived = MessageCommunication.receiveMessage(ELECTION_PROCESS_SHARED_MEMORY_FILE_NAME);
			appendMessage(messageReceived, pId, MessageType.ELECTION, Calendar.getInstance().getTimeInMillis()); // Append means will keep old messages in memory
			
			boolean isCoordinator = false;
			String oldMessagesInMemory = messageReceived;
			String newReceivedMessage = "";
			
			while (processRunning) {
				bullyAlgorithmUI.logIteraction("-------------------------------------------------------------");
				Thread.sleep(WAIT_TIME_MS);
				long now = Calendar.getInstance().getTimeInMillis();

				// Check previous messages
				oldMessagesInMemory = messageReceived;
				messageReceived = MessageCommunication.receiveMessage(ELECTION_PROCESS_SHARED_MEMORY_FILE_NAME);
				
				if(oldMessagesInMemory.length() < messageReceived.length()) {
					newReceivedMessage = messageReceived.substring(oldMessagesInMemory.length()>0 ? oldMessagesInMemory.length()-1 : 0 , messageReceived.length() - 1);
				} else if (oldMessagesInMemory.length() > messageReceived.length()) {
					newReceivedMessage = messageReceived;
				} else { // No msg received
					newReceivedMessage = "";
				}
				
				if(newReceivedMessage != null && !newReceivedMessage.equals("")) {
					String[] newMessages = newReceivedMessage.split(MESSAGE_DELIMITER);
					if(newMessages.length>0) {
						for (String newMessage : newMessages) {
							String[] msgContent = newMessage.split(MESSAGE_CONTENT_DELIMITER);
							if(msgContent.length>1) {
								long time = Long.parseLong(msgContent[2]);
								bullyAlgorithmUI.logIteraction(String.format("Process [%s] has received [%s] message from Process [%s] at [%s]", pId, msgContent[1], msgContent[0], Utils.getFormattedTime(time)));
							}
						}
					}
				}
				String[] oldMessages = messageReceived.split(MESSAGE_DELIMITER);
				ProcessStatus status = checkProcessStatus(oldMessages, pId, now);

				if (status.isCoordinatorExists()) { // Coordinator Exists = Do nothing and leave the coordination
					isCoordinator = false;
                    bullyAlgorithmUI.disableGenerateAndComputeButton();
                    
                    computingMemory = MessageCommunication.receiveMessage(RandomNumberComputingProcess.COMPUTING_SHARED_MEMORY_FILE_NAME);
                    
                    if(computingMemory.contains(RandomNumberComputingProcess.REQUEST_RUNNING_PROCESS)) {
	                    // Run task thread if the coordinator have send any task to do
	                    new Thread(new Runnable() {
	                        public void run() {
	                            try {
									RandomNumberComputingProcess.start(pId, false, ElectionBullyAlgorithmProcess.bullyAlgorithmUI);
								} catch (Throwable e) {
									e.printStackTrace();
								}
	                        }
	                    }).start();
                    }
                    
				} else if (isCoordinator) {	// I am the Coordinator = re-send the coordination msg
					appendMessage(messageReceived, pId, MessageType.COORDINATION, Calendar.getInstance().getTimeInMillis());  // Append means will keep old messages in memory
				} else {  // No Coordinator Exists = Start the Election process (Send an Election msg)
					if (!status.isHasSentElection()) {	// Didn't sent an Election msg recently = send Election msg
						appendMessage(messageReceived, pId, MessageType.ELECTION, Calendar.getInstance().getTimeInMillis());  // Append means will keep old messages in memory
					} else if (status.isTheWinningElector()) { 	// I am the winner and I have sent Election before = send Coordination msg and I am the Coordinator
						bullyAlgorithmUI.logIteraction(String.format("========== Process [%s] announce that it became the Coordinator ==========", pId));
						sendNewMessage(pId, MessageType.COORDINATION, Calendar.getInstance().getTimeInMillis());  // send new message means will keep old messages in memory
						isCoordinator = true;
                        bullyAlgorithmUI.enableGenerateAndComputeButton();
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static ProcessStatus checkProcessStatus(String[] oldMessages, int pId, long now) {
		ProcessStatus processStatus = new ProcessStatus(false, false);
		Set<Integer> candidateIds = new HashSet<Integer>();
		for (String msg : oldMessages) {
			if(msg.length() > 0) {
				String[] msgContent = msg.split(MESSAGE_CONTENT_DELIMITER);
				int senderPId = Integer.parseInt(msgContent[0]);
				String msgType = msgContent[1];
				long time = Long.parseLong(msgContent[2]);
				if (now - time < 1000L && senderPId > pId) {
					if (msgType.equals(MessageType.COORDINATION.toString()))
						processStatus.setCoordinatorExists(true);
					if (msgType.equals(MessageType.ELECTION.toString()))
						candidateIds.add(senderPId);
				} else if (msgType.equals(MessageType.ELECTION.toString()) && senderPId == pId) {
					processStatus.setHasSentElection(true);
				}
			}
		}
		processStatus.setTheWinningElector(candidateIds.isEmpty());
		return processStatus;
	}

	private static void appendMessage(String oldMessages, int pId, MessageType type, long now) throws Throwable {
		StringBuilder messageToSend = oldMessages != null ? new StringBuilder(oldMessages) : new StringBuilder();		
		messageToSend.append(pId).append(MESSAGE_CONTENT_DELIMITER).append(type).append(MESSAGE_CONTENT_DELIMITER).append(now).append(MESSAGE_DELIMITER);		
		MessageCommunication.sendMessage(ELECTION_PROCESS_SHARED_MEMORY_FILE_NAME, messageToSend.toString());
		bullyAlgorithmUI.logIteraction(String.format("Process [%s] has sent [%s] message at [%s]", pId, type.toString(), Utils.getFormattedTime(now)));
	}

	private static void sendNewMessage(int pId, MessageType type, long now) throws Throwable {
		appendMessage(null, pId, type, now); // Empty oldMessage to clear old messages
	}
}
