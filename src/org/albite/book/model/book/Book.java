package org.albite.book.model.book;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import org.albite.book.model.parser.HTMLTextParser;
import org.albite.book.model.parser.PlainTextParser;
import org.albite.book.model.parser.TextParser;
import org.albite.util.archive.zip.ArchiveZip;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

public abstract class Book implements Connection {

    public static final String EPUB_EXTENSION = ".epub";
    public static final String PLAIN_TEXT_EXTENSION = ".txt";
    public static final String HTM_EXTENSION = ".htm";
    public static final String HTML_EXTENSION = ".html";
    public static final String XHTML_EXTENSION = ".xhtml";

    public static final String[] SUPPORTED_BOOK_EXTENSIONS = new String[] {
        EPUB_EXTENSION,
        PLAIN_TEXT_EXTENSION,
        HTM_EXTENSION, HTML_EXTENSION, XHTML_EXTENSION
    };

    protected static final String   USERDATA_BOOK_TAG        = "book";
    protected static final String   USERDATA_BOOKMARK_TAG    = "bookmark";
    protected static final String   USERDATA_LANGUAGE_ATTRIB = "language";
    protected static final String   USERDATA_SIZE_ATTRIB     = "filesize";

    protected static final String   USERDATA_CHAPTER_TAG     = "chapter";
    protected static final String   USERDATA_CHAPTER_ATTRIB  = "chapter";
    protected static final String   USERDATA_ENCODING_ATTRIB = "encoding";
    protected static final String   USERDATA_POSITION_ATTRIB = "position";


    /*
     * Main info
     */
    protected String                title                    = "Untitled";
    protected String                author                   = "Unknown Author";
    protected String                language                 = null;
    protected String                currentLanguage          = null;
    protected String                description              = null;

    /*
     * Contains various book attribs,
     * e.g. 'fiction', 'for_children', 'prose', etc.
     */
    protected BookmarkManager       bookmarks = new BookmarkManager();

    /*
     * .alx book user settings
     */
    protected FileConnection        userfile                 = null;

    /*
     * Chapters
     */
    protected Chapter[]             chapters;
    protected Chapter               currentChapter;

    protected TextParser            parser;

    public abstract void close() throws IOException;

    protected void closeUserFile() throws IOException {
        if (userfile != null) {
            userfile.close();
        }
    }

    public final String getLanguage() {
        return currentLanguage;
    }

    /**
     *
     * @param language
     * @return true, if the language was changed
     */
    public final boolean setLanguage(final String language) {
        if (language != null && !language.equalsIgnoreCase(currentLanguage)) {
            currentLanguage = language;
            return true;
        }

        return false;
    }

    public final String getDefaultLanguage() {
        return language;
    }

    /**
     * unload all chapters from memory
     */
    public final void unloadChaptersBuffers() {
        Chapter chap = chapters[0];
        while (chap != null) {
            chap.unload();
            chap = chap.getNextChapter();
        }
    }

