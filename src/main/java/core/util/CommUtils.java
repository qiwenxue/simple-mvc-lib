package core.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import net.sf.json.JSONObject;
@SuppressWarnings("deprecation")
public class CommUtils {
	
	public static  Logger logger = Logger.getLogger(CommUtils.class);
	
	public static final String desKey =  "01FB975D64FEB63B";/* 必需的密钥 */
  
  public static Set<String> provinceNamesSet = new HashSet<String>();
    
  public static int corePoolSize  = 1;
  
  public static int maximumPoolSize  = 15;
  
  public static int keepAliveTime  = 3;

  public static final int REQUEST_TIME_OUT_DATA = 30000; //超时时间
  
  
  public static final String crtFilePath  = "";
    
  /**
   * 线程工具, 启动5个线程
   */
  public static ThreadPoolExecutor threadPoolExecutor =  new ThreadPoolExecutor(
      corePoolSize, 
      maximumPoolSize, 
      keepAliveTime, 
      TimeUnit.SECONDS ,  
      new ArrayBlockingQueue<Runnable>(2), 
      new ThreadPoolExecutor.CallerRunsPolicy()
  );

  public static boolean isNull(String str){
       
    
	 if(str == null || str.length() ==0 
	               || "null".equalsIgnoreCase(str) || str.trim().length() == 0){
		return true;
	 }
	 return false;
  }
  
  public static boolean isNotNull(String str){
    return !isNull(str);
  }
  
  public static String returnStr(String str){
	 if(isNull(str)){
	  return "";
	 }
	 return str;
  }
  
  /**
   * webservice特殊字符转换
   * @param xmlStr
   * @return
   */
  public static String encodeSpecile(String xmlStr){
	  String str = "";
	  if(isNull(xmlStr)){
		return "";
	  }
	    str = xmlStr.replace( "<", "&lt;");
	    str = str.replace(">", "&gt;");
	    str = str.replace("&", "&amp;");
	    str = str.replace("\"", "&quot;");
	    str = str.replace("'", "&apos;");
	    str = str.replace(">", "&gt;");
	  return str;
  }
  
  /**
   * 处理\ " ' 在json中的显示
   * @param srcStr
   * @return
   * @throws Exception
   */
  public static String encodeSpecial(String srcStr) {
    if (srcStr == null) {
      return "";
    }
    return srcStr.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\'", "\\\'")
                 .replaceAll("\r\n", "<div/>")
                 .replaceAll("\n", "<div/>")
                 .replaceAll("\r", "<div/>")
                 ;
  }
  
  /**
   * 去掉所有html标签
   * @param html
   * @return
   */
  public static String  cutHtml(String html){
    if ( isNull(html) ) {
      return "";
    }
    String result = "";
    result = html.replaceAll("(<[^/\\s][\\w]*)[\\s]*([^>]*)(>)", "$1$3")
                 .replaceAll("<[^>]*>", "");
    return result;
  }
  
  /**
   * 
   * @return
   */
  public static boolean isNullMap(Map<?,?> map){
	  if(map == null || map.size() == 0){
		 return true;
	  }
	  return false;
  }
  
  public static boolean isNotNullMap(Map<?,?> map){
	  return !isNullMap(map);
  }
  
  public static boolean isNullList(List<?> list){
	 if(list == null || list.size() ==0){
		 return true;
	 }
	 return false;
  }
  
  public static boolean isNotNullList(List<?> list){
	 return !isNullList(list);
  }
  
  public static int contNo = 6;
  
  /** 随机生成激活码**/
  public static String createActiveCode(){
	  String sRand = "";
	  Random random = new Random();
		for(int i=0; i<contNo; i++){      //一共显示6个数
		  int temp = random.nextInt(26)+65;
		  char ctmp = (char) temp;
		  sRand += String.valueOf(ctmp);
		}  
	 return sRand;
  }
  
