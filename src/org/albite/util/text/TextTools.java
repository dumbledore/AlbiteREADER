/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.text;

/**
 * Basic text processing tools
 *
 * @author albus
 */
public final class TextTools {
    private TextTools() {}

    /**
     *
     * Strips text from punctuation from both sides
     *
     * @param buffer    input character buffer
     * @param pos       start of text
     * @param len       length of text
     * @return          a new String, stripped from punctuation from both sides
     */
    public static String stripChars(
            final char[] buffer, final int pos, final int len) {

        int l = pos;
        int r = pos + len - 1;

        while (
                l <= r
                && !isAlphaNumeric(buffer[l])) {
            l++;
        }

        while (r >= l
                && !isAlphaNumeric(buffer[r])) {
            r--;
        }

        return new String(buffer, l, r - l + 1);
    }


    public static boolean isAlphaNumeric(final char c) {
        return Character.isDigit(c) || isLetter(c);
    }

    /**
     * very simple check that considers everything a letter, excluding
     * the regions that are certain NOT to contain letters
     *
     * @param c     the character being tested
     * @return      false if c is NOT a letter, true if c MAY be a letter
     */
    public static boolean isLetter(final char c) {

        if (c < 0x41) {
            return false;
        }

        /*
         * General punctuation
         */
        if (c >= 0x2000 && c <= 0x206F) {
            return false;
        }

        if (c > 0xC0) {
            return true;
        }

        if (c > 0x5A && c < 0x61) {
            return false;
        }

        if (c > 0x7A) {
            return false;
        }

        return true;
    }
}
