package core.util;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;

/**
 * dao chu wei excel
 * @author qiwx
 *
 */
@SuppressWarnings("deprecation")
public class ExportExcelStream {
  
  /**
   * 导出数据(多次导出，为了性能优化)
   */
  public static void export2Excel( HSSFWorkbook wb, HSSFSheet sheet, 
                                   String header, String[] cellTitle, 
                                   List<Map<String, String>> values, String fileName ) {
    HSSFCellStyle style = wb.createCellStyle();
    style.setAlignment(HSSFCellStyle.ALIGN_CENTER); //居中
    //设置字体样式
    HSSFFont  font = wb.createFont(); 
    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    font.setFontHeightInPoints((short)12);
    style.setFont(font); 
    //第四步, 创建单元格,并设置表头, 设置表头剧中
    int cellCount = 0;
    HSSFRow row = null;
    int rowCnt = sheet.getLastRowNum();
    if ( rowCnt == 0 ) {
      if(cellTitle != null && cellTitle.length > 0 ){
        cellCount  = cellTitle.length;
        if(CommUtils.isNotNull(header)){
          HSSFRow headerow = sheet.createRow(0);
          HSSFCell cellsdf = headerow.createCell(0);
          cellsdf.setCellValue(header);
          cellsdf.setCellStyle(style);
          sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, cellCount-1)); //0行0列,到0行3列
          row = sheet.createRow( rowCnt +1 );
        }else{
          row = sheet.createRow( rowCnt );
        }
        for(int i=0; i<cellTitle.length; i++){  // 添加每一个列的表头
          HSSFCell cell = row.createCell(i);  //第0个单元格
          cell.setCellValue(cellTitle[i]);         
          cell.setCellStyle(style);
        }
      }else{
        int count = 1;
        if(values != null){
          count = values.get(0).size();
        }
        if(CommUtils.isNotNull(header)){
          HSSFRow headerow = sheet.createRow(0);
          HSSFCell cellsdf = headerow.createCell(0);
          cellsdf.setCellValue(header);
          cellsdf.setCellStyle(style);
          sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, count-1)); //0行0列,到0行3列
          row = sheet.createRow(rowCnt +1);
        }else{
          row = sheet.createRow(rowCnt);
        }
      }
    }
    //第5步, 写入实体类  //插入列表内容 
    if(cellCount > 0){//存在表头
      if(values != null && values.size() > 0){
        for(int k=rowCnt; k<values.size() + rowCnt; k++){
          if(CommUtils.isNotNull(header)){
            row = sheet.createRow(k+2);
          }else{
            row = sheet.createRow(k+1);
          }
          Map<String, String> hashMap = values.get(k - rowCnt);
          Set<String> keySet = hashMap.keySet();
          for(String key : keySet){
            for(int m=0; m<cellTitle.length; m++){
              if(key.equalsIgnoreCase(cellTitle[m])){
                row.createCell(m).setCellValue(hashMap.get(key));  //把这个值存放到对应的列中去
              }
            }
          }
        }
      }
    }else{
      if(values != null && values.size() > 0){
        for(int k=rowCnt; k<values.size()+rowCnt; k++){
          int s = 0;
          if ( rowCnt > 0) {
            row = sheet.createRow(k + 1);
          } else if(CommUtils.isNotNull(header)){//表题不为空，但是表头为空
            row = sheet.createRow(k+1);
          }else{
            row = sheet.createRow(k);
          }
          Map<String, String> hashMap = values.get(k - rowCnt);
          Set<String> keySet = hashMap.keySet();
          for(String key : keySet){
            row.createCell(s++).setCellValue(hashMap.get(key));  //把这个值存放到对应的列中去
          }
        }
      }
    }
    // 第6步，将文件存到指定的位置 
    try {
      FileOutputStream fout  = new FileOutputStream(fileName);
      wb.write(fout);
      fout.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * 
   * @param wb         HSSFWorkbook
   * @param header     表头
   * @param cellTitle  列头
   * @param values     要保存的值
   * @param fileName   要保存的文件名称
   * @param sheetName  标签名称
   */
  public static void export2Excel(HSSFWorkbook wb, String header, String[] cellTitle, List<Map<String, String>> values, String fileName, String sheetName){
    //第一步, 创建一个webbook,对应一个Excel文件
    //第2步, 在webbook中添加一个sheet,对应Excel文件中的sheet
    HSSFSheet sheet = wb.createSheet(sheetName);     //生成sheet
    //第三步, 在sheet中添加表头第0行，对应老版本poi对Excel的行数列数的限制
    HSSFCellStyle style = wb.createCellStyle();
    style.setAlignment(HSSFCellStyle.ALIGN_CENTER); //居中
    //设置字体样式
    HSSFFont  font = wb.createFont(); 
    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    font.setFontHeightInPoints((short)12);
    style.setFont(font); 
    //第四步, 创建单元格,并设置表头, 设置表头剧中
    int cellCount = 0;
    HSSFRow row = null;
    if(cellTitle != null && cellTitle.length > 0){
      cellCount  = cellTitle.length;
      if(CommUtils.isNotNull(header)){
        HSSFRow headerow = sheet.createRow(0);
        HSSFCell cellsdf = headerow.createCell(0);
        cellsdf.setCellValue(header);
        cellsdf.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, cellCount-1)); //0行0列,到0行3列
        row = sheet.createRow(1);
      }else{
        row = sheet.createRow(0);
      }
      for(int i=0; i<cellTitle.length; i++){  // 添加每一个列的表头
        HSSFCell cell = row.createCell(i);  //第0个单元格
        cell.setCellValue(cellTitle[i]);         
        cell.setCellStyle(style);
      }
    }else{
      int count = 1;
      if(values != null){
        count = values.get(0).size();
      }
      if(CommUtils.isNotNull(header)){
        HSSFRow headerow = sheet.createRow(0);
        HSSFCell cellsdf = headerow.createCell(0);
        cellsdf.setCellValue(header);
        cellsdf.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, count-1)); //0行0列,到0行3列
        row = sheet.createRow(1);
      }else{
        row = sheet.createRow(0);
      }
    }
    //第5步, 写入实体类  //插入列表内容 
    if( cellCount > 0){ //表头不为空
      if(values != null && values.size() > 0){
        for(int k=0; k<values.size(); k++){
          if(CommUtils.isNotNull(header)){
            row = sheet.createRow(k+2);
          }else{
            row = sheet.createRow(k+1);
          }
          Map<String, String> hashMap = values.get(k);
          Set<String> keySet = hashMap.keySet();
          for(String key : keySet){
            for(int m=0; m<cellTitle.length; m++){
              if(key.equalsIgnoreCase(cellTitle[m])){
                row.createCell(m).setCellValue(hashMap.get(key));  //把这个值存放到对应的列中去
              }
            }
          }
        }
      }
    } else {
      if(values != null && values.size() > 0){
        for(int k=0; k<values.size(); k++){
          int s = 0;
          if(CommUtils.isNotNull(header)){ //表题不为空，但是表头为空
            row = sheet.createRow(k+1);
          }else{
            row = sheet.createRow(k);
          }
          Map<String, String> hashMap = values.get(k);
          Set<String> keySet = hashMap.keySet();
          for(String key : keySet){
            row.createCell(s++).setCellValue(hashMap.get(key));  //把这个值存放到对应的列中去
          }
        }
      }
    }
    // 第6步，将文件存到指定的位置 
    try {
      FileOutputStream fout  = new FileOutputStream(fileName, true);
      wb.write(fout);
      fout.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args){
   HSSFWorkbook wb = new HSSFWorkbook();
    String head1 = "测试1";
    String[] titles = new String[]{"姓名","年龄","住址","性别"};
    List list = new ArrayList();
    Map map1 = new LinkedHashMap<String, String>();
    map1.put("姓名", "大牛");
    map1.put("年龄", "100");
    map1.put("住址", "北京");
    map1.put("性别", "男");
    list.add(map1);
    Map map11 = new LinkedHashMap<String, String>();
    map11.put("姓名", "小兵");
    map11.put("年龄", "233");
    map11.put("住址", "北京");
    map11.put("性别", "男");
    list.add(map11);
    String fileName = "E:\\测试.xls";
    String sheetName = "测试1";
    HSSFSheet sheet = wb.createSheet(sheetName);     //生成sheet
    ExportExcelStream.export2Excel(wb, sheet,head1, titles, list, fileName);
    //ExportExcelStream.export2Excel(wb, sheet,head1, null, list, fileName);
    
    /*
    List list2 = new ArrayList();
    Map map2 = new LinkedHashMap<String, String>();
    map2.put("姓名", "大牛01");
    map2.put("年龄", "100");
    map2.put("住址", "北京");
    map2.put("性别", "男");
    list2.add(map2);
    Map map22 = new LinkedHashMap<String, String>();
    map22.put("姓名", "大牛022");
    map22.put("年龄", "100");
    map22.put("住址", "北京");
    map22.put("性别", "男");
    list2.add(map22);
    String sheetName2 = "测试2";
    //ExportExcelStream.export2Excel(wb, head2, titles2, list2, fileName, sheetName2);
    ExportExcelStream.export2Excel( wb,sheet,null, null, list2, fileName);
    
    List list3 = new ArrayList();
    Map map3 = new LinkedHashMap<String, String>();
    map3.put("姓名", "大牛0200");
    map3.put("年龄", "100");
    map3.put("住址", "北京");
    map3.put("性别", "男");
    list3.add(map3);
    Map map4 = new LinkedHashMap<String, String>();
    map4.put("姓名", "大牛0201");
    map4.put("年龄", "100");
    map4.put("住址", "北京");
    map4.put("性别", "男");
    list3.add(map4);
    Map map5 = new LinkedHashMap<String, String>();
    map5.put("姓名", "大牛0203");
    map5.put("年龄", "100");
    map5.put("住址", "北京");
    map5.put("性别", "男");
    list3.add(map5);
    String sheetName3 = "测试4";
    //ExportExcelStream.export2Excel(wb, head3, titles3, list3, fileName, sheetName3);
    ExportExcelStream.export2Excel( wb,sheet,null, null, list3, fileName);*/
  }
}

