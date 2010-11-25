/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.lang;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author albus
 */
public class AlbiteCharacter {

    public static final byte
	UNASSIGNED		= 0,
	UPPERCASE_LETTER	= 1,
	LOWERCASE_LETTER	= 2,
	TITLECASE_LETTER	= 3,
	MODIFIER_LETTER		= 4,
	OTHER_LETTER		= 5,
	NON_SPACING_MARK	= 6,
	ENCLOSING_MARK		= 7,
	COMBINING_SPACING_MARK	= 8,
	DECIMAL_DIGIT_NUMBER	= 9,
	LETTER_NUMBER		= 10,
	OTHER_NUMBER		= 11,
	SPACE_SEPARATOR		= 12,
	LINE_SEPARATOR		= 13,
	PARAGRAPH_SEPARATOR	= 14,
	CONTROL			= 15,
	FORMAT			= 16,
	PRIVATE_USE		= 18,
	SURROGATE		= 19,
	DASH_PUNCTUATION	= 20,
	START_PUNCTUATION	= 21,
	END_PUNCTUATION		= 22,
	CONNECTOR_PUNCTUATION	= 23,
	OTHER_PUNCTUATION	= 24,
	MATH_SYMBOL		= 25,
	CURRENCY_SYMBOL		= 26,
	MODIFIER_SYMBOL		= 27,
	OTHER_SYMBOL		= 28;

    public static final byte[]  X = new byte[1024];
    public static final short[] Y = new short[4032];
    public static final int[]   A = new int[632];

    static {
        /*
         * Load data from external file
         */
        InputStream is = (new Object()).getClass()
                .getResourceAsStream("/res/charmap.bin");

        System.out.println("CHARMAP: " + (is == null));

        if (is != null) {
            DataInputStream in = new DataInputStream(is);
            try {
                try {
                    for (int i = 0; i < 1024; i++) {
                        X[i] = in.readByte();
                    }

                    for (int i = 0; i < 4032; i++) {
                        Y[i] = in.readShort();
                    }

                    for (int i = 0; i < 632; i++) {
                        A[i] = in.readInt();
                    }
                } finally {
                    in.close();
                }
            } catch (IOException e) {}
        }
    }

    /**
     * Returns a value indicating a character category.
     *
     * @param   ch      the character to be tested.
     * @return  a value of type int, the character category.
     * @see     java.lang.Character#COMBINING_SPACING_MARK
     * @see     java.lang.Character#CONNECTOR_PUNCTUATION
     * @see     java.lang.Character#CONTROL
     * @see     java.lang.Character#CURRENCY_SYMBOL
     * @see     java.lang.Character#DASH_PUNCTUATION
     * @see     java.lang.Character#DECIMAL_DIGIT_NUMBER
     * @see     java.lang.Character#ENCLOSING_MARK
     * @see     java.lang.Character#END_PUNCTUATION
     * @see     java.lang.Character#FORMAT
     * @see     java.lang.Character#LETTER_NUMBER
     * @see     java.lang.Character#LINE_SEPARATOR
     * @see     java.lang.Character#LOWERCASE_LETTER
     * @see     java.lang.Character#MATH_SYMBOL
     * @see     java.lang.Character#MODIFIER_LETTER
     * @see     java.lang.Character#MODIFIER_SYMBOL
     * @see     java.lang.Character#NON_SPACING_MARK
     * @see     java.lang.Character#OTHER_LETTER
     * @see     java.lang.Character#OTHER_NUMBER
     * @see     java.lang.Character#OTHER_PUNCTUATION
     * @see     java.lang.Character#OTHER_SYMBOL
     * @see     java.lang.Character#PARAGRAPH_SEPARATOR
     * @see     java.lang.Character#PRIVATE_USE
     * @see     java.lang.Character#SPACE_SEPARATOR
     * @see     java.lang.Character#START_PUNCTUATION
     * @see     java.lang.Character#SURROGATE
     * @see     java.lang.Character#TITLECASE_LETTER
     * @see     java.lang.Character#UNASSIGNED
     * @see     java.lang.Character#UPPERCASE_LETTER
     * @since   JDK1.1
     */
    public static int getType(final char ch) {
        return A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x1F;
    }

