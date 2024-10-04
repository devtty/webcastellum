package org.webcastellum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class EncodingPerformanceTester {
    
    private final String filename;
    
    
    public EncodingPerformanceTester(final String filename) {
        if (filename == null) throw new NullPointerException("filename must not be null");
        this.filename = filename;
    }
    
    public void execute() throws IOException {
        System.out.println("Start ================================= "+filename);
        final StringBuilder text = new StringBuilder();
        BufferedReader reader;
        
        reader = null;
        try {
            reader = new BufferedReader( new FileReader(filename) );
            char[] buffer = new char[1024];
            int read = 0;
            while ( (read=reader.read(buffer)) != -1) {
                if (read > 0) text.append(buffer, 0, read);
            }
        } finally {
            if (reader != null) try { reader.close(); } catch (IOException ignored) {}
        }
        System.out.println("Loaded text with length: "+text.length());
        System.out.println("-----");

        
        long start;
        long duration; 
        
        // time decoding permutation stuff
        start = System.currentTimeMillis();
        try {
            System.out.println("Performing decoding permutation");
            final Permutation permutation = ServerUtils.permutateVariants(text.toString(), true, (byte)3);
            System.out.println("Received permutations: "+permutation.size());
        } catch (Exception e) {
            System.out.println("Caught exception: "+e);
        }
        duration = System.currentTimeMillis()-start;
        System.out.println("Duration for decoding permutation with exception-handling: "+duration+" ms");
        
        
    }
    
    
    
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println(Version.tagLine());
            System.err.println("This tool tests the performance of several decoding strategies for encoded text");
            System.err.println("Please provide the following argument: filenameText");
            System.exit(-1);
        }
        final EncodingPerformanceTester tester = new EncodingPerformanceTester(args[0]);
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
        tester.execute();
    }
    
    
    
    
}