    protected void loadUserFile(final String filename)
            throws BookException, IOException {

        /*
         * Set default chapter
         */
        currentChapter = chapters[0];

        /*
         * form user settings filename, i.e. ... .alb -> ... .alx
         * .txt -> .alx
         */
        int dotpos = filename.lastIndexOf('.');

        char[] alx_chars = new char[dotpos + 5]; //index + .alx + 1
        filename.getChars(0, dotpos +1, alx_chars, 0);
        alx_chars[dotpos+1] = 'a';
        alx_chars[dotpos+2] = 'l';
        alx_chars[dotpos+3] = 'x';

        String alx_filename = new String(alx_chars);

        try {
            userfile = (FileConnection) Connector.open(
                    alx_filename, Connector.READ_WRITE);

            if (!userfile.isDirectory()) {

                /*
                 * if there is a dir by that name,
                 * the functionality will be disabled
                 *
                 */
                if (!userfile.exists()) {

                    /*
                     * create the file if it doesn't exist
                     */
                    userfile.create();
                } else {

                    /*
                     * try to load user settings
                     */
                    loadUserData();
                }
            }
        } catch (SecurityException e) {
            if (userfile != null) {
                userfile.close();
                userfile = null;
            }
        } catch (IOException e) {
            if (userfile != null) {
                userfile.close();
                userfile = null;
            }
        } catch (BookException e) {

            /*
             * Obviously, the content is wrong. The file's content will be
             * overwritten as to prevent malformed files from
             * making it permanently impossible for the user to save date
             * for a particular book.
             */
        }
    }
    private void loadUserData() throws BookException, IOException {

        InputStream in = userfile.openInputStream();

        KXmlParser parser = null;
        Document doc = null;
        Element root;
        Element kid;

        try {
            parser = new KXmlParser();
            parser.setInput(new InputStreamReader(in, "UTF-8"));

            doc = new Document();
            doc.parse(parser);
            parser = null;
        } catch (XmlPullParserException e) {
            parser = null;
            doc = null;
            throw new BookException("Wrong XML data.");
        }

        try {

            /*
             * root element (<book>)
             */
            root = doc.getRootElement();

            try {
                int fileSize = Integer.parseInt(
                        root.getAttributeValue(
                        KXmlParser.NO_NAMESPACE, USERDATA_SIZE_ATTRIB));
                if (fileSize != fileSize()) {
                    throw new BookException("Wrong Filesize");
                }
            } catch (NumberFormatException nfe) {
                throw new BookException("Wrong Filesize");
            }

            final int cchapter = readIntFromXML(root, USERDATA_CHAPTER_ATTRIB);

            currentLanguage = root.getAttributeValue(
                    KXmlParser.NO_NAMESPACE, USERDATA_LANGUAGE_ATTRIB);

            int childCount = root.getChildCount();

            for (int i = 0; i < childCount ; i++ ) {
                if (root.getType(i) != Node.ELEMENT) {
                    continue;
                }

                kid = root.getElement(i);

                if (kid.getName().equals(USERDATA_BOOKMARK_TAG)
                        || kid.getName().equals(USERDATA_CHAPTER_TAG)) {

                    /*
                     * Bookmark or chapter
                     */
                    final int chapter =
                            readIntFromXML(kid, USERDATA_CHAPTER_ATTRIB);

                    int position =
                            readIntFromXML(kid, USERDATA_POSITION_ATTRIB);

                    if (position < 0) {
                        position = 0;
                    }

                    if (kid.getName().equals(USERDATA_BOOKMARK_TAG)) {

                        String text = kid.getText(0);

                        if (text == null) {
                            text = "Untitled";
                        }

                        bookmarks.addBookmark(
                                new Bookmark(getChapter(chapter),
                                position, text));

                    } else {
                        Chapter c = getChapter(chapter);
                        c.setCurrentPosition(position);
                        final String encoding =
                                kid.getAttributeValue(KXmlParser.NO_NAMESPACE,
                                USERDATA_ENCODING_ATTRIB);
                        c.setEncoding(encoding);
                    }
                }
            }

            currentChapter = getChapter(cchapter);

        } catch (NullPointerException e) {
            bookmarks.deleteAll();
            throw new BookException("Missing info (NP Exception)");

        } catch (IllegalArgumentException e) {
            bookmarks.deleteAll();
            throw new BookException("Malformed int data");

        } catch (RuntimeException e) {

            /*
             * document has not got a root element
             */
            bookmarks.deleteAll();
            throw new BookException("Wrong data");

        } finally {
            if (in != null)
                in.close();
        }
    }

    private int readIntFromXML(final Element kid, final String elementName) {
        int number = 0;

        try {
            number = Integer.parseInt(
                kid.getAttributeValue(
                KXmlParser.NO_NAMESPACE, elementName));
        } catch (NumberFormatException nfe) {}

        return number;
    }