    /**
     * Determines if the specified character is a letter or digit.
     * For a more complete specification that encompasses all Unicode
     * characters, see Gosling, Joy, and Steele, <i>The Java Language
     * Specification</i>.
     *
     * <p> A character is considered to be a letter if and only if
     * it is specified to be a letter or a digit by the Unicode 2.0 standard
     * (category "Lu", "Ll", "Lt", "Lm", "Lo", or "Nd" in the Unicode
     * specification data file).  In other words, isLetterOrDigit is true
     * of a character if and only if either isLetter is true of the character
     * or isDigit is true of the character.
     *
     * @param   ch   the character to be tested.
     * @return  <code>true</code> if the character is a letter or digit;
     *          <code>false</code> otherwise.
     * @see     java.lang.Character#isDigit(char)
     * @see     java.lang.Character#isJavaIdentifierPart(char)
     * @see     java.lang.Character#isJavaLetter(char)
     * @see     java.lang.Character#isJavaLetterOrDigit(char)
     * @see     java.lang.Character#isLetter(char)
     * @see     java.lang.Character#isUnicodeIdentifierPart(char)
     * @since   JDK1.0.2
     */
    public static boolean isLetterOrDigit(final char ch) {
        return (((((1 << UPPERCASE_LETTER) |
                   (1 << LOWERCASE_LETTER) |
                   (1 << TITLECASE_LETTER) |
                   (1 << MODIFIER_LETTER) |
                   (1 << OTHER_LETTER) |
                   (1 << DECIMAL_DIGIT_NUMBER))
                  >> (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x1F)) & 1) != 0);
    }

    /**
     * Determines if the specified character is a letter. For a
     * more complete specification that encompasses all Unicode
     * characters, see Gosling, Joy, and Steele, <i>The Java Language
     * Specification</i>.
     *
     * <p> A character is considered to be a letter if and only if
     * it is specified to be a letter by the Unicode 2.0 standard
     * (category "Lu", "Ll", "Lt", "Lm", or "Lo" in the Unicode
     * specification data file).
     *
     * <p> Note that most ideographic characters are considered
     * to be letters (category "Lo") for this purpose.
     *
     * <p> Note also that not all letters have case: many Unicode characters are
     * letters but are neither uppercase nor lowercase nor titlecase.
     *
     * @param   ch   the character to be tested.
     * @return  <code>true</code> if the character is a letter;
     *          <code>false</code> otherwise.
     * @see     java.lang.Character#isDigit(char)
     * @see     java.lang.Character#isJavaIdentifierStart(char)
     * @see     java.lang.Character#isJavaLetter(char)
     * @see     java.lang.Character#isJavaLetterOrDigit(char)
     * @see     java.lang.Character#isLetterOrDigit(char)
     * @see     java.lang.Character#isLowerCase(char)
     * @see     java.lang.Character#isTitleCase(char)
     * @see     java.lang.Character#isUnicodeIdentifierStart(char)
     * @see     java.lang.Character#isUpperCase(char)
     */
    public static boolean isLetter(final char ch) {
        return (((((1 << UPPERCASE_LETTER) |
                   (1 << LOWERCASE_LETTER) |
                   (1 << TITLECASE_LETTER) |
                   (1 << MODIFIER_LETTER) |
                   (1 << OTHER_LETTER))
                  >> (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x1F)) & 1) != 0);
    }

