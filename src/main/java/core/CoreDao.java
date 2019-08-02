package core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import core.db.DBConnectionUtil;
import core.util.CommUtils;
import core.util.Const;
import net.sf.json.JSONObject;

public class CoreDao extends CSQL {
  private static Logger logger = core.util.Logger.getLogger(CoreDao.class);
  private List<JSONObject> invoke(Connection con, String sql, Object... params) throws Exception {
    logger.debug("---->执行SQL:"+ sql + ";参数："+paramLogs( params ));
    return executeSQL(con, sql, params);
  }
  
  /**
   * 查询,已过时请用queryBeans
   * @param con
   * @param sql
   * @param clazz
   * @param params
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Deprecated
  public <T> List<T> selectBeans(Connection con, String sql, Class<T> clazz, Object... params) throws Exception {
    List<T> arrayList = new ArrayList<T>();
    List<JSONObject> objs = invoke(con, sql, params);
    for (JSONObject obj : objs) {
      T bean = (T) JSONObject.toBean(obj, clazz);
      arrayList.add(bean);
    }
    return arrayList;
  }
  
  /**
   * selectBean
   * @param con 
   * @param sql
   * @param clazz
   * @param params
   * @return
   * @throws Exception
   */
  public <T> T selectBean(Connection con, String sql, Class<T> clazz, Object... params) throws Exception {
    List<T> beans = selectBeans(con, sql, clazz, params);
    return beans.size() > 0 ? beans.get(0) : null;
  }
  
  private List<JSONObject> execute(Connection con, String sql, Object... param) throws Exception {
    return invoke(con, sql, param);
  }
  
  /**
   * select a jsonObject list by used columns
   * @param con 
   * @param sql
   * @param clazz
   * @param params
   * @return a result of list
   * @throws Exception
   */
  public List<JSONObject> selectColumns( Connection con, String sql, Object... param ) throws Exception {
    return execute(con, sql, param);
  }
  
  /**
   * 获得的beans, 此方法过时，请用queryBeans
   * @param con
   * @param sql
   * @param clazz
   * @param params
   * @return
   * @throws Exception
   */
  @Deprecated
  public <T> List<T> getBeans(Connection con, String sql, Class<T> clazz, Object... params) throws Exception {
    return selectBeans(con, sql, clazz, params);
  }

  /**
   * 保存一个bean
   * @param con
   * @param sql
   * @param params
   * @return
   * @throws Exception
   */
  public int saveBean(Connection con, String sql, Object... params) throws Exception {
    List<JSONObject> result = null;
    int res = 0;
    try {
      result = invoke(con, sql, params);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      res = result.get(0).getInt( Const.SQL.RESULT );
    } catch (Exception e) {
      e.printStackTrace();
    }
    return res;
  }
  
  /**
   * 通过ORM插入数据库
   * @param con
   * @param obj 必须为@Table类
   * @return
   * @throws Exception
   */
  public int saveBean( Connection con, Object obj ) throws Exception {
    ORM orm = new ORM( obj );
    int id = this.saveBean(con, orm.getInsertSQL(), orm.getValues());
    return id;
  }
  
  /**
   * 更新一个对象
   * @param con
   * @param obj 必须为@Table类
   * @param whereSql 条件
   * @param whereParams 条件参数
   * @return
   * @throws Exception
   */
  public int updateBean( Connection con, Object obj, String whereSql, Object... whereParams) throws Exception {
    ORM orm = new ORM( obj );
    StringBuffer sql = new StringBuffer();
    sql.append(  orm.getUpdateSQL() );
    if ( CommUtils.isNotNull(whereSql) ) {
      if ( !whereSql.toUpperCase().contains("WHERE") ) {
        sql.append(" WHERE ");
      }
      sql.append( whereSql );
    }
    Object[] params = orm.getValues();
    Object[] p = null;
    if ( whereParams != null ) {
      p = new Object[ params.length + whereParams.length ];
      for ( int i=0; i<params.length; i++ ) {
        p[i] = params[i];
      }
      for ( int k=params.length; k<p.length; k++ ) {
        p[k] = whereParams[k - params.length];
      }
    } else {
      p = new Object[  params.length ];
      for ( int i=0; i<params.length; i++ ) {
        p[i] = params[i];
      }
    }
    int id = this.updateBean(con, sql.toString(), p);
    return id;
  }
  
  /**
   * 保存一个bean
   * @param con
   * @param sql
   * @param params
   * @return
   * @throws Exception
   */
  public int save(Connection con, String tableName, String[] columns, Object... params) throws Exception {
    int result = 0;
    StringBuffer sql = new StringBuffer();
    sql.append(" insert into ").append(tableName).append("(");
    for (int cnt = 0; cnt < columns.length; cnt++) {
      sql.append(" ").append(columns[cnt]).append(" ");
      if (cnt < columns.length - 1) {
        sql.append(",");
      }
    }
    sql.append(")").append(" values (");
    for (int cnt = 0; cnt < columns.length; cnt++) {
      sql.append("?");
      if (cnt < columns.length - 1) {
        sql.append(",");
      }
    }
    sql.append(")");
    result = saveBean(con, sql.toString(), params);
    return result;
  }
  