    public final void saveUserData() {

        if (chapters != null && //i.e. if any chapters have been read
            userfile != null    //i.e. the file is OK for writing
            ) {


            final String encoding = "UTF-8";

            try {
                userfile.truncate(0);
                DataOutputStream dout = userfile.openDataOutputStream();
                try {
                    /*
                     * Root element
                     * <book crc="123456789" chapter="3" position="1234">
                     */
                    dout.write("<".getBytes(encoding));
                    dout.write(USERDATA_BOOK_TAG.getBytes(encoding));
                    dout.write(" ".getBytes(encoding));
                    dout.write(USERDATA_SIZE_ATTRIB.getBytes(encoding));
                    dout.write("=\"".getBytes(encoding));
                    dout.write(Integer.toString(fileSize())
                            .getBytes(encoding));
                    dout.write("\" ".getBytes(encoding));
                    dout.write(USERDATA_CHAPTER_ATTRIB.getBytes(encoding));
                    dout.write("=\"".getBytes(encoding));
                    dout.write(
                            Integer.toString(currentChapter.getNumber())
                            .getBytes(encoding));

                    if (currentLanguage != null) {
                        dout.write("\" ".getBytes(encoding));
                        dout.write(USERDATA_LANGUAGE_ATTRIB.getBytes(encoding));
                        dout.write("=\"".getBytes(encoding));
                        dout.write(currentLanguage.getBytes(encoding));
                    }

                    dout.write("\">\n".getBytes(encoding));

                    /*
                     * current chapter positions
                     * <chapter chapter="3" position="1234" />
                     */
                    for (int i = 0; i < chapters.length; i++) {
                        Chapter c = chapters[i];
                        int n = c.getNumber();
                        int pos = c.getCurrentPosition();

                        dout.write("\t<".getBytes(encoding));
                        dout.write(USERDATA_CHAPTER_TAG
                                .getBytes(encoding));
                        dout.write(" ".getBytes(encoding));
                        dout.write(USERDATA_CHAPTER_ATTRIB
                                .getBytes(encoding));
                        dout.write("=\"".getBytes(encoding));
                        dout.write(Integer.toString(n).getBytes(encoding));

                        if (c.getEncoding() != null) {
                            dout.write("\" ".getBytes(encoding));
                            dout.write(USERDATA_ENCODING_ATTRIB
                                    .getBytes(encoding));
                            dout.write("=\"".getBytes(encoding));
                            dout.write(c.getEncoding().getBytes(encoding));
                        }

                        dout.write("\" ".getBytes(encoding));
                        dout.write(USERDATA_POSITION_ATTRIB
                                .getBytes(encoding));
                        dout.write("=\"".getBytes(encoding));
                        dout.write(Integer.toString(pos)
                                .getBytes(encoding));
                        dout.write("\" />\n".getBytes(encoding));
                    }

                    /*
                     * bookmarks
                     * <bookmark chapter="3" position="1234">Text</bookmark>
                     */
                    Bookmark bookmark = bookmarks.getFirst();
                    while (bookmark != null) {
                        dout.write("\t<".getBytes(encoding));
                        dout.write(USERDATA_BOOKMARK_TAG
                                .getBytes(encoding));
                        dout.write(" ".getBytes(encoding));
                        dout.write(USERDATA_CHAPTER_ATTRIB
                                .getBytes(encoding));
                        dout.write("=\"".getBytes(encoding));
                        dout.write(Integer.toString(
                                bookmark.getChapter().getNumber()
                                ).getBytes(encoding));
                        dout.write("\" ".getBytes(encoding));
                        dout.write(USERDATA_POSITION_ATTRIB
                                .getBytes(encoding));
                        dout.write("=\"".getBytes(encoding));
                        dout.write(Integer.toString(bookmark.getPosition())
                                .getBytes(encoding));
                        dout.write("\">".getBytes(encoding));
                        dout.write(bookmark.getTextForHTML()
                                .getBytes(encoding));
                        dout.write("</".getBytes(encoding));
                        dout.write(USERDATA_BOOKMARK_TAG
                                .getBytes(encoding));
                        dout.write(">\n".getBytes(encoding));

                        bookmark = bookmark.next;
                    }

                    /*
                     * Close book tag
                     */
                    dout.write("</".getBytes(encoding));
                    dout.write(USERDATA_BOOK_TAG.getBytes(encoding));
                    dout.write(">\n".getBytes(encoding));

                } catch (IOException ioe) {
                } finally {
                    dout.close();
                }
            } catch (IOException ioe) {}
        }

    }

