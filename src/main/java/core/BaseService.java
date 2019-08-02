package core;
/**
 * baseService
 * @author Administrator
 *
 */
public abstract class BaseService {
  public CoreDao dao;
  public void setDao( CoreDao dao ) {
    this.dao = dao;
  }
  public CoreDao getDao() {
    return dao;
  }
  
  public BaseService() {
    initDao();
  }
  
  public abstract void initDao();
}
