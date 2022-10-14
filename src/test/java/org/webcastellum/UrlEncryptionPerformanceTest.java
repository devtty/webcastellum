package org.webcastellum;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletResponse;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

public class UrlEncryptionPerformanceTest {
    private static final int USER_COUNT = 10; // = simulated user count (= thread count)
    
    private static final int USER_CLICK_COUNT = 200; // = simulated pages the user requests (one after another)
    private static final int URL_COUNT_PER_PAGE = 150; // = simulated links per page
    private static final int USER_THINK_TIME = 100; // = simulated user think time between consequtive requests
    private static final int USER_RAMPUP_DELAY = 15; // = place every x ms a new user to the ramp and start it
    
    private static final String CONTEXT = "PerformanceTestSimulation";
    private static final Random RANDOM = new Random();
    
    private static final Logger LOGGER = Logger.getLogger(UrlEncryptionPerformanceTest.class.getName());
    
    public UrlEncryptionPerformanceTest() {
    }
    
    @Test
    @Ignore(value = "too long for build cycle")
    public void testPerformanceWithOriginalValues(){
        OptionalDouble result = testPerformance(100, 200, 150, 1000, 15);
        assertTrue(result.isPresent());
        assertTrue(result.getAsDouble() < 15.0);
    }
    
    @Test
    public void testPerformanceAb(){
        OptionalDouble result = testPerformance(10, 200, 150, 100, 15);
        assertTrue(result.isPresent());
        assertTrue(result.getAsDouble() < 15.0);
    }
    
    private OptionalDouble testPerformance(int userCount, int clickCount, int urlCount, int thinkTime, int rampup){
        final List<Long> times = Collections.synchronizedList(new ArrayList());

        // line up each user
        final List<Runnable> users = new ArrayList(userCount);
        for (int i=0; i<userCount; i++) {
            final Runnable user = () -> {
                try {
                    // rather immutable things across all requests of a user:
                    final Cipher cipher = CryptoUtils.getCipher();
                    final CryptoKeyAndSalt key = CryptoUtils.generateRandomCryptoKeyAndSalt(false);
                    final String currentContextPathAccessed = "/"+CONTEXT;
                    final String currentServletPathAccessed = "/index.jsp";
                    final String currentRequestUrlToCompareWith = "http://www.example.com"+currentContextPathAccessed+currentServletPathAccessed;
                    for (int cc = 0; cc<clickCount; cc++) {
                        // page relevant stuff:
                        // pre-create some random URLs
                        final List<String> urls = new ArrayList(urlCount);
                        for (int i1 = 0; i1 < urlCount; i1++) {
                            urls.add( createUrl() );
                        }
                        // pre-create other stuff
                        final HttpServletResponse response = new ResponseProtectionPerformanceTester.ResponseMock();
                        // go
                        final long timer = System.currentTimeMillis();
                        urls.forEach(u -> {
                            ResponseUtils.encryptQueryStringInURL(
                                    currentRequestUrlToCompareWith,
                                    currentContextPathAccessed,
                                    currentServletPathAccessed,
                                    u,
                                    false,
                                    false,
                                    Boolean.FALSE,
                                    false, 
                                    "___ENCRYPTED___",
                                    cipher,
                                    key, 
                                    false,
                                    false,
                                    true,
                                    response,
                                    false);
                        });
                        if (cc > 0) times.add(System.currentTimeMillis()-timer);// ignoring the first request to "warm up" let the mocks warm up...
                        // sleep
                        try {
                            Thread.sleep(thinkTime); 
                        } catch (InterruptedException e) {
                            LOGGER.log(Level.INFO, "Sleep was interrupted: {0}", e.getMessage()); 
                            Thread.currentThread().interrupt();
                        }
                    }
                }catch (NoSuchAlgorithmException | NoSuchPaddingException e) { 
                    LOGGER.log(Level.SEVERE, "Exception: {0}", e); 
                }
            };
            users.add(user);
        }

        // start each user
        final List<Thread> userThreads = new ArrayList(userCount);
        for (final Iterator iter = users.iterator(); iter.hasNext();) {
            final Thread userThread = new Thread((Runnable)iter.next());
            userThreads.add(userThread);
            userThread.start();
            // sleep
            if (rampup > 0)  
               try {
                   Thread.sleep(rampup); 
               } catch (InterruptedException e) { 
                   LOGGER.log(Level.INFO, "Sleep was interrupted: {0}", e.getMessage()); 
                   Thread.currentThread().interrupt();
               }
        }

        // collect users after they've finished
        for (final Iterator<Thread> iter = userThreads.iterator(); iter.hasNext();) {
            try {
                iter.next().join();
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, "Exception", ex);
            }
        }
        
        OptionalDouble average = times.stream().mapToDouble(a -> a).average();

        LOGGER.log(Level.INFO, "{0} ms average URL encrpytion per-request overhead for a page with {1} links and {2} simultaneous users with a user think time of {3} ms and a click count of {4} for each user with a ramp-up delay of {5}", new Object[]{average, URL_COUNT_PER_PAGE, USER_COUNT, USER_THINK_TIME, USER_CLICK_COUNT, USER_RAMPUP_DELAY});
        return average;
        
    }
    
    private static final String createUrl() {
        final StringBuilder url = new StringBuilder("/"+CONTEXT+"/");
        url.append( HoneylinkUtils.generateFilename(RANDOM) );
        url.append( HoneylinkUtils.generateParameters(RANDOM,1,7) );
        return url.toString();
    }
    
}
