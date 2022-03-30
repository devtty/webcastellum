package org.webcastellum;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletInputStream;

public final class ServletInputStreamAdapter extends ServletInputStream {

    private final InputStream delegate;

    public ServletInputStreamAdapter(byte[] in) {
        this( new ByteArrayInputStream(in) );
    }
    public ServletInputStreamAdapter(InputStream delegate) {
        this.delegate = delegate;
    }

    public int available() throws IOException {
        return delegate.available();
    }

    public int read() throws IOException {
        return delegate.read();
    }

    public int read(byte[] buf) throws IOException {
        return delegate.read(buf);
    }

    public int read(byte[] buf, int offset, int length) throws IOException {
        return delegate.read(buf, offset, length);
    }

    public void mark(int readLimit) {
        delegate.mark(readLimit);
    }

    public void reset() throws IOException {
        delegate.reset();
    }

    public boolean markSupported() {
        return delegate.markSupported();
    }
}
