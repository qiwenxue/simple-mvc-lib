package core.wrap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

public class BufferedServletInputStreamWrapper extends ServletInputStream {

	private static final int DEFAULT_READ_BUFFER_SIZE = 1024;
	private final byte[] EMPTY_ARRAY = new byte[0];
	private ByteArrayInputStream _is;
	private byte[] _bytes;

	/**
	 * 09. * takes in the actual input stream that we should be buffering 10.
	 */
	public BufferedServletInputStreamWrapper(InputStream stream,
			int length) throws IOException {
		_bytes = (length == 0) ? EMPTY_ARRAY : toBytes(stream, length);
		_is = new ByteArrayInputStream(_bytes);
	}

	@Override
	public int read() throws IOException {
		return _is.read();
	}

	@Override
	public int read(byte[] buf, int off, int len) {
		return _is.read(buf, off, len);
	}

	@Override
	public int read(byte[] buf) throws IOException {
		return _is.read(buf);
	}

	@Override
	public int available() {
		return _is.available();
	}

	/**
	 * 39. * resets the wrapper's stream so that it can be re-read from the
	 * stream. if we're 40. * using this somewhere were we expect it to be done
	 * again in the chain this should 41. * be called after we're through so we
	 * can reset the data. 42.
	 */

	public void resetWrapper() {
		_is = new ByteArrayInputStream(_bytes);
	}

	public byte[] getBytes() {
		return _bytes;
	}

	private byte[] toBytes(InputStream is, int bufferSize) throws IOException {
		bufferSize = (bufferSize <= 0) ? DEFAULT_READ_BUFFER_SIZE : bufferSize;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[bufferSize];
		int read = is.read(buffer);

		while (-1 != read) {
			bos.write(buffer, 0, read);
			read = is.read(buffer);
		}

		return bos.toByteArray();
	}

}
