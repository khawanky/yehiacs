package com.yehia.c3s.bullyalgorithm;

import java.io.File;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.StandardOpenOption;

public class MessageCommunication {
	public static final char messageDelimiter = '\0';
	
	
	public static String receiveMessage(String sharedFileName) throws Throwable {
		File f = new File(sharedFileName);
        FileChannel channel = FileChannel.open( f.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE );

        MappedByteBuffer b = channel.map( MapMode.READ_WRITE, 0, 4096 );
        CharBuffer charBuf = b.asCharBuffer();

        // Returns the received message
        StringBuffer messageReceived = new StringBuffer();
        
        char c;
        while((c=charBuf.get()) != 0) {
        	messageReceived.append(c);
        }
        charBuf.put(0, messageDelimiter);
        
        return messageReceived.toString();
	}
	
	public static void sendMessage(String sharedFileName, String message) throws Throwable {
		File f = new File(sharedFileName);

        FileChannel channel = FileChannel.open(f.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        MappedByteBuffer b = channel.map( MapMode.READ_WRITE, 0, 4096 );
        CharBuffer charBuf = b.asCharBuffer();

        char[] string = (message+messageDelimiter).toCharArray();
        charBuf.put(string);

        System.out.println("Waiting for client.");
        while(charBuf.get(0) != messageDelimiter);
        System.out.println("Finished waiting.");
	}
	
}
