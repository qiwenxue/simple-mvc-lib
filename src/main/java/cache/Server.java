package cache;

import java.io.Serializable;

import net.sf.json.JSONObject;
/**
 * 服务器
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class Server implements Serializable {
  private int id;
  private String ipAddress;//ip地址
  private boolean isHealth;//是否健康
  private int linkCount; //链接数
  private int port; //端口
  public int getPort() {
    return port;
  }
  public void setPort(int port) {
    this.port = port;
  }
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public String getIpAddress() {
    return ipAddress;
  }
  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }
  public boolean isHealth() {
    return isHealth;
  }
  public void setHealth(boolean isHealth) {
    this.isHealth = isHealth;
  }
  public int getLinkCount() {
    return linkCount;
  }
  public void setLinkCount(int linkCount) {
    this.linkCount = linkCount;
  }
  
  public String toJson() {
    JSONObject obj = new JSONObject();
    obj.put("id", id);
    obj.put("ipAddress", ipAddress);
    obj.put("isHealth", isHealth);
    obj.put("linkCount", linkCount);
    obj.put("port", port);
    return obj.toString();
  }
}
