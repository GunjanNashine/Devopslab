package com.devops.lab;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DevOps Lab 11.a: Palindrome Unit Tests")
public class PalindromeUnitTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "radar",
        "level",
        "rotor",
        "kayak",
        "reviver",
        "deified",
        "noon",
        "racecar"
    })
    @DisplayName("Should detect simple word palindromes")
    void testSimpleWordPalindromes(String word) {
        assertTrue(PalindromeChecker.isPalindrome(word), "Expected " + word + " to be recognized as a palindrome");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Madam",
        "RaceCar",
        "Noon",
        "Red rum, sir, is murder",
        "A man, a plan, a canal: Panama",
        "Was it a car or a cat I saw?",
        "No lemon, no melon",
        "Step on no pets",
        "Live on time, emit no evil"
    })
    @DisplayName("Should detect mixed-case and punctuated phrase palindromes")
    void testPhrasePalindromes(String phrase) {
        assertTrue(PalindromeChecker.isPalindrome(phrase), "Expected phrase: \"" + phrase + "\" to be a palindrome");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "12321",
        "123454321",
        "9889",
        "0",
        "11"
    })
    @DisplayName("Should detect numeric palindromes")
    void testNumericPalindromes(String numberStr) {
        assertTrue(PalindromeChecker.isPalindrome(numberStr));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "devops",
        "kubernetes",
        "selenium",
        "docker",
        "jenkins",
        "hello world",
        "automation testing"
    })
    @DisplayName("Should reject non-palindromes")
    void testNonPalindromes(String nonPalindrome) {
        assertFalse(PalindromeChecker.isPalindrome(nonPalindrome), "Expected " + nonPalindrome + " to NOT be a palindrome");
    }

    @Test
    @DisplayName("Should handle edge cases: single char, empty, null")
    void testEdgeCases() {
        assertTrue(PalindromeChecker.isPalindrome("a"));
        assertTrue(PalindromeChecker.isPalindrome(""));
        assertFalse(PalindromeChecker.isPalindrome(null));
    }

    @Test
    @DisplayName("Should verify strict vs normalized check")
    void testStrictCheck() {
        assertTrue(PalindromeChecker.isStrictPalindrome("racecar"));
        assertFalse(PalindromeChecker.isStrictPalindrome("Racecar"));
        assertFalse(PalindromeChecker.isStrictPalindrome("A man, a plan, a canal: Panama"));
    }
}
