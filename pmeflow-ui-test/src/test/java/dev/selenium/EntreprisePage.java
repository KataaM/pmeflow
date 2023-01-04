package dev.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object encapsulates the Home Page
 * @author clovis
 */
public class EntreprisePage {
  private WebDriver driver;
  
  // Check the 
  private By addContactButton = By.xpath("//*[@id=\"app\"]/div[1]/main/div/div/div[1]/div[2]/button[3]/span");  
  private By submitButton = By.xpath("//*[@id=\"app\"]/div[3]/div/div/div[2]/div[2]/button[2]/span");
  private By cancelButton = By.xpath("//*[@id=\"app\"]/div[3]/div/div/div[2]/div[2]/button[1]/span");
  private By companyName = By.xpath("//*[@id=\"input-79\"]");
  private By searchBar = By.xpath("//*[@id=\"input-50\"]");
  private By firstCompanyName = By.xpath("//*[@id=\"app\"]/div[1]/main/div/div/div[3]/div/div/div[1]/table/tbody/tr/td[1]");
  private By informationsDetailsName = By.xpath("//*[@id=\"app\"]/div/main/div/div/div[2]/div[1]/div/div[2]/div[2]/div");
  
  public EntreprisePage(WebDriver driver){
    this.driver = driver;
    if (!driver.getTitle().equals("Pmeflow")) {
      throw new IllegalStateException("Attention, ceci n'est pas la page d'accueil," +
            " La page actuellement chargée est : " + driver.getCurrentUrl());
    }
  }

  /**
    * Récupère le titre de (h1 tag)
    *
    * @return String message text
    */
  public String getPageTitle() {
	  return driver.getTitle();
  }
  
  
  public String getEntrepriseName() {
	  return driver.findElement(firstCompanyName).getText();
  }
  
  public String addEntrepriseFunction() {
	  driver.findElement(addContactButton).click();
	  driver.findElement(companyName).sendKeys("La compagnie du test");
	  driver.findElement(submitButton).click();
	  driver.findElement(searchBar).sendKeys("La compagnie du test");
	  return getEntrepriseName();
  }
  
  public String getEntrepriseDetails(String companyName) {
	  driver.findElement(searchBar).sendKeys(companyName);
	  driver.findElement(firstCompanyName).click();
	  return driver.findElement(informationsDetailsName).getText();
  }

  public EntreprisePage manageProfile() {
    // Page encapsulation to manage profile functionality
    return new EntreprisePage(driver);
  }
  /* More methods offering the services represented by Home Page
  of Logged User. These methods in turn might return more Page Objects
  for example click on Compose mail button could return ComposeMail class object */
}

