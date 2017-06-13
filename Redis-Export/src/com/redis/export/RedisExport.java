package com.redis.export;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class RedisExport {

	private static void getAllKeys(String host, String fileName, int db) throws Exception {
		Jedis jedis = new Jedis(host);
		jedis.select(db);
		Set<String> set = jedis.keys("EMAIL_1*");
		PrintWriter pw = new PrintWriter(new File(fileName));
		StringBuilder sb = null;
		for (String key : set) {
			sb = new StringBuilder();
			try {
				Map<String, String> resultsAsString = jedis.hgetAll(key);
				sb.append(key.substring(8));
				sb.append(',');
				sb.append(resultsAsString.get("delivered"));
				sb.append(',');
				sb.append(resultsAsString.get("dropped"));
				sb.append(',');
				sb.append(resultsAsString.get("deferred"));
				sb.append(',');
				sb.append(resultsAsString.get("bounce"));
				sb.append(',');
				sb.append(resultsAsString.get("spam"));
				sb.append(',');
				sb.append('\n');
			} catch (Exception e) {
				e.printStackTrace();
			}
			pw.write(sb.toString());
		}
		pw.close();
	}

	public static void main(String args[]) throws Exception{
		/*String host = "127.0.0.1";
		String fileName = "/Users/Kannan/Desktop/test.csv";
		int db =0;*/
		String host = args[0];
		String fileName = args[1];
		int db = Integer.parseInt(args[2]);
		getAllKeys(host, fileName,db);
	}
	
	private static void insertData(String host, int db)
	{
		Jedis jedis = new Jedis(host);
		jedis.select(db);
		jedis.hincrBy("EMAIL_1_2", "delivered", 4);
		jedis.hincrBy("EMAIL_1_2", "dropped", 5);
		jedis.hincrBy("EMAIL_1_2", "deferred", 6);
		jedis.hincrBy("EMAIL_1_2", "bounce", 7);
		jedis.hincrBy("EMAIL_1_2", "spam", 8);
	}

}
