package org.webcastellum;

import java.net.MalformedURLException;

public final class PermutationPerformanceTester {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println(Version.tagLine());
            System.err.println("This tool tests the performance of decoding string permutations (using highest permuation level)");
            System.err.println("Please provide the following arguments: size-of-test-string-in-bytes");
            System.exit(-1);
        }
        final int size = Integer.parseInt(args[0]);
        final PermutationPerformanceTester tester = new PermutationPerformanceTester(size);
        tester.test();
    }


    
    private final int size;

    public PermutationPerformanceTester(final int size) throws MalformedURLException {
        if (size <= 0) throw new IllegalArgumentException("size must be positive");
        this.size = size;
    }

    
    public void test() {
        for (int i=0; i<5; i++) {
            long timer = System.currentTimeMillis();
            ServerUtils.permutateVariants(LargeFormPostRequestTester.createTestdata(this.size), true, Byte.MAX_VALUE);
            if (i>0) System.out.println((System.currentTimeMillis()-timer)+" ms");
        }
    }
    
    
}
