/*
 * Copyright (C) 2007-2010 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.text.hyphenation;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import org.albite.lang.AlbiteCharacter;

///#define DEBUG_HYPHENATION

public final class ZLTextTeXHyphenator {
    private final Hashtable myPatternTable  = new Hashtable();
    private final String    language;

    void addPattern(final ZLTextTeXHyphenationPattern pattern) {
        myPatternTable.put(pattern, pattern);
    }

    public final String getLanguage() {
        return language;
    }

    public ZLTextTeXHyphenator(final String language) throws IOException {
        //#debug
        System.out.println("Creating hyphenator for _" + language + "_");

        this.language = language;
        final String resString = "/res/tex/" + language + ".tex";

        /*
         * Load tex file
         */
        //#debug
        System.out.println("Loading '" + resString + "'...");

        InputStream in = getClass().getResourceAsStream(resString);

        if (in != null) { //only if file exists
            try {
                DataInputStream din = new DataInputStream(in);
                int size = din.readShort();
                for (int i = 0; i < size; i++) {
                    char[] pattern = din.readUTF().toCharArray();
                    addPattern(
                            new ZLTextTeXHyphenationPattern(
                                    pattern, 0, pattern.length, true));
                }
                //#debug
                System.out.println("Hyphenator loaded successfully.");
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    //#debug
                    e.printStackTrace();
                }
            }
        }
    }

    //#ifdef DEBUG_HYPHENATION
//#     private static String printPattern(
//#             final char[] pattern, final boolean[] mask) {
//#
//#         final StringBuffer b = new StringBuffer(pattern.length * 2);
//#
//#         if (mask[0]) {
//#             b.append('~');
//#         }
//#
//#         for (int i = 1; i < mask.length; i++) {
//#             b.append(pattern[i]);
//#
//#             if (mask[i]) {
//#                 b.append('~');
//#             }
//#         }
//#
//#         return "_" + b.toString() + "_";
//#     }
    //#endif

    public final boolean hyphenate(
            final char[] stringToHyphenate,
            final boolean[] mask,
            final int length) {

        if (myPatternTable.isEmpty()) {
            for (int i = 0; i < length - 1; i++) {
                mask[i] = false;
            }
            return false;
        }

        byte[] values = new byte[length + 1];

        final Hashtable table = myPatternTable;
        ZLTextTeXHyphenationPattern pattern =
                new ZLTextTeXHyphenationPattern(stringToHyphenate, 0,
                length, false);

        for (int offset = 0; offset < length - 1; offset++) {

            int len = length - offset + 1;
            pattern.update(stringToHyphenate, offset, len - 1);
            while (--len > 0) {
                pattern.myLength = len;
                pattern.myHashCode = 0;
                ZLTextTeXHyphenationPattern toApply =
                        (ZLTextTeXHyphenationPattern) table.get(pattern);

                if (toApply != null) { //If found
                    toApply.apply(values, offset);
                }
            }
        }

        for (int i = 0; i < length - 1; i++) {
                mask[i] = (values[i + 1] % 2) == 1;
        }
        
        return true;
    }

    public final ZLTextHyphenationInfo getInfo(
            final char[] word, final int offset, final int len) {

        final boolean[] isLetter = new boolean[len];
        final char[] pattern = new char[len + 2];
        pattern[0] = ' ';
        for (int i = 0, j = offset; i < len; ++i, ++j) {
                char symbol = word[j];
                if (AlbiteCharacter.isLetter(symbol)) {
                        isLetter[i] = true;
                        pattern[i + 1] = AlbiteCharacter.toLowerCase(symbol);
                } else {
                        pattern[i + 1] = ' ';
                }
        }
        pattern[len + 1] = ' ';

        final ZLTextHyphenationInfo info = new ZLTextHyphenationInfo(len + 2);
        final boolean[] mask = info.Mask;

        hyphenate(pattern, mask, len + 2);

        //#ifdef DEBUG_HYPHENATION
//#         final String patHyphS = printPattern(pattern, mask);
//#         System.out.println(patHyphS);
        //#endif

        for (int i = 0, j = offset - 1; i <= len; ++i, ++j) {
                if ((i < 2) || (i > len - 2)) {
                        mask[i] = false;
                } else if (word[j] == '-') {
                        mask[i] = (i >= 3)
                                && isLetter[i - 3]
                                && isLetter[i - 2]
                                && isLetter[i]
                                && isLetter[i + 1];
                } else {
                        mask[i] = mask[i]
                                && isLetter[i - 2]
                                && isLetter[i - 1]
                                && isLetter[i]
                                && isLetter[i + 1];
                }
        }

        //#ifdef DEBUG_HYPHENATION
//#         final String patProcS = printPattern(pattern, mask);
//#         if (!patHyphS.equals(patProcS)) {
//#             System.out.println(patProcS);
//#         }
//#         System.out.println();
        //#endif

        return info;
    }
//    public final ZLTextHyphenationInfo getInfo(
//            final char[] word, final int offset, final int len) {
//
//        final char[] pattern = new char[len + 2];
//        pattern[0] = ' ';
//        for (int i = 0; i < len; i++) {
//            pattern[i + 1] = AlbiteCharacter.toLowerCase(word[offset + i]);
//        }
//
//        pattern[len + 1] = ' ';
//        final ZLTextHyphenationInfo info = new ZLTextHyphenationInfo(len + 2);
//        final boolean[] mask = info.Mask;
//        boolean res = hyphenate(pattern, mask, len + 2);
//        for (int i = 0; i <= len; ++i) {
//            if ((i < 2) || (i > len - 2)) {
//                /*
//                 * can't put hyphen after last char, right?
//                 */
//                mask[i] = false;
//            } else if (word[offset + i] == '-') {
//                mask[i] = true;
//            } else {
//                mask[i] = mask[i]
//                && AlbiteCharacter.isLetter(word[offset + i - 2])
//                && AlbiteCharacter.isLetter(word[offset + i - 1])
//                && AlbiteCharacter.isLetter(word[offset + i])
//                && AlbiteCharacter.isLetter(word[offset + i + 1]);
//            }
//        }
//        return info;
//    }
}