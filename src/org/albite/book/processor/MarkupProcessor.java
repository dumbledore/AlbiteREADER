/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.processor;

import javax.microedition.io.InputConnection;
import org.albite.book.model.element.Element;
import org.albite.util.archive.zip.ArchiveZip;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public interface MarkupProcessor {
    public abstract Element[] getElements(
            ArchiveZip archive,
            InputConnection file,
            int fileSize,
            String encoding);
}