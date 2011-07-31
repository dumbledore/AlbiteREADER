package org.albite.book.model.book;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java13.io.BufferedInputStream;
import javax.microedition.io.InputConnection;
import org.albite.albite.AlbiteMIDlet;
import org.albite.io.RandomReadingFile;
import org.albite.io.decoders.AlbiteStreamReader;
import org.albite.io.decoders.Encodings;
import org.albite.io.html.XhtmlStreamReader;
import org.albite.util.archive.File;

public class Chapter {

    public static final String     AUTO_ENCODING = "-";

    /*
     * Can be overwritten by the user
     */
    private String                  currentEncoding = AUTO_ENCODING;

    private final String            title;

    /*
     * Chapter's file & its size
     */
    private final InputConnection   file;
    private final int               fileSize;
    private final File              pathReference;

    private Chapter                 prevChapter;
    private Chapter                 nextChapter;

    private char[]                  textBuffer;

    private final boolean           processHtmlEntities;

    private int                     currentPosition = 0;

    private final int               number;

    public Chapter(
            final InputConnection file,
            final int fileSize,
            final File pathReference,
            final String title,
            final boolean processHtmlEntities,
            final int number) {

        this.file = file;
        this.fileSize = fileSize;
        this.pathReference = pathReference;
        this.title = title;
        this.processHtmlEntities = processHtmlEntities;
        this.number = number;
    }

    public final String getTitle() {
        return title;
    }

    public final Chapter getPrevChapter() {
        return prevChapter;
    }

    public final void setPrevChapter(final Chapter bc) {
        prevChapter = bc;
    }

    public final Chapter getNextChapter() {
        return nextChapter;
    }

    public final void setNextChapter(final Chapter bc) {
        nextChapter = bc;
    }

    public final char[] getTextBuffer() {
        if (textBuffer == null) {
            try {
                InputStream in = file.openInputStream();
                Reader r = null;
                AlbiteStreamReader asr = null;

                final boolean auto =
                        AUTO_ENCODING.equalsIgnoreCase(currentEncoding);

                if (auto) {
                    currentEncoding = Encodings.DEFAULT;
                }

                if (processHtmlEntities) {
                    if (!in.markSupported()) {
                        in = new BufferedInputStream(in);
                    }

                    /*
                     * Warning: if the XhtmlStreamReader is not used,
                     * then the HtmlParser won't work, as
                     * it relies on modified versions of '<' and '>'
                     */
                    asr = new AlbiteStreamReader(in, currentEncoding);
                    r = new XhtmlStreamReader(asr, auto, true);
                } else {
                    asr = new AlbiteStreamReader(in, currentEncoding);
                    r = asr;
                }

                try {
                    textBuffer = new char[fileSize];
                    int read = r.read(textBuffer);

                    if (read == -1) {
                        return new char[0];
                    }

                    if (read < fileSize) {
                        char[] res = new char[read];
                        System.arraycopy(textBuffer, 0, res, 0, read);
                        textBuffer = res;
                    }

                    currentEncoding = asr.getEncoding();

                } catch (IOException e) {
                    //#debug
                    AlbiteMIDlet.LOGGER.log(e);
                    textBuffer = new char[0];
                } finally {
                    in.close();
                }
            } catch (Exception e) {
                /*
                 * couldn't load the chapter,
                 * it will be rendered as "empty chapter"
                 */
                //#debug
                AlbiteMIDlet.LOGGER.log(e);
                textBuffer = new char[0];
            }
        }

        return textBuffer;
    }

    public final void unload() {
        textBuffer = null;
    }

    public final int getCurrentPosition() {
        return currentPosition;
    }

    public final void setCurrentPosition(final int pos) {
        if (pos < 0) {
            currentPosition = 0;
        }

        currentPosition = pos;
    }

    public final int getNumber() {
        return number;
    }

    public final String getEncoding() {
        return currentEncoding;
    }

    public String getPath() {
        if (pathReference != null) {
            return RandomReadingFile.getPathFromURL(pathReference.getURL());
        } else {
            return "";
        }
    }

    protected final boolean setEncoding(final String encoding) {
        if (
                encoding != null
                && !encoding.equalsIgnoreCase(currentEncoding)
                && (encoding.equalsIgnoreCase(AUTO_ENCODING)
                    || AlbiteStreamReader.encodingSupported(encoding))

                ) {

            /*
             * A new encoding, that's different from current's
             */
            currentEncoding = encoding;

            /*
             * Invalidate current buffer
             */
            textBuffer = null;

            return true;
        }

        return false;
    }
}