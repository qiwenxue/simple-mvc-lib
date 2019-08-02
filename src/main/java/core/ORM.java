package core;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;

import core.annotation.Column;
import core.annotation.Table;
import core.util.CommUtils;
import net.sf.json.JSONObject;

public final class ORM{
  private Object obj;
  private List<String> colums;    //字段名称, 不含主键
  private List<Object> values;    //字段的值
  private String tableName;       //对应的表名
  
  private List<String> _colums;     //字段名称
  private List<String> _filedNames; //属性名字
  private List<Object> _values;     //字段的值
  
  private Map<String, Class<?>> _filedTypes;
  
  private String primaryKey = "id";
  private static Map<String, String[]> columMap = new HashMap<String, String[]>();//这个类对应的数据库字段
  private List<JSONObject> commentList = new ArrayList<JSONObject>(); //描述信息， 不含主键
  
  public ORM( Object obj ) throws Exception {
    if ( obj == null ) throw new Exception("ORM传入的对象不能为null"); 
    this.obj = obj;
    colums = new ArrayList<String>();
    values = new ArrayList<Object>();
        
    _colums = new ArrayList<String>();
    _values = new ArrayList<Object>();
    _filedNames = new ArrayList<String>();
    
    _filedTypes = new HashMap<String, Class<?>>();
        
    execute( this.obj );
  }
  
  public List<JSONObject> getComments(){
    return commentList;
  }
    
  public String getTableName() {
    return tableName.toUpperCase();
  }
  
  public String getPrimaryKey() {
    return primaryKey;
  }
  
  public String[] getColums() {
    String key = this.obj.getClass().getName();//这个类的名称作为key
    String[] columns = columMap.get(key);
    if ( columns == null ) {
      String[] columnArray = new String[ colums.size() ];
      int i = 0;
      for ( String column  : colums ) {
        columnArray[i] = column;
        i++;
      }
      columMap.put(key, columnArray);
    }
    return columns;
  }
  
  public Object[] getValues() {
    Object[] vs = new Object[ values.size() ];
    int index = 0;
    for (Object value : values )  {
      vs[index] = value;
      index ++;
    }
    return vs;
  }
  
  public Class<?> getFiledType( String columnName ) {
    return _filedTypes.get(columnName);
  }
  
  public Class<?> getClazz( Object object ) {
    return object.getClass();
  }
    
  private void execute( Object object ) throws Exception {
    Class<?> clazz = object.getClass();
    String key = clazz.getName();
    Table table = (Table) clazz.getAnnotation(Table.class);
    if ( table == null ) {
      throw new Exception(clazz+"对应的数据库表名不能为空");
    }
    this.tableName = table.name().toUpperCase();
    String[] columns = columMap.get(key);
    if ( columns != null ) { //如果已经存在列，不用再去请求
      return;
    }
    Field[] fields=clazz.getDeclaredFields();
    initSelfFiled(fields, object); 
    
    Class<?> superClazz = clazz.getSuperclass();
    initSuperFiled(superClazz, object);
  }
  
  public String getInsertSQL() {
    StringBuffer sql = new StringBuffer();
    sql.append(" insert into ");
        sql.append( tableName );
        sql.append("(");
        for ( int index=0; index<colums.size(); index++ ) {
          String column = colums.get(index);
          sql.append( column );
          if ( index < colums.size() - 1) {
            sql.append(",");
          }
        }
        sql.append(") ");
    sql.append(" values (");
        for ( int index=0; index<colums.size(); index++ ) {
          sql.append( "?" );
          if ( index < colums.size() - 1 ) {
            sql.append(",");
          }
        }
    sql.append(")");
    return sql.toString();
  }
  
  public String getSelectSQL() {
    StringBuffer sql = new StringBuffer();
    sql.append(" select ");
    for ( int index=0; index<_colums.size(); index++ ) {
      String column = _colums.get(index);
      sql.append( column );
      sql.append(" AS \""+ _filedNames.get(index) +"\"");
      if ( index < _colums.size() - 1) {
        sql.append(",");
      }
    }
    sql.append(" FROM "+ tableName );
    return sql.toString();
  }
    
  public String getUpdateSQL() {
    StringBuffer sql = new StringBuffer();
    sql.append( " update ");
    sql.append( tableName );
    sql.append( " SET " );
    for ( int index=0; index<colums.size(); index++ ) {
      String column = colums.get(index);
      sql.append( column +"=?" );
      if ( index < colums.size() - 1) {
        sql.append(",");
      }
    }
    return sql.toString();
  }
  
  public String getDeleteSQL() {
    StringBuffer sql = new StringBuffer();
    sql.append(" delete ");
    sql.append(" from ");
    sql.append(" "+ tableName + " ");
    return sql.toString();
  }
  
  private Object initValue( String typeName, String columName ) throws Exception {
    if ( "java.lang.Integer".equals(typeName) ) {
      return new Integer(0);
    } else if ( "java.lang.Double".equals(typeName) ) {
      return new Double(0); 
    } else if ( "java.lang.String".equals(typeName) ) {
      return new String("");
    } else if ( "java.lang.Short".equals(typeName) ) {
      return (short)0;
    } else if ( "java.lang.Long".equals(typeName) ) {
      return new Long(0);
    } else if ( "java.math.BigDecimal".equals(typeName)) {
      return new BigDecimal(0);
    } else if ( "java.sql.Timestamp".equals(typeName) ) {
      return new Timestamp( new Date().getTime() );
    } else if ( "java.sql.Blob".equals(typeName) ) {
      SerialBlob b = new SerialBlob("".getBytes("UTF-8"));
      return b;
    } else if ("java.sql.Clob".equals(typeName)) {
      SerialClob c = new SerialClob("".toCharArray());
      return c;
    } else if ( "java.util.Date".equals(typeName) ) {
      return new Date();
    } else if ("java.lang.Float".equals(typeName)) {
      return new Float(0);
    }
    return "";
  }
  
