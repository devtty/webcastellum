package org.webcastellum;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public final class ZipScannerUtils {
    
    private static final String[] EMPTY = {};

    public static final String[] extractNameAndCommentStrings(final InputStream input) throws IOException {
        File temp = null;
        try {
            temp = TempFileUtils.writeToTempFile(input);
            return extractNameAndCommentStrings(temp);
        } finally {
            if (temp != null) TempFileUtils.deleteTempFile(temp);
        }
    }
    
    
    public static final String[] extractNameAndCommentStrings(final File file) throws IOException {
        try(ZipFile zipFile = new ZipFile(file)){
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            final Set<String> nameAndComments = new HashSet<>();

            while(entries.hasMoreElements()){
                ZipEntry ze = entries.nextElement();
                String name = ze.getName();
                    String comment = ze.getComment();
                    if (name != null)
                        nameAndComments.add(name);
                    if (comment != null)
                        nameAndComments.add(comment);
            }

            return nameAndComments.isEmpty() ? null : (String[]) nameAndComments.toArray(String[]::new);
        }catch(ZipException ex){
            //not a ZIP file - so no ZIP comments
            return EMPTY;
        }
    }
    
    public static final boolean isZipBomb(final InputStream input, final long thresholdTotalSize, final long thresholdFileCount, final double thresholdRatio) throws IOException {
        File temp = null;
        try {
            temp = TempFileUtils.writeToTempFile(input);
            return isZipBomb(temp, thresholdTotalSize, thresholdFileCount, thresholdRatio);
        } finally {
            if (temp != null) TempFileUtils.deleteTempFile(temp);
        }
    }
    
    public static final boolean isZipBomb(final File file, final long thresholdTotalSize, final long thresholdFileCount, final double thresholdRatio) throws IOException {
        if (thresholdTotalSize < 0) throw new IllegalArgumentException("thresholdTotalSize must not be negative");
        if (thresholdFileCount < 0) throw new IllegalArgumentException("thresholdFileCount must not be negative");
        
        try (ZipFile zipFile = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            long countedFiles = 0;
            long countedTotalBytes = 0;

            while (entries.hasMoreElements()) {
                ZipEntry ze = entries.nextElement();
                InputStream in = new BufferedInputStream(zipFile.getInputStream(ze));

                countedFiles++;

                int nBytes = -1;
                byte[] buffer = new byte[2048];
                long totalSizeEntry = 0;

                while ((nBytes = in.read(buffer)) > 0) {
                    totalSizeEntry += nBytes;
                    countedTotalBytes += nBytes;

                    double compressionRatio = totalSizeEntry / ze.getCompressedSize();

                    if (compressionRatio > (double) thresholdRatio) {
                        return true;
                    }
                }

                if (countedTotalBytes > thresholdTotalSize) {
                    return true;
                }

                if (countedFiles > thresholdFileCount && thresholdFileCount > 0) {
                    return true;
                }
            }
        } catch (ZipException ex) {
            // nota ZIP file - so no ZipBomb
            return false;
        }
         return false;
    }

    private ZipScannerUtils() {}
    
    
    
    

    
    /** /
    // ========= FOR ATTACK SIMULATION ONLY =========
    public static final void createZipBomb(final File file, final long size) throws IOException {
        ZipOutputStream zipper = null;
        try {
            zipper = new ZipOutputStream( new BufferedOutputStream( new FileOutputStream(file) ) );
            zipper.setLevel(Deflater.BEST_COMPRESSION);
            ZipEntry entry;
            // bomb
            entry = new ZipEntry("bomb");
            zipper.putNextEntry(entry);
            for (long i=0; i<size; i++) zipper.write(0);
        } finally {
            if (zipper != null) try { zipper.close(); } catch(IOException e) {}
        }
    }
    public static final void createZipCommentBasedInjection(final File file, final String maliciousComment) throws IOException {
        ZipOutputStream zipper = null;
        try {
            zipper = new ZipOutputStream( new BufferedOutputStream( new FileOutputStream(file) ) );
            zipper.setComment(maliciousComment);
            ZipEntry entry;
            // eins
            entry = new ZipEntry("injection.one");
            entry.setComment(maliciousComment);
            zipper.putNextEntry(entry);
            for (long i=0; i<2500; i++) zipper.write(0);
            // zwei
            entry = new ZipEntry("injection.two");
            entry.setComment(maliciousComment);
            zipper.putNextEntry(entry);
            for (long i=0; i<2500; i++) zipper.write(0);
        } finally {
            if (zipper != null) try { zipper.close(); } catch(IOException e) {}
        }
    }
    /** / // JUST FOR TESTING
    public static void main(String[] args) throws IOException {
        final String[] files = {"/tmp/exp.zip", "/tmp/comment.zip"};
        //createZipCommentBasedInjection(new File("/tmp/comment.zip"), "Just a test '--");
        //createZipBomb(new File("/tmp/exp.zip"), 50000000);
        /*
        // ZIP COMMENT INJECTIONS (here: sql injection)
        {
            for (int i=0; i< files.length; i++) System.out.println( Arrays.asList(extractNameAndCommentStrings(new File(files[i]))) );
            for (int i=0; i< files.length; i++) {
                InputStream input = null;
                try {
                    input = new BufferedInputStream( new FileInputStream(files[i]) );
                    System.out.println( Arrays.asList(extractNameAndCommentStrings(input)) );
                } finally {
                    if (input != null) try { input.close(); } catch (IOException ignored) {}
                }
            }
        }
        * /
        // ZIP BOMBS
        {
            for (int i=0; i< files.length; i++) System.out.println( isZipBomb(new File(files[i]), 30000000, 100) );
            for (int i=0; i< files.length; i++) {
                InputStream input = null;
                try {
                    input = new BufferedInputStream( new FileInputStream(files[i]) );
                    System.out.println( isZipBomb(input, 30000000, 100) );
                } finally {
                    if (input != null) try { input.close(); } catch (IOException ignored) {}
                }
            }
        }
    }
    /**/
    
    
    
        
    
}






    
