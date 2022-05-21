package org.webcastellum;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

/**
 *  This response wrapper class extends the support class HttpServletResponseWrapper,
 *  which implements all the methods in the HttpServletResponse interface, as
 *  delegations to the wrapped response.
 *  You only need to override the methods that you need to change.
 *  You can get access to the wrapped response using the method getResponse()
 */
public final class ResponseWrapper extends HttpServletResponseWrapper {
    
    private static final boolean DEBUG = false;
    
    private static final Pattern PATTERN_CRLF = Pattern.compile("\r\n");  private Matcher matcherCRLF;
    private static final String HEADER_LAST_MODIFIED = "Last-Modified";
    private static final int FUTURE_LAST_MODIFIED_TOLERANCE = 250; // TODO konfigurierbar machen

    
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String APPLICATION_PDF = "application/pdf";
    private static final String APPLICATION_BINARY = "application/octet-stream";

    
    
    
    // stuff for proper HTTP date parsing (not as static variables, since SimpleDateFormat is not thread-safe: therefore we instantiate new ones for every response wrapper)
    private final SimpleDateFormat[] HTTP_DATE_FORMATS = {
        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
        new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
        new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US)
    };
    private final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
    {
        HTTP_DATE_FORMATS[0].setTimeZone(GMT_TIMEZONE);
        HTTP_DATE_FORMATS[1].setTimeZone(GMT_TIMEZONE);
        HTTP_DATE_FORMATS[2].setTimeZone(GMT_TIMEZONE);
    }
    
    private final RequestWrapper request;
    private final boolean blockResponseHeadersWithCRLF, blockFutureLastModifiedHeaders, blockInvalidLastModifiedHeaders, blockNonLocalRedirects, useFullPathForResourceToBeAccessedProtection, additionalFullResourceRemoval, additionalMediumResourceRemoval, maskAmpersandsInModifiedLinks, hiddenFormFieldProtection,
            selectboxProtection, checkboxProtection, radiobuttonProtection, selectboxValueMasking, checkboxValueMasking, radiobuttonValueMasking,
            reuseSessionContent, applySetAfterWrite;
    private final String honeylinkPrefix, honeylinkSuffix;
    private final short honeylinkMaxPerPage;
    private final boolean randomizeHoneylinksOnEveryRequest;
    private final AttackHandler attackHandler;
    
    private final ContentInjectionHelper contentInjectionHelper;
    
    private final String requestedURL, servletPath, contextPath, ip;
    
    private final Set/*<String>*/ responseModificationContentTypes;
    
    private final boolean isCurrentRequestOfRelevantResourceType, isOptimizationHint, appendQuestionmarkOrAmpersandToLinks, appendSessionIdToLinks;
    //private boolean mayBeModified = false;


    private final Matcher[] matchersToExcludeLinksWithinScripts, matchersToExcludeLinksWithinTags, matchersToExcludeCompleteScript, matchersToExcludeCompleteTag;
    private final Matcher[] matchersToCaptureLinksWithinScripts, matchersToCaptureLinksWithinTags;
    private final WordDictionary[] prefiltersToExcludeLinksWithinScripts, prefiltersToExcludeLinksWithinTags, prefiltersToExcludeCompleteScript, prefiltersToExcludeCompleteTag;
    private final WordDictionary[] prefiltersToCaptureLinksWithinScripts, prefiltersToCaptureLinksWithinTags;
    private final int[][] groupNumbersToCaptureLinksWithinScripts, groupNumbersToCaptureLinksWithinTags;
    //private final List<String>[] tagNamesToCheck;
    
    
    
    
    private String tokenKey,tokenValue, parameterAndFormProtectionKeyKey;//, hiddenFormFieldProtectionKey,hiddenFormFieldProtectionValue, selectBoxProtectionKey,selectBoxProtectionValue;
    private boolean secretTokensApplied = false, parameterAndFormTokensApplied = false;
    
    private String cryptoDetectionString;
    private final Cipher cipher;
    private CryptoKeyAndSalt cryptoKey;
    private boolean cryptoApplied = false, pdfXssProtection = false;
    
    
    
    private int statusCode;
    private String contentType, contentDisposition;
    
    
    
    
    
    public ResponseWrapper(final HttpServletResponse response, final RequestWrapper request, final AttackHandler attackHandler, final ContentInjectionHelper contentInjectionHelper, final boolean isOptimizationHint, final String cryptoDetectionString, final Cipher cipher, final CryptoKeyAndSalt cryptoKey, final String tokenKey,final String tokenValue, final String parameterAndFormProtectionKeyKey, final boolean blockResponseHeadersWithCRLF, final boolean blockFutureLastModifiedHeaders, final boolean blockInvalidLastModifiedHeaders, final boolean blockNonLocalRedirects, final String ip, final Set/*<String>*/ responseModificationContentTypes,
            final WordDictionary[] prefiltersToExcludeCompleteScript, final Matcher[] matchersToExcludeCompleteScript, 
            final WordDictionary[] prefiltersToExcludeCompleteTag, final Matcher[] matchersToExcludeCompleteTag,
            final WordDictionary[] prefiltersToExcludeLinksWithinScripts, final Matcher[] matchersToExcludeLinksWithinScripts, 
            final WordDictionary[] prefiltersToExcludeLinksWithinTags, final Matcher[] matchersToExcludeLinksWithinTags,
            final WordDictionary[] prefiltersToCaptureLinksWithinScripts, final Matcher[] matchersToCaptureLinksWithinScripts, 
            final WordDictionary[] prefiltersToCaptureLinksWithinTags, final Matcher[] matchersToCaptureLinksWithinTags,
            final int[][] groupNumbersToCaptureLinksWithinScripts, 
            final int[][] groupNumbersToCaptureLinksWithinTags, 
            //final List<String>[] tagNamesToCheck,
            final boolean useFullPathForResourceToBeAccessedProtection, final boolean additionalFullResourceRemoval, final boolean additionalMediumResourceRemoval, final boolean maskAmpersandsInModifiedLinks,
            final boolean hiddenFormFieldProtection, final boolean selectboxProtection, final boolean checkboxProtection, final boolean radiobuttonProtection, final boolean selectboxValueMasking, final boolean checkboxValueMasking, final boolean radiobuttonValueMasking,
            final boolean appendQuestionmarkOrAmpersandToLinks, final boolean appendSessionIdToLinks, final boolean reuseSessionContent,
            final String honeylinkPrefix, final String honeylinkSuffix, final short honeylinkMaxPerPage, final boolean randomizeHoneylinksOnEveryRequest, 
            final boolean pdfXssProtection, final boolean applySetAfterWrite) {
        super(response);
        if (DEBUG) System.out.println(" =========> new RequestWrapper");
        if (request == null) throw new NullPointerException("request must not be null");
        if (attackHandler == null) throw new NullPointerException("attackHandler must not be null");
        if (contentInjectionHelper == null) throw new NullPointerException("contentInjectionHelper must not be null");
        if (ip == null) throw new NullPointerException("ip must not be null");
        this.request = request;
        this.attackHandler = attackHandler;
        this.contentInjectionHelper = contentInjectionHelper;
        
        this.cryptoDetectionString = cryptoDetectionString; // may be null
        this.cipher = cipher; // may be null
        this.cryptoKey = cryptoKey; // may be null
        
        this.tokenKey = tokenKey; // may be null
        this.tokenValue = tokenValue; // may be null

        this.useFullPathForResourceToBeAccessedProtection = useFullPathForResourceToBeAccessedProtection;
        this.additionalFullResourceRemoval = additionalFullResourceRemoval;
        this.additionalMediumResourceRemoval = additionalMediumResourceRemoval;
        
        if (matchersToExcludeCompleteScript == null) throw new NullPointerException("matchersToExcludeCompleteScript must not be null");
        if (matchersToExcludeCompleteTag == null) throw new NullPointerException("matchersToExcludeCompleteTag must not be null");
        this.prefiltersToExcludeCompleteScript = prefiltersToExcludeCompleteScript;
        this.matchersToExcludeCompleteScript = matchersToExcludeCompleteScript;
        this.prefiltersToExcludeCompleteTag = prefiltersToExcludeCompleteTag;
        this.matchersToExcludeCompleteTag = matchersToExcludeCompleteTag;
        if (matchersToExcludeLinksWithinScripts == null) throw new NullPointerException("matchersToExcludeLinksWithinScripts must not be null");
        if (matchersToExcludeLinksWithinTags == null) throw new NullPointerException("matchersToExcludeLinksWithinTags must not be null");
        this.prefiltersToExcludeLinksWithinScripts = prefiltersToExcludeLinksWithinScripts;
        this.matchersToExcludeLinksWithinScripts = matchersToExcludeLinksWithinScripts;
        this.prefiltersToExcludeLinksWithinTags = prefiltersToExcludeLinksWithinTags;
        this.matchersToExcludeLinksWithinTags = matchersToExcludeLinksWithinTags;
        
        if (matchersToCaptureLinksWithinScripts == null) throw new NullPointerException("matchersToCaptureLinksWithinScripts must not be null");
        if (matchersToCaptureLinksWithinTags == null) throw new NullPointerException("matchersToCaptureLinksWithinTags must not be null");
        this.prefiltersToCaptureLinksWithinScripts = prefiltersToCaptureLinksWithinScripts;
        this.matchersToCaptureLinksWithinScripts = matchersToCaptureLinksWithinScripts;
        this.prefiltersToCaptureLinksWithinTags = prefiltersToCaptureLinksWithinTags;
        this.matchersToCaptureLinksWithinTags = matchersToCaptureLinksWithinTags;
        for (Matcher matchersToCaptureLinksWithinScript : matchersToCaptureLinksWithinScripts) {
            if (matchersToCaptureLinksWithinScript.groupCount() < 1) {
                throw new IllegalArgumentException("Pattern must have an explicitly defined capturing group to identify the URL: " + matchersToCaptureLinksWithinScript.pattern());
            }
        }
        for (Matcher matchersToCaptureLinksWithinTag : matchersToCaptureLinksWithinTags) {
            if (matchersToCaptureLinksWithinTag.groupCount() < 1) {
                throw new IllegalArgumentException("Pattern must have an explicitly defined capturing group to identify the URL: " + matchersToCaptureLinksWithinTag.pattern());
            }
        }

        this.groupNumbersToCaptureLinksWithinScripts = groupNumbersToCaptureLinksWithinScripts;
        this.groupNumbersToCaptureLinksWithinTags = groupNumbersToCaptureLinksWithinTags;
        if (matchersToCaptureLinksWithinScripts.length != groupNumbersToCaptureLinksWithinScripts.length) throw new IllegalArgumentException("Lengths of capturing pattern and group-number array must be equal");
        if (matchersToCaptureLinksWithinTags.length != groupNumbersToCaptureLinksWithinTags.length) throw new IllegalArgumentException("Lengths of capturing pattern and group-number array must be equal");
        if (matchersToCaptureLinksWithinScripts.length != matchersToExcludeLinksWithinScripts.length) throw new IllegalArgumentException("Lengths of capturing pattern and exclusion pattern array must be equal");
        if (matchersToCaptureLinksWithinTags.length != matchersToExcludeLinksWithinTags.length) throw new IllegalArgumentException("Lengths of capturing pattern and exclusion pattern array must be equal");
        //this.tagNamesToCheck = tagNamesToCheck;


        
        
        this.parameterAndFormProtectionKeyKey = parameterAndFormProtectionKeyKey; // may be null
        this.blockResponseHeadersWithCRLF = blockResponseHeadersWithCRLF;
        this.blockFutureLastModifiedHeaders = blockFutureLastModifiedHeaders;
        this.blockInvalidLastModifiedHeaders = blockInvalidLastModifiedHeaders;
        this.blockNonLocalRedirects = blockNonLocalRedirects;
        this.requestedURL = ""+request.getRequestURL();
        this.servletPath = request.getServletPath();
        this.contextPath = request.getContextPath();
        this.ip = ip;
        this.responseModificationContentTypes = responseModificationContentTypes;
        this.isCurrentRequestOfRelevantResourceType = !this.contentInjectionHelper.isMatchingOutgoingResponseModificationExclusion(servletPath, request.getRequestURI());
        this.isOptimizationHint = isOptimizationHint;
        this.maskAmpersandsInModifiedLinks = maskAmpersandsInModifiedLinks;

        this.hiddenFormFieldProtection = hiddenFormFieldProtection;
        this.selectboxProtection = selectboxProtection;
        this.checkboxProtection = checkboxProtection;
        this.radiobuttonProtection = radiobuttonProtection;
        this.selectboxValueMasking = selectboxValueMasking;
        this.checkboxValueMasking = checkboxValueMasking;
        this.radiobuttonValueMasking = radiobuttonValueMasking;
        
        this.appendQuestionmarkOrAmpersandToLinks = appendQuestionmarkOrAmpersandToLinks;
        this.appendSessionIdToLinks = appendSessionIdToLinks;
        this.reuseSessionContent = reuseSessionContent;
        
        this.honeylinkPrefix = honeylinkPrefix;
        this.honeylinkSuffix = honeylinkSuffix;
        this.honeylinkMaxPerPage = honeylinkMaxPerPage;
        this.randomizeHoneylinksOnEveryRequest = randomizeHoneylinksOnEveryRequest;
        
        this.pdfXssProtection = pdfXssProtection;
        
        this.applySetAfterWrite = applySetAfterWrite;
    }
    
    
    
    

    
    
    
    // as the caller is allowed to re-define (and therefore overwrite) the crypto settings as is the case in renew-session-points
    public /*synchronized*/ void redefineCryptoDetectionString(final String cryptoDetectionString) {
        if (this.cryptoApplied) throw new IllegalStateException("Crypto setting already applied to response (can not redefine any more)");
        this.cryptoDetectionString = cryptoDetectionString;
    }
    public /*synchronized*/ void redefineCryptoKey(final CryptoKeyAndSalt cryptoKey) {
        if (this.cryptoApplied) throw new IllegalStateException("Crypto setting already applied to response (can not redefine any more)");
        this.cryptoKey = cryptoKey;
    }
    
    
    
    // as the caller is allowed to re-define (and therefore overwrite) the secret tokens as is the case in renew-session-points
    public /*synchronized*/ void redefineSecretTokenKey(final String key) {
        if (this.secretTokensApplied) throw new IllegalStateException("Secret tokens already applied to response (can not redefine any more)");
        this.tokenKey = key;
    }
    public /*synchronized*/ void redefineSecretTokenValue(final String value) {
        if (this.secretTokensApplied) throw new IllegalStateException("Secret tokens already applied to response (can not redefine any more)");
        this.tokenValue = value;
    }
    
    
    
    // as the caller is allowed to re-define (and therefore overwrite) the paramAndForm tokens as is the case in renew-session-points
    public /*synchronized*/ void redefineParameterAndFormProtectionKey(final String key) {
        if (this.parameterAndFormTokensApplied) throw new IllegalStateException("Param-and-Form tokens already applied to response (can not redefine any more)");
        this.parameterAndFormProtectionKeyKey = key;
    }

    /* not needed
    public /*synchronized* / void redefineParameterAndFormProtectionValue(final String value) {
        if (this.parameterAndFormTokensApplied) throw new IllegalStateException("Param-and-Form tokens already applied to response (can not redefine any more)");
        this.parameterAndFormProtectionValue = value;
    }*/
    
    
    
    
    
    
    private boolean isResponseModificationAllowed() {
        if (this.isCurrentRequestOfRelevantResourceType) {
            // determine content type
            String contentTypeUpperCased = extractContentTypeUpperCased();
            if (contentTypeUpperCased == null) contentTypeUpperCased = "NULL"; // yes, the word "null" means "match with an unset content-type" here...
            if (this.responseModificationContentTypes != null && this.responseModificationContentTypes.size() > 0) {
                // check if it matches
                if ( this.responseModificationContentTypes.contains(contentTypeUpperCased) ) {
                    if (DEBUG) System.out.println("isResponseModificationAllowed() is true");
                    //this.mayBeModified = true;
                    return true;
                } else if (DEBUG) System.out.println("isResponseModificationAllowed() check 3 is false ("+contentTypeUpperCased+"): "+this.responseModificationContentTypes);
            } else if (DEBUG) System.out.println("isResponseModificationAllowed() check 2 is false");
        } else if (DEBUG) System.out.println("isResponseModificationAllowed() check 1 is false");
        if (DEBUG) System.out.println("isResponseModificationAllowed() is false");
        return false;
    }
    
    
    private ServletOutputStream cachedServletOutputStream;
    //1.5@Override
    public /*synchronized*/ ServletOutputStream getOutputStream() throws IOException {
        if (DEBUG) System.out.println("getOutputStream() called");
        if (this.cachedServletOutputStream == null) {
            this.secretTokensApplied = true;
            this.parameterAndFormTokensApplied = true;
            this.cryptoApplied = true;
            if (!this.isOptimizationHint && isResponseModificationAllowed()) {
                //TODO: konfigurierbar statt hart das encoding auf super.getCharacterEncoding() setzen ?!? oder so lassen ?!?
                //this.setContentType("text/html; charset=UTF-8");
                this.cachedServletOutputStream = this.contentInjectionHelper.addActivatedFilters(super.getCharacterEncoding(),super.getOutputStream(),this.requestedURL,this.contextPath,this.servletPath,tokenKey,tokenValue,cryptoDetectionString,cipher,cryptoKey,this.parameterAndFormProtectionKeyKey,request,this,
                        this.prefiltersToExcludeCompleteScript, this.matchersToExcludeCompleteScript, 
                        this.prefiltersToExcludeCompleteTag, this.matchersToExcludeCompleteTag,
                        this.prefiltersToExcludeLinksWithinScripts, this.matchersToExcludeLinksWithinScripts, 
                        this.prefiltersToExcludeLinksWithinTags, this.matchersToExcludeLinksWithinTags,
                        this.prefiltersToCaptureLinksWithinScripts, this.matchersToCaptureLinksWithinScripts, 
                        this.prefiltersToCaptureLinksWithinTags, this.matchersToCaptureLinksWithinTags,
                        this.groupNumbersToCaptureLinksWithinScripts, 
                        this.groupNumbersToCaptureLinksWithinTags, 
                        //this.tagNamesToCheck,
                        this.useFullPathForResourceToBeAccessedProtection, this.additionalFullResourceRemoval, this.additionalMediumResourceRemoval, this.maskAmpersandsInModifiedLinks,
                        this.hiddenFormFieldProtection, this.selectboxProtection, this.checkboxProtection, this.radiobuttonProtection, this.selectboxValueMasking, this.checkboxValueMasking, this.radiobuttonValueMasking,
                        this.appendQuestionmarkOrAmpersandToLinks, this.appendSessionIdToLinks, this.reuseSessionContent,
                        this.honeylinkPrefix, this.honeylinkSuffix, this.honeylinkMaxPerPage, this.randomizeHoneylinksOnEveryRequest, this.applySetAfterWrite);
            } else this.cachedServletOutputStream = super.getOutputStream();
        }
        return this.cachedServletOutputStream;
    }
    private PrintWriter cachedPrintWriter;
    //1.5@Override
    public /*synchronized*/ PrintWriter getWriter() throws IOException {
        if (DEBUG) System.out.println("getWriter() called");
        if (this.cachedPrintWriter == null) {
            this.secretTokensApplied = true;
            this.parameterAndFormTokensApplied = true;
            this.cryptoApplied = true;
            if (!this.isOptimizationHint && isResponseModificationAllowed()) {
                this.cachedPrintWriter = this.contentInjectionHelper.addActivatedFilters(super.getWriter(),this.requestedURL,this.contextPath,this.servletPath,tokenKey,tokenValue,cryptoDetectionString,cipher,cryptoKey,this.parameterAndFormProtectionKeyKey,request,this,
                        this.prefiltersToExcludeCompleteScript, this.matchersToExcludeCompleteScript, 
                        this.prefiltersToExcludeCompleteTag, this.matchersToExcludeCompleteTag,
                        this.prefiltersToExcludeLinksWithinScripts, this.matchersToExcludeLinksWithinScripts, 
                        this.prefiltersToExcludeLinksWithinTags, this.matchersToExcludeLinksWithinTags,
                        this.prefiltersToCaptureLinksWithinScripts, this.matchersToCaptureLinksWithinScripts, 
                        this.prefiltersToCaptureLinksWithinTags, this.matchersToCaptureLinksWithinTags,
                        this.groupNumbersToCaptureLinksWithinScripts, 
                        this.groupNumbersToCaptureLinksWithinTags, 
                        //this.tagNamesToCheck,
                        this.useFullPathForResourceToBeAccessedProtection, this.additionalFullResourceRemoval, this.additionalMediumResourceRemoval, this.maskAmpersandsInModifiedLinks,
                        this.hiddenFormFieldProtection, this.selectboxProtection, this.checkboxProtection, this.radiobuttonProtection, this.selectboxValueMasking, this.checkboxValueMasking, this.radiobuttonValueMasking,
                        this.appendQuestionmarkOrAmpersandToLinks, this.appendSessionIdToLinks, this.reuseSessionContent,
                        this.honeylinkPrefix, this.honeylinkSuffix, this.honeylinkMaxPerPage, this.randomizeHoneylinksOnEveryRequest, this.applySetAfterWrite);
            } else this.cachedPrintWriter = super.getWriter();
        }
        return this.cachedPrintWriter;
    }
    
    
    
    
    
    
    
    //1.5@Override
    public void flushBuffer() throws IOException {
        if (DEBUG) System.out.println("Flushing buffer");
        super.flushBuffer();
        if (this.cachedPrintWriter != null) this.cachedPrintWriter.flush();
        if (this.cachedServletOutputStream != null) this.cachedServletOutputStream.flush();
    }
    

    
    
    //1.5@Override
    public void setStatus(final int statusCode) {
        this.statusCode = statusCode;
        super.setStatus(statusCode);
    }
    //1.5@Override
    public void setStatus(final int statusCode, final String message) {
        this.statusCode = statusCode;
        super.setStatus(statusCode, message);
    }
    
    //1.5@Override
    public void sendError(final int statusCode) throws IOException {
        this.statusCode = statusCode;
        super.sendError(statusCode);
    }
    //1.5@Override
    public void sendError(final int statusCode, final String message) throws IOException {
        this.statusCode = statusCode;
        super.sendError(statusCode, message);
    }
    
    protected int getCapturedStatus() {
        return this.statusCode;
    }
    
    
    
    //1.5@Override
    public void setContentLength(int length) {
        if (WebCastellumFilter.REMOVE_CONTENT_LENGTH_FOR_MODIFIABLE_RESPONSES && /*this.mayBeModified*/ isResponseModificationAllowed()) {
            if (DEBUG) System.out.println("Original response content length removed");
        } else {
            if (DEBUG) System.out.println("Response content length: "+length);
            super.setContentLength(length);
        }
    }
    
    //1.5@Override
    public void setContentType(String type) {
        if (DEBUG) System.out.println("Response content type: "+type);
        if (this.blockResponseHeadersWithCRLF) checkHeaderAgainstCRLF(type);
        if (this.pdfXssProtection && APPLICATION_PDF.equalsIgnoreCase(type)) {
            String filename = null;
            try {
                if (contentDisposition != null) {
                    final Map existingContentDispositionParsed = ServerUtils.parseContentDisposition(contentDisposition);
                    filename = (String) existingContentDispositionParsed.get("filename");
                }
            } catch (Exception e) {
                attackHandler.logWarningRequestMessage("Unable to extract filename from download (using default) due to exception: "+e.getMessage());
            }
            if (filename == null) filename = "download.pdf";
            setHeader(ResponseWrapper.CONTENT_DISPOSITION, "attachment; filename="+filename);
            type = APPLICATION_BINARY;
        }
        this.contentType = type;
        super.setContentType(type);
    }
    public String getFullCapturedContentType() {
        return this.contentType;
    }
    public String extractContentTypeUpperCased() {
        if (this.contentType == null) return null;
        String contentTypeUpperCased = this.contentType.trim().toUpperCase();
        // if the content-type contains a ";" character as in "text/html;charset=UTF-8" for example, simply take the first token before the ";"
        final int semicolon = contentTypeUpperCased.indexOf(";");
        if (semicolon > -1) {
            contentTypeUpperCased = contentTypeUpperCased.substring(0,semicolon);
        }
        return contentTypeUpperCased;
    }
    

    
    
    public String getFullCapturedContentDisposition() {
        return this.contentDisposition;
    }
    
    
    
    
    
    
    //1.5@Override
    public void setCharacterEncoding(final String encoding) {
        if (DEBUG) System.out.println("Response character encoding: "+encoding);
        if (this.blockResponseHeadersWithCRLF) checkHeaderAgainstCRLF(encoding);
        super.setCharacterEncoding(encoding);
    }
    
    //1.5@Override
    public void addCookie(final Cookie cookie) {
        if (this.blockResponseHeadersWithCRLF) {
            checkHeaderAgainstCRLF(cookie.getComment());
            checkHeaderAgainstCRLF(cookie.getDomain());
            checkHeaderAgainstCRLF(cookie.getName());
            checkHeaderAgainstCRLF(cookie.getPath());
            checkHeaderAgainstCRLF(cookie.getValue());
        }
        super.addCookie(cookie);
    }
    
    //1.5@Override
    public void addDateHeader(final String name, long date) {
        if (this.blockResponseHeadersWithCRLF) checkHeaderAgainstCRLF(name);
        if (this.blockFutureLastModifiedHeaders) checkHeaderAgainstFutureLastModified(name, date);
        super.addDateHeader(name, date);
    }
    //1.5@Override
    public void setDateHeader(final String name, long date) {
        if (this.blockResponseHeadersWithCRLF) checkHeaderAgainstCRLF(name);
        if (this.blockFutureLastModifiedHeaders) checkHeaderAgainstFutureLastModified(name, date);
        super.setDateHeader(name, date);
    }
    
    //1.5@Override
    public void addIntHeader(final String name, int value) {
        if (this.blockResponseHeadersWithCRLF) checkHeaderAgainstCRLF(name);
        super.addIntHeader(name, value);
    }
    //1.5@Override
    public void setIntHeader(final String name, int value) {
        if (this.blockResponseHeadersWithCRLF) checkHeaderAgainstCRLF(name);
        super.setIntHeader(name, value);
    }
    
    //1.5@Override
    public void addHeader(final String name, final String value) {
        if (DEBUG) System.out.println("addHeader: "+name+"="+value);
        if (this.blockResponseHeadersWithCRLF) {
            checkHeaderAgainstCRLF(name);
            checkHeaderAgainstCRLF(value);
        }
        checkHeaderAgainstFutureAndInvalidLastModified(name, value);
        if (CONTENT_DISPOSITION.equals(name)) this.contentDisposition = value;
        super.addHeader(name, value);
    }
    //1.5@Override
    public void setHeader(final String name, final String value) {
        if (DEBUG) System.out.println("setHeader: "+name+"="+value);
        if (this.blockResponseHeadersWithCRLF) {
            checkHeaderAgainstCRLF(name);
            checkHeaderAgainstCRLF(value);
        }
        checkHeaderAgainstFutureAndInvalidLastModified(name, value);
        if (CONTENT_DISPOSITION.equals(name)) this.contentDisposition = value;
        super.setHeader(name, value);
    }




    //1.5@Override
    public void sendRedirect(String location) throws IOException {
        if (DEBUG) System.out.println(" ===> sendRedirect: "+location);
        if (this.blockResponseHeadersWithCRLF) checkHeaderAgainstCRLF(location);
        // block redirects to foreign sites or applications
        if (this.blockNonLocalRedirects && !isLocalRedirect(location)) {
            final String message = "Non-local redirect detected: "+location+" (check your web app paths in the security configuration when the redirect is app internal)";
            handleAttack(message);
        }
        // count the overall number of redirects per second of this client and stop when threshold is reached
        if (this.attackHandler.isRedirectThresholdReached(this.ip)) {
            final String message = "Redirect per-client threshold ("+this.attackHandler.getRedirectThreshold()+" per reset period "+(this.attackHandler.getRedirectThresholdResetPeriod()/1000)+" seconds) reached: "+location;
            handleAttack(message);
        }
        // also inject the different types of tokens into the redirect-link, if it is a local redirect (and not a redirect to another site/app)
        // check if the current page is of relevant type and if the target page is also of relevant type
        if (DEBUG) System.out.println(" .... this.isCurrentRequestOfRelevantResourceType="+isCurrentRequestOfRelevantResourceType+" this.contentInjectionHelper.isMatchingIncomingLinkModificationExclusion="+this.contentInjectionHelper.isMatchingIncomingLinkModificationExclusion(location)+" isLocalRdirect="+isLocalRedirect(location));
        if (this.isCurrentRequestOfRelevantResourceType && !this.contentInjectionHelper.isMatchingIncomingLinkModificationExclusion(location) && isLocalRedirect(location)) {
            // if it is a redirect triggered by the anti spoofing mechanism of WebCastellum itself, don't append any protective content
            // ... so only append when it is *not* a WebCastellum internal redirect due to an recent attack
            if (!this.isRedirectingDueToRecentAttack) {
                location = injectSecretTokenIntoLink(location, false, true);
                location = injectParameterAndFormProtectionIntoLink(location, false, true);
                if (DEBUG) System.out.println(" ......> pre-encryption: "+location);
                // encryption is always the last step
                location = encryptQueryStringInLink(location, null); // null since we don't know here if GET or POST
                if (DEBUG) System.out.println(" ......> post-encryption: "+location);
                if (this.appendSessionIdToLinks) location = encodeURL(location);
            }
        }
        if (DEBUG) System.out.println(" ----> results in: "+location);
        super.sendRedirect(location);
    }
    
    
    private boolean isRedirectingDueToRecentAttack = false;
    void sendRedirectDueToRecentAttack(final String location) throws IOException {
        // count the overall number of redirects per second of this client and stop when threshold is reached
        if (this.attackHandler.isRedirectThresholdReached(this.ip)) {
            final String message = "Redirect per-client threshold ("+this.attackHandler.getRedirectThreshold()+" per reset period "+(this.attackHandler.getRedirectThresholdResetPeriod()/1000)+" seconds) reached: "+location;
            handleAttack(message);
        }
        // redirect
        this.isRedirectingDueToRecentAttack = true;
        sendRedirect(location);
        this.isRedirectingDueToRecentAttack = false;
    }
    
    
    
    
    
    //1.5@Override
    public String encodeURL(String url) {
        if (DEBUG) System.out.println(" =========> encodeURL: "+url);
        if (!this.isOptimizationHint) return super.encodeURL(url);
        url = super.encodeURL(url);
        // check if the current page is of relevant type and if the target page is also of relevant type
        final String urlDecoded = ServerUtils.decodeBrokenValueHtmlOnly(url, false);
        if (!ServerUtils.startsWithJavaScriptOrMailto(urlDecoded)) {
            url = urlDecoded;
            url = injectSecretTokenIntoLink(url, true, true); // also using "true" for isRedirect here since the &amp; escaping must be done at the HTML output level and not here (could be a redirect link here...)
            url = injectParameterAndFormProtectionIntoLink(url, true, true); // also using "true" for isRedirect here since the &amp; escaping must be done at the HTML output level and not here (could be a redirect link here...)
            url = ServerUtils.encodeHtmlSafe(url);
            // encryption is always the last step
            url = encryptQueryStringInLink(url, null); // null since we don't know here if GET or POST
        }
        return url;
    }
    //1.5@Override
    public String encodeUrl(final String url) {
        return this.encodeURL(url);
    }
    
    
 
