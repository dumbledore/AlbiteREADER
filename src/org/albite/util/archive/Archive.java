/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.archive;

import javax.microedition.io.Connection;

/**
 *
 * @author albus
 */
public interface Archive extends Connection, File {
    public ArchiveEntry getEntry(final String name);
}