package core.wrap;

import java.io.IOException;
import java.util.zip.InflaterInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class InflateRequestWrapper extends HttpServletRequestWrapper {
	private BufferedServletInputStreamWrapper _stream;

	public InflateRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		_stream = new BufferedServletInputStreamWrapper(
				new InflaterInputStream(request.getInputStream()), request.getContentLength());  
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
