package org.webcastellum;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;


public final class ServletOutputStreamAdapter extends ServletOutputStream {
    
    private final OutputStream sink;
    
    
    public ServletOutputStreamAdapter(final OutputStream delegate) {
        if (delegate == null) throw new NullPointerException("delegate must not be null");
        this.sink = delegate;
    }

    
    
    public void write(final int aByte) throws IOException {
        this.sink.write(aByte);
    }
    
    
    //1.5@Override
    public void close() throws IOException {
        this.sink.close();
    }

    //1.5@Override
    public void flush() throws IOException {
        this.sink.flush();
    }
    
}
