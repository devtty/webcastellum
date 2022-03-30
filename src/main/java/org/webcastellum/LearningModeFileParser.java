package org.webcastellum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public final class LearningModeFileParser {

    private static final boolean DEBUG = false;
    
    private static final int DOTS = 50;
  
    // TODO: konfigurierbar machen
    private static final Pattern PATTERN_IGNORE_LIST = Pattern.compile("(?i)\\.(gif|jpe?g|ico|swf|svg|zip|t?gz|tar|xls|doc|ppt|pdf|csv|png|bmp|xsd|dtd|xml|css|js|htc|html?|class|jar|txt)\\z");
    
    
    private static final String START_OF_ENTRY = "Regular request (learning mode)";
    private static final String START_OF_SERVLET_PATH = "servletPath = ";
    private static final String START_OF_METHOD = "method = ";
    private static final String START_OF_MIMETYPE = "mimeType = ";
    private static final String START_OF_CONTENTLENGTH = "contentLength = ";
    private static final String START_OF_REFERRER = "referer = "; // YES, the word must be spelled "referer" (i.e. misspelled) according to RFC
    private static final String START_OF_ENCODING = "encoding = ";
    private static final String START_OF_REQUEST_PARAM = "requestParam: ";
            
            
    
    // TODO: spaeter hier noch hinzufuegen, dass wenn keine files per argumentlist uebergeben wurden einfach der inhalt aus stdin als input genommen wird... im sinne von        cat xyz.* | java -cp .......
    public static final void main(final String[] args) {
        final List/*<File>*/ files = new ArrayList();
        for (int i=0; i<args.length; i++) {
            final File file = new File(args[i]);
            if (!file.exists() || !file.canRead()) {
                System.err.println("Ignoring non-existing or non-readable file: "+file.getAbsolutePath());
                continue;
            }
            if (file.isFile()) files.add(file);
            else if (file.isDirectory()) {
                final File[] filesInDir = file.listFiles();
                for (int j=0; j<filesInDir.length; j++) {
                    if (filesInDir[j].isFile()) files.add(filesInDir[j]);
                }
            }
        }
        LearningModeFileParser parser = new LearningModeFileParser(files);
        parser.parse();
        final Map/*<String,LearnedRequst>*/ learnedRequests = parser.getLearnedRequests();
        if (DEBUG) System.out.println(learnedRequests.values());
    }

    
    
    private final List/*<File>*/ files;
    private final List/*<LearningModeTrackedRequestEntry>*/ requestEntries = new ArrayList();
    private final Map/*<String,LearnedRequst>*/ learnedRequests = new HashMap();
    
    public LearningModeFileParser(final List/*<File>*/ files) {
        if (files == null) throw new NullPointerException("files must not be null");
        this.files = new ArrayList(files);
    }

    
    public Map getLearnedRequests() {
        return new HashMap(learnedRequests);
    }
    
    
    
    
    
    public void parse() {
        for (final Iterator iter = this.files.iterator(); iter.hasNext();) {
            final File file = (File) iter.next();
            workOnFile(file);
        }
        // correcting cross-check the resulting regexps and allow "all characters" for those that don't match (due to special localized chars for example)
        for (final Iterator iter = this.requestEntries.iterator(); iter.hasNext();) {
            final LearningModeTrackedRequestEntry requestEntry = (LearningModeTrackedRequestEntry) iter.next();
            crossCheckRulesAgainstRequestEntry(requestEntry, false);
        }
        // final NON-CORRECTING cross-check phase that warns instead of adjusts
        for (final Iterator iter = this.requestEntries.iterator(); iter.hasNext();) {
            final LearningModeTrackedRequestEntry requestEntry = (LearningModeTrackedRequestEntry) iter.next();
            crossCheckRulesAgainstRequestEntry(requestEntry, true);
        }
    }


    
    
    private void crossCheckRulesAgainstRequestEntry(final LearningModeTrackedRequestEntry requestEntry, final boolean warnInsteadOfAdjustment) {
        final String servletPath = requestEntry.getServletPath();
        if (servletPath == null || PATTERN_IGNORE_LIST.matcher(requestEntry.getServletPath()).find()) return;
        if (DEBUG) System.out.println("Cross-checking all rules against: "+requestEntry);
        for (final Iterator iter = this.learnedRequests.values().iterator(); iter.hasNext();) {
            final String method = requestEntry.getMethod();
            final LearnedRequest learnedRequest = (LearnedRequest) iter.next();
            final Pattern servletPathPattern = learnedRequest.createServletPathPattern();
            final Pattern methodPattern = learnedRequest.createMethodPattern();
            if (servletPathPattern.matcher(servletPath).find()
                && methodPattern.matcher(method).find()) {
                boolean mismatch = false;
                if (DEBUG) System.out.println("Servlet path and method matches for check: "+method+" "+servletPath);
                for (final Iterator params = requestEntry.getParameters().entrySet().iterator(); params.hasNext();) {
                    final Map.Entry/*<String,List<String>>*/ parameter = (Entry) params.next();
                    final String parameterName = (String) parameter.getKey();
                    final ParameterFeature feature = learnedRequest.getParameterFeature(parameterName);
                    if (feature != null) { // now cross-check each value against the learned parameter feature
                        final List/*<String>*/ parameterValues = (List) parameter.getValue();
                        if (parameterValues.size() > 1 && !feature.isHavingMultipleValues()) {
                            mismatch = true;
                            if (warnInsteadOfAdjustment) {
                                System.err.println("Rule mismatch (even after first correction cross-check): "+parameterName);
                            } else {
                                // adjust rule according to found mismatch
                                feature.setHavingMultipleValues(true);
                                System.out.println("Adjusted rule "+learnedRequest+" by setting multi-valued flag to true for parameter "+parameterName);
                            }
                        }
                        final Pattern parameterValuePattern = Pattern.compile( LearnedRequest.createRegularExpressionValue(feature) );
                        for (final Iterator values = parameterValues.iterator(); values.hasNext();) {
                            final String value = (String) values.next();
                            if (!parameterValuePattern.matcher(value).find()) {
                                mismatch = true;
                                if (warnInsteadOfAdjustment) {
                                    System.err.println("Rule mismatch (even after first correction cross-check): "+parameterName);
                                } else {
                                    // adjust rule according to found mismatch
                                    learnedRequest.removeParameterFeature(parameterName);
                                    System.out.println("Adjusted rule "+learnedRequest+" by removing check for parameter "+parameterName);
                                }
                                break;
                            }
                        }
                    }
                }
                if (DEBUG && mismatch) System.out.println("----------- RULE ADJUSTED DUE TO MISMATCH -----------");
            }
        }
    }
    
    
    

    

    private void workOnEntry(final LearningModeTrackedRequestEntry requestEntry) {
        if (requestEntry.getServletPath() == null || PATTERN_IGNORE_LIST.matcher(requestEntry.getServletPath()).find()) return;
        if (DEBUG) System.out.println("Learning from: "+requestEntry);
        final String key = requestEntry.getServletPath()+"_"+requestEntry.getMethod();
        LearnedRequest learnedRequest = (LearnedRequest) this.learnedRequests.get(key);
        if (learnedRequest == null) {
            learnedRequest = new LearnedRequest(requestEntry.getServletPath(), requestEntry.getMethod());
            this.learnedRequests.put(key, learnedRequest);
        }
        for (final Iterator/*<String,List<String>>*/ iter = requestEntry.getParameters().entrySet().iterator(); iter.hasNext();) {
            final Map.Entry entry = (Map.Entry) iter.next();
            learnedRequest.addParameterOccurence((String)entry.getKey(), (List/*<String>*/)entry.getValue());
        }
    }
    
    private void workOnFile(final File file) {
        System.out.println("Working on file: "+file.getAbsolutePath());
        BufferedReader reader = null;
        try {
            reader = new BufferedReader( new FileReader(file) );
            String line; int i=0; // reading and index stuff
            LearningModeTrackedRequestEntry requestEntry = null;
            while ((line=reader.readLine()) != null) {
                if (line.indexOf(START_OF_ENTRY) >= 0) {
                    // work on just finished entry
                    if (requestEntry != null) workOnEntry(requestEntry);
                    // counting stuff
                    System.out.print(".");
                    if (++i % DOTS == 0) {
                        System.out.println(i);
                    }
                    // start new entry
                    requestEntry = new LearningModeTrackedRequestEntry();
                    this.requestEntries.add(requestEntry);
                    // extract WC version
                    final String[] splitted = line.split(" ");
                    requestEntry.setVersion( splitted[ splitted.length-2 ] );
                } else {
                    if (requestEntry == null) {
                        if (i>0) System.err.println("Truncated request entry ("+i+") in file: "+file.getAbsolutePath());
                    } else {
                        line = ServerUtils.removeLeadingWhitespace(line);
                        // extract values
                        if (line.startsWith(START_OF_SERVLET_PATH)) {
                            requestEntry.setServletPath( ServerUtils.urlDecode( line.substring(START_OF_SERVLET_PATH.length()))  );
                        } else if (line.startsWith(START_OF_METHOD)) {
                            requestEntry.setMethod( ServerUtils.urlDecode( line.substring(START_OF_METHOD.length()))  );
                        } else if (line.startsWith(START_OF_MIMETYPE)) {
                            requestEntry.setMimeType( ServerUtils.urlDecode( line.substring(START_OF_MIMETYPE.length()))  );
                        } else if (line.startsWith(START_OF_CONTENTLENGTH)) {
                            requestEntry.setContentLength( ServerUtils.urlDecode( line.substring(START_OF_CONTENTLENGTH.length()))  );
                        } else if (line.startsWith(START_OF_REFERRER)) {
                            requestEntry.setReferrer( ServerUtils.urlDecode( line.substring(START_OF_REFERRER.length()))  );
                        } else if (line.startsWith(START_OF_ENCODING)) {
                            requestEntry.setEncoding( ServerUtils.urlDecode( line.substring(START_OF_ENCODING.length()))  );
                        } else if (line.startsWith(START_OF_REQUEST_PARAM)) {
                            line = line.substring(START_OF_REQUEST_PARAM.length());
                            final int firstEqualPos = line.indexOf(" = ");
                            if (firstEqualPos >= 0) {
                                final String paramName = line.substring(0, firstEqualPos);
                                final String paramValue = line.substring(firstEqualPos+3);
                                requestEntry.addParameter( ServerUtils.urlDecode(paramName) , ServerUtils.urlDecode(paramValue) );
                            }
                        }
                    }
                }
            }
            // work on just finished entry
            if (requestEntry != null) workOnEntry(requestEntry);
            if (i % DOTS != 0) System.out.println(i);
            System.out.println();
        } catch (RuntimeException e) {
            System.err.println("Caught RuntimeException: "+e.getMessage());
            if (DEBUG) e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Caught IOException: "+e.getMessage());
            if (DEBUG) e.printStackTrace();
        } finally {
            if (reader != null) try { reader.close(); } catch (IOException ignored) {}
        }
    }
    
    
    
    
    

    
}