  /** 随机生成新密码**/
  public static String createNewPass(){
	  String sRand = "";
	  Random random = new Random();
		for(int i=0; i<contNo; i++){      //一共显示6个数
		  int temp = random.nextInt(26)+96;
		  char ctmp = (char) temp;
		  sRand += String.valueOf(ctmp);
		}  
	 return sRand;
  }
    
  /**
   * 读取fileName文件里的key的值
   * @param key
   * @param fileName
   * @return
   */
  public static String getPropValByKey(String key, String fileName){
    ResourceBundle bundle = ResourceBundle.getBundle(fileName);
    String value = null;
    try {
      value = bundle.getString(key);
    } catch (Exception e) {
      logger.error("没有找到这个key："+ key+",返回NULL",e);
    }
    return value;
  }
	 
	
	public static String getGuid(){
	  return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
	}
    	
  /**
   * 转换sql语句中的like问题。
   * @param str
   * @param dbFieldName
   * @return
   * @throws SQLException
   */
  public static String escapeLike(String str, String dbms) throws SQLException{
    String result = "";
    if (dbms.equals("sqlserver")) {
      result = str.replace("[", "[[]").replace("_", "[_]").replace("%", "[%]").replace("\'", "\'\'");
    }else if (dbms.equals("mysql")) {
      result = str.replace("\\", "\\\\").replace("\"", "\\\"").replace("'", "\\'").replace("&", "\\&").replace("%", "\\%").replace("_", "\\_").replace("％", "\\％");
    }else if (dbms.equals("oracle")) {
      result = str.replace("\\", "\\\\").replace("\'", "\'\'").replace("%", "\\%").replace("_", "\\_").replace("％", "\\％");
    }else {
      throw new SQLException("not accepted dbms");
    }
    return result;
  }
  
  /**
   * 计算精度(四舍五入)
   */
  public static double round(double number, String partten){
    if(partten == null || partten == ""){
      partten = "#.00";
    }
    return Double.parseDouble(new DecimalFormat(partten).format(number));
  }
  
  /**
   * 把一个double类型的数据转换为字符串
   * @param value
   * @return
   */
  public static String formatNum(double value){
      String retValue = null;
      DecimalFormat df = new DecimalFormat();
      df.setMinimumFractionDigits(0);
      df.setMaximumFractionDigits(2);
      retValue = df.format(value);
      retValue = retValue.replaceAll(",", "");
      return retValue;
  }
  /**
   * 转换字符数字
   * @param doubleValue
   * @return
   */
  public static String formatNumStr( String doubleValue ){
    String doubleValueY = "0.00";
    if ( isNull( doubleValue )) {
      doubleValueY = "0.00";
    } else   if ( doubleValue.indexOf("￥") != -1) {
      doubleValueY = doubleValue.replace("￥", "");
    }  else {
      doubleValueY = doubleValue;
    }
    double b = Double.parseDouble( doubleValueY );
    if ( doubleValue.indexOf("￥") != -1 ) {
      return "￥" + b;
    }
    return formatNum( b );
  }
  /**
   * 截取数字串
   * @param doubleValue
   * @return
   */
  public static String formatNOStr(String doubleValue) {
    if ( isNull(doubleValue) ) {
      return "0.00";
    } else {
      int b = doubleValue.indexOf(".");
      if ( b == -1) { //没有小数点
        return doubleValue + ".00";
      } else {
        String k = doubleValue.substring( (b+1), doubleValue.length());
        if (k.length() == 1) {
          return doubleValue + "0";
        } 
      }
    }
    return doubleValue;
  }
  
