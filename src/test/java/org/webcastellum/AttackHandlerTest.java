package org.webcastellum;

import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.mockito.Mockito;

public class AttackHandlerTest {
    
    private AttackLogger attackLogger;
    private AttackHandler attackHandler;
    
    Pattern removeSensitiveDataRequestParamNamePattern = Pattern.compile(".");
    Pattern removeSensitiveDataValuePattern = Pattern.compile(".");
    
    @Before
    public void setUp(){
        attackLogger = Mockito.mock(AttackLogger.class);
        attackHandler = new AttackHandler(attackLogger, 2, 1000L, 500L, 300L, 350L,"/tmp", "applicationName", true, true, 5, false, true, removeSensitiveDataRequestParamNamePattern, removeSensitiveDataValuePattern, true);
    }
    
    public AttackHandlerTest() {
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNegativeThreshold(){
        new AttackHandler(attackLogger, -1, 0, 0, 0, 0, "/tmp", "applicationName", true, true, 0, true, true, removeSensitiveDataRequestParamNamePattern, removeSensitiveDataValuePattern, true);
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorWithoutSensitiveDataRequestParamNamePattern(){
        new AttackHandler(attackLogger, 2, 0, 0, 0, 0, "/tmp", "applicationName", true, true, 0, true, true, null, removeSensitiveDataValuePattern, true);
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorWithoutSensitiveDataValuePattern(){
        new AttackHandler(attackLogger, 2, 0, 0, 0, 0, "/tmp", "applicationName", true, true, 0, true, true, removeSensitiveDataRequestParamNamePattern, null, true);
    }

    @Test
    public void testGetBlockAttackingClientsThreshold() {
        assertEquals(2, attackHandler.getBlockAttackingClientsThreshold());
    }

    @Test
    public void testShouldBeBlocked() {
        //threshold is 2 --> 2 attacks pass but not the 3rd
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        assertFalse(attackHandler.shouldBeBlocked("127.0.0.1"));
        attackHandler.handleAttack(request, "127.0.0.1", "Test");
        assertFalse(attackHandler.shouldBeBlocked("127.0.0.1"));
        attackHandler.handleAttack(request, "127.0.0.1", "Test");
        assertTrue(attackHandler.shouldBeBlocked("127.0.0.1"));
        
    }

    @Test
    public void testGetRedirectThreshold() {
        assertEquals(5, attackHandler.getRedirectThreshold());
    }

    @Test
    public void testGetRedirectThresholdResetPeriod() {
        assertEquals(350L, attackHandler.getRedirectThresholdResetPeriod());
    }

    @Test
    public void testLogWarningRequestMessage() {
        attackHandler.logWarningRequestMessage("test");
        Mockito.verify(attackLogger).log(true, "Warning message: " + Version.versionNumber() + " [\n\ttest\n]");
    }

    @Test
    public void testLogRegularRequestMessage() {
        attackHandler.logRegularRequestMessage("test");
        Mockito.verify(attackLogger).log(false, "Regular message (pre/post-attack logging): " + Version.versionNumber() + " [\n\ttest\n]");
    }

    @Test
    public void testHandleRegularRequest() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        attackHandler.handleRegularRequest(request, "192.168.0.1");
        Mockito.verify(attackLogger).log(Mockito.eq(false), Mockito.startsWith("Regular request"));
    }

    @Test
    public void testHandleAttack() {
          HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
          attackHandler.handleAttack(request, "127.0.0.1", "handleAttackTest");
          Mockito.verify(attackLogger).log(Mockito.eq(true), Mockito.startsWith("Reference"));
    }
    
}
