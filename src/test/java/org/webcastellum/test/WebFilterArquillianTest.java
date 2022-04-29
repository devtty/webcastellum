package org.webcastellum.test;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openqa.selenium.WebDriver;

import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.webcastellum.WebCastellumFilter;

import static org.junit.Assert.*;

@RunWith(value = Arquillian.class)
@RunAsClient
public class WebFilterArquillianTest {


    @ArquillianResource
    private URL contextPath;
    @Drone
    private WebDriver webdriver;

    @Deployment
    public static WebArchive createDeployment(){
	WebArchive war =
	    ShrinkWrap.create(WebArchive.class, "test.war")
	    .addAsLibrary(new File("/home/user/.m2/repository/javax/jms/jms/1.1/jms-1.1.jar"))
	    .addAsLibrary(new File("/home/user/.m2/repository/javax/mail/mail/1.5.0-b01/mail-1.5.0-b01.jar"))
	    .addAsLibrary(new File("target/webcastellum-1.8.4.jar"))
	    .addAsWebResource(new File("src/test/resources/index.html"), "index.html")
	    .addAsWebResource(new File("src/test/resources/test.jsp"), "test.jsp")
	    .setWebXML(new File("src/test/resources/test-web.xml"));

	return war;
    }

    @Test
    public void testRoot(){
	webdriver.get(contextPath.toExternalForm());
	
	assertTrue("TITLE: " + webdriver.getTitle(), webdriver.getTitle().contains("Web Castellum Test JSP"));
	assertTrue(webdriver.getCurrentUrl().equals("http://localhost:8080/test/"));	
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
	
	assertTrue("AS: " + webdriver.getTitle(), webdriver.getTitle().contains("Web Castellum Test JSP"));
	//	assertTrue("msg: " + webdriver.getCurrentUrl(), webdriver.getCurrentUrl().equals("http://localhost:8080/test/.."));	
    }

    //@Ignore
    @Test
    public void testRoot5(){
	HttpClient client = HttpClient.newHttpClient();
	HttpRequest request = HttpRequest.newBuilder()
	    .uri(URI.create("http://localhost:8080/test/%2e%2e%2f,"))
	    .build();

	try{
	    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
	    
	    assertEquals(response.statusCode(), HttpURLConnection.HTTP_BAD_REQUEST);
	    assertEquals(response.uri().toString(), "http://localhost:8080/test/%2e%2e%2f,");
	}catch(IOException | InterruptedException e){
	    fail();
	}
    }

    @Test
    public void testRoot6(){
	HttpClient client = HttpClient.newHttpClient();
	HttpRequest request = HttpRequest.newBuilder()
	    .uri(URI.create("http://localhost:8080/test/test.jsp"))
	    .build();
	client.sendAsync(request, BodyHandlers.ofString())
	    .thenApply(HttpResponse::body)
	    .thenAccept(System.out::println)
	    .join();
    }

    @Test
    public void testSQLInjectionViaGetParam7(){
	webdriver.get(contextPath.toExternalForm() + "?test=1'%20or%20'1'%20=%20'1&amp;password=1'%20or%20'1'%20=%20'1");

	//same as getCurrentUrl
	System.out.println("C" + contextPath.toExternalForm() + "E");
	
	assertTrue("AS: " + webdriver.getTitle(), webdriver.getTitle().contains("HTTP Status 503"));
	//	assertTrue("msg: " + webdriver.getCurrentUrl(), webdriver.getCurrentUrl().equals("http://localhost:8080/test/.."));	
    }

    @Test
    public void testSQLInjectionViaGetParam(){
	String requestUri = "http://localhost:8080/test/test.jsp?test=1'%20or%20'1'%20=%20'1&amp;password=1'%20or%20'1'%20=%20'1";
	assertBlockedWithoutRedirect(requestUri);
    }

    @Test
    public void testSQLInjectionViaGetParam2(){
	String requestUri = "http://localhost:8080/test/test.jsp?test=1'%20or%20'1'%20=%20'1'))/*&amp;password=foo";
	assertBlockedWithoutRedirect(requestUri);
    }

    private void assertBlockedWithoutRedirect(String requestUri){
	HttpClient client = HttpClient.newHttpClient();
	HttpRequest request = HttpRequest.newBuilder()
	    .uri(URI.create(requestUri))
	    .build();

	try{
	    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
	    
	    assertEquals(response.statusCode(), HttpURLConnection.HTTP_UNAVAILABLE);
	    assertEquals(response.uri().toString(), requestUri);
	}catch(IOException | InterruptedException e){
	    fail();
	}

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
