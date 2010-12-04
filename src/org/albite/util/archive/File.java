/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.archive;

import java.io.IOException;

/**
 *
 * @author albus
 */
public interface File {
    public int fileSize() throws IOException;
    public String getURL();
}
