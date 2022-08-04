package org.webcastellum;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public final class MultipartServletRequest extends HttpServletRequestWrapper {

    private static final boolean DEBUG = false;

    private boolean hasUrlParamsOnFirstAttempt = false;

    //private ServletRequestDataSourceAdapter adapter;
    private ParsedMultipartRequest parsedRequest;

    /**
     * Map of all the files uploaded in the multipart form. Only set if
     * <code>multiPart</code> is <code>true</code>.
     */
    private Map<String, List<MultipartFileInfo>> filesOfRequest = new HashMap<>();

    /**
     * Map of all the parameter values uploaded in the multipart form. Only set
     * if
     * <code>multiPart</code> is <code>true</code>.
     */
    private Map<String, List<String>> formParametersOfRequest = new HashMap<>();
    private Map<String, List<String>> urlParametersOfRequest = new HashMap<>();
    private Map<String, List<String>> urlAndFormParametersOfRequestMerged = new HashMap<>(); // TODO: besser mit weniger RAM-Verbrauch nicht so redundant abbilden ?!?

    private boolean hideMultipartFormParametersSinceWeAreWithingApplicationAccess = false;

    private final boolean bufferFileUploadsToDisk;

    private final MultipartSizeLimitDefinition multipartSizeLimit;
    private int fileCount;

    public MultipartServletRequest(final MultipartRequestParser parser, final HttpServletRequest request, final boolean bufferFileUploadsToDisk) throws IOException {
        this(parser, request, null, bufferFileUploadsToDisk);
    }

    public MultipartServletRequest(final MultipartRequestParser parser, final HttpServletRequest request, final MultipartSizeLimitDefinition multipartSizeLimit, final boolean bufferFileUploadsToDisk) throws IOException {
        super(request);
        this.bufferFileUploadsToDisk = bufferFileUploadsToDisk;
        this.multipartSizeLimit = multipartSizeLimit; // may be null
        Logger.getLogger(MultipartServletRequest.class.getName()).log(Level.FINE, "Calling constructor for a potentially multipart submitted form: {0}", request);
        try {
            this.parsedRequest = parser.parse(request, multipartSizeLimit == null ? 0 : multipartSizeLimit.getMaxInputStreamLength(), bufferFileUploadsToDisk);
            extractSubmittedFormValues();
            extractSubmittedUrlValues();
        } catch (MultipartRequestParsingException | RuntimeException e) {
            throw new IOException(e.toString());
        } finally {
            if (this.parsedRequest != null) {
                this.parsedRequest.clearAllButCapturedInputStream();
            }
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // multipart handling:
        try {
            return this.parsedRequest == null ? null : new ServletInputStreamAdapter(this.parsedRequest.replayCapturedInputStream());
        } catch (MultipartRequestParsingException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Map getParameterMap() {
        return ServerUtils.convertMapOfCollectionsToMapOfStringArrays(this.hideMultipartFormParametersSinceWeAreWithingApplicationAccess ? this.urlParametersOfRequest : this.urlAndFormParametersOfRequestMerged);
    }

    @Override
    public Enumeration getParameterNames() {
        return new IteratorEnumerationAdapter((this.hideMultipartFormParametersSinceWeAreWithingApplicationAccess ? this.urlParametersOfRequest : this.urlAndFormParametersOfRequestMerged).keySet().iterator());
    }

    @Override
    public String getParameter(String name) {
        // multipart handling:
        final List<String> values = (this.hideMultipartFormParametersSinceWeAreWithingApplicationAccess ? this.urlParametersOfRequest : this.urlAndFormParametersOfRequestMerged).get(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        final Object result = values.get(0);
        return result == null ? null : result.toString();
    }

    @Override
    public String[] getParameterValues(String name) {
        // multipart handling:
        final List<String> values = (this.hideMultipartFormParametersSinceWeAreWithingApplicationAccess ? this.urlParametersOfRequest : this.urlAndFormParametersOfRequestMerged).get(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return ServerUtils.convertCollectionToStringArray(values);
    }

    public Iterator<MultipartFileInfo> getSubmittedFiles(String name) {
        // multipart handling:
        final List<MultipartFileInfo> files = (List) this.filesOfRequest.get(name);
        if (files == null || files.isEmpty()) {
            return Collections.EMPTY_SET.iterator();
        }
        return files.iterator();
    }

    public boolean isHideMultipartFormParametersSinceWeAreWithingApplicationAccess() {
        return hideMultipartFormParametersSinceWeAreWithingApplicationAccess;
    }

    public void setHideMultipartFormParametersSinceWeAreWithingApplicationAccess(boolean hideMultipartFormParametersSinceWeAreWithingApplicationAccess) {
        this.hideMultipartFormParametersSinceWeAreWithingApplicationAccess = hideMultipartFormParametersSinceWeAreWithingApplicationAccess;
    }

    public void clear() {
        if (this.parsedRequest != null) {
            this.parsedRequest.clearAll();
            this.parsedRequest = null;
        }
        if (this.filesOfRequest != null) {
            this.filesOfRequest.clear();
        }
    }

    // added to also include URL params of the form submit... yes, that's all possible...
    private void extractSubmittedFormValues() throws MultipartRequestParsingException, IOException {
        int numElements = this.parsedRequest.getElementCount();

        String formParamName = null;
        String value = null;

        String filename = null;
        String contenttype = null;
        long size = 0;
        MultipartFileInfo mpFileInfo = null;

        for (int i = 0; i < numElements; ++i) {
            formParamName = this.parsedRequest.getFormFieldName(i);
            filename = this.parsedRequest.getSubmittedFileName(i);
            if (filename == null) { // THIS PARAMETER VALUE IS A REGULAR STRING PARAMETER
                value = this.parsedRequest.getFormFieldContent(i);
                addToMapOfCollections(this.formParametersOfRequest, formParamName, value);
                addToMapOfCollections(this.urlAndFormParametersOfRequestMerged, formParamName, value);
            } else { // THIS PARAMETER VALUE IS A FILE
                fileCount++;
                // check size limits (stream size limit is checked in datasource adapter)
                if (multipartSizeLimit != null && multipartSizeLimit.getMaxFileUploadCount() > 0) {
                    if (fileCount > multipartSizeLimit.getMaxFileUploadCount()) {
                        throw new ServerAttackException("maxFileUploadCount threshold for multipart file uploads exceeded");
                    }
                }
                size = this.parsedRequest.getSubmittedFileSize(i);
                if (multipartSizeLimit != null) {
                    if (multipartSizeLimit.getMaxFileNameLength() > 0 && filename.length() > multipartSizeLimit.getMaxFileNameLength()) {
                        throw new ServerAttackException("maxFileNameLength threshold for multipart file uploads exceeded");
                    }
                    if (multipartSizeLimit.getMaxFileUploadSize() > 0 && size > multipartSizeLimit.getMaxFileUploadSize()) {
                        throw new ServerAttackException("maxFileUploadSize threshold for multipart file uploads exceeded");
                    }
                }
                contenttype = this.parsedRequest.getSubmittedFileContentType(i); // set by the client -- can't be trusted
                final InputStream in = this.parsedRequest.getSubmittedFileInputStream(i);
                mpFileInfo = (size > 0)
                        ? new MultipartFileInfo(formParamName, contenttype, filename, in, size, bufferFileUploadsToDisk)
                        : new MultipartFileInfo(formParamName, contenttype, filename, in, bufferFileUploadsToDisk);
                in.close(); // TODO: passend ? ja, oder?
                // check against ZIP bombs
                if (multipartSizeLimit != null && (multipartSizeLimit.getZipBombThresholdTotalSize() > 0 || multipartSizeLimit.getZipBombThresholdFileCount() > 0)) {
                    // TODO: wenn eh per file auf disk gebuffered wurde, ist ein mpFileInfo.getFile() um erneut ein file daraus zuschreiben (in ZipScannerUtils) doppelt gemoppelt... daher im falle von disk buffering lieber einfach nur den file-handle druchreichen an ZipScannerUtils....
                    if (ZipScannerUtils.isZipBomb(mpFileInfo.getFile(), multipartSizeLimit.getZipBombThresholdTotalSize(), multipartSizeLimit.getZipBombThresholdFileCount())) {
                        throw new ServerAttackException("Potential ZIP bomb detected");
                    }
                }
                addToMapOfCollections(this.filesOfRequest, formParamName, mpFileInfo);
            }
        }
    }

    private void extractSubmittedUrlValues() {
        if (hasUrlParamsOnFirstAttempt) {
            return;
        }
        // NOW THE URL PARAMS ALSO (they can be safely taken from the underlying original request, which holds all URL params BUT NO form params since we're havig a multipart form submit here...)
        Logger.getLogger(MultipartServletRequest.class.getName()).log(Level.INFO, "==> in delegate: {0}", getRequest().getParameterMap());
        for (final Enumeration urlParamNames = getRequest().getParameterNames(); urlParamNames.hasMoreElements();) {
            final String urlParamName = (String) urlParamNames.nextElement();
            Logger.getLogger(MultipartServletRequest.class.getName()).log(Level.FINE, "---> URL PARAM IN MULTIPART FORM: {0}", urlParamName);
            final String[] values = getRequest().getParameterValues(urlParamName);
            if (values != null) {
                for (String value : values) {
                    addToMapOfCollections(this.urlParametersOfRequest, urlParamName, value);
                    addToMapOfCollections(this.urlAndFormParametersOfRequestMerged, urlParamName, value);
                    hasUrlParamsOnFirstAttempt = true;
                    Logger.getLogger(MultipartServletRequest.class.getName()).log(Level.INFO, "           ---> with value: {0}", value);
                }
            }
        }
    }

    public void reextractSubmittedUrlValues() {
        extractSubmittedUrlValues();
    }

    private static void addToMapOfCollections(final Map<String,List<String>> map, final String key, final String value) {
        List<String> col = map.get(key);
        if (col == null) {
            col = new ArrayList<>();
            map.put(key, col);
        }
        col.add(value);
    }

    private static void addToMapOfCollections(final Map<String,List<MultipartFileInfo>> map, final String key, final MultipartFileInfo value) {
        List<MultipartFileInfo> col = map.get(key);
        if (col == null) {
            col = new ArrayList/*<MultipartFileInfo>*/();
            map.put(key, col);
        }
        col.add(value);
    }

}