    public final int getChaptersCount() {
        return chapters.length;
    }

    public final Chapter getChapter(final int number) {

        if (number < 0) {
            return chapters[0];
        }

        if (number > chapters.length - 1) {
            return chapters[chapters.length - 1];
        }

        return chapters[number];
    }

    public final void fillBookInfo(Form f) {

        StringItem s;

        s = new StringItem("Title:", title);
        s.setLayout(StringItem.LAYOUT_LEFT);

        f.append(s);

        s = new StringItem("Author:", author);
        f.append(s);

        s = new StringItem("Language:", language);
        f.append(s);

        if (description != null) {
            f.append(description);
        }

        Hashtable meta = getMeta();

        if (meta != null) {
            for (Enumeration e = getMeta().keys(); e.hasMoreElements();) {
                final String infoName = (String) e.nextElement();
                final String infoValue = (String) meta.get(infoName);

                if (infoName != null && infoValue != null) {
                    s = new StringItem(infoName + ":", infoValue);
                    f.append(s);
                }
            }
        }

        s = new StringItem("Language:", language);
    }

    public abstract Hashtable getMeta();

    public final BookmarkManager getBookmarkManager() {
        return bookmarks;
    }

    public final Chapter getCurrentChapter() {
        return currentChapter;
    }

    public final void setCurrentChapter(final Chapter bc) {
        currentChapter = bc;
    }

    public final int getCurrentChapterPosition() {
        return currentChapter.getCurrentPosition();
    }

    public final void setCurrentChapterPos(final int pos) {
        if (pos < 0 || pos >= currentChapter.getTextBuffer().length) {
            throw new IllegalArgumentException("Position is wrong");
        }

        currentChapter.setCurrentPosition(pos);
    }

    public final TextParser getParser() {
        return parser;
    }

    public static Book open(String filename)
            throws IOException, BookException {

        filename = filename.toLowerCase();

        if (filename.endsWith(EPUB_EXTENSION)) {
            return new EPubBook(filename);
        }

        if (filename.endsWith(PLAIN_TEXT_EXTENSION)) {
            return new FileBook(filename, new PlainTextParser(), false);
        }

        if (filename.endsWith(HTM_EXTENSION)
                || filename.endsWith(HTML_EXTENSION)
                || filename.endsWith(XHTML_EXTENSION)) {
            return new FileBook(filename, new HTMLTextParser(), true);
         }

        throw new BookException("Unsupported file format.");
    }

    public abstract int fileSize();

    public abstract String getURL();

    public abstract ArchiveZip getArchive();

    public static final String[][] LANGUAGES = new String[][] {
        new String[] {"bg", "Bulgarian"},
        new String[] {"cs", "Czech"},
        new String[] {"cy", "Welsh"},
        new String[] {"da", "Danish"},
        new String[] {"de", "German"},
        new String[] {"el", "Greek"},
        new String[] {"en", "English"},
        new String[] {"eo", "Esperanto"},
        new String[] {"es", "Spanish"},
        new String[] {"fi", "Finnish"},
        new String[] {"fr", "French"},
        new String[] {"hr", "Croatian"},
        new String[] {"id", "Indonesian"},
        new String[] {"it", "Italian"},
        new String[] {"pl", "Polish"},
        new String[] {"pt", "Portuguese"},
        new String[] {"ro", "Romanian"},
        new String[] {"ru", "Russian"},
        new String[] {"sl", "Slovene"},
        new String[] {"sv", "Swedish"},
        new String[] {"tr", "Turkish"},
        new String[] {"uk", "Ukrainian"}
    };
}