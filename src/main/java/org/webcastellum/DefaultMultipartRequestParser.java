package org.webcastellum;

import java.io.IOException;
import javax.mail.MessagingException;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

public class DefaultMultipartRequestParser implements MultipartRequestParser {

    public void setFilterConfig(final FilterConfig filterConfig) throws FilterConfigurationException {
        //final ConfigurationManager configManager = ConfigurationUtils.createConfigurationManager(filterConfig);
    }

    
    public boolean isMultipartRequest(final HttpServletRequest request) {
        /*
        if (!"post".equals(request.getMethod().toLowerCase())) {
            return false;
        }*/ // besser auskommentiert lassen, damit auch ein Angreifer nicht per Multipart GET (was eigentlich nicht geht) zugreifen kann
        final String contentType = RequestUtils.getContentType(request);
        final int contentLength = request.getContentLength();
        // IE handles redirects using the previous request's content type so we need to 
        // ignore the Content-Type on requests with a Content-Length less than zero.
        return contentType != null && contentType.startsWith("multipart/form-data") && contentLength > -1;
    }

    
    
    public ParsedMultipartRequest parse(final HttpServletRequest request, final int multipartSizeLimit, final boolean bufferFileUploadsToDisk) throws MultipartRequestParsingException {
        try {
            final ServletRequestDataSourceAdapter adapter = new ServletRequestDataSourceAdapter(request, multipartSizeLimit, bufferFileUploadsToDisk);
            final ParsedMultipartRequest parsedRequest = new DefaultParsedMultipartRequest(adapter);
            return parsedRequest;
        } catch (IOException e) { 
            throw new MultipartRequestParsingException(e);
        } catch (MessagingException e) { 
            throw new MultipartRequestParsingException(e);
        } catch (RuntimeException e) { 
            throw new MultipartRequestParsingException(e);
        }
    }


}
