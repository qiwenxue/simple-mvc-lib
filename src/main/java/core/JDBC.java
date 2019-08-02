package core;

public final class JDBC {
  public class ColumnType {
    public static final String CHAR = "CHAR";  //对应java String
    public static final String VARCHAR = "VARCHAR";//对应java String
    public static final String VARCHAR2 = "VARCHAR2";//对应java String
    public static final String VARCHAR3 = "VARCHAR3";//对应java String
    public static final String LONGVARCHAR = "LONGVARCHAR";//对应java String
    public static final String LONGTEXT = "LONGTEXT";
    public static final String TEXT = "TEXT";
    
    public static final String NUMERIC = "NUMERIC";//对应java BigDecimal
    public static final String DECIMAL = "DECIMAL";//对应java BigDecimal
    public static final String BIT = "BIT"; //对应java Boolean
    
    public static final String TINYINT = "TINYINT"; //对应java Integer
    public static final String SMALLINT = "SMALLINT";//对应java Integer
    public static final String INTEGER = "INTEGER";//对应java Integer
    public static final String INT = "INT";//对应java Integer
    
    public static final String BIGINT = "BIGINT";//对应java Long
    public static final String REAL = "REAL";//对应java Float
    public static final String FLOAT = "FLOAT";//对应java Double
    public static final String DOUBLE = "DOUBLE";//对应java Double
    public static final String LONG = "LONG"; //对应java Long
    
    public static final String BINARY = "BINARY";//对应java byte[]
    public static final String VARBINARY = "VARBINARY";//对应java byte[]
    public static final String LONGVARBINARY = "LONGVARBINARY";//对应java byte[]
    
    public static final String DATE = "DATE";//对应ava.sql.Date
    public static final String TIME = "TIME";//对应ava.sql.Time
    public static final String TIMESTAMP = "TIMESTAMP";//对应ava.sql.Timestamp
    public static final String DATETIME = "DATETIME";//对应ava.sql.Timestamp
    public static final String BLOB = "BLOB";
    public static final String CLOB = "CLOB";
    public static final String NUMBER = "NUMBER";
  }
}
