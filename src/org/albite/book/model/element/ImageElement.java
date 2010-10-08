/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.element;

import org.albite.util.archive.zip.ArchiveZipEntry;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class ImageElement implements Element {
    public final ArchiveZipEntry   entry;
    public final char[]            text;

    public ImageElement(final ArchiveZipEntry file, final char[] text) {
        this.entry = file;
        this.text = text;
    }
}