  /**
   * 截取数字字符
   * @param doubleValue
   * @return
   */
  public static String doubleStr2Integer(String doubleValue){
    if ( "0.00".equalsIgnoreCase(doubleValue) || "0.0".equalsIgnoreCase(doubleValue) || isNull(doubleValue)) {
      return "0";
    } else {
      int last = doubleValue.indexOf(".");
      if ( last != -1 ) {
        String s = doubleValue.substring(0, doubleValue.indexOf("."));
        return s;
      } else {
        return doubleValue;
      }
    }
  }
  
  
  /**
   * 调用url，返回json
   * @param url
   * @param paramMap
   * @return
   * @throws IOException
   */
  public static String getHttpClientResult(String url, Map<String, String> paramMap) throws IOException {
    HttpClient client = new HttpClient();
    PostMethod post = new PostMethod(url);
    post.setRequestHeader("Connection", "close");
    try {
      if ( isNotNullMap(paramMap) ) {
        Set<String> keySets = paramMap.keySet();
        for ( String key : keySets ) {
          String paramKey = key;
          String paramValue = paramMap.get(paramKey);
          post.addParameter(paramKey, paramValue);
        }
      }
      post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
      client.getHttpConnectionManager().getParams().setConnectionTimeout(120000);//10秒超时
      client.getHttpConnectionManager().getParams().setSoTimeout(120000);
      client.executeMethod(post);
      String s = post.getResponseBodyAsString();
      return s;
    } catch (HttpException e) {
      logger.error("connect to "+ url +" error :" + e.getMessage());
      e.printStackTrace();
      throw e;
    } catch (IOException e) {
      logger.error("connect to "+ url +" io error:" + e.getMessage());
      e.printStackTrace();
      throw e;
    } finally {
      post.releaseConnection();
    }
  }
  
  /**
   * 调用url，返回json
   * @param url
   * @param paramMap
   * @return
   * @throws IOException
   */
  public static JSONObject getHttpClientResultJson(String url, Map<String, String> paramMap) throws IOException {
    HttpClient client = new HttpClient();
    PostMethod post = new PostMethod(url);
    post.setRequestHeader("Connection", "close");
    JSONObject js =  null;
    try {
      if ( isNotNullMap(paramMap) ) {
        Set<String> keySets = paramMap.keySet();
        for ( String key : keySets ) {
          String paramKey = key;
          String paramValue = paramMap.get(paramKey);
          post.addParameter(paramKey, paramValue);
        }
      }
      post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
      client.getHttpConnectionManager().getParams().setConnectionTimeout(120000);//10秒超时
      client.getHttpConnectionManager().getParams().setSoTimeout(120000);
      client.executeMethod(post);
      String jsobject = post.getResponseBodyAsString();
      js = JSONObject.fromObject(jsobject);
    } catch (HttpException e) {
      logger.error("connect to "+ url +" error :" + e.getMessage());
      e.printStackTrace();
      throw e;
    } catch (IOException e) {
      logger.error("connect to "+ url +" io error:" + e.getMessage());
      e.printStackTrace();
      throw e;
    } finally {
      post.releaseConnection();
    }
    return js;
  }
  
  /**
   * 转全角的函数(SBC case) 
   * 全角空格为12288，半角空格为32
   * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
   *
   * @param input
   *            任意字符串
   * @return 全角字符串
   */
  public static String toSBC(String input) {
    if (isNull(input)) {
      return "";
    }
    // 半角转全角：
    char[] c = input.toCharArray();
    for (int i = 0; i < c.length; i++) {
      if (c[i] == 32) {
        c[i] = (char) 12288;
        continue;
      }
      if (c[i] < 127)
        c[i] = (char) (c[i] + 65248);
    }
    return new String(c);
  }

  /**
   * 转半角的函数(DBC case) 全角空格为12288，半角空格为32
   * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
   *
   * @param input
   *            任意字符串
   * @return 半角字符串
   */
  public static String toDBC(String input) {
    if (isNull(input)) {
      return "";
    }
    char[] c = input.toCharArray();
    for (int i = 0; i < c.length; i++) {
      if (c[i] == 12288) {
        c[i] = (char) 32;
        continue;
      }
      if (c[i] > 65280 && c[i] < 65375)
        c[i] = (char) (c[i] - 65248);
    }
    return new String(c);
  }
  /**
   * 正则验证电子邮件
   * @param email
   * @return
   */
  public static boolean isEmail( String email ) {
    if ( isNull( email ) ) {
      return false;
    }
    email = email.toLowerCase();
    String str="^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    Pattern p = Pattern.compile(str);     
    Matcher m = p.matcher(email);     
    return m.matches();     
  }
  
