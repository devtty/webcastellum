package org.webcastellum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegularExpressionPerformanceTester {
    
    private final String filename;
    private final List/*<String>*/ regExpFiles;
    private final SortedSet/*<Result>*/ results = new TreeSet();
    
    
    public RegularExpressionPerformanceTester(final String filename, final List/*<String>*/ regExpFiles) {
        if (filename == null) throw new NullPointerException("filename must not be null");
        if (regExpFiles == null) throw new NullPointerException("regExpFiles must not be null");
        this.filename = filename;
        this.regExpFiles = regExpFiles;
    }
    
    public void execute() throws IOException {
        for (final Iterator iter = this.regExpFiles.iterator(); iter.hasNext();) {
            final String regExpFile = (String) iter.next();
            testPerformance(regExpFile);
        }
    }
    
    public SortedSet/*<Result>*/ getResults() {
        return new TreeSet(this.results);
    }
    
    private void testPerformance(final String regExpFile) throws IOException {
        System.out.println("Start ================================= "+regExpFile);
        // TODO: Java5 use StringBuilder
        final StringBuilder expression = new StringBuilder();
        final StringBuilder content = new StringBuilder();
        BufferedReader reader;
        
        reader = null;
        try {
            reader = new BufferedReader( new FileReader(regExpFile) );
            char[] buffer = new char[1024];
            int read = 0;
            while ( (read=reader.read(buffer)) != -1) {
                if (read > 0) expression.append(buffer, 0, read);
            }
        } finally {
            if (reader != null) try { reader.close(); } catch (IOException ignored) {}
        }
        System.out.println("Loaded regular expression pattern with length: "+expression.length());
        long start = System.currentTimeMillis();
        final Pattern pattern = Pattern.compile(expression.toString().trim());
        System.out.println("Compiled regular expression pattern in: "+(System.currentTimeMillis()-start)+" ms");
        
        reader = null;
        try {
            reader = new BufferedReader( new FileReader(filename) );
            String line;
            while ( (line=reader.readLine()) != null ) {
                content.append(line);
            }
        } finally {
            if (reader != null) try { reader.close(); } catch (IOException ignored) {}
        }
        
        System.out.println("Read content with length: "+content.length());
        
        Matcher matcher;
        
        start = System.currentTimeMillis();
        System.out.println("Creating matcher");
        matcher = pattern.matcher(content);
        System.out.println("Matcher created");
        System.out.println("Found: "+matcher.find());
        final long duration = System.currentTimeMillis()-start;
        System.out.println("Duration: "+duration+" ms");
        this.results.add(new Result(regExpFile,duration));
        
        
        
/*
        System.out.println("2nd run:");
        start = System.currentTimeMillis();
        System.out.println("Creating matcher");
        matcher = pattern.matcher(content);
        System.out.println("Matcher created");
        System.out.println("Found: "+matcher.find());
        System.out.println("Duration: "+(System.currentTimeMillis()-start));
 */
    }
    
    
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println(Version.tagLine());
            System.err.println("This tool tests the performance of regular expression matching");
            System.err.println("Please provide the following arguments: filenameText filenameRegexp [more regexp files optionally]");
            System.exit(-1);
        }
        final List/*<String>*/ regExpFiles = new ArrayList(Arrays.asList(args));
        regExpFiles.remove(0);
        final RegularExpressionPerformanceTester tester = new RegularExpressionPerformanceTester(args[0], regExpFiles); // TODO Java5 use varargs
        tester.execute();
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        final SortedSet/*<Result>*/ results = tester.getResults();
        for (Iterator iter = results.iterator(); iter.hasNext();) {
            final Result result = (Result) iter.next();
            System.out.println(result);
        }
    }
    
    
    
    
    
    public static final class Result implements Comparable/*<Result>*/ {
        private final String name;
        private final long duration;
        public Result(final String name, final long duration) {
            this.name = name;
            this.duration = duration;
            System.out.println("name="+name);
        }
        //1.5@Override
        public String toString() {
            return this.name+": "+this.duration+" ms";
        }
        //1.5@Override
        public final int hashCode() {
            int hash = 7;
            hash = 31 * hash + (this.name != null ? this.name.hashCode() : 0);
            hash = 31 * hash + (int)(duration ^ (duration >>> 32));
            return hash;
        }
        //1.5@Override
        public final boolean equals(Object obj) {
            if (this == obj) return true;
            if ((obj == null) || (obj.getClass() != this.getClass())) return false;
            Result other = (Result)obj;
            // short-cut to compare by object-identity first (before using equals then)
            return (duration == other.duration) && (name == other.name || (name != null && name.equals(other.name)));
        }
        public final int compareTo(/*Result*/Object other) {
            // here we enforce a natural sort order that uses the "identification" of this request definition,
            // so that the user can name (identify) the files using numbers for example (00_xxx, 01_xxx, 02_xxxx)
            // to have the sorting one desires....
            final long durationLeft = this.duration;
            final long durationRight = ((Result)other).duration;
            final int comparison = (durationLeft<durationRight? -1 : (durationLeft==durationRight ? 0 : 1));
            if (comparison != 0) return comparison;
            return this.name.compareTo(((Result)other).name);
        }
    }
    
    
    
    
    
    /*
     * TODO: Apply these performance optimizations in WebCastellum for regular expression matchings:
     *=========================================================
     *
     *      1) Use pre-compiled Patterns even for simple String functions (each String convenience function has a pre-compiled Pattern equivanlent:
     *              String.matches("[regex]");      -->     Pattern.compile("[regex]").matcher(input).matches();
     *              String.replaceAll("[regex]");    -->     Pattern.compile("[regex]").matcher(input).replaceAll("[replacement]");
     *              String.replaceFirst("[regex]");  -->    Pattern.compile("[regex]").matcher(input).replaceFirst("[replacement]");
     *              String.split("[regex]", [n]);       -->    Pattern.compile("[regex]").split(input, [n]);
     *              String.split("[regex]");            -->     Pattern.compile("[regex]").split(input);
     *  
     *      2) Avoid using capturing-groups ( ) where they are not required
     *              When parantheses are needed, try declaring them as non-capturing groups using ?: after the opening parantheses:   (?:aaaa|bbbb|cccc)   ==> well, this does not really affect performance
     *          Better: check paratheses in all expressions if they are not misplaced and have too much content...
     *
     *      3) Try using possessive or reluctant quantifiers instead of greedy quantifiers as suggested here: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5058495
     *          might help in
     *              response-modifications/*.properties  ruleset
     *
     */
    
    
    // Java performance bug (bad algorithm taking exponential time) in RegEx
    // see: http://perlmonks.org/index.pl?node_id=502408
    // and maybe: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5058495
    // and maybe: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4704828
    public static final class MeanTestcaseForRegEx {
        private static final String DATA = "foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo fo";
        private static final String PATTERN_GOOD_1 = "^(\\s*foo\\s*)*";
        private static final String PATTERN_GOOD_2 = "^(\\s+foo\\s+)*$";
        private static final String PATTERN_GOOD_3 = "^(\\s*+foo\\s*+)*+$"; // same as PATTERN_BAD_1 but using possesive quantifiers instead of greedy quantifiers
        private static final String PATTERN_BAD_1 = "^(\\s*foo\\s*)*$";
        private static final String PATTERN_BAD_2 = "^(\\s*foo\\s*)+$";
        private static final String PATTERN_BAD_3 = "^( *foo *)*$";
        private static final void check(final String patternValue) {
            final Pattern pattern = Pattern.compile(patternValue);
            System.out.println("Compiled pattern: "+patternValue);
            final Matcher matcher = pattern.matcher(DATA);
            System.out.println("Created matcher");
            final boolean found = matcher.find();
            System.out.println("Finished (found="+found+")");
            System.out.println("-------------");
        }
        public static final void main(String[] args) {
            System.out.println("Starting good 1"); check(PATTERN_GOOD_1);
            System.out.println("Starting good 2"); check(PATTERN_GOOD_2);
            System.out.println("Starting good 3"); check(PATTERN_GOOD_3);
            System.out.println("Starting bad 1"); check(PATTERN_BAD_1);
            System.out.println("Starting bad 2"); check(PATTERN_BAD_2);
            System.out.println("Starting bad 3"); check(PATTERN_BAD_3);
        }
    }
    
}
