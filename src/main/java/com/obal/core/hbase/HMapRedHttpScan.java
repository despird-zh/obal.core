package com.obal.core.hbase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.ResultSerialization;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.serializer.Serializer;
import org.apache.hadoop.mapreduce.Job;

/**
 * HMapRedHttpScan provide Map Reduce function, this will be used to validate the ACL
 * of record at server side.
 * <p>
 * ACL validation is processed in mapper; while in reducer the results are sent to 
 * remote listening servlet directly.
 * </p>
 * 
 * @author despird
 * @version 0.1 2014-3-1
 */
public class HMapRedHttpScan {
	
	private Configuration config = null;
	
	/** the source table */
	private String source;
	/** the scan */
	private Scan scan;
	/** the setting of job */
	private Map<String, String> setting;
	
	/**
	 * Constructor
	 * 
	 * @param source the source table name
	 * @param scan the scan object. 
	 **/
	public HMapRedHttpScan(String source,Scan scan){
			
		this.source = source;			
		this.scan = scan;			
	}
	
	/**
	 * Set the job setting 
	 **/
	public void setJobSetting(Map<String, String> setting){
		this.setting = setting;
	}
	
	/**
	 * Launch the mapreduce scan 
	 * @param jobName the name of job 
	 **/
	public int mapredScan(String jobName) throws Exception {  
	        
	    Job job = Job.getInstance(config);
	    // Initialize the job setting 
	    if(setting != null){
	    	Configuration conf = job.getConfiguration();
	    	for(Map.Entry<String, String> entry:setting.entrySet()){
	    		
	    		conf.set(entry.getKey(), entry.getValue());
	    	}	  
	    }
	    
	    job.setJobName(jobName);
	    job.setJarByClass(HMapRedHttpScan.class);  

	    TableMapReduceUtil.initTableMapperJob(                  
	         source,                  
	         scan,  
	         ScanMapper.class,                
	         ImmutableBytesWritable.class,                
	         Result.class,  
	         job);  
	    TableMapReduceUtil.initTableReducerJob(                 
	         "obal.garbage",               
	         ScanReducer.class,  
	         job); 
	        
	    return job.waitForCompletion(true) ? 0 : 1;        
	        
	} 
	
	/**
	 * The Mapper class here the Acl will be parsed
	 * 
	 **/
    public static class ScanMapper extends TableMapper<ImmutableBytesWritable, Result> {

        private int numRecords = 0;
        Serializer<Result> serializer;
        
    	protected void setup(Context context) throws IOException, InterruptedException {
    		ResultSerialization rstSerialUtil = new ResultSerialization();
        	serializer = rstSerialUtil.getSerializer(Result.class);
    	}
    	
        @Override
        public void map(ImmutableBytesWritable row, Result values, Context context) throws IOException {
            numRecords++;
            System.out.println("map entrance "+numRecords);
        	// extract userKey from the compositeKey (userId + counter)
            ImmutableBytesWritable userKey = new ImmutableBytesWritable(Bytes.toBytes(numRecords));
            try {
            	/**
            	 * ACL processing
            	 **/
                context.write(userKey, values);
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
            
            if ((numRecords % 1000) == 0) {
                context.setStatus("mapper processed " + numRecords + " records so far");
            }
        }
        
        protected void cleanup(Context context) throws IOException, InterruptedException {
        	serializer = null;
        }
    }

    /**
     * Reducer the result will be sent to remote servlet collector. 
     **/
    public static class ScanReducer extends TableReducer<ImmutableBytesWritable, Result, ImmutableBytesWritable> {

    	HttpClient httpClient = null;
    	ThreadPoolExecutor threadPool = null;
    	int count = 0;
    	protected void setup(Context context) throws IOException, InterruptedException {
    		
        	httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        	httpClient.getHostConfiguration().setHost("192.168.1.8", 8080, "http");
        	httpClient.getParams().setSoTimeout(10000);
        	
        	threadPool = new ThreadPoolExecutor(
        			2, // core thread size
        			5, // max thread size
        			3, // idle time
        			TimeUnit.SECONDS, 
        			new LinkedBlockingQueue<Runnable>(),
        			new ThreadPoolExecutor.AbortPolicy());
    	}
    	
        public void reduce(ImmutableBytesWritable key, Iterable<Result> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            System.out.println("reduce data "+Bytes.toInt(key.get()));
            for (final Result val : values) {
            	sum++;
            	PostMethod post = new PostMethod("/demo/mapredresult/");
            	ResultSendTask task = new ResultSendTask(httpClient, post, val);   	
				threadPool.submit(task);
            }

            //context.write(key, put);
        }

        protected void cleanup(Context context) throws IOException, InterruptedException {
        	httpClient = null;
        	threadPool.shutdown();
        	threadPool = null;
        }
    }

    public static class ResultSendTask implements Runnable{
    	
    	HttpClient httpclient = null;
    	Result result = null;
    	PostMethod post =  null;
    	static int count = 0;
    	Serializer<Result> serializer;
    	DataOutputBuffer outBuffer ;
    	int i = 0;
    	
    	public ResultSendTask(HttpClient httpclient,PostMethod post,Result result){
    		i = count++;
    		this.post = post;
    		this.httpclient = httpclient;
    		this.result = result;
    		ResultSerialization rstSerialUtil = new ResultSerialization();
        	serializer = rstSerialUtil.getSerializer(Result.class);
        	outBuffer = new DataOutputBuffer();
    	}
    	
		@Override
		public void run() {
			
			String feedback ="NONE";
			
        	try {        		
        		
        		System.out.println("----start sending result:"+ i);
            	outBuffer.reset();
            	
                serializer.open(outBuffer);
                serializer.serialize(result);
                
        		DataInputBuffer dinbuf = new DataInputBuffer();
        		dinbuf.reset(outBuffer.getData(),0,outBuffer.getLength());

        		System.out.println("task-length of data-"+i+":"+outBuffer.getLength());
				RequestEntity entity=new InputStreamRequestEntity(dinbuf);
	            post.setRequestEntity(entity);
	            post.addRequestHeader("dataindex", String.valueOf(i));
	             // execute the method
	            httpclient.executeMethod(post);
	            InputStream ins = post.getResponseBodyAsStream();	           
	           	feedback = IOUtils.toString(ins) ;
	            
			} catch (IOException e) {
		
				e.printStackTrace();
			} finally {
	             // always release the connection after we're done 
	             post.releaseConnection();
	             System.out.println("----end sending result:" + i + " feedback:" + feedback);
	        }
			cleanup();
		}
    	
    	private void cleanup(){
    		
    		this.post = null;
    		this.httpclient = null;
    		this.result = null;
    		serializer = null;
    		outBuffer = null;
    	}
    }
    
    public void init(){
    	
		config = HBaseConfiguration.create();
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.set("hbase.zookeeper.quorum", "192.168.1.133");
		config.set("hbase.master", "192.168.1.133:60010");
		File file = new File(".");
		try {

			String path = file.getCanonicalPath();
			System.out.println("---init start:" + path);
			System.setProperty("hadoop.home.dir", "D:\\n.repo\\hadoop-2.2.0");

		} catch (IOException e) {
			e.printStackTrace();
		}
    }

}
