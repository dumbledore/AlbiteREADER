/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.dictionary;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 *
 * @author albus
 */
public class DictionaryManager {
    private String          folder          = null;
    private Dictionary[]    dictionaries    = null;

    public final void reloadDictionaries(final String folder) {
        if (folder != null && !folder.equalsIgnoreCase(this.folder)) {
            this.folder = folder;
            reloadDictionaries();
        }
    }

    private void closeDictionaries() {
        if (dictionaries != null) {
            for (int i = 0; i < dictionaries.length; i++) {
                try {
                    dictionaries[i].close();
                } catch (IOException e) {}
            }
        }
    }

    public final void reloadDictionaries() {

        closeDictionaries();

        final Vector dicts = new Vector();

        try {
            FileConnection f =
                    (FileConnection) Connector.open(folder, Connector.READ);

            if (f.exists() && f.isDirectory()) {

                final Enumeration filesList =
                        f.list("*" + Dictionary.FILE_EXTENSION, true);

                while (filesList.hasMoreElements()) {

                    final String s = (String) filesList.nextElement();

                    try {
                        /*
                         * Trying to load dictionary
                         */
                        final Dictionary dict =
                                new Dictionary(folder + s);
                        dicts.addElement(dict);
                    } catch (DictionaryException e) {
                        /*
                         * If a problem has occurred,
                         * don't add the dict.
                         */
                    }
                }
            }
        }

        /*
         * Couldn't open the folder
         */
        catch (IOException e) {}
        catch (SecurityException e) {}
        catch (IllegalArgumentException e) {}

        final int size = dicts.size();

        if (size > 0) {
            dictionaries = new Dictionary[size];

            for (int i = 0; i < size; i++) {
                dictionaries[i] = (Dictionary) dicts.elementAt(i);
            }
        } else {
            dictionaries = null;
        }
    }

//    public final void updateCurrentDictionaries() {
//
//        /*
//         * Unload current dicts
//         */
//        if (localDictionaries != null) {
//            for (int i = 0; i < localDictionaries.length; i++) {
//                localDictionaries[i].unload();
//            }
//        }
//
//        if (localDictionaries == null) {
//            currentLocalDictionaries = null;
//            return;
//        }
//
//        /*
//         * Make new list
//         */
//        Vector v = new Vector();
//        for (int i = 0; i < localDictionaries.length; i++) {
//            final LocalDictionary d = localDictionaries[i];
//
//            if (d.getLanguage() != null && d.getLanguage().equalsIgnoreCase(language)) {
//                v.addElement(d);
//            }
//        }
//
//        final int size = v.size();
//
//        if (size > 0) {
//            currentLocalDictionaries = new LocalDictionary[size];
//
//            for (int i = 0; i < size; i++) {
//                currentLocalDictionaries[i] = (LocalDictionary) v.elementAt(i);
//            }
//        } else {
//            currentLocalDictionaries = null;
//        }
//    }

//    public final void setCurrentBookDictionary(final LocalDictionary d) {
//        currentBookDictionary = d;
//    }

//    public final Dictionary getCurrentBookDictionary() {
//        return currentBookDictionary;
//    }

    public final Dictionary[] getDictionaries() {
        return dictionaries;
    }

    /**
     * Unloads dicts' indices
     */
    public final void unloadDictionaries() {
        if (dictionaries != null) {
            for (int i = 0; i < dictionaries.length; i++) {
                dictionaries[i].unload();
            }
        }
    }
}