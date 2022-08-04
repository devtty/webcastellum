package org.webcastellum;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.servlet.http.HttpServletRequest;

public abstract class RequestDefinitionContainer/*<T extends RequestDefinition>*/ extends AbstractDefinitionContainer {

    public static final String FORMAT_TIME = "yyyyMMddHHmmss";
    public static final String FORMAT_TIME_YEAR = "yyyy";
    public static final String FORMAT_TIME_MONTH = "MM";
    public static final String FORMAT_TIME_DAY = "dd";
    public static final String FORMAT_TIME_HOUR = "HH";
    public static final String FORMAT_TIME_MINUTE = "mm";
    public static final String FORMAT_TIME_SECOND = "ss";
    public static final String FORMAT_TIME_WEEKDAY = "EEEE";

    protected static final String KEY_CUSTOM_REQUEST_MATCHER = "customRequestMatcher";

    protected static final String KEY_NEGATION = "negation";

    protected static final String KEY_SERVLET_PATH = "servletPath";
    protected static final String KEY_SERVLET_PATH_PREFILTER = "servletPath@prefilter";
    protected static final String KEY_CONTEXT_PATH = "contextPath";
    protected static final String KEY_CONTEXT_PATH_PREFILTER = "contextPath@prefilter";
    protected static final String KEY_PATH_INFO = "pathInfo";
    protected static final String KEY_PATH_INFO_PREFILTER = "pathInfo@prefilter";
    protected static final String KEY_PATH_TRANSLATED = "pathTranslated";
    protected static final String KEY_PATH_TRANSLATED_PREFILTER = "pathTranslated@prefilter";
    protected static final String KEY_COUNTRY = "country";
    protected static final String KEY_COUNTRY_PREFILTER = "country@prefilter";
    protected static final String KEY_REMOTE_ADDR = "remoteAddr";
    protected static final String KEY_REMOTE_ADDR_PREFILTER = "remoteAddr@prefilter";
    protected static final String KEY_REMOTE_HOST = "remoteHost";
    protected static final String KEY_REMOTE_HOST_PREFILTER = "remoteHost@prefilter";
    protected static final String KEY_REMOTE_PORT = "remotePort";
    protected static final String KEY_REMOTE_PORT_PREFILTER = "remotePort@prefilter";
    protected static final String KEY_REMOTE_USER = "remoteUser";
    protected static final String KEY_REMOTE_USER_PREFILTER = "remoteUser@prefilter";
    protected static final String KEY_TIME = "time";
    protected static final String KEY_TIME_PREFILTER = "time@prefilter";
    protected static final String KEY_TIME_YEAR = "timeYear";
    protected static final String KEY_TIME_YEAR_PREFILTER = "timeYear@prefilter";
    protected static final String KEY_TIME_MONTH = "timeMonth";
    protected static final String KEY_TIME_MONTH_PREFILTER = "timeMonth@prefilter";
    protected static final String KEY_TIME_DAY = "timeDay";
    protected static final String KEY_TIME_DAY_PREFILTER = "timeDay@prefilter";
    protected static final String KEY_TIME_HOUR = "timeHour";
    protected static final String KEY_TIME_HOUR_PREFILTER = "timeHour@prefilter";
    protected static final String KEY_TIME_MINUTE = "timeMinute";
    protected static final String KEY_TIME_MINUTE_PREFILTER = "timeMinute@prefilter";
    protected static final String KEY_TIME_SECOND = "timeSecod";
    protected static final String KEY_TIME_SECOND_PREFILTER = "timeSecod@prefilter";
    protected static final String KEY_TIME_WEEKDAY = "timeWeekday";
    protected static final String KEY_TIME_WEEKDAY_PREFILTER = "timeWeekday@prefilter";
    protected static final String KEY_AUTH_TYPE = "authType";
    protected static final String KEY_AUTH_TYPE_PREFILTER = "authType@prefilter";
    protected static final String KEY_SCHEME = "scheme";
    protected static final String KEY_SCHEME_PREFILTER = "scheme@prefilter";
    protected static final String KEY_METHOD = "method";
    protected static final String KEY_METHOD_PREFILTER = "method@prefilter";
    protected static final String KEY_PROTOCOL = "protocol";
    protected static final String KEY_PROTOCOL_PREFILTER = "protocol@prefilter";
    protected static final String KEY_MIME_TYPE = "mimeType";
    protected static final String KEY_MIME_TYPE_PREFILTER = "mimeType@prefilter";
    protected static final String KEY_ENCODING = "encoding";
    protected static final String KEY_ENCODING_PREFILTER = "encoding@prefilter";
    protected static final String KEY_CONTENT_LENGTH = "contentLength";
    protected static final String KEY_CONTENT_LENGTH_PREFILTER = "contentLength@prefilter";
    protected static final String KEY_HEADER_NAME_ANY = "header";
    protected static final String KEY_HEADER_NAME_ANY_PREFILTER = "header@prefilter";
    protected static final String KEY_HEADER_NAME_PREFIX = "header_";
    protected static final String KEY_HEADER_NAME_PREFIX_PREFILTER = "header@prefilter_";
    protected static final String KEY_HEADER_COUNT_ANY = "headerCount";
    protected static final String KEY_HEADER_COUNT_ANY_PREFILTER = "headerCount@prefilter";
    protected static final String KEY_HEADER_COUNT_PREFIX = "headerCount_";
    protected static final String KEY_HEADER_COUNT_PREFIX_PREFILTER = "headerCount@prefilter_";
    protected static final String KEY_HEADER_NAME_LIST = "headerNameListSorted";
    protected static final String KEY_HEADER_NAME_LIST_PREFILTER = "headerNameListSorted@prefilter";
    protected static final String KEY_REQUEST_URL = "requestURL";
    protected static final String KEY_REQUEST_URL_PREFILTER = "requestURL@prefilter";
    protected static final String KEY_REQUEST_URI = "requestURI";
    protected static final String KEY_REQUEST_URI_PREFILTER = "requestURI@prefilter";
    protected static final String KEY_COOKIE_NAME_ANY = "cookie";
    protected static final String KEY_COOKIE_NAME_ANY_PREFILTER = "cookie@prefilter";
    protected static final String KEY_COOKIE_NAME_PREFIX = "cookie_";
    protected static final String KEY_COOKIE_NAME_PREFIX_PREFILTER = "cookie@prefilter_";
    protected static final String KEY_COOKIE_COUNT_ANY = "cookieCount";
    protected static final String KEY_COOKIE_COUNT_ANY_PREFILTER = "cookieCount@prefilter";
    protected static final String KEY_COOKIE_COUNT_PREFIX = "cookieCount_";
    protected static final String KEY_COOKIE_COUNT_PREFIX_PREFILTER = "cookieCount@prefilter_";
    protected static final String KEY_COOKIE_NAME_LIST = "cookieNameListSorted";
    protected static final String KEY_COOKIE_NAME_LIST_PREFILTER = "cookieNameListSorted@prefilter";
    protected static final String KEY_REQUESTED_SESSION_ID = "requestedSessionId";
    protected static final String KEY_REQUESTED_SESSION_ID_PREFILTER = "requestedSessionId@prefilter";
    protected static final String KEY_QUERY_STRING = "queryString";
    protected static final String KEY_QUERY_STRING_PREFILTER = "queryString@prefilter";
    protected static final String KEY_PARAM_NAME_ANY = "requestParam";
    protected static final String KEY_PARAM_NAME_ANY_PREFILTER = "requestParam@prefilter";
    protected static final String KEY_PARAM_NAME_PREFIX = "requestParam_";
    protected static final String KEY_PARAM_NAME_PREFIX_PREFILTER = "requestParam@prefilter_";
    protected static final String KEY_PARAM_COUNT_ANY = "requestParamCount";
    protected static final String KEY_PARAM_COUNT_ANY_PREFILTER = "requestParamCount@prefilter";
    protected static final String KEY_PARAM_COUNT_PREFIX = "requestParamCount_";
    protected static final String KEY_PARAM_COUNT_PREFIX_PREFILTER = "requestParamCount@prefilter_";
    protected static final String KEY_PARAM_NAME_LIST = "requestParamNameListSorted";
    protected static final String KEY_PARAM_NAME_LIST_PREFILTER = "requestParamNameListSorted@prefilter";
    protected static final String KEY_SERVER_NAME = "serverName";
    protected static final String KEY_SERVER_NAME_PREFILTER = "serverName@prefilter";
    protected static final String KEY_SERVER_PORT = "serverPort";
    protected static final String KEY_SERVER_PORT_PREFILTER = "serverPort@prefilter";
    protected static final String KEY_LOCAL_ADDR = "localAddr";
    protected static final String KEY_LOCAL_ADDR_PREFILTER = "localAddr@prefilter";
    protected static final String KEY_LOCAL_NAME = "localName";
    protected static final String KEY_LOCAL_NAME_PREFILTER = "localName@prefilter";
    protected static final String KEY_LOCAL_PORT = "localPort";
    protected static final String KEY_LOCAL_PORT_PREFILTER = "localPort@prefilter";

    private final boolean nonStandardPermutationsAllowed;
    private final RequestDefinition defaultMatch;

    private boolean isHavingEnabledRequestParamCheckingRules;
    private boolean isHavingEnabledQueryStringCheckingRules;
    private boolean isHavingEnabledHeaderCheckingRules;
    private boolean isHavingEnabledCookieCheckingRules;

    protected RequestDefinitionContainer(final RuleFileLoader ruleFileLoader, final boolean nonStandardPermutationsAllowed) {
        super(ruleFileLoader);
        this.nonStandardPermutationsAllowed = nonStandardPermutationsAllowed;
        this.defaultMatch = createRequestDefinition(true, "DEFAULT_MATCH", "Default match indicator (because of the configured flag to treat no servletPath match automatically as a match)", null, Pattern.compile("")/*null is not allowed so we simply use an empty pattern here*/, false);
    }

    public final boolean isNonStandardPermutationsAllowed() {
        return this.nonStandardPermutationsAllowed;
    }

    public boolean isHavingEnabledCookieCheckingRules() {
        return isHavingEnabledCookieCheckingRules;
    }

    public boolean isHavingEnabledHeaderCheckingRules() {
        return isHavingEnabledHeaderCheckingRules;
    }

