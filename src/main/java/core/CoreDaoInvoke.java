package core;


public class CoreDaoInvoke extends CoreDao{
  /**
   * 单例模式
   */
  private static CoreDaoInvoke dao = new CoreDaoInvoke();
  private CoreDaoInvoke(){}
  public static CoreDaoInvoke getInstance() {
    return dao;
  }
}
