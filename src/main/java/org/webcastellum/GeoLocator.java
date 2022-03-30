package org.webcastellum;

// TODO rename Locator into something better.... derived from Locating ?
public interface GeoLocator extends Configurable {

    String getCountryCode(String ip) throws GeoLocatingException;
    boolean isEnabled();
    boolean isCachingOfNegativeRepliesAllowed();

}
