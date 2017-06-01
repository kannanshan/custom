package com.freshdesk.ml.segment.tracker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.freshdesk.ml.segment.logs.LogDownloader;
import com.freshdesk.ml.segment.tracker.EventTracker;
import com.freshdesk.ml.segment.tracker.MasterTracker.MasterTrackerWorker;

public class MasterTracker {


	public static BlockingQueue<String> logLocationQueue = null;
	public static EventTracker eventTracker = null;

	public static void createLogLocationQueue() {
		System.out.println("Queue created");
		logLocationQueue = new LinkedBlockingQueue<>();
	}

	public static void createEventTracker() {
		eventTracker = new EventTracker();
	}

	public static BlockingQueue<String> getLogLocationQueue() {
		return logLocationQueue;
	}

	static class MasterTrackerWorker extends Thread {

		public void run() {
			while(true)
			{
			try {
				String objectKey = logLocationQueue.take();
				System.out.println("Object key received from Queue ="+objectKey);
				if (LogDownloader.downloadLog(objectKey)) {
					eventTracker.startTracking();
				}

			}

			catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}

			finally {

			}
			}
		}
	}

	public static void startMasterTracker() {
		createLogLocationQueue();
		MasterTracker.createEventTracker();
		MasterTrackerWorker masterTrackerWorker = new MasterTrackerWorker();
		masterTrackerWorker.start();

	}



}
