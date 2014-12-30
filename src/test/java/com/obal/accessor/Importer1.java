package com.obal.accessor;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * writes random access logs into hbase table
 * 
 *   userID_count => {
 *      details => {
 *          page
 *      }
 *   }
 * 
 * @author sujee ==at== sujee.net
 *
 */
public class Importer1 {

    public static void main(String[] args) throws Exception {
        
        String [] pages = {"/", "/a.html", "/b.html", "/c.html"};
        
        Configuration config =  HBaseConfiguration.create();
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.set("hbase.zookeeper.quorum", "192.168.1.133");
		config.set("hbase.master", "192.168.1.133:60010");
		File file = new File(".");
		try {

			String path = file.getCanonicalPath();
			System.out.println("===:" + path);
			System.setProperty("hadoop.home.dir", "D:\\n.repo\\hadoop-2.2.0");

		} catch (IOException e) {
			e.printStackTrace();
		}
        HTable htable = new HTable(config, "access_logs");
        htable.setAutoFlush(false);
        htable.setWriteBufferSize(1024 * 1024 * 12);
        
        int totalRecords = 100000;
        int maxID = totalRecords / 1000;
        Random rand = new Random();
        System.out.println("importing " + totalRecords + " records ....");
        for (int i=0; i < totalRecords; i++)
        {
            int userID = rand.nextInt(maxID) + 1;
            byte [] rowkey = Bytes.add(Bytes.toBytes(userID), Bytes.toBytes(i));
            String randomPage = pages[rand.nextInt(pages.length)];
            Put put = new Put(rowkey);
            put.add(Bytes.toBytes("details"), Bytes.toBytes("page"), Bytes.toBytes(randomPage));
            htable.put(put);
        }
        htable.flushCommits();
        htable.close();
        System.out.println("done");
    }
}