  /**
   * 验证是否是手机号
   * @param phone
   * @return
   */
  public static boolean isMobilePhone( String phone) {
    if ( isNull(phone) ) {
      return false;
    }
    Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$");  
    Matcher m = p.matcher(phone);  
    return m.matches();  
  }
  /**
   * 正则验证固定电话
   * @param phone
   * @return
   */
  public static boolean isPhone( String phone ) {
    if ( isNull(phone) ) {
      return false;
    }
    Pattern p = Pattern.compile("^((\\d{3,4})|\\d{3,4}-)?\\d{7,8}$");
    Matcher m = p.matcher(phone);
    return m.matches();
  }
  /**
   * 验证是不是电话号码
   * @return
   */
  public static boolean isMobileOrPhone( String phone ) {
    if ( isMobilePhone(phone) || isPhone(phone) ) {
      return true;
    }
    return false;
  }
  
  
  /**
   * 获取访问请求的真实IP
   * @param args
   */
/*  public static String getIpAddr(HttpServletRequest request) {        
     String ip = request.getHeader("x-forwarded-for");        
      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {        
         ip = request.getHeader("Proxy-Client-IP");        
     }        
      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {        
         ip = request.getHeader("WL-Proxy-Client-IP");        
      }        
     if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {        
          ip = request.getRemoteAddr();        
     }        
     return ip;        
  }*/
  
  /**
   * 判断有没有乱码
   * @param str
   * @return
   */
  public static boolean isGoodString( String str ) {
     Pattern p = Pattern.compile("^[0-9]{0,}[a-zA-Z]{0,}[\u4e00-\u9fa5]{0,}$");  
     Matcher m = p.matcher(str);  
     return m.matches();  
  }
  
  
  public static String getRandomNum() {
    String result = "";
    for (int i=0; i<6; i++) {
      int k = (int)(Math.random() * 9);
      result += k;
    }
    return result;
  }
  
  public static String getRandomNum( int fix ) {
    String result = "";
    for (int i=0; i<fix; i++) {
      int k = (int)(Math.random() * 9);
      result += k;
    }
    return result;
  }
  
  public static String DEFAULT_CODE_TYPE = "UTF-8";
  
  /**
   * 采用UTF8编码
   * @param source
   * @return   
   */
  public static String encode(String source){
  String dest = "";
  if(CommUtils.isNull(source)){
   return "";
  }
  try {
    dest = URLEncoder.encode(source, DEFAULT_CODE_TYPE);
  } catch (UnsupportedEncodingException e) {
    e.printStackTrace();
  }
   return dest;
  }
  /**
   * 采用指定的编码方式编码
   * @param source
   * @param code
   * @return
   */
  public static String encode(String source, String code){
  String dest = "";
  try {
   dest = URLEncoder.encode(source, code);
  } catch (UnsupportedEncodingException e) {
   e.printStackTrace();
  }
  return dest;
  }
  /**
   * 采用UTF8解码
   * @param source
   * @return
   */
  public static String decode(String source){
   if(CommUtils.isNull(source)){
     return "";
   }
   String dest = "";
   try {
    dest = URLDecoder.decode(source, DEFAULT_CODE_TYPE);
   } catch (UnsupportedEncodingException e) {
     e.printStackTrace();
   }
   if ( !CommUtils.isNull(dest) ) {
     dest = dest
     .replaceAll("\r\n", "")
     .replaceAll("\n", "")
     .replaceAll("\r", "");
   }
    return dest;
  }
  
  /**
   * 解2次编码
   * @param source
   * @return
   */
  public static String decode2time( String source ) {
    return decode(decode(source));
  }
  
