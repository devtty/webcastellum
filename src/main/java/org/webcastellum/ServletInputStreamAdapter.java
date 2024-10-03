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

    @Override
    public int available() throws IOException {
        return delegate.available();
    }

    @Override
    public int read() throws IOException {
        return delegate.read();
    }

    @Override
    public int read(byte[] buf) throws IOException {
        return delegate.read(buf);
    }

    @Override
    public int read(byte[] buf, int offset, int length) throws IOException {
        return delegate.read(buf, offset, length);
    }

    @Override
    public synchronized void mark(int readLimit) {
        delegate.mark(readLimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        delegate.reset();
    }

    @Override
    public boolean markSupported() {
        return delegate.markSupported();
    }
}
