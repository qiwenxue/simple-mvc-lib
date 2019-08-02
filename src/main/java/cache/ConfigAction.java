package cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.json.JSONObject;
import core.BaseAction;
import core.annotation.Action;
import core.util.CommUtils;
import core.util.Const;

@Action(namespace="conf")
public class ConfigAction extends BaseAction {
   
  public String getServerConfig() throws Exception {
    String servers = (String)Memcached.getInstance().get("serverips");
    if ( CommUtils.isNull(servers) ) {
      setResult(Const.ERRCODE.IS_FAIL, "", "没有可用的服务器");
      return SUCCESS;
    }
    String[] ips = servers.split(",");
    String server = getMinLink( ips ) ;
    JSONObject resObj = new JSONObject();
    resObj.put("data", server);
    resObj.put(Const.RESPONSE.STATE, Const.ERRCODE.IS_SUCCESS);
    setData(resObj.toString());
    return SUCCESS;
  }
  
  /**
   * 获得压力最小的服务器地址
   * @param ips
   * @return
   */
  @SuppressWarnings("unchecked")
  public String getMinLink( String[] ips ) {
    List<Server> servers = new ArrayList<Server>();
    for ( int i=0; i<ips.length; i++) {
      if ( CommUtils.isNull(ips[i])) {
    	   continue;
      }
      String s = (String)Memcached.getInstance().getCached().get( ips[i] );
      JSONObject aServer = JSONObject.fromObject( s );
      Server as = (Server)JSONObject.toBean(aServer, Server.class);
      if ( as != null ) {
        servers.add( as );
      }
    }
    if ( !servers.isEmpty() ) {
      Collections.sort(servers, new ServerComparor());
      return servers.get(0).toJson();
    } else {
      return null;
    }
  }
  
  public static void main(String[] args) {
    ConfigAction s = new ConfigAction();
    String[] ips = new String[]{"172.0.0.1:12122","172.0.0.2:12122","172.0.0.3:12122"};
    String d = s.getMinLink(ips);
    System.out.println( d );
  }
}
