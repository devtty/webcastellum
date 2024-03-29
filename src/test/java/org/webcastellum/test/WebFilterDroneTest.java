package org.webcastellum.test;

import java.net.URL;
import java.io.File;
import java.util.concurrent.TimeUnit;


import org.openqa.selenium.WebDriver;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import org.jboss.arquillian.graphene.page.InitialPage;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(value = Arquillian.class)
@RunAsClient
public class WebFilterDroneTest {
    
    @ArquillianResource
    private URL contextPath;

    @Drone
    private WebDriver webdriver;

    @Deployment
    public static WebArchive createDeployment(){
	WebArchive war =
	    ShrinkWrap.create(WebArchive.class, "test.war")
	    .addAsLibrary(new File(System.getProperty("user.home") + "/.m2/repository/javax/jms/jms/1.1/jms-1.1.jar"))
	    .addAsLibrary(new File(System.getProperty("user.home") + "/.m2/repository/javax/mail/mail/1.5.0-b01/mail-1.5.0-b01.jar"))
	    .addAsLibrary(new File("target/webcastellum-1.8.5-SNAPSHOT.jar"))
	    .addAsWebResource(new File("src/test/resources/index.html"), "index.html")
	    .addAsWebResource(new File("src/test/resources/test.jsp"), "test.jsp")
	    .setWebXML(new File("src/test/resources/test-web-drone.xml"));

	return war;
    }

    @Test
    public void testRoot(){
	webdriver.get(contextPath.toExternalForm());
	
	assertTrue("TITLE: " + webdriver.getTitle(), webdriver.getTitle().contains("Web Castellum Test JSP"));
	assertEquals("http://localhost:8080/test/test.jsp", webdriver.getCurrentUrl());
    }

    @Test
    public void testRoot2(){
	webdriver.get(contextPath.toExternalForm() + "%2e%2e%2f,");
	
	assertTrue("AS: " + webdriver.getTitle(), webdriver.getTitle().contains("HTTP Status 400"));
	//	assertTrue("msg: " + webdriver.getCurrentUrl(), webdriver.getCurrentUrl().equals("http://localhost:8080/test/.."));	
    }

    @Test
    public void testRoot3(){
	webdriver.get(contextPath.toExternalForm() + "test.jsp");
	
	assertTrue("AS: " + webdriver.getTitle(), webdriver.getTitle().contains("Web Castellum Test JSP"));
	//	assertTrue("msg: " + webdriver.getCurrentUrl(), webdriver.getCurrentUrl().equals("http://localhost:8080/test/.."));	
    }

    //@Ignore
    @Test
    public void testRoot4(){
	webdriver.get(contextPath.toExternalForm() + "?test=%T1");

	//same as getCurrentUrl
	System.out.println("C" + contextPath.toExternalForm() + "E");
	//System.out.println("W3: " + webdriver.getTitle());
	
	assertTrue("AS: " + webdriver.getTitle(), webdriver.getTitle().contains("Web Castellum Test JSP"));
	//	assertTrue("msg: " + webdriver.getCurrentUrl(), webdriver.getCurrentUrl().equals("http://localhost:8080/test/.."));	
    }

    @Test
    public void testSQLInjectionViaGetParam7(){
	webdriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
	
	webdriver.get(contextPath.toExternalForm() + "test.jsp");


	//same as getCurrentUrl
	System.out.println("C" + contextPath.toExternalForm() + "E");
	System.out.println("EC" + webdriver.getCurrentUrl() + "E");
	System.out.println("ET" + webdriver.getTitle() + "E");

	//	Graphene.waitGui();
	
	webdriver.get(contextPath.toExternalForm() + "test.jsp?test=1'%20or%20'1'%20=%20'1&amp;password=1'%20or%20'1'%20=%20'1");


	System.out.println("XX: " + webdriver.getTitle());
	
	assertTrue("AS: " + webdriver.getTitle(), webdriver.getTitle().contains("Security Alert"));
	//	assertTrue("msg: " + webdriver.getCurrentUrl(), webdriver.getCurrentUrl().equals("http://localhost:8080/test/.."));	
    }

    @Test
    public void testSimpleFormPost(@InitialPage TestPage testPage){
	
       	assertEquals("Parameter: null", testPage.getFirstNameDiv().getText());  //here
	
	testPage.submit("Horst");

	assertEquals( "Parameter: Horst", testPage.getFirstNameDiv().getText());
    }

    @Test
    public void testSQLInjectionViaPostParam(@InitialPage TestPage testPage){
       	assertEquals(testPage.getFirstNameDiv().getText(), "Parameter: null"); //here
	
	testPage.submit("1'%20or%20'1'%20=%20'1&amp;password=1'%20or%20'1'%20=%20'1");

	assertTrue("AS: " + webdriver.getTitle(), webdriver.getTitle().contains("Security Alert"));	
    }

    public void testSecretTokenLinkInjection(){}
    public void testQueryStringEncryption(){}
    public void testExtraEncryptedMediumPathRemoval(){}
    public void testParameterAndFormProtection(){}
    public void testExtraDisabledFormFieldProtection(){}
    public void testExtraHiddenFormFieldProtection(){}
    public void testExtraSelectboxProtection(){}
    public void testExtraCheckboxProtection(){}
    public void testExtraRadiobuttonProtection(){}
    public void testExtraSelectboxValueMasking(){}
    public void testExtraCheckboxValueMasking(){}
    public void testExtraRadiobuttonValueMasking(){}
    

}
