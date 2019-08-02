package core.util;

import java.io.OutputStream;
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
 * 导出为Excel(适合1-2万条数据的导出)
 * @author qwx
 *
 */
@SuppressWarnings("deprecation")
public class ExportExcel {
  
  /**
   * 
   * @param wb         HSSFWorkbook
   * @param header     表头
   * @param cellTitle  列头
   * @param values     要保存的值
   * @param fileName   要保存的文件名称
   * @param sheetName  标签名称
   */
  public static OutputStream export2Excel(OutputStream ops, HSSFWorkbook wb, String header, String[] cellTitle, 
                                                             List<Map<String, String>> values, String sheetName){
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
    if(cellCount > 0){
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
    }else{
      if(values != null && values.size() > 0){
        for(int k=0; k<values.size(); k++){
          int s = 0;
          if(CommUtils.isNotNull(header)){
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
      //FileOutputStream fout  = new FileOutputStream(fileName);
      wb.write(ops);
      //ops.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ops;
  }
}