  /**
   * 批量插入数据
   * @param con
   * @param tableName
   * @param columns
   * @param params
   * @param loop
   * @throws Exception
   */
  public void saveBeans(Connection con, String tableName, String[] columns, List<Object[]> params, int loop)
      throws Exception {
    int residue = params.size() % loop; // 余数
    try {
      if (con.getAutoCommit()) {
        con.setAutoCommit(false);
      }
      for (int i = 0; i < params.size(); i++) {
        save(con, tableName, columns, params.get(i));
        if ((i + 1) % loop == 0) {
          con.commit();
        }
      }
      if (residue > 0) {
        con.commit();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      con.setAutoCommit(true);
    }
  }
    
  /**
   * 批量插入数据
   * @param con
   * @param objs，存放的必须是@Table的类
   * @param loop 每多少个提交一下事物
   * @throws Exception
   */
  public void saveBeans (  Connection con, List<Object> objs, int loop ) throws Exception {
    if ( CommUtils.isNullList(objs) ) {
      return;
    }
    ORM orm = new ORM(objs.get(0));
    String sql = orm.getInsertSQL();
    List<Object[]> parmas = new ArrayList<Object[]>();
    for ( int i=0; i<objs.size(); i++ ) {
      Object oneObj = objs.get(i);
      ORM orm2 = new ORM(oneObj);
      parmas.add(orm2.getValues());
    }
    executeBatch(con, sql, parmas, loop);
  }

  /**
   * 批量更新
   * 
   * @param con
   * @param tableName
   * @param columns
   * @param params
   * @param loop
   * @throws SQLException
   */
  public void updateBeans(Connection con, String tableName, String[] columns, String[] whereColums,
                                                                   List<Object[]> params, int loop) throws SQLException {
    int residue = params.size() % loop; // 余数
    try {
      if (con.getAutoCommit()) {
        con.setAutoCommit(false);
      }
      for (int i = 0; i < params.size(); i++) {
        update(con, tableName, columns, whereColums, params.get(i));
        if ((i + 1) % loop == 0) {
          con.commit();
        }
      }
      if (residue > 0) {
        con.commit();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      con.setAutoCommit(true);
    }
  }

  /**
   * 批量处理
   * 
   * @param con
   * @param sql
   * @param columns
   * @param params
   * @param loop
   * @throws Exception
   */
  private void executeBatch(Connection con, String sql, List<Object[]> params, int loop) throws Exception {
    int residue = params.size() % loop; // 余数
    try {
      if (con.getAutoCommit()) {
        con.setAutoCommit(false);
      }
      PreparedStatement ps = con.prepareStatement(sql);
      for (int i = 0; i < params.size(); i++) {
        setParams(params.get(i), ps);
        ps.addBatch();
        if ((i + 1) % loop == 0) {
          ps.executeBatch();
          con.commit();
        }
      }
      if (residue > 0) {
        ps.executeBatch();
        con.commit();
      }
    } catch (Exception e) {
      logger.error(e);
      throw e;
    } finally {
      con.setAutoCommit(true);
    }
  }

  /**
   * 更新bean
   * 
   * @param con
   * @param sql
   * @param params
   * @return
   * @throws Exception
   */
  public int updateBean(Connection con, String sql, Object... params) throws Exception {
    List<JSONObject> result = invoke(con, sql, params);
    return result.get(0).getInt(Const.SQL.RESULT);
  }

  /**
   * 简单更新bean
   * 
   * @param con
   * @param sql
   * @param params
   * @return
   * @throws Exception
   */
  public int update(Connection con, String tableName, String[] columns, 
                                    String[] whereColumns, Object... params)  throws Exception {
    int result = 0;
    StringBuffer sql = new StringBuffer();
    sql.append(" update ").append(tableName);
    sql.append(" set ");
    for (int cnt = 0; cnt < columns.length; cnt++) {
      sql.append(columns[cnt]).append("=?");
      if (cnt < columns.length - 1) {
        sql.append(",");
      }
    }
    if (whereColumns.length > 0) {
      sql.append(" where ");
    }
    for (int cnt = 0; cnt < whereColumns.length; cnt++) {
      sql.append(whereColumns[cnt]).append("=?");
      if (cnt < whereColumns.length - 1) {
        sql.append(" and ");
      }
    }
    result = updateBean(con, sql.toString(), params);
    return result;
  }

  /**
   * 删除bean
   * 
   * @param con
   * @param sql
   * @param params
   * @return
   * @throws Exception
   */
  public int deleteBean(Connection con, String sql, Object... params) throws Exception {
    List<JSONObject> result = invoke(con, sql, params);
    return result.get(0).getInt(Const.SQL.RESULT);
  }

  /**
   * 获得一个bean
   * 
   * @param con
   * @param sql
   * @param clazz
   * @param params
   * @return
   * @throws Exception
   */
  public <T> T getBean(Connection con, String sql, Class<T> clazz, Object... params) throws Exception {
    return selectBean(con, sql, clazz, params);
  }
  
  /**
   * 获得个数
   * @param con
   * @param table
   * @param whereSql
   * @param param
   * @return
   * @throws Exception
   */
  public Long getCount(Connection con, String table, String whereSql, Object ...param ) throws Exception {
    String sql = " select count(*) as cnt from "+ table ;
    if ( CommUtils.isNotNull(whereSql) ) {
      if (! whereSql.toUpperCase().contains("WHERE") ) {
        sql += " WHERE ";
      }
      sql += " " + whereSql;
    }
    List<JSONObject> jsons = execute(con, sql, param);
    if ( jsons.size() == 0) {
      return 0L;
    } 
    JSONObject json = jsons.get(0);
    long count = json.getInt("cnt");
    return count;
  }
  
  /**
   *求和
   * @param con
   * @param table
   * @param sumColumn
   * @param whereSql
   * @param param
   * @return
   * @throws Exception 
   */
  public Double getSum( Connection con, String table, String sumColumn, String whereSql, Object ...param ) throws Exception {
    String sql = " select sum("+ sumColumn +") as cnt from "+ table ;
    if ( CommUtils.isNotNull(whereSql) ) {
      sql += " WHERE " + whereSql;
    }
    List<JSONObject> jsons = execute(con, sql, param);
    if ( jsons.size() == 0) {
      return 0d;
    } 
    JSONObject json = jsons.get(0);
    double count = json.getDouble("cnt");
    return count;
  }
  
  public Long getSumLong( Connection con, String table, String sumColumn, String whereSql, Object ...param ) throws Exception {
    String sql = " select sum("+ sumColumn +") as cnt from "+ table ;
    if ( CommUtils.isNotNull(whereSql) ) {
      sql += " WHERE " + whereSql;
    }
    List<JSONObject> jsons = execute(con, sql, param);
    if ( jsons.size() == 0) {
      return 0L;
    } 
    JSONObject json = jsons.get(0);
    long count = json.getLong("cnt");
    return count;
  }
  
  
  /**
   * 获得一个bean
   * @param con
   * @param sql
   * @param clazz 必须为@Table类
   * @param params 参数
   * @return
   * @throws Exception
   */
  public <T> T queryBean(Connection con, String sql, Class<T> clazz, Object... params) throws Exception {
    logger.debug("---->执行SQL:"+ sql + ";参数："+paramLogs( params ));
    List<T> list = queryBeans(con, sql,  clazz, params);
    if ( list.size() > 0 ) {
      return list.get(0);
    }
    return null;
  }
    
  /**
   * 获得的beans
   * @param con
   * @param sql
   * @param clazz 必须为@Table类
   * @param params
   * @return
   * @throws Exception
   */
  public <T> List<T> queryBeans(Connection con, String sql, Class<T> clazz, Object... params) throws Exception {
    logger.debug("---->执行SQL:"+ sql + ";参数："+paramLogs( params ));
    List<T> arrayList = new ArrayList<T>();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      if ( CommUtils.isNotNull( sql )) {
        ps = con.prepareStatement( sql );
        setParams(params, ps);
        rs = ps.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        int columCnt = metaData.getColumnCount();
        ORM orm = new ORM( clazz.newInstance() );
        while ( rs.next() ) {
          T a = clazz.newInstance();
          for ( int i=0; i<columCnt; i++ ) {
            int cloumIndex = i+1;
            String columName = metaData.getColumnLabel(cloumIndex); 
            String columTypeName = metaData.getColumnTypeName( cloumIndex );
            String methodName = "set"+ (columName.substring(0, 1).toUpperCase()) + (columName.substring(1));
            Class<?> parameteType = orm.getFiledType(columName);
            setValue(rs, methodName, columTypeName, cloumIndex, a, clazz, parameteType );
          }
          arrayList.add(a);
        }
      } else {
        return arrayList;
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      DBConnectionUtil.close(rs, ps, null);
    }
    return arrayList;
  }
  

  /**
   * 数据库维护语句
   * @param con
   * @throws SQLException
   */
  public int ddl(Connection con, String sql) throws Exception {
    if (getOptType(sql, Const.SQL.SELECT) 
        || getOptType(sql, Const.SQL.DELETE) 
        || getOptType(sql, Const.SQL.UPDATE)
        || getOptType(sql, Const.SQL.INSERT)  ) {
      throw new Exception("ddl只用来执行DDL语句");
    }
    PreparedStatement ps = con.prepareStatement(sql);
    int b = ps.executeUpdate();
    return b;
  }
   
  /**
   * 执行语句
   * @param con
   * @param sql
   * @return
   * @throws SQLException
   */
  public int execute(Connection con, String sql) throws SQLException {
    PreparedStatement ps = con.prepareStatement(sql);
    int b = ps.executeUpdate();
    return b;
  }
}
