package core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import core.util.CommUtils;
import core.util.Const;
import core.wrap.GZipRequestWrapper;
import core.wrap.InflateRequestWrapper;
import net.sf.json.JSONObject;

/**
 * 拦截
 * @author qiwx
 *
 */
public class DispatcherFileter implements Filter {
  private static Logger logger = core.util.Logger.getLogger(DispatcherFileter.class);
  @Override
  public void destroy() {
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) 
                                                  throws IOException, ServletException {
    
    CoreDao dao = CoreDaoInvoke.getInstance();
    Result<String> result = new Result<String>();
    HttpServletRequest request = (HttpServletRequest)req;
    HttpServletResponse response = (HttpServletResponse)resp;
    request.setCharacterEncoding("UTF-8");
    HttpSession session = request.getSession();
    String callback = request.getParameter("callbackfun");//解决js跨域问题
    String ce = request.getHeader("Content-Encoding"); //用来解压
    String uriKey = request.getRequestURI();
    String moudle = request.getContextPath();//部署路径
    Class<?> actionClassIntance = null;
    Object actionClassObject = null;
    logger.debug("--->>>>the request url:"+ uriKey+"----the app path:"+ moudle);
    try {
      request = parseRequest( ce, request );
      if ( !moudle.equals("/") && !moudle.equals("") ) {
        uriKey = uriKey.substring(moudle.length());
      }
      Unit unit = ReflectionCache.getClazz(uriKey);
      if ( unit == null ) {
        chain.doFilter(request, response);
        return;
      } 
      String methodName  = unit.getMethod();
      actionClassIntance = unit.getClazz();
      actionClassObject  = unit.getObj();//新对象，非单例
      unit.getDefaultMethodByName(Unit.METHOD_SETREQUEST).invoke(actionClassObject, request);////方法名称，参数类型
      unit.getDefaultMethodByName(Unit.METHOD_SETRESPONSE).invoke(actionClassObject, response);//
      unit.getDefaultMethodByName(Unit.METHOD_SETSESSION).invoke(actionClassObject, session);//
      unit.getDefaultMethodByName(Unit.METHOD_SETDAO).invoke(actionClassObject, dao);//
      unit.injectServices(actionClassObject, dao); //注入属性
      unit.injectParams( request, actionClassObject );     //把传过来的参数封装成对象
      Object res = actionClassIntance.getMethod( methodName ).invoke(actionClassObject);//执行类中的方法
      result = (Result<String>)actionClassIntance.getMethod(Unit.METHOD_GETRESULT).invoke(actionClassObject);
      logger.debug("--->>>>the request result:"+ result.getResult()+"---the response type:"+ result.getResponseType());
      if ( StringUtils.isEmpty(result.getResult()) || result == null ) {  
        logger.warn("---"+methodName+"-->没有返回值,忘记调用setResult了么？默认不处理...");  
        return;   
      }
      execute(result, callback, unit,request, response, res);
    } catch ( Exception e) {
      String msg = e.getMessage();
      try {
        if ( CommUtils.isNull(msg) ) {
          msg = e.getCause().getLocalizedMessage();
        }
      } catch (Exception e2) {
        msg = "系统内部错误";
      }
      JSONObject obj = new JSONObject();
      obj.put(Const.RESPONSE.STATE, Const.ERRCODE.IS_FAIL); //请求状态
      obj.put(Const.RESPONSE.ERR_CODE, Const.ERRCODE.SYS_ERR);//错误码
      obj.put(Const.RESPONSE.ERRMSG, msg);
      obj.put(Const.EXCEPTION, e.getMessage());
      ajaxMethod(obj.toString(),response);
      logger.error("数据出错了=>", e);
    } finally {
      result = null;
      actionClassObject = null;
      result = null;
    }
  }
  
  @Override
  public void init(FilterConfig config) throws ServletException {
  }
  
