package org.webcastellum;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;

/**
 * Holds information about an HTML multipart form response field.
 */
public final class MultipartFileInfo {

    private final boolean bufferFileUploadsToDisk;
    
    private String name, contentType, filename;
    private long length;

    private byte[] data = new byte[0]; // never null
    private File buffer;

    
    private MultipartFileInfo(final String name, final String contentType, final String filename, final boolean bufferFileUploadsToDisk) {
        super();
        this.name = name;
        this.contentType = contentType;
        this.filename = filename;
        this.bufferFileUploadsToDisk = bufferFileUploadsToDisk;
    }
    
    
    
    
    public MultipartFileInfo(final String name, final String contentType, final String filename, final InputStream in, final boolean bufferFileUploadsToDisk) throws IOException {
        this (name, contentType, filename, bufferFileUploadsToDisk);
        if (bufferFileUploadsToDisk) {
            this.buffer = TempFileUtils.writeToTempFile(in);
            this.length = this.buffer.length();
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int read = in.read();
            while (read >= 0) {
                out.write((byte) read);
                read = in.read();
            }
            this.data = out.toByteArray();
            this.length = this.data.length;
        }
    }
    
    public MultipartFileInfo(final String name, final String contentType, final String filename, final InputStream in, final long length, final boolean bufferFileUploadsToDisk) throws IOException {
        this (name, contentType, filename, bufferFileUploadsToDisk);
        this.length = length;
        if (bufferFileUploadsToDisk) {
            this.buffer = TempFileUtils.writeToTempFile(in, length, 0);
        } else {
            this.data = new byte[(int)length];
            int index = 0;
            int read = in.read();
            while (read >= 0 && index < length) {
                data[index] = (byte) read;
                read = in.read();
                ++index;
            }
        }
    }
    
    

    
    
    public String getName() {
        return name;
    }
    public String getContentType() {
        return contentType;
    }
    public String getFilename() {
        return filename;
    }
    public long getLength() {
        return this.length;
    }
    public InputStream getFile() throws FileNotFoundException {
        if (this.bufferFileUploadsToDisk) {
            return new BufferedInputStream(new FileInputStream(this.buffer));
        } else {
            return new ByteArrayInputStream(data);
        }
    }
    
    //1.5@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append("Name=" + name);
        sb.append(" ");
        sb.append("Content-Type=" + contentType);
        sb.append(" ");
        sb.append("Filename=" + filename);
        sb.append(" ");
        sb.append("Length=" + getLength());
        sb.append(")");
        return sb.toString();
    }
    
    
}
