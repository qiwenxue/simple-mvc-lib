package core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import core.util.CommUtils;
import core.util.Const;
import core.util.RequestUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BaseAction {
  protected HttpServletRequest request;
  protected HttpServletResponse response;
  protected HttpSession session; 
  protected static final String SUCCESS = Const.SUCCESS;
  protected static final String ERROR = Const.ERROR;
  protected CoreDao coreDao;
  protected Result<String> result;
    
  public Result<String> getResult() {
    return result;
  }
  public void setResult(Result<String> result) {
    this.result = result;
  }
  public HttpSession getSession() {
    return session;
  }
  public void setSession(HttpSession session) {
    this.session = session;
  }
  public CoreDao getDao() {
    if ( coreDao == null ) {
      coreDao = CoreDaoInvoke.getInstance();
    }
    return coreDao;
  }
  public void setDao(CoreDao dao) {
    this.coreDao = dao;
  }

  public BaseAction() {
    result = new Result<String>();
  }
  
  protected HttpServletRequest getRequest() {
    return request;
  }
  
  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }
  
  protected HttpServletResponse getResponse() {
    return response;
  }
  public void setResponse(HttpServletResponse response) {
    this.response = response;
  }
  
  protected <T> T getBean( Class<T> entityClass ) throws Exception {
    return RequestUtil.copyParam(entityClass, request);
  }
  
  /**
   *  跳转
   * @param url
   * @return
   */
  protected void setForword( String key ) {
    JSONObject obj = new JSONObject();
    obj.put(Const.RESPONSE.ERR_CODE, 0);
    obj.put(Const.RESPONSE.KEY, key);
    result.setResult(obj.toString(), Const.RESPONSE.FORWORD);
  }
    
  /**
   * 重定向
   * @param url
   * @return
   */
  protected void setRedirect( String key ){
    JSONObject obj = new JSONObject();
    obj.put(Const.RESPONSE.ERR_CODE, 0);
    obj.put(Const.RESPONSE.KEY, key);
    result.setResult(obj.toString(), Const.RESPONSE.REDIRECT);
  }
    
  /**
   * 采用ajax方式把Map转换成功json结果集
   * @param status
   * @param data
   * @param errMsg
   * @return
   */
  @Deprecated
  protected void setMap2Json(int status, List<Map<String, Object>> data, String errMsg ) {
    JSONObject obj = new JSONObject();
    JSONObject resultObj = new JSONObject();
    JSONArray array = new JSONArray();
    if ( data != null && data.size() > 0) {
      for ( int i=0; i<data.size(); i++ ) {
        Map<String, Object> map = data.get(i);
        JSONObject mapObj = map2Json( map );
        array.add(mapObj);
      }
    }
    obj.put(Const.RESPONSE.ERR_CODE, String.valueOf(status));
    obj.put(Const.RESPONSE.DATA, array);
    obj.put(Const.RESPONSE.ERRMSG, errMsg);
    resultObj.put(Const.RESPONSE.DATA, obj);
    result.setResult(resultObj.toString(), Const.RESPONSE.AJAX);
  }
  
  /**
   * 采用ajax方式把Map转换成功Xml结果集
   * @param status
   * @param data
   * @param errMsg
   * @return
   */
  @Deprecated
  protected void setMap2Xml(int status, List<Map<String, Object>> data, String errMsg ) {
    JSONObject resultObj = new JSONObject();
    StringBuffer xml = new StringBuffer();
    xml.append("<?xml version='1.0' encoding='UTF-8'?>");
    xml.append("<entitys>");
    if ( data != null && data.size() > 0) {
      for ( int i=0; i<data.size(); i++ ) {
        Map<String, Object> map = data.get(i);
        String entity = map2Xml(map);
        xml.append( entity );
      }
    }
    xml.append("</entitys>");
    resultObj.put(Const.RESPONSE.DATA, xml.toString());
    result.setResult(resultObj.toString(), Const.RESPONSE.AJAX);
  }
      
  @Deprecated
  protected void setMapResult(int status, Map<String, Object> data) {
    JSONObject obj = new JSONObject();
    JSONObject resultObj = new JSONObject();
    obj.put(Const.RESPONSE.ERR_CODE, String.valueOf(status));
    obj.put(Const.RESPONSE.DATA, map2Json( data ));
    resultObj.put(Const.RESPONSE.DATA, obj);
    result.setResult(resultObj.toString(), Const.RESPONSE.AJAX);
  }
  
  protected void setResult(int status, JSONObject data, String errMsg) {
    setResult(status, data.toString(), errMsg);
  }
  
  protected void setResult(int status, String data, String errMsg) {
    JSONObject obj = new JSONObject();
    JSONObject resultObj = new JSONObject();
    obj.put(Const.RESPONSE.ERR_CODE, String.valueOf(status));
    obj.put(Const.RESPONSE.ERRMSG, errMsg);
    if ( CommUtils.isNotNull(data) ) {
      obj.put(Const.RESPONSE.DATA, data);
    }
    resultObj.put(Const.RESPONSE.DATA, obj);
    result.setResult(resultObj.toString(), Const.RESPONSE.AJAX);
  }
  
  protected void setResult(int status, String errMsg) {
    setResult( status, "", errMsg);
  }
  
  /**
   * 采用ajax提交
   * @param data
   * @return
   */
  protected void setData( String data ) {
    JSONObject resultObj = new JSONObject();
    resultObj.put(Const.RESPONSE.DATA, data);
    result.setResult(resultObj.toString(), Const.RESPONSE.AJAX);
  }
    
  private JSONObject map2Json( Map<String, Object> map ) {
    JSONObject obj = new JSONObject();
    if ( map != null && map.size() > 0) {
      for ( Map.Entry<String, Object> kv : map.entrySet()) {
        String key = kv.getKey();
        Object value = kv.getValue();
        obj.put(key, value == null ? "" : value );
      }
    }
    return obj;
  }
  
  private String map2Xml( Map<String, Object> map ) {
    StringBuffer xml = new StringBuffer();
    xml.append("<entity>");
    if ( map != null && map.size() > 0) {
      for ( Map.Entry<String, Object> kv : map.entrySet()) {
        String key = kv.getKey();
        Object value = kv.getValue();
        xml.append("<").append( key ).append(">");
        xml.append( CommUtils.encodeSpecile(value.toString()) );
        xml.append("</").append( key ).append(">");
      }
    }
    xml.append("</entity>");
    return xml.toString();
  }
  
  /**
   * 从body中获取请求参数
   * @TODO :  
   */
  public String getData(String charsetName) throws IOException{
    ServletInputStream is = request.getInputStream();
    byte[] data = readBytes(is);
    String requestString = new String(data,charsetName);
    return requestString;
  }
  
  /**
   * 从body中获取请求参数
   * @TODO :  
   */
  public String getData() throws IOException{ 
    ServletInputStream is = request.getInputStream();
    byte[] data = readBytes(is);
    String requestString = new String(data,"UTF-8");
    return requestString;
  }
  
  /**
   * 将输入流转换成字节
   */
  private static byte[] readBytes(InputStream inStream) {
    ByteArrayOutputStream baos = null;
    try {
      baos = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int len = -1;
      while ((len = inStream.read(buffer)) != -1) {
        baos.write(buffer, 0, len);
      }
      baos.close();
      inStream.close();
      return baos.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (inStream != null) {
          inStream.close();
        }
        if (baos != null) {
          baos.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
}
