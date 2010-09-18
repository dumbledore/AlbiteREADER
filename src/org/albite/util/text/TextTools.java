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
     * Prepares the word for lookup in a dictionary<p />
     * First, strips text from punctuation from both sides.
     * Then, if the word starts with a digit, strip non-digits from the right,
     * as they most probably are showing the unit type of a physical quantity
     *
     * @param buffer    input character buffer
     * @param pos       start of text
     * @param len       length of text
     * @return          a new String, stripped from punctuation from both sides
     */
    public static String prepareForDict(
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

        /*
         * If the stripped word starts with a digit or minus / hyphen,
         * try to remove non-digits from the right
         */
        if (
                /*
                 * 98.6F
                 */
                (l < r && Character.isDigit(buffer[l]))

                /*
                 * -273.15F
                 * −273.15F
                 * –273.15F
                 */
                || (l + 1 < r && (Character.isDigit(buffer[l + 1]))
                    && (buffer[l] == '-' || buffer[l] == '−'
                        || buffer[l] == '–')
                    )

                ) {
            while (r >= l
                    && (buffer[r] != '.')
                    && (buffer[r] != ',')
                    && !Character.isDigit(buffer[r])) {
                r--;
            }

            /*
             * It's easiest, if we create a dedicated buffer, where we could
             * swap characters, if needed, i.e. some chars may need to be
             * changed in order to be parsable by Double.parseDouble()
             */
            char[] b2 = new char[r - l + 1];
            for (int i = 0; i < b2.length; i++) {
                switch (buffer[l + i]) {
                    case ',':
                        b2[i] = '.';
                        break;

                    case '–':
                    case '−':
                        b2[i] = '-';
                        break;

                    default:
                    b2[i] = buffer[l + i];
                }
            }

            return new String(b2);
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
