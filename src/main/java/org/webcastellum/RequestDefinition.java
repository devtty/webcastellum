package org.webcastellum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class RequestDefinition extends AbstractDefinition {
    
    
    private CustomRequestMatcher customRequestMatcher;
    
    private WordDictionary servletPathPrefilter;
    private Pattern servletPathPattern;
    private boolean servletPathPatternNegated;
    

    private final Map/*<String,Pattern>*/ requestParamName2ValuePattern = new HashMap(); // null is used as key for "any parameter"
    private final Map/*<String,WordDictionary>*/ requestParamName2ValuePrefilter = new HashMap(); // null is used as key for "any parameter"
    private final Map/*<String,Pattern>*/ requestParamName2CountPattern = new HashMap(); // null is used as key for "any parameter"
    private final Map/*<String,WordDictionary>*/ requestParamName2CountPrefilter = new HashMap(); // null is used as key for "any parameter"
    private final List/*<String>*/ requestParamNamesValueNegated = new ArrayList(); // null is used as name for the "any parameter"-property
    private final List/*<String>*/ requestParamNamesCountNegated = new ArrayList(); // null is used as name for the "any parameter"-property
    private Pattern requestParamNameListPattern; 
    private WordDictionary requestParamNameListPrefilter; 
    private boolean requestParamNameListPatternNegated; 

    private final Map/*<String,Pattern>*/ headerName2ValuePattern = new HashMap(); // null is used as key for "any header"
    private final Map/*<String,WordDictionary>*/ headerName2ValuePrefilter = new HashMap(); // null is used as key for "any header"
    private final Map/*<String,Pattern>*/ headerName2CountPattern = new HashMap(); // null is used as key for "any header"
    private final Map/*<String,WordDictionary>*/ headerName2CountPrefilter = new HashMap(); // null is used as key for "any header"
    private final List/*<String>*/ headerNamesValueNegated = new ArrayList(); // null is used as name for the "any header"-property
    private final List/*<String>*/ headerNamesCountNegated = new ArrayList(); // null is used as name for the "any header"-property
    private Pattern headerNameListPattern;
    private WordDictionary headerNameListPrefilter;
    private boolean headerNameListPatternNegated;

    private final Map/*<String,Pattern>*/ cookieName2ValuePattern = new HashMap(); // null is used as key for "any cookie"
    private final Map/*<String,WordDictionary>*/ cookieName2ValuePrefilter = new HashMap(); // null is used as key for "any cookie"
    private final Map/*<String,Pattern>*/ cookieName2CountPattern = new HashMap(); // null is used as key for "any cookie"
    private final Map/*<String,WordDictionary>*/ cookieName2CountPrefilter = new HashMap(); // null is used as key for "any cookie"
    private final List/*<String>*/ cookieNamesValueNegated = new ArrayList(); // null is used as name for the "any cookie"-property
    private final List/*<String>*/ cookieNamesCountNegated = new ArrayList(); // null is used as name for the "any cookie"-property
    private Pattern cookieNameListPattern;
    private WordDictionary cookieNameListPrefilter;
    private boolean cookieNameListPatternNegated;

    
    private WordDictionary queryStringPrefilter, 
            methodPrefilter, protocolPrefilter, mimeTypePrefilter, 
            remoteAddrPrefilter, remoteHostPrefilter, remotePortPrefilter, remoteUserPrefilter, 
            requestedSessionIdPrefilter, authTypePrefilter, pathInfoPrefilter, pathTranslatedPrefilter,
            contextPathPrefilter, requestURLPrefilter, requestURIPrefilter, encodingPrefilter, contentLengthPrefilter,
            schemePrefilter, serverNamePrefilter, serverPortPrefilter, localNamePrefilter, localAddrPrefilter, localPortPrefilter,
            timePrefilter, timeYearPrefilter, timeMonthPrefilter, timeDayPrefilter, timeHourPrefilter, timeMinutePrefilter, timeSecondPrefilter, timeWeekdayPrefilter, countryPrefilter;

    private Pattern queryStringPattern, 
            methodPattern, protocolPattern, mimeTypePattern, 
            remoteAddrPattern, remoteHostPattern, remotePortPattern, remoteUserPattern, 
            requestedSessionIdPattern, authTypePattern, pathInfoPattern, pathTranslatedPattern,
            contextPathPattern, requestURLPattern, requestURIPattern, encodingPattern, contentLengthPattern,
            schemePattern, serverNamePattern, serverPortPattern, localNamePattern, localAddrPattern, localPortPattern,
            timePattern, timeYearPattern, timeMonthPattern, timeDayPattern, timeHourPattern, timeMinutePattern, timeSecondPattern, timeWeekdayPattern, countryPattern;

    private boolean queryStringPatternNegated, 
            methodPatternNegated, protocolPatternNegated, mimeTypePatternNegated, 
            remoteAddrPatternNegated, remoteHostPatternNegated, remotePortPatternNegated, remoteUserPatternNegated, 
            requestedSessionIdPatternNegated, authTypePatternNegated, pathInfoPatternNegated, pathTranslatedPatternNegated,
            contextPathPatternNegated, requestURLPatternNegated, requestURIPatternNegated, encodingPatternNegated, contentLengthPatternNegated,
            schemePatternNegated, serverNamePatternNegated, serverPortPatternNegated, localNamePatternNegated, localAddrPatternNegated, localPortPatternNegated,
            timePatternNegated, timeYearPatternNegated, timeMonthPatternNegated, timeDayPatternNegated, timeHourPatternNegated, timeMinutePatternNegated, countryPatternNegated, 
            timeSecondPatternNegated, timeWeekdayPatternNegated;
    
    
    
    
    
    protected RequestDefinition(final boolean enabled, final String identification, final String description,       final CustomRequestMatcher customRequestMatcher) {
        super(enabled, identification, description);
        if (customRequestMatcher == null) throw new NullPointerException("customRequestMatcher must not be null");
        this.customRequestMatcher = customRequestMatcher;
    }

    
    protected RequestDefinition(final boolean enabled, final String identification, final String description,       final WordDictionary servletPathPrefilter, final Pattern servletPathPattern, final boolean servletPathPatternNegated) {
        super(enabled, identification, description);
        this.servletPathPrefilter = servletPathPrefilter; // nullable
        if (servletPathPattern == null) throw new NullPointerException("servletPathPattern must not be null");
        this.servletPathPattern = servletPathPattern;
        this.servletPathPatternNegated = servletPathPatternNegated;
    }

    
    
    
    
    
    
    
    
    
    
    public final boolean isHavingCustomRequestMatcher() {
        return this.customRequestMatcher != null;
    }
    
    public final CustomRequestMatcher getCustomRequestMatcher() {
        return customRequestMatcher;
    }
        
    
    
    
    
    public final WordDictionary getServletPathPrefilter() {
        return servletPathPrefilter;
    }
    
    public final Pattern getServletPathPattern() {
        return servletPathPattern;
    }
    public final boolean isServletPathPatternNegated() {
        return this.servletPathPatternNegated;
    }
    
    
    
    
    
/*OLD
    public final Pattern getCookieCountPattern() {
        return cookieCountPattern;
    }
    public final boolean isCookieCountPatternNegated() {
        return this.cookieCountPatternNegated;
    }
    protected final void setCookieCountPattern(final Pattern cookieCountPattern, final boolean negated) {
        this.cookieCountPattern = cookieCountPattern;
        this.cookieCountPatternNegated = negated;
    }*/

    
    public final Pattern getCookieNameListPattern() {
        return cookieNameListPattern;
    }
    public final boolean isCookieNameListPatternNegated() {
        return this.cookieNameListPatternNegated;
    }
    protected final void setCookieNameListPattern(final Pattern cookieNameListPattern, final boolean negated) {
        this.cookieNameListPattern = cookieNameListPattern;
        this.cookieNameListPatternNegated = negated;
    }
        
/*OLD
    public final Pattern getCookieValueListPattern() {
        return cookieValueListPattern;
    }
    public final boolean isCookieValueListPatternNegated() {
        return this.cookieValueListPatternNegated;
    }
    protected final void setCookieValueListPattern(final Pattern cookieValueListPattern, final boolean negated) {
        this.cookieValueListPattern = cookieValueListPattern;
        this.cookieValueListPatternNegated = negated;
    }*/
    
    
    
    
    
/*OLD
    public final Pattern getHeaderCountPattern() {
        return headerCountPattern;
    }
    public final boolean isHeaderCountPatternNegated() {
        return this.headerCountPatternNegated;
    }
    protected final void setHeaderCountPattern(final Pattern headerCountPattern, final boolean negated) {
        this.headerCountPattern = headerCountPattern;
        this.headerCountPatternNegated = negated;
    }*/
    

    public final Pattern getHeaderNameListPattern() {
        return headerNameListPattern;
    }
    public final boolean isHeaderNameListPatternNegated() {
        return this.headerNameListPatternNegated;
    }
    protected final void setHeaderNameListPattern(final Pattern headerNameListPattern, final boolean negated) {
        this.headerNameListPattern = headerNameListPattern;
        this.headerNameListPatternNegated = negated;
    }
    
/*OLD
    public final Pattern getHeaderValueListPattern() {
        return headerValueListPattern;
    }
    public final boolean isHeaderValueListPatternNegated() {
        return this.headerValueListPatternNegated;
    }
    protected final void setHeaderValueListPattern(final Pattern headerValueListPattern, final boolean negated) {
        this.headerValueListPattern = headerValueListPattern;
        this.headerValueListPatternNegated = negated;
    }*/
    
    
    
    
/*OLD
    public final Pattern getRequestParamCountPattern() {
        return requestParamCountPattern;
    }
    public final boolean isRequestParamCountPatternNegated() {
        return this.requestParamCountPatternNegated;
    }
    protected final void setRequestParamCountPattern(final Pattern requestParamCountPattern, final boolean negated) {
        this.requestParamCountPattern = requestParamCountPattern;
        this.requestParamCountPatternNegated = negated;
    }*/


    public final Pattern getRequestParamNameListPattern() {
        return requestParamNameListPattern;
    }
    public final boolean isRequestParamNameListPatternNegated() {
        return this.requestParamNameListPatternNegated;
    }
    protected final void setRequestParamNameListPattern(final Pattern requestParamNameListPattern, final boolean negated) {
        this.requestParamNameListPattern = requestParamNameListPattern;
        this.requestParamNameListPatternNegated = negated;
    }
    
/*OLD
    public final Pattern getRequestParamValueListPattern() {
        return requestParamValueListPattern;
    }
    public final boolean isRequestParamValueListPatternNegated() {
        return this.requestParamValueListPatternNegated;
    }
    protected final void setRequestParamValueListPattern(final Pattern requestParamValueListPattern, final boolean negated) {
        this.requestParamValueListPattern = requestParamValueListPattern;
        this.requestParamValueListPatternNegated = negated;
    }*/
        

    
    
    
    

/*OLD
    public final Pattern getRequestParamCountExcludingInternalParamsPattern() {
        return requestParamCountExcludingInternalParamsPattern;
    }
    public final boolean isRequestParamCountExcludingInternalParamsPatternNegated() {
        return this.requestParamCountExcludingInternalParamsPatternNegated;
    }
    protected final void setRequestParamCountExcludingInternalParamsPattern(final Pattern requestParamCountExcludingInternalParamsPattern, final boolean negated) {
        this.requestParamCountExcludingInternalParamsPattern = requestParamCountExcludingInternalParamsPattern;
        this.requestParamCountExcludingInternalParamsPatternNegated = negated;
    }


    public final Pattern getRequestParamNameListExcludingInternalParamsPattern() {
        return requestParamNameListExcludingInternalParamsPattern;
    }
    public final boolean isRequestParamNameListExcludingInternalParamsPatternNegated() {
        return this.requestParamNameListExcludingInternalParamsPatternNegated;
    }
    protected final void setRequestParamNameListExcludingInternalParamsPattern(final Pattern requestParamNameListExcludingInternalParamsPattern, final boolean negated) {
        this.requestParamNameListExcludingInternalParamsPattern = requestParamNameListExcludingInternalParamsPattern;
        this.requestParamNameListExcludingInternalParamsPatternNegated = negated;
    }
    

    public final Pattern getRequestParamValueListExcludingInternalParamsPattern() {
        return requestParamValueListExcludingInternalParamsPattern;
    }
    public final boolean isRequestParamValueListExcludingInternalParamsPatternNegated() {
        return this.requestParamValueListExcludingInternalParamsPatternNegated;
    }
    protected final void setRequestParamValueListExcludingInternalParamsPattern(final Pattern requestParamValueListExcludingInternalParamsPattern, final boolean negated) {
        this.requestParamValueListExcludingInternalParamsPattern = requestParamValueListExcludingInternalParamsPattern;
        this.requestParamValueListExcludingInternalParamsPatternNegated = negated;
    }*/
            
    
    
    
    
    
    public final Pattern getMethodPattern() {
        return methodPattern;
    }
    public final boolean isMethodPatternNegated() {
        return this.methodPatternNegated;
    }
    protected final void setMethodPattern(final Pattern methodPattern, final boolean negated) {
        this.methodPattern = methodPattern;
        this.methodPatternNegated = negated;
    }

    
    
    
    

   
    public final Pattern getProtocolPattern() {
        return protocolPattern;
    }
    public final boolean isProtocolPatternNegated() {
        return this.protocolPatternNegated;
    }
    protected final void setProtocolPattern(final Pattern protocolPattern, final boolean negated) {
        this.protocolPattern = protocolPattern;
        this.protocolPatternNegated = negated;
    }

    
    
    
    

   
    public final Pattern getMimeTypePattern() {
        return mimeTypePattern;
    }
    public final boolean isMimeTypePatternNegated() {
        return this.mimeTypePatternNegated;
    }
    protected final void setMimeTypePattern(final Pattern mimeTypePattern, final boolean negated) {
        this.mimeTypePattern = mimeTypePattern;
        this.mimeTypePatternNegated = negated;
    }

    
    
    
    
    
    

    
    

   
    public final Pattern getTimePattern() {
        return timePattern;
    }
    public final boolean isTimePatternNegated() {
        return this.timePatternNegated;
    }
    protected final void setTimePattern(final Pattern timePattern, final boolean negated) {
        this.timePattern = timePattern;
        this.timePatternNegated = negated;
    }

    

   
    public final Pattern getTimeYearPattern() {
        return timeYearPattern;
    }
    public final boolean isTimeYearPatternNegated() {
        return this.timeYearPatternNegated;
    }
    protected final void setTimeYearPattern(final Pattern timeYearPattern, final boolean negated) {
        this.timeYearPattern = timeYearPattern;
        this.timeYearPatternNegated = negated;
    }

    
        
   
    public final Pattern getTimeMonthPattern() {
        return timeMonthPattern;
    }
    public final boolean isTimeMonthPatternNegated() {
        return this.timeMonthPatternNegated;
    }
    protected final void setTimeMonthPattern(final Pattern timeMonthPattern, final boolean negated) {
        this.timeMonthPattern = timeMonthPattern;
        this.timeMonthPatternNegated = negated;
    }

    
        
   
    public final Pattern getTimeDayPattern() {
        return timeDayPattern;
    }
    public final boolean isTimeDayPatternNegated() {
        return this.timeDayPatternNegated;
    }
    protected final void setTimeDayPattern(final Pattern timeDayPattern, final boolean negated) {
        this.timeDayPattern = timeDayPattern;
        this.timeDayPatternNegated = negated;
    }

    
        
   
    public final Pattern getTimeHourPattern() {
        return timeHourPattern;
    }
    public final boolean isTimeHourPatternNegated() {
        return this.timeHourPatternNegated;
    }
    protected final void setTimeHourPattern(final Pattern timeHourPattern, final boolean negated) {
        this.timeHourPattern = timeHourPattern;
        this.timeHourPatternNegated = negated;
    }

    
        
   
    public final Pattern getTimeMinutePattern() {
        return timeMinutePattern;
    }
    public final boolean isTimeMinutePatternNegated() {
        return this.timeMinutePatternNegated;
    }
    protected final void setTimeMinutePattern(final Pattern timeMinutePattern, final boolean negated) {
        this.timeMinutePattern = timeMinutePattern;
        this.timeMinutePatternNegated = negated;
    }

    
        
   
    public final Pattern getTimeSecondPattern() {
        return timeSecondPattern;
    }
    public final boolean isTimeSecondPatternNegated() {
        return this.timeSecondPatternNegated;
    }
    protected final void setTimeSecondPattern(final Pattern timeSecondPattern, final boolean negated) {
        this.timeSecondPattern = timeSecondPattern;
        this.timeSecondPatternNegated = negated;
    }

    
        
   
    public final Pattern getTimeWeekdayPattern() {
        return timeWeekdayPattern;
    }
    public final boolean isTimeWeekdayPatternNegated() {
        return this.timeWeekdayPatternNegated;
    }
    protected final void setTimeWeekdayPattern(final Pattern timeWeekdayPattern, final boolean negated) {
        this.timeWeekdayPattern = timeWeekdayPattern;
        this.timeWeekdayPatternNegated = negated;
    }

    

    
    

    
    
    
    public final Pattern getCountryPattern() {
        return countryPattern;
    }
    public final boolean isCountryPatternNegated() {
        return this.countryPatternNegated;
    }
    protected final void setCountryPattern(final Pattern countryPattern, final boolean negated) {
        this.countryPattern = countryPattern;
        this.countryPatternNegated = negated;
    }
    
    
    
    
    
    
    
    public final Pattern getRemoteAddrPattern() {
        return remoteAddrPattern;
    }
    public final boolean isRemoteAddrPatternNegated() {
        return this.remoteAddrPatternNegated;
    }
    protected final void setRemoteAddrPattern(final Pattern remoteAddrPattern, final boolean negated) {
        this.remoteAddrPattern = remoteAddrPattern;
        this.remoteAddrPatternNegated = negated;
    }

    
    public final Pattern getRemoteHostPattern() {
        return remoteHostPattern;
    }
    public final boolean isRemoteHostPatternNegated() {
        return this.remoteHostPatternNegated;
    }
    protected final void setRemoteHostPattern(final Pattern remoteHostPattern, final boolean negated) {
        this.remoteHostPattern = remoteHostPattern;
        this.remoteHostPatternNegated = negated;
    }

    
    public final Pattern getRemotePortPattern() {
        return remotePortPattern;
    }
    public final boolean isRemotePortPatternNegated() {
        return this.remotePortPatternNegated;
    }
    protected final void setRemotePortPattern(final Pattern remotePortPattern, final boolean negated) {
        this.remotePortPattern = remotePortPattern;
        this.remotePortPatternNegated = negated;
    }

    
    public final Pattern getRemoteUserPattern() {
        return remoteUserPattern;
    }
    public final boolean isRemoteUserPatternNegated() {
        return this.remoteUserPatternNegated;
    }
    protected final void setRemoteUserPattern(final Pattern remoteUserPattern, final boolean negated) {
        this.remoteUserPattern = remoteUserPattern;
        this.remoteUserPatternNegated = negated;
    }

    
    
    
    
    

    public final Pattern getAuthTypePattern() {
        return authTypePattern;
    }
    public final boolean isAuthTypePatternNegated() {
        return this.authTypePatternNegated;
    }
    protected final void setAuthTypePattern(final Pattern authTypePattern, final boolean negated) {
        this.authTypePattern = authTypePattern;
        this.authTypePatternNegated = negated;
    }

        
    
    

    public final Pattern getPathInfoPattern() {
        return pathInfoPattern;
    }
    public final boolean isPathInfoPatternNegated() {
        return this.pathInfoPatternNegated;
    }
    protected final void setPathInfoPattern(final Pattern pathInfoPattern, final boolean negated) {
        this.pathInfoPattern = pathInfoPattern;
        this.pathInfoPatternNegated = negated;
    }

        
    
    

    public final Pattern getPathTranslatedPattern() {
        return pathTranslatedPattern;
    }
    public final boolean isPathTranslatedPatternNegated() {
        return this.pathTranslatedPatternNegated;
    }
    protected final void setPathTranslatedPattern(final Pattern pathTranslatedPattern, final boolean negated) {
        this.pathTranslatedPattern = pathTranslatedPattern;
        this.pathTranslatedPatternNegated = negated;
    }

        
    
    
    
    
    

    public final Pattern getContextPathPattern() {
        return contextPathPattern;
    }
    public final boolean isContextPathPatternNegated() {
        return this.contextPathPatternNegated;
    }
    protected final void setContextPathPattern(final Pattern contextPathPattern, final boolean negated) {
        this.contextPathPattern = contextPathPattern;
        this.contextPathPatternNegated = negated;
    }

        
    
    
    
    
    public final Pattern getRequestURLPattern() {
        return requestURLPattern;
    }
    public final boolean isRequestURLPatternNegated() {
        return this.requestURLPatternNegated;
    }
    protected final void setRequestURLPattern(final Pattern requestURLPattern, final boolean negated) {
        this.requestURLPattern = requestURLPattern;
        this.requestURLPatternNegated = negated;
    }

    
    
    
    public final Pattern getRequestURIPattern() {
        return requestURIPattern;
    }
    public final boolean isRequestURIPatternNegated() {
        return this.requestURIPatternNegated;
    }
    protected final void setRequestURIPattern(final Pattern requestURIPattern, final boolean negated) {
        this.requestURIPattern = requestURIPattern;
        this.requestURIPatternNegated = negated;
    }

    
    
    
    

    public final Pattern getEncodingPattern() {
        return encodingPattern;
    }
    public final boolean isEncodingPatternNegated() {
        return this.encodingPatternNegated;
    }
    protected final void setEncodingPattern(final Pattern encodingPattern, final boolean negated) {
        this.encodingPattern = encodingPattern;
        this.encodingPatternNegated = negated;
    }

        
    
    
    
    

    public final Pattern getContentLengthPattern() {
        return contentLengthPattern;
    }
    public final boolean isContentLengthPatternNegated() {
        return this.contentLengthPatternNegated;
    }
    protected final void setContentLengthPattern(final Pattern contentLengthPattern, final boolean negated) {
        this.contentLengthPattern = contentLengthPattern;
        this.contentLengthPatternNegated = negated;
    }

        
    

    public final Pattern getSchemePattern() {
        return schemePattern;
    }
    public final boolean isSchemePatternNegated() {
        return this.schemePatternNegated;
    }
    protected final void setSchemePattern(final Pattern schemePattern, final boolean negated) {
        this.schemePattern = schemePattern;
        this.schemePatternNegated = negated;
    }
    
    
    

    
    public final Pattern getServerNamePattern() {
        return serverNamePattern;
    }
    public final boolean isServerNamePatternNegated() {
        return this.serverNamePatternNegated;
    }
    protected final void setServerNamePattern(final Pattern serverNamePattern, final boolean negated) {
        this.serverNamePattern = serverNamePattern;
        this.serverNamePatternNegated = negated;
    }
    
    
    
    
    public final Pattern getServerPortPattern() {
        return serverPortPattern;
    }
    public final boolean isServerPortPatternNegated() {
        return this.serverPortPatternNegated;
    }
    protected final void setServerPortPattern(final Pattern serverPortPattern, final boolean negated) {
        this.serverPortPattern = serverPortPattern;
        this.serverPortPatternNegated = negated;
    }
    
    

    
    
    
    
    public final Pattern getLocalNamePattern() {
        return localNamePattern;
    }
    public final boolean isLocalNamePatternNegated() {
        return this.localNamePatternNegated;
    }
    protected final void setLocalNamePattern(final Pattern localNamePattern, final boolean negated) {
        this.localNamePattern = localNamePattern;
        this.localNamePatternNegated = negated;
    }
    
    
    
    
    public final Pattern getLocalPortPattern() {
        return localPortPattern;
    }
    public final boolean isLocalPortPatternNegated() {
        return this.localPortPatternNegated;
    }
    protected final void setLocalPortPattern(final Pattern localPortPattern, final boolean negated) {
        this.localPortPattern = localPortPattern;
        this.localPortPatternNegated = negated;
    }
    
    
    
    
    public final Pattern getLocalAddrPattern() {
        return localAddrPattern;
    }
    public final boolean isLocalAddrPatternNegated() {
        return this.localAddrPatternNegated;
    }
    protected final void setLocalAddrPattern(final Pattern localAddrPattern, final boolean negated) {
        this.localAddrPattern = localAddrPattern;
        this.localAddrPatternNegated = negated;
    }
    
    
    
    
    
    
    
    
    
    
    
    public final Pattern getRequestedSessionIdPattern() {
        return requestedSessionIdPattern;
    }
    public final boolean isRequestedSessionIdPatternNegated() {
        return this.requestedSessionIdPatternNegated;
    }
    protected final void setRequestedSessionIdPattern(final Pattern requestedSessionIdPattern, final boolean negated) {
        this.requestedSessionIdPattern = requestedSessionIdPattern;
        this.requestedSessionIdPatternNegated = negated;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // null is used as key for "any parameter"
    protected final void addRequestParamValuePattern(final String requestParamName, final Pattern pattern, final boolean negated) {
        this.requestParamName2ValuePattern.put(requestParamName, pattern);
        if (negated) this.requestParamNamesValueNegated.add(requestParamName);
    }
    // null is used as key for "any parameter"
    public final boolean isRequestParamValuePatternNegated(final String requestParamName) {
        return this.requestParamNamesValueNegated.contains(requestParamName);
    }
    // null is used as key for "any parameter"
    public final Pattern getRequestParamValuePattern(final String requestParamName) {
        return (Pattern) this.requestParamName2ValuePattern.get(requestParamName);
    }
    public final Set/*<String>*/ getRequestParamValuePatternNames() {
        return this.requestParamName2ValuePattern.keySet();
    }
    public final boolean isHavingAnyRequestParamValuePatterns() {
        return !this.requestParamName2ValuePattern.isEmpty();
    }

    
    // null is used as key for "any parameter"
    protected final void addRequestParamCountPattern(final String requestParamName, final Pattern pattern, final boolean negated) {
        this.requestParamName2CountPattern.put(requestParamName, pattern);
        if (negated) this.requestParamNamesCountNegated.add(requestParamName);
    }
    // null is used as key for "any parameter"
    public final boolean isRequestParamCountPatternNegated(final String requestParamName) {
        return this.requestParamNamesCountNegated.contains(requestParamName);
    }
    // null is used as key for "any parameter"
    public final Pattern getRequestParamCountPattern(final String requestParamName) {
        return (Pattern) this.requestParamName2CountPattern.get(requestParamName);
    }
    public final Set/*<String>*/ getRequestParamCountPatternNames() {
        return this.requestParamName2CountPattern.keySet();
    }
    public final boolean isHavingAnyRequestParamCountPatterns() {
        return !this.requestParamName2CountPattern.isEmpty();
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // null is used as key for "any header"
    protected final void addHeaderValuePattern(String headerName, final Pattern pattern, final boolean negated) {
        if (headerName != null) headerName = headerName.toUpperCase(); // names are case-insensitive here, so all is upper-cased
        this.headerName2ValuePattern.put(headerName, pattern);
        if (negated) this.headerNamesValueNegated.add(headerName);
    }
    // null is used as key for "any header"
    public final boolean isHeaderValuePatternNegated(String headerName) {
        if (headerName != null) headerName = headerName.toUpperCase(); // names are case-insensitive here, so all is upper-cased
        return this.headerNamesValueNegated.contains(headerName);
    }
    // null is used as key for "any header"
    public final Pattern getHeaderValuePattern(String headerName) {
        if (headerName != null) headerName = headerName.toUpperCase(); // names are case-insensitive here, so all is upper-cased
        return (Pattern) this.headerName2ValuePattern.get(headerName);
    }
    public final Set/*<String>*/ getHeaderValuePatternNamesUppercased() { // as all keys were upper-cased when putting into map, the result is in upper-case only
        return this.headerName2ValuePattern.keySet();
    }
    public final boolean isHavingAnyHeaderValuePatterns() {
        return !this.headerName2ValuePattern.isEmpty();
    }

    
    // null is used as key for "any header"
    protected final void addHeaderCountPattern(String headerName, final Pattern pattern, final boolean negated) {
        if (headerName != null) headerName = headerName.toUpperCase(); // names are case-insensitive here, so all is upper-cased
        this.headerName2CountPattern.put(headerName, pattern);
        if (negated) this.headerNamesCountNegated.add(headerName);
    }
    // null is used as key for "any header"
    public final boolean isHeaderCountPatternNegated(String headerName) {
        if (headerName != null) headerName = headerName.toUpperCase(); // names are case-insensitive here, so all is upper-cased
        return this.headerNamesCountNegated.contains(headerName);
    }
    // null is used as key for "any header"
    public final Pattern getHeaderCountPattern(String headerName) {
        if (headerName != null) headerName = headerName.toUpperCase(); // names are case-insensitive here, so all is upper-cased
        return (Pattern) this.headerName2CountPattern.get(headerName);
    }
    public final Set/*<String>*/ getHeaderCountPatternNamesUppercased() { // as all keys were upper-cased when putting into map, the result is in upper-case only
        return this.headerName2CountPattern.keySet();
    }
    public final boolean isHavingAnyHeaderCountPatterns() {
        return !this.headerName2CountPattern.isEmpty();
    }
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // null is used as key for "any cookie"
    protected final void addCookieValuePattern(String cookieName, final Pattern pattern, final boolean negated) {
        if (cookieName != null) cookieName = cookieName.toUpperCase(); // names are case-insensitive here, so all is upper-cased
        this.cookieName2ValuePattern.put(cookieName, pattern);
        if (negated) this.cookieNamesValueNegated.add(cookieName);
    }
    // null is used as key for "any cookie"
    public final boolean isCookieValuePatternNegated(String cookieName) {
        if (cookieName != null) cookieName = cookieName.toUpperCase(); // names are case-insensitive here, so all is upper-cased
        return this.cookieNamesValueNegated.contains(cookieName);
    }
    // null is used as key for "any cookie"
    public final Pattern getCookieValuePattern(String cookieName) {
        if (cookieName != null) cookieName = cookieName.toUpperCase(); // names are case-insensitive here, so all is upper-cased
        return (Pattern) this.cookieName2ValuePattern.get(cookieName);
    }
    public final Set/*<String>*/ getCookieValuePatternNamesUppercased() { // as all keys were upper-cased when putting into map, the result is in upper-case only
        return this.cookieName2ValuePattern.keySet();
    }
    public final boolean isHavingAnyCookieValuePatterns() {
        return !this.cookieName2ValuePattern.isEmpty();
    }

    
    // null is used as key for "any cookie"
    protected final void addCookieCountPattern(String cookieName, final Pattern pattern, final boolean negated) {
        if (cookieName != null) cookieName = cookieName.toUpperCase(); // names are case-insensitive here, so all is upper-cased
        this.cookieName2CountPattern.put(cookieName, pattern);
        if (negated) this.cookieNamesCountNegated.add(cookieName);
    }
    // null is used as key for "any cookie"
    public final boolean isCookieCountPatternNegated(String cookieName) {
        if (cookieName != null) cookieName = cookieName.toUpperCase(); // names are case-insensitive here, so all is upper-cased
        return this.cookieNamesCountNegated.contains(cookieName);
    }
    // null is used as key for "any cookie"
    public final Pattern getCookieCountPattern(String cookieName) {
        if (cookieName != null) cookieName = cookieName.toUpperCase(); // names are case-insensitive here, so all is upper-cased
        return (Pattern) this.cookieName2CountPattern.get(cookieName);
    }
    public final Set/*<String>*/ getCookieCountPatternNamesUppercased() { // as all keys were upper-cased when putting into map, the result is in upper-case only
        return this.cookieName2CountPattern.keySet();
    }
    public final boolean isHavingAnyCookieCountPatterns() {
        return !this.cookieName2CountPattern.isEmpty();
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public final Pattern getQueryStringPattern() {
        return queryStringPattern;
    }
    public final boolean isQueryStringPatternNegated() {
        return this.queryStringPatternNegated;
    }
    protected final void setQueryStringPattern(final Pattern queryStringPattern, final boolean negated) {
        this.queryStringPattern = queryStringPattern;
        this.queryStringPatternNegated = negated;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    


    public WordDictionary getAuthTypePrefilter() {
        return authTypePrefilter;
    }

    public void setAuthTypePrefilter(WordDictionary authTypePrefilter) {
        this.authTypePrefilter = authTypePrefilter;
    }

    public WordDictionary getContentLengthPrefilter() {
        return contentLengthPrefilter;
    }

    public void setContentLengthPrefilter(WordDictionary contentLengthPrefilter) {
        this.contentLengthPrefilter = contentLengthPrefilter;
    }

    public WordDictionary getContextPathPrefilter() {
        return contextPathPrefilter;
    }

    public void setContextPathPrefilter(WordDictionary contextPathPrefilter) {
        this.contextPathPrefilter = contextPathPrefilter;
    }

    public WordDictionary getCountryPrefilter() {
        return countryPrefilter;
    }

    public void setCountryPrefilter(WordDictionary countryPrefilter) {
        this.countryPrefilter = countryPrefilter;
    }

    public WordDictionary getEncodingPrefilter() {
        return encodingPrefilter;
    }

    public void setEncodingPrefilter(WordDictionary encodingPrefilter) {
        this.encodingPrefilter = encodingPrefilter;
    }

    public WordDictionary getLocalAddrPrefilter() {
        return localAddrPrefilter;
    }

    public void setLocalAddrPrefilter(WordDictionary localAddrPrefilter) {
        this.localAddrPrefilter = localAddrPrefilter;
    }

    public WordDictionary getLocalNamePrefilter() {
        return localNamePrefilter;
    }

    public void setLocalNamePrefilter(WordDictionary localNamePrefilter) {
        this.localNamePrefilter = localNamePrefilter;
    }

    public WordDictionary getLocalPortPrefilter() {
        return localPortPrefilter;
    }

    public void setLocalPortPrefilter(WordDictionary localPortPrefilter) {
        this.localPortPrefilter = localPortPrefilter;
    }

    public WordDictionary getMethodPrefilter() {
        return methodPrefilter;
    }

    public void setMethodPrefilter(WordDictionary methodPrefilter) {
        this.methodPrefilter = methodPrefilter;
    }

    public WordDictionary getMimeTypePrefilter() {
        return mimeTypePrefilter;
    }

    public void setMimeTypePrefilter(WordDictionary mimeTypePrefilter) {
        this.mimeTypePrefilter = mimeTypePrefilter;
    }

    public WordDictionary getPathInfoPrefilter() {
        return pathInfoPrefilter;
    }

    public void setPathInfoPrefilter(WordDictionary pathInfoPrefilter) {
        this.pathInfoPrefilter = pathInfoPrefilter;
    }

    public WordDictionary getPathTranslatedPrefilter() {
        return pathTranslatedPrefilter;
    }

    public void setPathTranslatedPrefilter(WordDictionary pathTranslatedPrefilter) {
        this.pathTranslatedPrefilter = pathTranslatedPrefilter;
    }

    public WordDictionary getProtocolPrefilter() {
        return protocolPrefilter;
    }

    public void setProtocolPrefilter(WordDictionary protocolPrefilter) {
        this.protocolPrefilter = protocolPrefilter;
    }

    public WordDictionary getQueryStringPrefilter() {
        return queryStringPrefilter;
    }

    public void setQueryStringPrefilter(WordDictionary queryStringPrefilter) {
        this.queryStringPrefilter = queryStringPrefilter;
    }

    public WordDictionary getRemoteAddrPrefilter() {
        return remoteAddrPrefilter;
    }

    public void setRemoteAddrPrefilter(WordDictionary remoteAddrPrefilter) {
        this.remoteAddrPrefilter = remoteAddrPrefilter;
    }

    public WordDictionary getRemoteHostPrefilter() {
        return remoteHostPrefilter;
    }

    public void setRemoteHostPrefilter(WordDictionary remoteHostPrefilter) {
        this.remoteHostPrefilter = remoteHostPrefilter;
    }

    public WordDictionary getRemotePortPrefilter() {
        return remotePortPrefilter;
    }

    public void setRemotePortPrefilter(WordDictionary remotePortPrefilter) {
        this.remotePortPrefilter = remotePortPrefilter;
    }

    public WordDictionary getRemoteUserPrefilter() {
        return remoteUserPrefilter;
    }

    public void setRemoteUserPrefilter(WordDictionary remoteUserPrefilter) {
        this.remoteUserPrefilter = remoteUserPrefilter;
    }

    public WordDictionary getRequestURIPrefilter() {
        return requestURIPrefilter;
    }

    public void setRequestURIPrefilter(WordDictionary requestURIPrefilter) {
        this.requestURIPrefilter = requestURIPrefilter;
    }

    public WordDictionary getRequestURLPrefilter() {
        return requestURLPrefilter;
    }

    public void setRequestURLPrefilter(WordDictionary requestURLPrefilter) {
        this.requestURLPrefilter = requestURLPrefilter;
    }

    public WordDictionary getRequestedSessionIdPrefilter() {
        return requestedSessionIdPrefilter;
    }

    public void setRequestedSessionIdPrefilter(WordDictionary requestedSessionIdPrefilter) {
        this.requestedSessionIdPrefilter = requestedSessionIdPrefilter;
    }

    public WordDictionary getSchemePrefilter() {
        return schemePrefilter;
    }

    public void setSchemePrefilter(WordDictionary schemePrefilter) {
        this.schemePrefilter = schemePrefilter;
    }

    public WordDictionary getServerNamePrefilter() {
        return serverNamePrefilter;
    }

    public void setServerNamePrefilter(WordDictionary serverNamePrefilter) {
        this.serverNamePrefilter = serverNamePrefilter;
    }

    public WordDictionary getServerPortPrefilter() {
        return serverPortPrefilter;
    }

    public void setServerPortPrefilter(WordDictionary serverPortPrefilter) {
        this.serverPortPrefilter = serverPortPrefilter;
    }

    public WordDictionary getTimeDayPrefilter() {
        return timeDayPrefilter;
    }

    public void setTimeDayPrefilter(WordDictionary timeDayPrefilter) {
        this.timeDayPrefilter = timeDayPrefilter;
    }

    public WordDictionary getTimeHourPrefilter() {
        return timeHourPrefilter;
    }

    public void setTimeHourPrefilter(WordDictionary timeHourPrefilter) {
        this.timeHourPrefilter = timeHourPrefilter;
    }

    public WordDictionary getTimeMinutePrefilter() {
        return timeMinutePrefilter;
    }

    public void setTimeMinutePrefilter(WordDictionary timeMinutePrefilter) {
        this.timeMinutePrefilter = timeMinutePrefilter;
    }

    public WordDictionary getTimeMonthPrefilter() {
        return timeMonthPrefilter;
    }

    public void setTimeMonthPrefilter(WordDictionary timeMonthPrefilter) {
        this.timeMonthPrefilter = timeMonthPrefilter;
    }

    public WordDictionary getTimePrefilter() {
        return timePrefilter;
    }

    public void setTimePrefilter(WordDictionary timePrefilter) {
        this.timePrefilter = timePrefilter;
    }

    public WordDictionary getTimeSecondPrefilter() {
        return timeSecondPrefilter;
    }

    public void setTimeSecondPrefilter(WordDictionary timeSecondPrefilter) {
        this.timeSecondPrefilter = timeSecondPrefilter;
    }

    public WordDictionary getTimeWeekdayPrefilter() {
        return timeWeekdayPrefilter;
    }

    public void setTimeWeekdayPrefilter(WordDictionary timeWeekdayPrefilter) {
        this.timeWeekdayPrefilter = timeWeekdayPrefilter;
    }

    public WordDictionary getTimeYearPrefilter() {
        return timeYearPrefilter;
    }

    public void setTimeYearPrefilter(WordDictionary timeYearPrefilter) {
        this.timeYearPrefilter = timeYearPrefilter;
    }

    public WordDictionary getCookieNameListPrefilter() {
        return cookieNameListPrefilter;
    }

    public void setCookieNameListPrefilter(WordDictionary cookieNameListPrefilter) {
        this.cookieNameListPrefilter = cookieNameListPrefilter;
    }

    public WordDictionary getHeaderNameListPrefilter() {
        return headerNameListPrefilter;
    }

    public void setHeaderNameListPrefilter(WordDictionary headerNameListPrefilter) {
        this.headerNameListPrefilter = headerNameListPrefilter;
    }

    public WordDictionary getRequestParamNameListPrefilter() {
        return requestParamNameListPrefilter;
    }

    public void setRequestParamNameListPrefilter(WordDictionary requestParamNameListPrefilter) {
        this.requestParamNameListPrefilter = requestParamNameListPrefilter;
    }

    
        
    
    
    
    
    
    
    
    
    
    
    // null is used as key for "any header"
    protected final void addHeaderValuePrefilter(final String headerName, final WordDictionary prefilter) {
        this.headerName2ValuePrefilter.put(headerName, prefilter);
    }
    // null is used as key for "any header"
    public final WordDictionary getHeaderValuePrefilter(final String headerName) {
        return (WordDictionary) this.headerName2ValuePrefilter.get(headerName);
    }
    public final Set/*<String>*/ getHeaderValuePrefilterNames() {
        return this.headerName2ValuePrefilter.keySet();
    }
    public final boolean isHavingAnyHeaderValuePrefilters() {
        return !this.headerName2ValuePrefilter.isEmpty();
    }
    
    // null is used as key for "any header"
    protected final void addHeaderCountPrefilter(final String headerName, final WordDictionary prefilter) {
        this.headerName2CountPrefilter.put(headerName, prefilter);
    }
    // null is used as key for "any header"
    public final WordDictionary getHeaderCountPrefilter(final String headerName) {
        return (WordDictionary) this.headerName2CountPrefilter.get(headerName);
    }
    public final Set/*<String>*/ getHeaderCountPrefilterNames() {
        return this.headerName2CountPrefilter.keySet();
    }
    public final boolean isHavingAnyHeaderCountPrefilters() {
        return !this.headerName2CountPrefilter.isEmpty();
    }

    
    
    

    

    
    
    // null is used as key for "any cookie"
    protected final void addCookieValuePrefilter(final String cookieName, final WordDictionary prefilter) {
        this.cookieName2ValuePrefilter.put(cookieName, prefilter);
    }
    // null is used as key for "any cookie"
    public final WordDictionary getCookieValuePrefilter(final String cookieName) {
        return (WordDictionary) this.cookieName2ValuePrefilter.get(cookieName);
    }
    public final Set/*<String>*/ getCookieValuePrefilterNames() {
        return this.cookieName2ValuePrefilter.keySet();
    }
    public final boolean isHavingAnyCookieValuePrefilters() {
        return !this.cookieName2ValuePrefilter.isEmpty();
    }
    
    // null is used as key for "any cookie"
    protected final void addCookieCountPrefilter(final String cookieName, final WordDictionary prefilter) {
        this.cookieName2CountPrefilter.put(cookieName, prefilter);
    }
    // null is used as key for "any cookie"
    public final WordDictionary getCookieCountPrefilter(final String cookieName) {
        return (WordDictionary) this.cookieName2CountPrefilter.get(cookieName);
    }
    public final Set/*<String>*/ getCookieCountPrefilterNames() {
        return this.cookieName2CountPrefilter.keySet();
    }
    public final boolean isHavingAnyCookieCountPrefilters() {
        return !this.cookieName2CountPrefilter.isEmpty();
    }

    
    
    
    

    
    
    // null is used as key for "any parameter"
    protected final void addRequestParamValuePrefilter(final String requestParamName, final WordDictionary prefilter) {
        this.requestParamName2ValuePrefilter.put(requestParamName, prefilter);
    }
    // null is used as key for "any parameter"
    public final WordDictionary getRequestParamValuePrefilter(final String requestParamName) {
        return (WordDictionary) this.requestParamName2ValuePrefilter.get(requestParamName);
    }
    public final Set/*<String>*/ getRequestParamValuePrefilterNames() {
        return this.requestParamName2ValuePrefilter.keySet();
    }
    public final boolean isHavingAnyRequestParamValuePrefilters() {
        return !this.requestParamName2ValuePrefilter.isEmpty();
    }

    // null is used as key for "any parameter"
    protected final void addRequestParamCountPrefilter(final String requestParamName, final WordDictionary prefilter) {
        this.requestParamName2CountPrefilter.put(requestParamName, prefilter);
    }
    // null is used as key for "any parameter"
    public final WordDictionary getRequestParamCountPrefilter(final String requestParamName) {
        return (WordDictionary) this.requestParamName2CountPrefilter.get(requestParamName);
    }
    public final Set/*<String>*/ getRequestParamCountPrefilterNames() {
        return this.requestParamName2CountPrefilter.keySet();
    }
    public final boolean isHavingAnyRequestParamCountPrefilters() {
        return !this.requestParamName2CountPrefilter.isEmpty();
    }

        
    
}
