package org.webcastellum;

public final class Version {
        
    public static final byte VERSION_MAJOR = 1;
    public static final byte VERSION_MINOR = 8;
    public static final byte VERSION_PATCH = 5;
    

    private Version() {}

    public static final String versionNumber() {
        return new StringBuilder().append(VERSION_MAJOR).append(".").
        append(VERSION_MINOR).append(".").
        append(VERSION_PATCH).toString();
    }
    
    public static final String tagLine() {
        return new StringBuilder("((( WebCastellum ))) version ").append(versionNumber()).
                append(" - web application firewall").toString();
    }

    public static final String helpLine() {
        return "For documentation, support, and updates visit: http://www.WebCastellum.org";
    }
    
    public static final void main(String[] args) {
        System.out.println( tagLine() );
        System.out.println( helpLine() );
    }
        
    
}
