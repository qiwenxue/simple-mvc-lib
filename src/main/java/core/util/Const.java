package core.util;
/**
 * 常量类
 * @author Administrator
 *
 */
public class Const {
 public static final String DEFAULT_CODE = "UTF-8";
 /**
  * 异常
  */
 public static final String EXCEPTION = "exception";
  
 /**
  * 验证码
  */
 public static final String CODE_IMG = "CODE_IMG";
 
 public final class LOAD{
   public static final String SERVICE_FIX = "service";//service的xml
   public static final String URL_WRITER_FIX = "urlWrter";
   public static final String BEAN = "bean";
   public static final String DAO_FIX = "dao"; //dao的xml
 }
  
 public final class SQL {
   public static final String INSERT = "insert";
   public static final String UPDATE = "update";
   public static final String DELETE = "delete";
   public static final String SELECT = "select";
   public static final String RESULT = "result";
   /**
    * 没有主键返回值
    */
   public static final int  NO_PRIMARY_KEY = 1; 
 }
  
 public final class ERRCODE {
   public static final int  IS_SUCCESS = 0;
   public static final int  IS_FAIL = 1;
   public static final int SYS_ERR = 500;
 }
 
 public final class RESPONSE{
   public static final int AJAX = 0;
   public static final int FORWORD = 1;
   public static final int REDIRECT = 2;
   public static final String DATA = "data";
   public static final String TYPE = "type";
   public static final String KEY = "key";
   public static final String STATE = "state";
   public static final String ERRMSG = "desc";
   public static final String ERR_CODE = "errCode";
 }
 
 public static final String SUCCESS = "SUCCESS";
 public static final String ERROR = "ERROR";
}
