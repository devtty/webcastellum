package org.webcastellum;


public interface ProductionModeChecker extends Configurable {
    
    boolean isProductionMode() throws ProductionModeCheckingException;
    
}
