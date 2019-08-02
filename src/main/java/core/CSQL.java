package core;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import core.db.DBConnectionUtil;
import core.util.Const;
import core.util.DateUtil;
import core.util.TimestampMorpher;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

public class CSQL {
  private Logger logger = org.apache.log4j.Logger.getLogger(getClass());
  public final List<JSONObject> executeSQL(Connection con, String sql, Object... param) throws Exception {
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<JSONObject> resultList = new ArrayList<JSONObject>();
    try {
      if ( getOptType(sql, Const.SQL.INSERT) ) {
        DatabaseMetaData metaData = con.getMetaData();
        ps = metaData.supportsGetGeneratedKeys()
            ? con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            : con.prepareStatement(sql);
      } else {
        ps = con.prepareStatement(sql);
      }  
      setParams(param, ps);
      service(sql, resultList, ps, rs);
    } catch (SQLException e) {
      logger.error(e);
      throw e;
    } finally {
      DBConnectionUtil.close(rs, ps, null);
    }
    return resultList;
  }

  private final void setPrepareStatementBody(Object obj, int i, PreparedStatement ps) throws SQLException {
    if (obj instanceof java.lang.String) {
      obj = obj == null ? "" : obj;
      ps.setString(i, (String) obj);
    } else if (obj instanceof java.lang.Integer) {
      ps.setInt(i, (Integer) obj);
    } else if (obj instanceof java.lang.Double) {
      ps.setDouble(i, (Double) obj);
    } else if (obj instanceof Timestamp) {
      obj = obj == null ? new Timestamp( new Date().getTime() ) : obj;
      ps.setTimestamp(i, (Timestamp) obj);
    } else if (obj instanceof java.util.Date) {
      obj = (obj == null) ? new java.util.Date() : obj;
      ps.setDate(i, new java.sql.Date(((java.util.Date) obj).getTime()));
    } else if (obj instanceof Blob) {
      ps.setBlob(i, (Blob) obj);
    } else if (obj instanceof Clob) {
      ps.setClob(i, (Clob) obj);
    } else if (obj instanceof BigDecimal) {
      obj = (obj == null) ? new BigDecimal(0) : obj;
      ps.setBigDecimal(i, (BigDecimal) obj);
    } else if (obj instanceof Long) {
      ps.setLong(i, (Long) obj);
    } else if ( obj instanceof Short ) {
      ps.setShort( i, (Short)obj );
    } else if ( obj instanceof Boolean ) {
      ps.setBoolean(i, (Boolean)obj);
    } else if ( obj instanceof java.lang.Float ) {
      ps.setFloat(i, (Float)obj);
    } else if ( obj instanceof java.lang.Byte ) {
      ps.setByte(i, (byte)obj);
    } else if ( obj instanceof byte[]) {
      ps.setBytes(i, (byte[])obj); 
    } else if ( obj instanceof java.sql.Time ) {
      ps.setTime(i, (Time)obj);
    } else {
      throw new SQLException("没有找到"+ obj + "对应的数据库类型");
    }
  }
   
  private final void setObjArray(Object[] params, PreparedStatement ps) throws SQLException {
    for (int i = 0; i < params.length; i++) {
      setPrepareStatementBody(params[i], i + 1, ps);
    }
  }

  public final void setParams(Object params, PreparedStatement ps) throws SQLException {
    if (params != null) {
       if (params instanceof Object[]) {
        setObjArray((Object[]) params, ps);
      } else {
        throw new SQLException("参数类型不是Object[]类型");
      }
    }
  }