  /**
   * 采用UTF8解码
   * @param source
   * @return
   */
  public static String decode(String source, String code){
    if(CommUtils.isNull(source)){
      return "";
    }
   String dest = "";
   try {
    dest = URLDecoder.decode(source, code);
   } catch (UnsupportedEncodingException e) {
     e.printStackTrace();
   }
  return dest;
  }
  
  /**
   * 读取xml文件
   * @throws Exception 
   */
  @SuppressWarnings("unused")
  public static Map<String, String> getElement(String xmlDoc) throws DocumentException{
    Map<String, String> map = new HashMap<String, String>();
    //创建一个新的字符串
    try {
      StringReader read = new StringReader(xmlDoc);
      //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
      InputSource source = new InputSource(read);
      //创建一个新的SAXBuilder
      SAXReader reader = new SAXReader();
      //通过输入源构造一个Document
      Document doc = reader.read(source);
      //取的根元素
      Element root = (Element) doc.getRootElement();
      String rootName = root.getName();
      for(Iterator<?> i_obj = root.elementIterator(); i_obj.hasNext();){
         Element e_obj = (Element)i_obj.next();
      //获取当前元素的名字
       String nodeName = e_obj.getName();
       String nodeValue = e_obj.getText();
       //logger.info("节点名称:"+ nodeName+"----value:"+ nodeValue); 
         map.put(nodeName, nodeValue);
      }
    } catch (DocumentException e) {
      throw e;
    } 
   return map;
  }
  
  public static Map<String, String> getSimpleElement( String xmlDoc ) throws DocumentException {
    Map<String, String> map = new HashMap<String, String>();
    try {
      StringReader read = new StringReader(xmlDoc);
      //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
      InputSource source = new InputSource(read);
      //创建一个新的SAXBuilder
      SAXReader reader = new SAXReader();
      //通过输入源构造一个Document
      Document doc = reader.read(source);
      //取的根元素
      Element root = (Element) doc.getRootElement();
      String rootName = root.getName();
      String rootVlaue = root.getText();
      map.put(rootName, rootVlaue);
    } catch (DocumentException e) {
      throw e;
    } 
   return map;
  }
  
  public static String getHttpResultByGet( String url ) throws HttpException, IOException {
    GetMethod  method = new GetMethod( url );
    HttpClient httpClient = new HttpClient();
    int code = httpClient.executeMethod(method);
    String result = "";
    if ( code == 200 ) {
      result = method.getResponseBodyAsString();
    }
    return result;
  }
  public static JSONObject getHttpResultJsonByGet( String url ) throws HttpException, IOException {
    String json = getHttpResultByGet( url );
    JSONObject obj = JSONObject.fromObject( json );
    return obj;
  }
  
  /**
   * 读取xml字符串到一个document
   * @param xmlDoc
   * @return
   * @throws DocumentException
   */
  public static Document loadXMLStr(String xmlDoc) throws DocumentException{
    StringReader read = new StringReader(xmlDoc);
    //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
    InputSource source = new InputSource(read);
    //创建一个新的SAXBuilder
    SAXReader reader = new SAXReader();
    //通过输入源构造一个Document
    Document doc = reader.read(source); 
    return doc;
  }
  
  public static String getLocalIP() {
  	InetAddress addr = null;
  	try {
  		addr = InetAddress.getLocalHost();
  	} catch (UnknownHostException e) {
  		e.printStackTrace();
  		return null;
  	}
  	byte[] ipAddr = addr.getAddress();
  	String ipAddrStr = "";
  	for (int i = 0; i < ipAddr.length; i++) {
  		if (i > 0) {
  			ipAddrStr += ".";
  		}
  		ipAddrStr += ipAddr[i] & 0xFF;
  	}
  	System.out.println(ipAddrStr);
  	return ipAddrStr;
  }
  
