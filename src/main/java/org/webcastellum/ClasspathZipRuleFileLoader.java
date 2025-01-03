package org.webcastellum;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.FilterConfig;

public final class ClasspathZipRuleFileLoader extends AbstractFilebasedRuleFileLoader {
    
    public static final String PARAM_RULE_FILES_CLASSPATH_REFERENCE = "RuleFilesClasspathReference";
    
    private static final String DEFAULT = "org/webcastellum/rules.zip";
    
    private String classpathReference = DEFAULT; // pre-initialized to have a default when testing via mocks
        
    
    
    @Override
    public void setFilterConfig(final FilterConfig filterConfig) throws FilterConfigurationException {
        super.setFilterConfig(filterConfig);
        final ConfigurationManager configManager = ConfigurationUtils.createConfigurationManager(filterConfig);
        this.classpathReference = ConfigurationUtils.extractOptionalConfigValue(configManager, PARAM_RULE_FILES_CLASSPATH_REFERENCE, DEFAULT);
    }
    
    
    public RuleFile[] loadRuleFiles() throws RuleLoadingException {
        if (this.classpathReference == null) throw new IllegalStateException("FilterConfig must be set before loading rules files");
        if (this.path == null) throw new IllegalStateException("Path must be set before loading rules files");
        ZipInputStream zipper = null;
        try {
            final List<RuleFile> rules = new ArrayList<>();
            final InputStream input = getClass().getClassLoader().getResourceAsStream(this.classpathReference);
            if (input == null) throw new FileNotFoundException("Unable to locate zipped rule file on classpath: "+this.classpathReference);
            zipper = new ZipInputStream( new BufferedInputStream(input) );
            ZipEntry zipEntry = null;
            do {
                zipEntry = zipper.getNextEntry();
                if (zipEntry != null) {
                    if (!zipEntry.isDirectory()) {
                        String name = zipEntry.getName();
                        if (name != null && isMatchingSuffix(name)) {
                            // remove leading slash if there is one
                            if (name.startsWith("/") && name.length()>1) name = name.substring(1);
                            if (name.startsWith(this.path)) { //= OK, we've got a relevant file here
                                final Properties properties = new Properties();
                                properties.load(zipper);
                                rules.add( new RuleFile(name,properties) );
                            }
                        }
                    }
                    zipper.closeEntry();
                }
            } while (zipEntry != null);
            return (RuleFile[])rules.toArray(new RuleFile[0]);
        } catch (Exception e) {
            throw new RuleLoadingException(e);
        } finally {
            if (zipper != null) try { zipper.close(); } catch(IOException ignored) {}
        }
    }
    
    
}