  private final void service(String sqlStatus, List<JSONObject> resultList, 
                                           PreparedStatement ps, ResultSet rs) throws Exception {
    try {
      if (getOptType(sqlStatus, Const.SQL.INSERT)) {
        int res = ps.executeUpdate();
        rs = ps.getGeneratedKeys();
        JSONObject obj = new JSONObject();
        if ( rs.next() )  {
          obj.put(Const.SQL.RESULT, rs.getObject(1));
        } else {
          obj.put(Const.SQL.RESULT, res);
        }
        resultList.add(obj);
      } else if (getOptType(sqlStatus, Const.SQL.DELETE)) {
        int id = ps.executeUpdate();
        JSONObject obj = new JSONObject();
        obj.put(Const.SQL.RESULT, id);
        resultList.add(obj);
      } else if (getOptType(sqlStatus, Const.SQL.UPDATE)) {
        ps.executeUpdate();
        int cnt = ps.getUpdateCount();
        JSONObject obj = new JSONObject();
        obj.put(Const.SQL.RESULT, cnt);
        resultList.add(obj);
      } else if (getOptType(sqlStatus, Const.SQL.SELECT)) {
        rs = ps.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        int columCnt = metaData.getColumnCount();
        String[] formats={"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss:SSS"
                              , "yyyy-MM-dd HH:mm:ss E", "yyyyMMddHHmmssSSS"};  
        JSONUtils.getMorpherRegistry().registerMorpher(new TimestampMorpher(formats)); 
        JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(formats));
        while ( rs.next() ) {
          JSONObject obj = new JSONObject();
          for (int i = 0; i < columCnt; i++) {
            putValue(rs, metaData, i+1, obj );
          }
          resultList.add(obj);
        }
      }
    } catch (Exception e) {
      logger.error("执行SQL出错：",e);
      throw new Exception("执行SQL出错："+ sqlStatus);
    }
  }
  
  private final void putValue( ResultSet rs, ResultSetMetaData metaData, 
                                             int columnCnt, JSONObject obj ) throws SQLException {
    String columName = metaData.getColumnLabel( columnCnt ); 
    String typeName = metaData.getColumnTypeName( columnCnt );
    if ( JDBC.ColumnType.CHAR.equals(typeName) 
         || JDBC.ColumnType.VARCHAR.equals(typeName) 
         || JDBC.ColumnType.VARCHAR2.equals(typeName) 
         || JDBC.ColumnType.VARCHAR3.equals(typeName) 
         || JDBC.ColumnType.LONGVARCHAR.equals(typeName)
         || JDBC.ColumnType.LONGTEXT.equals(typeName) 
         || JDBC.ColumnType.TEXT.equals(typeName) 
        ) {
      String value = rs.getString(columnCnt);
      obj.put(columName, value);
    } else if ( JDBC.ColumnType.NUMERIC.equals(typeName) 
          || JDBC.ColumnType.DECIMAL.equals(typeName)) {
      BigDecimal bg = rs.getBigDecimal(columnCnt);
      if ( bg == null ) { bg = new BigDecimal(0);  }
      obj.put(columName, bg);
    } else if ( JDBC.ColumnType.BIT.equals(typeName) ) {
      boolean bit = rs.getBoolean(columnCnt);
      obj.put(columName, bit);
    } else if ( JDBC.ColumnType.INT.equals(typeName)
               ||JDBC.ColumnType.INTEGER.equals(typeName) 
               || JDBC.ColumnType.NUMBER.equals(typeName)
               || JDBC.ColumnType.TINYINT.equals(typeName) 
               || JDBC.ColumnType.SMALLINT.equals(typeName)) {
      int inter = rs.getInt(columnCnt);
      obj.put(columName, inter);
    } else if ( JDBC.ColumnType.BIGINT.equals(typeName) ) {
      long longer = rs.getLong(columnCnt);
      obj.put(columName, longer);
    } else if ( JDBC.ColumnType.REAL.equals(typeName) ) {
      float floater = rs.getFloat(columnCnt);
      obj.put(columName, floater);
    } else if (JDBC.ColumnType.FLOAT.equals(typeName) 
               || JDBC.ColumnType.DOUBLE.equals(typeName)) {
      double doubler = rs.getDouble(columnCnt);
      obj.put(columName, doubler);
    } else if ( JDBC.ColumnType.BINARY.equals(typeName) 
               || JDBC.ColumnType.VARBINARY.equals(typeName) 
               || JDBC.ColumnType.LONGVARBINARY.equals(typeName) ) {
      byte[] byter = rs.getBytes(columnCnt);
      obj.put(columName, byter);
    } else if (JDBC.ColumnType.DATE.equals(typeName)) {
      java.sql.Date date = rs.getDate(columnCnt);
      java.util.Date d = new java.util.Date(date.getTime());
      obj.put(columName, DateUtil.parseDate(d));
    } else if (JDBC.ColumnType.TIME.equals(typeName) ) {
      java.sql.Time time = rs.getTime(columnCnt);
      obj.put(columName, time);
    } else if ( JDBC.ColumnType.TIMESTAMP.equals(typeName) 
                || JDBC.ColumnType.DATETIME.equals(typeName)) {
      java.sql.Timestamp columValue = rs.getTimestamp(columnCnt);
      java.sql.Timestamp time = columValue == null ? null : (Timestamp)columValue;
      obj.put(columName, time == null ? "" : time.toString());
    } else if (JDBC.ColumnType.CLOB.equals(typeName) ) {
      java.sql.Clob clob = rs.getClob(columnCnt);
      obj.put(columName, clob);
    } else if (JDBC.ColumnType.BLOB.equals(typeName)) {
      //java.sql.Blob blob = rs.getBlob(columnCnt);
      byte[] byter = rs.getBytes(columnCnt);
      obj.put(columName, byter);
    } else {
      throw new SQLException("没有找到"+ typeName + "对应的JDBC类型");
    }
  }
  
  public final <T> T setValue( ResultSet rs, String methodName, String typeName, 
                               int columnCnt, T a, Class<T> clazz, Class<?> parameterType ) throws Exception {
    if (    JDBC.ColumnType.CHAR.equals(typeName) 
         || JDBC.ColumnType.VARCHAR.equals(typeName) 
         || JDBC.ColumnType.VARCHAR2.equals(typeName) 
         || JDBC.ColumnType.VARCHAR3.equals(typeName) 
         || JDBC.ColumnType.LONGVARCHAR.equals(typeName)
         || JDBC.ColumnType.LONGTEXT.equals(typeName) 
         || JDBC.ColumnType.TEXT.equals(typeName)  
        ) {
      String value = rs.getString(columnCnt);
      Method m = clazz.getMethod(methodName, String.class);
      m.invoke(a, value);
    } else if ( JDBC.ColumnType.NUMERIC.equals(typeName) 
             || JDBC.ColumnType.DECIMAL.equals(typeName)) {
      BigDecimal bg = rs.getBigDecimal(columnCnt);
      if ( bg == null ) { bg = new BigDecimal(0);  }
      Method m = clazz.getMethod(methodName, BigDecimal.class);
      m.invoke(a, bg);
    } else if ( JDBC.ColumnType.BIT.equals(typeName) ) {
      boolean bit = rs.getBoolean(columnCnt);
      if ( "boolean".equals(parameterType.getName()) ) {
        Method m = clazz.getMethod(methodName, boolean.class);
        m.invoke(a, bit);
      } else {
        Method m = clazz.getMethod(methodName, Boolean.class);
        m.invoke(a, bit);
      }
    } else if ( JDBC.ColumnType.INT.equals(typeName)
               || JDBC.ColumnType.INTEGER.equals(typeName) 
               || JDBC.ColumnType.NUMBER.equals(typeName) 
               || JDBC.ColumnType.TINYINT.equals(typeName) 
               || JDBC.ColumnType.SMALLINT.equals(typeName)) {
      Object inter = null;
      if ( "int".equals(parameterType.getName()) ) {
        inter = rs.getInt(columnCnt);
        Method m = clazz.getMethod(methodName, int.class);
        m.invoke(a, inter);
      } else if ("java.lang.Integer".equals(parameterType.getName())) {
        inter = rs.getInt(columnCnt);
        Method m = clazz.getMethod(methodName, java.lang.Integer.class);
        m.invoke(a,  Integer.valueOf(inter==null ? "0" : String.valueOf(inter)));
      } else if ("double".equals(parameterType.getName()) ) {
        inter = rs.getDouble(columnCnt);
        Method m = clazz.getMethod(methodName, double.class);
        m.invoke(a,  inter);
      } else if ("java.lang.Double".equals(parameterType.getName()) ) {
        inter = rs.getDouble(columnCnt);
        Method m = clazz.getMethod(methodName, java.lang.Double.class);
        m.invoke(a,  Double.valueOf(inter==null ? "0" : String.valueOf(inter)));
      }  
    } else if ( JDBC.ColumnType.BIGINT.equals(typeName) 
             || JDBC.ColumnType.LONG.equals(typeName) ) {
      Object longer = rs.getLong(columnCnt);
      if ("long".equals(parameterType.getName()) ) {
        Method m = clazz.getMethod(methodName, long.class);
        m.invoke(a, Long.valueOf(longer==null ? "0" : String.valueOf(longer)).longValue());
      } else if ( "java.lang.Long".equals(parameterType.getName())) {
        Method m = clazz.getMethod(methodName, Long.class);
        m.invoke(a, Long.valueOf(longer==null ? "0" : String.valueOf(longer)));
      }
    } else if ( JDBC.ColumnType.REAL.equals(typeName) ) {
      Object floater = rs.getFloat(columnCnt);
      if ( "float".equals(parameterType.getName())) {
        Method m = clazz.getMethod(methodName, float.class);
        m.invoke(a, Float.valueOf(floater==null ? "0" : String.valueOf(floater)).floatValue());
      } else {
        Method m = clazz.getMethod(methodName, Float.class);
        m.invoke(a, Float.valueOf(floater==null ? "0" : String.valueOf(floater)));
      }
    } else if (JDBC.ColumnType.FLOAT.equals(typeName) 
            || JDBC.ColumnType.DOUBLE.equals(typeName)) {
      Object doubler = rs.getDouble(columnCnt);
      if ( "double".equals(parameterType.getName())) {
        Method m = clazz.getMethod(methodName, double.class);
        m.invoke(a, Double.valueOf(doubler==null ? "0" : String.valueOf(doubler)).doubleValue());
      } else if ("java.lang.Double".equals(parameterType.getName()) ) {
        Method m = clazz.getMethod(methodName, java.lang.Double.class);
        m.invoke(a,  Double.valueOf(doubler==null ? "0" : String.valueOf(doubler)));
      } 
    } else if (   JDBC.ColumnType.BINARY.equals(typeName) 
               || JDBC.ColumnType.VARBINARY.equals(typeName) 
               || JDBC.ColumnType.LONGVARBINARY.equals(typeName) ) {
      byte[] byter = rs.getBytes(columnCnt);
      Method m = clazz.getMethod(methodName, byte[].class);
      m.invoke(a, byter);
    } else if (JDBC.ColumnType.DATE.equals(typeName)) {
      java.sql.Date date = rs.getDate(columnCnt);
      java.util.Date d = new java.util.Date(date.getTime());
      Method m = clazz.getMethod(methodName,java.util.Date.class);
      m.invoke(a, d);
    } else if (JDBC.ColumnType.TIME.equals(typeName) ) {
      java.sql.Time time = rs.getTime(columnCnt);
      Method m = clazz.getMethod(methodName,java.sql.Time.class);
      m.invoke(a, time);
    } else if (   JDBC.ColumnType.TIMESTAMP.equals(typeName) 
               || JDBC.ColumnType.DATETIME.equals(typeName)) {
      java.sql.Timestamp columValue = rs.getTimestamp(columnCnt);
      java.sql.Timestamp time = columValue == null ? null : (Timestamp)columValue;
      Method m = clazz.getMethod(methodName,java.sql.Timestamp.class);
      m.invoke(a, time);
    } else if (JDBC.ColumnType.CLOB.equals(typeName) ) {
      java.sql.Clob clob = rs.getClob(columnCnt);
      Method m = clazz.getMethod(methodName,java.sql.Clob.class);
      m.invoke(a, clob);
    } else if (JDBC.ColumnType.BLOB.equals(typeName)) {
      //java.sql.Blob blob = rs.getBlob(columnCnt);
      byte[] byter = rs.getBytes(columnCnt);
      Method m = clazz.getMethod(methodName,byte[].class);
      m.invoke(a, byter);
    } else {
      throw new Exception("-->"+ methodName +"方法中参数对应数据库中类型未找到。");
    }
    return a;
  }

  public final boolean getOptType(String sql, String type) {
    String jugSQL = sql.toUpperCase().replaceAll(" ", "").replaceAll("\r\n", "").replaceAll("\n\r", "")
        .replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "");
    type = type.trim();
    if (type.equalsIgnoreCase(jugSQL.substring(0, type.length()))) {
      return true;
    }
    return false;
  }
  
  public String paramLogs( Object ...objects ) {
    StringBuffer parma = new StringBuffer();
    if ( objects != null && objects.length > 0 ) {
      int i = 0;
      parma.append("[");
        for ( Object o : objects ) {
          parma.append(o);
          if ( i < objects.length - 1 ) {
            parma.append(",");
          }
        }
      parma.append("]");
    }
    return parma.toString();
  }
  
}