  private void initFieldType( String typeName, String columName ) throws Exception {
    if ( "java.lang.Integer".equals(typeName) ) {
      _filedTypes.put(columName, Integer.class);
    } else if ( "int".equals(typeName) ) {
      _filedTypes.put(columName, int.class);
    } else if ( "java.lang.Double".equals(typeName) ) {
      _filedTypes.put(columName, Double.class);
    } else if ( "double".equals(typeName) ) {
      _filedTypes.put(columName, double.class);
    } else if ( "java.lang.String".equals(typeName) ) {
      _filedTypes.put(columName, String.class);
    } else if ( "java.lang.Short".equals(typeName)) {
      _filedTypes.put(columName, Short.class);
    } else if ( "short".equals(typeName) ) {
      _filedTypes.put(columName, short.class);
    } else if ( "java.lang.Long".equals(typeName) ) {
      _filedTypes.put(columName, Long.class);
    } else if ( "long".equals(typeName) ) {
      _filedTypes.put(columName, long.class);
    } else if ( "java.math.BigDecimal".equals(typeName)) {
      _filedTypes.put(columName, BigDecimal.class);
    } else if ( "java.sql.Timestamp".equals(typeName) ) {
      _filedTypes.put(columName, Timestamp.class);
    } else if ( "java.sql.Blob".equals(typeName) ) {
      _filedTypes.put(columName, SerialBlob.class);
    } else if ("java.sql.Clob".equals(typeName)) {
      _filedTypes.put(columName, SerialClob.class);
    }  else if ( "java.util.Date".equals(typeName) ) {
      _filedTypes.put(columName, Date.class);
    } else if ("java.lang.Byte".equals(typeName) ) {
      _filedTypes.put(columName, Byte.class);
    } else if ( "byte".equals(typeName) ) {
      _filedTypes.put(columName, byte.class);
    } else if ( "java.lang.Boolean".equals(typeName) ) {
      _filedTypes.put(columName, Boolean.class);
    } else if (  "boolean".equals(typeName) ) {
      _filedTypes.put(columName, boolean.class);
    } else if ( "java.lang.Character".equals(typeName) ) {
      _filedTypes.put(columName, Character.class);
    } else if ( "char".equals(typeName) ) {
      _filedTypes.put(columName, char.class);
    } else if (  "java.lang.Float".equals(typeName)) {
      _filedTypes.put(columName, Float.class);
    } else if ( "float".equals(typeName) ) {
      _filedTypes.put(columName, float.class);
    }
  }
  
  
  public void initSelfFiled(Field[] fields, Object object) throws Exception {
    for ( Field field : fields ) {
      Column fieldAnnotation=field.getAnnotation(Column.class);//对应数据库中的列明
      if ( fieldAnnotation == null ) {
        continue;
      }
      field.setAccessible(true);
      Object value = field.get(object);//真实的值
      String columName = fieldAnnotation.name();//字段名称
      String comment = fieldAnnotation.comment();
      _filedNames.add( field.getName() );
      _colums.add( columName.toUpperCase() );
      Class<?> type = field.getType();
      String typeName = type.getName();
      if ( value == null ) {
        value = initValue(typeName, field.getName());
      }
      initFieldType(typeName, field.getName() );
      _values.add(value);
      if ( fieldAnnotation.id() && fieldAnnotation.isAutoIncrement() ) { //是自增长的主键
        primaryKey = columName;
        continue;
      }
      colums.add(columName.toUpperCase());
      values.add(value);
      if ( CommUtils.isNotNull(comment) ) {
        JSONObject obj = new JSONObject();
        obj.put("colName", field.getName());
        obj.put("comment", comment);
        commentList.add(obj);
      }
    }
  }
  
  public void initSuperFiled(Class<?> superClazz, Object object) throws Exception {
    if ( superClazz != Object.class ) {
      Field[] fields=superClazz.getDeclaredFields();
      for ( Field field : fields ) {
        Column fieldAnnotation=field.getAnnotation(Column.class);//对应数据库中的列明
        if ( fieldAnnotation == null ) {
          continue;
        }
        field.setAccessible(true);
        Object value = field.get(object);//真实的值
        String columName = fieldAnnotation.name();//字段名称
        String comment = fieldAnnotation.comment();
        if ( _filedNames.contains(field.getName()) ) {//如果已经有了就默认去掉
          continue;
        }
        _filedNames.add( field.getName() );
        _colums.add( columName.toUpperCase() );
        Class<?> type = field.getType();
        String typeName = type.getName();
        if ( value == null ) {
          value = initValue(typeName, field.getName());
        }
        initFieldType(typeName, field.getName() );
        _values.add(value);
        if ( fieldAnnotation.id() && fieldAnnotation.isAutoIncrement() ) { //是自增长的主键
          primaryKey = columName;
          continue;
        }
        colums.add(columName.toUpperCase());
        values.add(value);
        if ( CommUtils.isNotNull(comment) ) {
          JSONObject obj = new JSONObject();
          obj.put("colName", field.getName());
          obj.put("comment", comment);
          commentList.add(obj);
        }
      } 
    }
  }
}
