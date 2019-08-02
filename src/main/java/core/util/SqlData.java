package core.util;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SqlData implements Serializable{
  private String sql;

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }
}
