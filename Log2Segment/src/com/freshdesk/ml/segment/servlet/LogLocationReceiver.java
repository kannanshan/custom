package com.freshdesk.ml.segment.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.freshdesk.ml.segment.common.Constants;
import com.freshdesk.ml.segment.logs.LogDownloader;
import com.freshdesk.ml.segment.tracker.EventTracker;
import com.freshdesk.ml.segment.tracker.MasterTracker;

/**
 * Servlet implementation class LogLocationReceiver
 */
@WebServlet("/LogLocationReceiver")
public class LogLocationReceiver extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogLocationReceiver() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Map<String,String[]> requestParameters = new HashMap<String,String[]>();
		requestParameters = request.getParameterMap();
	//	String s3ObjectKey = requestParameters.get(Constants.OBJECT_KEY)[0];
	//	MasterTracker.getLogLocationQueue().add(s3ObjectKey);
	//	System.out.println("Object key added to Queue="+s3ObjectKey);
		readKeyFromFile();
	}
	
	@Override
	  public void init() throws ServletException {
	}
	
	
	private void readKeyFromFile()
	{
		BufferedReader br = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader("/Users/Kannan/Desktop/downloads/s3_object_keys.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
				MasterTracker.getLogLocationQueue().add((sCurrentLine.split(",")[1]));
			}
		}
		catch(Exception e)
		{}
	}
	
	public static void main(String args[])
	{
		LogLocationReceiver lo = new LogLocationReceiver();
		lo.readKeyFromFile();
	}

}
