package org.webcastellum;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public final class ResponseProtectionPerformanceTester {

    private static final int DEFAULT_EXECUTION_COUNT = 100;
    private final File htmlFile,  outputFile;

    public ResponseProtectionPerformanceTester(final File htmlFile) {
        this.htmlFile = htmlFile;
        this.outputFile = new File(htmlFile.getAbsolutePath() + ".out");
    }

    public void execute(final int executionCount) throws NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, RuleLoadingException {
        System.out.println("Source file: " + this.htmlFile.getAbsoluteFile());
        System.out.println("Target file: " + this.outputFile.getAbsoluteFile());
        long durationWithout, durationWith;
        for (int i=0; i<5; i++) {
            durationWithout = test(false, executionCount);
            durationWith = test(true, executionCount);
            System.out.println("Delta time: " + (durationWith - durationWithout) + " ms");
        }
    }

    private ResponseModificationDefinition[] downCast(RequestDefinition[] definitions) {
        final ResponseModificationDefinition[] results = new ResponseModificationDefinition[definitions.length];
        for (int i = 0; i < definitions.length; i++) {
            results[i] = (ResponseModificationDefinition) definitions[i];
        }
        return results;
    }

    private final long test(final boolean applyFilter, final int executionCount) throws NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, RuleLoadingException {
        final boolean stripHtmlEnabled = true;
        final boolean injectSecretTokensEnabled = true;
        final boolean encryptQueryStringsEnabled = true;
        final boolean protectParamsAndFormsEnabled = true;
        final boolean applyExtraProtectionForDisabledFormFields = true;
        final boolean applyExtraProtectionForReadonlyFormFields = false;
        final boolean applyExtraProtectionForRequestParamValueCount = false;

        final ContentInjectionHelper helper = new ContentInjectionHelper();
        final RuleFileLoader ruleFileLoaderModificationExcludes = new ClasspathZipRuleFileLoader();
        ruleFileLoaderModificationExcludes.setPath(WebCastellumFilter.MODIFICATION_EXCLUDES_DEFAULT);
        final ContentModificationExcludeDefinitionContainer containerModExcludes = new ContentModificationExcludeDefinitionContainer(ruleFileLoaderModificationExcludes);
        containerModExcludes.parseDefinitions();
        helper.setContentModificationExcludeDefinitions(containerModExcludes);
        
        final AttackHandler attackHandler = new AttackHandler(null, 123, 600000, 100000, 300000, 300000, null, "MOCK",
                false, false, 
                0, false, false,
                Pattern.compile("sjghggfakgfjagfgajgfjasgfs"), Pattern.compile("sjghggfakgfjagfgajgfjasgfs"), true);
        final SessionCreationTracker sessionCreationTracker = new SessionCreationTracker(attackHandler, 0, 600000, 300000, 0, "", "", "", "");
        final RequestWrapper request = new RequestWrapper(new RequestMock(), helper, sessionCreationTracker, "123.456.789.000", false, true, true);

        final RuleFileLoader ruleFileLoaderResponseModifications = new ClasspathZipRuleFileLoader();
        ruleFileLoaderResponseModifications.setPath(WebCastellumFilter.RESPONSE_MODIFICATIONS_DEFAULT);
        final ResponseModificationDefinitionContainer container = new ResponseModificationDefinitionContainer(ruleFileLoaderResponseModifications);
        container.parseDefinitions();




        // fetch the response-modification matchers
        final ResponseModificationDefinition[] responseModificationDefinitions = downCast(container.getAllEnabledRequestDefinitions());
        final List/*<Pattern>*/ tmpPatternsToExcludeCompleteTag = new ArrayList(responseModificationDefinitions.length);
        final List/*<Pattern>*/ tmpPatternsToExcludeCompleteScript = new ArrayList(responseModificationDefinitions.length);
        final List/*<Pattern>*/ tmpPatternsToExcludeLinksWithinScripts = new ArrayList(responseModificationDefinitions.length);
        final List/*<Pattern>*/ tmpPatternsToExcludeLinksWithinTags = new ArrayList(responseModificationDefinitions.length);
        final List/*<Pattern>*/ tmpPatternsToCaptureLinksWithinScripts = new ArrayList(responseModificationDefinitions.length);
        final List/*<Pattern>*/ tmpPatternsToCaptureLinksWithinTags = new ArrayList(responseModificationDefinitions.length);
        final List/*<WordDictionary>*/ tmpPrefiltersToExcludeCompleteTag = new ArrayList(responseModificationDefinitions.length);
        final List/*<WordDictionary>*/ tmpPrefiltersToExcludeCompleteScript = new ArrayList(responseModificationDefinitions.length);
        final List/*<WordDictionary>*/ tmpPrefiltersToExcludeLinksWithinScripts = new ArrayList(responseModificationDefinitions.length);
        final List/*<WordDictionary>*/ tmpPrefiltersToExcludeLinksWithinTags = new ArrayList(responseModificationDefinitions.length);
        final List/*<WordDictionary>*/ tmpPrefiltersToCaptureLinksWithinScripts = new ArrayList(responseModificationDefinitions.length);
        final List/*<WordDictionary>*/ tmpPrefiltersToCaptureLinksWithinTags = new ArrayList(responseModificationDefinitions.length);
        final List/*<Integer[]>*/ tmpGroupNumbersToCaptureLinksWithinScripts = new ArrayList(responseModificationDefinitions.length);
        final List/*<Integer[]>*/ tmpGroupNumbersToCaptureLinksWithinTags = new ArrayList(responseModificationDefinitions.length);
        //final List<List<String>> tmpTagNames = new ArrayList(responseModificationDefinitions.length);
        for (ResponseModificationDefinition responseModificationDefinition : responseModificationDefinitions) {
            if (responseModificationDefinition.isMatchesScripts()) {
                tmpPatternsToExcludeCompleteScript.add( responseModificationDefinition.getScriptExclusionPattern() );
                tmpPrefiltersToExcludeCompleteScript.add( responseModificationDefinition.getScriptExclusionPrefilter() );
                tmpPatternsToExcludeLinksWithinScripts.add( responseModificationDefinition.getUrlExclusionPattern() );
                tmpPrefiltersToExcludeLinksWithinScripts.add( responseModificationDefinition.getUrlExclusionPrefilter() );
                tmpPatternsToCaptureLinksWithinScripts.add( responseModificationDefinition.getUrlCapturingPattern() );
                tmpPrefiltersToCaptureLinksWithinScripts.add( responseModificationDefinition.getUrlCapturingPrefilter() );
                tmpGroupNumbersToCaptureLinksWithinScripts.add(ServerUtils.convertSimpleToObjectArray(responseModificationDefinition.getCapturingGroupNumbers()));
            }
            if (responseModificationDefinition.isMatchesTags()) {
                tmpPatternsToExcludeCompleteTag.add( responseModificationDefinition.getTagExclusionPattern() );
                tmpPrefiltersToExcludeCompleteTag.add( responseModificationDefinition.getTagExclusionPrefilter() );
                tmpPatternsToExcludeLinksWithinTags.add( responseModificationDefinition.getUrlExclusionPattern() );
                tmpPrefiltersToExcludeLinksWithinTags.add( responseModificationDefinition.getUrlExclusionPrefilter() );
                tmpPatternsToCaptureLinksWithinTags.add( responseModificationDefinition.getUrlCapturingPattern() );
                tmpPrefiltersToCaptureLinksWithinTags.add( responseModificationDefinition.getUrlCapturingPrefilter() );
                tmpGroupNumbersToCaptureLinksWithinTags.add(ServerUtils.convertSimpleToObjectArray(responseModificationDefinition.getCapturingGroupNumbers()));
                //tmpTagNames.add(responseModificationDefinition.getTagNames());
            }
        }
        // convert lists of Patterns to arrays of Matchers
        final Matcher[] matchersToExcludeCompleteTag = ServerUtils.convertListOfPatternToArrayOfMatcher(tmpPatternsToExcludeCompleteTag);
        final Matcher[] matchersToExcludeCompleteScript = ServerUtils.convertListOfPatternToArrayOfMatcher(tmpPatternsToExcludeCompleteScript);
        final Matcher[] matchersToExcludeLinksWithinScripts = ServerUtils.convertListOfPatternToArrayOfMatcher(tmpPatternsToExcludeLinksWithinScripts);
        final Matcher[] matchersToExcludeLinksWithinTags = ServerUtils.convertListOfPatternToArrayOfMatcher(tmpPatternsToExcludeLinksWithinTags);
        final Matcher[] matchersToCaptureLinksWithinScripts = ServerUtils.convertListOfPatternToArrayOfMatcher(tmpPatternsToCaptureLinksWithinScripts);
        final Matcher[] matchersToCaptureLinksWithinTags = ServerUtils.convertListOfPatternToArrayOfMatcher(tmpPatternsToCaptureLinksWithinTags);
        final WordDictionary[] prefiltersToExcludeCompleteTag = (WordDictionary[]) tmpPrefiltersToExcludeCompleteTag.toArray(new WordDictionary[0]);
        final WordDictionary[] prefiltersToExcludeCompleteScript = (WordDictionary[]) tmpPrefiltersToExcludeCompleteScript.toArray(new WordDictionary[0]);
        final WordDictionary[] prefiltersToExcludeLinksWithinScripts = (WordDictionary[]) tmpPrefiltersToExcludeLinksWithinScripts.toArray(new WordDictionary[0]);
        final WordDictionary[] prefiltersToExcludeLinksWithinTags = (WordDictionary[]) tmpPrefiltersToExcludeLinksWithinTags.toArray(new WordDictionary[0]);
        final WordDictionary[] prefiltersToCaptureLinksWithinScripts = (WordDictionary[]) tmpPrefiltersToCaptureLinksWithinScripts.toArray(new WordDictionary[0]);
        final WordDictionary[] prefiltersToCaptureLinksWithinTags = (WordDictionary[]) tmpPrefiltersToCaptureLinksWithinTags.toArray(new WordDictionary[0]);
        final int[][] groupNumbersToCaptureLinksWithinScripts = ServerUtils.convertArrayIntegerListTo2DimIntArray(tmpGroupNumbersToCaptureLinksWithinScripts);
        final int[][] groupNumbersToCaptureLinksWithinTags = ServerUtils.convertArrayIntegerListTo2DimIntArray(tmpGroupNumbersToCaptureLinksWithinTags);
        //final List<String>[] tagNamesToCheck = tmpTagNames.toArray(new List[0]);




        // warm up
        final Cipher cipher = CryptoUtils.getCipher();
        final CryptoKeyAndSalt key = CryptoUtils.generateRandomCryptoKeyAndSalt(false);
        Cipher.getInstance("AES");
        MessageDigest.getInstance("SHA-1");

        final ResponseWrapper response = new ResponseWrapper(new ResponseMock(), request, attackHandler, helper, false, "___ENCRYPTED___", cipher, key, "___SEC-KEY___", "___SEC-VALUE___", "___PROT-KEY___",
                false, false, false, false, "123.456.789.000", new HashSet(),
                prefiltersToExcludeCompleteScript, matchersToExcludeCompleteScript, 
                prefiltersToExcludeCompleteTag, matchersToExcludeCompleteTag, 
                prefiltersToExcludeLinksWithinScripts, matchersToExcludeLinksWithinScripts, 
                prefiltersToExcludeLinksWithinTags, matchersToExcludeLinksWithinTags,
                prefiltersToCaptureLinksWithinScripts, matchersToCaptureLinksWithinScripts, 
                prefiltersToCaptureLinksWithinTags, matchersToCaptureLinksWithinTags,
                groupNumbersToCaptureLinksWithinScripts, groupNumbersToCaptureLinksWithinTags,
                //tagNamesToCheck,
                true, false, true, true, true, true, true, true, true, true, true, false, false, true, "", "", (short) 3, true, false, false);


        final List/*<Long>*/ durations = new ArrayList();
        for (int i = 0; i < executionCount; i++) {
            final long start = System.currentTimeMillis();
            Reader reader = null;
            Writer writer = null;
            try {
                reader = new BufferedReader(new FileReader(this.htmlFile));
                writer = new FileWriter(this.outputFile);
                if (applyFilter) {
                    writer = new ResponseFilterWriter(writer,true,
                            "http://127.0.0.1/test/sample", "/test", "/test", "___SEC-KEY___", "___SEC-VALUE___", "___PROT-KEY___", cipher, key,
                            helper, "___ENCRYPTED___", request, response,
                            stripHtmlEnabled, injectSecretTokensEnabled, protectParamsAndFormsEnabled, encryptQueryStringsEnabled,
                            applyExtraProtectionForDisabledFormFields, applyExtraProtectionForReadonlyFormFields, applyExtraProtectionForRequestParamValueCount,
                            prefiltersToExcludeCompleteScript, matchersToExcludeCompleteScript, 
                            prefiltersToExcludeCompleteTag, matchersToExcludeCompleteTag, 
                            prefiltersToExcludeLinksWithinScripts, matchersToExcludeLinksWithinScripts, 
                            prefiltersToExcludeLinksWithinTags, matchersToExcludeLinksWithinTags,
                            prefiltersToCaptureLinksWithinScripts, matchersToCaptureLinksWithinScripts, 
                            prefiltersToCaptureLinksWithinTags, matchersToCaptureLinksWithinTags,
                            groupNumbersToCaptureLinksWithinScripts, groupNumbersToCaptureLinksWithinTags, 
                            //tagNamesToCheck,
                            true, true, false, true, true, true, true, true, true, true, true, false, false, true,
                            "", "", (short) 3, true, false);
                    writer = new BufferedWriter(writer);
                }
                char[] chars = new char[16 * 1024];
                int read;
                while ((read = reader.read(chars)) != -1) {
                    if (read > 0) {
                        writer.write(chars, 0, read);
                    }
                }
                durations.add(new Long(System.currentTimeMillis() - start));
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ignored) {
                    }
                }
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        // calc average
        long sum = 0;
        for (final Iterator iter = durations.iterator(); iter.hasNext();) {
            Long value = (Long) iter.next();
            sum += value.longValue();
        }
        //if (applyFilter) System.out.println("Excution count for this loop is "+executionCount+" and the cumulated invocation count for the cryptodetection method is here: "+ResponseUtils.TUNING_COUNTER_FOR_DEBUGGING);
        return sum / durations.size();
    }

    public static final void main(String[] args) throws Exception {
        final String filename;
        if (args.length < 1) {
            System.out.println(Version.tagLine());
            System.err.println("This tool tests the performance of HTML parsing with all security features enabled");
            System.err.println("Please provide the following argument: filenameHtmlFile");
            System.err.println("USING DEFAULT");
            filename = "../../../Temp/input.html";
        } else {
            filename = args[0];
        }
        final File htmlFile = new File(filename);
        final ResponseProtectionPerformanceTester tester = new ResponseProtectionPerformanceTester(htmlFile);
        int executionCount;
        if (args.length > 1) {
            executionCount = Integer.parseInt(args[1]);
        } else {
            executionCount = DEFAULT_EXECUTION_COUNT;
        }
        if (executionCount <= 0) {
            executionCount = DEFAULT_EXECUTION_COUNT;
        }
        tester.execute(executionCount);
    }

    public static final class RequestMock implements HttpServletRequest {

        private HttpSession session = new SessionMock();

        public String getAuthType() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Cookie[] getCookies() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public long getDateHeader(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getHeader(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Enumeration getHeaders(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Enumeration getHeaderNames() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getIntHeader(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getMethod() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getPathInfo() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getPathTranslated() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getContextPath() {
            return "/test";
        }

        public String getQueryString() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getRemoteUser() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean isUserInRole(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Principal getUserPrincipal() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getRequestedSessionId() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getRequestURI() {
            return "/test";
        }

        public StringBuffer getRequestURL() {
            return new StringBuffer("/test");
        }

        public String getServletPath() {
            return "/test";
        }

        public HttpSession getSession(boolean arg0) {
            return this.session;
        }

        public HttpSession getSession() {
            return this.session;
        }

        public boolean isRequestedSessionIdValid() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean isRequestedSessionIdFromCookie() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean isRequestedSessionIdFromURL() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean isRequestedSessionIdFromUrl() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Object getAttribute(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Enumeration getAttributeNames() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getCharacterEncoding() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getContentLength() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getContentType() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public ServletInputStream getInputStream() throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getParameter(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Enumeration getParameterNames() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String[] getParameterValues(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Map getParameterMap() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getProtocol() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getScheme() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getServerName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getServerPort() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public BufferedReader getReader() throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getRemoteAddr() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getRemoteHost() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setAttribute(String arg0, Object arg1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void removeAttribute(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Locale getLocale() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Enumeration getLocales() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean isSecure() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public RequestDispatcher getRequestDispatcher(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getRealPath(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getRemotePort() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getLocalName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getLocalAddr() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getLocalPort() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public static final class ResponseMock implements HttpServletResponse {

        public void addCookie(Cookie arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean containsHeader(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String encodeURL(String arg0) {
            return arg0;
        }

        public String encodeRedirectURL(String arg0) {
            return arg0;
        }

        public String encodeUrl(String arg0) {
            return arg0;
        }

        public String encodeRedirectUrl(String arg0) {
            return arg0;
        }

        public void sendError(int arg0, String arg1) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void sendError(int arg0) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void sendRedirect(String arg0) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setDateHeader(String arg0, long arg1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void addDateHeader(String arg0, long arg1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setHeader(String arg0, String arg1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void addHeader(String arg0, String arg1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setIntHeader(String arg0, int arg1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void addIntHeader(String arg0, int arg1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setStatus(int arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setStatus(int arg0, String arg1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getCharacterEncoding() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getContentType() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public ServletOutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public PrintWriter getWriter() throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setCharacterEncoding(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setContentLength(int arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setContentType(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setBufferSize(int arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getBufferSize() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void flushBuffer() throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void resetBuffer() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean isCommitted() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void reset() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setLocale(Locale arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Locale getLocale() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public static final class SessionMock implements HttpSession {

        private final Map map = new HashMap();

        public long getCreationTime() {
            return -1;
        }

        public String getId() {
            return "X";
        }

        public long getLastAccessedTime() {
            return -1;
        }

        public ServletContext getServletContext() {
            return null;
        }

        public void setMaxInactiveInterval(int i) {
        }

        public int getMaxInactiveInterval() {
            return -1;
        }

        public javax.servlet.http.HttpSessionContext getSessionContext() {
            return null;
        }

        public void setAttribute(String string, Object object) {
            this.map.put(string, object);
        }

        public void removeAttribute(String string) {
            this.map.remove(string);
        }

        public Object getAttribute(String string) {
            return this.map.get(string);
        }

        public Enumeration getAttributeNames() {
            return new Vector(this.map.keySet()).elements();
        }

        public void invalidate() {
            this.map.clear();
        }

        public Object getValue(String string) {
            return this.map.get(string);
        }

        public String[] getValueNames() {
            return (String[]) this.map.keySet().toArray(new String[0]);
        }

        public void putValue(String string, Object object) {
            this.map.put(string, object);
        }

        public void removeValue(String string) {
            this.map.remove(string);
        }

        public boolean isNew() {
            return false;
        }
    }
}
