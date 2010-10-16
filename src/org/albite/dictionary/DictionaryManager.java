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
    private String              folder                      = null;
    private LocalDictionary[]   localDictionaries           = null;

    public final void reloadDictionaries(final String folder) {
        this.folder = folder;
        reloadDictionaries();
    }

    public final void reloadDictionaries() {

        if (folder != null) {
            final Vector dicts = new Vector();

            try {
                FileConnection f =
                        (FileConnection) Connector.open(folder, Connector.READ);

                if (f.exists() && f.isDirectory()) {

                    final Enumeration filesList =
                            f.list("*" + LocalDictionary.FILE_EXTENSION, true);

                    while (filesList.hasMoreElements()) {

                        final String s = (String) filesList.nextElement();

                        try {

                            /*
                             * Opening the dictionary file.
                             */
                            final FileConnection file = (FileConnection)
                                    Connector.open(folder + s, Connector.READ);

                            /*
                             * Trying to load dictionary
                             */
                            try {
                                final Dictionary dict =
                                        new LocalDictionary(file);

                                dicts.addElement(dict);
                            } catch (SecurityException e) {
                                file.close();
                            } catch (DictionaryException e) {
                                file.close();
                            }
                        /*
                         * If an exception is thrown, just skip the dict
                         */
                        } catch (IOException e) {
                        } catch (SecurityException e) {}
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
                localDictionaries = new LocalDictionary[size];

                for (int i = 0; i < size; i++) {
                    localDictionaries[i] = (LocalDictionary) dicts.elementAt(i);
                }
            } else {
                localDictionaries = null;
            }
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

    public final LocalDictionary[] getLocalDictionaries() {
        return localDictionaries;
    }

    public final void unloadDictionaries() {
        if (localDictionaries != null) {
            for (int i = 0; i < localDictionaries.length; i++) {
                localDictionaries[i].unload();
            }
        }
    }
}