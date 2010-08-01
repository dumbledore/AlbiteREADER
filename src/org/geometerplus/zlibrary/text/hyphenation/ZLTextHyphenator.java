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

//import java.util.*;
//import org.geometerplus.zlibrary.core.util.*;

//import org.geometerplus.zlibrary.text.view.ZLTextWord;

public abstract class ZLTextHyphenator {
	protected static ZLTextHyphenator ourInstance;
	
//	public static ZLTextHyphenator Instance() {
//		if (ourInstance == null) {
//			ourInstance = new ZLTextTeXHyphenator();
//		}
//		return ourInstance;
//	}
//
//	public static void deleteInstance() {
//		if (ourInstance != null) {
//			ourInstance.unload();
//			ourInstance = null;
//		}
//	}
//
//	protected ZLTextHyphenator() {
//	}

//	public abstract void load(final String language);
//	public abstract void unload();

	public ZLTextHyphenationInfo getInfo(final char[] word, int offset, int len) {
//		final boolean[] isLetter = new boolean[len];
		final char[] pattern = new char[len + 2];
		pattern[0] = ' ';
//                for (int i=0; i<len; i++) {
                for (int i=0; i<len; i++) {
                    pattern[i+1] = Character.toLowerCase(word[offset + i]);
//                    if (!Character.isLowerCase(pattern[i])) {
//                        pattern[i] = ' ';
//                        isLetter[i] = false;
//                    } else {
//                        isLetter[i] = true;
//                    }
                }

		pattern[len + 1] = ' ';
//                int i = (int)pattern[len+1];
		final ZLTextHyphenationInfo info = new ZLTextHyphenationInfo(len + 2);
		final boolean[] mask = info.Mask;
		hyphenate(pattern, mask, len + 2);
//		for (int i = 0, j = word.Offset - 1; i <= len; ++i, ++j) {
		for (int i = 0; i <= len; ++i) {
//			if ((i < 2) || (i > len - 2)) { //can't put hyphen after last char, right?
			if ((i < 2) || (i >= len - 2)) { //won't put hyphen if only one char left
				mask[i] = false;
			} else if (word[offset + i] == '-') {
				mask[i] = (i >= 3);
//					&& isLetter[i - 3]
//					&& isLetter[i - 2]
//					&& isLetter[i]
//					&& isLetter[i + 1];
			} else {
				mask[i] = mask[i];
//					&& isLetter[i - 2]
//					&& isLetter[i - 1]
//					&& isLetter[i]
//					&& isLetter[i + 1];
			}
		}

		return info;
	}

	protected abstract void hyphenate(char[] stringToHyphenate, boolean[] mask, int length);
}
