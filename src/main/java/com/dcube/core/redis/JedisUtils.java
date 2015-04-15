package com.dcube.core.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.exception.AccessorException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtils {
	
	static Logger LOGGER = LoggerFactory.getLogger(JedisUtils.class);
	
	private static JedisPool jedisPool = null;
    private static JedisPoolConfig config = null;
    
    /**
     * Initial JedisPool 
     * 
     */
    public static JedisPool initialPool() throws AccessorException {
    	
		config = new JedisPoolConfig();  
	    config.setMaxIdle(10);
	    config.setMaxTotal(50);
	    config.setMaxWaitMillis(500);
	    
	    config.setTestOnBorrow(true);  
	    config.setTestOnReturn(true);
	    
	    try{    
 
	    	jedisPool = new JedisPool(config, "192.168.1.133", 6379 , 12000);
	    	
        } catch(Exception e) {  
        	
        	LOGGER.error("Error when create JedisPool object",e); 
        	throw new AccessorException("Cann't initial redis pool.",e);
        }
	    
	    return jedisPool;
    }
    
    /**
     * Return Jedis object to JedisPool
     * 
     * @param redis
     */
    public static void returnJedis(Jedis redis) throws AccessorException {
        if (redis != null && jedisPool != null) {
           jedisPool.returnResource(redis);
        }
        if(jedisPool == null){
        	
        	throw new AccessorException("JedisPool is null, Pls. initial it firstly");
        }
    }
    
    /**
     * Borrow Jedis object from JedisPool
     * 
     * @return Jedis object
     */
    public static Jedis borrowJedis()throws AccessorException{

        if(jedisPool == null){
        	
        	throw new AccessorException("JedisPool is null, Pls. initial it firstly");
        }
        
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        } catch (Exception e) {  
        	// return broken one to pool.
        	jedisPool.returnBrokenResource(jedis);
            throw new AccessorException("Fail to borrow Jedis from JedisPool.", e);
        } 
        
        return jedis;
    }
}
