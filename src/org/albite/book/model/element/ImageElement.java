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
public class ImageElement extends TextElement {
    public final ArchiveZipEntry   entry;

    public ImageElement(final ArchiveZipEntry file, final char[] text) {
        super(text);
        this.entry = file;
    }

    public final byte getType() {
        return IMAGE;
    }
}