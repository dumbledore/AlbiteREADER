/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.lang;

import java.io.DataInputStream;
import java.io.IOException;
import org.albite.io.AlbiteStreamReader;

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
                && !AlbiteCharacter.isLetterOrDigit(buffer[l])) {
            l++;
        }

        while (r >= l
                && !AlbiteCharacter.isLetterOrDigit(buffer[r])) {
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

    /**
     * Implementation of readUTF using the AlbiteCharacterDecoder API
     * and returning char[] instead of String
     */
    public static char[] readUTF(final DataInputStream in) throws IOException {
        return readUTF(in, in.readUnsignedShort());
    }

    public static char[] readUTF(final DataInputStream in, final int utflen)
            throws IOException {

        AlbiteStreamReader r =
                new AlbiteStreamReader(in, AlbiteStreamReader.ENCODING_UTF_8);

        return r.read(utflen);
    }

    public static int compareCharArrays(
            final char[] c1, final int c1Offset, final int c1Len,
            final char[] c2, final int c2Offset, final int c2Len) {

        /* we need the smallest range */
        int search_range = Math.min(c1Len, c2Len);

        for (int i = 0; i < search_range; i++) {
            char c1x = c1[i+c1Offset];
            char c2x = c2[i+c2Offset];

            if (c1x == c2x) {
                /* the two words still match */
                continue;
            }

            if (c1x < c2x) {
                 /* c1 is before */
                return -1;
            }

            /* c1 is after */
            return 1;
        }

        /*
         * Scanned all common chars
         */

        if (c1Len == c2Len) {
            /*  the same */
            return 0;
        }

        if (c1Len < c2Len) {
            /* c1 is before */
            return -1;
        }

        /* c1 is after */
        return 1;
    }

    public static int binarySearch(final char[][] haystack, final char[] key) {

        int left = 0;
        int right = haystack.length;
        int middle;

        int compare = 0;

        while (right > left) {
            middle = left + ((right - left) / 2);

            compare =
                    compareCharArrays(
                    key, 0, key.length,
                    haystack[middle], 0, haystack[middle].length);

            if (compare == 0) {
                return middle;
            }

            if (compare < 0) {
                right = middle;
            } else {
                left = middle + 1;
            }
        }

        /*
         * Decrease the index by one. Thus, one can make the difference
         * whether the exact word has been found when the returned index
         * should be zero.
         */
        return -left -1;
    }
}