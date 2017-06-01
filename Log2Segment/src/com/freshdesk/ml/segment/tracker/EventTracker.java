package com.freshdesk.ml.segment.tracker;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletContext;

import com.freshdesk.ml.segment.common.CommonFileReader;
import com.freshdesk.ml.segment.data.EventData;

import com.freshdesk.ml.segment.common.Constants;

public class EventTracker {
	
	private ExecutorService threadpool=null;
	private static Properties actionMapProperties = new Properties();
	public static Properties configProperties = new Properties();
	RequestHandler reqHandler = new RequestHandler();
	EventData eventData = new EventData();
	
	
	public EventTracker()
	{
	}
	
	public static Properties getActionMap()
	{
		return actionMapProperties;
	}
	
	public void startEventTrackingParallely()
	{
		List<MappedByteBuffer> mappedByteBufferList;
		List<Future> futureList = new ArrayList();
		try
		{
		 String fileLocation = configProperties.getProperty(Constants.APPLICATION_LOG);
		 File file = new File(fileLocation);
		 FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
		 
		 mappedByteBufferList = getMappedByteBuffer(fileChannel);
		 for(MappedByteBuffer mappedByteBuffer : mappedByteBufferList)
		 {
			 futureList.add(threadpool.submit(new EventTrackerWorker(mappedByteBuffer)));
			 
		 }
		 for(Future future : futureList)
		 {
			 future.get();
		 }
		 threadpool.shutdown();
		 
		 fileChannel.close();
		// file.delete();
		 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	private List<MappedByteBuffer> getMappedByteBuffer(FileChannel fileChannel) throws Exception
	{
		List<MappedByteBuffer> mappedByteBufferList = new ArrayList();
		long size = fileChannel.size()/20;
		MappedByteBuffer memoryByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, Integer.MAX_VALUE);
		mappedByteBufferList.add(memoryByteBuffer);
		
		return mappedByteBufferList;
	}
	
	public void startTracking()
	{
		System.out.println("Started reading the file and sending to segments");
		readFromBuffer(configProperties.getProperty("application_log"));
		
	}
	
	private List<EventData> readFromBuffer(String location)
	{
		List<EventData> eventList = new ArrayList();
		EventData eventData = null;
		BufferedReader br = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(location));
			while ((sCurrentLine = br.readLine()) != null) {
				eventData = getEventObject(sCurrentLine);
				if(eventData != null)
				reqHandler.sendRequest(eventData);
			}
			System.out.println("File processed"+location);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return eventList;
	}

	
	private EventData getEventObject(String eventLog) {
		String userId;
		String accId;
		String controllerName;
		String actionName;
		String timeStamp;
		userId = "1";
		String[] eventInfo = eventLog.split(", ");
		try{
		accId = eventInfo[1].split("=")[1];
		timeStamp = eventInfo[0].split(" ")[0] + " " + eventInfo[0].split(" ")[1];
		controllerName = eventInfo[5].split("=")[1];
		actionName = eventInfo[6].split("=")[1];
		if(EventTracker.getActionMap().keySet().contains(actionName+"--"+controllerName))
		{
			String segmentAction = ((String)EventTracker.getActionMap().get(actionName+"--"+controllerName)).split("--")[1];
			String moduleName = ((String)EventTracker.getActionMap().get(actionName+"--"+controllerName)).split("--")[0];
			eventData.setAccID(accId);
			eventData.setEventName(actionName);
			eventData.setControllerName(controllerName);
			eventData.setTimeStamp(timeStamp);
			eventData.setUserID("14375976"); //User id is hard coded since the log file does not contain the user id.
			eventData.setSegmentAction(segmentAction);
			eventData.setModule(moduleName);
		}
		else
			return null;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		//System.out.println("EventData = "+eventData);
		return eventData;
	}

	
	
	public static Properties getActionMapProperties() {
		return actionMapProperties;
	}

	public static void setActionMapProperties(Properties actionMapProperties) {
		EventTracker.actionMapProperties = actionMapProperties;
	}

	
	public static Properties getConfigProperties() {
		return configProperties;
	}

	public static void setConfigProperties(Properties configProperties) {
		EventTracker.configProperties = configProperties;
	}

	public static void main(String args[]) throws Exception
	{
		EventTracker event = new EventTracker();
		event.startTracking();
	}

}
