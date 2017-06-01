package com.freshdesk.ml.segment.servlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.freshdesk.ml.segment.tracker.EventTracker;
import com.freshdesk.ml.segment.tracker.MasterTracker; 

import com.freshdesk.ml.segment.common.Constants;

public class StartupListener implements ServletContextListener {
	
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		
		try {
			
			ServletContext context = servletContextEvent.getServletContext();
			InputStream resourceContent = context.getResourceAsStream("/WEB-INF/config/config.properties");
			EventTracker.getConfigProperties().load(resourceContent);
			resourceContent = new FileInputStream(new File(EventTracker.getConfigProperties().getProperty(Constants.TRACKING_EVENT)));
			EventTracker.getActionMapProperties().load(resourceContent);
			MasterTracker.startMasterTracker();
		}
		
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
		
		
		
	}
	
	public void contextDestroyed(ServletContextEvent s) {
		
	}

}
