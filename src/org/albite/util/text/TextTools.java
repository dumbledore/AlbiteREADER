/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.text;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;

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
//                && !isAlphaNumeric(buffer[l])) {
            l++;
        }

        while (r >= l
                && !AlbiteCharacter.isLetterOrDigit(buffer[r])) {
//                && !isAlphaNumeric(buffer[r])) {
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

//    public static boolean isAlphaNumeric(final char c) {
//        return Character.isDigit(c) || isLetter(c);
//    }
//
//    /**
//     * very simple check that considers everything a letter, excluding
//     * the regions that are certain NOT to contain letters
//     *
//     * @param c     the character being tested
//     * @return      false if c is NOT a letter, true if c MAY be a letter
//     */
//    public static boolean isLetter(final char c) {
//
//        if (c < 0x41) {
//            return false;
//        }
//
//        /*
//         * General punctuation
//         */
//        if (c >= 0x2000 && c <= 0x206F) {
//            return false;
//        }
//
//        if (c > 0xC0) {
//            return true;
//        }
//
//        if (c > 0x5A && c < 0x61) {
//            return false;
//        }
//
//        if (c > 0x7A) {
//            return false;
//        }
//
//        return true;
//    }

    /**
     * Reads from the
     * stream <code>in</code> a representation
     * of a Unicode  character string encoded in
     * Java modified UTF-8 format; this string
     * of characters  is then returned as a <code>String</code>.
     * The details of the modified UTF-8 representation
     * are  exactly the same as for the <code>readUTF</code>
     * method of <code>DataInput</code>.
     *
     * @param      in   a data input stream.
     * @return     a Unicode char array
     * @exception  EOFException            if the input stream reaches the end
     *               before all the bytes.
     * @exception  IOException             if an I/O error occurs.
     * @exception  UTFDataFormatException  if the bytes do not represent a
     *               valid UTF-8 encoding of a Unicode string.
     * @see        java.io.DataInputStream#readUnsignedShort()
     */
    public final static char[] readUTF(DataInput in) throws IOException {
        int utflen = in.readUnsignedShort();
        StringBuffer str = new StringBuffer(utflen);
        byte[] bytearr = new byte[utflen];
        int c, char2, char3;
        int count = 0;

 	in.readFully(bytearr, 0, utflen);

	while (count < utflen) {

     	    c = (int) bytearr[count] & 0xff;

	    switch (c >> 4) {

	        case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
		    /* 0xxxxxxx*/
		    count++;
                    str.append((char)c);
		    break;

	        case 12: case 13:
		    /* 110x xxxx   10xx xxxx*/
		    count += 2;
		    if (count > utflen)
			throw new UTFDataFormatException();
		    char2 = (int) bytearr[count-1];
		    if ((char2 & 0xC0) != 0x80)
			throw new UTFDataFormatException();
                    str.append((char)(((c & 0x1F) << 6) | (char2 & 0x3F)));
		    break;

	        case 14:
		    /* 1110 xxxx  10xx xxxx  10xx xxxx */
		    count += 3;
		    if (count > utflen)
			throw new UTFDataFormatException();
		    char2 = (int) bytearr[count-2];
		    char3 = (int) bytearr[count-1];
		    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
			throw new UTFDataFormatException();
                    str.append((char)(((c     & 0x0F) << 12) |
                    	              ((char2 & 0x3F) << 6)  |
                    	              ((char3 & 0x3F) << 0)));
		    break;

	        default:
		    /* 10xx xxxx,  1111 xxxx */
		    throw new UTFDataFormatException();
		}
	}

        // The number of chars produced may be less than utflen
        char[] res = new char[str.length()];
        str.getChars(0, str.length(), res, 0);
        return res;
    }

    public static int compareCharArrays(
            char[] c1, int c1Offset, int c1Len,
            char[] c2, int c2Offset, int c2Len) {

//        if (c1Offset + c1Len > c1.length || c2Offset + c2Len > c2.length)
//            throw new IllegalArgumentException("Char arrays supplied with bad indices.");

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
//            middle = (left + right) / 2;
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
