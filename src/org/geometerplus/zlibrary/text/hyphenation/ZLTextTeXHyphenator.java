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

public final class ZLTextTeXHyphenator { //extends ZLTextHyphenator {
	private final Hashtable myPatternTable = new Hashtable();
	private short language = -1;
	
	public ZLTextTeXHyphenator() {
	}

	void addPattern(ZLTextTeXHyphenationPattern pattern) {
		myPatternTable.put(pattern, pattern);
	}

	public void load(final short language) {
//          Check if last loaded language is the same
            if (this.language == language) {
                return;
            }

            unload();

            InputStream in = getClass().getResourceAsStream("/res/tex/" + language + ".tex");
            if (in != null) { //only if file exists
                try {
                    DataInputStream din = new DataInputStream(in);
                    int size = din.readShort();
                    for (int i = 0; i < size; i++) {
                        char[] pattern = din.readUTF().toCharArray();
                        addPattern(new ZLTextTeXHyphenationPattern(pattern, 0, pattern.length, true));
                    }
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    //not found so won't hyphenate
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {System.err.println(e.getMessage());}
                }
            }
            this.language = language;
        }


	public void unload() {
		myPatternTable.clear();
	}

	public boolean hyphenate(char[] stringToHyphenate, boolean[] mask, int length) {

            if (myPatternTable.isEmpty()) {
                for (int i = 0; i < length - 1; i++) {
                        mask[i] = false;
                }
                return false;
            }

            byte[] values = new byte[length + 1];

            final Hashtable table = myPatternTable; //WOW!? Some kindda optimization technique
            ZLTextTeXHyphenationPattern pattern = new ZLTextTeXHyphenationPattern(stringToHyphenate, 0, length, false);
            for (int offset = 0; offset < length - 1; offset++) {

                    int len = length - offset + 1;
                    pattern.update(stringToHyphenate, offset, len - 1);
                    while (--len > 0) {
                            pattern.myLength = len;
                            pattern.myHashCode = 0;
                            ZLTextTeXHyphenationPattern toApply = (ZLTextTeXHyphenationPattern)table.get(pattern);
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

        public ZLTextHyphenationInfo getInfo(final char[] word, int offset, int len) {
		final char[] pattern = new char[len + 2];
		pattern[0] = ' ';
                for (int i=0; i<len; i++) {
                    pattern[i+1] = Character.toLowerCase(word[offset + i]);
                }

		pattern[len + 1] = ' ';
		final ZLTextHyphenationInfo info = new ZLTextHyphenationInfo(len + 2);
		final boolean[] mask = info.Mask;
		boolean res = hyphenate(pattern, mask, len + 2);
		for (int i = 0; i <= len; ++i) {
			if ((i < 2) || (i > len - 2)) { //can't put hyphen after last char, right?
                            mask[i] = false;
			} else if (word[offset + i] == '-') {
                            mask[i] = true;
//                            mask[i] = (i >= 3)
//                                && isLetter(word[offset + i - 3])
//                                && isLetter(word[offset + i - 2])
//                                && isLetter(word[offset + i])
//                                && isLetter(word[offset + i + 1]);
			} else {
                            mask[i] = mask[i]
                                && isLetter(word[offset + i - 2])
                                && isLetter(word[offset + i - 1])
                                && isLetter(word[offset + i])
                                && isLetter(word[offset + i + 1]);
			}
		}
		return info;
	}

        public static boolean isLetter(char c) {
            //very simple check that consideres everything a letter, excluding
            //the regions that are certain NOT to contain letters
            if (c < 0x41)
                return false;
            if (c > 0xC0)
                return true;
            if (c > 0x5A && c < 0x61)
                return false;
            if (c > 0x7A)
                return false;
            return true;
        }
}