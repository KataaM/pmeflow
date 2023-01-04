package dev.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class testSelenium {
	
	@Test
    public void randomTest() {
		
		System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver-V107.0.5304.62.exe");
        WebDriver driver = new ChromeDriver();
        
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

        driver.get("http://localhost:8080/");
        
        String title = driver.getTitle();
        assertEquals("Se connecter à Pmeflow SSO", title, "mauvais titre");

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        
        WebElement username = driver.findElement(By.name("username"));
        WebElement password = driver.findElement(By.name("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("#kc-login"));

        username.sendKeys("admin.keycloak");
        password.sendKeys("123AZErty.");
        submitButton.click();

        WebElement message = driver.findElement(By.id("message"));
        String value = message.getText();
        assertEquals("Received!", value);

        driver.quit();
    }
	
}
