package core.util;
import java.io.File;
import java.io.InputStream;

public class FormFile {
  /* 上传文件的数据 */
  private File file;
  /* 文件大小 */
  private long fileSize;
  /* 文件名称 */
  private String fileName;
  /* 请求参数名称 */
  private String parameterName;
  /* 文件路径 */
  private String filePath;
  
  private InputStream inputStream;
  
  public FormFile(){}
  
  public FormFile( String fileName, String fileParamName, String filePath ) {
    this.fileName = fileName;
    this.parameterName = fileParamName;
    this.filePath = filePath;
    this.file = new File( this.filePath );
    this.fileSize = this.file.length();
  }
  
  public FormFile( String fileName, String fileParamName, InputStream inputStream ){
    this.fileName = fileName;
    this.parameterName = fileParamName;
    this.inputStream = inputStream;
  }
     
  public long getFileSize() {
    return fileSize;
  }

  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }

  public File getFile() {
    return file;
  }
  
  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getParameterName() {
    return parameterName;
  }

  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }
  
  public void setFile(File file) {
    this.file = file;
  }

  public void setFileSize(int fileSize) {
    this.fileSize = fileSize;
  }

  public InputStream getInputStream() {
    return inputStream;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }
  
}