/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;
import org.albite.book.parser.AlbiteTextParser;
import org.albite.dictionary.DictionaryException;
import org.albite.dictionary.LocalDictionary;
import org.albite.util.archive.Archive;
import org.albite.util.archive.ArchiveException;
import org.albite.util.archive.ArchivedFile;
import org.albite.io.AlbiteStreamReader;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author albus
 */
public class AlbiteBook extends Book {
    private static final String BOOK_TAG                    = "book";
    private static final String BOOK_TITLE_TAG              = "title";
    private static final String BOOK_AUTHOR_TAG             = "author";
    private static final String BOOK_DESCRIPTION_TAG        = "description";
    private static final String BOOK_LANGUAGE_TAG           = "language";
    private static final String BOOK_META_TAG               = "meta";
    private static final String BOOK_THUMBNAIL_ATTRIB       = "thumbnail";
    private static final String BOOK_DICTIONARY_ATTRIB      = "dictionary";

    final private static String INFO_TAG                    = "info";
    final private static String INFO_NAME_ATTRIB            = "name";

    final private static String CHAPTER_TAG                 = "chapter";
    final private static String CHAPTER_SOURCE_ATTRIB       = "src";

    /*
     * Book file
     */
    private Archive bookArchive;

    /*
     * Contains various book attribs,
     * e.g. 'fiction', 'for_children', 'prose', etc.
     */
    private Hashtable           meta;
    private ArchivedFile        thumbImageFile              = null;

    private LocalDictionary     dict                        = null;

    public AlbiteBook(final String filename)
            throws IOException, BookException, ArchiveException {

        super(filename, new AlbiteTextParser());

        bookArchive = new Archive(filename);

        try {
            /*
             * load chapters info (filename + title)
             */
            chapters = loadChaptersDescriptor();

            /*
             * load book description (title, author, etc.)
             */
            loadBookDescriptor();
        } catch (IOException ioe) {
        } catch (BookException be) {
            close();
            throw be;
        }
    }

