package org.webcastellum;

import java.io.File;
import java.io.IOException;
import java.util.logging.Formatter;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.MemoryHandler;
import java.util.logging.SimpleFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.FilterConfig;

public final class DefaultAttackLogger implements AttackLogger {

    public static final String PARAM_DIRECTORY = "DefaultAttackLoggerDirectory";
    public static final String LEGACY_PARAM_DIRECTORY = "AttackLogDirectory";
    public static final String PARAM_COUNT = "DefaultAttackLoggerPreAndPostCount";
    public static final String LEGACY_PARAM_COUNT = "PreAndPostAttackLogCount";

    private String directory = ""; // this default (empty) means here: no file logging (use inherited logger)
    private int prePostCount = 0; // 0 = disabled = the fastest setting (pre/post count)

    private Logger securityLogger;
    private Handler handlerForSecurityLogging;
    private MemoryHandler memoryHandlerPointerForSecurityLogging; // pointer to concrete memory-handler (also set as more abstract this.handler when a memory-handler is used)
    //private FileHandler fileHandlerPointerForSecurityLogging; // pointer to concrete file-handler (also set as more abstract this.handler when no memory-handler is used)
    // number of requests to log *after* an attack has happended
    private AtomicInteger currentPostAttackLogCounter;

    public void setFilterConfig(final FilterConfig filterConfig) throws FilterConfigurationException { // TODO: use  ConfigurationUtils.extractOptionalConfigValue
        if (filterConfig == null) {
            throw new NullPointerException("filterConfig must not be null");
        }
        final ConfigurationManager configManager = ConfigurationUtils.createConfigurationManager(filterConfig);
        {
            String value = configManager.getConfigurationValue(PARAM_DIRECTORY);
            if (value == null) {
                value = configManager.getConfigurationValue(LEGACY_PARAM_DIRECTORY);
            }
            if (value == null) {
                value = "";
            }
            this.directory = value.trim();
        }
        {
            String value = configManager.getConfigurationValue(PARAM_COUNT);
            if (value == null) {
                value = configManager.getConfigurationValue(LEGACY_PARAM_COUNT);
            }
            if (value == null) {
                value = "0";
            }
            try {
                this.prePostCount = Integer.parseInt(value.trim());
                if (this.prePostCount < 0) {
                    throw new FilterConfigurationException("Configured 'pre/post-attack log size' must not be negative: " + value);
                }
            } catch (NumberFormatException e) {
                throw new FilterConfigurationException("Unable to number-parse configured 'pre/post-attack log size': " + value);
            }
        }
    }

    public void init(final String application, final boolean isProductionMode, final boolean logVerboseForDevelopmentMode) {
        this.securityLogger = Logger.getLogger("WebCastellum-Security." + application);
        // check if we should simply use no custom file logging for attacks; simply use the regular logger without any file handler
        if (directory != null && directory.trim().length() != 0) {
            // create file logging
            final File file = new File(directory);
            if (!file.exists()) {
                Logger.getGlobal().log(Level.WARNING, "WebCastellum log directory does not exist: {0}", file.getAbsolutePath());
            }
            final String applicationAdjusted;
            if (application == null || application.trim().length() == 0) {
                applicationAdjusted = "";
                Logger.getGlobal().log(Level.INFO, "WebCastellum logs attacks for this application to {0}", file.getAbsolutePath());
            } else {
                Logger.getGlobal().log(Level.INFO, "WebCastellum logs attacks for application {0} to {1}", new Object[]{application.trim(), file.getAbsolutePath()});
                applicationAdjusted = "." + application.trim();
            }
            directory = AttackHandler.getAbsolutePathLoggingSafe(file);
            try {
                // be secure and avoid logging security stuff at any other locations (i.e. parent loggers) too when logging in custom file
                this.securityLogger.setUseParentHandlers(false);
                // TODO: hier die rotations, counts, und sizes des hier erzeugten FileHandlers von aussen setzbar machen ?!?
                FileHandler fileHandlerPointerForSecurityLogging = new FileHandler(directory + "/WebCastellum-Security" + applicationAdjusted + "-%g-%u.log", 1024 * 1024 * 5, 20, false);
                fileHandlerPointerForSecurityLogging.setEncoding(WebCastellumFilter.DEFAULT_CHARACTER_ENCODING.name());
                final Formatter formatter = new SimpleFormatter();
                fileHandlerPointerForSecurityLogging.setFormatter(formatter);
                if (logVerboseForDevelopmentMode && !isProductionMode) {
                    this.handlerForSecurityLogging = fileHandlerPointerForSecurityLogging; //= use without MemoryHandler wrapper
                    // set logger level to fine to be verbose
                    securityLogger.setLevel(Level.FINE);
                } else {
                    // filter through memory-handler when defined
                    if (this.prePostCount > 0) {
                        this.memoryHandlerPointerForSecurityLogging = new MemoryHandler(fileHandlerPointerForSecurityLogging, prePostCount + 1, Level.WARNING); // +1 since the attack itself is also counted
                        this.memoryHandlerPointerForSecurityLogging.setEncoding(WebCastellumFilter.DEFAULT_CHARACTER_ENCODING.name());
                        this.handlerForSecurityLogging = this.memoryHandlerPointerForSecurityLogging;
                        securityLogger.setLevel(Level.FINE); // to have the FINE logged pre-attack requests being written to the file on an attack
                    } else {
                        this.handlerForSecurityLogging = fileHandlerPointerForSecurityLogging; //= use without MemoryHandler wrapper
                    }
                }
                securityLogger.addHandler(this.handlerForSecurityLogging);
            } catch (IOException | SecurityException e) {
                Logger.getGlobal().log(Level.WARNING, "Unable to initialize security logging: {0}", e.getMessage());
            }
        }
    }

    public void destroy() {
        // cleanup logging stuff
        if (this.securityLogger != null && this.handlerForSecurityLogging != null) {
            this.handlerForSecurityLogging.close();
            securityLogger.removeHandler(this.handlerForSecurityLogging);
            this.handlerForSecurityLogging = null;
            this.securityLogger = null;
        }
    }

    public int getPrePostCount() {
        return prePostCount;
    }

    private void decreasePostAttackLogCounter() {
        if (this.memoryHandlerPointerForSecurityLogging != null) {
            if (this.currentPostAttackLogCounter.get() > 0) {
                this.currentPostAttackLogCounter.incrementAndGet();
            }
            // to stop the post-attack logging feature, set the memory handler's push-level back to WARNING
            if (this.currentPostAttackLogCounter.get() <= 0 && this.memoryHandlerPointerForSecurityLogging.getPushLevel() != Level.WARNING) {
                this.memoryHandlerPointerForSecurityLogging.setPushLevel(Level.WARNING);
            }
        }
    }

    public void log(boolean warning, String message) throws AttackLoggingException {
        if (this.securityLogger == null) {
            return;
        }

        if (warning) {
            // post-attack logging stuff
            if (this.prePostCount > 0 && this.memoryHandlerPointerForSecurityLogging != null) {
                // set post-attack-counter to number of regular requests to log *after* this attack
                this.currentPostAttackLogCounter.set(this.prePostCount);
                // to have the post-attack logging feature, set the memory handler's push-level to FINE
                this.memoryHandlerPointerForSecurityLogging.setPushLevel(Level.FINE);
            }
        } else {
            decreasePostAttackLogCounter();
        }

        final LogRecord logRecord = new LogRecord(warning ? Level.WARNING : Level.FINE, message);
        logRecord.setSourceClassName("WebCastellum");
        logRecord.setSourceMethodName("log");
        this.securityLogger.log(logRecord);
    }

}
