package org.webcastellum.test;

import org.jboss.arquillian.graphene.GrapheneElement;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.jboss.arquillian.graphene.Graphene.guardHttp;

public class TestPage {

    @FindBy(id = "fname")
    private GrapheneElement firstName;

    @FindBy(id = "lname")
    private GrapheneElement lastName;

    @FindBy(id = "check1")
    private GrapheneElement check1;

    @FindBy(id = "submit")
    private GrapheneElement submitButton;

    //    @FindBy(xpath = "[@id='firstnameParameter']")
    @FindBy(id = "firstnameParameter")
    private WebElement firstNameDiv;
	
    
    public GrapheneElement getFirstName(){
	return firstName;
    }

    public GrapheneElement getLastName(){
	return lastName;
    }

    public GrapheneElement getCheck1(){
	return check1;
    }

    public GrapheneElement getSubmitButton(){
	return submitButton;
    }

    public WebElement getFirstNameDiv(){
	return firstNameDiv;
    }
    
    
    public void submit(String firstName){
	this.firstName.sendKeys(firstName);
	guardHttp(submitButton).click();
    }
    
    
    
}
