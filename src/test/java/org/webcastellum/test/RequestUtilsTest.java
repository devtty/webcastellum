package org.webcastellum.test;

import junit.framework.TestCase;
import org.webcastellum.RequestUtils;

public class RequestUtilsTest extends TestCase {
    
    public RequestUtilsTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRemoveParameter() {
        assertEquals(null, RequestUtils.removeParameter(null, null));
        executeTests(true, true);
        executeTests(true, false);
        executeTests(false, true);
        executeTests(false, false);
    }


    private void executeTests(final boolean questionmark, final boolean maskAmpersands) {
        final String qm = questionmark ? "?" : "";
        final String amp = maskAmpersands ? "&amp;" : "&";

        assertEquals(qm+"one=two"+amp+"three=four"+amp+"five=six#seven", RequestUtils.removeParameter(qm+"one=two"+amp+"three=four"+amp+"five=six#seven", null));

        assertEquals(qm+"three=four"+amp+"five=six#seven", RequestUtils.removeParameter(qm+"one=two"+amp+"three=four"+amp+"five=six#seven", "one"));
        assertEquals(qm+"one=two"+amp+"five=six#seven", RequestUtils.removeParameter(qm+"one=two"+amp+"three=four"+amp+"five=six#seven", "three"));
        assertEquals(qm+"one=two"+amp+"three=four#seven", RequestUtils.removeParameter(qm+"one=two"+amp+"three=four"+amp+"five=six#seven", "five"));
        assertEquals(qm+"one=two", RequestUtils.removeParameter(qm+"one=two"+amp+"three=four", "three"));
        assertEquals(qm+"", RequestUtils.removeParameter(qm+"one=two", "one"));
        assertEquals(qm+"one=two", RequestUtils.removeParameter(qm+"one=two", "nothing"));

        assertEquals(qm+"three=four"+amp+"five=six#seven", RequestUtils.removeParameter(qm+"one=0"+amp+"three=four"+amp+"five=six#seven", "one"));
        assertEquals(qm+"one=two"+amp+"five=six#seven", RequestUtils.removeParameter(qm+"one=two"+amp+"three=0"+amp+"five=six#seven", "three"));
        assertEquals(qm+"one=two"+amp+"three=four#seven", RequestUtils.removeParameter(qm+"one=two"+amp+"three=four"+amp+"five=0#seven", "five"));
        assertEquals(qm+"one=two", RequestUtils.removeParameter(qm+"one=two"+amp+"three=0", "three"));
        assertEquals(qm+"", RequestUtils.removeParameter(qm+"one=0", "one"));
        assertEquals(qm+"one=two", RequestUtils.removeParameter(qm+"one=two", "nothing"));

        assertEquals(qm+"three=four"+amp+"five=six#seven", RequestUtils.removeParameter(qm+"one="+amp+"three=four"+amp+"five=six#seven", "one"));
        assertEquals(qm+"one=two"+amp+"five=six#seven", RequestUtils.removeParameter(qm+"one=two"+amp+"three="+amp+"five=six#seven", "three"));
        assertEquals(qm+"one=two"+amp+"three=four#seven", RequestUtils.removeParameter(qm+"one=two"+amp+"three=four"+amp+"five=#seven", "five"));
        assertEquals(qm+"one=two", RequestUtils.removeParameter(qm+"one=two"+amp+"three=", "three"));
        assertEquals(qm+"", RequestUtils.removeParameter(qm+"one=", "one"));
        assertEquals(qm+"one=two", RequestUtils.removeParameter(qm+"one=two", "nothing"));

        assertEquals(qm+"three=four"+amp+"five=six#seven", RequestUtils.removeParameter(qm+"one"+amp+"three=four"+amp+"five=six#seven", "one"));
        assertEquals(qm+"one=two"+amp+"five=six#seven", RequestUtils.removeParameter(qm+"one=two"+amp+"three"+amp+"five=six#seven", "three"));
        assertEquals(qm+"one=two"+amp+"three=four#seven", RequestUtils.removeParameter(qm+"one=two"+amp+"three=four"+amp+"five#seven", "five"));
        assertEquals(qm+"one=two", RequestUtils.removeParameter(qm+"one=two"+amp+"three", "three"));
        assertEquals(qm+"", RequestUtils.removeParameter(qm+"one", "one"));
        assertEquals(qm+"one=two", RequestUtils.removeParameter(qm+"one=two", "nothing"));
    }


}
