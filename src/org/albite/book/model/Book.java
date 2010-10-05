package org.albite.book.model;

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
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.StringItem;
import org.albite.book.parser.AlbiteTextParser;
import org.albite.book.parser.PlainTextParser;
import org.albite.book.parser.TextParser;
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

public abstract class Book implements Connection {

    public static final String ALBITE_TEXT_EXTENSION = ".alt";
    public static final String PLAIN_TEXT_EXTENSION = ".txt";

    public static final String[] SUPPORTED_BOOK_EXTENSIONS = new String[] {
        Archive.FILE_EXTENSION,
        ALBITE_TEXT_EXTENSION,
        PLAIN_TEXT_EXTENSION
    };

    protected static final String   USERDATA_BOOK_TAG        = "book";
    protected static final String   USERDATA_BOOKMARK_TAG    = "bookmark";
    protected static final String   USERDATA_CHAPTER_ATTRIB  = "chapter";
    protected static final String   USERDATA_CHAPTER_TAG     = "chapter";
    protected static final String   USERDATA_POSITION_ATTRIB = "position";
    protected static final String   USERDATA_CRC_ATTRIB      = "crc";


    /*
     * Main info
     */
    protected String                title                    = "Untitled";
    protected String                author                   = "Unknown Author";
    protected short                 language = Languages.LANG_UNKNOWN;
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

    private final TextParser        parser;

    public Book(final String filename, final TextParser parser)
            throws IOException, BookException {

        this.parser = parser;

        /*
         * Setup the bookmarks
         */
        try {
            /*
             * load user data
             */
            loadUserFile(filename);
        } catch (IOException ioe) {
        } catch (BookException be) {
            close();
            throw be;
        }
    }

    public void close() throws IOException {
        if (userfile != null) {
            userfile.close();
        }
    }

    public final short getLanguage() {
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

    private void loadUserFile(final String filename)
            throws BookException, IOException {
        /*
         * form user settings filename, i.e. ... .alb -> ... .alx
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
            parser.setInput(new InputStreamReader(in));

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
                int crc = Integer.parseInt(
                        root.getAttributeValue(
                        KXmlParser.NO_NAMESPACE, USERDATA_CRC_ATTRIB));

                if (this instanceof AlbiteBook) {
                    if (crc != ((AlbiteBook) this).getBookArchive().getCRC()) {
                        throw new BookException("Wrong CRC");
                    }
                }
            } catch (NumberFormatException nfe) {
                throw new BookException("Wrong CRC");
            }

            final int cchapter = readIntFromXML(root, USERDATA_CHAPTER_ATTRIB);

            int childCount = root.getChildCount();

            for (int i = 0; i < childCount ; i++ ) {
                if (root.getType(i) != Node.ELEMENT) {
                    continue;
                }

                kid = root.getElement(i);

                if (kid.getName().equals(USERDATA_BOOKMARK_TAG)
                        || kid.getName().equals(USERDATA_CHAPTER_TAG)) {

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
                    dout.write(USERDATA_CRC_ATTRIB.getBytes(encoding));
                    dout.write("=\"".getBytes(encoding));
                    dout.write(Integer.toString(
                            (this instanceof AlbiteBook
                            ? ((AlbiteBook) this).getBookArchive().getCRC()
                            : 0))
                            .getBytes(encoding));
                    dout.write("\" ".getBytes(encoding));
                    dout.write(USERDATA_CHAPTER_ATTRIB.getBytes(encoding));
                    dout.write("=\"".getBytes(encoding));
                    dout.write(
                            Integer.toString(currentChapter.getNumber())
                            .getBytes(encoding));
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
        if (getThumbImageFile() != null) {
            Image image;

            try {
                InputStream in = getThumbImageFile().openInputStream();
                try {
                    image = Image.createImage(in);

                    ImageItem ii =
                            new ImageItem(
                            null,
                            image,
                            ImageItem.LAYOUT_CENTER,
                            null);

                    f.append(ii);
                } finally {
                    in.close();
                }
            } catch (IOException ioe) {}
        }

        StringItem s;

        s = new StringItem("Title:", title);
        s.setLayout(StringItem.LAYOUT_LEFT);

        f.append(s);

        s = new StringItem("Author:", author);
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

        s = new StringItem("Language ID:", Integer.toString(language));
    }

    public LocalDictionary getDictionary() {
        return null;
    }

    public Hashtable getMeta() {
        return null;
    }

    public ArchivedFile getThumbImageFile(){
        return null;
    }

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

    public abstract String getURL();

    public static Book open(String filename)
            throws IOException, BookException, ArchiveException {

        filename = filename.toLowerCase();

        if (filename.endsWith(Archive.FILE_EXTENSION)) {
            return new AlbiteBook(filename);
        }

        if (filename.endsWith(ALBITE_TEXT_EXTENSION)) {
            return new FileBook(filename, new AlbiteTextParser(),
                    AlbiteStreamReader.DEFAULT_ENCODING);
        }

        if (filename.endsWith(PLAIN_TEXT_EXTENSION)) {
            return new FileBook(filename, new PlainTextParser(),
                    AlbiteStreamReader.DEFAULT_ENCODING);
        }

        throw new BookException("Unsupported file format.");
    }
}