package com.yehia.c3s.bullyalgorithm.process;
import java.util.Arrays;

import com.yehia.c3s.bullyalgorithm.communication.MessageCommunication;
import com.yehia.c3s.bullyalgorithm.gui.BullyAlgorithmUI;
import com.yehia.c3s.bullyalgorithm.utils.Utils;

public class RandomNumberComputingProcess {
	public static final String COMPUTING_SHARED_MEMORY_FILE_NAME = "ComputingSharedMemory";	
	public static final String REQUEST_RUNNING_PROCESS = "RequestRunningProcess";
	public static final String RESULT_NOT_COMPUTED = "NotComputed";
	public static final int RANDOM_NUMBERS_COUNT = 10000;
	public static final int RANDOM_NUMBERS_RANGE = 1000000;
	public static final long WAIT_TIME_MS = 1000L;

	// Example: RequestRunningProcess#pId_pId_pId_pId_pId_pId_
	public static final String COORDINATOR_REQUEST_DELIMITER = "#";
	public static final String RUNNING_PROCESS_DELIMITER = "_";
	
	// Example: pId!1,2,3,4,5!Result&pId!1,2,3,4,5!Result&pId!1,2,3,4,5!Result&
	public static final String PID_CHUNK_AND_RESULT_DELIMITER = "!";
	public static final String BETWEEN_CHUNKS_DELIMITER = "&";
	public static final String CHUNKS_ITEMS_DELIMITER = ",";
	
	
	public static void start(int pId, boolean isCoordinator, BullyAlgorithmUI bullyAlgorithmUI) throws Throwable {
		if(isCoordinator) {
			try {
				distributeAndCoordinatorCompution(pId,bullyAlgorithmUI);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			computeChunk(pId);
		}
	}
	
	public static void distributeAndCoordinatorCompution(int pId, BullyAlgorithmUI bullyAlgorithmUI) throws Throwable {
		requestRunningProcessesFromCoordinator(pId);
		Thread.sleep(WAIT_TIME_MS*2);
		int[] runningProcesses = getOtherRunningProcesses();
		if(runningProcesses == null || runningProcesses.length < 1) {
			bullyAlgorithmUI.printFinalResult("No running process");
			throw new Exception("No running process");
		}
		
		sendChunksToRunningProcesses(runningProcesses);
		Thread.sleep(WAIT_TIME_MS*2);
		int result = checkForChunkComputingResultsAndFinalComputing();
		bullyAlgorithmUI.printFinalResult(result+"");
		bullyAlgorithmUI.enableGenerateAndComputeButton();
	}
	
	public static void computeChunk(int pId) throws Throwable {
		String receivedMessage = MessageCommunication.receiveMessage(COMPUTING_SHARED_MEMORY_FILE_NAME);
		appendRunningProcess(receivedMessage, pId, false);
		Thread.sleep(WAIT_TIME_MS*2);
		processComputeAndUpdateResult(pId);
	}
	
// =========== Process private Methods =========== //
	
	// Request processes
	private static void requestRunningProcessesFromCoordinator(int pId) throws Throwable {
		appendRunningProcess(null, pId, true); // Empty oldMessage to clear old messages
	}
	
	private static void appendRunningProcess(String oldMessages, int pId, boolean isCoordinator) throws Throwable {
		StringBuilder messageToSend = oldMessages != null ? new StringBuilder(oldMessages) : new StringBuilder();	
		if(isCoordinator) {
			MessageCommunication.sendMessage(COMPUTING_SHARED_MEMORY_FILE_NAME, REQUEST_RUNNING_PROCESS+COORDINATOR_REQUEST_DELIMITER);
		} else {
			if(!messageToSend.toString().contains(pId+RUNNING_PROCESS_DELIMITER)){
					MessageCommunication.sendMessage(COMPUTING_SHARED_MEMORY_FILE_NAME, messageToSend.append(pId+RUNNING_PROCESS_DELIMITER).toString());
			}
		}
	}
	
	// get running processes
	private static int[] getOtherRunningProcesses() throws Throwable {
		String receivedMessage = MessageCommunication.receiveMessage(COMPUTING_SHARED_MEMORY_FILE_NAME);
		String[] requestAndpIds = receivedMessage.split(COORDINATOR_REQUEST_DELIMITER);
		int[] pIds = null;
		if(requestAndpIds!= null && requestAndpIds.length == 2) {
			String[] strPIds=  requestAndpIds[1].split(RUNNING_PROCESS_DELIMITER);
			pIds = Arrays.asList(strPIds).stream().mapToInt(Integer::parseInt).toArray();
		}		
		return pIds;
	}

	
	// Divide and Send chunks for running processes
	private static void sendChunksToRunningProcesses(int[] runningProcesses) throws Throwable {
		int[] generatedNumbers = Utils.generateRandomNumersArray(RANDOM_NUMBERS_COUNT, RANDOM_NUMBERS_RANGE);
		
		int chunkSize =(int) Math.ceil( (double)RANDOM_NUMBERS_COUNT / (runningProcesses.length) );
		
		String oldMessages = "";
		int startIndex=0;
		int endIndex = chunkSize-1;
		for (int i = 0; i < runningProcesses.length; i++) {
			int[] newArray = Arrays.copyOfRange(generatedNumbers, startIndex, endIndex);
			startIndex=endIndex+1;
			if(endIndex+chunkSize > RANDOM_NUMBERS_COUNT-1) {
				endIndex = RANDOM_NUMBERS_COUNT - endIndex + 1;
			} else {
				endIndex=endIndex+chunkSize;
			}
			oldMessages=appendChunk(oldMessages, runningProcesses[i], newArray);
			
		}
	}
	
	private static String appendChunk(String oldMessages, int pId, int[] chunkInt) throws Throwable {
		StringBuilder messageToSend = oldMessages != null ? new StringBuilder(oldMessages) : new StringBuilder();
		StringBuilder chunkStr = new StringBuilder();
		for (int number : chunkInt) {
			chunkStr.append(number).append(CHUNKS_ITEMS_DELIMITER);
		}
		
		MessageCommunication.sendMessage(COMPUTING_SHARED_MEMORY_FILE_NAME,
				messageToSend.append(pId).append(PID_CHUNK_AND_RESULT_DELIMITER).append(chunkStr).append(PID_CHUNK_AND_RESULT_DELIMITER).append(RESULT_NOT_COMPUTED).append(BETWEEN_CHUNKS_DELIMITER).toString());
		return messageToSend.toString();
	}
	
	// Process Updates its chunk result
	private static int processComputeAndUpdateResult (int pId) throws Throwable {
		String receivedMessage = MessageCommunication.receiveMessage(COMPUTING_SHARED_MEMORY_FILE_NAME);
		String[] chunks = receivedMessage.split(BETWEEN_CHUNKS_DELIMITER);
		String[] chunkContent = null;
		int[] chunkInt = null;
		int min=-1;
		String processResult="";
		for (String chunk : chunks) {
			if(chunk.contains(pId+PID_CHUNK_AND_RESULT_DELIMITER)) {
				chunkContent=chunk.split(PID_CHUNK_AND_RESULT_DELIMITER);
				if(chunkContent.length>2) {
					if(chunkContent[2].equals(RESULT_NOT_COMPUTED)) {
						chunkInt = Arrays.asList(chunkContent[1].split(CHUNKS_ITEMS_DELIMITER)).stream().mapToInt(Integer::parseInt).toArray();
						min=Utils.getMinimumNumber(chunkInt);
						processResult=chunk;
						processResult=processResult.replace(RESULT_NOT_COMPUTED, min+"");
						MessageCommunication.sendMessage(COMPUTING_SHARED_MEMORY_FILE_NAME, receivedMessage.replace(chunk, processResult));
					}
				}
			}
		}
		return min;
	}
	
	// Check if processes finish, returns get all results, and flush Data Computing memory
	private static int checkForChunkComputingResultsAndFinalComputing () throws Throwable {
		String receivedMessage = MessageCommunication.receiveMessage(COMPUTING_SHARED_MEMORY_FILE_NAME);
		String[] chunks = receivedMessage.split(BETWEEN_CHUNKS_DELIMITER);
		String[] chunkContent = null;
		int[] intResults=new int[chunks.length];
		
		for (int i = 0; i < chunks.length; i++) {
			if(!chunks[i].contains(RESULT_NOT_COMPUTED)) {
				chunkContent=chunks[i].split(PID_CHUNK_AND_RESULT_DELIMITER);
				if(chunkContent.length>2) {
					intResults[i]=Integer.parseInt(chunkContent[2]);
				}
			}
		}
		return Utils.getMinimumNumber(intResults);
	}

}
