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
  
public class HTester extends BaseTester{  
  
    public static Configuration configuration;  
    static {  
        configuration = HBaseConfiguration.create();  
        configuration.set("hbase.zookeeper.property.clientPort", "2181");  
        configuration.set("hbase.zookeeper.quorum", "192.168.1.133");  
       configuration.set("hbase.master", "192.168.1.133:60010");
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
  
    public static void main(String[] args) {  
    	
    	initLog4j();
        createTable("dcube.demo.doc");  
        insertData("dcube.demo.doc");  
       //  QueryAll("wujintao");  
         QueryByCondition1("obal.meta.attr");  
        // QueryByCondition2("wujintao");  
       // QueryByCondition3("wujintao");  
       // deleteRow("wujintao","abcdef");  
       // deleteByCondition("wujintao","abcdef");  
    }  
  
    /** 
     * 创建表 
     * @param tableName 
     */  
    public static void createTable(final String tableName) {  
        System.out.println("============start create table ......");  
        try {  
            HBaseAdmin hBaseAdmin = new HBaseAdmin(configuration);  
            if (hBaseAdmin.tableExists(tableName)) {// 如果存在要创建的表，那么先删除，再创建
            	if( hBaseAdmin.isTableEnabled(tableName)){
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
     * @param tableName 
     */  
    public static void insertData(String tableName) {  
        System.out.println("start insert data ......");  
        HConnection connection = null;
        HTableInterface table = null;

        try {  
            connection = HConnectionManager.createConnection(configuration);
            table = connection.getTable(tableName);
            //HTable table = (HTable) pool.getTable(tableName); 
            for(int i = 0; i< 6;i++){
	            Put put = new Put(("a"+i).getBytes());// 一个PUT代表一行数据，再NEW一个PUT表示第二行数据,每行一个唯一的ROWKEY，此处rowkey为put构造方法中传入的值  
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
        }finally{
        	
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
    /** 
     * 根据 rowkey删除一条记录 
     * @param tablename 
     * @param rowkey 
     */  
     public static void deleteRow(String tablename, String rowkey)  {  
        try {  
            HTable table = new HTable(configuration, tablename);  
            List list = new ArrayList();  
            Delete d1 = new Delete(rowkey.getBytes());  
            list.add(d1);  
              
            table.delete(list);  
            System.out.println("删除行成功!");  
              
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
          
  
    }  
  
     /** 
      * 组合条件删除 
      * @param tablename 
      * @param rowkey 
      */  
     public static void deleteByCondition(String tablename, String rowkey)  {  
            //目前还没有发现有效的API能够实现 根据非rowkey的条件删除 这个功能能，还有清空表全部数据的API操作  
  
    }  
  
  
    /** 
     * 查询所有数据 
     * @param tableName 
     */  
    public static void QueryAll(String tableName) {  
        HConnection connection = null;
        HTableInterface table = null;

        try {  
            connection = HConnectionManager.createConnection(configuration);
            table = connection.getTable(tableName);
            ResultScanner rs = table.getScanner(new Scan());  
            for (Result r : rs) {  
                System.out.println("获得到rowkey:" + new String(r.getRow()));  
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("列：" + new String(keyValue.getFamily())  
                            + "====值:" + new String(keyValue.getValue()));  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  finally{
        	
        	try {
				table.close();
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        }
    }  
  
    /** 
     * 单条件查询,根据rowkey查询唯一一条记录 
     * @param tableName 
     */  
    public static void QueryByCondition1(String tableName) {  
  
        HConnection connection = null;
        HTableInterface table = null;

        try {  
            connection = HConnectionManager.createConnection(configuration);
            table = connection.getTable(tableName);
            Get scan = new Get("1414165569189".getBytes());// 根据rowkey查询  
            Result r = table.get(scan);  
            System.out.println("获得到rowkey:" + new String(r.getRow()));  
            Cell cell = r.getColumnLatestCell("c0".getBytes(), "readonly".getBytes());
            byte[] bval = cell.getValueArray();
            
            for (KeyValue keyValue : r.raw()) {  
                System.out.println("列:" + new String(keyValue.getFamily())  
                		+ "/Q:" + new String(keyValue.getQualifier()));
                
                
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  finally{
        	
        	try {
				table.close();
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        }
    }  
  
    /** 
     * 单条件按查询，查询多条记录 
     * @param tableName 
     */  
    public static void QueryByCondition2(String tableName) {  
  
        HConnection connection = null;
        HTableInterface table = null;

        try {  
            connection = HConnectionManager.createConnection(configuration);
            table = connection.getTable(tableName);
            Filter filter = new SingleColumnValueFilter(Bytes  
                    .toBytes("column1"), null, CompareOp.EQUAL, Bytes  
                    .toBytes("aaa")); // 当列column1的值为aaa时进行查询  
            Scan s = new Scan();  
            s.setFilter(filter);  
            ResultScanner rs = table.getScanner(s);  
            for (Result r : rs) {  
                System.out.println("获得到rowkey:" + new String(r.getRow()));  
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("列：" + new String(keyValue.getFamily())  
                            + "====值:" + new String(keyValue.getValue()));  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }   finally{
        	
        	try {
				table.close();
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        }
  
    }  
  
    /** 
     * 组合条件查询 
     * @param tableName 
     */  
    public static void QueryByCondition3(String tableName) {  
  
        HConnection connection = null;
        HTableInterface table = null;

        try {  
            connection = HConnectionManager.createConnection(configuration);
            table = connection.getTable(tableName);
  
            List<Filter> filters = new ArrayList<Filter>();  
  
            Filter filter1 = new SingleColumnValueFilter(Bytes  
                    .toBytes("column1"), null, CompareOp.EQUAL, Bytes  
                    .toBytes("aaa"));  
            filters.add(filter1);  
  
            Filter filter2 = new SingleColumnValueFilter(Bytes  
                    .toBytes("column2"), null, CompareOp.EQUAL, Bytes  
                    .toBytes("bbb"));  
            filters.add(filter2);  
  
            Filter filter3 = new SingleColumnValueFilter(Bytes  
                    .toBytes("column3"), null, CompareOp.EQUAL, Bytes  
                    .toBytes("ccc"));  
            filters.add(filter3);  
  
            FilterList filterList1 = new FilterList(filters);  
  
            Scan scan = new Scan();  
            scan.setFilter(filterList1);  
            ResultScanner rs = table.getScanner(scan);  
            for (Result r : rs) {  
                System.out.println("获得到rowkey:" + new String(r.getRow()));  
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("列：" + new String(keyValue.getFamily())  
                            + "====值:" + new String(keyValue.getValue()));  
                }  
            }  
            rs.close();  
  
        } catch (Exception e) {  
            e.printStackTrace();  
        }   finally{
        	
        	try {
				table.close();
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        }
  
    }  
    
    
}  
