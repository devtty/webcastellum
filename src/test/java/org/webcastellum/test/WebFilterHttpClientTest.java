package org.webcastellum.test;

import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.io.File;
import java.io.IOException;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(value = Arquillian.class)
@RunAsClient
public class WebFilterHttpClientTest {


    public static final int HTTP_I_AM_A_TEAPOT = 418;
    
    @ArquillianResource
    private URL contextPath;

    @Deployment
    public static WebArchive createDeployment(){
	WebArchive war =
	    ShrinkWrap.create(WebArchive.class, "test.war")
	    .addAsLibrary(new File(System.getProperty("user.home") + "/.m2/repository/javax/jms/jms/1.1/jms-1.1.jar"))
	    .addAsLibrary(new File(System.getProperty("user.home") + "/.m2/repository/javax/mail/mail/1.5.0-b01/mail-1.5.0-b01.jar"))
	    .addAsLibrary(new File("target/webcastellum-1.8.5-SNAPSHOT.jar"))
	    .addAsWebResource(new File("src/test/resources/index.html"), "index.html")
	    .addAsWebResource(new File("src/test/resources/test.jsp"), "test.jsp")
	    .setWebXML(new File("src/test/resources/test-web.xml"));

	return war;
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
    public void testSQLInjectionViaGetParam(){
	String requestUri = contextPath.toExternalForm() + "test.jsp?test=1'%20or%20'1'%20=%20'1&amp;password=1'%20or%20'1'%20=%20'1";
	assertBlockedWithoutRedirect(requestUri);
    }

    @Test
    public void testSQLInjectionViaGetParam2(){
	String requestUri = contextPath.toExternalForm() + "test.jsp?test=1'%20or%20'1'%20=%20'1'))/*&amp;password=foo";
	assertBlockedWithoutRedirect(requestUri);
    }
    
    @Test
    public void testDeleteRequest(){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test/1"))
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            //DELETE isn't declared as available method in 01B_Disllowed-Method and must fail
            assertEquals(418, response.statusCode());
        } catch (IOException | InterruptedException ex) {
            fail(ex.getMessage());
        }
    }

    private void assertBlockedWithoutRedirect(String requestUri){
	HttpClient client = HttpClient.newHttpClient();
	HttpRequest request = HttpRequest.newBuilder()
	    .uri(URI.create(requestUri))
	    .build();

	try{
	    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
	    	   
    	    assertEquals(response.statusCode(), HTTP_I_AM_A_TEAPOT);
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
