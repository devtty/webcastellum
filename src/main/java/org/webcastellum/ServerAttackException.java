package org.webcastellum;


public final class ServerAttackException extends java.lang.RuntimeException {
    
    public ServerAttackException() {
    }
    public ServerAttackException(String msg) {
        super(adjustMessage(msg));
    }
    public ServerAttackException(Throwable cause) {
        super(cause);
    }
    public ServerAttackException(String msg, Throwable cause) {
        super(adjustMessage(msg), cause);
    }
    
    
    private static String adjustMessage(final String msg) {
        if (msg == null) return null;
        return "POTENTIAL ATTACK "+msg;
    }
    
}
