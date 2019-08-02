package core.db;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import core.util.CommUtils;
import core.util.CoreDataUtil;
/**
 * 数据库链接统计类
 * @author qiwx
 *
 */
public class DBConnectionUtil {
  private static org.apache.log4j.Logger log = core.util.Logger.getLogger(DBConnectionUtil.class);
  private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();
  /**
   * 初始化 数据源,每个数据源建立连接池
   * @throws NameNotFoundException
   * @throws NameAlreadyBoundException
   * @throws SQLException
   * @throws PropertyVetoException 
   */
  public static void init() throws NameNotFoundException, NameAlreadyBoundException, SQLException, PropertyVetoException{
     Map<String, Object> map = CoreDataUtil.getDataSourceCache();
     if ( CommUtils.isNotNullMap(map) ) {
        Set<String> keys = map.keySet();
        for ( String key : keys ) {
          log.debug("....加载数据源....>"+ key);
          DBConnectionDataSource ds = (DBConnectionDataSource)map.get(key);
          ComboPooledDataSource cpds=new ComboPooledDataSource(); 
          cpds.setDataSourceName( key );
          cpds.setDriverClass( ds.getDriver() );
          cpds.setJdbcUrl( ds.getUrl() );
          cpds.setUser(ds.getUser());
          cpds.setPassword(ds.getPassword());
          cpds.setInitialPoolSize(ds.getInitCnt());
          cpds.setMinPoolSize( ds.getMinConnection() );  
          cpds.setMaxPoolSize( ds.getMaxConnection() ); 
          cpds.setIdleConnectionTestPeriod((int)ds.getIdleConnectonTestPeriodTime());
          cpds.setMaxIdleTime( (int)ds.getTimeout() );
          cpds.setAutoCommitOnClose(false);
          cpds.setCheckoutTimeout( (int)ds.getWaitTime());
          cpds.setMaxStatements( ds.getMaxstatementsInt() );
          CoreDataUtil.put(key, cpds);
        }
     }
  }
  
  /**
   * 返回数据源dataSource
   * @param dataSource
   * @return
   * @throws Exception
   */
  public static Connection getDBConnection( String dataSource ) throws Exception {
    Connection con = threadLocal.get();
    if ( con == null || con.isClosed() ) {
      con = getConnection( dataSource );
      threadLocal.set(con);
    }
    con.setAutoCommit(false);
    return con;
  }
  
  private synchronized static Connection getConnection( String dataSource ) throws Exception {
    ComboPooledDataSource cpds = (ComboPooledDataSource)CoreDataUtil.get( dataSource );
    return cpds.getConnection();
  }
  
  /**
   * 返回数据源dataSource
   * @param dataSource
   * @return
   * @throws Exception
   */
  public  static Connection getDBConnection() throws Exception {
    String main_db = CommUtils.getPropValByKey("default_db", "conf");
    String default_db = CommUtils.isNull(main_db)? "default" : main_db;
    Connection con = threadLocal.get();
    if ( con == null || con.isClosed() ) {
      con = getConnection( default_db );
      if ( con.getAutoCommit() ) {
        con.setAutoCommit(false);
      }
      threadLocal.set(con);
    }
    con.setAutoCommit(false);
    return con;
  }
  
  public static void close( Connection con ) {
    close(null, null, con);
  }
    
  public static void close(ResultSet rs, Statement stmt, Connection con){
    try{
      if(con != null && !con.isClosed()){
         if ( !con.getAutoCommit() ) {
           con.commit();
           con.setAutoCommit(true);
         }
         con.close();
         threadLocal.remove();
      }
      if(rs != null){
        rs.close();
       }
       if(stmt != null){
         stmt.close();
       }
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  public static void rollBack( Connection conn ) {
    log.debug("....回滚数据库....");
    try { 
      if(conn != null ){
        if (!conn.getAutoCommit()) {
          conn.rollback();
        }
      }
    } catch (SQLException e) {
      log.error(" SQLException ==>", e);
    }
  }
  
  /**
   * 获得mysql链接
   * @param url
   * @param userName
   * @param pwd
   * @return
   */
  public static Connection getDBConnection( DBConnectionDataSource ds ){
    Connection conn = null;
    try {
      Class.forName(ds.getDriver()).newInstance();
      conn = DriverManager.getConnection(ds.getUrl(), ds.getUser(), ds.getPassword());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return conn;
  }   
  /**
   * 获得mysql链接
   * @param url
   * @param userName
   * @param pwd
   * @return
   */
  public synchronized static Connection getDBConnection(String url, String user, String passwrod){
    Connection conn = null;
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      conn = DriverManager.getConnection(url, user, passwrod);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return conn;
  }   
  
  /**
   * 获得mysql链接
   * @param url
   * @param userName
   * @param pwd
   * @return
   */
  public static Connection getDBConnection(String driver, String url, String user, String passwrod){
    Connection conn = null;
    try {
      Class.forName(driver).newInstance();
      conn = DriverManager.getConnection(url, user, passwrod);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return conn;
  }   
}
