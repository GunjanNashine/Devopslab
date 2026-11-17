package com.devops.lab.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("DevOps Lab 11.b: Palindrome Selenium Automated Test Suite")
public class PalindromeSeleniumTest extends BaseSeleniumTest {

    @Test
    @Order(1)
    @DisplayName("TC-01: Verify Application Page Title & DOM Element Presence")
    public void test01_PageLoadAndEssentialElements() {
        driver.get(baseUrl);

        // Verify Page Title
        String title = driver.getTitle();
        assertTrue(title.contains("Palindrome"), "Page title should contain 'Palindrome'. Found: " + title);

        // Verify Page Header
        WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pageTitle")));
        assertTrue(header.getText().contains("Palindrome Verification System"));

        // Verify Essential Form Elements
        assertTrue(driver.findElement(By.id("inputString")).isDisplayed(), "Input string field must be visible");
        assertTrue(driver.findElement(By.id("btnCheck")).isDisplayed(), "Check Palindrome button must be visible");
        assertTrue(driver.findElement(By.id("btnClear")).isDisplayed(), "Clear button must be visible");
        assertTrue(driver.findElement(By.id("systemStatus")).isDisplayed(), "System status badge must be visible");
    }

    @Test
    @Order(2)
    @DisplayName("TC-02: Verify Simple Word Palindrome ('radar')")
    public void test02_SimpleWordPalindrome() {
        driver.get(baseUrl);

        WebElement input = driver.findElement(By.id("inputString"));
        input.clear();
        input.sendKeys("radar");

        driver.findElement(By.id("btnCheck")).click();

        WebElement resultContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultContainer")));
        assertTrue(resultContainer.isDisplayed());

        WebElement resultBadge = driver.findElement(By.id("resultBadge"));
        assertEquals("PALINDROME", resultBadge.getText().trim());
        assertTrue(resultBadge.getAttribute("class").contains("badge-success"));

        WebElement normalizedText = driver.findElement(By.id("normalizedTextDisplay"));
        assertEquals("radar", normalizedText.getText().trim());

        WebElement reversedText = driver.findElement(By.id("reversedTextDisplay"));
        assertEquals("radar", reversedText.getText().trim());
    }

    @Test
    @Order(3)
    @DisplayName("TC-03: Verify Mixed-Case Palindrome ('Madam')")
    public void test03_MixedCasePalindrome() {
        driver.get(baseUrl);

        WebElement input = driver.findElement(By.id("inputString"));
        input.clear();
        input.sendKeys("Madam");

        driver.findElement(By.id("btnCheck")).click();

        WebElement resultBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultBadge")));
        assertEquals("PALINDROME", resultBadge.getText().trim());
        assertTrue(resultBadge.getAttribute("class").contains("badge-success"));

        WebElement normalizedText = driver.findElement(By.id("normalizedTextDisplay"));
        assertEquals("madam", normalizedText.getText().trim());
    }

    @Test
    @Order(4)
    @DisplayName("TC-04: Verify Phrase with Punctuation & Spaces ('A man, a plan, a canal: Panama')")
    public void test04_PhraseWithPunctuation() {
        driver.get(baseUrl);

        WebElement input = driver.findElement(By.id("inputString"));
        input.clear();
        input.sendKeys("A man, a plan, a canal: Panama");

        driver.findElement(By.id("btnCheck")).click();

        WebElement resultBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultBadge")));
        assertEquals("PALINDROME", resultBadge.getText().trim());
        assertTrue(resultBadge.getAttribute("class").contains("badge-success"));

        WebElement normalizedText = driver.findElement(By.id("normalizedTextDisplay"));
        assertEquals("amanaplanacanalpanama", normalizedText.getText().trim());

        WebElement reversedText = driver.findElement(By.id("reversedTextDisplay"));
        assertEquals("amanaplanacanalpanama", reversedText.getText().trim());
    }

    @Test
    @Order(5)
    @DisplayName("TC-05: Verify Non-Palindrome Detection ('Kubernetes')")
    public void test05_NonPalindromeWord() {
        driver.get(baseUrl);

        WebElement input = driver.findElement(By.id("inputString"));
        input.clear();
        input.sendKeys("Kubernetes");

        driver.findElement(By.id("btnCheck")).click();

        WebElement resultBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultBadge")));
        assertEquals("NOT A PALINDROME", resultBadge.getText().trim());
        assertTrue(resultBadge.getAttribute("class").contains("badge-failure"));

        WebElement resultStatusText = driver.findElement(By.id("resultStatusText"));
        assertTrue(resultStatusText.getText().contains("is NOT a Palindrome"));
    }

    @Test
    @Order(6)
    @DisplayName("TC-06: Verify Numeric Palindrome ('12321')")
    public void test06_NumericPalindrome() {
        driver.get(baseUrl);

        WebElement input = driver.findElement(By.id("inputString"));
        input.clear();
        input.sendKeys("12321");

        driver.findElement(By.id("btnCheck")).click();

        WebElement resultBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultBadge")));
        assertEquals("PALINDROME", resultBadge.getText().trim());
        assertTrue(resultBadge.getAttribute("class").contains("badge-success"));
    }

    @Test
    @Order(7)
    @DisplayName("TC-07: Verify Empty Input Validation Alert")
    public void test07_EmptyInputValidation() {
        driver.get(baseUrl);

        WebElement input = driver.findElement(By.id("inputString"));
        input.clear();

        driver.findElement(By.id("btnCheck")).click();

        WebElement alertBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("validationAlert")));
        assertTrue(alertBox.isDisplayed());
        assertTrue(alertBox.getText().contains("Please enter a non-empty string"));
    }

    @Test
    @Order(8)
    @DisplayName("TC-08: Verify Clear Button Functionality")
    public void test08_ClearButtonFunctionality() {
        driver.get(baseUrl);

        WebElement input = driver.findElement(By.id("inputString"));
        input.sendKeys("test string to be cleared");
        driver.findElement(By.id("btnCheck")).click();

        // Verify result is displayed
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultContainer")));

        // Click Clear button
        driver.findElement(By.id("btnClear")).click();

        // Verify input is empty and result is hidden
        assertEquals("", input.getAttribute("value"));
        assertFalse(driver.findElement(By.id("resultContainer")).isDisplayed());
    }

    @Test
    @Order(9)
    @DisplayName("TC-09: Verify History Table Real-Time Updates")
    public void test09_HistoryTableLogging() {
        driver.get(baseUrl);

        WebElement input = driver.findElement(By.id("inputString"));

        // Check first string
        input.clear();
        input.sendKeys("level");
        driver.findElement(By.id("btnCheck")).click();

        // Check second string
        input.clear();
        input.sendKeys("devops");
        driver.findElement(By.id("btnCheck")).click();

        // Verify history rows
        List<WebElement> rows = driver.findElements(By.cssSelector("#historyTableBody tr"));
        assertTrue(rows.size() >= 2, "History table should contain at least 2 entries");

        WebElement historyCount = driver.findElement(By.id("historyCount"));
        assertTrue(historyCount.getText().contains("checks performed"));
    }

    @Test
    @Order(10)
    @DisplayName("TC-10: Verify Quick Test Preset Buttons")
    public void test10_QuickSamplePresetClicks() {
        driver.get(baseUrl);

        WebElement chipPanama = driver.findElement(By.xpath("//button[@data-sample='Madam']"));
        chipPanama.click();

        WebElement resultBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultBadge")));
        assertEquals("PALINDROME", resultBadge.getText().trim());

        WebElement input = driver.findElement(By.id("inputString"));
        assertEquals("Madam", input.getAttribute("value"));
    }
}
