package core;

import java.io.Serializable;

public class MethodProtocol implements Serializable {
  private static final long serialVersionUID = -4461784343596662765L;
  private String methodUri;
  private String methodType;
    
  public String getMethodUri() {
    return methodUri;
  }
  public void setMethodUri(String methodUri) {
    this.methodUri = methodUri;
  }
  public String getMethodType() {
    return methodType;
  }
  public void setMethodType(String methodType) {
    this.methodType = methodType;
  }
  public MethodProtocol(String methodUri, String methodType) {
    this.methodUri = methodUri;
    this.methodType = methodType;
  }
  
}
