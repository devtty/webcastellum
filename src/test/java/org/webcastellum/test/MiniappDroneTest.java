package org.webcastellum.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(value = Arquillian.class)
@RunAsClient
public class MiniappDroneTest {
    
    @ArquillianResource
    private URL contextPath;
    
    @Drone
    private WebDriver webdriver;

    @Deployment
    public static WebArchive createDeployment() throws IOException{
        WebArchive war =
                ShrinkWrap.create(WebArchive.class, "miniapp.war")
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
        return war;
    }
    
    @Test
    public void testRoot(){
        webdriver.get(contextPath.toExternalForm());
        assertTrue("TITLE: ".concat(webdriver.getTitle()), webdriver.getTitle().contains("MiniApp"));
    }
}
