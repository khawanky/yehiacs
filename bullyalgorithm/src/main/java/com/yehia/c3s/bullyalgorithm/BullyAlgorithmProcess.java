package com.yehia.c3s.bullyalgorithm;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import com.yehia.c3s.models.MessageType;
import com.yehia.c3s.models.ProcessStatus;

public class BullyAlgorithmProcess {
	public static final long waitTimeMs = 1000L;

	public static void main(String[] args) {
		int processId = Integer.parseInt((String) JOptionPane.showInputDialog("Please enter process Id"));
		start(processId);
	}

	public static void start(int pId) {
		try {
			boolean isCoordinator = false;
			String messageReceived = MessageCommunication.receiveMessage("BullySharedMemory1");
			appendMessage(messageReceived, pId, MessageType.ELECTION, Calendar.getInstance().getTimeInMillis());

			while (true) {
				Thread.sleep(waitTimeMs);
				long now = Calendar.getInstance().getTimeInMillis();

				// Check previous messages
				messageReceived = MessageCommunication.receiveMessage("BullySharedMemory1");
				System.out.printf("Process [%s] has recieved a message [%s]\n", pId, messageReceived);

				String[] oldMessages = messageReceived.split("#");

				ProcessStatus status = checkProcessStatus(oldMessages, pId, now);

				if (status.isCoordinatorExists()) {
					System.out.printf("Coordinator Exists = Continue\n");
					continue;	
				}
				else {
					System.out.printf("No Coordinator Exists = Start Election\n");

					if(status.isTheWinningElector() && !isCoordinator) {
						sendNewMessage(pId, MessageType.COORDINATION, Calendar.getInstance().getTimeInMillis());
						isCoordinator = true; // will consider himself as coordinator temporary without
						// communicating this
						System.out.printf(
								"************ Process [%s] has been announced as a coordinator **************\n", pId);
						continue;
						
					}
					
					if (isCoordinator) {
						appendMessage(messageReceived, pId, MessageType.COORDINATION,
								Calendar.getInstance().getTimeInMillis());
					} else {
						appendMessage(messageReceived, pId, MessageType.ELECTION, Calendar.getInstance().getTimeInMillis());
					}
					System.out.println("----------------------------------------------------");
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
			String[] content = msg.toString().split("_");
			int senderPId = Integer.parseInt(content[0]);
			String msgType = content[1];
			long time = Long.parseLong(content[2]);
			if (now - time < 1000L) {
				if (msgType.equals(MessageType.COORDINATION.toString())) {
					processStatus.setCoordinatorExists(true);
				} else if (msgType.equals(MessageType.ELECTION.toString()) && senderPId > pId)
					candidateIds.add(senderPId);
			}
		}
		processStatus.setTheWinningElector(candidateIds.isEmpty());
		return processStatus;
	}

	private static void appendMessage(String oldMessages, int pId, MessageType type, long now) throws Throwable {
		StringBuilder messageToSend = oldMessages != null ? new StringBuilder(oldMessages) : new StringBuilder();
		messageToSend.append(pId).append("_").append(type).append("_").append(now).append("#");
		MessageCommunication.sendMessage("BullySharedMemory1", messageToSend.toString());
		System.out.printf("Process [%s] has sent a message [%s]\n", pId, messageToSend.toString());
	}

	private static void sendNewMessage(int pId, MessageType type, long now) throws Throwable {
		appendMessage(null, pId, type, now); // Empty oldMessage to clear old messages
	}

}