    private void loadBookDescriptor() throws BookException, IOException {

        ArchivedFile bookDescriptor = bookArchive.getFile("book.xml");

        if (bookDescriptor == null) {
            throw new BookException("Missing book descriptor <book.xml>");
        }

        InputStream in = bookDescriptor.openInputStream();
        try {
            meta = new Hashtable(10);

            KXmlParser parser = null;
            Document doc = null;
            Element root;
            Element kid;

            try {
                parser = new KXmlParser();
                parser.setInput(new InputStreamReader(in));

                doc = new Document();
                doc.parse(parser);
                parser = null;

            } catch (XmlPullParserException xppe) {
                parser = null;
                doc = null;
                throw new BookException(
                    "Book descriptor <book.xml> contains wrong data.");
            }

            root = doc.getRootElement();

            final String thumbString =
                    root.getAttributeValue(
                        KXmlParser.NO_NAMESPACE,
                        BOOK_THUMBNAIL_ATTRIB);

            if (thumbString != null) {
                thumbImageFile = bookArchive.getFile(thumbString);
            }

            final String dictString =
                    root.getAttributeValue(
                        KXmlParser.NO_NAMESPACE,
                        BOOK_DICTIONARY_ATTRIB);

            if (dictString != null) {
                try {
                    dict = new LocalDictionary(bookArchive.getFile(dictString));
                } catch (DictionaryException e) {

                    /*
                     * Couldn't load dict.
                     */
                    dict = null;
                }
            }

            int child_count = root.getChildCount();
            for (int i = 0; i < child_count ; i++ ) {
                if (root.getType(i) != Node.ELEMENT) {
                    continue;
                }

                kid = root.getElement(i);
                if (kid.getName().equals(BOOK_TITLE_TAG)) {
                    title = kid.getText(0);
                }

                if (kid.getName().equals(BOOK_AUTHOR_TAG)) {
                    author = kid.getText(0);
                }

                if (kid.getName().equals(BOOK_DESCRIPTION_TAG)) {
                    description = kid.getText(0);
                }

                if (kid.getName().equals(BOOK_LANGUAGE_TAG))
                    try {
                        language = Short.parseShort(kid.getText(0));
                        if (language < 1 || language > Languages.LANGS_COUNT) {
                            language = Languages.LANG_UNKNOWN; //set to default
                        }
                    } catch (NumberFormatException nfe) {
                        language = Languages.LANG_UNKNOWN; //set to default
                    }

                if (kid.getName().equals(BOOK_META_TAG)) {

                    final int metaCount = kid.getChildCount();

                    for (int m = 0; m < metaCount; m++) {

                        final Element metaField = kid.getElement(m);

                        if (metaField != null) {

                            if (metaField.getName().equals(INFO_TAG)) {

                                final String infoName =
                                        metaField.getAttributeValue(
                                        KXmlParser.NO_NAMESPACE,
                                        INFO_NAME_ATTRIB);

                                final String infoValue =
                                        metaField.getText(0);

                                if (infoName != null && infoValue != null) {
                                    meta.put(infoName, infoValue);
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            in.close();
        }
    }

    public final LocalDictionary getDictionary() {
        return dict;
    }

    public final Hashtable getMeta() {
        return meta;
    }

    public final ArchivedFile getThumbImageFile() {
        return thumbImageFile;
    }

    public final String getURL() {
        return (bookArchive).getURL();
    }

    protected final Chapter[] loadChaptersDescriptor()
            throws BookException, IOException {

        ArchivedFile tocDescriptor = bookArchive.getFile("toc.xml");
        if (tocDescriptor == null)
            throw new BookException("Missing TOC descriptor <toc.xml>");

        InputStream in = tocDescriptor.openInputStream();

        try {
            KXmlParser parser = null;
            Document doc = null;
            Element root;
            Element kid;

            try {
                parser = new KXmlParser();
                parser.setInput(new InputStreamReader(in));

                doc = new Document();
                doc.parse(parser);
                parser = null;
            } catch (XmlPullParserException xppe) {
                parser = null;
                doc = null;
                throw new BookException(
                        "TOC descriptor <toc.xml> contains wrong data.");
            }

            root = doc.getRootElement();
            int child_count = root.getChildCount();

            String chapterFileName = null;
            String chapterTitle = null;

            Vector chaptersVector = new Vector();
            int currentChapterNumber = 0;
            Chapter prev = null;

            ArchivedFile af = null;

            for (int i = 0; i < child_count ; i++ ) {
                if (root.getType(i) != Node.ELEMENT) {
                        continue;
                }

                kid = root.getElement(i);
                if (kid.getName().equals(CHAPTER_TAG)) {

                    currentChapterNumber++;

                    chapterFileName = kid.getAttributeValue(
                            KXmlParser.NO_NAMESPACE, CHAPTER_SOURCE_ATTRIB);

                    if (chapterFileName == null)
                        throw new BookException(
                                "Invalid TOC descriptor: chapter does not"
                                + "provide src information.");

                    if (kid.getChildCount() > 0) {
                        chapterTitle = kid.getText(0);
                        if (chapterTitle == null
                                || chapterTitle.length() == 0
                                || chapterTitle.trim().length() == 0)
                        {
                            chapterTitle = "Chapter #" + (currentChapterNumber);
                        }
                    } else {
                        chapterTitle = "Chapter #" + (currentChapterNumber);
                    }

                    af = bookArchive.getFile(chapterFileName);
                    if (af == null) {
                        throw new BookException("Chapter #" + currentChapterNumber
                                + " declared, but its file <" + chapterFileName
                                + "> is missing");
                    }

                    final Chapter cur =
                            new Chapter(af, af.fileSize(),
                            AlbiteStreamReader.DEFAULT_ENCODING,
                            chapterTitle, currentChapterNumber - 1);
                    /*
                     * TODO: Read encoding from file or if it's XHTML it may
                     * be even easier!
                     */

                    chaptersVector.addElement(cur);

                    if (prev != null) {
                        prev.setNextChapter(cur);
                        cur.setPrevChapter(prev);
                    }

                    prev = cur;
                }
            }

            if (currentChapterNumber < 1) {
                throw new BookException(
                        "No chapters were found in the TOC descriptor.");
            }

            final Chapter[] chapters = new Chapter[chaptersVector.size()];

            for (int i = 0; i < chaptersVector.size(); i ++) {
                chapters[i] = (Chapter) chaptersVector.elementAt(i);
            }

            currentChapter = chapters[0]; //default value

            return chapters;
        } finally {
            in.close();
        }
    }

    public final Archive getBookArchive() {
        return bookArchive;
    }

    public final void close() throws IOException {
        bookArchive.close();
        super.close();
    }
}