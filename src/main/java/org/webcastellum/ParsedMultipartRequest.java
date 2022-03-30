package org.webcastellum;

import java.io.InputStream;

public interface ParsedMultipartRequest {

    InputStream replayCapturedInputStream() throws MultipartRequestParsingException;
    
    int getElementCount() throws MultipartRequestParsingException;
    
    String getFormFieldName(int i) throws MultipartRequestParsingException;
    
    String getFormFieldContent(int i) throws MultipartRequestParsingException;

    String getSubmittedFileName(int i) throws MultipartRequestParsingException;
    long getSubmittedFileSize(int i) throws MultipartRequestParsingException;
    String getSubmittedFileContentType(int i) throws MultipartRequestParsingException;
    InputStream getSubmittedFileInputStream(int i) throws MultipartRequestParsingException;

    void clearAllButCapturedInputStream();
    void clearAll();
    
}
