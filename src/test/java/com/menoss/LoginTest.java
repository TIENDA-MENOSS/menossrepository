
package com.menoss;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String URL = "http://localhost:8080";

    
    @BeforeEach
        public void setup() {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);

        wait = new WebDriverWait(
            driver, Duration.ofSeconds(10)
        );
    }


    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoginExitosoYCierreSesion() {

        driver.get(URL + "/registro");

        String correoPrueba = "test-" +
                UUID.randomUUID() + "@menoss.com";

        String passwordPrueba = "Clave12345";

        driver.findElement(By.id("nombre"))
                .sendKeys("Usuario Prueba");

        driver.findElement(By.id("correo"))
                .sendKeys(correoPrueba);

        driver.findElement(By.id("password"))
                .sendKeys(passwordPrueba);

        driver.findElement(By.id("registrar"))
                .click();


        WebElement mensajeRegistro = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("mensaje")
                )
        );

        assertEquals(
                "Registro exitoso",
                mensajeRegistro.getText()
        );


        driver.get(URL + "/login");

        driver.findElement(By.id("correo"))
                .sendKeys(correoPrueba);

        driver.findElement(By.id("password"))
                .sendKeys(passwordPrueba);

        driver.findElement(By.id("iniciarSesion"))
                .click();

        wait.until(
                ExpectedConditions.urlToBe(URL + "/")
        );

        WebElement cerrarSesion = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("cerrarSesion")
                )
        );

        assertTrue(cerrarSesion.isDisplayed());

        System.out.println(
                "LOGIN EXITOSO - Usuario autenticado"
        );

        cerrarSesion.click();


        wait.until(
                ExpectedConditions.urlToBe(URL + "/login")
        );

        WebElement botonLogin = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("iniciarSesion")
                )
        );

        assertTrue(botonLogin.isDisplayed());

        System.out.println(
                "LOGOUT EXITOSO - Sesion finalizada"
        );
    }

    @Test
    public void testLoginIncorrecto() {


        driver.get(URL + "/login");

  
        driver.findElement(By.id("correo"))
                .sendKeys("inexistente@menoss.com");

        driver.findElement(By.id("password"))
                .sendKeys("Incorrecta123");

        driver.findElement(By.id("iniciarSesion"))
                .click();

        WebElement mensaje = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("mensaje")
                )
        );

        assertEquals(
                "Correo o contraseña incorrectos",
                mensaje.getText()
        );

        System.out.println(
                "LOGIN INCORRECTO - Acceso rechazado"
        );
    }
}
