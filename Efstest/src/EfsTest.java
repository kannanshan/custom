import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class EfsTest {
	
	//static String sourceDirectory = "";
	//static String destDirectory = "/efs/test/";
	static String produceSourceDirectory = "/Users/Kannan/Desktop/";
	static String produceDestDirectory = "/Users/Kannan/Desktop/s3test/";
	//static String consumeSourceDirectory = "/Users/Kannan/Desktop/s3test/";
	//static String consumeDestDirectory = "/Users/Kannan/Desktop/";
	static int iterativeCount =0;
	//static String produceSourceDirectory = "";
	//static String produceDestDirectory = "/efs/test/";
	//static String consumeSourceDirectory = "/efs/test/";
	//static String consumeDestDirectory = "/home/kannan/";
	
	//static String produceSourceDirectory = "/home/kannan/";
	//static String produceDestDirectory = "/efs/test/";
	static String consumeSourceDirectory = "/efs/test/";
	static String consumeDestDirectory = "/home/kannan/";
	
	static AmazonS3 s3=null;
	
	static
	{
		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.useTcpKeepAlive();
		clientConfig.setMaxConnections(5);
		String accessKey = "";
		String secretKey = "";
		s3 =new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey), clientConfig);
	}
	
	public static void main(String args[]) throws Exception
	{
		args = new String[]{"4","3"};
		String fileNames[] = new String[] { "25kb.txt", "150kb.txt", "1mb.txt", "5mb.txt", "25mb.txt" };
		// String fileNames[] = new String[] { "25kb.txt" };
		iterativeCount = Integer.parseInt(args[1]);
		if (args[0].equalsIgnoreCase("1")) {
			produceFilesToEfs(fileNames);
		}
		if (args[0].equalsIgnoreCase("2")) {
			consumeFilesFromEfs(fileNames);
		}
		if (args[0].equalsIgnoreCase("3")) {
			produceFilesToS3(fileNames);
		}
		if (args[0].equalsIgnoreCase("4")) {
			consumeFilesFromS3(fileNames);
		}
	}
	
	private static void produceFilesToS3(String[] fileNames) throws Exception
	{
		int count =1;
		long avgData;
		Map<String,Long> map = new HashMap();
		while(count <= iterativeCount)
		{
			for (String fileName : fileNames) {
				File source = new File(produceSourceDirectory + fileName);
				long startTime = System.currentTimeMillis();
				s3.putObject(new PutObjectRequest("cdn.freshpo.com", "csv_test/"+fileName+count, source));
				long endtime = System.currentTimeMillis() - startTime;
				System.out.println("fileName:"+fileName+", value:"+endtime);
				if (map.get(fileName) == null) {
					avgData = endtime;
					map.put(fileName, avgData);
				} else {
					avgData = map.get(fileName)+endtime;
				}
				map.put(fileName, ((long) avgData / 2));
			}
			count++;
		}
		System.out.println(map);
	}
	
	private static void consumeFilesFromS3(String[] fileNames) throws Exception
	{
		int count =1;
		long avgData;
		Map<String,Long> map = new HashMap();
		while(count <= iterativeCount)
		{
			for (String fileName : fileNames) {
				File source = new File(produceSourceDirectory + fileName);
				File dest = new File(produceDestDirectory + count + "/" + fileName);
				dest.getParentFile().mkdirs();
				long startTime = System.currentTimeMillis();
				S3Object object = s3.getObject(
		                new GetObjectRequest("cdn.freshpo.com", "csv_test/"+fileName+count));
				InputStream objectData = object.getObjectContent();
				displayTextInputStream(objectData,dest);
				s3.deleteObject(new DeleteObjectRequest("cdn.freshpo.com", "csv_test/"+fileName+count));
				long endtime = System.currentTimeMillis() - startTime;
				System.out.println("fileName:"+fileName+", value:"+endtime);
				if (map.get(fileName) == null) {
					avgData = endtime;
					map.put(fileName, avgData);
				} else {
					avgData = map.get(fileName)+endtime;
				}
				map.put(fileName, ((long) avgData / 2));
			}
			count++;
		}
		System.out.println(map);
	}
	
	private static void produceFilesToEfs(String[] fileNames) throws Exception
	{
		int count =1;
		long avgData;
		Map<String,Long> map = new HashMap();
		while(count <= iterativeCount)
		{
			for (String fileName : fileNames) {
				File source = new File(produceSourceDirectory + fileName);
				File dest = new File(produceDestDirectory + count + "/" + fileName);
				dest.getParentFile().mkdirs();
				long startTime = System.currentTimeMillis();
				copyFileUsingStream(source, dest);
				long endtime = System.currentTimeMillis() - startTime;
				System.out.println("fileName:"+fileName+", value:"+endtime);
				if (map.get(fileName) == null) {
					avgData = endtime;
					map.put(fileName, avgData);
				} else {
					avgData = map.get(fileName)+endtime;
				}
				map.put(fileName, ((long) avgData / 2));
			}
			count++;
		}
		System.out.println(map);
	}
	
	private static void consumeFilesFromEfs(String[] fileNames) throws Exception
	{
		int count =1;
		long avgData;
		Map<String,Long> map = new HashMap();
		while(count <= iterativeCount)
		{
			for (String fileName : fileNames) {
				File source = new File(consumeSourceDirectory + count + "/"+fileName);
				File dest = new File(consumeDestDirectory + count + "/" + fileName);
				dest.getParentFile().mkdirs();
				long startTime = System.currentTimeMillis();
				copyFileUsingStream(source, dest);
				//dest.delete();
				source.delete();
				long endtime = System.currentTimeMillis() - startTime;
				System.out.println("fileName:"+fileName+", value:"+endtime);
				if (map.get(fileName) == null) {
					avgData = endtime;
					map.put(fileName, avgData);
				} else {
					avgData = map.get(fileName)+endtime;
				}
				map.put(fileName, ((long) avgData / 2));
			}
			count++;
		}
		System.out.println(map);
	}
	
	private static void copyFileUsingStream(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}
	
	private static void displayTextInputStream(InputStream input, File dest)
		    throws IOException {
		    	// Read one text line at a time and display.
		        BufferedReader reader = new BufferedReader(new 
		        		InputStreamReader(input));
		        OutputStream os = new FileOutputStream(dest);
		        try{
		        byte[] buffer = new byte[1024];
		        int length;
		        while ((length = input.read(buffer)) > 0) {
		            os.write(buffer, 0, length);
		        }
		    } finally {
		    	input.close();
		        os.close();
		    }
		        
	}
}


