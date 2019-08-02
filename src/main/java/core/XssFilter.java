package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import core.util.CommUtils;
import core.wrap.XssHttpServletRequestWrapper;
/**
 * 防御Xss攻击的代码
 * @author 
 *
 */
public class XssFilter implements Filter {
  @Override
  public void init(FilterConfig config) throws ServletException {
  }
  
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    if ( isBug(req) ) {// 如果BUG入侵
      response.reset();
      return;
    }
    XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
    chain.doFilter(xssRequest, response);
  }

  @Override
  public void destroy() {
  }
  
  private List<String> keywords = new ArrayList<String>();  
  public void initKeyWord() {
     keywords.add("getWriter");  
     keywords.add("FileOutputStream");  
     keywords.add("getRuntime");  
     keywords.add("getRequest");  
     keywords.add("getProperty");  
     keywords.add("\\u0023");  
     keywords.add("\\43");
  }
  /**
   * bug漏洞
   * @return
   */
  private boolean isBug(HttpServletRequest request) {
    String queryString = request.getQueryString(); 
    if(queryString != null) {  
       for(String keyword : keywords) {  
            if(queryString.indexOf(keyword) != -1) {  
                String ip = CommUtils.getIpAddr(request);
                System.out.println("get the invade address:" + ip);
                return true;  
            }  
        }  
    }  
    java.util.Enumeration<String> e = request.getParameterNames();  
    while (e.hasMoreElements()) {
      String parName = e.nextElement();
      for (String keyword : keywords) {
        if (parName.indexOf(keyword) != -1) {
          String ip = CommUtils.getIpAddr(request);
          System.out.println("get the invade address:" + ip);
          return true;
        }
      }
    }
    return false;
  }
}