    public boolean isHavingEnabledQueryStringCheckingRules() {
        return isHavingEnabledQueryStringCheckingRules;
    }

    public boolean isHavingEnabledRequestParamCheckingRules() {
        return isHavingEnabledRequestParamCheckingRules;
    }

    /**
     * NOTE: The caller of this class already synchronizes the reloading and
     * using of rules properly (see WebCastellumFilter.doFilter() and
     * WebCastellumFilter.doBeforeProcessing()), so that synchronization is not
     * required here: the caller ensures that rule reloading and rule using is
     * completely serialized
     *
     * @return message to log when loading is finished (to avoid logging while
     * within synchronized block, since Tomcat for example has problems with
     * stdout to a file after a hibernation when in synchronized block)
     */
    @Override
    public final String parseDefinitions() throws RuleLoadingException, RuleLoadingException {
        final RuleFile[] ruleFiles = this.ruleFileLoader.loadRuleFiles();
        final String message = "WebCastellum loaded " + (ruleFiles.length < 10 ? " " : "") + ruleFiles.length + " security rule" + (ruleFiles.length == 1 ? ":  " : "s: ") + this.ruleFileLoader.getPath() + " (via " + this.ruleFileLoader.getClass().getName() + ")"; // TODO: Java5 use StringBuilder
        final SortedSet newDefinitions = new TreeSet();
        boolean newHasEnabledDefinitions = false, newHavingEnabledRequestParamCheckingRules = false, newHavingEnabledQueryStringCheckingRules = false, newHavingEnabledHeaderCheckingRules = false, newHavingEnabledCookieCheckingRules = false;

        for (RuleFile ruleFile : ruleFiles) {
            final Properties properties = ruleFile.getProperties();
            // extract request rules from rule file
            // "enabled" and "description" are the standard base properties that even exist for CustomRequestMatcher based rule files
            final boolean enabled = ("" + true).equals(properties.getProperty(KEY_ENABLED, "true").trim().toLowerCase());
            if (enabled) {
                newHasEnabledDefinitions = true;
            }
            final String description = properties.getProperty(KEY_DESCRIPTION);
            if (description == null) {
                throw new IllegalRuleDefinitionFormatException("Description property (" + KEY_DESCRIPTION + ") not found in rule file: " + ruleFile);
            }
            // here we decide if it is a pure declarative rule file (which then required a "servletPath" property) or if it is a CustomRequestMatcher based rule file (which then requires a "customRequestMatcher" property)
            final String servletPath = properties.getProperty(KEY_SERVLET_PATH);
            final String customRequestMatcherClassName = properties.getProperty(KEY_CUSTOM_REQUEST_MATCHER);
            if (servletPath == null && customRequestMatcherClassName == null) {
                throw new IllegalRuleDefinitionFormatException("Servlet path property (" + KEY_SERVLET_PATH + ") OR custom request matcher property (" + KEY_CUSTOM_REQUEST_MATCHER + ") not found in rule file: " + ruleFile);
            }
            // now decide if a standard rule file or a custom-request-matcher based rule file should be created
            if (properties.containsKey(KEY_CUSTOM_REQUEST_MATCHER)) {
                //= custom-request-matcher based rule file @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                try {
                    final Class customRequestMatcherClass = Class.forName(customRequestMatcherClassName.trim());
                    final CustomRequestMatcher customRequestMatcher = (CustomRequestMatcher) customRequestMatcherClass.newInstance();
                    // create the request-definition using that customRequestMatcher
                    final /*"T" anstatt RequestDefinition*/ RequestDefinition requestDefinition = createRequestDefinition(enabled, ruleFile.getName(), description, customRequestMatcher);
                    // apply specific code from the sub-types (template method like)
                    extractAndRemoveSpecificProperties(requestDefinition, properties);
                    // let the custom-request-matcher read all custom-request-matcher-properties 
                    final Properties customRequestMatcherProperties = new Properties();
                    for (final Enumeration keys = properties.propertyNames(); keys.hasMoreElements();) {
                        final String key = (String) keys.nextElement();
                        if (!KEY_CUSTOM_REQUEST_MATCHER.equals(key) && !KEY_DESCRIPTION.equals(key) && !KEY_ENABLED.equals(key)) {
                            final String value = properties.getProperty(key);
                            customRequestMatcherProperties.setProperty(key, value);
                        }
                    }
                    customRequestMatcher.setCustomRequestMatcherProperties(customRequestMatcherProperties);
                    // add RequestDefinition to sorted set
                    newDefinitions.add(requestDefinition);
                } catch (ClassNotFoundException e) {
                    throw new IllegalRuleDefinitionFormatException("Unable to locate class for customRequestMatcher in rule file: " + ruleFile, e);
                } catch (InstantiationException e) {
                    throw new IllegalRuleDefinitionFormatException("Unable to instantiate class for customRequestMatcher in rule file: " + ruleFile, e);
                } catch (IllegalAccessException e) {
                    throw new IllegalRuleDefinitionFormatException("Unable to access class for customRequestMatcher in rule file: " + ruleFile, e);
                } catch (CustomRequestMatchingException e) {
                    throw new IllegalRuleDefinitionFormatException("Unable to set custom request matching properties for customRequestMatcher in rule file: " + ruleFile, e);
                }
            } else {
                //= standard rule file @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                // fetch the list of negations
                final List/*<String>*/ negations = new ArrayList();
                if (properties.getProperty(KEY_NEGATION) != null) {
                    // split, since they're comma-separated'
                    final String[] negationNamesSplitted = properties.getProperty(KEY_NEGATION).split(",");
                    for (String negationNamesSplitted1 : negationNamesSplitted) {
                        final String negationName = negationNamesSplitted1.trim();
                        if (negationName.length() > 0) {
                            negations.add(negationName);
                        }
                    }
                }
                // quick check to find disallowed negation names
                if (negations.contains(KEY_DESCRIPTION)) {
                    throw new IllegalRuleDefinitionFormatException("Description property is not allowed in negation names found in rule file: " + ruleFile);
                }
                if (negations.contains(KEY_ENABLED)) {
                    throw new IllegalRuleDefinitionFormatException("Enabled flag is not allowed in negation names found in rule file: " + ruleFile);
                }
                if (negations.contains(KEY_NEGATION)) {
                    throw new IllegalRuleDefinitionFormatException("Not allowed to negate the negations found in rule file: " + ruleFile);
                }
                // fetch the expressions
                try {
                    final Pattern servletPathPattern = Pattern.compile(servletPath);
                    final boolean servletPathPatternNegated = negations.contains(KEY_SERVLET_PATH);
                    final WordDictionary servletPathPrefilter = WordDictionary.createInstance(properties.getProperty(KEY_SERVLET_PATH_PREFILTER));
                    final /*"T" anstatt RequestDefinition*/ RequestDefinition requestDefinition = createRequestDefinition(enabled, ruleFile.getName(), description, servletPathPrefilter, servletPathPattern, servletPathPatternNegated);
                    // apply specific code from the sub-types (template method like)
                    extractAndRemoveSpecificProperties(requestDefinition, properties);

                    // quick check to find misspelled negation names
                    // NOTE: negations are not [yet] allowed for names specified in sub-types of RuleDefinition,
                    // so we make this check after the custom properties of the sub-type of RuleDefinition have been
                    // already worked on and removed from the properties object
                    final List/*<String>*/ copyOfNegations = new ArrayList(negations);
                    for (final Enumeration keys = properties.propertyNames(); keys.hasMoreElements();) {
                        copyOfNegations.remove(keys.nextElement());
                    }
                    if (!copyOfNegations.isEmpty()) {
                        throw new IllegalRuleDefinitionFormatException("Unknown negation names (" + copyOfNegations + ") found in rule file: " + ruleFile);
                    }

                    // create a copy for live removal of worked on keys
                    final Set/*<String>*/ copyOfKeys = properties.keySet();
                    // remove special keys
                    copyOfKeys.remove(KEY_DESCRIPTION);
                    copyOfKeys.remove(KEY_SERVLET_PATH);
                    copyOfKeys.remove(KEY_SERVLET_PATH_PREFILTER);
                    copyOfKeys.remove(KEY_NEGATION);
                    copyOfKeys.remove(KEY_ENABLED);

                    // now load all (also optional) settings
                    for (final Enumeration keys = properties.propertyNames(); keys.hasMoreElements();) {
                        final String key = (String) keys.nextElement();
                        if (KEY_CONTEXT_PATH.equals(key)) {
                            requestDefinition.setContextPathPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_PATH_INFO.equals(key)) {
                            requestDefinition.setPathInfoPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_PATH_TRANSLATED.equals(key)) {
                            requestDefinition.setPathTranslatedPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_COUNTRY.equals(key)) {
                            requestDefinition.setCountryPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_REMOTE_ADDR.equals(key)) {
                            requestDefinition.setRemoteAddrPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_REMOTE_HOST.equals(key)) {
                            requestDefinition.setRemoteHostPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_REMOTE_PORT.equals(key)) {
                            requestDefinition.setRemotePortPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_REMOTE_USER.equals(key)) {
                            requestDefinition.setRemoteUserPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_TIME.equals(key)) {
                            requestDefinition.setTimePattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_TIME_YEAR.equals(key)) {
                            requestDefinition.setTimeYearPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_TIME_MONTH.equals(key)) {
                            requestDefinition.setTimeMonthPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_TIME_DAY.equals(key)) {
                            requestDefinition.setTimeDayPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_TIME_HOUR.equals(key)) {
                            requestDefinition.setTimeHourPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_TIME_MINUTE.equals(key)) {
                            requestDefinition.setTimeMinutePattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_TIME_SECOND.equals(key)) {
                            requestDefinition.setTimeSecondPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_TIME_WEEKDAY.equals(key)) {
                            requestDefinition.setTimeWeekdayPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_AUTH_TYPE.equals(key)) {
                            requestDefinition.setAuthTypePattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_SCHEME.equals(key)) {
                            requestDefinition.setSchemePattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_METHOD.equals(key)) {
                            requestDefinition.setMethodPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_PROTOCOL.equals(key)) {
                            requestDefinition.setProtocolPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_MIME_TYPE.equals(key)) {
                            requestDefinition.setMimeTypePattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_ENCODING.equals(key)) {
                            requestDefinition.setEncodingPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_CONTENT_LENGTH.equals(key)) {
                            requestDefinition.setContentLengthPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (key.length() > KEY_HEADER_NAME_PREFIX.length() && key.startsWith(KEY_HEADER_NAME_PREFIX)) {
                            if (enabled) {
                                newHavingEnabledHeaderCheckingRules = true;
                            }
                            final String headerName = key.substring(KEY_HEADER_NAME_PREFIX.length()).toUpperCase();
                            if (requestDefinition.getHeaderValuePattern(headerName) != null) {
                                throw new IllegalRuleDefinitionFormatException("Multiple headers with the same name are not allowed in rule file (note that header names are case-insensitive): " + ruleFile);
                            }
                            final Pattern headerValuePattern = Pattern.compile(properties.getProperty(key));
                            requestDefinition.addHeaderValuePattern(headerName, headerValuePattern, negations.contains(key));
                        } else if (KEY_HEADER_NAME_ANY.equals(key)) {
                            if (enabled) {
                                newHavingEnabledHeaderCheckingRules = true;
                            }
                            final Pattern headerValuePattern = Pattern.compile(properties.getProperty(key));
                            // null as special key that stands for "any header name"
                            requestDefinition.addHeaderValuePattern(null, headerValuePattern, negations.contains(key));
                        } else if (key.length() > KEY_HEADER_COUNT_PREFIX.length() && key.startsWith(KEY_HEADER_COUNT_PREFIX)) {
                            if (enabled) {
                                newHavingEnabledHeaderCheckingRules = true;
                            }
                            final String headerName = key.substring(KEY_HEADER_COUNT_PREFIX.length()).toUpperCase();
                            if (requestDefinition.getHeaderCountPattern(headerName) != null) {
                                throw new IllegalRuleDefinitionFormatException("Multiple headers with the same name are not allowed in rule file (note that header names are case-insensitive): " + ruleFile);
                            }
                            final Pattern headerCountPattern = Pattern.compile(properties.getProperty(key));
                            requestDefinition.addHeaderCountPattern(headerName, headerCountPattern, negations.contains(key));
                        } else if (KEY_HEADER_COUNT_ANY.equals(key)) {
                            if (enabled) {
                                newHavingEnabledHeaderCheckingRules = true;
                            }
                            final Pattern headerCountPattern = Pattern.compile(properties.getProperty(key));
                            // null as special key that stands for "any header name"
                            requestDefinition.addHeaderCountPattern(null, headerCountPattern, negations.contains(key));
                        } else if (KEY_HEADER_NAME_LIST.equals(key)) {
                            if (enabled) {
                                newHavingEnabledHeaderCheckingRules = true;
                            }
                            requestDefinition.setHeaderNameListPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_REQUEST_URL.equals(key)) {
                            requestDefinition.setRequestURLPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_REQUEST_URI.equals(key)) {
                            requestDefinition.setRequestURIPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (key.length() > KEY_COOKIE_NAME_PREFIX.length() && key.startsWith(KEY_COOKIE_NAME_PREFIX)) {
                            if (enabled) {
                                newHavingEnabledCookieCheckingRules = true;
                            }
                            final String cookieName = key.substring(KEY_COOKIE_NAME_PREFIX.length()).toUpperCase();
                            if (requestDefinition.getCookieValuePattern(cookieName) != null) {
                                throw new IllegalRuleDefinitionFormatException("Multiple cookies with the same name are not allowed in rule file (note that cookie names are case-insensitive): " + ruleFile);
                            }
                            final Pattern cookieValuePattern = Pattern.compile(properties.getProperty(key));
                            requestDefinition.addCookieValuePattern(cookieName, cookieValuePattern, negations.contains(key));
                        } else if (KEY_COOKIE_NAME_ANY.equals(key)) {
                            if (enabled) {
                                newHavingEnabledCookieCheckingRules = true;
                            }
                            final Pattern cookieValuePattern = Pattern.compile(properties.getProperty(key));
                            // null as special key that stands for "any cookie name"
                            requestDefinition.addCookieValuePattern(null, cookieValuePattern, negations.contains(key));
                        } else if (key.length() > KEY_COOKIE_COUNT_PREFIX.length() && key.startsWith(KEY_COOKIE_COUNT_PREFIX)) {
                            if (enabled) {
                                newHavingEnabledCookieCheckingRules = true;
                            }
                            final String cookieName = key.substring(KEY_COOKIE_COUNT_PREFIX.length()).toUpperCase();
                            if (requestDefinition.getCookieCountPattern(cookieName) != null) {
                                throw new IllegalRuleDefinitionFormatException("Multiple cookies with the same name are not allowed in rule file (note that cookie names are case-insensitive): " + ruleFile);
                            }
                            final Pattern cookieCountPattern = Pattern.compile(properties.getProperty(key));
                            requestDefinition.addCookieCountPattern(cookieName, cookieCountPattern, negations.contains(key));
                        } else if (KEY_COOKIE_COUNT_ANY.equals(key)) {
                            if (enabled) {
                                newHavingEnabledCookieCheckingRules = true;
                            }
                            final Pattern cookieCountPattern = Pattern.compile(properties.getProperty(key));
                            // null as special key that stands for "any cookie name"
                            requestDefinition.addCookieCountPattern(null, cookieCountPattern, negations.contains(key));
                        } else if (KEY_COOKIE_NAME_LIST.equals(key)) {
                            if (enabled) {
                                newHavingEnabledCookieCheckingRules = true;
                            }
                            requestDefinition.setCookieNameListPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_REQUESTED_SESSION_ID.equals(key)) {
                            requestDefinition.setRequestedSessionIdPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_QUERY_STRING.equals(key)) {
                            if (enabled) {
                                newHavingEnabledQueryStringCheckingRules = true;
                            }
                            requestDefinition.setQueryStringPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (key.length() > KEY_PARAM_NAME_PREFIX.length() && key.startsWith(KEY_PARAM_NAME_PREFIX)) {
                            if (enabled) {
                                newHavingEnabledRequestParamCheckingRules = true;
                            }
                            final String paramName = key.substring(KEY_PARAM_NAME_PREFIX.length());
                            if (requestDefinition.getRequestParamValuePattern(paramName) != null) {
                                throw new IllegalRuleDefinitionFormatException("Multiple request parameters with the same name are not allowed in rule file: " + ruleFile);
                            }
                            final Pattern paramValuePattern = Pattern.compile(properties.getProperty(key));
                            requestDefinition.addRequestParamValuePattern(paramName, paramValuePattern, negations.contains(key));
                        } else if (KEY_PARAM_NAME_ANY.equals(key)) {
                            if (enabled) {
                                newHavingEnabledRequestParamCheckingRules = true;
                            }
                            final Pattern paramValuePattern = Pattern.compile(properties.getProperty(key));
                            // null as special key that stands for "any parameter name"
                            requestDefinition.addRequestParamValuePattern(null, paramValuePattern, negations.contains(key));
                        } else if (key.length() > KEY_PARAM_COUNT_PREFIX.length() && key.startsWith(KEY_PARAM_COUNT_PREFIX)) {
                            if (enabled) {
                                newHavingEnabledRequestParamCheckingRules = true;
                            }
                            final String paramName = key.substring(KEY_PARAM_COUNT_PREFIX.length());
                            if (requestDefinition.getRequestParamCountPattern(paramName) != null) {
                                throw new IllegalRuleDefinitionFormatException("Multiple request parameters with the same name are not allowed in rule file: " + ruleFile);
                            }
                            final Pattern paramCountPattern = Pattern.compile(properties.getProperty(key));
                            requestDefinition.addRequestParamCountPattern(paramName, paramCountPattern, negations.contains(key));
                        } else if (KEY_PARAM_COUNT_ANY.equals(key)) {
                            if (enabled) {
                                newHavingEnabledRequestParamCheckingRules = true;
                            }
                            final Pattern paramCountPattern = Pattern.compile(properties.getProperty(key));
                            // null as special key that stands for "any parameter name"
                            requestDefinition.addRequestParamCountPattern(null, paramCountPattern, negations.contains(key));
                        } else if (KEY_PARAM_NAME_LIST.equals(key)) {
                            if (enabled) {
                                newHavingEnabledRequestParamCheckingRules = true;
                            }
                            requestDefinition.setRequestParamNameListPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_SERVER_NAME.equals(key)) {
                            requestDefinition.setServerNamePattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_SERVER_PORT.equals(key)) {
                            requestDefinition.setServerPortPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_LOCAL_ADDR.equals(key)) {
                            requestDefinition.setLocalAddrPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_LOCAL_NAME.equals(key)) {
                            requestDefinition.setLocalNamePattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } else if (KEY_LOCAL_PORT.equals(key)) {
                            requestDefinition.setLocalPortPattern(Pattern.compile(properties.getProperty(key)), negations.contains(key));
                        } // optional PREFILTER values
                        else if (KEY_CONTEXT_PATH_PREFILTER.equals(key)) {
                            requestDefinition.setContextPathPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_PATH_INFO_PREFILTER.equals(key)) {
                            requestDefinition.setPathInfoPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_PATH_TRANSLATED_PREFILTER.equals(key)) {
                            requestDefinition.setPathTranslatedPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_COUNTRY_PREFILTER.equals(key)) {
                            requestDefinition.setCountryPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_REMOTE_ADDR_PREFILTER.equals(key)) {
                            requestDefinition.setRemoteAddrPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_REMOTE_HOST_PREFILTER.equals(key)) {
                            requestDefinition.setRemoteHostPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_REMOTE_PORT_PREFILTER.equals(key)) {
                            requestDefinition.setRemotePortPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_REMOTE_USER_PREFILTER.equals(key)) {
                            requestDefinition.setRemoteUserPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_TIME_PREFILTER.equals(key)) {
                            requestDefinition.setTimePrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_TIME_YEAR_PREFILTER.equals(key)) {
                            requestDefinition.setTimeYearPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_TIME_MONTH_PREFILTER.equals(key)) {
                            requestDefinition.setTimeMonthPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_TIME_DAY_PREFILTER.equals(key)) {
                            requestDefinition.setTimeDayPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_TIME_HOUR_PREFILTER.equals(key)) {
                            requestDefinition.setTimeHourPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_TIME_MINUTE_PREFILTER.equals(key)) {
                            requestDefinition.setTimeMinutePrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_TIME_SECOND_PREFILTER.equals(key)) {
                            requestDefinition.setTimeSecondPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_TIME_WEEKDAY_PREFILTER.equals(key)) {
                            requestDefinition.setTimeWeekdayPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_AUTH_TYPE_PREFILTER.equals(key)) {
                            requestDefinition.setAuthTypePrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_SCHEME_PREFILTER.equals(key)) {
                            requestDefinition.setSchemePrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_METHOD_PREFILTER.equals(key)) {
                            requestDefinition.setMethodPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_PROTOCOL_PREFILTER.equals(key)) {
                            requestDefinition.setProtocolPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_MIME_TYPE_PREFILTER.equals(key)) {
                            requestDefinition.setMimeTypePrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_ENCODING_PREFILTER.equals(key)) {
                            requestDefinition.setEncodingPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_CONTENT_LENGTH_PREFILTER.equals(key)) {
                            requestDefinition.setContentLengthPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_HEADER_NAME_LIST_PREFILTER.equals(key)) {
                            requestDefinition.setHeaderNameListPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_REQUEST_URL_PREFILTER.equals(key)) {
                            requestDefinition.setRequestURLPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_REQUEST_URI_PREFILTER.equals(key)) {
                            requestDefinition.setRequestURIPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_COOKIE_NAME_LIST_PREFILTER.equals(key)) {
                            requestDefinition.setCookieNameListPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_REQUESTED_SESSION_ID_PREFILTER.equals(key)) {
                            requestDefinition.setRequestedSessionIdPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_QUERY_STRING_PREFILTER.equals(key)) {
                            requestDefinition.setQueryStringPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_PARAM_NAME_LIST_PREFILTER.equals(key)) {
                            requestDefinition.setRequestParamNameListPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_SERVER_NAME_PREFILTER.equals(key)) {
                            requestDefinition.setServerNamePrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_SERVER_PORT_PREFILTER.equals(key)) {
                            requestDefinition.setServerPortPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_LOCAL_ADDR_PREFILTER.equals(key)) {
                            requestDefinition.setLocalAddrPrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_LOCAL_NAME_PREFILTER.equals(key)) {
                            requestDefinition.setLocalNamePrefilter(WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_LOCAL_PORT_PREFILTER.equals(key)) {
                            requestDefinition.setLocalPortPrefilter(WordDictionary.createInstance(properties.getProperty(key)));

                        } else if (key.length() > KEY_HEADER_NAME_PREFIX_PREFILTER.length() && key.startsWith(KEY_HEADER_NAME_PREFIX_PREFILTER)) {
                            final String headerName = key.substring(KEY_HEADER_NAME_PREFIX_PREFILTER.length()).toUpperCase();
                            if (requestDefinition.getHeaderValuePrefilter(headerName) != null) {
                                throw new IllegalRuleDefinitionFormatException("Multiple headers with the same name are not allowed in rule file (note that header names are case-insensitive): " + ruleFile);
                            }
                            requestDefinition.addHeaderValuePrefilter(headerName, WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_HEADER_NAME_ANY_PREFILTER.equals(key)) {
                            // null as special key that stands for "any header name"
                            requestDefinition.addHeaderValuePrefilter(null, WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (key.length() > KEY_HEADER_COUNT_PREFIX_PREFILTER.length() && key.startsWith(KEY_HEADER_COUNT_PREFIX_PREFILTER)) {
                            final String headerName = key.substring(KEY_HEADER_COUNT_PREFIX_PREFILTER.length()).toUpperCase();
                            if (requestDefinition.getHeaderCountPrefilter(headerName) != null) {
                                throw new IllegalRuleDefinitionFormatException("Multiple headers with the same name are not allowed in rule file (note that header names are case-insensitive): " + ruleFile);
                            }
                            requestDefinition.addHeaderCountPrefilter(headerName, WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_HEADER_COUNT_ANY_PREFILTER.equals(key)) {
                            // null as special key that stands for "any header name"
                            requestDefinition.addHeaderCountPrefilter(null, WordDictionary.createInstance(properties.getProperty(key)));

                        } else if (key.length() > KEY_COOKIE_NAME_PREFIX_PREFILTER.length() && key.startsWith(KEY_COOKIE_NAME_PREFIX_PREFILTER)) {
                            final String cookieName = key.substring(KEY_COOKIE_NAME_PREFIX_PREFILTER.length()).toUpperCase();
                            if (requestDefinition.getCookieValuePrefilter(cookieName) != null) {
                                throw new IllegalRuleDefinitionFormatException("Multiple cookies with the same name are not allowed in rule file (note that cookie names are case-insensitive): " + ruleFile);
                            }
                            requestDefinition.addCookieValuePrefilter(cookieName, WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_COOKIE_NAME_ANY_PREFILTER.equals(key)) {
                            // null as special key that stands for "any cookie name"
                            requestDefinition.addCookieValuePrefilter(null, WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (key.length() > KEY_COOKIE_COUNT_PREFIX_PREFILTER.length() && key.startsWith(KEY_COOKIE_COUNT_PREFIX_PREFILTER)) {
                            final String cookieName = key.substring(KEY_COOKIE_COUNT_PREFIX_PREFILTER.length()).toUpperCase();
                            if (requestDefinition.getCookieCountPrefilter(cookieName) != null) {
                                throw new IllegalRuleDefinitionFormatException("Multiple cookies with the same name are not allowed in rule file (note that cookie names are case-insensitive): " + ruleFile);
                            }
                            requestDefinition.addCookieCountPrefilter(cookieName, WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_COOKIE_COUNT_ANY_PREFILTER.equals(key)) {
                            // null as special key that stands for "any cookie name"
                            requestDefinition.addCookieCountPrefilter(null, WordDictionary.createInstance(properties.getProperty(key)));

                        } else if (key.length() > KEY_PARAM_NAME_PREFIX_PREFILTER.length() && key.startsWith(KEY_PARAM_NAME_PREFIX_PREFILTER)) {
                            final String paramName = key.substring(KEY_PARAM_NAME_PREFIX_PREFILTER.length());
                            if (requestDefinition.getRequestParamValuePrefilter(paramName) != null) {
                                throw new IllegalRuleDefinitionFormatException("Multiple request parameters with the same name are not allowed in rule file: " + ruleFile);
                            }
                            requestDefinition.addRequestParamValuePrefilter(paramName, WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_PARAM_NAME_ANY_PREFILTER.equals(key)) {
                            // null as special key that stands for "any parameter name"
                            requestDefinition.addRequestParamValuePrefilter(null, WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (key.length() > KEY_PARAM_COUNT_PREFIX_PREFILTER.length() && key.startsWith(KEY_PARAM_COUNT_PREFIX_PREFILTER)) {
                            final String paramName = key.substring(KEY_PARAM_COUNT_PREFIX_PREFILTER.length());
                            if (requestDefinition.getRequestParamCountPrefilter(paramName) != null) {
                                throw new IllegalRuleDefinitionFormatException("Multiple request parameters with the same name are not allowed in rule file: " + ruleFile);
                            }
                            requestDefinition.addRequestParamCountPrefilter(paramName, WordDictionary.createInstance(properties.getProperty(key)));
                        } else if (KEY_PARAM_COUNT_ANY_PREFILTER.equals(key)) {
                            // null as special key that stands for "any parameter name"
                            requestDefinition.addRequestParamCountPrefilter(null, WordDictionary.createInstance(properties.getProperty(key)));

                        } else {
                            // yet unknown key, so don't remove it from the list: continue directly without removal (see below)
                            continue;
                        }
                        // remove the already worked on key from the list
                        copyOfKeys.remove(key);
                    }
                    // check for any unknown keys
                    if (!copyOfKeys.isEmpty()) {
                        throw new IllegalRuleDefinitionFormatException("Unknown keys (" + copyOfKeys + ") found in rule file: " + ruleFile);
                    }
                    // add RequestDefinition to sorted set
                    newDefinitions.add(requestDefinition);
                } catch (PatternSyntaxException e) {
                    throw new IllegalRuleDefinitionFormatException("Invalid regular expression syntax in rule file: " + ruleFile, e);
                }
            }
        }
        // now overwrite the previous values/rules: in order to make it as quick and atomic as possible
        this.definitions = newDefinitions;
        this.hasEnabledDefinitions = newHasEnabledDefinitions;
        this.isHavingEnabledRequestParamCheckingRules = newHavingEnabledRequestParamCheckingRules;
        this.isHavingEnabledQueryStringCheckingRules = newHavingEnabledQueryStringCheckingRules;
        this.isHavingEnabledHeaderCheckingRules = newHavingEnabledHeaderCheckingRules;
        this.isHavingEnabledCookieCheckingRules = newHavingEnabledCookieCheckingRules;

        return message;
    }

    protected final RequestDefinition[] getAllEnabledRequestDefinitions() {
        final List/*<RequestDefinition>*/ results = new ArrayList();
        for (final Iterator iter = this.definitions.iterator(); iter.hasNext();) {
            final RequestDefinition requestDefinition = (RequestDefinition) iter.next();
            if (requestDefinition.isEnabled()) {
                results.add(requestDefinition);
            }
        }
        return (RequestDefinition[]) results.toArray(new RequestDefinition[0]); // empty = no match // filled = matches
    }

    /**
     * NOTE: The caller of this class already synchronizes the reloading and
     * using of rules properly (see WebCastellumFilter.doFilter() and
     * WebCastellumFilter.doBeforeProcessing()), so that synchronization is not
     * required here: the caller ensures that rule reloading and rule using is
     * completely serialized
     *
     * @param matchingSingleValueIsEnough true when for array-based values (like
     * multiple values for the same header or param) it is enough if at least
     * one value out of the array matches
     * @return the request definition (for logging, etc.) in case of a matching
     * request, or simply null when no match is available
     */
    protected final RequestDefinition[] getAllMatchingRequestDefinitions(final HttpServletRequest request, final String servletPath, final String contextPath, String pathInfo, String pathTranslated,
            final String clientAddress, final String remoteHost, final int remotePort, String remoteUser, String authType, final String scheme,
            final String method, final String protocol, String mimeType, String encoding, final int contentLength, final Map/*<String,Permutation>*/ headerMapVariants,
            final String requestURL, final String requestURI, final String serverName, final int serverPort,
            final String localAddr, final String localName, final int localPort, final String country, final Map/*<String,Permutation>*/ cookieMapVariants, String requestedSessionId, Permutation queryStringVariants,
            final Map/*<String,Permutation[]>*/ requestParameterMapVariants, final Map/*<String,String[]>*/ requestParameterMapExcludingInternalParams,
            final boolean matchingSingleValueIsEnough, final boolean treatNonMatchingServletPathAsMatch) throws CustomRequestMatchingException {
        return checkMatchingRequestDefinitions(false,
                request, servletPath, contextPath, pathInfo, pathTranslated, clientAddress, remoteHost, remotePort, remoteUser, authType, scheme,
                method, protocol, mimeType, encoding, contentLength, headerMapVariants, requestURL, requestURI, serverName, serverPort,
                localAddr, localName, localPort, country, cookieMapVariants, requestedSessionId, queryStringVariants, requestParameterMapVariants,
                requestParameterMapExcludingInternalParams, matchingSingleValueIsEnough, treatNonMatchingServletPathAsMatch);
    }

    /**
     * NOTE: The caller of this class already synchronizes the reloading and
     * using of rules properly (see WebCastellumFilter.doFilter() and
     * WebCastellumFilter.doBeforeProcessing()), so that synchronization is not
     * required here: the caller ensures that rule reloading and rule using is
     * completely serialized
     *
     * @param matchingSingleValueIsEnough true when for array-based values (like
     * multiple values for the same header or param) it is enough if at least
     * one value out of the array matches
     * @return the request definition (for logging, etc.) in case of a matching
     * request, or simply null when no match is available
     */
    protected final RequestDefinition getMatchingRequestDefinition(final HttpServletRequest request, final String servletPath, final String contextPath, String pathInfo, String pathTranslated,
            final String clientAddress, final String remoteHost, final int remotePort, String remoteUser, String authType, final String scheme,
            final String method, final String protocol, String mimeType, String encoding, final int contentLength, final Map/*<String,Permutation>*/ headerMapVariants,
            final String requestURL, final String requestURI, final String serverName, final int serverPort,
            final String localAddr, final String localName, final int localPort, final String country, final Map/*<String,Permutation>*/ cookieMapVariants, String requestedSessionId, Permutation queryStringVariants,
            final Map/*<String,Permutation[]>*/ requestParameterMapVariants, final Map/*<String,String[]>*/ requestParameterMapExcludingInternalParams,
            final boolean matchingSingleValueIsEnough, final boolean treatNonMatchingServletPathAsMatch) throws CustomRequestMatchingException {
        final RequestDefinition[] results = checkMatchingRequestDefinitions(true,
                request, servletPath, contextPath, pathInfo, pathTranslated, clientAddress, remoteHost, remotePort, remoteUser, authType, scheme,
                method, protocol, mimeType, encoding, contentLength, headerMapVariants, requestURL, requestURI, serverName, serverPort,
                localAddr, localName, localPort, country, cookieMapVariants, requestedSessionId, queryStringVariants, requestParameterMapVariants,
                requestParameterMapExcludingInternalParams, matchingSingleValueIsEnough, treatNonMatchingServletPathAsMatch);
        if (results.length == 0) {
            return null;
        }
        assert results.length == 1; // since only the first match should be returned here
        return results[0];
    }

    private final RequestDefinition[] checkMatchingRequestDefinitions(final boolean returnOnlyTheFirstMatchingDefinition,
            final HttpServletRequest request, final String servletPath, final String contextPath, String pathInfo, String pathTranslated,
            final String clientAddress, final String remoteHost, final int remotePort, String remoteUser, String authType, final String scheme,
            final String method, final String protocol, String mimeType, String encoding, final int contentLength, final Map/*<String,Permutation>*/ headerMapVariants,
            final String requestURL, final String requestURI, final String serverName, final int serverPort,
            String localAddr, String localName, final int localPort, String country, final Map/*<String,Permutation>*/ cookieMapVariants, String requestedSessionId, Permutation queryStringVariants,
            final Map/*<String,Permutation[]>*/ requestParameterMapVariants, final Map/*<String,String[]>*/ requestParameterMapExcludingInternalParams,
            final boolean matchingSingleValueIsEnough, final boolean treatNonMatchingServletPathAsMatch) throws CustomRequestMatchingException {
        // shortcuts
        if (!this.hasEnabledDefinitions) {
            return new RequestDefinition[0];
        }

        // set a default for non submitted parameters (see below)
        final Permutation permutationWithEmptyString = new Permutation();
        permutationWithEmptyString.addStandardPermutation("");
        permutationWithEmptyString.seal();

        // some arguments are optional, some are mandatory...
        if (servletPath == null) {
            throw new NullPointerException("servletPath must not be null");
        }
        if (contextPath == null) {
            throw new NullPointerException("contextPath must not be null");
        }
        if (pathInfo == null) {
            pathInfo = "";
        }
        if (pathTranslated == null) {
            pathTranslated = "";
        }
        if (clientAddress == null) {
            throw new NullPointerException("clientAddress must not be null");
        }
        if (remoteHost == null) {
            throw new NullPointerException("remoteHost must not be null");
        }
        if (remoteUser == null) {
            remoteUser = "";
        }
        if (authType == null) {
            authType = "";
        }
        if (scheme == null) {
            throw new NullPointerException("scheme must not be null");
        }
        if (method == null) {
            throw new NullPointerException("method must not be null");
        }
        if (protocol == null) {
            throw new NullPointerException("protocol must not be null");
        }
        if (mimeType == null) {
            mimeType = "";
        }
        if (encoding == null) {
            encoding = "";
        }
        if (headerMapVariants == null) {
            throw new NullPointerException("headerMapVariants must not be null");
        }
        if (requestURL == null) {
            throw new NullPointerException("requestURL must not be null");
        }
        if (requestURI == null) {
            throw new NullPointerException("requestURI must not be null");
        }
        if (serverName == null) {
            throw new NullPointerException("serverName must not be null");
        }
        if (localAddr == null) {
            localAddr = "";
        }
        if (localName == null) {
            localName = "";
        }
        if (country == null) {
            country = "";
        }
        if (cookieMapVariants == null) {
            throw new NullPointerException("cookieMapVariants must not be null");
        }
        if (requestedSessionId == null) {
            requestedSessionId = "";
        }
        if (queryStringVariants == null) {
            queryStringVariants = permutationWithEmptyString;
        }
        if (requestParameterMapVariants == null) {
            throw new NullPointerException("requestParameterMapVariants must not be null");
        }
        if (requestParameterMapExcludingInternalParams == null) {
            throw new NullPointerException("requestParameterMapExcludingInternalParams must not be null");
        }

        // =====
        // Now prepare some data so that normal request definition (rule) files can be procerssed quickly.
        // Custom request matcher don't use that prepared data.
        // fetch counts 
// TODO: auch auslagern aus dieser Methode und bereits vorher in WebCastellumFilter einmalig erledigen und hier nur die Ergebnisse (sizes) als Parameter rein
        final int totalHeaderValueCount = getTotalValueCount(headerMapVariants);
        final int totalCookieValueCount = getTotalValueCount(cookieMapVariants);
//OLD        final int totalRequestParamValueCount = getTotalValueCount(requestParameterMapVariants);
        final int totalRequestParamValueCountExcludingInternalParams = getTotalValueCount(requestParameterMapExcludingInternalParams);
        // fetch name-lists (sorted alphabetically)
// TODO: auch auslagern aus dieser Methode und bereits vorher in WebCastellumFilter einmalig erledigen und hier nur die Ergebnisse (lists) als Parameter rein
        final String headerNameList = sortedNameList(headerMapVariants.keySet()); //if(DEBUG) System.out.println("headerNameList="+headerNameList);
        final String cookieNameList = sortedNameList(cookieMapVariants.keySet()); //if(DEBUG) System.out.println("cookieNameList="+cookieNameList);
//OLD        final String requestParamNameList = sortedNameList(requestParameterMapVariants.keySet()); //if(DEBUG) System.out.println("requestParamNameList="+requestParamNameList);
        final String requestParamNameListExcludingInternalParams = sortedNameList(requestParameterMapExcludingInternalParams.keySet()); //if(DEBUG) System.out.println("requestParamNameList="+requestParamNameList);
        /* old
        // fetch value-lists (sorted alphabetically)
        final String headerValueList = sortedValueList(headerMap.values()); //if(DEBUG) System.out.println("headerValueList="+headerValueList);
        final String cookieValueList = sortedValueList(cookieMap.values()); //if(DEBUG) System.out.println("cookieValueList="+cookieValueList);
        final String requestParamValueList = sortedValueList(requestParameterMap.values()); //if(DEBUG) System.out.println("requestParamValueList="+requestParamValueList);
        final String requestParamValueListExcludingInternalParams = sortedValueList(requestParameterMapExcludingInternalParams.values()); //if(DEBUG) System.out.println("requestParamValueList="+requestParamValueList);
         */

        // fetch time relevant data (create SimpleDateFormat on the fly, as it is not thread-safe)
// TODO: auch auslagern aus dieser Methode und bereits vorher in WebCastellumFilter einmalig erledigen und hier nur die Ergebnisse (dates) als Parameter rein
        final Date now = new Date();
        final String time = new SimpleDateFormat(FORMAT_TIME, Locale.US).format(now);
        final String timeYear = new SimpleDateFormat(FORMAT_TIME_YEAR, Locale.US).format(now);
        final String timeMonth = new SimpleDateFormat(FORMAT_TIME_MONTH, Locale.US).format(now);
        final String timeDay = new SimpleDateFormat(FORMAT_TIME_DAY, Locale.US).format(now);
        final String timeHour = new SimpleDateFormat(FORMAT_TIME_HOUR, Locale.US).format(now);
        final String timeMinute = new SimpleDateFormat(FORMAT_TIME_MINUTE, Locale.US).format(now);
        final String timeSecond = new SimpleDateFormat(FORMAT_TIME_SECOND, Locale.US).format(now);
        final String timeWeekday = new SimpleDateFormat(FORMAT_TIME_WEEKDAY, Locale.US).format(now);

        // decodings, whitespace removals, null-byte replacings, etc.
        assert queryStringVariants != null;
        assert requestParameterMapVariants != null;
        assert headerMapVariants != null;
        assert cookieMapVariants != null;

        // =====
        // check if any of the request definitions match
        boolean hasAtLeastOneMatchingServletPath = false; // only used for standard rule files (custom request matchers are out of scope for this flag)
        final List/*<RequestDefinition>*/ results = new ArrayList();
        for (final Iterator iter = this.definitions.iterator(); iter.hasNext();) {
            final RequestDefinition requestDefinition = (RequestDefinition) iter.next();
            if (!requestDefinition.isEnabled()) {
                continue; // short-circuit to ignore this disabled rule: continue with next request-definition to check
            }

            Logger.getLogger(RequestDefinitionContainer.class.getName()).log(Level.FINEST, "{0} start of rule '{1}'", new Object[]{System.currentTimeMillis(), requestDefinition.getIdentification()});

            // now decide if a standard rule file or a custom-request-matcher based rule file should be worked on
            if (requestDefinition.isHavingCustomRequestMatcher()) { //= custom-request-matcher based rule file @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                final CustomRequestMatcher customRequestMatcher = requestDefinition.getCustomRequestMatcher();
                if (customRequestMatcher.isRequestMatching(request, clientAddress, country)) {
                    results.add(requestDefinition);
                    if (returnOnlyTheFirstMatchingDefinition) {
                        return (RequestDefinition[]) results.toArray(new RequestDefinition[0]);
                    }
                } else {
                    continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                }
            } else { //= standard rule file @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                final Pattern servletPathPattern = requestDefinition.getServletPathPattern();
                if (WordMatchingUtils.matchesWord(requestDefinition.getServletPathPrefilter(), servletPath, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && servletPathPattern.matcher(servletPath).find() == !requestDefinition.isServletPathPatternNegated()) {
                    //if (DEBUG) System.out.println("Checking: "+requestDefinition.getIdentification());
                    hasAtLeastOneMatchingServletPath = true;

                    // check the optional context-path pattern ########################################################################
                    final Pattern contextPathPattern = requestDefinition.getContextPathPattern();
                    if (contextPathPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getContextPathPrefilter(), contextPath, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && contextPathPattern.matcher(contextPath).find() == requestDefinition.isContextPathPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional country pattern ########################################################################
                    final Pattern countryPattern = requestDefinition.getCountryPattern();
                    if (countryPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getCountryPrefilter(), country, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && countryPattern.matcher(country).find() == requestDefinition.isCountryPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional path-info pattern ########################################################################
                    final Pattern pathInfoPattern = requestDefinition.getPathInfoPattern();
                    if (pathInfoPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getPathInfoPrefilter(), pathInfo, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && pathInfoPattern.matcher(pathInfo).find() == requestDefinition.isPathInfoPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional path-info-translated pattern ########################################################################
                    final Pattern pathTranslatedPattern = requestDefinition.getPathTranslatedPattern();
                    if (pathTranslatedPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getPathTranslatedPrefilter(), pathTranslated, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && pathTranslatedPattern.matcher(pathTranslated).find() == requestDefinition.isPathTranslatedPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional client-address pattern ########################################################################
                    final Pattern clientAddressPattern = requestDefinition.getRemoteAddrPattern();
                    if (clientAddressPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getRemoteAddrPrefilter(), clientAddress, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && clientAddressPattern.matcher(clientAddress).find() == requestDefinition.isRemoteAddrPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional client-host pattern ########################################################################
                    final Pattern remoteHostPattern = requestDefinition.getRemoteHostPattern();
                    if (remoteHostPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getRemoteHostPrefilter(), remoteHost, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && remoteHostPattern.matcher(remoteHost).find() == requestDefinition.isRemoteHostPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional client-port pattern ########################################################################
                    final Pattern remotePortPattern = requestDefinition.getRemotePortPattern();
                    if (remotePortPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getRemotePortPrefilter(), "" + remotePort, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && remotePortPattern.matcher("" + remotePort).find() == requestDefinition.isRemotePortPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional client-user pattern ########################################################################
                    final Pattern remoteUserPattern = requestDefinition.getRemoteUserPattern();
                    if (remoteUserPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getRemoteUserPrefilter(), remoteUser, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && remoteUserPattern.matcher(remoteUser).find() == requestDefinition.isRemoteUserPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional time pattern ########################################################################
                    final Pattern timePattern = requestDefinition.getTimePattern();
                    if (timePattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getTimePrefilter(), time, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && timePattern.matcher(time).find() == requestDefinition.isTimePatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }
                    // check the optional time-year pattern ########################################################################
                    final Pattern timeYearPattern = requestDefinition.getTimeYearPattern();
                    if (timeYearPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getTimeYearPrefilter(), timeYear, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && timeYearPattern.matcher(timeYear).find() == requestDefinition.isTimeYearPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }
                    // check the optional time-moth pattern ########################################################################
                    final Pattern timeMonthPattern = requestDefinition.getTimeMonthPattern();
                    if (timeMonthPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getTimeMonthPrefilter(), timeMonth, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && timeMonthPattern.matcher(timeMonth).find() == requestDefinition.isTimeMonthPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }
                    // check the optional time-day pattern ########################################################################
                    final Pattern timeDayPattern = requestDefinition.getTimeDayPattern();
                    if (timeDayPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getTimeDayPrefilter(), timeDay, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && timeDayPattern.matcher(timeDay).find() == requestDefinition.isTimeDayPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }
                    // check the optional time-hour pattern ########################################################################
                    final Pattern timeHourPattern = requestDefinition.getTimeHourPattern();
                    if (timeHourPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getTimeHourPrefilter(), timeHour, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && timeHourPattern.matcher(timeHour).find() == requestDefinition.isTimeHourPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }
                    // check the optional time-minute pattern ########################################################################
                    final Pattern timeMinutePattern = requestDefinition.getTimeMinutePattern();
                    if (timeMinutePattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getTimeMinutePrefilter(), timeMinute, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && timeMinutePattern.matcher(timeMinute).find() == requestDefinition.isTimeMinutePatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }
                    // check the optional time-second pattern ########################################################################
                    final Pattern timeSecondPattern = requestDefinition.getTimeSecondPattern();
                    if (timeSecondPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getTimeSecondPrefilter(), timeSecond, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && timeSecondPattern.matcher(timeSecond).find() == requestDefinition.isTimeSecondPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }
                    // check the optional time-weekday pattern ########################################################################
                    final Pattern timeWeekdayPattern = requestDefinition.getTimeWeekdayPattern();
                    if (timeWeekdayPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getTimeWeekdayPrefilter(), timeWeekday, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && timeWeekdayPattern.matcher(timeWeekday).find() == requestDefinition.isTimeWeekdayPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional header-count pattern ######################################################################## using matches() for the counts
                    final Pattern headerCountPattern = requestDefinition.getHeaderCountPattern(null); // null as key is used for the expression matching the total value count
                    if (headerCountPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getHeaderCountPrefilter(null), "" + totalHeaderValueCount, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && headerCountPattern.matcher("" + totalHeaderValueCount).matches() == requestDefinition.isHeaderCountPatternNegated(null))) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }
                    {
                        boolean continueWithNextRequestDefinition = false;
                        for (final Iterator it = requestDefinition.getHeaderCountPatternNamesUppercased().iterator(); it.hasNext();) {
                            final String name = (String) it.next();
                            if (name != null) { // since "null as key" has only the header expression that should be matched against all headers and not only against a named one (see above)
                                final Permutation[] values = (Permutation[]) headerMapVariants.get(name);
                                final int count = values == null ? 0 : values.length;
                                final Pattern pattern = requestDefinition.getHeaderCountPattern(name);
                                final boolean match = WordMatchingUtils.matchesWord(requestDefinition.getHeaderCountPrefilter(name), "" + count, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && pattern.matcher("" + count).matches() != requestDefinition.isHeaderCountPatternNegated(name);
                                // since ALL header conditions must match, we can safely assume a valid (good) request when at least one header condition does not match
                                if (!match) {
                                    continueWithNextRequestDefinition = true;
                                    break;
                                }
                            }
                        }
                        if (continueWithNextRequestDefinition) {
                            continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                        }
                    }
                    // check the optional header-name-list pattern ########################################################################
                    final Pattern headerNameListPattern = requestDefinition.getHeaderNameListPattern();
                    if (headerNameListPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getHeaderNameListPrefilter(), headerNameList, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && headerNameListPattern.matcher(headerNameList).find() == requestDefinition.isHeaderNameListPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional cookie-count pattern ######################################################################## using matches() for the counts
                    final Pattern cookieCountPattern = requestDefinition.getCookieCountPattern(null); // null as key is used for the expression matching the total value count
                    if (cookieCountPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getCookieCountPrefilter(null), "" + totalCookieValueCount, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && cookieCountPattern.matcher("" + totalCookieValueCount).matches() == requestDefinition.isCookieCountPatternNegated(null))) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }
                    {
                        boolean continueWithNextRequestDefinition = false;
                        for (final Iterator it = requestDefinition.getCookieCountPatternNamesUppercased().iterator(); it.hasNext();) {
                            final String name = (String) it.next();
                            if (name != null) { // since "null as key" has only the cookie expression that should be matched against all cookies and not only against a named one (see above)
                                final Permutation[] values = (Permutation[]) cookieMapVariants.get(name);
                                final int count = values == null ? 0 : values.length;
                                final Pattern pattern = requestDefinition.getCookieCountPattern(name);
                                final boolean match = WordMatchingUtils.matchesWord(requestDefinition.getCookieCountPrefilter(name), "" + count, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && pattern.matcher("" + count).matches() != requestDefinition.isCookieCountPatternNegated(name);
                                // since ALL cookie conditions must match, we can safely assume a valid (good) request when at least one cookie condition does not match
                                if (!match) {
                                    continueWithNextRequestDefinition = true;
                                    break;
                                }
                            }
                        }
                        if (continueWithNextRequestDefinition) {
                            continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                        }
                    }
                    // check the optional cookie-name-list pattern ########################################################################
                    final Pattern cookieNameListPattern = requestDefinition.getCookieNameListPattern();
                    if (cookieNameListPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getCookieNameListPrefilter(), cookieNameList, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && cookieNameListPattern.matcher(cookieNameList).find() == requestDefinition.isCookieNameListPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional requestParam-count pattern ######################################################################## using matches() for the counts
                    final Pattern requestParamCountPattern = requestDefinition.getRequestParamCountPattern(null); // null as key is used for the expression matching the total value count
                    if (requestParamCountPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getRequestParamCountPrefilter(null), "" + totalRequestParamValueCountExcludingInternalParams, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && requestParamCountPattern.matcher("" + totalRequestParamValueCountExcludingInternalParams).matches() == requestDefinition.isRequestParamCountPatternNegated(null))) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }
                    {
                        boolean continueWithNextRequestDefinition = false;
                        for (final Iterator it = requestDefinition.getRequestParamCountPatternNames().iterator(); it.hasNext();) {
                            final String name = (String) it.next();
                            if (name != null) { // since "null as key" has only the RequestParam expression that should be matched against all RequestParams and not only against a named one (see above)
                                final Permutation[] values = (Permutation[]) requestParameterMapVariants.get(name);
                                final int count = values == null ? 0 : values.length;
                                final Pattern pattern = requestDefinition.getRequestParamCountPattern(name);
                                final boolean match = WordMatchingUtils.matchesWord(requestDefinition.getRequestParamCountPrefilter(name), "" + count, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && pattern.matcher("" + count).matches() != requestDefinition.isRequestParamCountPatternNegated(name);
                                // since ALL RequestParam conditions must match, we can safely assume a valid (good) request when at least one RequestParam condition does not match
                                if (!match) {
                                    continueWithNextRequestDefinition = true;
                                    break;
                                }
                            }
                        }
                        if (continueWithNextRequestDefinition) {
                            continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                        }
                    }
                    // check the optional requestParam-name-list pattern ########################################################################
                    final Pattern requestParamNameListPattern = requestDefinition.getRequestParamNameListPattern();
                    if (requestParamNameListPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getRequestParamNameListPrefilter(), requestParamNameListExcludingInternalParams, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && requestParamNameListPattern.matcher(requestParamNameListExcludingInternalParams).find() == requestDefinition.isRequestParamNameListPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional auth-type pattern ########################################################################
                    final Pattern authTypePattern = requestDefinition.getAuthTypePattern();
                    if (authTypePattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getAuthTypePrefilter(), authType, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && authTypePattern.matcher(authType).find() == requestDefinition.isAuthTypePatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional scheme pattern ########################################################################
                    final Pattern schemePattern = requestDefinition.getSchemePattern();
                    if (schemePattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getSchemePrefilter(), scheme, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && schemePattern.matcher(scheme).find() == requestDefinition.isSchemePatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional method pattern ########################################################################
                    final Pattern methodPattern = requestDefinition.getMethodPattern();
                    if (methodPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getMethodPrefilter(), method, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && methodPattern.matcher(method).find() == requestDefinition.isMethodPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional protocol pattern ########################################################################
                    final Pattern protocolPattern = requestDefinition.getProtocolPattern();
                    if (protocolPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getProtocolPrefilter(), protocol, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && protocolPattern.matcher(protocol).find() == requestDefinition.isProtocolPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional mimeType pattern ########################################################################
                    final Pattern mimeTypePattern = requestDefinition.getMimeTypePattern();
                    if (mimeTypePattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getMimeTypePrefilter(), mimeType, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && mimeTypePattern.matcher(mimeType).find() == requestDefinition.isMimeTypePatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional encoding pattern ########################################################################
                    final Pattern encodingPattern = requestDefinition.getEncodingPattern();
                    if (encodingPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getEncodingPrefilter(), encoding, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && encodingPattern.matcher(encoding).find() == requestDefinition.isEncodingPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional contentLength pattern ########################################################################
                    final Pattern contentLengthPattern = requestDefinition.getContentLengthPattern();
                    if (contentLengthPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getContentLengthPrefilter(), "" + contentLength, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && contentLengthPattern.matcher("" + contentLength).find() == requestDefinition.isContentLengthPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional header patterns ########################################################################
                    {
                        // here (if defined) check the definition that should be matched against any headers:
                        final Pattern valuePatternForAnyHeader = requestDefinition.getHeaderValuePattern(null); // null as key is used for the expression which should be matched against any headers
                        if (valuePatternForAnyHeader != null) {
                            final WordDictionary prefilter = requestDefinition.getHeaderValuePrefilter(null); // null as key is used for the expression which should be matched against any headers
                            final Matcher matcher = valuePatternForAnyHeader.matcher("");
                            final boolean expectation = !requestDefinition.isHeaderValuePatternNegated(null); // null as special key for "any header name"
                            // Try to match all headers against the expression, and short-circuit to flag as valid (good) request only
                            // when not even a single match occurred (if matchingSingleValueIsEnough)
                            boolean foundMatch = !matchingSingleValueIsEnough;
                            for (final Iterator it = headerMapVariants.values().iterator(); it.hasNext();) {
                                final Permutation[] values = (Permutation[]) it.next();
                                for (Permutation value : values) {
                                    if (ServerUtils.isVariantMatching(value, prefilter, matcher, this.nonStandardPermutationsAllowed) == expectation) {
                                        if (matchingSingleValueIsEnough) {
                                            foundMatch = true;
                                            break;
                                        }
                                    } else {
                                        if (!matchingSingleValueIsEnough) {
                                            foundMatch = false;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!foundMatch) {
                                continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                            }
                        }
                        // now those definitions that have named headers
                        boolean continueWithNextRequestDefinition = false;
                        for (final Iterator it = requestDefinition.getHeaderValuePatternNamesUppercased().iterator(); it.hasNext();) {
                            final String headerName = (String) it.next();
                            if (headerName != null) { // since "null as key" has only the header expression that should be matched against all headers and not only against a named one (see above)
                                Permutation[] headerValues = (Permutation[]) headerMapVariants.get(headerName);
                                if (headerValues == null) {
                                    headerValues = new Permutation[]{permutationWithEmptyString};
                                }
                                // OK, the request has indeed a potentially header, so check if the condition matches (on each individual value for that header)
                                final WordDictionary prefilter = requestDefinition.getHeaderValuePrefilter(headerName);
                                final Matcher matcher = requestDefinition.getHeaderValuePattern(headerName).matcher("");
                                final boolean expectation = !requestDefinition.isHeaderValuePatternNegated(headerName);
                                boolean match = !matchingSingleValueIsEnough;
                                for (Permutation headerValue : headerValues) {
                                    if (ServerUtils.isVariantMatching(headerValue, prefilter, matcher, this.nonStandardPermutationsAllowed) == expectation) {
                                        if (matchingSingleValueIsEnough) {
                                            match = true;
                                            break;
                                        }
                                    } else {
                                        if (!matchingSingleValueIsEnough) {
                                            match = false;
                                            break;
                                        }
                                    }
                                }
                                // since ALL header conditions must match, we can safely assume a valid (good) request when at least one header condition does not match
                                if (!match) {
                                    continueWithNextRequestDefinition = true;
                                    break;
                                }
                            }
                        }
                        if (continueWithNextRequestDefinition) {
                            continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                        }
                    }

                    // check the optional requestURL pattern ########################################################################
                    final Pattern requestURLPattern = requestDefinition.getRequestURLPattern();
                    if (requestURLPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getRequestURLPrefilter(), requestURL, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && requestURLPattern.matcher(requestURL).find() == requestDefinition.isRequestURLPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional requestURI pattern ########################################################################
                    final Pattern requestURIPattern = requestDefinition.getRequestURIPattern();
                    if (requestURIPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getRequestURIPrefilter(), requestURI, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && requestURIPattern.matcher(requestURI).find() == requestDefinition.isRequestURIPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional serverName pattern ########################################################################
                    final Pattern serverNamePattern = requestDefinition.getServerNamePattern();
                    if (serverNamePattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getServerNamePrefilter(), serverName, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && serverNamePattern.matcher(serverName).find() == requestDefinition.isServerNamePatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional serverPort pattern ########################################################################
                    final Pattern serverPortPattern = requestDefinition.getServerPortPattern();
                    if (serverPortPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getServerPortPrefilter(), "" + serverPort, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && serverPortPattern.matcher("" + serverPort).find() == requestDefinition.isServerPortPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional localAddr pattern ########################################################################
                    final Pattern localAddrPattern = requestDefinition.getLocalAddrPattern();
                    if (localAddrPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getLocalAddrPrefilter(), localAddr, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && localAddrPattern.matcher(localAddr).find() == requestDefinition.isLocalAddrPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional localName pattern ########################################################################
                    final Pattern localNamePattern = requestDefinition.getLocalNamePattern();
                    if (localNamePattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getLocalNamePrefilter(), localName, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && localNamePattern.matcher(localName).find() == requestDefinition.isLocalNamePatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional localPort pattern ########################################################################
                    final Pattern localPortPattern = requestDefinition.getLocalPortPattern();
                    if (localPortPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getLocalPortPrefilter(), "" + localPort, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && localPortPattern.matcher("" + localPort).find() == requestDefinition.isLocalPortPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional cookie patterns ########################################################################
                    {
                        // here (if defined) check the definition that should be matched against any cookies:
                        final Pattern valuePatternForAnyCookie = requestDefinition.getCookieValuePattern(null); // null as key is used for the expression which should be matched against any cookies
                        if (valuePatternForAnyCookie != null) {
                            final WordDictionary prefilter = requestDefinition.getCookieValuePrefilter(null); // null as key is used for the expression which should be matched against any cookies
                            final Matcher matcher = valuePatternForAnyCookie.matcher("");
                            final boolean expectation = !requestDefinition.isCookieValuePatternNegated(null); // null as special key for "any cookie name"
                            // Try to match all cookies against the expression, and short-circuit to flag as valid (good) request only
                            // when not even a single match occurred (if matchingSingleValueIsEnough)
                            boolean foundMatch = !matchingSingleValueIsEnough;
                            for (final Iterator it = cookieMapVariants.values().iterator(); it.hasNext();) {
                                final Permutation[] values = (Permutation[]) it.next();
                                for (Permutation value : values) {
                                    if (ServerUtils.isVariantMatching(value, prefilter, matcher, this.nonStandardPermutationsAllowed) == expectation) {
                                        if (matchingSingleValueIsEnough) {
                                            foundMatch = true;
                                            break;
                                        }
                                    } else {
                                        if (!matchingSingleValueIsEnough) {
                                            foundMatch = false;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!foundMatch) {
                                continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                            }
                        }
                        // now those definitions that have named cookies
                        boolean continueWithNextRequestDefinition = false;
                        for (final Iterator it = requestDefinition.getCookieValuePatternNamesUppercased().iterator(); it.hasNext();) {
                            final String cookieName = (String) it.next();
                            if (cookieName != null) { // since "null as key" has only the cookie expression that should be matched against all cookies and not only against a named one (see above)
                                Permutation[] cookieValues = (Permutation[]) cookieMapVariants.get(cookieName);
                                if (cookieValues == null) {
                                    cookieValues = new Permutation[]{permutationWithEmptyString};
                                }
                                // OK, the request has indeed a potentially cookie, so check if the condition matches (on each individual value for that cookie)
                                final WordDictionary prefilter = requestDefinition.getCookieValuePrefilter(cookieName);
                                final Matcher matcher = requestDefinition.getCookieValuePattern(cookieName).matcher("");
                                final boolean expectation = !requestDefinition.isCookieValuePatternNegated(cookieName);
                                boolean match = !matchingSingleValueIsEnough;
                                for (Permutation cookieValue : cookieValues) {
                                    if (ServerUtils.isVariantMatching(cookieValue, prefilter, matcher, this.nonStandardPermutationsAllowed) == expectation) {
                                        if (matchingSingleValueIsEnough) {
                                            match = true;
                                            break;
                                        }
                                    } else {
                                        if (!matchingSingleValueIsEnough) {
                                            match = false;
                                            break;
                                        }
                                    }
                                }
                                // since ALL cookie conditions must match, we can safely assume a valid (good) request when at least one cookie condition does not match
                                if (!match) {
                                    continueWithNextRequestDefinition = true;
                                    break;
                                }
                            }
                        }
                        if (continueWithNextRequestDefinition) {
                            continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                        }
                    }

                    // check the optional requestedSessionId pattern ########################################################################
                    final Pattern requestedSessionIdPattern = requestDefinition.getRequestedSessionIdPattern();
                    if (requestedSessionIdPattern != null && (WordMatchingUtils.matchesWord(requestDefinition.getRequestedSessionIdPrefilter(), requestedSessionId, WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && requestedSessionIdPattern.matcher(requestedSessionId).find() == requestDefinition.isRequestedSessionIdPatternNegated())) {
                        continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                    }

                    // check the optional query-string pattern and its decoded variants ########################################################################
                    final Pattern queryStringPattern = requestDefinition.getQueryStringPattern();
                    if (queryStringPattern == null // = no queryString pattern defined, so don't check for it; i.e. it is a match
                            || ServerUtils.isVariantMatching(queryStringVariants, requestDefinition.getQueryStringPrefilter(), queryStringPattern.matcher(""), this.nonStandardPermutationsAllowed) != requestDefinition.isQueryStringPatternNegated()) {
                        // if there are parameter conditions for this servlet path check them too; otherwise directly flag as request
                        // here (if defined) check the definition that should be matched against any request params:
                        final Pattern requestValuePatternForAnyParameter = requestDefinition.getRequestParamValuePattern(null); // null as key is used for the expression which should be matched against any parameters
                        if (requestValuePatternForAnyParameter != null) {
                            final WordDictionary prefilter = requestDefinition.getRequestParamValuePrefilter(null); // null as key is used for the expression which should be matched against any parameters
                            final Matcher matcher = requestValuePatternForAnyParameter.matcher("");
                            final boolean expectation = !requestDefinition.isRequestParamValuePatternNegated(null); // null as special key for "any parameter name"
                            // Try to match all parameters against the expression, and short-circuit to flag as valid (good) request only
                            // when not even a single match occurred (if matchingSingleValueIsEnough)
                            boolean foundMatch = !matchingSingleValueIsEnough;
                            for (final Iterator it = requestParameterMapVariants.values().iterator(); it.hasNext();) {
                                final Permutation[] values = (Permutation[]) it.next();
                                for (Permutation value : values) {
                                    if (ServerUtils.isVariantMatching(value, prefilter, matcher, this.nonStandardPermutationsAllowed) == expectation) {
                                        if (matchingSingleValueIsEnough) {
                                            foundMatch = true;
                                            break;
                                        }
                                    } else {
                                        if (!matchingSingleValueIsEnough) {
                                            foundMatch = false;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!foundMatch) {
                                continue; // short-circuit to recognize as a valid request: continue with next request-definition to check
                            }
                        }
                        // now those definitions that have named request params
                        boolean continueWithNextRequestDefinition = false;
                        for (final Iterator it = requestDefinition.getRequestParamValuePatternNames().iterator(); it.hasNext();) {
                            final String parameterName = (String) it.next();
                            if (parameterName != null) { // since "null as key" has only the requestParam expression that should be matched against all request parameters and not only against a named one (see above)
                                Permutation[] requestParameterValues = (Permutation[]) requestParameterMapVariants.get(parameterName);
                                if (requestParameterValues == null) {
                                    requestParameterValues = new Permutation[]{permutationWithEmptyString};
                                }
                                // OK, the request has indeed a potentially parameter, so check if the condition matches (on each individual value for that parameter)
                                final WordDictionary prefilter = requestDefinition.getRequestParamValuePrefilter(parameterName);
                                final Matcher matcher = requestDefinition.getRequestParamValuePattern(parameterName).matcher("");
                                final boolean expectation = !requestDefinition.isRequestParamValuePatternNegated(parameterName);
                                boolean match = !matchingSingleValueIsEnough;
                                for (Permutation requestParameterValue : requestParameterValues) {
                                    if (ServerUtils.isVariantMatching(requestParameterValue, prefilter, matcher, this.nonStandardPermutationsAllowed) == expectation) {
                                        if (matchingSingleValueIsEnough) {
                                            match = true;
                                            break;
                                        }
                                    } else {
                                        if (!matchingSingleValueIsEnough) {
                                            match = false;
                                            break;
                                        }
                                    }
                                }
                                // since ALL parameter conditions must match, we can safely assume a valid (good) request when at least one parameter condition does not match
                                if (!match) {
                                    continueWithNextRequestDefinition = true;
                                    break;
                                }
                            }
                        }
                        if (continueWithNextRequestDefinition) {
                            continue; // short-circuit to recognize as a valid request: continue with next request-definition to check to seek further for a match
                        }

                        // when we've come here, flag as matching request
                        assert requestDefinition != null;
                        // if (DEBUG) System.out.println("Flagged as matching request according to rule: "+requestDefinition.getDescription()); // = MATCHING REQUEST: since all parameter conditions (if any) matched
                        results.add(requestDefinition);
                        if (returnOnlyTheFirstMatchingDefinition) {
                            return (RequestDefinition[]) results.toArray(new RequestDefinition[0]); // shortcut to stop after the first match since only the first match is desired
                        }
                    }

                }
            }

        }

        // special case if a non-matching of any servletPath configured shall be treated as a match (mostly useful for auto-flipping of whitelist rules)
        if (treatNonMatchingServletPathAsMatch && !hasAtLeastOneMatchingServletPath) {
            results.add(this.defaultMatch);
        }
        // return all matches
        return (RequestDefinition[]) results.toArray(new RequestDefinition[0]); // empty = no match // filled = matches
    }

    protected static final String commaDelimitedList(final Collection/*<String>*/ collection) {
        final StringBuilder result = new StringBuilder();
        for (final Iterator/*<String>*/ iter = collection.iterator(); iter.hasNext();) {
            final String element = (String) iter.next();
            if (element != null) {
                result.append(element);
                if (iter.hasNext()) {
                    result.append(',');
                }
            }
        }
        return result.toString();
    }

    protected static final String sortedNameList(final Set/*<String>*/ keys) {
        return commaDelimitedList(new TreeSet(keys));
    }

    protected static final String sortedValueList(final Collection/*<String[]>*/ values) {
        final SortedSet/*<String>*/ elements = new TreeSet();
        for (final Iterator/*<String[]>*/ iter = values.iterator(); iter.hasNext();) {
            final String[] value = (String[]) iter.next();
            if (value != null && value.length > 0) {
                for (String value1 : value) {
                    elements.add(value1);
                }
            }
        }
        return commaDelimitedList(elements);
    }

    /**
     * creates the concrete rule-definition instance
     */
    protected abstract /*"T" anstatt RequestDefinition*/ RequestDefinition createRequestDefinition(boolean enabled, String identification, String description, CustomRequestMatcher customRequestMatcher);

    /**
     * creates the concrete rule-definition instance
     */
    protected abstract /*"T" anstatt RequestDefinition*/ RequestDefinition createRequestDefinition(boolean enabled, String identification, String description, WordDictionary servletPathPrefilter, Pattern servletPathPattern, boolean servletPathPatternNegated);

    /**
     * Can be overridden to extract custom properties for the concrete
     * rule-definition instance. It is important that implementations of this
     * method remove any used custom property keys from the given properties
     * object, so that the syntax checking of proper rule definition key names
     * still works and catches misspelled (unknown) names.
     */
    protected abstract void extractAndRemoveSpecificProperties(/*"T" anstatt RequestDefinition*/RequestDefinition requestDefinition, Properties properties) throws IllegalRuleDefinitionFormatException;

    private int getTotalValueCount(final Map/*<String,Object[]>*/ variants) {
        if (variants == null) {
            return 0;
        }
        int total = 0;
        for (final Iterator/*<Object[]>*/ iter = variants.values().iterator(); iter.hasNext();) {
            final Object[] values = (Object[]) iter.next(); // Object[] since it can be String[] as well as Permutation[]
            if (values != null) {
                total += values.length;
            }
        }
        return total;
    }

}