    /**
     * Determines if the specified character is a digit.
     * A character is considered to be a digit if it is not in the range
     * <code>'&#92;u2000'&nbsp;&lt;=&nbsp;ch&nbsp;&lt;=&nbsp;'&#92;u2FFF'</code>
     * and its Unicode name contains the word
     * "<code>DIGIT</code>". For a more complete
     * specification that encompasses all Unicode characters that are
     * defined as digits, see Gosling, Joy, and Steele, <i>The Java
     * Language Specification</i>.
     * <p>
     * These are the ranges of Unicode characters that are considered digits:
     * <table>
     * <tr><td>0x0030 through 0x0039</td>
     *                        <td>ISO-LATIN-1 digits ('0' through '9')</td></tr>
     * <tr><td>0x0660 through 0x0669</td>  <td>Arabic-Indic digits</td></tr>
     * <tr><td>0x06F0 through 0x06F9</td>
     *                                <td>Extended Arabic-Indic digits</td></tr>
     * <tr><td>0x0966 through 0x096F</td>  <td>Devanagari digits</td></tr>
     * <tr><td>0x09E6 through 0x09EF</td>  <td>Bengali digits</td></tr>
     * <tr><td>0x0A66 through 0x0A6F</td>  <td>Gurmukhi digits</td></tr>
     * <tr><td>0x0AE6 through 0x0AEF</td>  <td>Gujarati digits</td></tr>
     * <tr><td>0x0B66 through 0x0B6F</td>  <td>Oriya digits</td></tr>
     * <tr><td>0x0BE7 through 0x0BEF</td>  <td>Tamil digits</td></tr>
     * <tr><td>0x0C66 through 0x0C6F</td>  <td>Telugu digits</td></tr>
     * <tr><td>0x0CE6 through 0x0CEF</td>  <td>Kannada digits</td></tr>
     * <tr><td>0x0D66 through 0x0D6F</td>  <td>Malayalam digits</td></tr>
     * <tr><td>0x0E50 through 0x0E59</td>  <td>Thai digits</td></tr>
     * <tr><td>0x0ED0 through 0x0ED9</td>  <td>Lao digits</td></tr>
     * <tr><td>0x0F20 through 0x0F29</td>  <td>Tibetan digits</td></tr>
     * <tr><td>0xFF10 through 0xFF19</td>  <td>Fullwidth digits</td></tr>
     * </table>
     *
     * @param   ch   the character to be tested.
     * @return  <code>true</code> if the character is a digit;
     *          <code>false</code> otherwise.
     * @see     java.lang.Character#digit(char, int)
     * @see     java.lang.Character#forDigit(int, int)
     */
    public static boolean isDigit(final char ch) {
        return (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x1F) == DECIMAL_DIGIT_NUMBER;
    }

    /**
     * The given character is mapped to its lowercase equivalent; if the
     * character has no lowercase equivalent, the character itself is
     * returned.
     * <p>
     * A character has a lowercase equivalent if and only if a lowercase
     * mapping is specified for the character in the Unicode attribute
     * table.
     * <p>
     * Note that some Unicode characters in the range
     * <code>'&#92;u2000'</code> to <code>'&#92;u2FFF'</code> have lowercase
     * mappings; this method does map such characters to their lowercase
     * equivalents even though the method <code>isUpperCase</code> does
     * not return <code>true</code> for such characters.
     *
     * @param   ch   the character to be converted.
     * @return  the lowercase equivalent of the character, if any;
     *          otherwise the character itself.
     * @see     java.lang.Character#isLowerCase(char)
     * @see     java.lang.Character#isUpperCase(char)
     * @see     java.lang.Character#toTitleCase(char)
     * @see     java.lang.Character#toUpperCase(char)
     */
    public static char toLowerCase(final char ch) {
        final int val = A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)];

        if ((val & 0x00200000) != 0) {
          return (char)(ch + (val >> 22));
        } else {
          return ch;
        }
    }

    public static char[] toLowerCase(final char[] ch) {
        final char[] res = new char[ch.length];
        for (int i = 0; i < ch.length; i++) {
            res[i] = toLowerCase(ch[i]);
        }

        return res;
    }

    public static String toLowerCase(final String s) {
        return new String(toLowerCase(s.toCharArray()));
    }
}