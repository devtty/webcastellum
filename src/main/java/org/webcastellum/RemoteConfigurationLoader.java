package org.webcastellum;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.FilterConfig;


public class RemoteConfigurationLoader implements ConfigurationLoader {

    private static final Logger LOGGER = Logger.getLogger(RemoteConfigurationLoader.class.getName());
    
    private FilterConfig filterConfig;
    
    @Override
    public String getConfigurationValue(String key) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(new URI("https://devtty.de/wc/1A42/config.json")).GET().build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            
            //quickanddirty replace as soon as possible
            int start = response.body().indexOf(key);
            String r = response.body().substring(start).substring(key.length()+ 4);
            r = r.substring(0, r.indexOf("\""));
            LOGGER.log(Level.FINE, "Response: ", response.body());
            
            return r;
            
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            Logger.getLogger(RemoteConfigurationLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(key.equals("sadf"))
                return "sdf";
            else
                return "";
    }

    @Override
    public void setFilterConfig(FilterConfig filterConfig) throws FilterConfigurationException {
        LOGGER.log(Level.FINE, "Setting filterConfig");
        this.filterConfig = filterConfig;
    }
    
    // Java5 @Override
    public String toString() {
        return "web.xml filter init parameters";
    }
    
}
