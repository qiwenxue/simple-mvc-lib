package core;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.util.CommUtils;

public class FileUtil {
	private final static Logger logger = LoggerFactory.getLogger(FileUtil.class);
  
	/**
	 * 下载文件
	 * @param file
	 * @param response
	 * @param isDelete
	 */
	public static void downloadFile(File file, HttpServletResponse response, boolean isDelete) {
		try {
			// 以流的形式下载文件。
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
			response.reset();
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Length", "" + file.length());
			response.setHeader("Content-Disposition", "attachment;filename=" + CommUtils.encode(file.getName()));
			
	    byte[] buffer = new byte[1024];
      int len = 0;
      while((len = fis.read(buffer)) != -1) {
        toClient.write(buffer, 0, len);
      }
			//toClient.write(buffer);
			toClient.flush();
			toClient.close();
			fis.close();
			if (isDelete) {  //清空目录
				File fs = file.getParentFile();
				for (File f : fs.listFiles()) {
					f.delete(); // file.delete(); 是否将生成的服务器端文件删除
				}
			}
		} catch (IOException ex) {
			logger.error("下载文件时异常", ex);
		}
	}
  /**
   * 把文件打包成zip格式
   * @param inputFile
   * @param outputstream
   * @throws IOException
   * @throws ServletException
   */
	public static void zipFile(File inputFile, ZipOutputStream outputstream) throws IOException, ServletException {
		try {
			if (inputFile.exists()) {
				if (inputFile.isFile()) {
					FileInputStream inStream = new FileInputStream(inputFile);
					BufferedInputStream bInStream = new BufferedInputStream(inStream);
					ZipEntry entry = new ZipEntry(inputFile.getName());
					outputstream.putNextEntry(entry);

					final int MAX_BYTE = 10*1024*1024; // 最大的流为10M
					long streamTotal = 0; // 接受流的容量
					int streamNum = 0; // 流需要分开的数量
					int leaveByte = 0; // 文件剩下的字符数
					byte[] inOutbyte; // byte数组接受文件的数据

					streamTotal = bInStream.available(); // 通过available方法取得流的最大字符数
					streamNum = (int) Math.ceil(streamTotal / MAX_BYTE); // 取得流文件需要分开的数量
					leaveByte = (int) streamTotal % MAX_BYTE; // 分开文件之后,剩余的数量
					
					if (streamNum > 0) {
						for (int j = 0; j < streamNum; j++) {
							inOutbyte = new byte[MAX_BYTE];
							// 读入流,保存在byte数组
							bInStream.read(inOutbyte, 0, MAX_BYTE);
							outputstream.write(inOutbyte, 0, MAX_BYTE); // 写出流
						}
					}
					// 写出剩下的流数据
					inOutbyte = new byte[leaveByte];
					bInStream.read(inOutbyte, 0, leaveByte);
					outputstream.write(inOutbyte);
					
					bInStream.close(); // 关闭
					inStream.close();
					outputstream.closeEntry(); // Closes the current ZIP entry
				}
			} else {
				throw new ServletException("文件不存在！");
			}
		} catch (IOException e) {
			throw e;
		}
	}

	public static void zipFile(List<File> files, OutputStream outputStream) throws IOException, ServletException {
	  ZipOutputStream zipOut = null;
		try {
		  zipOut = new ZipOutputStream(outputStream);// 压缩流
			int size = files.size();
			// 压缩列表中的文件
			for (int i = 0; i < size; i++) {
				File file = (File) files.get(i);
				zipFile(file, zipOut);
			}
		} catch (IOException e) {
			throw e;
		} finally {
		  if ( zipOut != null ) {
		    zipOut.close();  //在此处管理流,以免压缩文件出问题
		  }
		}
	}
	
	public static HttpServletResponse download(String path,HttpServletRequest request, HttpServletResponse response) {
    InputStream fis= null;
      try {
          // path是指欲下载的文件的路径。
          File file = new File(path);
          // 取得文件名。
          String filename = file.getName();
          // 取得文件的后缀名。
          // 以流的形式下载文件。
          fis = new BufferedInputStream(new FileInputStream(file));
          byte[] buffer = new byte[fis.available()];
          fis.read(buffer);
          fis.close();
          // 清空response
          response.reset();
          // 设置response的Header
          response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("utf-8"),"ISO-8859-1"));
          response.addHeader("Content-Length", "" + file.length());
          response.setHeader("Cache-control","private");
          response.setHeader("Accept-Ranges","bytes");
          response.setContentType("application/octet-stream");
          OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
          toClient.write(buffer);
          toClient.flush();
          toClient.close();
          if ( file != null && file.exists() ) {
            file.delete();
          }
      } catch (IOException ex) {
          ex.printStackTrace();
      } finally {
        if (fis != null) {
          try {
            fis.close();  //关闭流
            fis = null;
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        
      }
      return response;
  }
	
	/**
	 * 从网络Url中下载文件
	 * @param urlStr
	 * @param fileName
	 * @param savePath
	 * @throws Exception 
	 */
	public static File  downLoadFromUrl(String urlStr,String fileName,String savePath) throws Exception{
	  logger.info("begin download the file "+urlStr+".........");
	    URL url = new URL(urlStr);
	    URLConnection conn = getURLConnection(url);//判断是http还是https
	    //设置超时间为3秒
	    conn.setConnectTimeout(3*1000);
	    //防止屏蔽程序抓取而返回403错误
	    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
	    //得到输入流
	    InputStream inputStream = conn.getInputStream();
	    //获取自己数组
	    byte[] getData = readInputStream(inputStream);
	    //文件保存位置
	    File saveDir = new File(savePath);
	    if(!saveDir.exists()){
	        saveDir.mkdir();
	    }
	    File file = new File(saveDir+File.separator+fileName);
	    FileOutputStream fos = new FileOutputStream(file);
	    fos.write(getData);
	    if(fos!=null){
	        fos.close();
	    }
	    if(inputStream!=null){
	        inputStream.close();
	    }
	    logger.info("dowload the file:"+fileName+" success");
	    return file;
	}
	
	/**
	 * 从输入流中获取字节数组
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static  byte[] readInputStream(InputStream inputStream) throws IOException {
	    byte[] buffer = new byte[1024];
	    int len = 0;
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    while((len = inputStream.read(buffer)) != -1) {
	        bos.write(buffer, 0, len);
	    }
	    bos.close();
	    return bos.toByteArray();
	}    
	
	public static URLConnection getURLConnection(URL url) throws Exception {
	  String protocol = url.getProtocol();
	  if ("http".equalsIgnoreCase(protocol)) {
	    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	    return conn;
	  } else if ("https".equalsIgnoreCase(protocol)) {
	    TrustManager[] tm = {new X509TrustManager (){ //信任所有证书

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }
	    }}; 
	    
	    SSLContext sslContext = SSLContext.getInstance("SSL"); 
	    sslContext.init(null, tm, null);
	    //从上述SSLContext对象中得到SSLSocketFactory对象 
	    SSLSocketFactory ssf = sslContext.getSocketFactory();
	    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
        @Override
        public boolean verify(String arg0, SSLSession arg1) {  //相信所有的地址证书
          return true;
        }
      });
	    HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
	    conn.setSSLSocketFactory( ssf );
      return conn;
	  }
	  return null;
	}
}
