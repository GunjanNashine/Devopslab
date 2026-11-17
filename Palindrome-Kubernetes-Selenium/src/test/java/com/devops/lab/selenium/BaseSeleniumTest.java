package com.devops.lab.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.time.Duration;

/**
 * BaseSeleniumTest handles WebDriver setup, Chrome binary detection,
 * connectivity pre-flight checks, and teardown.
 */
public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = System.getProperty("baseUrl");
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            baseUrl = "http://localhost:3000";
        }
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        // Pre-flight check: ensure application endpoint is responsive
        try {
            URL url = new URI(baseUrl + "/health").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("GET");
            conn.connect();
            int code = conn.getResponseCode();
            conn.disconnect();
            if (code != 200) {
                System.err.println("Warning: /health responded with HTTP " + code);
            }
        } catch (Exception e) {
            System.err.println("[BaseSeleniumTest] Pre-flight warning: Could not ping " + baseUrl + "/health: " + e.getMessage());
        }

        ChromeOptions options = new ChromeOptions();

        // Check for specific Chrome binary location on Windows
        File chromeX86 = new File("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
        File chrome64 = new File("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
        if (chromeX86.exists()) {
            options.setBinary(chromeX86);
        } else if (chrome64.exists()) {
            options.setBinary(chrome64);
        }

        String headlessProp = System.getProperty("headless");
        boolean isHeadless = headlessProp == null || !headlessProp.equalsIgnoreCase("false");

        if (isHeadless) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));

        wait = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {
            }
        }
    }
}
