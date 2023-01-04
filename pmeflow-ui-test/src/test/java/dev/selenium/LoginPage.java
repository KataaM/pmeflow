package dev.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/*
 * 
 * Class POM de test concernant la page de login
 * 
 */

public class LoginPage {
	
	private WebDriver driver;

	  // <input name="user_name" type="text" value="">
	  private By usernameBy = By.name("username");
	  // <input name="password" type="password" value="">
	  private By passwordBy = By.name("password");
	  // <input name="sign_in" type="submit" value="SignIn">
	  private By signinBy = By.name("login");

	  public LoginPage(WebDriver driver){
		    this.driver = driver;
		     if (!driver.getTitle().equals("Se connecter à Pmeflow SSO")) {
		         throw new IllegalStateException("Attention, ceci n'est pas la page d'accueil," +
		                 " La page actuellement chargée est : " + driver.getCurrentUrl());
		    }
	  }

	  /**
	    * Login as valid user
	    *
	    * @param userName
	    * @param password
	    * @return HomePage object
	    */
	  public EntreprisePage loginValidUser(String userName, String password) {
		  driver.findElement(usernameBy).sendKeys(userName);
		  driver.findElement(passwordBy).sendKeys(password);
		  driver.findElement(signinBy).click();
		  return new EntreprisePage(driver);
	  }
}
