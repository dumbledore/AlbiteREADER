/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.archive.folder;

import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import org.albite.util.archive.Archive;
import org.albite.util.archive.ArchiveEntry;

/**
 *
 * @author albus
 */
public class ArchiveFolder implements Archive {
    private final String url;

    public ArchiveFolder(final String url) {
        this.url = url;
    }

    public ArchiveEntry getEntry(String name) {
        try {
            //#debug
            System.out.println("Searching for *" + name + "*");

            FileConnection file = (FileConnection) Connector.open(name);
            if (file.exists() && !file.isDirectory()) {
                return new ArchiveFileEntry(file);
            }
        } catch (IOException e) {
            //#debug
            e.printStackTrace();
        }

        return null;
    }

    public void close() throws IOException {
        /*
         * Does nothing as there is nothing to be done
         */
    }

    public int fileSize() {
        return 0;
    }

    public String getURL() {
        return url;
    }
}
