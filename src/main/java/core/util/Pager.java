package core.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 分页类,
 * 首页从第1页开始
 * @author qwx
 *
 */
@SuppressWarnings("rawtypes")
public class Pager<T> {
  /** 总的数据个数 **/
  private int elementsCount;    
  /** 页面大小, 每页显示多少条数 **/
  private int pageSize;
  /** 总共有多少个页面 **/
  private int pagesCount;
  /** 当前页是第几页  **/
  private int currentPage =1;  
  /** 当前页前面有多少页 **/
  private int priCurrNo = 5;
  /** 存放查询出来的数据 **/
  private List<T> objs= new ArrayList<T>();
  /** 查询时输入的起始位置 ,在sql中使用**/
  private int limit;
  /** 是否是第一页 **/
  private boolean isFirst;
  /** 是不是最后一页 **/
  private boolean isLast;
  
  public int getLimit() {
    limit = (currentPage-1) * pageSize;
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getPriCurrNo() {
    return priCurrNo;
  }

  public void setPriCurrNo(int priCurrNo) {
    this.priCurrNo = priCurrNo;
  }
  
  public int getElementsCount() {
    return elementsCount;
  }
  
  public void setElementsCount(int elementsCount) {
    this.elementsCount = elementsCount;
  }
  
  public int getPageSize() {
    if ( pageSize == 0 ) {
      pageSize = 5; //默认5个 
    }
    return pageSize;
  }
  
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public void setPagesCount(int pagesCount) {
    this.pagesCount = pagesCount;
  }
  
  public int getCurrentPage() {
    return currentPage;
  }
  
  public List<T> getObjs() {
    return objs;
  }
  
  public void setObjs(List<T> objs) {
    this.objs = objs;
  }
  
  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  } 
  
  /** 每页显示多少条数据  **/
  public int getPagesCount() {
    pagesCount = (getElementsCount() % getPageSize() == 0) 
        ? (getElementsCount() / getPageSize())
        : (getElementsCount() / getPageSize() + 1);
    return pagesCount;
  }
  
  /**
   * 生成分页的号码
   * @return
   * @throws Exception 
   */
  public String toPagerJs() throws Exception{
    String pageNos = "";
    //当前页之前的页码
    int currNo = this.getCurrentPage();
    int priNo = this.getPriCurrNo();
    int total = this.getPagesCount();
    if(currNo > total){
      currNo = total;
    }
    // currNo前面的部分
    if(currNo - priNo>0){
      for(int i=currNo - priNo; i<currNo; i++){
        pageNos += i + ",";
      }
    }else{
      for(int j=1; j<currNo; j++){
        pageNos += j + ",";
      }
    }
    pageNos += "" + currNo + ",";   //当前页
    
    if(currNo + priNo < total){
      for(int k = currNo+1; k <= currNo+priNo; k++){
        pageNos += k + ",";
      }
    }else{
      for(int m=currNo+1; m <= total; m++){
        pageNos += m + ",";
      }
    }
    if( !StringUtils.isEmpty(pageNos) ){
      int k = pageNos.length();
      int index = pageNos.lastIndexOf(",");
      if(k == index+1){
        pageNos = pageNos.substring(0, index);
      }
    }else{
      pageNos = "";
    }
    JSONObject obj = new JSONObject();
    obj.put(Const.RESPONSE.STATE, 0);
    obj.put("currNo", getCurrentPage());
    obj.put("elmcnt", getElementsCount());
    obj.put("totalPage", getPagesCount());
    obj.put("nos", pageNos);
    obj.put("pageSize", pageSize);
    obj.put("isFirst", isFirst());
    obj.put("isLast",  isLast());
    obj.put("data", toPageAndData());
    return obj.toString();
  }
  /**
   * js拼成页码和数据
   * @return 返回stirng
   * @throws Exception
   */
  private String toPageAndData() throws Exception{
    JSONArray array = new JSONArray();
    if(objs == null || objs.size() ==0){
      return array.toString();
    }
    for ( int i=0; i<objs.size(); i++ ) {
      JSONObject obj = JSONObject.fromObject(objs.get(i));
      array.add(obj);
    }
    return array.toString();
  }

  public boolean isFirst() {
    isFirst = currentPage <= 1;
    return isFirst;
  }

  public void setFirst(boolean isFirst) {
    this.isFirst = isFirst;
  }
  
  public boolean isLast() {
    isLast = currentPage >= this.getPagesCount();
    return isLast;
  }

  public void setLast(boolean isLast) {
    this.isLast = isLast;
  }
    
}
