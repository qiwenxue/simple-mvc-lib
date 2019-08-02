package socket;

import core.util.CommUtils;

public class Config {
  public static String socketServerIp = CommUtils.getPropValByKey("socket.local.ip", "conf");
  public static String socketServerPort = CommUtils.getPropValByKey("socket.port", "conf");
}
