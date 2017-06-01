package com.freshdesk.ml.segment.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONObject;

import com.freshdesk.ml.segment.common.Constants;

public class EventData {
	
	private String userID;
	private String accID;
	private String controllerName;
	private String segmentAction;
	private String toolName;
	private String ipAddress;
	private String timeStamp;
	private String eventName;
	private String module;
	
	public EventData(String userID,String accID,String controllerName , String eventName,String toolName,String timeStamp,String segmentAction,String module)
	{
		this.userID = userID;
		this.accID = accID;
		this.eventName = eventName;
		this.toolName = toolName;
		this.timeStamp = timeStamp;
		this.controllerName = controllerName;
		this.segmentAction = segmentAction;
		this.module = module;
	}
	
	public EventData()
	{}
	
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}



	public String getUserID() {
		return userID;
	}
	
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	public String getAccID() {
		return accID;
	}
	
	public void setAccID(String accID) {
		this.accID = accID;
	}
	
	
	public String getToolName() {
		return toolName;
	}
	
	public void setToolName(String toolName) {
		this.toolName = toolName;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	
	
	public String getControllerName() {
		return controllerName;
	}

	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}

	public String getSegmentAction() {
		return segmentAction;
	}

	public void setSegmentAction(String segmentAction) {
		this.segmentAction = segmentAction;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public JSONObject getRequestParamsForSending()
	{
		 JSONObject jsonParams = new JSONObject();
		 JSONObject properties = new JSONObject();
		 JSONObject integrations = new JSONObject();
		 try{
		 jsonParams.put(Constants.USER_ID, userID); 
		 jsonParams.put(Constants.EVENT, eventName);
		 jsonParams.put(Constants.TIME_STAMP, getISOTimestamp(timeStamp));
	     properties.put(Constants.MODULE_NAME, module);
		 properties.put(Constants.GROUP_ID,accID);
		 properties.put(Constants.SEGMENT_TYPE, "track");
		 properties.put(Constants.USER_ID, userID); 
		 properties.put(Constants.EVENT, eventName);
		 jsonParams.put(Constants.SEGMENT_PROPERTIES, properties);
		 integrations.put(Constants.ALL, false);
		 integrations.put(Constants.NATERO,true );
		 jsonParams.put(Constants.INTEGRATIONS,integrations);
		 }
		 catch(Exception e)
		 {
			 System.out.println("Exception in creating json object");
		 }
		 return jsonParams;
	}
	
	public String toString()
	{
		String display="";
		display = "AccId = "+accID+", UserID = "+userID+", Action = "+eventName+", controllerName="+controllerName+" ,segment action="+segmentAction+", module ="+module;
		return display;
	}
	
	private String getISOTimestamp(String timestamp)
	{
		String requiredTimeStamp="";
		try
		{
		TimeZone tz = TimeZone.getTimeZone("UTC");
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
    	df.setTimeZone(tz);
    	DateFormat formatter = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
    	Date date = formatter.parse(timestamp);
    	requiredTimeStamp = df.format(date);
		}
		catch(Exception e)
		{
			System.out.println("Invalid date"+timestamp);
			
		}
		return requiredTimeStamp;
	}

}
