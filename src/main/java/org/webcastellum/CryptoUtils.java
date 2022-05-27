package org.webcastellum;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public final class CryptoUtils {
    
    private CryptoUtils() {}

    
    
    
    
    // TODO: die ciphers und deren modes per konfig in web.xml setzbar machen...
    private static final String CIPHER_DATA = "AES/GCM/NoPadding"; // AES, AES/ECB/PKCS5Padding, Blowfish, DES
    private static final String CIPHER_KEY = "AES"; // AES, Blowfish, DES
    private static final String DIGEST = "SHA-1"; // MD5, SHA-1, SHA-256, SHA-512
    private static final int KEY_SIZE = 128;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom(); // TODO: use SecureRandom.getInstance()
    private static final Random RANDOM = new Random();
    
    
    private static int digestLength = -1;
    
    
    private static final char[] ALPHABET = new char[] {
      'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
      'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
      '0','1','2','3','4','5','6','7','8','9'
    };
    
    
    // populate internal lookup table
    private static String internalToHexString(final byte b) {
        final int value = (b & 0x7F) + (b < 0 ? 128 : 0);
        String ret = (value < 16 ? "0" : "");
        ret += Integer.toHexString(value).toUpperCase();
        return ret;
    }
    private static String[] hexStringLookup = new String[256];
    static {
        for (byte b=Byte.MIN_VALUE; b<=Byte.MAX_VALUE; b++) {
            hexStringLookup[b+128] = internalToHexString(b);
            if (b == Byte.MAX_VALUE) break;
        }
    }

    
    
    
    
    
    
    public static String toHexString(final byte b) {
        return hexStringLookup[b+128];
    }    
    public static byte toByteValue(String hex) {
        final int length = hex.length();
        if (length<1 || length>2) throw new IllegalArgumentException("hex must be at max a two-digit hex value like B1");
        return (byte) Integer.parseInt(hex, 16);
    }    
    public static int toIntValue(String hex) {
        final int length = hex.length();
        if (length<1 || length>8) throw new IllegalArgumentException("hex must be at max a eight-digit hex value like ABCDEF12");
        return Integer.parseInt(hex, 16);
    }
    
    



    // TODO: per web.xml das pattern konfigurierbar machen
    //static final Pattern PATTERN_UNWANTED_RANDOM_CONTENT = Pattern.compile("(?i)etc|passwd|exe|bin|format|select|insert|delete|union|update|drop|[s5]ex|fuck|[s5]hit|cmd|0d0a|x00|dba");
    static final WordDictionary UNWANTED_RANDOM_CONTENT = new WordDictionary("etc passwd exe bin format select insert delete union update drop shit fuck sex cmd 0d0a x00 dba admin");
    public static String generateRandomToken(final boolean secure) {
        // here even the length is random
        return generateRandomToken(secure, (secure?SECURE_RANDOM:RANDOM).nextInt(3)+7); // TODO: per web.xml konfigurierbar machen !! wie lang das range sein soll (von, bis)
    }
    public static String generateRandomToken(final boolean secure, final int length) {
        StringBuilder result = null;
        //Matcher matcher = null;
        for (int x=0; x<100; x++) { // = not more than 100 tries
            result = new StringBuilder(length);
            final int max = ALPHABET.length-1;
            for (int i=0; i<length; i++) {
                result.append( ALPHABET[generateRandomNumber(secure, 0,max)] );
            }
            //if (matcher == null) matcher = PATTERN_UNWANTED_RANDOM_CONTENT.matcher(result);
            //else matcher.reset(result);
            if (!WordMatchingUtils.matchesWord(UNWANTED_RANDOM_CONTENT, result.toString(), WebCastellumFilter.TRIE_MATCHING_THRSHOLD)) break;
        }
        return result.toString();
    }
    

    
    public static byte[] generateRandomBytes(final boolean secure) {
        // here even the length is random
        return generateRandomBytes(secure, (secure?SECURE_RANDOM:RANDOM).nextInt(8)+10);
    }
    public static byte[] generateRandomBytes(final boolean secure, final int length) {
        final byte[] result = new byte[length];
        (secure?SECURE_RANDOM:RANDOM).nextBytes(result);
        return result;
    }
    
    
    public static int generateRandomNumber(final boolean secure) {
        return (secure?SECURE_RANDOM:RANDOM).nextInt();
    }
    
    public static int generateRandomNumber(final boolean secure, final int low, final int high) {
        if (low >= high) throw new IllegalArgumentException("Low value must be lower than high value (low="+low+" and high="+high+")");
        final int difference = high - low;
        return low + (secure?SECURE_RANDOM:RANDOM).nextInt(difference);
    }
    
    
    public static int getHashLength() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(DIGEST).getDigestLength();
    }
    public static byte[] hash(final byte[] saltBefore, final String content, final byte[] saltAfter, final int repeatedHashingCount) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (content == null) return null;
        final MessageDigest digest = MessageDigest.getInstance(DIGEST);
        if (digestLength == -1) digestLength = digest.getDigestLength();
        for (int i=0; i<repeatedHashingCount; i++) {
            if (i > 0) digest.update( digest.digest() );
            digest.update( saltBefore );
            digest.update( content.getBytes(WebCastellumFilter.DEFAULT_CHARACTER_ENCODING) );
            digest.update( saltAfter );
        }
        return digest.digest();
    }

    

