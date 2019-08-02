package socket;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Message implements Serializable {
  private int id; //消息id
  private String to; //发送给谁
  private String from; //从哪里接收的
  private String msg;  //消息
  private String key;  //对应handler的key，在socket.xml里配置的key
  
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public String getTo() {
    return to;
  }
  public void setTo(String to) {
    this.to = to;
  }
  public String getFrom() {
    return from;
  }
  public void setFrom(String from) {
    this.from = from;
  }
  public String getMsg() {
    return msg;
  }
  public void setMsg(String msg) {
    this.msg = msg;
  }
}
