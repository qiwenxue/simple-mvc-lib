package core;

import java.io.Serializable;

public final class Result<T> implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 6791920763167967507L;
  private T result;
  private int responseType;
  public Result(){}
   
  public T getResult() {
    return result;
  }
  
  public int getResponseType() {
    return responseType;
  }
  
  public void setResult(T result, int responseType) {
    this.result = result;
    this.responseType = responseType;
  }
     
}
