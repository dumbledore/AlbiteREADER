/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.processor;

import java.io.IOException;
import javax.microedition.io.InputConnection;
import org.albite.book.model.element.Element;
import org.albite.book.model.element.TextElement;
import org.albite.io.AlbiteStreamReader;
import org.albite.util.archive.zip.ArchiveZip;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class PlainTextProcessor implements MarkupProcessor {

    public final Element[] getElements(
            final ArchiveZip archive,
            final InputConnection file,
            final int fileSize,
            final String encoding) {

        char[] textBuffer = null;

        try {
            AlbiteStreamReader r = new AlbiteStreamReader(
                    file.openInputStream(), encoding);

            try {
                textBuffer = r.read(fileSize);
            } catch (IOException e) {
                textBuffer = new char[0];
            } finally {
                r.close();
            }
        } catch (Exception e) {
            /*
             * couldn't load the chapter,
             * it will be rendered as "empty chapter"
             */
            textBuffer = new char[0];
        }

        return new Element[] {new TextElement(textBuffer)};
    }
}