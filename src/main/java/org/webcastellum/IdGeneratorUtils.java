package org.webcastellum;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.UUID;

public final class IdGeneratorUtils {
    
    
    private static boolean useJava5 = true;
    
    
    /**
     * Secure random to provide nonrepeating seed
     */
    private static SecureRandom seeder;
    
    /**
     * Cached value for mid part of UUID string
     */
    private static String midValue;
    
    
    

    
    
    private IdGeneratorUtils() {}
    
    
    
    
    
    
    public static synchronized String createId() { // TODO: muss das hier synchronzed sein ?
        if (!IdGeneratorUtils.useJava5) return createIdJavaOld();
        try {
            return createIdJavaNew();
        } catch(NoClassDefFoundError e) {
            IdGeneratorUtils.useJava5 = false;
            return createIdJavaOld();
        }
    }
    
    
    
    private static String createIdJavaNew() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "").toUpperCase();
//  OLD        throw new NoClassDefFoundError("Compiled under Java SE 1.4");
    }
    
    private static String createIdJavaOld() {
        return getUUID();
    }
    
    
    /**
     * Returns a UUID
     * @return a new UUID
     */
    private static String getUUID() {
        initUUID();
        long lTimeNow = System.currentTimeMillis();
        // get int value as unsigned
        int iTimeLow = (int) lTimeNow & 0xFFFFFFFF;
        // get next random value
        int iNode = IdGeneratorUtils.seeder.nextInt();
        String sUUID = (hexFormat(iTimeLow, 8) + IdGeneratorUtils.midValue + hexFormat(iNode, 8)).toUpperCase();
        return sUUID;
    }
    
    
    private static void initUUID() {
        if (IdGeneratorUtils.midValue == null) {
            byte[] bytes;
            try {
                InetAddress inet = InetAddress.getLocalHost();
                bytes = inet.getAddress();
            } catch (UnknownHostException e) {
                bytes = new byte[] {127,0,0,1};
            }
            String sHexInetAddress = hexFormat(bytes);
            String sThisHashCode = hexFormat(System.identityHashCode(IdGeneratorUtils.class), 8);
            IdGeneratorUtils.midValue = sHexInetAddress + sThisHashCode;
            IdGeneratorUtils.seeder = new SecureRandom();
            IdGeneratorUtils.seeder.nextInt();
        }
    }
    
    /**
     * Formats an input value as a hex string (inclusive padding)
     * @param iValue value to convert to a hex string
     * @param iPadding number of characters to pad hex string to
     * @return the hex string
     */
    private static String hexFormat(int iValue, int iPadding) {
        String sHex = Integer.toHexString(iValue);
        while (sHex.length() < iPadding) sHex = "0" + sHex;
        return sHex;
    }
    
    
    
    /**
     * Formats an input value as a hex string
     * @param bytes value to convert to a hex string
     * @return the hex string
     */
    private static String hexFormat(byte[] bytes) {
        StringBuilder sbHex = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            // get int value as unsigned
            int iValue = (int) bytes[i] & 0xFF;
            String sHex = Integer.toHexString(iValue);
            if (sHex.length() == 1) {
                sHex = "0" + sHex;
            }
            sbHex.append(sHex);
        }
        String result = sbHex.toString();
        if (result.length() > 8) {
            result = result.substring(0, 8);
        }
        return result;
    }
    
    
    
    
}
