/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Albus Dumbledore
 */
public class Dictionary {
    public static boolean DICTIONARY_FUNCTIONALITY_ENABLED = false;
    //used to check if the dictionary is actually working, i.e. index is loaded
    //and file is found. This way the application can run even without the
    //dictionary being avaiable

    char[][]    entriesNames;
    int[]       entriesPositions;
    short       language;
    int         entriesCount;

    public void load(final short language) {
//      Check if last loaded language is the same
        if (this.language == language) {
            return;
        }

        InputStream in = getClass().getResourceAsStream("/res/dictindex/" + language + ".bin");
        if (in != null) {
            try {
                DataInputStream din = new DataInputStream(in);
                int size = din.readShort();
                entriesNames = new char[size][];
                entriesPositions = new int[size];
                entriesCount = size;
                for (int i = 0; i < size; i++) {
                    entriesNames[i] = din.readUTF().toCharArray();
                    entriesPositions[i] = i; //TODO: enter real positions
                }

                //dictionary loaded
                DICTIONARY_FUNCTIONALITY_ENABLED = true;

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            DICTIONARY_FUNCTIONALITY_ENABLED = false;
        }
    }

        int compareCharArrays(char[] c1, int c1Offset, int c1Len, char[] c2, int c2Offset, int c2Len) {

        if (c1Offset + c1Len > c1.length || c2Offset + c2Len > c2.length) throw new IllegalArgumentException("Char arrays supplied with bad indices.");
        int search_range = c1Len; //we need the smallest range

        if (c2Len < c1Len)
            search_range = c2Len;

        for (int i = 0; i < search_range; i++) {
            char c1x = c1[i+c1Offset];
            char c2x = c2[i+c2Offset];

            if (c1x == c2x) //two words still match
                continue;

            if (c1x < c2x)
                return -1; // c1 is before

            return 1; //c1 is after
        }

        if (c1Len == c2Len)
            return 0; // the same

        if (c1Len < c2Len)
            return -1; // c1 is before

        return 1; // c1 is after
    }

    public int findFirst(char[] text, int offset, int length) {
        final int size = entriesCount;
        int comparisonResult = 1;
        int lastIndex = 0;
        while (true){//lastIndex<size && comparisonResult > 0) {
            if (lastIndex >= size)
                return -1; //Not found

            comparisonResult = compareCharArrays(text, offset, length, entriesNames[lastIndex], 0, entriesNames[lastIndex].length);

            if (comparisonResult <= 0)
                return lastIndex-1; //implies that last item was OK

            lastIndex++;
        }
    }

    public char[] getEntryName(int i) {
        return entriesNames[i];
    }

    public int getEntryPosition(int i) {
        return entriesPositions[i];
    }

    public char[] getEntryText(int i) {
        char[] res = {'2'}; //TODO: Read from file stream
        return res;
    }
}