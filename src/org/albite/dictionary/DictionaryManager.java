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
        if (folder != null
                && !folder.equals("")
                && !folder.equalsIgnoreCase(this.folder)) {

            this.folder = folder;
            reloadDictionaries();
        }
    }

    public void closeDictionaries() {
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

        if (folder == null) {
            return;
        }

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
        } catch (Exception e) {
            /*
             * Couldn't open the folder
             */
        }
        
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