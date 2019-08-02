package core.util;

 
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
 
import org.apache.http.conn.ssl.SSLSocketFactory;
 
/**
 * 需要读取cer证书,然后进行验证
 * @author qwx
 *
 */
public class TrustCertainHostNameFactory extends SSLSocketFactory {
 
  private static TrustCertainHostNameFactory mInstance;
 
  public TrustCertainHostNameFactory(KeyStore truststore) throws NoSuchAlgorithmException,
      KeyManagementException, KeyStoreException, UnrecoverableKeyException {
    super(truststore);
  }
 
  public static TrustCertainHostNameFactory getDefault(String crtFilePath ) {
    KeyStore keystore = null;
    try {
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      InputStream in = new FileInputStream(crtFilePath);
      Certificate ca = cf.generateCertificate(in);
 
      keystore = KeyStore.getInstance(KeyStore.getDefaultType());
      keystore.load(null, null);
      keystore.setCertificateEntry("ca", ca);
 
      if (null == mInstance) {
        mInstance = new TrustCertainHostNameFactory(keystore);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return mInstance;
  }
 
  @Override
  public Socket createSocket() throws IOException {
    return super.createSocket();
  }
 
  @Override
  public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
      throws IOException, UnknownHostException {
    return super.createSocket(socket, host, port, autoClose);
  }
 
}
