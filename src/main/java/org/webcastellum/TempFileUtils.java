package org.webcastellum;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class TempFileUtils {
    
    
    
    
    /**
     * Copies the contents of the InputStream to the OutputStream. Reads from 
     * the InputStream until the end-of-stream marker is reached. Both streams 
     * are left open.
     * <p>
     * Performs a raw transfer from the InputStream to the OutputStream, so if 
     * buffering would improve performance, the streams should be buffered 
     * before being passed in.
     */
    public static void pipeStreams(final InputStream in, final OutputStream out, final ByteArrayOutputStream sink) throws IOException {
        pipeStreams(in, out, sink, 0, 0);
    }
    public static void pipeStreams(final InputStream in, final OutputStream out, final ByteArrayOutputStream sink, final long lengthToUseFromStream, final long maxInputStreamLength) throws IOException {
        long totalByteCount = 1;
        int bite = in.read();
        while (bite >= 0) {
            out.write(bite);
            bite = in.read();
            // TODO: hier evtl nur alle 100 bytes checken statt nach jedem byte ??? waere das wirklich schneller ????
            if (maxInputStreamLength > 0 && sink.size() > maxInputStreamLength) throw new ServerAttackException("maximum stream size (DoS protection) threshold exceeded");
            if (lengthToUseFromStream > 0) {
                totalByteCount++;
                if (totalByteCount > lengthToUseFromStream) break;
            }
        }
    }    
    
    
    
    
    
    public static final File writeToTempFile(final InputStream input) throws IOException {
        return writeToTempFile(input, 0, 0);
    }
    /**
     * Writes the input stream into a temp file
     * (does not close the input stream, since that's the caller's responsibility)
     * @param input
     * @param maxInputStreamLength
     * @return
     * @throws java.io.IOException
     */
    public static final File writeToTempFile(final InputStream input, final long lengthToUseFromStream, final long maxInputStreamLength) throws IOException {
        OutputStream output = null;
        try {
            final File temp = File.createTempFile("tmp", null, WebCastellumFilter.TEMP_DIRECTORY);
            output = new BufferedOutputStream( new FileOutputStream(temp) );
            byte[] buffer = new byte[16*1024]; // TODO: Blocksize konfigurierbar machen
            int bytesRead;
            long bytesWritten = 0;
            while ( (bytesRead=input.read(buffer)) != -1 ) {
                if (bytesRead > 0) {
                    // check if we should stop already
                    if (lengthToUseFromStream > 0) {
                        if (bytesWritten + bytesRead > lengthToUseFromStream) {
                            final int bytesToWriteLastChunk = (int) (lengthToUseFromStream - bytesWritten);
                            if (bytesToWriteLastChunk > 0) output.write(buffer, 0, bytesToWriteLastChunk);
                            // stop
                            break;
                        }
                    }
                    output.write(buffer, 0, bytesRead);
                    // TODO: hier evtl nur alle 100 bytes checken statt nach jedem byte ??? waere das wirklich schneller ????
                    if (lengthToUseFromStream > 0 || maxInputStreamLength > 0) {
                        bytesWritten += bytesRead;
                        if (maxInputStreamLength > 0 && bytesWritten > maxInputStreamLength) throw new ServerAttackException("maximum stream size (DoS protection) threshold exceeded");
                    }
                }
            }
            return temp;
        } finally {
            if (output != null) try { output.close(); } catch(IOException ignored) {}
        }
    }
    
    
    
    public static final boolean deleteTempFile(final File temp) {
        if (temp != null) try { 
            return !temp.delete();
        } catch (RuntimeException e) {
            return false;
        }
        return false;
    }
    
    
    
    
    private TempFileUtils() {}
    
        
    
}
    
