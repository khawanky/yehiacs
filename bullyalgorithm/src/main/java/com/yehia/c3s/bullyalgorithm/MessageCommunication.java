package com.yehia.c3s.bullyalgorithm;

import java.io.File;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.StandardOpenOption;

public class MessageCommunication {
	public static final char messageDelimiter = '\0';
	
	
	public static void receiveMessage(String sharedFileName) throws Throwable {
		File f = new File(sharedFileName);
        FileChannel channel = FileChannel.open( f.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE );

        MappedByteBuffer b = channel.map( MapMode.READ_WRITE, 0, 4096 );
        CharBuffer charBuf = b.asCharBuffer();

        // Prints 'Hello server'
        char c;
        while((c=charBuf.get()) != 0) {
            System.out.print(c);
        }
        System.out.println();

        charBuf.put(0, messageDelimiter);
	}
	
	public static void sendMessage(String sharedFileName, String message) throws Throwable {
		File f = new File(sharedFileName);

        FileChannel channel = FileChannel.open(f.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        MappedByteBuffer b = channel.map( MapMode.READ_WRITE, 0, 4096 );
        CharBuffer charBuf = b.asCharBuffer();

        char[] string = (message+messageDelimiter).toCharArray();
        charBuf.put(string);

        System.out.println("Waiting for client.");
        while( charBuf.get(0) != messageDelimiter);
        System.out.println("Finished waiting.");
	}
}
