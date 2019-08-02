package core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.annotation.Method;
public class ProtocolFilter implements Filter {
  
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest)request;
    HttpServletResponse resp = (HttpServletResponse)response;
    String uri = req.getRequestURI();
    String moudle = req.getContextPath();
    String methodType = req.getMethod();//请求方法
    if ( !moudle.equals("/") && !moudle.equals("") ) {
      uri = uri.substring(moudle.length());
    }
    Unit unit = ReflectionCache.getClazz(uri);
    if ( unit != null && !Method.ALL.equals(unit.getRequestType()) ) {
      if (! methodType.equalsIgnoreCase(unit.getRequestType()) ) {
        out(resp, "< "+ uri + " > 此方法只支持"+ unit.getRequestType() + "请求");
        return;
      }
    }
    chain.doFilter(req, resp);
    return;
  }

  @Override
  public void init(FilterConfig config) throws ServletException {
  }
    
  private void out( HttpServletResponse response, String messages ) {
    PrintWriter out = null;
    try {
      response.setContentType("text/html;charset=UTF-8");
      response.setHeader("Cache-Control", "no-cache");
      response.setCharacterEncoding("UTF-8");
      out = response.getWriter();
      out.print(String.valueOf(messages));
    } catch (Exception e) {
      System.out.println("ajaxMethod出错了=>"+e.getMessage());
    } finally {
      if (out != null) {
        out.flush();
        out.close();
      }
    }
  }

  @Override
  public void destroy() {
       
  }
}
