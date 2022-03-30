package org.webcastellum;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

public class DefaultParsedMultipartRequest implements ParsedMultipartRequest {

    private ServletRequestDataSourceAdapter adapter;
    private MimeMultipart form;
    
    
    
    public DefaultParsedMultipartRequest(final ServletRequestDataSourceAdapter adapter) throws MessagingException {
        this.adapter = adapter;
        this.form = new MimeMultipart(adapter);
    }
    
    
    
    
    

    public int getElementCount() throws MultipartRequestParsingException {
        try {
            return this.form.getCount();
        } catch (MessagingException e) {
            throw new MultipartRequestParsingException(e);
        }
    }

    
    public String getFormFieldName(int i) throws MultipartRequestParsingException {
        try {
            final BodyPart bp = this.form.getBodyPart(i);
            final String[] headervalues = bp.getHeader("Content-Disposition");
            final String contentdisposition = (headervalues.length > 0) ? headervalues[0] : "";
            final Map map = ServerUtils.parseContentDisposition(contentdisposition);
            return (String) map.get("name");
        } catch (MessagingException e) {
            throw new MultipartRequestParsingException(e);
        }
    }

    
    // returns null when no file (i.e. regular form param)
    public String getSubmittedFileName(int i) throws MultipartRequestParsingException {
        try {
            final BodyPart bp = this.form.getBodyPart(i);
            return bp.getFileName();
        } catch (MessagingException e) {
            throw new MultipartRequestParsingException(e);
        }
    }

    
    public String getFormFieldContent(int i) throws MultipartRequestParsingException {
        try {
            final BodyPart bp = this.form.getBodyPart(i);
            final Object content = bp.getContent();
            return content == null ? null : content.toString();
        } catch (IOException e) {
            throw new MultipartRequestParsingException(e);
        } catch (MessagingException e) {
            throw new MultipartRequestParsingException(e);
        }
    }

    
    public long getSubmittedFileSize(int i) throws MultipartRequestParsingException {
        try {
            final BodyPart bp = this.form.getBodyPart(i);
            return bp.getSize();
        } catch (MessagingException e) {
            throw new MultipartRequestParsingException(e);
        }
    }

    
    public String getSubmittedFileContentType(int i) throws MultipartRequestParsingException {
        try {
            final BodyPart bp = this.form.getBodyPart(i);
            return bp.getContentType();
        } catch (MessagingException e) {
            throw new MultipartRequestParsingException(e);
        }
    }

    
    public InputStream getSubmittedFileInputStream(int i) throws MultipartRequestParsingException {
        try {
            final BodyPart bp = this.form.getBodyPart(i);
            return bp.getInputStream();
        } catch (IOException e) {
            throw new MultipartRequestParsingException(e);
        } catch (MessagingException e) {
            throw new MultipartRequestParsingException(e);
        }
    }
    
    
    
    
    
    
    
    public InputStream replayCapturedInputStream() throws MultipartRequestParsingException {
        try {
            return this.adapter.getInputStream();
        } catch (IOException e) {
            throw new MultipartRequestParsingException(e);
        }
    }
    
    
    

    public void clearAllButCapturedInputStream() {
        if (this.form != null) {
            this.form = null;
        }
    }

    public void clearAll() {
        clearAllButCapturedInputStream();
        if (this.adapter != null) {
            this.adapter.clear();
            this.adapter = null;
        }
    }

}
