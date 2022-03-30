package org.webcastellum;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

public final class StringDecodingTester {
    
    
    
    
    private final String testString;
    
    public StringDecodingTester(final String testString) {
        if (testString == null) throw new NullPointerException("testString must not be null");
        this.testString = testString;
        System.out.println("Test string is: "+this.testString);
    }
    
    public void run(final boolean decode) {
        final Map/*<String.Charset>*/ charsets = Charset.availableCharsets();
        for (final Iterator iter = charsets.values().iterator(); iter.hasNext();) {
            final Charset charset = (Charset) iter.next();
            final String message = "Charset "+charset;
            System.out.print(message);
            for (int i = message.length(); i<40; i++) System.out.print(" ");
            try {
                if (decode) System.out.println(URLDecoder.decode(this.testString,charset.name()));
                else System.out.println(URLEncoder.encode(this.testString,charset.name()));
            } catch (Exception e) {
                System.out.println("EXCEPTION: "+e.getMessage());
            }
        }
    }

    

    public void permutate() {
        final Permutation permutation = ServerUtils.permutateVariants(this.testString,true,Byte.MAX_VALUE);
        System.out.println("Standard variants: ");
        for (final Iterator iter = permutation.getStandardPermutations().iterator(); iter.hasNext();) {
            System.out.println("\t"+iter.next());
        }
        System.out.println("Additional non-standard variants: ");
        for (final Iterator iter = permutation.getNonStandardPermutations().iterator(); iter.hasNext();) {
            System.out.println("\t"+iter.next());
        }
    }
    

    
    
    public void decodeBroken() {
        final String decoded = ServerUtils.decodeBrokenValue(this.testString);
        System.out.println("Decoded broken: "+decoded);
        System.out.println("Decoded twice broken: "+ServerUtils.decodeBrokenValue(decoded));
    }
    
    
    public static final void main(String[] args) {
        if (args.length != 2) {
            System.out.println(Version.tagLine());
            System.err.println("This tool encodes or decodes the given argument string with all available charsets on this platform.\nAlso it is able to permutate the given testString by applying several decoding strategies (showing results only when they're different).\nAlso this tool is able to encode even broken encodings.");
            System.err.println("Provide the following arguments: encode|decode|permutate|decodeBroken testString");
            System.exit(-1);
        }
        final StringDecodingTester tester = new StringDecodingTester(args[1].trim());
        if ("permutate".equalsIgnoreCase(args[0].trim())) {
            tester.permutate();
        } else if ("decodeBroken".equalsIgnoreCase(args[0].trim())) {
            tester.decodeBroken();
        } else tester.run("decode".equalsIgnoreCase(args[0].trim()));
    }
    
}
