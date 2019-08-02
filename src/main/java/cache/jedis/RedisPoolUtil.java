package cache.jedis;

import core.util.CommUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis连接池
 * @author Administrator
 *
 */
public final class RedisPoolUtil {
    
  //Redis服务器ip
  private static String ADDR = "127.0.0.1";
  //redis的端口
  private static int  PORT = 6379; 
  //访问密码
  private static String PASSWORD = null;
  //可以连接的最大数, 默认为8; 如果赋值为-1，则表示不限制； 
  //如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
  private static int MAX_ACTIVE = 1024;
  //一个pool最多有多少个空闲的实例，默认为8。  
  private static int MAX_IDLE = 200;
  //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
  private static int MAX_WAIT = 10000; //最多等待时间
  private static int TIMEOUT = 10000;  //超时时间
  //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
  private static boolean TEST_ON_BORROW = true;
  private static JedisPool jedisPool = null;
  private static int TIME_BETWEEN_EVICTION_RUNSMILLIS = 30000;
  
  private static int NUM_TESTS_PER_EVICTIONRUN = 10;
  
  private static int MIN_EVICTABLE_IDLE_TIMEMILLIS = 10000;
  
  static {
    try {
      JedisPoolConfig config = new JedisPoolConfig();
      String address = CommUtils.getPropValByKey("jedis.address", "conf");
      String port = CommUtils.getPropValByKey("jedis.port", "conf");
      String passwd = CommUtils.getPropValByKey("jedis.passwd", "conf");
      String maxActive = CommUtils.getPropValByKey("jedis.max.active", "conf");
      String maxIdle = CommUtils.getPropValByKey("jedis.max.idle", "conf");
      String maxWait = CommUtils.getPropValByKey("jedis.max.wait", "conf");
      String timeout = CommUtils.getPropValByKey("jedis.timeout", "conf");
      String onborrow = CommUtils.getPropValByKey("jedis.test.onbrower", "conf");
      ADDR = CommUtils.isNotNull(address) ? address: ADDR;
      PORT = CommUtils.isNotNull(port)? Integer.parseInt(port) : PORT;
      PASSWORD = CommUtils.isNull(passwd)? PASSWORD : passwd;
      MAX_ACTIVE = CommUtils.isNotNull(maxActive)? Integer.parseInt(maxActive):MAX_ACTIVE;
      MAX_IDLE = CommUtils.isNotNull(maxIdle)? Integer.parseInt(maxIdle):MAX_IDLE;
      MAX_WAIT = CommUtils.isNotNull(maxWait)? Integer.parseInt(maxIdle):MAX_WAIT;
      TIMEOUT = CommUtils.isNotNull(timeout)? Integer.parseInt(maxIdle):TIMEOUT;
      TEST_ON_BORROW = CommUtils.isNotNull(onborrow)? Boolean.parseBoolean(onborrow):TEST_ON_BORROW;
      
      // 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
      config.setBlockWhenExhausted(true);
      config.setJmxEnabled(true);
      // 设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数)
      config.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
      // 最大空闲连接数, 默认8个 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
      config.setMaxIdle(MAX_IDLE);
      //config.setMaxWait(MAX_WAIT);
      config.setTestOnBorrow(TEST_ON_BORROW);
      // 最大连接数, 默认8个
      config.setMaxTotal(200);
      // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
      config.setMaxWaitMillis(1000 * 100);
      /** Idle时进行连接扫描 **/
      //config.setTestWhileIdle(true);
      /** 表示idle object evitor两次扫描之间要sleep的毫秒数 **/
      //config.setTimeBetweenEvictionRunsMillis(TIME_BETWEEN_EVICTION_RUNSMILLIS);
      /** 表示idle object evitor每次扫描的最多的对象数 **/
      //config.setNumTestsPerEvictionRun(NUM_TESTS_PER_EVICTIONRUN);
      /**
       * 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；
       * 这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义 
       **/
      config.setMinEvictableIdleTimeMillis(MIN_EVICTABLE_IDLE_TIMEMILLIS);
      
      jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT, PASSWORD);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  //获取Jedis实例
  public synchronized static Jedis getJedis() {
    if ( jedisPool != null ) {
      Jedis jedis = jedisPool.getResource();
      return jedis;
    }
    return null;
  }
  //释放链接
  public synchronized static void releaseJedis( final Jedis jedis ) {
    if ( jedis != null ) {
      jedisPool.returnResource( jedis );
    }
  }
}
