package org.webcastellum;

import java.io.Serializable;
import javax.crypto.SecretKey;

public final class CryptoKeyAndSalt implements Serializable {

    private static final long serialVersionUID = 1L;

    private final SecretKey key;
    private byte[] saltBefore;
    private byte[] saltAfter;
    private int repeatedHashingCount;

    public CryptoKeyAndSalt(final SecretKey key) {
        if (key == null) {
            throw new NullPointerException("key must not be null");
        }
        this.key = key;
    }

    public CryptoKeyAndSalt(final byte[] saltBefore, final SecretKey key, final byte[] saltAfter, final int repeatedHashingCount) {
        this(key);
        if (saltBefore == null) {
            throw new NullPointerException("saltBefore must not be null");
        }
        if (saltAfter == null) {
            throw new NullPointerException("saltAfter must not be null");
        }
        if (repeatedHashingCount < 1) {
            throw new IllegalArgumentException("repeatedHashingCount must be positive");
        }
        this.saltBefore = saltBefore;
        this.saltAfter = saltAfter;
        this.repeatedHashingCount = repeatedHashingCount;
    }

    public boolean isExtraHashingProtection() {
        return this.saltBefore != null && this.saltAfter != null && this.repeatedHashingCount > 0;
    }

    public SecretKey getKey() {
        return key;
    }

    public byte[] getSaltBefore() {
        return saltBefore;
    }

    public byte[] getSaltAfter() {
        return saltAfter;
    }

    public int getRepeatedHashingCount() {
        return this.repeatedHashingCount;
    }

    //1.5@Override
    public String toString() {
        return "CKAS"; // = Crypto key and salt
    }

}
