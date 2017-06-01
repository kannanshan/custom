package com.freshdesk.ml.segment.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.*;

public class CommonFileReader {
	
	CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
	
	public static Map<String,Set<String>> getMapFromFile(String fileLocation)
	{
		Map<String,Set<String>> fileMap = new HashMap();
		try
		{
		BufferedReader br = new BufferedReader(new FileReader(fileLocation));
		String eventLog;
		while ((eventLog = br.readLine()) != null) {
			try{
			String actionName = eventLog.split(" ")[0];
			String controllerName = eventLog.split(" ")[1];
			Set<String> controllerSet = fileMap.get(actionName);
			if(controllerSet == null)
			{
				controllerSet = new HashSet<String>();
				fileMap.put(actionName, controllerSet);
			}
			controllerSet.add(controllerName);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return fileMap;
	}

	public static void main(String args[])
	{
		CommonFileReader reader = new CommonFileReader();
		System.out.println(reader.getMapFromFile("/Users/Kannan/Desktop/TrackingEventList.txt"));
	}
	
}