  /**
   * 通过httpPost的方式进行网络请求获取到返回结果
   * 
   * @return
   * @throws Exception 
   */
  @SuppressWarnings("resource")
  public static String doPost(String requestPath, Map<String, Object> requestValues) throws Exception {
    // 发出HTTP request
    HttpPost httpRequest = null;// 请求对象
    DefaultHttpClient client = new DefaultHttpClient();
    try {
      List<NameValuePair> params = new ArrayList<NameValuePair>();// 请求值
      HttpResponse httpResponse;// 请求响应对象
      /* 建立HttpPost连接 */
      httpRequest = new HttpPost(requestPath);
      /* Post运作传送变数必须用NameValuePair[]阵列储存 */
      params = new ArrayList<NameValuePair>();
      if ( requestValues != null && requestValues.size() > 0 ) {
        Iterator<String> keys = requestValues.keySet().iterator();
        String key;
        while (keys.hasNext()) {
          key = keys.next();
          if ( key != null && key.length() > 0 ) {
            Object value = requestValues.get(key);
            if (value != null) {
              params.add(new BasicNameValuePair(key, value.toString()));
            }
          }
        }
      }
      httpRequest.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
      httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
      BasicHttpParams httpParameters = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(httpParameters,REQUEST_TIME_OUT_DATA);
      HttpConnectionParams.setSoTimeout(httpParameters, REQUEST_TIME_OUT_DATA);
      httpRequest.setParams(httpParameters);
      //httpRequest.setHeader("Accept-Encoding", "gzip");
      httpResponse = client.execute(httpRequest);
      int code = httpResponse.getStatusLine().getStatusCode();
      if (code == 200) {
       // Header head = httpResponse.getEntity().getContentEncoding(); // 检查压缩算法
        String strResult = EntityUtils.toString(httpResponse.getEntity());
        return strResult;
      } else {
        throw new Exception("连接服务器失败:返回码"+ code);
      }
    } catch (Exception e1) {
      e1.printStackTrace();
      throw new Exception("连接超时", e1);
    } finally {
      if ( client != null ) {
        client.getConnectionManager().shutdown();
      }
    }
  }
  
  /**
   * 通过GET的方式进行网络请求获取到返回结果
   * 
   * @return
   * @throws Exception 
   */
  public static String doGet( String url, Map<String, Object> paramMap) throws Exception {
    StringBuffer sb = new StringBuffer("?");
    String paramUrl = "";
    if ( paramMap != null && paramMap.size() > 0 ) {
      for ( Map.Entry<String, Object> map : paramMap.entrySet() ) {
        sb.append( map.getKey() +"=" + map.getValue() );
        sb.append("&");
      }
      paramUrl = sb.substring(0, sb.lastIndexOf("&"));
    }
    HttpGet get=new HttpGet(url + paramUrl);  
    @SuppressWarnings("resource")
    DefaultHttpClient client=new DefaultHttpClient();
    try {  
      HttpResponse response=client.execute(get);//执行Get方法   
      BasicHttpParams httpParameters = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(httpParameters,REQUEST_TIME_OUT_DATA);
      HttpConnectionParams.setSoTimeout(httpParameters, REQUEST_TIME_OUT_DATA);
      String result=EntityUtils.toString(response.getEntity()); 
      int code = response.getStatusLine().getStatusCode();
      if ( code == 200 ) {
        return result;
      } else {
        throw new Exception("连接服务器失败:返回码"+ code);
      }
    } catch (Exception e) {  
      e.printStackTrace();
      throw new Exception("连接超时", e);
    } finally {
      if ( client != null ) {
        client.getConnectionManager().shutdown();
      }
    }
  }
  
