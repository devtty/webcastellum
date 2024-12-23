package org.webcastellum.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RunWith(value = Arquillian.class)
@RunAsClient
public class MiniappDroneTest {

    @ArquillianResource
    private URL contextPath;

    @Drone
    private WebDriver webdriver;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "miniapp.war")
                .addAsLibrary(new File(System.getProperty("user.home") + "/.m2/repository/javax/jms/jms/1.1/jms-1.1.jar"))
                .addAsLibrary(new File(System.getProperty("user.home") + "/.m2/repository/javax/mail/mail/1.5.0-b01/mail-1.5.0-b01.jar"))
                .addAsLibrary(new File("target/webcastellum-1.8.5-SNAPSHOT.jar"))
                .addAsWebResource(new File("src/test/resources/miniapp/index.jsp"))
                .addAsWebResource(new File("src/test/resources/miniapp/echo.jsp"))
                .addAsWebResource(new File("src/test/resources/miniapp/form.jsp"))
                .addAsWebResource(new File("src/test/resources/miniapp/increment.jsp"))
                .addAsWebResource(new File("src/test/resources/miniapp/test.png"))
                .addAsWebResource(new File("src/test/resources/miniapp/test/index.jsp"), "test/index.jsp")
                .addAsWebInfResource(new File("src/test/resources/miniapp/WEB-INF/jspf/content.jspf"), "jspf/content.jspf")
                .addAsWebInfResource(new File("src/test/resources/miniapp/WEB-INF/jspf/menu.jspf"), "jspf/menu.jspf")
                .addAsWebInfResource(new File("src/test/resources/miniapp/WEB-INF/classes/demo/Redirect.class"), "classes/demo/Redirect.class")
                .setWebXML(new File("src/test/resources/miniapp/WEB-INF/web.xml"));
    }

    @Test
    public void testRoot() {
        webdriver.get(contextPath.toExternalForm());
        assertTrue("TITLE: ".concat(webdriver.getTitle()), webdriver.getTitle().contains("MiniApp"));
        checkMenu();
    }

    @Test
    public void testTest() {
        webdriver.get(contextPath.toExternalForm() + "/test/");
        assertEquals("TEST", webdriver.findElement(By.cssSelector("body > h2:nth-child(1)")).getText());
        assertTrue(Pattern.compile("Session-ID((.|\n)*)([A-F0-9]{32})").matcher(webdriver.getPageSource()).find());
    }

    @Test
    public void testTestWithParams() {
        webdriver.get(contextPath.toExternalForm() + "/test/?testparam=test1&testparam2=test2");
        assertEquals("TEST", webdriver.findElement(By.cssSelector("body > h2:nth-child(1)")).getText());
        assertTrue(Pattern.compile("Session-ID((.|\n)*)([A-F0-9]{32})").matcher(webdriver.getPageSource()).find());
        assertEquals("testparam", webdriver.findElement(By.cssSelector("body > dl:nth-child(4) > dt:nth-child(1)")).getText());
        assertEquals("[test1]", webdriver.findElement(By.cssSelector("body > dl:nth-child(4) > dd:nth-child(2)")).getText());
        assertEquals("testparam2", webdriver.findElement(By.cssSelector("body > dl:nth-child(4) > dt:nth-child(3)")).getText());
        assertEquals("[test2]", webdriver.findElement(By.cssSelector("body > dl:nth-child(4) > dd:nth-child(4)")).getText());
    }

    @Test
    public void testRedirect() {
        webdriver.get(contextPath.toExternalForm() + "/redirect");
        String currentUrl = webdriver.getCurrentUrl();
        assertEquals("http://localhost:8080/miniapp/", currentUrl);
    }

    @Test
    public void testStartLink() {
        webdriver.get(contextPath.toExternalForm() + "/");
        HashSet<String> encUri = new HashSet<>();

        for (int i = 0; i < 20; i++) {
            WebElement startLink = webdriver.findElement(By.linkText("start"));
            String href = startLink.getAttribute("href");
            assertFalse(href.contains("index.jsp"));
            startLink.click();
            assertTrue(encUri.add(webdriver.getCurrentUrl()));
            System.out.println("C" + i + ": " + webdriver.getCurrentUrl());
        }
    }

    private void checkMenu() {
        List<WebElement> startLink = webdriver.findElements(By.linkText("start"));
        assertEquals(1, startLink.size());

        List<WebElement> formLinks = webdriver.findElements(By.linkText("form"));
        assertEquals(11, formLinks.size());

        //10 true links and 10 JS
        List<WebElement> echoLinks = webdriver.findElements(By.linkText("echo"));
        assertEquals(10, echoLinks.size());

        List<WebElement> incrementLinks = webdriver.findElements(By.partialLinkText("increment by"));
        assertEquals(3, incrementLinks.size());

        List<WebElement> miniappLinks = webdriver.findElements(By.partialLinkText("MiniApp"));
        assertEquals(2, miniappLinks.size());

    }

}