/* already handled in sendRedirect() here
    @Override
    public String encodeRedirectURL(final String url) {
        String result = super.encodeRedirectURL(url);
        // check if the current page is of relevant type and if the target page is also of relevant type
        if ( this.isCurrentRequestOfRelevantResourceType && this.contentInjectionHelper.isRelevantResourceType(result) ) {
            result = injectHiddenFormFieldAndSelectBoxProtectionIntoLink(result);
            result = injectSecretTokenIntoLink(result);
            // encryption is always the last step
            result = encryptQueryStringInLink(result);
        }
        return result;
    }
    @Override
    public String encodeRedirectUrl(final String url) {
        String result = super.encodeRedirectUrl(url);
        // check if the current page is of relevant type and if the target page is also of relevant type
        if ( this.isCurrentRequestOfRelevantResourceType && this.contentInjectionHelper.isRelevantResourceType(result) ) {
            result = injectHiddenFormFieldAndSelectBoxProtectionIntoLink(result);
            result = injectSecretTokenIntoLink(result);
            // encryption is always the last step
            result = encryptQueryStringInLink(result);
        }
        return result;
    }
 */
    
    
    
    
    
    
    
    
    private void checkHeaderAgainstCRLF(final String value) {
        if (value != null) {
            if (this.matcherCRLF == null) this.matcherCRLF = PATTERN_CRLF.matcher(value);
            else this.matcherCRLF.reset(value);
            if (this.matcherCRLF.find()) {
                final String message = "CRLF detected in response header field";
                handleAttack(message);
            }
        }
    }
    
    private void checkHeaderAgainstFutureLastModified(final String name, final long date) {
        if (name != null && HEADER_LAST_MODIFIED.equalsIgnoreCase(name) && date > System.currentTimeMillis()+FUTURE_LAST_MODIFIED_TOLERANCE) {
            final String message = "Future Last-Modified response header detected: "+new Date(date);
            handleAttack(message);
        }
    }
    private void checkHeaderAgainstFutureAndInvalidLastModified(final String name, final String value) {
        if ((this.blockFutureLastModifiedHeaders || this.blockInvalidLastModifiedHeaders) 
                && name != null && value != null && value.length() > 0 && HEADER_LAST_MODIFIED.equalsIgnoreCase(name)) {
            final Date parsedDate = parseHttpDate(value);
            if (parsedDate == null) {
                if (this.blockInvalidLastModifiedHeaders) {
                    final String message = "Strange date format used for date header 'Last-Modified': "+value;
                    handleAttack(message);
                }
            } else if (this.blockFutureLastModifiedHeaders) checkHeaderAgainstFutureLastModified(name, parsedDate.getTime());
        }
    }
    
    private void handleAttack(final String message) {
        this.attackHandler.handleAttack(this.request, this.ip, message);
        // propagate form the response-wrapper back to filter chain
        throw new ServerAttackException(message);
    }
    
    
    
    
    
    private boolean isLocalRedirect(final String location) {
        if (location == null) return false;
        // OLD: if (location.toLowerCase().startsWith("http")) {
        if (ServerUtils.containsColonBeforeFirstSlashOrQuestionmark(location)) {
            // TODO: hier ggf. noch zusaetzlich den Context-Anteil der URL checken
            if (ServerUtils.isSameServer(location,this.requestedURL)) return true;
        } else {
            final char firstChar = location.charAt(0);
            // TODO: when it starts with "/" as an absolute adressing or with "../" it could be a switch to another context (i.e. another app) so that the context of the URL must be checked also, or?
            if (firstChar == '?' || firstChar == '.' || firstChar == '/' || Character.isLetter(firstChar) || Character.isDigit(firstChar)) return true; // OLD: if (!location.startsWith("/")) return true;
            if (location.startsWith(this.contextPath)) return true;
        }
        return false;
    }

    
    
    
    
    // not as static method, since SimpleDateFormat is not thread-safe: therefore we use new SimpleDateFormat's for every response wrapper
    private Date parseHttpDate(final String value) {
        if (value == null || value.length() == 0) return null;
        Date result = null;
        for (SimpleDateFormat HTTP_DATE_FORMATS1 : HTTP_DATE_FORMATS) {
            try {
                result = HTTP_DATE_FORMATS1.parse(value);
            }catch (ParseException ignored) {}catch (RuntimeException ignored) {}
        }
        return result;
    }
    
    
    
    
    
    private /*synchronized*/ String injectSecretTokenIntoLink(String result, final boolean urlAlreadyDecodedAndDoesNotNeedToBeEncodedAndStartsWithCheckAlreadyDone, final boolean isRedirect) {
        if (result == null) return null;
        this.secretTokensApplied = true;
        if (this.contentInjectionHelper.isInjectSecretTokenIntoLinks()) {
            // since encryption hides the tokens (but happens after the tokens) we can safely assume that the tokens are already injected when the link is encrypted
            // therefore only inject tokens when encryption is not active or has not yet already taken place
            if (!this.contentInjectionHelper.isEncryptQueryStringInLinks() || !ResponseUtils.isAlreadyEncrypted(this.cryptoDetectionString,result)) { // = only inject tokens when either encryption is disabled or has not taken place, when for example response.encodeURL already caught that URL
                result = ResponseUtils.injectParameterIntoURL(result, this.tokenKey, this.tokenValue, this.maskAmpersandsInModifiedLinks&&!isRedirect, this.appendQuestionmarkOrAmpersandToLinks, urlAlreadyDecodedAndDoesNotNeedToBeEncodedAndStartsWithCheckAlreadyDone);
            }
        }
        return result;
    }
    

    
    
    /**
     * for link only
     */
    private /*synchronized*/ String injectParameterAndFormProtectionIntoLink(String result, final boolean urlAlreadyDecodedAndDoesNotNeedToBeEncodedAndStartsWithCheckAlreadyDone, final boolean isRedirect) {
        if (result == null) return null;
        this.parameterAndFormTokensApplied = true;
        if (this.contentInjectionHelper.isProtectParametersAndForms() // so this.contentInjectionHelper.isEncryptQueryStringInLinks() is automatically true
                && !this.contentInjectionHelper.isExtraStrictParameterCheckingForEncryptedLinks()) { // as this method here is called only for links (not for forms) we can apply the strictness check here
            // since encryption hides the tokens (but happens after the tokens) we can safely assume that the tokens are already injected when the link is encrypted
            // therefore only inject tokens when encryption is not active or has not yet already taken place
            if (!ResponseUtils.isAlreadyEncrypted(this.cryptoDetectionString,result)) { // = only inject tokens when either encryption is disabled or has not taken place, when for example response.encodeURL already caught that URL
                final HttpSession session = this.request.getSession(false);
                if (session != null) {
                    final String parameterAndFormProtectionValue = ResponseUtils.getKeyForParameterProtectionOnly(result, session, this.hiddenFormFieldProtection, this.reuseSessionContent, this.applySetAfterWrite); // since a redirect has only parameters (eventually) and definitifely no form values
                    if (parameterAndFormProtectionValue != null) {
                        result = ResponseUtils.injectParameterIntoURL(result, this.parameterAndFormProtectionKeyKey, parameterAndFormProtectionValue, this.maskAmpersandsInModifiedLinks&&!isRedirect, this.appendQuestionmarkOrAmpersandToLinks, urlAlreadyDecodedAndDoesNotNeedToBeEncodedAndStartsWithCheckAlreadyDone);
                    }
                } else System.err.println("Strange situation: session does not exist where expected: injectParameterAndFormProtectionIntoLink()");
            }
        }
        return result;
    }
    /*
     * for link togther with form only
     *
    /*synchronized* / String injectParameterAndFormProtectionIntoLink(String result, final ParameterAndFormProtection pafp) {
        if (result == null || pafp == null) return null;
        this.parameterAndFormTokensApplied = true;
        if (this.contentInjectionHelper.isProtectParametersAndForms()) { // so this.contentInjectionHelper.isEncryptQueryStringInLinks() is autmatically true
            final HttpSession session = this.request.getSession(false);
            if (session != null) {
                final String parameterAndFormProtectionValue = ResponseUtils.getKeyForParameterAndFormProtection(result, pafp, session, this.reuseSessionContent);
                if (parameterAndFormProtectionValue != null && !ResponseUtils.isAlreadyEncrypted(this.cryptoDetectionString,result)) { // = only inject tokens when either encryption is disabled or has not taken place, when for example response.encodeURL already caught that URL
                    result = ResponseUtils.injectParameterIntoURL(result, this.parameterAndFormProtectionKeyKey, parameterAndFormProtectionValue, this.maskAmpersandsInModifiedLinks, this.appendQuestionmarkOrAmpersandToLinks);
                }
            } else System.err.println("Strange situation: session does not exist where expected: injectParameterAndFormProtectionIntoLink()");
        }
        return result;
    }*/
    
    
    private /*synchronized*/ String encryptQueryStringInLink(String result, final Boolean isRequestMethodPOST) {
        if (result == null) return null;
        this.cryptoApplied = true;
        if (this.contentInjectionHelper.isEncryptQueryStringInLinks() 
                && !ResponseUtils.isAlreadyEncrypted(this.cryptoDetectionString,result)
                && ServerUtils.isInternalHostURL(this.requestedURL,ServerUtils.decodeBrokenValueHtmlOnly(result,false))) 
            result = ResponseUtils.encryptQueryStringInURL(this.requestedURL, this.contextPath, this.servletPath, result, false, false, isRequestMethodPOST, this.contentInjectionHelper.isSupposedToBeStaticResource(ResponseUtils.extractURI(result)), this.cryptoDetectionString, this.cipher, this.cryptoKey, useFullPathForResourceToBeAccessedProtection, this.additionalFullResourceRemoval, this.additionalMediumResourceRemoval, this, this.appendQuestionmarkOrAmpersandToLinks);
        return result;
    }

    

    
    
}