  /**
   * 通过java程序上传附件
   * @param url 地址
   * @param uploadFiles 文件列表
   * @param textMap   文本参数
   * @return
   * @throws Exception
   * @throws IOException
   */
  public static String upload( String url, FormFile[] uploadFiles, Map<String, Object> textMap ) throws Exception, IOException {
    @SuppressWarnings("resource")
    DefaultHttpClient httpclient = new DefaultHttpClient();
    String params = "";
    if ( textMap != null ) {
      int i = 0 ;
      for ( Map.Entry<String, Object> entry : textMap.entrySet() ) {  //此处为了防止从reqeust.getParamers()中获取不到文本参数，拼接url
        params += entry + "=" + CommUtils.encode( entry.getValue().toString());
        if ( i < textMap.size()-1 ) {
          params += "&";
        }
        i++;
      }
    }
    HttpPost post = new HttpPost(url + "?" + params);
    MultipartEntityBuilder builder = MultipartEntityBuilder.create(); //文件传输
    for ( FormFile formFile : uploadFiles) {
      File file = new File( formFile.getFilePath() ); 
      builder.addBinaryBody(formFile.getParameterName(), file);
    }
    if ( textMap != null ) {
      for ( Map.Entry<String, Object> entry : textMap.entrySet() ) {  //把文本参数加到body中
        ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
        builder.addTextBody(entry.getKey(), entry.getClass().toString(), contentType);
      }
    }
    HttpEntity mpEntity = builder.build();
    post.setEntity(mpEntity);
    HttpEntity resEntity = null;
    try {
      HttpResponse response = httpclient.execute(post);
      resEntity = response.getEntity();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (resEntity != null) {
        resEntity.consumeContent();
      }
      httpclient.getConnectionManager().shutdown();
    }
    return EntityUtils.toString(resEntity,"utf-8");
  }
  
  /**
   * post请求(参数放到body里)
   * @param url
   * @param uploadFiles
   * @param textMap
   * @return
   * @throws Exception
   * @throws IOException
   */
  @SuppressWarnings("resource")
  public static String post( String url, Map<String, Object> textMap ) throws Exception, IOException {
    DefaultHttpClient httpclient = new DefaultHttpClient();
    String requestUrl = url ;
    HttpPost post = new HttpPost(requestUrl);
    MultipartEntityBuilder builder = MultipartEntityBuilder.create(); //文件传输
    post.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
    BasicHttpParams httpParameters = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(httpParameters,30000);
    HttpConnectionParams.setSoTimeout(httpParameters, 30000);
    if ( textMap != null ) {
      for ( Map.Entry<String, Object> entry : textMap.entrySet() ) {
        ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
        builder.addTextBody(entry.getKey(), entry.getValue().toString(), contentType);//设置请求参数
      }
    }
    HttpEntity mpEntity = builder.build();
    post.setEntity(mpEntity);
    HttpEntity resEntity = null;
    String result = "";
    try {
      HttpResponse response = httpclient.execute(post);
      resEntity = response.getEntity();
      result = EntityUtils.toString(resEntity,"utf-8");
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }  finally {
      if ( httpclient != null ) {
        httpclient.getConnectionManager().shutdown();
      }
    }
    return result;
  }
  