// TODO: hier ein padding (am beginn oder am ende?) einfuegen, anhand welchem man erkennen kann, wenn der Decrypt mit nem alten Key stattfindet und daher nur Murks rauskommt... wobei dann eh sehr oft ne BadPaddingException geworfen wird
    /*
    public static Cipher createReusableCipher() {
        try {
            return Cipher.getInstance(CIPHER);
        } catch (NoSuchAlgorithmException e) {
            // TODO: log warning
            return null; // to avoid reusing the cipher
        } catch (NoSuchPaddingException e) {
            // TODO: log warning
            return null; // to avoid reusing the cipher
        }
    }*/
    public static Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        return Cipher.getInstance(CIPHER_DATA);
    }
    public static CryptoKeyAndSalt generateRandomCryptoKeyAndSalt(final boolean extraHashingProtection) throws NoSuchAlgorithmException {
        final KeyGenerator keyGenerator = KeyGenerator.getInstance(CIPHER_KEY);
        keyGenerator.init(KEY_SIZE);
        final CryptoKeyAndSalt key;
        final SecretKey secret = keyGenerator.generateKey();
        if (extraHashingProtection) {
            final byte[] saltBefore = generateRandomBytes(true);
            final byte[] saltAfter = generateRandomBytes(true);
            final int repeatedHashingCount = generateRandomNumber(true, 2, 5);
            key = new CryptoKeyAndSalt(saltBefore, secret, saltAfter, repeatedHashingCount);
        } else {
            key = new CryptoKeyAndSalt(secret);
        }
        return key;
    }
    public static String encryptURLSafe(final String content, final CryptoKeyAndSalt key, Cipher cipher) throws InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchPaddingException, UnsupportedEncodingException {
        // NPE checks omitted for performance, since NPE will be thrown anyway
        //if (content == null) throw new NullPointerException("content must not be null");
        //if (key == null) throw new NullPointerException("key must not be null");
        if (cipher == null) cipher = getCipher();
        cipher.init(Cipher.ENCRYPT_MODE, key.getKey(), SECURE_RANDOM);
//////////        final byte[] bytes = cipher.doFinal( (content+bytesToHex(hash(content))).getBytes() );
        final byte[] payloadAndHash;
        if (key.isExtraHashingProtection()) {
            final byte[] payload = cipher.update(content.getBytes(WebCastellumFilter.DEFAULT_CHARACTER_ENCODING));
            final byte[] hash = cipher.doFinal(hash(key.getSaltBefore(),content,key.getSaltAfter(),key.getRepeatedHashingCount()));
            payloadAndHash = new byte[payload.length+hash.length];
            System.arraycopy(payload, 0, payloadAndHash, 0, payload.length);
            System.arraycopy(hash, 0, payloadAndHash, payload.length, hash.length);
        } else {
            payloadAndHash = cipher.doFinal(content.getBytes(WebCastellumFilter.DEFAULT_CHARACTER_ENCODING));
        }
//        return URLEncoder.encode( Base64Helper.encodeBytes(payloadAndHash, Base64Helper.DONT_BREAK_LINES), WebCastellumFilter.DEFAULT_CHARACTER_ENCODING ).replace('%','*');
        // Im Result die %-Zeichen durch ein "*" erstzen lassen (ein "*" taucht im Base64-Zeichensatz und auch im UTF-8-Must-Esape nicht auf, daher ist das OK)
        // Damit ist sichergestellt, dass nicht aus Versehen eine zufaelliges Verschluesselungs-Ergebnis dieser Methode ein Angriffs-Pattern wie %00 oder %0D%0A enthaelt
//        return BASE_64_HELPER.encodeBytes(payloadAndHash, Base64Helper.DONT_BREAK_LINES|Base64Helper.URL_SAFE);
        return Base64Utils.encode(payloadAndHash);
        /*
        final String base64 = Base64Helper.encodeBytes(payloadAndHash, Base64Helper.DONT_BREAK_LINES);
        // zuvor wird der Base64-Result noch flott URLEncoded, allerdings ohne URLEncoder.encode(), da die einzigen zwei zu enkodierenden Sonderzeichen in Base64 lieber
        // (und schneller) manuell ausgetauscht werden. Hierbei erledigen wir dann gleich das Ersetzen der %-Zeichen durch die *-Zeichen wegen der URLs
        final StringBuilder result = new StringBuilder( base64.length()+10 );
        for (int i=0; i<base64.length(); i++) {
            final char c = base64.charAt(i);
            if (c=='+') result.append("*2B"); // using *2B instead of %2B
            else if (c=='/') result.append("*2F"); // using *2F instead of %2F
            else result.append(c);
        }
        return result.toString();
         */
    }
    public static String decryptURLSafe(String content, final CryptoKeyAndSalt key) throws IllegalBlockSizeException, InvalidKeyException, NoSuchAlgorithmException, BadPaddingException, NoSuchPaddingException, UnsupportedEncodingException {
        if (content == null) throw new NullPointerException("content must not be null");
        if (key == null) throw new NullPointerException("key must not be null");
        //final SecretKeySpec secretKeySpec = new SecretKeySpec(key, CIPHER);
        final Cipher cipher = Cipher.getInstance(CIPHER_DATA);
        cipher.init(Cipher.DECRYPT_MODE, key.getKey(), SECURE_RANDOM);
  //   System.out.println("aaa content="+content);        
        if (content.indexOf('%') >= 0) content = ServerUtils.urlDecode(content);
//        final byte[] decryptedBytes = cipher.doFinal(BASE_64_HELPER.decode(content,Base64Helper.DONT_BREAK_LINES|Base64Helper.URL_SAFE));
        final byte[] decryptedBytes = cipher.doFinal(Base64Utils.decode(content));
        final int stop;
        final byte[] hash;
        if (key.isExtraHashingProtection()) {
            final int signatureHexLength = digestLength > 0 ? digestLength : 16; // 16 bytes is our hash long
            hash = new byte[signatureHexLength];
            stop = decryptedBytes.length-signatureHexLength;
            System.arraycopy(decryptedBytes, stop, hash, 0, signatureHexLength);
        } else {
            stop = decryptedBytes.length;
            hash = null;
        }
        final String url = new String(decryptedBytes, 0, stop, WebCastellumFilter.DEFAULT_CHARACTER_ENCODING);
        if (key.isExtraHashingProtection()) {
            final byte[] expectedHash = hash(key.getSaltBefore(),url,key.getSaltAfter(),key.getRepeatedHashingCount());
            if (!Arrays.equals(hash,expectedHash)) throw new IllegalArgumentException("Hash of decrypted value does not match");
        }
        return url;
    }
    
    
    
    

    
    
    public static String bytesToHex(final byte[] bytes) {
        if (bytes == null) return null;
        if (bytes.length == 0) return "";
        final StringBuilder result = new StringBuilder(bytes.length);
        for (int i=0; i<bytes.length; i++) {
            result.append( toHexString(bytes[i]) );
        }
        return result.toString();
    }
    
    public static byte[] hexToBytes(final String hex) {
        byte[] bts = new byte[hex.length() / 2];
        for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        }
        return bts;
    }
    
    
    
    public static byte[] compress(final byte[] input) {
        // Compressor with highest level of compression
        final Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);
        // Give the compressor the data to compress
        compressor.setInput(input);
        compressor.finish();
        // Create an expandable byte array to hold the compressed data.
        // It is not necessary that the compressed data will be smaller than
        // the uncompressed data.
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream(input.length);
            // Compress the data
            byte[] buf = new byte[1024];
            while (!compressor.finished()) {
                int count = compressor.deflate(buf);
                bos.write(buf, 0, count);
            }
        } finally {
            if (bos != null) try { bos.close(); } catch (IOException ignored) {}
        }
        // Get the compressed data
        return bos.toByteArray();
    }
    public static byte[] decompress(final byte[] input) throws DataFormatException {
        final Inflater decompressor = new Inflater();
        decompressor.setInput(input);
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream(input.length);
            byte[] buf = new byte[1024];
            while (!decompressor.finished()) {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            }
        } finally {
            if (bos != null) try { bos.close(); } catch (IOException ignored) {}
        }
        return bos.toByteArray();
    }
        
    
    
}
