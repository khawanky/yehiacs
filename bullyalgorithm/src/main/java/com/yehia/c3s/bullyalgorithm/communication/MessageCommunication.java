package com.yehia.c3s.bullyalgorithm.communication;

import java.io.File;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.StandardOpenOption;

public class MessageCommunication {
	public static final int sharedFileMaxBufferSize = 4194304; // 4 MBs
	
	public static String receiveMessage(String sharedFileName) throws Throwable {
		File f = new File(sharedFileName);
        FileChannel channel = FileChannel.open(f.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        MappedByteBuffer b = channel.map(MapMode.READ_WRITE, 0, sharedFileMaxBufferSize);
        CharBuffer charBuf = b.asCharBuffer();

        // Returns the received message
        StringBuilder messageReceived = new StringBuilder();
        
        char c;
        while((c=charBuf.get()) != 0) {
        	messageReceived.append(c);
        }
        return messageReceived.toString();
	}
	
	public static void sendMessage(String sharedFileName, String message) throws Throwable {
		File f = new File(sharedFileName);
        FileChannel channel = FileChannel.open(f.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        MappedByteBuffer b = channel.map(MapMode.READ_WRITE, 0, sharedFileMaxBufferSize);
        CharBuffer charBuf = b.asCharBuffer();

        char[] string = message.toCharArray();
        charBuf.put(string);
	}
	
}
