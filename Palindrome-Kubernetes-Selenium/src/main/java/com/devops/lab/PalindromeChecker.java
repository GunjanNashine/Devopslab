package com.devops.lab;

import java.util.Scanner;

/**
 * DevOps Lab Experiment 11.a: Palindrome Program
 * Core Palindrome Engine and CLI entry point.
 */
public class PalindromeChecker {

    /**
     * Normalized palindrome check:
     * Ignores whitespace, punctuation, and character case.
     * Uses two-pointer technique: O(N) time, O(1) auxiliary space.
     *
     * @param input Input string
     * @return true if palindrome, false otherwise
     */
    public static boolean isPalindrome(String input) {
        if (input == null) {
            return false;
        }

        int left = 0;
        int right = input.length() - 1;

        while (left < right) {
            char charLeft = input.charAt(left);
            char charRight = input.charAt(right);

            if (!Character.isLetterOrDigit(charLeft)) {
                left++;
                continue;
            }

            if (!Character.isLetterOrDigit(charRight)) {
                right--;
                continue;
            }

            if (Character.toLowerCase(charLeft) != Character.toLowerCase(charRight)) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }

    /**
     * Strict palindrome check:
     * Requires exact character-by-character equivalence backwards and forwards.
     */
    public static boolean isStrictPalindrome(String input) {
        if (input == null) {
            return false;
        }
        String reversed = new StringBuilder(input).reverse().toString();
        return input.equals(reversed);
    }

    /**
     * Returns the cleaned/normalized alphanumeric lowercase version of string.
     */
    public static String clean(String input) {
        if (input == null) return "";
        return input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }

    /**
     * Returns the reverse of the string.
     */
    public static String reverse(String input) {
        if (input == null) return "";
        return new StringBuilder(input).reverse().toString();
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("   DevOps Lab 11.a: Palindrome Program & Git SCM Check    ");
        System.out.println("==========================================================");

        String text;
        if (args.length > 0) {
            text = String.join(" ", args);
            System.out.println("Input from argument: \"" + text + "\"");
        } else {
            System.out.println("Enter string to verify: ");
            Scanner sc = new Scanner(System.in);
            if (sc.hasNextLine()) {
                text = sc.nextLine();
            } else {
                text = "racecar";
            }
            sc.close();
        }

        String cleaned = clean(text);
        String reversedCleaned = reverse(cleaned);
        boolean result = isPalindrome(text);
        boolean strict = isStrictPalindrome(text);

        System.out.println("----------------------------------------------------------");
        System.out.println("Input String      : " + text);
        System.out.println("Normalized String : " + cleaned);
        System.out.println("Reversed Form     : " + reversedCleaned);
        System.out.println("Palindrome Status : " + (result ? "SUCCESS: PALINDROME" : "FAILURE: NOT A PALINDROME"));
        System.out.println("Strict Palindrome : " + (strict ? "YES" : "NO"));
        System.out.println("==========================================================");
    }
}
