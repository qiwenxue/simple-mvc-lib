package cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.danga.MemCached.MemCachedClient;

import core.util.CommUtils;
import core.util.PropertyUtil;

/**
 * 缓存
 * @author Administrator
 *
 */
public class Memcached extends MemcachConfig {
  private static MemCachedClient mcc;
  private static boolean isLoadCache = false;
  private static Memcached memcached = new Memcached();
  
  public static Memcached getInstance() {
    PropertyUtil util = PropertyUtil.getInstance();
    //isLoadCache = Boolean.parseBoolean(util.getValueByKey("memcache.isLoadCache", "conf"));
    initConfig( true ) ;// true加载缓存
    return memcached;
  }
  private static  void initConfig(boolean isLoad) {
    if ( isLoad && mcc == null) {
      mcc = initConfig( mcc );
      mcc.setSanitizeKeys(false);
    }
  }
  
  public MemCachedClient getCached() {
    return mcc;
  }
  
  public void put( String key, Object value ) {
    mcc.set(key, value);
  }
  public Object get( String key ) {
    return mcc.get(key,64);
  }
  /**
   * 是否存在key
   * @param key
   * @return
   */
  public boolean containsKey( String key ) {
    return mcc.keyExists(key);
  }
  
  /**
   * 清空缓存(需要从新写一个，flushAll不会清除缓存内容，而是设置过期时间)
   */
  public void clear() {
    List<String> keys =  getAllKeys();
    for (String key : keys ) {
      System.out.println("------->>>>>>"+ key);
      mcc.delete(CommUtils.decode(key) );
      System.out.println("------->>>>>>"+ mcc.get(key));
    }
  }
  
  @SuppressWarnings("unchecked")
  public List<String> getAllKeys() {
    List<String> list = new ArrayList<String>();
    Map<String, Map<String, String>> items = mcc.statsItems();
    for (Iterator<String> itemIt = items.keySet().iterator(); itemIt.hasNext();) {
      String itemKey = itemIt.next();
      Map<String, String> maps = items.get(itemKey);
      for (Iterator<String> mapsIt = maps.keySet().iterator(); mapsIt.hasNext();) {
        String mapsKey = mapsIt.next();
        String mapsValue = maps.get(mapsKey);
        if (mapsKey.endsWith("number")) {// memcached key 类型
                                         // item_str:integer:number_str,是我们保存的数据
          String[] arr = mapsKey.split(":");
          int slabNumber = Integer.valueOf(arr[1].trim());
          int limit = Integer.valueOf(mapsValue.trim());
          Map<String, Map<String, String>> dumpMaps = mcc.statsCacheDump(slabNumber, limit);
          for (Iterator<String> dumpIt = dumpMaps.keySet().iterator(); dumpIt.hasNext();) {
            String dumpKey = dumpIt.next();
            Map<String, String> allMap = dumpMaps.get(dumpKey);
            for (Iterator<String> allIt = allMap.keySet().iterator(); allIt.hasNext();) {
              String allKey = allIt.next();
              System.out.println("***********>>>" + allKey);
              list.add(allKey.trim());
            }
          }
        }
      }
    }
    return list;
  }
  
  public static void main(String[] args) {
    /*Memcached.getInstance().getCached().set("serverips", "172.0.0.1:12122,172.0.0.2:12122,172.0.0.3:12122");
    
    Server s = new Server();
    s.setHealth(true);
    s.setId(1);
    s.setIpAddress("172.0.0.1");
    s.setLinkCount(100);
    s.setPort(12112);
    Memcached.getInstance().getCached().set("172.0.0.1:12122", s.toJson());
    
    s = new Server();
    s.setHealth(true);
    s.setId(1);
    s.setIpAddress("172.0.0.2");
    s.setLinkCount(20);
    s.setPort(12112);
    Memcached.getInstance().getCached().set("172.0.0.2:12122", s.toJson());
    
    s = new Server();
    s.setHealth(true);
    s.setId(1);
    s.setIpAddress("172.0.0.3");
    s.setLinkCount(200);
    s.setPort(12112);
    Memcached.getInstance().getCached().set("172.0.0.3:12122", s.toJson());*/
    Memcached.getInstance().put("xxxx:1111@333333333", "xxxxxxxxx");
    
   List<String> keys = Memcached.getInstance().getAllKeys();
    for (String key : keys) {
      Object obj = Memcached.getInstance().get(key);
      System.out.println("-----"+key+"-------\n"+ obj + "**********");
    }

   Object obj1 = Memcached.getInstance().get("xxxx:1111@333333333");
   System.out.println( obj1 + "**********");
  } 
}
