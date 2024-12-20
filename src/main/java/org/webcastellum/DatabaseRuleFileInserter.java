package org.webcastellum;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Small command-line tool to write the rule files (read via properties files) into the database
 */
public final class DatabaseRuleFileInserter {

    
    private final String jdbcDriver;
    private final String jdbcUrl;
    private final String jdbcUser;
    private final String jdbcPassword;
    private final String table;
    private final String columnPath;
    private final String columnFilename;
    private final String columnPropertyKey;
    private final String columnPropertyValue;
    private final String fileStorageBase;
    private final String fileStoragePath;
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseRuleFileInserter.class.getName());
    private static final String PARAM_VALIDATION_MSG = "The parameter does not validate against the syntax pattern (%s): {%s}";
    private static final String SQL_LOG_MSG = "SQL Error (%d): %s";
    
    public DatabaseRuleFileInserter(final String jdbcDriver, final String jdbcUrl, final String jdbcUser, final String jdbcPassword, final String table, final String columnPath, final String columnFilename, final String columnPropertyKey, final String columnPropertyValue, final String fileStorageBase, final String fileStoragePath) {
        this.jdbcDriver = jdbcDriver;
        this.jdbcUrl = jdbcUrl;
        this.jdbcUser = jdbcUser;
        this.jdbcPassword = jdbcPassword;
        this.table = table;
        this.columnPath = columnPath;
        this.columnFilename = columnFilename;
        this.columnPropertyKey = columnPropertyKey;
        this.columnPropertyValue = columnPropertyValue;
        this.fileStorageBase = fileStorageBase;
        this.fileStoragePath = fileStoragePath;
    }

    
    public void convertFromFileToDatabase() throws IOException, ClassNotFoundException, SQLException {
        final File directory = new File(this.fileStorageBase, this.fileStoragePath);
        if (!directory.exists()) throw new IllegalArgumentException("Directory does not exist: "+directory.getAbsolutePath());
        if (!directory.isDirectory()) throw new IllegalArgumentException("Directory exist but is not a real directory (maybe a file?): "+directory.getAbsolutePath());
        final File[] files = directory.listFiles();
        if (files.length > 0) {
            final String path = removeTrailingSlash(this.fileStoragePath);
            Class.forName(this.jdbcDriver);
            Connection connection = null; 
            PreparedStatement preparedStatement = null;

            try {
                connection = DriverManager.getConnection(this.jdbcUrl, this.jdbcUser, this.jdbcPassword);
                connection.setAutoCommit(false);
                preparedStatement = connection.prepareStatement("INSERT INTO "+this.table+" ("+this.columnPath+", "+this.columnFilename+", "+this.columnPropertyKey+", "+this.columnPropertyValue+") VALUES (?,?,?,?)");
                for (File file : files) {
                    if (file.isFile()) {
                        if (!file.canRead()) 
                            throw new IllegalArgumentException("Unable to read rule definition file: "+file.getAbsolutePath());
                        final Properties properties = new Properties();
                        
                        try(BufferedInputStream in = new BufferedInputStream( new FileInputStream(file) )) {
                            properties.load(in);
                            Enumeration<?> keys = properties.propertyNames();
                            while(keys.hasMoreElements()){
                                final String key = (String) keys.nextElement();
                                final String value = properties.getProperty(key);
                                
                                preparedStatement.setString(1, path);
                                preparedStatement.setString(2, file.getName());
                                preparedStatement.setString(3, key);
                                preparedStatement.setString(4, value);
                                preparedStatement.addBatch();
                                
                                preparedStatement.clearParameters();
                            }
                            preparedStatement.executeBatch();
                            LOGGER.log(Level.INFO, "Finished with file {0}", file.getAbsolutePath());
                        }
                    }
                }
                connection.commit();
            } catch (RuntimeException | IOException | SQLException e) {
                if (connection != null) try { connection.rollback(); } catch (SQLException ignored) {
                    LOGGER.log(Level.FINE, String.format( SQL_LOG_MSG, ignored.getErrorCode(), ignored.getSQLState() ));
                }
                throw e;
            } finally {
                if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException ignored) {
                    LOGGER.log(Level.FINE, String.format( SQL_LOG_MSG, ignored.getErrorCode(), ignored.getSQLState() ));
                }
                if (connection != null) try { connection.close(); } catch (SQLException ignored) {
                    LOGGER.log(Level.FINE, String.format( SQL_LOG_MSG, ignored.getErrorCode(), ignored.getSQLState() ));
                }
            }
        }
    }
    
    
    public static void main(String[] args) {
        if (args.length != 11) {
            LOGGER.log(Level.INFO, Version.tagLine());
            LOGGER.log(Level.SEVERE, "This tool imports existing security rule properties files into the database");
            LOGGER.log(Level.SEVERE, "Provide the following arguments: jdbcDriver jdbcUrl jdbcUser jdbcPassword table columnPath columnFilename columnPropertyKey columnPropertyValue fileStorageBase fileStoragePath");
            System.exit(-1);
        }
        final Pattern allowedDatabaseCharacters = Pattern.compile(AbstractSqlRuleFileLoader.VALID_DATABASE_SYNTAX);
        final String jdbcDriver = args[0];
        final String jdbcUrl = args[1];
        final String jdbcUser = args[2];
        final String jdbcPassword = args[3];
        final String table = args[4];
        final Matcher matcher = allowedDatabaseCharacters.matcher(table);
        if (!matcher.matches())
            showErrorAndExit(String.format(PARAM_VALIDATION_MSG, allowedDatabaseCharacters, table));
        final String columnPath = args[5]; matcher.reset(columnPath);
        if (!matcher.matches())
            showErrorAndExit(String.format(PARAM_VALIDATION_MSG, allowedDatabaseCharacters, columnPath));
        final String columnFilename = args[6]; matcher.reset(columnFilename); if (!matcher.matches()) showErrorAndExit(String.format(PARAM_VALIDATION_MSG,allowedDatabaseCharacters, columnFilename));
        final String columnPropertyKey = args[7]; matcher.reset(columnPropertyKey); if (!matcher.matches()) showErrorAndExit(String.format(PARAM_VALIDATION_MSG,allowedDatabaseCharacters, columnPropertyKey));
        final String columnPropertyValue = args[8]; matcher.reset(columnPropertyValue); if (!matcher.matches()) showErrorAndExit(String.format(PARAM_VALIDATION_MSG, allowedDatabaseCharacters, columnPropertyValue));
        final String fileStorageBase = args[9];
        final String fileStoragePath = args[10];
        try {
            final DatabaseRuleFileInserter converter = new DatabaseRuleFileInserter(jdbcDriver, jdbcUrl, jdbcUser, jdbcPassword, table, columnPath, columnFilename, columnPropertyKey, columnPropertyValue, fileStorageBase, fileStoragePath);
            converter.convertFromFileToDatabase();
        } catch (IOException | ClassNotFoundException | SQLException e) {
            String message = e.getMessage();
            if (message == null || "null".equalsIgnoreCase(message)) {
                e.printStackTrace();
            }
            showErrorAndExit(message);
        }
    }

    private static void showErrorAndExit(final String message) {
        LOGGER.log(Level.SEVERE, "ERROR: {0}", message);
        System.exit(-2);
    }

    private String removeTrailingSlash(final String value) {
        if (value == null) return null;
        if (value.endsWith("/") || value.endsWith("\\")) return value.substring(0,value.length()-1);
        return value;
    }
    
}
