package com.yehia.c3s.bullyalgorithm.process;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import com.yehia.c3s.bullyalgorithm.communication.MessageCommunication;
import com.yehia.c3s.bullyalgorithm.models.MessageType;
import com.yehia.c3s.bullyalgorithm.models.ProcessStatus;
import com.yehia.c3s.bullyalgorithm.utils.Utils;

public class BullyAlgorithmStartUp {
	public static final long waitTimeMs = 1000L;
	public static final String SharedMemoryFileName = "IPCSharedMemory";

	public static void main(String[] args) {
		int processId = Integer.parseInt((String) JOptionPane.showInputDialog("Please enter process Id"));
		start(processId);
	}

	public static void start(int pId) {
		try {
			String messageReceived = MessageCommunication.receiveMessage(SharedMemoryFileName);
			appendMessage(messageReceived, pId, MessageType.ELECTION, Calendar.getInstance().getTimeInMillis()); // Append means will keep old messages in memory
			
			boolean isCoordinator = false;
			String oldMessagesInMemory = messageReceived;
			String newReceivedMessage = "";
			
			while (true) {
				System.out.println("-------------------------------------------------------------");
				Thread.sleep(waitTimeMs);
				long now = Calendar.getInstance().getTimeInMillis();

				// Check previous messages
				oldMessagesInMemory = messageReceived;
				messageReceived = MessageCommunication.receiveMessage(SharedMemoryFileName);
				
				if(oldMessagesInMemory.length() < messageReceived.length()) {
					newReceivedMessage = messageReceived.substring(oldMessagesInMemory.length()>0 ? oldMessagesInMemory.length()-1 : 0 , messageReceived.length() - 1);
				} else if (oldMessagesInMemory.length() > messageReceived.length()) {
					newReceivedMessage = messageReceived;
				} else { // No msg received
					newReceivedMessage = "";
				}
				
				if(newReceivedMessage != null && !newReceivedMessage.equals("")) {
					String[] newMessages = newReceivedMessage.split("#");
					if(newMessages.length>0) {
						for (String newMessage : newMessages) {
							String[] msgContent = newMessage.split("_");
							if(msgContent.length>1) {
								long time = Long.parseLong(msgContent[2]);
								System.out.printf("Process [%s] has received [%s] message from Process [%s] at [%s]\n", pId, msgContent[1], msgContent[0], Utils.getFormattedTime(time));
							}
						}
					}
				}

				String[] oldMessages = messageReceived.split("#");

				ProcessStatus status = checkProcessStatus(oldMessages, pId, now);

				if (status.isCoordinatorExists()) { // Coordinator Exists = Do nothing and leave the coordination
					isCoordinator = false;
				} else if (isCoordinator) {	// I am the Coordinator = re-send the coordination msg
					appendMessage(messageReceived, pId, MessageType.COORDINATION, Calendar.getInstance().getTimeInMillis());  // Append means will keep old messages in memory
				} else {  // No Coordinator Exists = Start the Election process (Send an Election msg)
					if (!status.isHasSentElection()) {	// Didn't sent an Election msg recently = send Election msg
						appendMessage(messageReceived, pId, MessageType.ELECTION, Calendar.getInstance().getTimeInMillis());  // Append means will keep old messages in memory
					} else if (status.isTheWinningElector()) { 	// I am the winner and I have sent Election before = send Coordination msg and I am the Coordinator
						System.out.printf("========== Process [%s] announce that it became the Coordinator ==========\n", pId);
						sendNewMessage(pId, MessageType.COORDINATION, Calendar.getInstance().getTimeInMillis());  // send new message means will keep old messages in memory
						isCoordinator = true;
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
				String[] msgContent = msg.toString().split("_");
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
		messageToSend.append(pId).append("_").append(type).append("_").append(now).append("#");		
		MessageCommunication.sendMessage(SharedMemoryFileName, messageToSend.toString());
		System.out.printf("Process [%s] has sent [%s] message at [%s]\n", pId, type.toString(), Utils.getFormattedTime(now));
	}

	private static void sendNewMessage(int pId, MessageType type, long now) throws Throwable {
		appendMessage(null, pId, type, now); // Empty oldMessage to clear old messages
	}
}
