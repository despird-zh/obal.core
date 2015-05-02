package com.dcube.base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;

import com.dcube.aop.ByteValueUtils;
import com.dcube.core.hbase.HAclBrowseFilter;

public class HCustomFilterTester extends BaseTester {

	public static Configuration configuration;
	static {
		configuration = HBaseConfiguration.create();
		configuration.set("hbase.zookeeper.property.clientPort", "2181");
		configuration.set("hbase.zookeeper.quorum", "192.168.1.133");
		configuration.set("hbase.master", "192.168.1.133:60010");
		File file = new File(".");
		try {

			String path = file.getCanonicalPath();
			System.out.println("===:" + path);
			System.setProperty("hadoop.home.dir", path + "/target/classes");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		initLog4j();
		//createTable("dcube.demo.doc");
		//insertData("dcube.demo.doc");
		testFilter("dcube.demo.doc");
	}

	/**
	 * 创建表
	 * 
	 * @param tableName
	 */
	public static void createTable(final String tableName) {
		System.out.println("============start create table ......");
		try {
			HBaseAdmin hBaseAdmin = new HBaseAdmin(configuration);
			if (hBaseAdmin.tableExists(tableName)) {// 如果存在要创建的表，那么先删除，再创建
				if (hBaseAdmin.isTableEnabled(tableName)) {
					hBaseAdmin.disableTable(tableName);
				}
				hBaseAdmin.deleteTable(tableName);
				System.out.println(tableName + " is exist,detele....");
			}
			HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
			tableDescriptor.addFamily(new HColumnDescriptor("c0"));
			tableDescriptor.addFamily(new HColumnDescriptor("acl"));
			hBaseAdmin.createTable(tableDescriptor);
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("end create table ......");
	}

	/**
	 * 插入数据
	 * 
	 * @param tableName
	 */
	public static void insertData(String tableName) {
		System.out.println("start insert data ......");
		HConnection connection = null;
		HTableInterface table = null;

		try {
			connection = HConnectionManager.createConnection(configuration);
			table = connection.getTable(tableName);
			// HTable table = (HTable) pool.getTable(tableName);
			for (int i = 0; i < 6; i++) {
				Put put = new Put(("a" + i).getBytes());// 一个PUT代表一行数据，再NEW一个PUT表示第二行数据,每行一个唯一的ROWKEY，此处rowkey为put构造方法中传入的值
				put.add("c0".getBytes(), "q1".getBytes(), "aaa".getBytes());// 本行数据的第一列
				put.add("c0".getBytes(), "q2".getBytes(), "bbb".getBytes());// 本行数据的第三列
				put.add("c0".getBytes(), "q3".getBytes(), "ccc".getBytes());// 本行数据的第三列
				table.put(put);
				table.flushCommits();
			}
			Put put = new Put(("a0").getBytes());
			put.add("acl".getBytes(), "owner".getBytes(), "usr1".getBytes());
			put.add("acl".getBytes(), "u:".getBytes(), "b".getBytes());
			put.add("acl".getBytes(), "o:".getBytes(), "n".getBytes());
			table.put(put);

			put = new Put(("a1").getBytes());
			put.add("acl".getBytes(), "owner".getBytes(), "usr2".getBytes());
			put.add("acl".getBytes(), "u:".getBytes(), "b".getBytes());
			put.add("acl".getBytes(), "o:".getBytes(), "b".getBytes());
			table.put(put);

			put = new Put(("a2").getBytes());
			put.add("acl".getBytes(), "owner".getBytes(), "usr2".getBytes());
			put.add("acl".getBytes(), "u:".getBytes(), "n".getBytes());
			put.add("acl".getBytes(), "u:usr3".getBytes(), "b".getBytes());
			put.add("acl".getBytes(), "o:".getBytes(), "n".getBytes());
			table.put(put);

			put = new Put(("a3").getBytes());
			put.add("acl".getBytes(), "owner".getBytes(), "usr1".getBytes());
			put.add("acl".getBytes(), "u:".getBytes(), "n".getBytes());
			put.add("acl".getBytes(), "u:usr4".getBytes(), "b".getBytes());
			put.add("acl".getBytes(), "o:".getBytes(), "n".getBytes());
			table.put(put);

			put = new Put(("a4").getBytes());
			put.add("acl".getBytes(), "owner".getBytes(), "usr1".getBytes());
			put.add("acl".getBytes(), "u:".getBytes(), "n".getBytes());
			put.add("acl".getBytes(), "g:grp1".getBytes(), "b".getBytes());
			put.add("acl".getBytes(), "g:grp2".getBytes(), "b".getBytes());
			put.add("acl".getBytes(), "o:".getBytes(), "n".getBytes());
			table.put(put);

			put = new Put(("a5").getBytes());
			put.add("acl".getBytes(), "owner".getBytes(), "usr1".getBytes());
			put.add("acl".getBytes(), "u:".getBytes(), "n".getBytes());
			put.add("acl".getBytes(), "g:grp1".getBytes(), "b".getBytes());
			put.add("acl".getBytes(), "g:grp2".getBytes(), "b".getBytes());
			put.add("acl".getBytes(), "o:".getBytes(), "n".getBytes());
			table.put(put);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				table.close();
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		System.out.println("end insert data ......");
	}

	/**
	 * 删除一张表
	 * 
	 * @param tableName
	 */
	public static void dropTable(String tableName) {
		try {
			HBaseAdmin admin = new HBaseAdmin(configuration);
			admin.disableTable(tableName);
			admin.deleteTable(tableName);
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void testFilter(String tableName) {
		HConnection connection = null;
		HTableInterface table = null;

		try {
			System.out.println("==== get connection"); 
			connection = HConnectionManager.createConnection(configuration);
			System.out.println("==== get table"); 
			table = connection.getTable(tableName);
			
            //Filter filter = new HAclBrowseFilter(Bytes.toBytes("usr1"), null);
            Filter filter = new HAclBrowseFilter(null, new String[]{"grp1"});
            Scan s = new Scan();  
            s.setFilter(filter);  
            System.out.println("==== get result scanner"); 
            ResultScanner scanner = table.getScanner(s);  
            System.out.println("==== got scan result");  
            for (Result r : scanner) {  
                System.out.println("====begin rowkey:" + new String(r.getRow()));  
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("cf:" + new String(keyValue.getFamily())  
                    + "/qua:" + new String(keyValue.getQualifier())
                    + "/val:" + new String(keyValue.getValue()));  
                }
                System.out.println("====end rowkey:" + new String(r.getRow()));  
            }  
            System.out.println("==== end test"); 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				table.close();
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
