package org.webcastellum;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class AttackHandler {
    
    private static final Logger LOGGER = Logger.getLogger(AttackHandler.class.getName());
    
    private final Map<String,Counter> attackCounter = Collections.synchronizedMap(new HashMap<>());
    private final Map<String,Counter> redirectCounter = Collections.synchronizedMap(new HashMap<>());
    private final int blockAttackingClientsThreshold;
    private final int blockRepeatedRedirectsThreshold;
 
    private final long resetPeriodMillisAttack;
    private final long resetPeriodMillisRedirectThreshold;
    private final boolean logSessionValuesOnAttack;
    private final boolean invalidateSessionOnAttack;
    private final boolean isProductionMode;
    private final boolean logVerboseForDevelopmentMode;
    private final boolean logClientUserData;
    private final String blockMessage;
    private final Pattern removeSensitiveDataRequestParamNamePattern;
    private final Pattern removeSensitiveDataRequestParamNameAndValueUrlPattern;
    private final Pattern removeSensitiveDataValuePattern;
    
    private ClientBlacklist clientBlacklist;

    private Timer cleanupTimerAttackTracking, cleanupTimerRedirectTracking;
    private TimerTask taskAttackTracking, taskRedirectTracking;

    // learning mode aggregation logger
    private Logger learningModeLogger;
    private FileHandler handlerForLearningModeLogging; // here we know it is always a FileHandler if it is present

    private final AttackLogger attackLogger;
    
    public AttackHandler(final AttackLogger attackLogger, final int threshold, final long cleanupIntervalMillis, final long blockPeriodMillis, final long resetPeriodMillisAttack, final long resetPeriodMillisRedirectThreshold, final String learningModeAggregationDirectory, final String applicationName,
            final boolean logSessionValuesOnAttack, final boolean invalidateSessionOnAttack,
            final int blockRepeatedRedirectsThreshold, final boolean isProductionMode, final boolean logVerboseForDevelopmentMode,
            final Pattern removeSensitiveDataRequestParamNamePattern, final Pattern removeSensitiveDataValuePattern, final boolean logClientUserData) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
        }
        if (removeSensitiveDataRequestParamNamePattern == null) {
            throw new NullPointerException("removeSensitiveDataRequestParamNamePattern must not be null");
        }
        if (removeSensitiveDataValuePattern == null) {
            throw new NullPointerException("removeSensitiveDataValuePattern must not be null");
        }
        this.attackLogger = attackLogger;
        this.blockAttackingClientsThreshold = threshold;
        this.resetPeriodMillisAttack = resetPeriodMillisAttack;
        this.resetPeriodMillisRedirectThreshold = resetPeriodMillisRedirectThreshold;
        this.logSessionValuesOnAttack = logSessionValuesOnAttack;
        this.invalidateSessionOnAttack = invalidateSessionOnAttack;
        this.isProductionMode = isProductionMode;
        this.logVerboseForDevelopmentMode = logVerboseForDevelopmentMode;
        this.blockMessage = String.format("%d seconds", Math.round(blockPeriodMillis / 1000d));
        this.blockRepeatedRedirectsThreshold = blockRepeatedRedirectsThreshold;
        if (this.blockAttackingClientsThreshold > 0) {
            this.clientBlacklist = new ClientBlacklist(cleanupIntervalMillis, blockPeriodMillis);
        }
        this.removeSensitiveDataRequestParamNamePattern = removeSensitiveDataRequestParamNamePattern;
        this.removeSensitiveDataRequestParamNameAndValueUrlPattern = Pattern.compile("(?:" + removeSensitiveDataRequestParamNamePattern.pattern() + ")=[^\\&]*");
        this.removeSensitiveDataValuePattern = removeSensitiveDataValuePattern;
        this.logClientUserData = logClientUserData;
        initTimers(cleanupIntervalMillis);
        initLogging(learningModeAggregationDirectory, applicationName);
    }
    
    private void initTimers(final long cleanupIntervalMillis) {
        // init attack tracking stuff
        if (this.blockAttackingClientsThreshold > 0) { // 0 = disabled
            this.cleanupTimerAttackTracking = new Timer("AttackHandler-cleanup-attacks", true);
            this.taskAttackTracking = new CleanupIncrementingCounterTask("AttackHandler-cleanup-attacks",this.attackCounter);
            this.cleanupTimerAttackTracking.scheduleAtFixedRate(taskAttackTracking, CryptoUtils.generateRandomNumber(false,60000,300000), cleanupIntervalMillis);
        }
        // init redirect tracking stuff
        if (this.blockRepeatedRedirectsThreshold > 0) { // 0 = disabled
            this.cleanupTimerRedirectTracking = new Timer("AttackHandler-cleanup-redirects", true);
            this.taskRedirectTracking = new CleanupIncrementingCounterTask("AttackHandler-cleanup-redirects",this.redirectCounter);
            this.cleanupTimerRedirectTracking.scheduleAtFixedRate(taskRedirectTracking, CryptoUtils.generateRandomNumber(false,60000,300000), cleanupIntervalMillis);
        }
    }
    
    public void destroy() {
        // cleanup attack tracking stuff
        this.attackCounter.clear();
        if (this.taskAttackTracking != null) {
            this.taskAttackTracking.cancel();
            this.taskAttackTracking = null;
        }
        if (this.cleanupTimerAttackTracking != null) {
            this.cleanupTimerAttackTracking.cancel();
            this.cleanupTimerAttackTracking = null;
            this.attackCounter.clear();
        }
        // cleanup redirect tracking stuff
        this.redirectCounter.clear();
        if (this.taskRedirectTracking != null) {
            this.taskRedirectTracking.cancel();
            this.taskRedirectTracking = null;
        }
        if (this.cleanupTimerRedirectTracking != null) {
            this.cleanupTimerRedirectTracking.cancel();
            this.cleanupTimerRedirectTracking = null;
            this.redirectCounter.clear();
        }
        // cleanup client blacklist
        if (this.clientBlacklist != null) {
            this.clientBlacklist.destroy();
        }
        this.attackLogger.destroy();
        if (this.learningModeLogger != null && this.handlerForLearningModeLogging != null) {
            this.handlerForLearningModeLogging.close();
            learningModeLogger.removeHandler(this.handlerForLearningModeLogging);
            this.handlerForLearningModeLogging = null;
            this.learningModeLogger = null;
        }
    }
    
    public int getBlockAttackingClientsThreshold() {
        return this.blockAttackingClientsThreshold;
    }
    
    public boolean shouldBeBlocked(final String ip) {
        if (this.blockAttackingClientsThreshold == 0) return false;
        return this.clientBlacklist != null && this.clientBlacklist.isBlacklisted(ip);
    }
    
    public boolean isRedirectThresholdReached(final String ip) {
        if (this.blockRepeatedRedirectsThreshold == 0) return false;
        if (this.blockRepeatedRedirectsThreshold > 0 && this.cleanupTimerRedirectTracking != null) {
            return trackRedirecting(ip);
        }
        return false;
    }
    
    public int getRedirectThreshold() {
        return this.blockRepeatedRedirectsThreshold;
    }
    
    public long getRedirectThresholdResetPeriod() {
        return this.resetPeriodMillisRedirectThreshold;
    }
    
    public void logWarningRequestMessage(final String message) {
        if (this.attackLogger != null) {
            try {
                final StringBuilder logMessage = new StringBuilder("Warning message: ").append(Version.versionNumber()).append(" ").append("[\n");
                logMessage.append("\t").append(message).append("\n");
                logMessage.append("]");
                this.attackLogger.log(true, logMessage.toString());
            } catch (AttackLoggingException e) {
                Logger.getGlobal().log(Level.SEVERE, "Unable to log request message: {0}", e.getMessage());
            }
        }
    }                    
    
    public void logRegularRequestMessage(final String message) {
        if (this.attackLogger != null && (this.attackLogger.getPrePostCount() > 0 || (this.logVerboseForDevelopmentMode && !this.isProductionMode))) {
            try {
                final StringBuilder logMessage = new StringBuilder("Regular message (pre/post-attack logging): ").append(Version.versionNumber()).append(" ").append("[\n");
                logMessage.append("\t").append(message).append("\n");
                logMessage.append("]");
                this.attackLogger.log(false, logMessage.toString());
            } catch (AttackLoggingException e) {
                Logger.getGlobal().log(Level.SEVERE, "Unable to log request message: {0}", e.getMessage());
            }
        }
    }                
    
    public void handleRegularRequest(final HttpServletRequest request, final String ip) {
        if (this.attackLogger != null && (this.attackLogger.getPrePostCount() > 0 || (this.logVerboseForDevelopmentMode && !this.isProductionMode))) {
            try {
                final StringBuilder logMessage = new StringBuilder("Regular request (pre/post-attack logging): ").append(Version.versionNumber()).append(" ").append("[\n");
                logMessage.append( RequestUtils.extractSecurityRelevantRequestContent(request,ip,false,this.removeSensitiveDataRequestParamNamePattern,this.removeSensitiveDataRequestParamNameAndValueUrlPattern,this.removeSensitiveDataValuePattern,logClientUserData) );
                logMessage.append("]");
                this.attackLogger.log(false, logMessage.toString());
            } catch (AttackLoggingException e) {
                Logger.getGlobal().log(Level.SEVERE, "Unable to log request details: {0}", e.getMessage());
            }
        }
    }
    
    public Attack handleAttack(final HttpServletRequest request, final String ip, final String message) {
        // also (if enabled) count attacks by client IP to allow blocking
        boolean blocked = false;
        if (this.blockAttackingClientsThreshold > 0 && this.cleanupTimerAttackTracking != null && this.clientBlacklist != null) {
            blocked = trackBlocking(ip);
        }
        
        // log attack
        final String logReferenceId = IdGeneratorUtils.createId();
        final StringBuilder logMessage = new StringBuilder("Reference ").append(logReferenceId).append(" (production mode is ").append(this.isProductionMode?"enabled): ":"disabled): ").append(Version.versionNumber()).append("\n").append(message).append(" [\n");
        logMessage.append( RequestUtils.extractSecurityRelevantRequestContent(request,ip,this.logSessionValuesOnAttack,this.removeSensitiveDataRequestParamNamePattern,this.removeSensitiveDataRequestParamNameAndValueUrlPattern,this.removeSensitiveDataValuePattern,this.logClientUserData) );
        logMessage.append("]");
        
        // invalidate session on attack ?
        if (this.invalidateSessionOnAttack) {
            try {
                final HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                    logMessage.append(" ==> emergency action: session invalidated");
                }
            } catch (Exception e) {
                logMessage.append(" ==> emergency action failed: unable to invalidate session: ").append(e.getMessage());
            }
        }
        
        // client blocked ?
        if (blocked) {
            logMessage.append(" ==> further protection: client will be blocked for ").append(this.blockMessage);
        }
        
        final String logMessageString = logMessage.toString();
        if (this.attackLogger != null) {
            this.attackLogger.log(true, logMessageString);
        } else {
            LOGGER.log(Level.INFO, logMessageString);
        }
        return new Attack(logMessageString, logReferenceId);
    }
    
    private void logMessage(final Logger logger, final Level level, final String logMessage) {
        if (level == null || logMessage == null) return;
        final LogRecord logRecord = new LogRecord(level, logMessage);
        logRecord.setSourceClassName("WebCastellum");
        logRecord.setSourceMethodName("log");
        logger.log(logRecord);
    }
    
    private boolean trackBlocking(final String ip) {
        if (this.clientBlacklist == null) {
            throw new IllegalStateException("Client blacklist not initialized");
        }
        boolean blocked = false;
        synchronized (this.attackCounter) {
            Counter counter = this.attackCounter.get(ip);
            if (counter == null) {
                counter = new IncrementingCounter(this.resetPeriodMillisAttack);
                this.attackCounter.put(ip, counter);
            } else {
                counter.increment(); // = overaged will automatically be reset and reused (i.e. starting again at 1)
            }
            if (counter.getCounter() >= this.blockAttackingClientsThreshold) {
                this.attackCounter.remove(ip);
                blocked = true;
            }
        }
        if (blocked) this.clientBlacklist.blacklistClient(ip);
        return blocked;
    }
    
    private boolean trackRedirecting(final String ip) {
        synchronized (this.redirectCounter) {
            Counter counter = this.redirectCounter.get(ip);
            if (counter == null) {
                counter = new IncrementingCounter(this.resetPeriodMillisRedirectThreshold);
                this.redirectCounter.put(ip, counter);
            } else {
                counter.increment(); // = overaged will automatically be reset and reused (i.e. starting again at 1)
            }
            if (counter.getCounter() >= this.blockRepeatedRedirectsThreshold) {
                this.redirectCounter.remove(ip);
                return true;
            }
        }
        return false;
    }

    public void handleLearningModeRequestAggregation(final HttpServletRequest requestAsSeenByTheApplication) {
        if (this.learningModeLogger != null && requestAsSeenByTheApplication != null) {
            try {
                final String servletPath = requestAsSeenByTheApplication.getServletPath();
                if (servletPath != null) {
                    final StringBuilder logMessage = new StringBuilder("Regular request (learning mode): ").append(Version.versionNumber()).append(" ").append("[\n");
                    RequestUtils.appendValueToMessage(logMessage, "servletPath", ServerUtils.urlEncode(servletPath));
                    RequestUtils.appendValueToMessage(logMessage, "method", ServerUtils.urlEncode(requestAsSeenByTheApplication.getMethod()));
                    RequestUtils.appendValueToMessage(logMessage, "mimeType", ServerUtils.urlEncode(requestAsSeenByTheApplication.getContentType()));
                    RequestUtils.appendValueToMessage(logMessage, "contentLength", ""+requestAsSeenByTheApplication.getContentLength());
                    RequestUtils.appendValueToMessage(logMessage, "encoding", ServerUtils.urlEncode(requestAsSeenByTheApplication.getCharacterEncoding()));
                    RequestUtils.appendValueToMessage(logMessage, "referer", ServerUtils.urlEncode(requestAsSeenByTheApplication.getHeader("referer")));
                    final Enumeration names = requestAsSeenByTheApplication.getParameterNames();
                    if (names != null) {
                        while (names.hasMoreElements()) {
                            final String name = (String) names.nextElement();
                            final String[] values = requestAsSeenByTheApplication.getParameterValues(name);
                            if (values != null) {
                                for (String value : values) {
                                    RequestUtils.appendValueToMessage(logMessage, "requestParam: "+ServerUtils.urlEncode(name), ServerUtils.urlEncode(value));
                                }
                            }
                        }
                    } else {
                        Logger.getGlobal().log(Level.SEVERE, "This servlet-container does not allow the access of request params... VERY STRANGE");
                    }
                    logMessage.append("]");
                    logMessage(this.learningModeLogger, Level.FINE, logMessage.toString());
                }
            } catch (UnsupportedEncodingException e) {
                Logger.getGlobal().log(Level.SEVERE, "Unable to learn from (log) request details: {0}", e.getMessage());
            }
        }
    }
    
    private void initLogging(String learningModeAggregationDirectory, final String application) {
        // the security-logger can be initialized anyway
        this.attackLogger.init(application, isProductionMode, logVerboseForDevelopmentMode);
        
        // now take care of the learning mode aggregation stuff (if activated)
        if (learningModeAggregationDirectory != null && learningModeAggregationDirectory.trim().length() != 0) {
            this.learningModeLogger =  Logger.getLogger("WebCastellum-LearningMode."+application);
            // create file logging
            final File file = new File(learningModeAggregationDirectory);
            final String applicationAdjusted;
            if (application == null || application.trim().length() == 0) {
                applicationAdjusted = ""; 
                LOGGER.log(Level.INFO, "WebCastellum logs learning mode data for this application to: {0}", file.getAbsolutePath());
            } else {
                LOGGER.log(Level.INFO, "WebCastellum logs learning mode data for application: {0} to {1}", new Object[]{application.trim(), file.getAbsolutePath()});
                applicationAdjusted = "."+application.trim();
            }
            learningModeAggregationDirectory = getAbsolutePathLoggingSafe(file);
            try {
                // be secure and avoid logging learningMode stuff at any other locations (i.e. parent loggers) too when logging in custom file
                this.learningModeLogger.setUseParentHandlers(false);
                // TODO: hier die rotations, counts, und sizes des hier erzeugten FileHandlers von aussen setzbar machen ?!? 
                handlerForLearningModeLogging = new FileHandler(learningModeAggregationDirectory+"/WebCastellum-LearningMode"+applicationAdjusted+"-%g-%u.log", 1024*1024*10, 50, false);
                this.handlerForLearningModeLogging.setEncoding(WebCastellumFilter.DEFAULT_CHARACTER_ENCODING);
                final Formatter formatter = new SimpleFormatter();
                handlerForLearningModeLogging.setFormatter(formatter);
                // set logger level to fine to be verbose
                learningModeLogger.setLevel(Level.FINE);
                learningModeLogger.addHandler(this.handlerForLearningModeLogging);
            } catch (IOException | SecurityException e) {
                Logger.getGlobal().log(Level.SEVERE, "Unable to initialize learning mode data logging: {0}", e.getMessage());
            }
        }
    }
    
    // TODO: in eine utility klasse packen
    static final String getAbsolutePathLoggingSafe(final File file) {
        String result = file.getAbsolutePath();
        // escape for FileHandler special characters (see docs of java.util.logging.FileHandler)
        result = result.replaceAll("\\\\","/");
        result = result.replaceAll("%","%%");
        return result;
    }
    
    /*
    public static void main(String[] args) {
        System.out.println( "Current relative root is: "+new File("").getAbsolutePath() );
        initLogging("logs");
        logger.warning("TEST log message: "+new Date());
    }*/

}
