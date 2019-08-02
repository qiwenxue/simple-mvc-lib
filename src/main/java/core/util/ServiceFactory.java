package core.util;

import core.BaseService;
import core.CoreDao;
import core.CoreDaoInvoke;

public final class ServiceFactory<T extends BaseService> {
  
  public  T getService(Class<T> classIntance) throws Exception {
    T t;
    try {
      t = classIntance.newInstance();
      CoreDao dao = CoreDaoInvoke.getInstance();
      classIntance.getMethod("setDao", CoreDao.class).invoke(t, dao);
    } catch (Exception e) {
      throw new Exception("生成Service时报错:", e);
    }
    return t;
  }
}