  public static void main(String[] args) throws DocumentException {
    threadPoolExecutor.execute(new Runnable() {
      
      @Override
      public void run() {
        // TODO Auto-generated method stub
        
      }
    });
    
    Future<?> future = threadPoolExecutor.submit( new Runnable() {
      
      @Override
      public void run() {
         
      }
    });
    try {
      future.get();
    } catch (InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  /**
   * 获取IP地址
   * @param request
   * @return
   */
  public static String getIpAddr(HttpServletRequest request) {
    if (null == request) {
      return null;
    }
    String proxs[] = { 
        "X-Forwarded-For"
      , "Proxy-Client-IP"
      , "WL-Proxy-Client-IP"
      , "HTTP_CLIENT_IP"
      , "HTTP_X_FORWARDED_FOR" 
      ,"x-real-ip" 
    };
    String ip = null;
    for (String prox : proxs) {
      ip = request.getHeader(prox);
      if (CommUtils.isNull(ip) || "unknown".equalsIgnoreCase(ip)) {
        continue;
      } else {
        break;
      }
    }
    if ( CommUtils.isNull(ip) ) {
      return request.getRemoteAddr();
    }
    return ip;
 }
  
  /**
   * 通过httpPost的方式进行网络请求获取到返回结果
   * 
   * @return
   * @throws Exception 
   */
  public static String doHttpsPost( String requestPath, Map<String, Object> requestValues) throws Exception {
    // 发出HTTP request
    HttpPost httpRequest = null;// 请求对象
    DefaultHttpClient client = new DefaultHttpClient();
    try {
      List<NameValuePair> params = new ArrayList<NameValuePair>();// 请求值
      HttpResponse httpResponse;// 请求响应对象
      /* 建立HttpPost连接 */
      httpRequest = new HttpPost(requestPath);
      /* Post运作传送变数必须用NameValuePair[]阵列储存 */
      params = new ArrayList<NameValuePair>();
      Iterator<String> keys = requestValues.keySet().iterator();
      String key;
      while (keys.hasNext()) {
        key = keys.next();
        if ( key != null && key.length() > 0 ) {
          Object value = requestValues.get(key);
          if (value != null) {
            params.add(new BasicNameValuePair(key, value.toString()));
          }
        }
      }
      BasicHttpParams httpParameters = new BasicHttpParams();
      httpRequest.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
      httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
      
      HttpConnectionParams.setConnectionTimeout(httpParameters,REQUEST_TIME_OUT_DATA);
      HttpConnectionParams.setSoTimeout(httpParameters, REQUEST_TIME_OUT_DATA);
      httpRequest.setParams(httpParameters);
      
      SchemeRegistry registry = new SchemeRegistry();
      registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 8280));
      registry.register(new Scheme("https", TrustCertainHostNameFactory.getDefault(crtFilePath), 8380));
      ClientConnectionManager manager = new ThreadSafeClientConnManager(httpParameters, registry);
      client = new DefaultHttpClient(manager, httpParameters);
           
      // shenxy 设置压缩算法
      //httpRequest.setHeader("Accept-Encoding", "gzip");
      httpResponse = client.execute(httpRequest);
      int code = httpResponse.getStatusLine().getStatusCode();
      if (code == 200) {
        Header head = httpResponse.getEntity().getContentEncoding(); // 检查压缩算法
        String strResult = EntityUtils.toString(httpResponse.getEntity());
        return strResult;
      } else {
        throw new Exception("连接服务器失败:返回码"+ code);
      }
    } catch (Exception e1) {
      e1.printStackTrace();
      throw new Exception("连接超时", e1);
    } finally {
      if ( client != null ) {
        client.getConnectionManager().shutdown();
      }
    }
  }
  
  public static String doHttpsGet(String url, HashMap<String, String> paramMap) throws Exception {
    StringBuffer sb = new StringBuffer("?");
    String paramUrl = "";
    if ( paramMap != null ) {
      for ( Map.Entry<String, String> map : paramMap.entrySet() ) {
        sb.append( map.getKey() +"=" + map.getValue() );
        sb.append("&");
      }
      paramUrl = sb.substring(0, sb.lastIndexOf("&"));
    }
    
    SchemeRegistry registry = new SchemeRegistry();
    //registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    
    HttpGet get=new HttpGet(url + paramUrl);  
    DefaultHttpClient client = null;
    try { 
      BasicHttpParams httpParameters = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(httpParameters,REQUEST_TIME_OUT_DATA);
      HttpConnectionParams.setSoTimeout(httpParameters, REQUEST_TIME_OUT_DATA);
      registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 8280));
      registry.register(new Scheme("https", TrustCertainHostNameFactory.getDefault(crtFilePath), 8380));
      ClientConnectionManager manager = new ThreadSafeClientConnManager(httpParameters, registry);
      
      client = new DefaultHttpClient(manager, httpParameters);
      HttpResponse response=client.execute(get);//执行Post方法   
      
      String result=EntityUtils.toString(response.getEntity()); 
      int code = response.getStatusLine().getStatusCode();
      if ( code == 200 ) {
        return result;
      } else {
        throw new Exception("连接服务器失败:返回码"+ code);
      }
    } catch (Exception e) {  
      e.printStackTrace();
      throw new Exception("连接超时", e);
    } finally {
      if ( client != null ) {
        client.getConnectionManager().shutdown();
      }
    }
  }
}