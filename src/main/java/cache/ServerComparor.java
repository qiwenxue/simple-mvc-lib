package cache;

import java.util.Comparator;

@SuppressWarnings({ "rawtypes" })
public class ServerComparor implements Comparator {
  @Override
  public int compare(Object o1, Object o2) {
    Server s1 = (Server)o1;
    Server s2 = (Server)o2;
    if ( s1.getLinkCount() > s2.getLinkCount()) {  //降序
      return 1;
    }
    return 0;
  }
}
