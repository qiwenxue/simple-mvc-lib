package core.util;

import java.util.HashMap;
import java.util.Map;

public class CoreDataUtil {
   //public static Memcached dataMap;如果换成Memcached直接引用即可
   private static Map<String, Object> dataCache = new HashMap<String, Object>();
   private static Map<String, Object> dataSourceCache  = new HashMap<String, Object>();
   private static Map<String, Object> configCache = new HashMap<String, Object>();
   
   public static Map<String, Object> getDataSourceCache() {
    return dataSourceCache;
   }
  
   public static Map<String, Object> getConfigCache() {
    return configCache;
   }
  
   public static Object get( String key ) {
     return dataCache.get(key);
   }
   
   public static Map<String, Object> getDataCache() {
     return dataCache;
   }
   
   public static void put( String key, Object value ) {
      dataCache.put(key, value);
   }
   
   public static Object getSource( String key ){
     return dataSourceCache.get(key);
   }
   
   public static void putSource( String key, Object value ) {
     dataSourceCache.put(key, value);
   }
   
   public static Object getConfig( String key ){
     return configCache.get(key);
   }
   
   public static void putConfig(  String key, Object value ) {
     configCache.put(key, value);
   }
}
