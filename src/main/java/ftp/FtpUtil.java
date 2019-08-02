package ftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FtpUtil {

  private FTPClient ftpClient;
  private static FtpUtil util;

  private FtpUtil() {
  }

  public static FtpUtil getInstance() {
    if (util == null) {
      return new FtpUtil();
    }
    return util;
  }

  public FTPClient getFtp() {
    return ftpClient;
  }

  public void setFtp(FTPClient ftp) {
    this.ftpClient = ftp;
  }

  public void makeDir(String fileDir) {
    try {
      ftpClient.makeDirectory(fileDir);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   * @param path
   *          上传到ftp服务器哪个路径下
   * @param addr
   *          地址
   * @param port
   *          端口号
   * @param username
   *          用户名
   * @param password
   *          密码
   * @return
   * @throws Exception
   */
  public boolean connect(String addr, int port, String username, String password) throws Exception {
    boolean result = false;
    ftpClient = new FTPClient();
    int reply;
    ftpClient.connect(addr, port);
    ftpClient.login(username, password);
    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
    reply = ftpClient.getReplyCode();
    if (!FTPReply.isPositiveCompletion(reply)) {
      ftpClient.disconnect();
      return result;
    }
    result = true;
    return result;
  }

  /**
   * @author
   * @class FtpUtil
   * @title upload
   * @Description :
   * @time 2013 2013-11-27
   * @return void
   * @exception :(Error note)
   * @param file
   *          上传的文件或文件夹
   * @param path
   *          ftp服务器 保存文件的路径
   * @throws Exception
   */
  @SuppressWarnings("unused")
  private void upload(File file, String bakPath) throws Exception {
    System.out.println("开始上传的是否是文件 :" + file.isDirectory());
    ftpClient.setBufferSize(1024);
    ftpClient.setControlEncoding("GBK");
    try {
      if (file.isDirectory()) { // 把目录的文件全传上去
        ftpClient.makeDirectory(file.getName());
        ftpClient.changeWorkingDirectory(file.getName());
        ftpClient.enterLocalPassiveMode();
        String[] files = file.list();
        for (int i = 0; i < files.length; i++) {
          File file1 = new File(file.getPath() + File.separator + files[i]);
          if (file1.isDirectory()) {
            upload(file1, bakPath);
            ftpClient.changeToParentDirectory();
          } else {
            File file2 = new File(file.getPath() + File.separator + files[i]);
            FileInputStream input = new FileInputStream(file2);
            ftpClient.storeFile(file2.getName(), input);
            input.close();
          }
        }
      } else {
        File file2 = new File(file.getPath());
        System.out.println("上传的本地文件路径是" + file2.getAbsolutePath());
        InputStream input = new FileInputStream(file2);
        ftpClient.changeWorkingDirectory(bakPath);
        ftpClient.enterLocalPassiveMode();
        ftpClient.storeFile(file2.getName(), input);
        input.close(); // 关闭输入流
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @author
   * @class ItemFtp
   * @title download
   * @Description : FPT 下载文件方法
   * @time 2013 2013-11-27
   * @return void
   * @exception :(Error note)
   * @param remotePath
   *          下载的文件的路径
   * @param fileName
   *          下载的文件名
   * @param localPath
   *          下载的文件本地路径
   * @throws Exception
   */
  @SuppressWarnings("unused")
  private void download(String remotePath, String fileName, String localPath) throws Exception {

    try {
      ftpClient.changeWorkingDirectory(remotePath);

      // 列出该目录下所有文件
      FTPFile[] fs = ftpClient.listFiles();
      // 遍历所有文件，找到指定的文件
      for (FTPFile ff : fs) {
        if (ff.getName().equals(fileName)) {
          // 根据绝对路径初始化文件
          File localFile = new File(localPath + File.separator + ff.getName());
          // 输出流
          OutputStream is = new FileOutputStream(localFile);
          // 下载文件
          ftpClient.retrieveFile(ff.getName(), is);
          System.out.println("下载成功!");
          is.close();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @author
   * @class ItemFtp
   * @title download
   * @Description : FPT 下载文件方法
   * @time 2013 2013-11-27
   * @return void
   * @exception :(Error note)
   * @param remotePath
   *          下载的文件的路径
   * @param fileName
   *          下载的文件名
   * @param localPath
   *          下载的文件本地路径
   * @throws Exception
   */
  @SuppressWarnings("unused")
  private void download(String remotePath, String[] fileNames, String localPath) throws Exception {

    try {
      ftpClient.changeWorkingDirectory(remotePath);

      // 列出该目录下所有文件
      FTPFile[] fs = ftpClient.listFiles();
      // 遍历所有文件，找到指定的文件
      for (FTPFile ff : fs) {
        for (String fileName : fileNames) {
          if (ff.getName().equals(fileName)) {
            // 根据绝对路径初始化文件
            File localFile = new File(localPath + File.separator + ff.getName());
            // 输出流
            OutputStream is = new FileOutputStream(localFile);
            // 下载文件
            ftpClient.retrieveFile(ff.getName(), is);
            System.out.println("文件" + fileName + "下载成功!");
            is.close();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 关闭ftp连接
   */
  public void closeFtp() {
    if (ftpClient != null && ftpClient.isConnected()) {
      try {
        ftpClient.logout();
        ftpClient.disconnect();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * <p>
   * 删除FTP上的文件
   * </p>
   * 
   * @param pathname
   * @return true||false
   */
  public boolean removeFile(String pathname) {
    boolean flag = false;
    if (ftpClient != null) {
      try {
        flag = ftpClient.deleteFile(pathname);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        closeFtp();
      }
    }
    return flag;
  }

  /**
   * 复制文件.把sourceDir目录下的文件sourceFileName拷贝到targetDir目录下
   * @param sourceFileName
   * @param targetFile
   * @throws IOException
   */
  public void copyFile(String sourceFileName, String sourceDir, String targetDir) throws IOException {
    ByteArrayInputStream in = null;
    ByteArrayOutputStream fos = new ByteArrayOutputStream();
    try {
      File dir = new File(targetDir);
      if (!dir.exists()) {
        ftpClient.makeDirectory(targetDir);
      }
      ftpClient.setBufferSize(1024 * 2);
      // 变更工作路径
      ftpClient.changeWorkingDirectory(sourceDir);
      // 设置以二进制流的方式传输
      ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
      // 将文件读到内存中
      ftpClient.retrieveFile(new String(sourceFileName.getBytes("GBK"), "iso-8859-1"), fos);
      in = new ByteArrayInputStream(fos.toByteArray());
      if (in != null) {
        ftpClient.changeWorkingDirectory(targetDir);
        ftpClient.storeFile(new String(sourceFileName.getBytes("GBK"), "iso-8859-1"), in);
      }
    } finally {
      // 关闭流
      if (in != null) {
        in.close();
      }
      if (fos != null) {
        fos.close();
      }
    }
  }

  /**
   * 复制文件夹.把sourceDir目录下的文件拷贝到targetDir目录下
   * @param sourceDir
   * @param targetDir
   * @throws IOException
   */
  public void copyDirectiory(String sourceDir, String targetDir) throws IOException {
    // 新建目标目录
    File dir = new File(targetDir);
    if (!dir.exists()) {
      ftpClient.makeDirectory(targetDir);
    }
    // 获取源文件夹当前下的文件或目录
    FTPFile[] ftpFiles = ftpClient.listFiles(sourceDir);
    for (int i = 0; i < ftpFiles.length; i++) {
      if (ftpFiles[i].isFile()) {
        copyFile(ftpFiles[i].getName(), sourceDir, targetDir);
      } else if (ftpFiles[i].isDirectory()) {
        copyDirectiory(sourceDir + File.separator + ftpFiles[i].getName(),
            targetDir + File.separator + ftpFiles[i].getName());
      }
    }
  }

  public static void main(String[] args) throws Exception {
    FtpUtil utils = FtpUtil.getInstance();
    try {
      //boolean isConnected = utils.connect("117.27.234.110", 21, "telecom", "telecom@yonyou2016_*12"); // 上传的账号
      boolean isConnected = utils.connect("42.123.77.187", 58022, "wpzs2_dev", "D8|mq[6SuL"); // 上传的账号
      if (isConnected) {
        // File file = new File("E:\\ftpfile\\222.pdf");
        // utils.upload(file, "/home/telecom/DataBak2");
        // utils.makeDir("DataBak3");

        // utils.copyFile("10800_HOTSTOREASS_T_TELECOMENT_20170117_20170113_D_00_0001.DAT.gz",
        // "/home/telecom/DataBak2", "/home/telecom/DataBak6");
        utils.copyDirectiory("/home/telecom/Data", "/home/telecom/DataBak8");
        
      } else {
        System.out.println("连接不上ftp服务器");
      }

      utils.closeFtp();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("连接不上ftp服务器");
    }
  }

}
