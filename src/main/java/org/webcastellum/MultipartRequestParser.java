package org.webcastellum;

import javax.servlet.http.HttpServletRequest;


/**
 * Implementtions must be thread-safe
 */
public interface MultipartRequestParser extends Configurable {
    
    boolean isMultipartRequest(final HttpServletRequest request);

    ParsedMultipartRequest parse(final HttpServletRequest request, final int multipartSizeLimit, final boolean bufferFileUploadsToDisk) throws MultipartRequestParsingException;
    
    
}
