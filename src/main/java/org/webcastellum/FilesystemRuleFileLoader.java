package org.webcastellum;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.servlet.FilterConfig;

public final class FilesystemRuleFileLoader extends AbstractFilebasedRuleFileLoader {
    
    public static final String PARAM_RULE_FILES_BASE_PATH = "RuleFilesBasePath";

    
    private String base;
        
    
    
    @Override
    public void setFilterConfig(final FilterConfig filterConfig) throws FilterConfigurationException {
        super.setFilterConfig(filterConfig);
        final ConfigurationManager configManager = ConfigurationUtils.createConfigurationManager(filterConfig);
        this.base = ConfigurationUtils.extractMandatoryConfigValue(configManager, PARAM_RULE_FILES_BASE_PATH);
    }
    
    
    @Override
    public RuleFile[] loadRuleFiles() throws RuleLoadingException {
        if (this.base == null) throw new IllegalStateException("FilterConfig must be set before loading rules files");
        if (this.path == null) throw new IllegalStateException("Path must be set before loading rules files");
        try {
            final File directory = new File(this.base, this.path);
            if (!directory.exists()) throw new IllegalArgumentException("Directory does not exist: "+directory.getAbsolutePath());
            if (!directory.isDirectory()) throw new IllegalArgumentException("Directory exists but is not a directory (maybe just a file?): "+directory.getAbsolutePath());
            final List<RuleFile> rules = new ArrayList<>();
            final File[] files = directory.listFiles();
            for (File file : files) {
                if (file.isFile() && isMatchingSuffix(file.getName())) {
                    if (!file.canRead()) throw new FileNotFoundException("Unable to read rule definition file: "+file.getAbsolutePath());
                    final Properties properties = new Properties();
                    BufferedInputStream input = null;
                    try {
                        input = new BufferedInputStream( new FileInputStream(file) );
                        properties.load(input);
                        rules.add( new RuleFile(file.getAbsolutePath(),properties) );
                    } finally {
                        if (input != null) try { input.close(); } catch (IOException ignored) {}
                    }
                }
            }
            return (RuleFile[])rules.toArray(RuleFile[]::new);
        } catch (Exception e) {
            throw new RuleLoadingException(e);
        }
    }
    
    
}
