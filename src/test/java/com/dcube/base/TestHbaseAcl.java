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
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
public class TestHbaseAcl extends BaseTester{
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
    
	String tableName = "test_acl";
	//Configuration config = HBaseConfiguration.create();
	/**
	 * 部分代码来自hbase权威指南
	 * @throws IOException
	 */
	public void testRowFilter() throws IOException {
		HTable table = new HTable(config, tableName);
		Scan scan = new Scan();
		System.out.println("只列出小于col5的列");
		//Filter filter1 = new QualifierFilter(CompareFilter.CompareOp.LESS, 
		//	      new BinaryComparator(Bytes.toBytes("group:grp2")));
		Filter filter1 = new SingleColumnValueFilter("acl".getBytes(), 
				"group:grp1".getBytes(), 
				CompareFilter.CompareOp.GREATER_OR_EQUAL, "2".getBytes());
		scan.setFilter(filter1);
		ResultScanner scanner1 = table.getScanner(scan);
		for (Result res : scanner1) {
			System.out.println(res);
		}
		scanner1.close();

	}
	
	public void testPut() throws IOException {
		HTable table = new HTable(config, tableName);
		Put p = new Put("row001".getBytes());

		p.add("c0".getBytes(), "att1".getBytes(), "normal-value-2".getBytes());
		p.add("acl".getBytes(), "group:grp2".getBytes(), "2".getBytes());

		table.put(p);
		table.close();
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
				HColumnDescriptor hcd1 = new HColumnDescriptor("c0");
				htd.addFamily(hcd1);
				HColumnDescriptor hcd2 = new HColumnDescriptor("acl");
				htd.addFamily(hcd2);
				admin.createTable(htd);
			}
			HTable table = new HTable(config, tableName);
			table.setAutoFlush(false);
			int count = 5;
			for (int i = 1; i <= count; ++i) {
				Put p = new Put(String.format("row%03d", i).getBytes());
				String prev = i % 3 == 0? "0":
					(i % 3 == 1? "1":"2");
				p.add("c0".getBytes(), "att1".getBytes(), "normal-value-1".getBytes());
				p.add("acl".getBytes(), "group:grp1".getBytes(), prev.getBytes());
				p.add("acl".getBytes(), "group:grp2".getBytes(), prev.getBytes());
				p.add("acl".getBytes(), "role:role1".getBytes(), prev.getBytes());
				p.add("acl".getBytes(), "role:role2".getBytes(), prev.getBytes());
				p.add("acl".getBytes(), "user:usr1".getBytes(), prev.getBytes());
				p.add("acl".getBytes(), "user:usr2".getBytes(), prev.getBytes());
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
		TestHbaseAcl test = new TestHbaseAcl();
		//test.init();
		test.testRowFilter();
		test.testPut();
	}
}
