package org.webcastellum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.crypto.Cipher;
import javax.servlet.http.HttpServletResponse;



public final class UrlEncryptionPerformanceTester {
    
    private static final int USER_COUNT = 100; // = simulated user count (= thread count)
    
    private static final int USER_CLICK_COUNT = 200; // = simulated pages the user requests (one after another)
    private static final int URL_COUNT_PER_PAGE = 150; // = simulated links per page
    private static final int USER_THINK_TIME = 1000; // = simulated user think time between consequtive requests
    private static final int USER_RAMPUP_DELAY = 15; // = place every x ms a new user to the ramp and start it
    
    private static final String CONTEXT = "PerformanceTestSimulation";
    private static final Random RANDOM = new Random();
    
    
    public static final void main(String[] args) throws Exception {
        final List/*<Long>*/ times = Collections.synchronizedList(new ArrayList());

        // line up each user
        final List/*<Runnable>*/ users = new ArrayList(USER_COUNT);
        for (int i=0; i<USER_COUNT; i++) {
            final Runnable user = new Runnable() {
                public void run() {
                    try {
                        // rather immutable things across all requests of a user:
                        final Cipher cipher = CryptoUtils.getCipher();
                        final CryptoKeyAndSalt key = CryptoUtils.generateRandomCryptoKeyAndSalt(false);
                        final String currentContextPathAccessed = "/"+CONTEXT;
                        final String currentServletPathAccessed = "/index.jsp";
                        final String currentRequestUrlToCompareWith = "http://www.example.com"+currentContextPathAccessed+currentServletPathAccessed;

                        for (int cc=0; cc<USER_CLICK_COUNT; cc++) {
                            // page relevant stuff:
                            // pre-create some random URLs
                            final List/*<String>*/ urls = new ArrayList(URL_COUNT_PER_PAGE);
                            for (int i=0; i<URL_COUNT_PER_PAGE; i++) urls.add( createUrl() );
                            // pre-create other stuff
                            final HttpServletResponse response = new ResponseProtectionPerformanceTester.ResponseMock();
                            // go
                            final long timer = System.currentTimeMillis();
                            for (final Iterator iter = urls.iterator(); iter.hasNext();) {
                                ResponseUtils.encryptQueryStringInURL(
                                        currentRequestUrlToCompareWith, 
                                        currentContextPathAccessed, 
                                        currentServletPathAccessed, 
                                        (String)iter.next(),
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
                            }
                            if (cc > 0) times.add(new Long(System.currentTimeMillis()-timer));// ignoring the first request to "warm up" let the mocks warm up...
                            // sleep
                            try { Thread.sleep(USER_THINK_TIME); } catch (InterruptedException e) { System.out.println("Sleep was interrupted: "+e.getMessage()); }
                        }
                    } catch (Exception e) { System.err.println("Exception: "+e); }
                }
            };
            users.add(user);
        }

        // start each user
        final List/*<Thread>*/ userThreads = new ArrayList(USER_COUNT);
        for (final Iterator iter = users.iterator(); iter.hasNext();) {
            final Thread userThread = new Thread((Runnable)iter.next());
            userThreads.add(userThread);
            userThread.start();
            // sleep
           if (USER_RAMPUP_DELAY > 0)  try { Thread.sleep(USER_RAMPUP_DELAY); } catch (InterruptedException e) { System.out.println("Sleep was interrupted: "+e.getMessage()); }
        }

        // collect users after they've finished
        for (final Iterator iter = userThreads.iterator(); iter.hasNext();) {
            ((Thread)iter.next()).join();
        }
            
        double total = 0;
        for (final Iterator iter = times.iterator(); iter.hasNext();) {
            total += ((Integer)iter.next()).intValue();
        }
        final long average = Math.round(total / times.size());
        System.out.println(average+" ms average URL encrpytion per-request overhead for a page with "+URL_COUNT_PER_PAGE+" links and "+USER_COUNT+" simultaneous users with a user think time of "+USER_THINK_TIME+" ms and a click count of "+USER_CLICK_COUNT+" for each user with a ramp-up delay of "+USER_RAMPUP_DELAY);
    }
    
    
    private static final String createUrl() {
        final StringBuilder url = new StringBuilder("/"+CONTEXT+"/");
        url.append( HoneylinkUtils.generateFilename(RANDOM) );
        url.append( HoneylinkUtils.generateParameters(RANDOM,1,7) );
        return url.toString();
    }

}
