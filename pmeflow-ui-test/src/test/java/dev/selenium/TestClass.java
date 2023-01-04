package dev.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
/***
 * Tests login feature
 */
public class TestClass {

  @Test
  public void testLogin() {
	  
	System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver-V107.0.5304.62.exe");
    WebDriver driver = new ChromeDriver();
    
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

    driver.get("http://localhost:8080/private/entreprises");
    
    LoginPage signInPage = new LoginPage(driver);
    EntreprisePage entreprisePage = signInPage.loginValidUser("admin.keycloak", "123AZErty.");
    assertEquals(entreprisePage.getPageTitle(), "Pmeflow", "Erreur sur le login, vérifier les idtfs de login et la page");
    driver.quit();
  }
  
  @Test
  public void testAddEntreprise() {
	  
	System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver-V107.0.5304.62.exe");
    WebDriver driver = new ChromeDriver();
    
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

    driver.get("http://localhost:8080/private/entreprises");
    
    LoginPage signInPage = new LoginPage(driver);
    EntreprisePage entreprisePage = signInPage.loginValidUser("admin.keycloak", "123AZErty.");
    assertEquals(entreprisePage.addEntrepriseFunction(), "La compagnie du test", "Erreur sur l'ajout");
    driver.quit();
  }
  
  @Test
  public void testOpenDetailEntreprise() {
	  
	System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver-V107.0.5304.62.exe");
    WebDriver driver = new ChromeDriver();
    
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

    driver.get("http://localhost:8080/private/entreprises");
    
    LoginPage signInPage = new LoginPage(driver);
    EntreprisePage entreprisePage = signInPage.loginValidUser("admin.keycloak", "123AZErty.");
    
    assertEquals(entreprisePage.getEntrepriseDetails("Twitter"), "Twitter", "Erreur sur l'ouverture du détail entreprise");
    driver.quit();
  }

  
  @Test
  public void goBackPreviousPage() {
	  
  }
  
}

