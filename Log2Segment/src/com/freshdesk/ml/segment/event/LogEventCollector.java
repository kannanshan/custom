package com.freshdesk.ml.segment.event;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.*;
import java.util.concurrent.Callable;

import com.freshdesk.ml.segment.data.EventData;

public class LogEventCollector {

	 String fileLocation;
	 CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
	
	public LogEventCollector(String fileLocation)
	{
		this.fileLocation = fileLocation;
	}
	
	private List<EventData> ReadFromFile()
	{
		List<EventData> eventList=null;
	    MappedByteBuffer memoryByteBuffer;
		try
		{
		 File file = new File(fileLocation);
		 FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
		 memoryByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 4096 * 8);
		// eventList =  readFromMappedBuffer(memoryByteBuffer);
		 fileChannel.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return eventList;
	}
	
	
	class EventTrackerWorker implements Callable
	{
		MappedByteBuffer mappedByteBuffer;
		
		EventTrackerWorker(MappedByteBuffer mappedByteBuffer)
		{
			this.mappedByteBuffer = mappedByteBuffer;
		}
		
	private List<EventData> readFromMappedBuffer() throws Exception
	{
		List<EventData> eventList = new ArrayList();
			 CharBuffer cb = decoder.decode(mappedByteBuffer);
			 BufferedReader br = new BufferedReader(new CharArrayReader(cb.array())); 
			 String eventLog;
			 while ((eventLog = br.readLine()) != null) 
			 {
				 try{
					 eventList.add(createEventObject(eventLog));
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }
			 }
			return eventList;
		}
		
	private EventData createEventObject(String eventLog)
	{
		 String userId;
		 String accId;
		 String controllerName;
		 String actionName;
		 String timeStamp;
		 userId ="1";
		 String[] eventInfo = eventLog.split(", ");
		 accId = eventInfo[1].split("=")[1];
		 timeStamp = eventInfo[0].split(" ")[0]+" "+eventInfo[0].split(" ")[1];
		 controllerName =eventInfo[5].split("=")[1];
		 actionName = eventInfo[6].split("=")[1];
		 EventData eventData = new EventData(userId,accId,controllerName,actionName,"segment",timeStamp,"track","");
		 return eventData;
	}

	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	}
	public static void main(String args[])
	{
		String fileName = "/Users/Kannan/Desktop/application.log";
		LogEventCollector logEventCollector = new LogEventCollector(fileName);
		logEventCollector.ReadFromFile();
	}

}
