package socket;

import cache.Memcached;

public class SocketService {
  
  public boolean set( String key, int count ) {
    return Memcached.getInstance().getCached().set(key, count);
  }
  
  public Object get( String key ) {
    return Memcached.getInstance().getCached().get(key);
  }
  
}
