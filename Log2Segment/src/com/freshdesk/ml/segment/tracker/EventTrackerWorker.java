package com.freshdesk.ml.segment.tracker;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.FileReader;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.freshdesk.ml.segment.data.EventData;

public class EventTrackerWorker implements Callable {
	MappedByteBuffer mappedByteBuffer;
	CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
	RequestHandler reqHandler = new RequestHandler();
	EventData eventData = new EventData();

	EventTrackerWorker(MappedByteBuffer mappedByteBuffer) {
		this.mappedByteBuffer = mappedByteBuffer;
	}
	
	EventTrackerWorker(){}

	private List<EventData> readFromMappedBuffer() throws Exception {
		List<EventData> eventList = new ArrayList();
		CharBuffer cb = decoder.decode(mappedByteBuffer);
		BufferedReader br = new BufferedReader(new CharArrayReader(cb.array()));
		String eventLog;
		while ((eventLog = br.readLine()) != null) {
			try {
				//EventData eventData = createEventObject(eventLog);
				if(eventData != null)
					eventList.add(eventData);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return eventList;
	}
	
	private List<EventData> readFromBuffer()
	{
		StringBuffer stringBuffer = new StringBuffer();
		List<EventData> eventList = new ArrayList();
		for (int i = 0; i < mappedByteBuffer.limit(); i++)
        {
			char character = (char) mappedByteBuffer.get();
			if(stringBuffer.length()>4000)
			{
				stringBuffer.delete(0, stringBuffer.length());
				System.out.println("Length = "+stringBuffer.length());
				System.out.println("Length exceeds");
				System.out.println("Length = "+mappedByteBuffer.capacity());
				continue;		
			}
			if(character == '\n')
			{
				//eventList.add(createEventObject(stringBuffer.toString()));
				stringBuffer.delete(0, stringBuffer.length());
			}
			stringBuffer.append(character);
           
        }
		return eventList;
	}
	
	private List<EventData> readFromBuffer(String location)
	{
		List<EventData> eventList = new ArrayList();
		BufferedReader br = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(location));
			while ((sCurrentLine = br.readLine()) != null) {
				reqHandler.sendRequest(getEventObject(sCurrentLine));
			}
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
		if(EventTracker.getActionMap().keySet().contains(actionName+"$$-$$"+controllerName))
		{
			String segmentAction = ((String)EventTracker.getActionMap().get(actionName+"_"+controllerName)).split("$$-$$")[1];
			String moduleName = ((String)EventTracker.getActionMap().get(actionName+"_"+controllerName)).split("$$-$$")[0];
			eventData.setAccID(accId);
			eventData.setEventName(actionName);
			eventData.setControllerName(controllerName);
			eventData.setTimeStamp(timeStamp);
			eventData.setUserID("123");
			eventData.setSegmentAction(segmentAction);
			eventData.setModule(moduleName);
		}
		}
		catch(Exception e){}
		//System.out.println("EventData = "+eventData);
		return eventData;
	}

	@Override
	public Object call() throws Exception {
		
		List<EventData> eventList = readFromBuffer(EventTracker.configProperties.getProperty("application_log_location"));
		System.out.println("Size = "+eventList);
		return null;
	}

}
