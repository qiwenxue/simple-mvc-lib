package core.db;

import java.io.Serializable;
/**
 * 数据库连接参数类
 * @author ibm
 *
 */
@SuppressWarnings("serial")
public class DBConnectionDataSource implements Serializable{ 
  private String driver;//驱动程序
  private String url;   //数据库地址
  private String user;  //数据库用户名
  private String password;//数据库密码
  private String minConn="1";//初始化连接数
  private String maxConn="1";//最大连接数
  private String idletime = "600000";//连接的最大空闲时间
  private String waittime = "30000";//获得连接时如果没有可用连接最大等待时间
  private String initpool = "10";
  private String idleConnectionTestPeriod = "30";//每次30s检查一下空闲列表
  private String maxstatements = "0";
  
  public int getMaxstatementsInt() {
    return Integer.parseInt( getMaxstatements() );
  }
  
  public String getMaxstatements() {
    return maxstatements;
  }

  public void setMaxstatements(String maxstatements) {
    this.maxstatements = maxstatements;
  }

  public String getIdleConnectionTestPeriod() {
    return idleConnectionTestPeriod;
  }
  
  public int getIdleConnectonTestPeriodTime() {
    return Integer.parseInt( getIdleConnectionTestPeriod() );
  }

  public void setIdleConnectionTestPeriod(String idleConnectionTestPeriod) {
    this.idleConnectionTestPeriod = idleConnectionTestPeriod;
  }

  public int getInitCnt() {
    return Integer.parseInt(getInitpool());
  }
  
  public String getInitpool() {
    return initpool;
  }
  public void setInitpool(String initpool) {
    this.initpool = initpool;
  }
  public String getMinConn() {
    return minConn;
  }
  public void setMinConn(String minConn) {
    this.minConn = minConn;
  }
  public String getMaxConn() {
    return maxConn;
  }
  public void setMaxConn(String maxConn) {
    this.maxConn = maxConn;
  }
  public String getIdletime() {
    return idletime;
  }
  public void setIdletime(String idletime) {
    this.idletime = idletime;
  }
  public String getWaittime() {
    return waittime;
  }
  public void setWaittime(String waittime) {
    this.waittime = waittime;
  }
  public String getDriver() {
    return driver;
  }
  public void setDriver(String driver) {
    this.driver = driver;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public String getUser() {
    return user;
  }
  public void setUser(String user) {
    this.user = user;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public int getMinConnection() {
    return Integer.parseInt(getMinConn());
  }
   
  public int getMaxConnection() {
    return Integer.parseInt(getMaxConn());
  }
   
  public long getTimeout() {
    return Long.parseLong(getIdletime()); 
  }
  
  public long getWaitTime() {
    return Long.parseLong(getWaittime());
  }
}
