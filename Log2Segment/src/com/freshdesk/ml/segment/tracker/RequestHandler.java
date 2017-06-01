package com.freshdesk.ml.segment.tracker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import com.freshdesk.ml.segment.common.Constants;

import com.freshdesk.ml.segment.data.EventData;

public class RequestHandler {
	
	String url = Constants.SEGMENT_URL;
	String key = "";
	HttpClient httpClient;
	final HttpParams httpParams;
	
	public RequestHandler()
	{
		httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		httpClient = new DefaultHttpClient(httpParams);
	}

	public boolean sendRequest(EventData eventData)
	{
		boolean flag = false;
		HttpPost httpPost = new HttpPost(url+eventData.getSegmentAction());
		httpPost.addHeader(Constants.AUTHORIZATION, "");
		httpPost.addHeader(Constants.CONTENT_TYPE,"application/json");
		try{
			StringEntity params = new StringEntity(eventData.getRequestParamsForSending().toString());
			httpPost.setEntity(params);
			System.out.println("Request "+httpPost);
			HttpResponse httpResponse = httpClient .execute(httpPost);
			System.out.println("POST Response Status:: "
	                 + httpResponse.getStatusLine().getStatusCode());
	        BufferedReader reader = new BufferedReader(new InputStreamReader(
	                httpResponse.getEntity().getContent()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();
	        while ((inputLine = reader.readLine()) != null) {
	            response.append(inputLine);
	        }
	        reader.close();
	        //System.out.println(response.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("eventData = "+eventData);
		return flag;
		
	}
	
	public boolean sendRequest(List<EventData> eventDataList)
	{
		boolean flag = false;
		for(EventData eventData : eventDataList)
		flag = sendRequest(eventData);
		return flag;
	}
	
	
	public static void main(String args[]) throws Exception
	{
	 RequestHandler req = new RequestHandler();
	 EventData eventData = new EventData("14375976","2","testcontroller","To Do created","s","1457594377904","track","Updated ticket");
	 List<EventData> list = new ArrayList();
	 list.add(eventData);
	req.sendRequest(list);
	}
}
