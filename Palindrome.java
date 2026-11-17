import java.util.Scanner;

/**
 * DevOps Lab Experiment 11.a: Palindrome Program
 * Checks whether a given string is a palindrome or not.
 * Supports both standard normalized check (case-insensitive, ignoring spaces/punctuation)
 * and strict character-by-character check.
 */
public class Palindrome {

    /**
     * Checks if a string is a palindrome using two-pointer technique after normalization.
     * Normalization removes non-alphanumeric characters and converts to lowercase.
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     *
     * @param input The input string to check
     * @return true if the string is a palindrome, false otherwise
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

            // Skip non-alphanumeric characters on the left
            if (!Character.isLetterOrDigit(charLeft)) {
                left++;
                continue;
            }

            // Skip non-alphanumeric characters on the right
            if (!Character.isLetterOrDigit(charRight)) {
                right--;
                continue;
            }

            // Compare case-insensitively
            if (Character.toLowerCase(charLeft) != Character.toLowerCase(charRight)) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }

    /**
     * Strict check: Exactly matches characters in reverse order (including case & spaces).
     *
     * @param input The raw input string
     * @return true if raw string strictly equals its reversed form
     */
    public static boolean isStrictPalindrome(String input) {
        if (input == null) {
            return false;
        }
        String reversed = new StringBuilder(input).reverse().toString();
        return input.equals(reversed);
    }

    /**
     * Cleans the string to show the normalized version used in palindrome evaluation.
     */
    public static String cleanString(String input) {
        if (input == null) return "";
        return input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   DevOps Lab 11.a - String Palindrome Checker    ");
        System.out.println("==================================================");

        Scanner sc = new Scanner(System.in);
        String testInput;

        if (args.length > 0) {
            testInput = String.join(" ", args);
            System.out.println("Input provided via command line: \"" + testInput + "\"");
        } else {
            System.out.print("Enter a string to test: ");
            if (sc.hasNextLine()) {
                testInput = sc.nextLine();
            } else {
                testInput = "A man, a plan, a canal: Panama";
                System.out.println(testInput + " (default demo)");
            }
        }

        String cleaned = cleanString(testInput);
        String reversedCleaned = new StringBuilder(cleaned).reverse().toString();
        boolean palindrome = isPalindrome(testInput);
        boolean strictPalindrome = isStrictPalindrome(testInput);

        System.out.println("\n----------------- Analysis Result ----------------");
        System.out.println("Original String   : \"" + testInput + "\"");
        System.out.println("Normalized String : \"" + cleaned + "\"");
        System.out.println("Reversed String   : \"" + reversedCleaned + "\"");
        System.out.println("Length (Original) : " + testInput.length());
        System.out.println("Length (Cleaned)  : " + cleaned.length());
        System.out.println("Standard Palindrome (Phrase/Case-Insensitive): " + (palindrome ? "YES (PALINDROME)" : "NO (NOT A PALINDROME)"));
        System.out.println("Strict Palindrome   (Exact character match)  : " + (strictPalindrome ? "YES" : "NO"));
        System.out.println("==================================================");

        sc.close();
    }
}
