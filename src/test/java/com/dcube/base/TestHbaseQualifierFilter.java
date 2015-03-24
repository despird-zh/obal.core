package com.dcube.base;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.util.Bytes;
public class TestHbaseQualifierFilter extends BaseTester{
    public static Configuration config;  
    static {  
    	config = HBaseConfiguration.create();  
    	config.set("hbase.zookeeper.property.clientPort", "2181");  
    	config.set("hbase.zookeeper.quorum", "192.168.1.133");  
    	config.set("hbase.master", "192.168.1.133:60010");
       File file = new File("."); 
       try {
		
		String path= file.getCanonicalPath();
		System.out.println("===:"+path);		
		System.setProperty("hadoop.home.dir",path+"/target/classes");
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       
    }  
    
	String tableName = "test_qualifier_filter";
	//Configuration config = HBaseConfiguration.create();
	/**
	 * 部分代码来自hbase权威指南
	 * @throws IOException
	 */
	public void testRowFilter() throws IOException {
		HTable table = new HTable(config, tableName);
		Scan scan = new Scan();
		System.out.println("只列出小于col5的列");
		Filter filter1 = new QualifierFilter(CompareFilter.CompareOp.LESS, 
			      new BinaryComparator(Bytes.toBytes("col5")));
		scan.setFilter(filter1);
		ResultScanner scanner1 = table.getScanner(scan);
		for (Result res : scanner1) {
			System.out.println(res);
		}
		scanner1.close();
		System.out.println("get也可以设置filter");
		Get get1 = new Get(Bytes.toBytes("row003"));
	    get1.setFilter(filter1);
	    Result result1 = table.get(get1); 
	    System.out.println("Result of get(): " + result1);
	}
	/**
	 * 初始化数据
	 */
	public void init() {
		// 创建表和初始化数据
		try {
			HBaseAdmin admin = new HBaseAdmin(config);
			if (!admin.tableExists(tableName)) {
				HTableDescriptor htd = new HTableDescriptor(tableName);
				HColumnDescriptor hcd1 = new HColumnDescriptor("data1");
				htd.addFamily(hcd1);
				HColumnDescriptor hcd2 = new HColumnDescriptor("data2");
				htd.addFamily(hcd2);
				HColumnDescriptor hcd3 = new HColumnDescriptor("data3");
				htd.addFamily(hcd3);
				admin.createTable(htd);
			}
			HTable table = new HTable(config, tableName);
			table.setAutoFlush(false);
			int count = 50;
			for (int i = 1; i <= count; ++i) {
				Put p = new Put(String.format("row%03d", i).getBytes());
				p.add("data1".getBytes(), String.format("col%01d", i % 10)
						.getBytes(), String.format("data1%03d", i).getBytes());
				p.add("data2".getBytes(), String.format("col%01d", i % 10)
						.getBytes(), String.format("data2%03d", i).getBytes());
				p.add("data3".getBytes(), String.format("col%01d", i % 10)
						.getBytes(), String.format("data3%03d", i).getBytes());
				table.put(p);
			}
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		initLog4j();
		TestHbaseQualifierFilter test = new TestHbaseQualifierFilter();
		test.init();
		test.testRowFilter();
	}
}
