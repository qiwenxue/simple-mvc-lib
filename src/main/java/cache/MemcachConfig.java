package cache;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

import core.util.PropertyUtil;
/**
 * memocache
 * @author Administrator
 *
 */
public class MemcachConfig {
  
  private static int initConn=5;
  private static int minConn = 5;
  private static int maxConn=250;
  private static long maxIdle = 1000 * 60 * 60 * 3;
  private static long maintSleep = 2000;
  private static boolean isNagle = false;
  private static boolean  aliveCheck = false;
  private static String poolName = "DEFAULT_POOL";
  private static String poolServers = "47.93.197.153:11211,47.93.242.40:11211";
  private static String weights = "1,1";
  
  static {
    PropertyUtil util = PropertyUtil.getInstance();
    initConn = 1;// Integer.parseInt(util.getValueByKey("memcache.initConn", "conf"));
    minConn = 1;//Integer.parseInt(util.getValueByKey("memcache.minConn", "conf"));
    maxConn = 3;//Integer.parseInt(util.getValueByKey("memcache.maxConn", "conf"));
    maxIdle = 100;//Long.parseLong(util.getValueByKey("memcache.maxIdle", "conf"));
    maintSleep = 1000;//Long.parseLong(util.getValueByKey("memcache.maintSleep", "conf"));
    isNagle = true;//Boolean.parseBoolean(util.getValueByKey("memcache.isNagle", "conf"));
    aliveCheck = true;//Boolean.parseBoolean(util.getValueByKey("memcache.aliveCheck", "conf"));
    //poolServers = util.getValueByKey("memcache.poolServers", "conf");
    //weights = util.getValueByKey("memcache.weights", "conf");
  }
  
  public static MemCachedClient initConfig( MemCachedClient mcc ) {
    
    SockIOPool pool = SockIOPool.getInstance( poolName );
     //设置连接池可用的cache服务器列表，server的构成形式是IP:PORT（如：127.0.0.1:11211）
    String[] servers = poolServers.split(",");  
    String[] weightss = weights.split(",");
    Integer[] intWeights = new Integer[weightss.length];
    pool.setServers( servers );
    for ( int i=0; i<weightss.length; i++) {
      intWeights[i] = Integer.parseInt(weightss[i]);
    }
    //设置连接池可用cache服务器的权重，和server数组的位置一一对应
    //其实现方法是通过根据每个权重在连接池的bucket中放置同样数目的server
    //(如下代码所示)，因此所有权重的最大公约数应该是1，不然会引起bucket资源的浪费。
    pool.setWeights( intWeights );
    
    // 设置开始时每个cache服务器的可用连接数
    pool.setInitConn( initConn );
    
    //设置每个服务器最少可用连接数
    pool.setMinConn( minConn );
    
    //设置每个服务器最大可用连接数
    pool.setMaxConn( maxConn );
    
    //设置可用连接池的最长等待时间
    pool.setMaxIdle( maxIdle );
    
    //设置连接池维护线程的睡眠时间 设置为0，维护线程不启动
    //维护线程主要通过log输出socket的运行状况，监测连接数目及空闲等待时间等参数以控制连接创建和关闭。
    pool.setMaintSleep( maintSleep );
    
    //设置是否使用Nagle算法，因为我们的通讯数据量通常都比较大（相对TCP控制数据）而且要求响应及时，因此该值需要设置为false(默认是true)
    pool.setNagle( isNagle );
    pool.setSocketTO(3000);
    pool.setSocketConnectTO(0);
    
   //设置连接心跳监测开关。
   // 设为true则每次通信都要进行连接是否有效的监测，造成通信次数倍增，加大网络负载，因此该参数应该在对HA要求比较高的场合设为TRUE
   // ，默认状态是false。
    pool.setAliveCheck( aliveCheck );
     // 设置连接失败恢复开关
     // 设置为TRUE，当宕机的服务器启动或中断的网络连接后，这个socket连接还可继续使用，否则将不再使用，默认状态是true，建议保持默认。
    pool.setFailover(true);
    //设置hash算法 alg=0 使用String.hashCode()获得hash code,该方法依赖JDK，可能和其他客户端不兼容，建议不使用
    //alg=1 使用original 兼容hash算法，兼容其他客户端 alg=2
    //使用CRC32兼容hash算法，兼容其他客户端，性能优于original算法 alg=3 使用MD5 hash算法
    //采用前三种hash算法的时候，查找cache服务器使用余数方法。采用最后一种hash算法查找cache服务时使用consistent方法。
    pool.setHashingAlg(3);
    // 设置完pool参数后最后调用该方法，启动pool。
    pool.initialize();
    // 设定是否压缩放入cache中的数据 默认值是ture 如果设定该值为true，需要设定CompressThreshold?
    mcc = new MemCachedClient(poolName);
    //mcc.setCompressEnable(true);
    //设定需要压缩的cache数据的阈值 默认值是30k
    //mcc.setCompressThreshold( 64 * 1024 );
    // 设置cache数据的原始类型是String 默认值是false
    //只有在确定cache的数据类型是string的情况下才设为true，这样可以加快处理速度。
    mcc.setPrimitiveAsString(true);
    
    return mcc;
  }
}
