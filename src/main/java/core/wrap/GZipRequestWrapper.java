package core.wrap;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
/**
 * Gzip解压request
 * @author Administrator
 *
 */
public class GZipRequestWrapper extends HttpServletRequestWrapper {

	private BufferedServletInputStreamWrapper _stream;
    
	public GZipRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		_stream = new BufferedServletInputStreamWrapper(
				new GZIPInputStream(request.getInputStream()), request.getContentLength());  
	}

	@Override
	public BufferedServletInputStreamWrapper getInputStream() {
		return _stream;
	}

	@Override
	public int getContentLength() {
		return _stream.getBytes().length;
	}
}
