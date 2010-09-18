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
import org.albite.util.text.TextTools;

public final class ZLTextTeXHyphenator { //extends ZLTextHyphenator {
    private final Hashtable myPatternTable = new Hashtable();
    private short language = -1;

    void addPattern(ZLTextTeXHyphenationPattern pattern) {
        myPatternTable.put(pattern, pattern);
    }

    public void load(final short language) {
        
        if (this.language == language) {
            return;
        }

        unload();

        /*
         * Load tex file
         */
        InputStream in = getClass().getResourceAsStream(
                "/res/tex/" + language + ".tex");

        if (in != null) { //only if file exists
            try {
                DataInputStream din = new DataInputStream(in);
                int size = din.readShort();
                for (int i = 0; i < size; i++) {
                    char[] pattern = din.readUTF().toCharArray();
                    addPattern(new ZLTextTeXHyphenationPattern(pattern, 0,
                            pattern.length, true));
                }
            } catch (IOException e) {
                e.printStackTrace();
                //not found so won't hyphenate
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        this.language = language;
    }


    public void unload() {
        myPatternTable.clear();
    }

    public boolean hyphenate(char[] stringToHyphenate,
            boolean[] mask, int length) {

        if (myPatternTable.isEmpty()) {
            for (int i = 0; i < length - 1; i++) {
                mask[i] = false;
            }
            return false;
        }

        byte[] values = new byte[length + 1];

        final Hashtable table = myPatternTable; //WOW!? Some kindda optimization technique
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
                        (ZLTextTeXHyphenationPattern)table.get(pattern);
                
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
            } else {
                mask[i] = mask[i]
                && TextTools.isLetter(word[offset + i - 2])
                && TextTools.isLetter(word[offset + i - 1])
                && TextTools.isLetter(word[offset + i])
                && TextTools.isLetter(word[offset + i + 1]);
            }
        }

        return info;
    }

}