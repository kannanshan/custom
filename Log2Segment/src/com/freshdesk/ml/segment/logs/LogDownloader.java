package com.freshdesk.ml.segment.logs;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.freshdesk.ml.segment.tracker.EventTracker;
import com.freshdesk.ml.segment.common.Constants;


public class LogDownloader {
	
	public static boolean downloadLog(String s3ObjectKey)
	{
		boolean flag = false;
		String bucket = EventTracker.configProperties.getProperty(Constants.BUCKET_NAME);
		String key = s3ObjectKey;
		String application_log = EventTracker.configProperties.getProperty(Constants.APPLICATION_LOG);
		String awsAccessKey = EventTracker.configProperties.getProperty(Constants.AWS_ACCESS_KEY);
		String awsSecretKey = EventTracker.configProperties.getProperty(Constants.AWS_SECRET_KEY);
		try{	
			
			AmazonS3Client s3Client = (!awsAccessKey.equalsIgnoreCase("IAM") && !awsSecretKey.equalsIgnoreCase("IAM")) ? 
					new AmazonS3Client(new BasicAWSCredentials(awsAccessKey,awsSecretKey)) : new AmazonS3Client();
			
			//AmazonS3Client s3Client = new AmazonS3Client(new InstanceProfileCredentialsProvider());
		
			copyFromS3(bucket,key,application_log,awsAccessKey,awsSecretKey,s3Client);	
			flag = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	public static void copyFromS3(String bucket, String source, String application_log,String awsAccessKey,String awsSecretKey,AmazonS3Client client) throws IOException {
		
		S3Object obj = client.getObject(bucket, source);
		System.out.println(obj.getObjectMetadata().toString());
		System.out.println(obj.getBucketName());
		
		GZIPInputStream zis = new GZIPInputStream(obj.getObjectContent());
	    byte[] buffer = new byte[1024];
        File newFile = new File(application_log);
        System.out.println(newFile.getName());
        System.out.println("Started copying the file");
        FileOutputStream fos = new FileOutputStream(newFile);             
        int len;
        while ((len = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
        }
        System.out.println("Completed copying to the file "+application_log);
       
        fos.close();
	
 }
	
	
	
	public static void main(String args[])
	{
		//InputStream resourceContent = context.getResourceAsStream("/WEB-INF/config/config.properties");
		//new EventTracker();
		//System.out.println("dwa");
		//test();
	
	}
}