  /**
   * ajax方法
   * 
   * @param messages
   * @throws Exception
   */
  private void ajaxMethod( String messages, HttpServletResponse response ){
    PrintWriter out = null;
    try {
      response.setContentType("text/html;charset=UTF-8");
      response.setHeader("Cache-Control", "no-cache");
      response.setCharacterEncoding("UTF-8");
      out = response.getWriter();
      out.print(String.valueOf(messages));
    } catch (Exception e) {
      logger.error("ajaxMethod出错了=>", e);
    } finally {
      if (out != null) {
        out.flush();
        out.close();
      }
      messages = null;
    }
  }
    
  private HttpServletRequest parseRequest(  String ce, HttpServletRequest request ) throws Exception {
    if ( ce != null ) {
      if (ce.indexOf("deflate") >= 0 || ce.indexOf("inflate") >= 0) { 
        return new InflateRequestWrapper(request); 
      } 
      else if (ce.indexOf("gzip") >= 0) {
        return new GZipRequestWrapper(request);
      }
    }
    return request;
  }
  
  private void doResposeInGZip( String data,  HttpServletRequest request, HttpServletResponse response ) throws IOException {
	  String ac = request.getHeader("Accept-Encoding");
    response.setContentType("text/html;charset=UTF-8");
    response.setHeader("Cache-Control", "no-cache");
    response.setCharacterEncoding("UTF-8");
	  byte[] result = null;
	  if ( !StringUtils.isEmpty(ac) ) {
		  if ( "gzip".equalsIgnoreCase(ac)) {
			  ByteArrayOutputStream out  =  new  ByteArrayOutputStream();  
			  GZIPOutputStream gout  =  new  GZIPOutputStream(out);  
			  gout.write(data.getBytes("UTF-8")); 
			  gout.close();  
			  result = out.toByteArray();
			  response.setHeader("Content-Encoding","gzip");  
			  response.setHeader("Content-Length", result.length+""); 
			  out.close();
		  }
	  }
	  response.getOutputStream().write( result );
  }
  
  private void doResponse( String data, HttpServletRequest request, HttpServletResponse response) throws IOException {
	  String acceptEncode = request.getHeader("Accept-Encoding");//是否要求压缩
	  if ("gzip".equalsIgnoreCase(acceptEncode)) {
		  doResposeInGZip(data, request, response );
	  } else {
		  ajaxMethod( data, response ); 
	  }
  }
  
  private void execute( Result<String> result, String callbackFunction, Unit unit, 
                                     HttpServletRequest request, HttpServletResponse response, Object res ) throws Exception {
    int requestType = result.getResponseType();
    JSONObject returnObj = null;
    try {
      returnObj = JSONObject.fromObject(result.getResult());
      if (requestType == Const.RESPONSE.AJAX) {// ajax方法
        String rs = returnObj.getString(Const.RESPONSE.DATA);
        boolean isJsn = false;
        JSONObject obj2 = null;
        String resStr = "";
        try {
          obj2 = JSONObject.fromObject(rs);//验证是不是json数据
          isJsn = true;
        } catch (Exception e) {
          isJsn = false;
        }
        resStr = isJsn? obj2.toString(): rs;
        int state = 0;
        if ( res != null ) {
          if ( Const.SUCCESS.equals(res.toString())) {
            state = Const.ERRCODE.IS_SUCCESS;
          } else if (Const.ERROR.equals(res.toString())) {
            state = Const.ERRCODE.IS_FAIL;
          }
          if ( isJsn ) {
            obj2.put(Const.RESPONSE.STATE, state);
            resStr = obj2.toString();
          } 
        }
        if (CommUtils.isNull(callbackFunction)) {//处理跨域的问题，返回jsonp格式
          doResponse(resStr, request, response);
        } else {
          String callbackStr = callbackFunction + "(" + resStr + ")";
          doResponse(callbackStr, request, response);
        }
      } else if (Const.RESPONSE.FORWORD == requestType) {// 跳转
        String urlKey = returnObj.getString(Const.RESPONSE.KEY);
        request.getRequestDispatcher(unit.getForwardUri(urlKey)).forward(request, response);
      } else if (Const.RESPONSE.REDIRECT == requestType) {// 重定向
        String urlKey = returnObj.getString(Const.RESPONSE.KEY);
        response.sendRedirect(unit.getRedirectUri(urlKey));
      }
    } catch (Exception e) {
      throw e;
    } finally {
      returnObj = null;
    }
  }
}
