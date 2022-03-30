package org.webcastellum;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import javax.activation.DataSource;
import javax.servlet.ServletRequest;

/**
 * This library provides a DataSource interface to an ServletRequest.
 */
public class ServletRequestDataSourceAdapter implements DataSource {
    
    private static final int MAX_INITIAL_SIZE = 5*1024*1024; // 5 MB
    
    
    
    
    private final String name, contentType;
    private final boolean bufferFileUploadsToDisk;
    
    private byte[] data;
    private File buffer;
    
    
    
    public ServletRequestDataSourceAdapter(final ServletRequest req, final String name, final int maxInputStreamLength, final boolean bufferFileUploadsToDisk) throws IOException {
        super();
        
        this.bufferFileUploadsToDisk = bufferFileUploadsToDisk;
        
        if (this.bufferFileUploadsToDisk) {
            this.buffer = TempFileUtils.writeToTempFile(new BufferedInputStream(req.getInputStream()), 0, maxInputStreamLength);
        } else {
            final int length = req.getContentLength();
            // avoid that spoofed content-length headers allocate too much buffer memory initially
            final int initialSize = Math.min((length < 0) ? 1024 : length, MAX_INITIAL_SIZE);
            InputStream in = null;
            OutputStream out = null;
            ByteArrayOutputStream sink = null;
            try {
                in = new BufferedInputStream(req.getInputStream());
                sink = new ByteArrayOutputStream(initialSize);
                out = new BufferedOutputStream(sink);
                TempFileUtils.pipeStreams(in, out, sink, 0, maxInputStreamLength);
                out.flush();
                this.data = sink.toByteArray();
            } finally {
                if (out != null) try { out.close(); } catch (IOException ignored) {}
                out = null;
                in = null;
                // servlet input stream must not be closed, since that's the web container's responsibility
            }
        }
        
        this.contentType = RequestUtils.getContentType(req);
        this.name = name;
    }
    
    
    public ServletRequestDataSourceAdapter(ServletRequest req, final int maxInputStreamLength, final boolean bufferFileUploadsToDisk) throws IOException {
        this(req, req.getRemoteAddr(), maxInputStreamLength, bufferFileUploadsToDisk);
    }
    
    
    public String getContentType() {
        return contentType;
    }
    public String getName() {
        return name;
    }
    
    public InputStream getInputStream() throws IOException {
        if (this.bufferFileUploadsToDisk) {
            return new BufferedInputStream(new FileInputStream(this.buffer));
        } else {
            return new ByteArrayInputStream(data);
        }
    }
    
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("getOutputStream() not required for ServletRequest to DataSource adapter");
    }
    
    protected void clear() {
        if (this.bufferFileUploadsToDisk) {
            TempFileUtils.deleteTempFile(this.buffer);
        } else {
            this.data = null;
        }
    }
    
    
    
}
